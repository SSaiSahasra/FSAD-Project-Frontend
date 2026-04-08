package com.conference.platform.repository;

import com.conference.platform.entity.Review;
import com.conference.platform.entity.PaperSubmission;
import com.conference.platform.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByPaper(PaperSubmission paper);
    List<Review> findByReviewer(User reviewer);
}
