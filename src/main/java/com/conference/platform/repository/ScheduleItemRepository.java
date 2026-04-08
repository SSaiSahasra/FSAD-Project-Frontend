package com.conference.platform.repository;

import com.conference.platform.entity.ScheduleItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ScheduleItemRepository extends JpaRepository<ScheduleItem, Long> {
    List<ScheduleItem> findAllByOrderByStartTimeAsc();
    List<ScheduleItem> findByStartTimeBetween(LocalDateTime start, LocalDateTime end);
}
