package com.conference.platform.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.conference.platform.entity.PaperSubmission;
import com.conference.platform.entity.SubmissionStatus;
import com.conference.platform.entity.User;

@Repository
public interface PaperSubmissionRepository extends JpaRepository<PaperSubmission, Long> {
    @Override
    @EntityGraph(attributePaths = {"submittedBy", "reviewer"})
    Page<PaperSubmission> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"submittedBy", "reviewer"})
    Page<PaperSubmission> findBySubmittedBy(User user, Pageable pageable);

    @EntityGraph(attributePaths = {"submittedBy", "reviewer"})
    Page<PaperSubmission> findByReviewer(User user, Pageable pageable);

    @EntityGraph(attributePaths = {"submittedBy", "reviewer"})
    Page<PaperSubmission> findByStatus(SubmissionStatus status, Pageable pageable);

    @EntityGraph(attributePaths = {"submittedBy", "reviewer"})
    Page<PaperSubmission> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    @Query("select ps from PaperSubmission ps left join fetch ps.submittedBy left join fetch ps.reviewer")
    List<PaperSubmission> findAllWithUsers();

    @Query("select ps from PaperSubmission ps left join fetch ps.submittedBy left join fetch ps.reviewer where ps.submittedBy = :user")
    List<PaperSubmission> findBySubmittedByWithUsers(@Param("user") User user);
}
