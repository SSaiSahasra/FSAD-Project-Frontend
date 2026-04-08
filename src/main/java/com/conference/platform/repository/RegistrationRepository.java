package com.conference.platform.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.conference.platform.entity.Registration;
import com.conference.platform.entity.User;

@Repository
public interface RegistrationRepository extends JpaRepository<Registration, Long> {
    Optional<Registration> findByUser(User user);
    boolean existsByUserAndPaymentStatus(User user, String paymentStatus);

    @Query("select r from Registration r join fetch r.user")
    List<Registration> findAllWithUser();
}
