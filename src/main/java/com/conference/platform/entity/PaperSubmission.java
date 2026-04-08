package com.conference.platform.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "paper_submissions")
public class PaperSubmission extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 2000)
    private String abstractText;

    private String authors;

    private String filePath;

    @Column(length = 2000)
    private String reviewComments;

    private Double reviewMarks;

    private Double reviewPercentage;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubmissionStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User submittedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewer_id")
    private User reviewer;

    @OneToMany(mappedBy = "paper", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

    public PaperSubmission() {}

    public PaperSubmission(Long id, String title, String abstractText, String authors, String filePath, 
                           SubmissionStatus status, User submittedBy, User reviewer) {
        this.id = id;
        this.title = title;
        this.abstractText = abstractText;
        this.authors = authors;
        this.filePath = filePath;
        this.status = status;
        this.submittedBy = submittedBy;
        this.reviewer = reviewer;
    }

    public static class PaperSubmissionBuilder {
        private Long id;
        private String title;
        private String abstractText;
        private String authors;
        private String filePath;
        private SubmissionStatus status;
        private User submittedBy;
        private User reviewer;

        public PaperSubmissionBuilder id(Long id) { this.id = id; return this; }
        public PaperSubmissionBuilder title(String title) { this.title = title; return this; }
        public PaperSubmissionBuilder abstractText(String abstractText) { this.abstractText = abstractText; return this; }
        public PaperSubmissionBuilder authors(String authors) { this.authors = authors; return this; }
        public PaperSubmissionBuilder filePath(String filePath) { this.filePath = filePath; return this; }
        public PaperSubmissionBuilder status(SubmissionStatus status) { this.status = status; return this; }
        public PaperSubmissionBuilder submittedBy(User submittedBy) { this.submittedBy = submittedBy; return this; }
        public PaperSubmissionBuilder reviewer(User reviewer) { this.reviewer = reviewer; return this; }
        public PaperSubmission build() { return new PaperSubmission(id, title, abstractText, authors, filePath, status, submittedBy, reviewer); }
    }

    public static PaperSubmissionBuilder builder() { return new PaperSubmissionBuilder(); }

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
    public User getSubmittedBy() { return submittedBy; }
    public void setSubmittedBy(User submittedBy) { this.submittedBy = submittedBy; }
    public User getReviewer() { return reviewer; }
    public void setReviewer(User reviewer) { this.reviewer = reviewer; }
    public List<Review> getReviews() { return reviews; }
    public void setReviews(List<Review> reviews) { this.reviews = reviews; }
}
