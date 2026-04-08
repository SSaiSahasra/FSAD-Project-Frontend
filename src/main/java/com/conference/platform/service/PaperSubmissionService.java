package com.conference.platform.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.conference.platform.dto.PaperSubmissionDto;
import com.conference.platform.entity.PaperSubmission;
import com.conference.platform.entity.SubmissionStatus;
import com.conference.platform.entity.User;
import com.conference.platform.exception.ApiException;
import com.conference.platform.repository.PaperSubmissionRepository;
import com.conference.platform.repository.UserRepository;

@Service
public class PaperSubmissionService {

    private final PaperSubmissionRepository submissionRepository;
    private final UserRepository userRepository;

    public PaperSubmissionService(PaperSubmissionRepository submissionRepository, UserRepository userRepository) {
        this.submissionRepository = submissionRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public PaperSubmissionDto submitPaper(PaperSubmissionDto dto, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ApiException("User not found"));

        PaperSubmission submission = PaperSubmission.builder()
                .title(dto.getTitle())
                .abstractText(dto.getAbstractText())
                .authors(dto.getAuthors())
                .filePath(dto.getFilePath())
                .status(SubmissionStatus.PENDING)
                .submittedBy(user)
                .build();

        PaperSubmission saved = submissionRepository.save(submission);
        return mapToDto(saved);
    }

    @Transactional(readOnly = true)
    public Page<PaperSubmissionDto> getAllSubmissions(Pageable pageable) {
        if (pageable.isUnpaged()) {
            return new PageImpl<>(submissionRepository.findAllWithUsers().stream().map(this::mapToDto).toList());
        }
        return submissionRepository.findAll(pageable).map(this::mapToDto);
    }

    @Transactional(readOnly = true)
    public Page<PaperSubmissionDto> getMySubmissions(String userEmail, Pageable pageable) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ApiException("User not found"));

        if (pageable.isUnpaged()) {
            return new PageImpl<>(submissionRepository.findBySubmittedByWithUsers(user).stream().map(this::mapToDto).toList());
        }

        return submissionRepository.findBySubmittedBy(user, pageable).map(this::mapToDto);
    }

    @Transactional
    public PaperSubmissionDto updateStatus(Long id, SubmissionStatus status) {
        PaperSubmission submission = submissionRepository.findById(id)
                .orElseThrow(() -> new ApiException("Submission not found"));
        submission.setStatus(status);
        return mapToDto(submissionRepository.save(submission));
    }

    @Transactional
    public PaperSubmissionDto assignReviewer(Long submissionId, Long reviewerId) {
        PaperSubmission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new ApiException("Submission not found"));
        User reviewer = userRepository.findById(reviewerId)
                .orElseThrow(() -> new ApiException("Reviewer not found"));
        
        submission.setReviewer(reviewer);
        submission.setStatus(SubmissionStatus.UNDER_REVIEW);
        return mapToDto(submissionRepository.save(submission));
    }

    @Transactional
    public PaperSubmissionDto addReview(Long id, String comments, Double marks, Double percentage, SubmissionStatus status) {
        PaperSubmission submission = submissionRepository.findById(id)
                .orElseThrow(() -> new ApiException("Submission not found"));

        submission.setReviewComments(comments);
        submission.setReviewMarks(marks);
        submission.setReviewPercentage(percentage);

        if (status != null) {
            submission.setStatus(status);
        } else {
            submission.setStatus(SubmissionStatus.UNDER_REVIEW);
        }

        return mapToDto(submissionRepository.save(submission));
    }

    private PaperSubmissionDto mapToDto(PaperSubmission entity) {
        return PaperSubmissionDto.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .abstractText(entity.getAbstractText())
                .authors(entity.getAuthors())
                .filePath(entity.getFilePath())
                .reviewComments(entity.getReviewComments())
                .reviewMarks(entity.getReviewMarks())
                .reviewPercentage(entity.getReviewPercentage())
                .status(entity.getStatus())
                .submittedBy(entity.getSubmittedBy().getName())
                .reviewer(entity.getReviewer() != null ? entity.getReviewer().getName() : null)
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
