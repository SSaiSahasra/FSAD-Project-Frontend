package com.conference.platform.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "reviews")
public class Review extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paper_id", nullable = false)
    private PaperSubmission paper;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewer_id", nullable = false)
    private User reviewer;

    @Column(length = 2000)
    private String comments;

    private Integer rating;

    public Review() {}

    public Review(Long id, PaperSubmission paper, User reviewer, String comments, Integer rating) {
        this.id = id;
        this.paper = paper;
        this.reviewer = reviewer;
        this.comments = comments;
        this.rating = rating;
    }

    public static class ReviewBuilder {
        private Long id;
        private PaperSubmission paper;
        private User reviewer;
        private String comments;
        private Integer rating;

        public ReviewBuilder id(Long id) { this.id = id; return this; }
        public ReviewBuilder paper(PaperSubmission paper) { this.paper = paper; return this; }
        public ReviewBuilder reviewer(User reviewer) { this.reviewer = reviewer; return this; }
        public ReviewBuilder comments(String comments) { this.comments = comments; return this; }
        public ReviewBuilder rating(Integer rating) { this.rating = rating; return this; }
        public Review build() { return new Review(id, paper, reviewer, comments, rating); }
    }

    public static ReviewBuilder builder() { return new ReviewBuilder(); }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public PaperSubmission getPaper() { return paper; }
    public void setPaper(PaperSubmission paper) { this.paper = paper; }
    public User getReviewer() { return reviewer; }
    public void setReviewer(User reviewer) { this.reviewer = reviewer; }
    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }
    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }
}
