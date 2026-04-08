package com.conference.platform.dto;

import java.time.LocalDateTime;

import com.conference.platform.entity.SubmissionStatus;

public class PaperSubmissionDto {
    private Long id;
    private String title;
    private String abstractText;
    private String authors;
    private String filePath;
    private String reviewComments;
    private Double reviewMarks;
    private Double reviewPercentage;
    private SubmissionStatus status;
    private String submittedBy;
    private String reviewer;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public PaperSubmissionDto() {}

    public PaperSubmissionDto(Long id, String title, String abstractText, String authors, String filePath,
                              String reviewComments, Double reviewMarks, Double reviewPercentage,
                              SubmissionStatus status, String submittedBy, String reviewer,
                              LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.title = title;
        this.abstractText = abstractText;
        this.authors = authors;
        this.filePath = filePath;
        this.reviewComments = reviewComments;
        this.reviewMarks = reviewMarks;
        this.reviewPercentage = reviewPercentage;
        this.status = status;
        this.submittedBy = submittedBy;
        this.reviewer = reviewer;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static class PaperSubmissionDtoBuilder {
        private Long id;
        private String title;
        private String abstractText;
        private String authors;
        private String filePath;
        private String reviewComments;
        private Double reviewMarks;
        private Double reviewPercentage;
        private SubmissionStatus status;
        private String submittedBy;
        private String reviewer;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public PaperSubmissionDtoBuilder id(Long id) { this.id = id; return this; }
        public PaperSubmissionDtoBuilder title(String title) { this.title = title; return this; }
        public PaperSubmissionDtoBuilder abstractText(String abstractText) { this.abstractText = abstractText; return this; }
        public PaperSubmissionDtoBuilder authors(String authors) { this.authors = authors; return this; }
        public PaperSubmissionDtoBuilder filePath(String filePath) { this.filePath = filePath; return this; }
        public PaperSubmissionDtoBuilder reviewComments(String reviewComments) { this.reviewComments = reviewComments; return this; }
        public PaperSubmissionDtoBuilder reviewMarks(Double reviewMarks) { this.reviewMarks = reviewMarks; return this; }
        public PaperSubmissionDtoBuilder reviewPercentage(Double reviewPercentage) { this.reviewPercentage = reviewPercentage; return this; }
        public PaperSubmissionDtoBuilder status(SubmissionStatus status) { this.status = status; return this; }
        public PaperSubmissionDtoBuilder submittedBy(String submittedBy) { this.submittedBy = submittedBy; return this; }
        public PaperSubmissionDtoBuilder reviewer(String reviewer) { this.reviewer = reviewer; return this; }
        public PaperSubmissionDtoBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public PaperSubmissionDtoBuilder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }
        public PaperSubmissionDto build() {
            PaperSubmissionDto dto = new PaperSubmissionDto();
            dto.setId(id);
            dto.setTitle(title);
            dto.setAbstractText(abstractText);
            dto.setAuthors(authors);
            dto.setFilePath(filePath);
            dto.setReviewComments(reviewComments);
            dto.setReviewMarks(reviewMarks);
            dto.setReviewPercentage(reviewPercentage);
            dto.setStatus(status);
            dto.setSubmittedBy(submittedBy);
            dto.setReviewer(reviewer);
            dto.setCreatedAt(createdAt);
            dto.setUpdatedAt(updatedAt);
            return dto;
        }
    }

    public static PaperSubmissionDtoBuilder builder() { return new PaperSubmissionDtoBuilder(); }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getAbstractText() { return abstractText; }
    public void setAbstractText(String abstractText) { this.abstractText = abstractText; }
    public String getAuthors() { return authors; }
    public void setAuthors(String authors) { this.authors = authors; }
    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
    public String getReviewComments() { return reviewComments; }
    public void setReviewComments(String reviewComments) { this.reviewComments = reviewComments; }
    public Double getReviewMarks() { return reviewMarks; }
    public void setReviewMarks(Double reviewMarks) { this.reviewMarks = reviewMarks; }
    public Double getReviewPercentage() { return reviewPercentage; }
    public void setReviewPercentage(Double reviewPercentage) { this.reviewPercentage = reviewPercentage; }
    public SubmissionStatus getStatus() { return status; }
    public void setStatus(SubmissionStatus status) { this.status = status; }
    public String getSubmittedBy() { return submittedBy; }
    public void setSubmittedBy(String submittedBy) { this.submittedBy = submittedBy; }
    public String getReviewer() { return reviewer; }
    public void setReviewer(String reviewer) { this.reviewer = reviewer; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
