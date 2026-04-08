package com.conference.platform.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "schedule_items")
public class ScheduleItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    private String location;

    private String type;

    public ScheduleItem() {}

    public ScheduleItem(Long id, String title, String description, LocalDateTime startTime, LocalDateTime endTime, String location, String type) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
        this.location = location;
        this.type = type;
    }

    public static class ScheduleItemBuilder {
        private Long id;
        private String title;
        private String description;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private String location;
        private String type;

        public ScheduleItemBuilder id(Long id) { this.id = id; return this; }
        public ScheduleItemBuilder title(String title) { this.title = title; return this; }
        public ScheduleItemBuilder description(String description) { this.description = description; return this; }
        public ScheduleItemBuilder startTime(LocalDateTime startTime) { this.startTime = startTime; return this; }
        public ScheduleItemBuilder endTime(LocalDateTime endTime) { this.endTime = endTime; return this; }
        public ScheduleItemBuilder location(String location) { this.location = location; return this; }
        public ScheduleItemBuilder type(String type) { this.type = type; return this; }
        public ScheduleItem build() { return new ScheduleItem(id, title, description, startTime, endTime, location, type); }
    }

    public static ScheduleItemBuilder builder() { return new ScheduleItemBuilder(); }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}
