package com.conference.platform.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.conference.platform.dto.ApiResponse;
import com.conference.platform.dto.PaperSubmissionDto;
import com.conference.platform.entity.Registration;
import com.conference.platform.entity.Role;
import com.conference.platform.entity.ScheduleItem;
import com.conference.platform.entity.SubmissionStatus;
import com.conference.platform.entity.User;
import com.conference.platform.repository.RegistrationRepository;
import com.conference.platform.repository.ScheduleItemRepository;
import com.conference.platform.repository.UserRepository;
import com.conference.platform.service.PaperSubmissionService;
import com.conference.platform.service.RealtimeService;

@RestController
@RequestMapping("/api")
public class LegacyApiController {

    private final PaperSubmissionService submissionService;
    private final UserRepository userRepository;
    private final RegistrationRepository registrationRepository;
    private final ScheduleItemRepository scheduleItemRepository;
    private final RealtimeService realtimeService;

    public LegacyApiController(PaperSubmissionService submissionService,
                               UserRepository userRepository,
                               RegistrationRepository registrationRepository,
                               ScheduleItemRepository scheduleItemRepository,
                               RealtimeService realtimeService) {
        this.submissionService = submissionService;
        this.userRepository = userRepository;
        this.registrationRepository = registrationRepository;
        this.scheduleItemRepository = scheduleItemRepository;
        this.realtimeService = realtimeService;
    }

    @GetMapping("/papers")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getPapers(@AuthenticationPrincipal UserDetails userDetails) {
        boolean isAdminOrReviewer = userDetails.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()) || "ROLE_REVIEWER".equals(a.getAuthority()));

        var page = isAdminOrReviewer
                ? submissionService.getAllSubmissions(Pageable.unpaged())
                : submissionService.getMySubmissions(userDetails.getUsername(), Pageable.unpaged());

        List<Map<String, Object>> response = page.getContent().stream()
                .map(this::toLegacyPaper)
                .toList();

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping(value = "/papers", consumes = {"multipart/form-data"})
    public ResponseEntity<ApiResponse<Map<String, Object>>> submitPaper(
            @RequestParam("title") String title,
            @RequestParam("abstract") String abstractText,
            @RequestParam("authors") String authors,
            @RequestParam(value = "keywords", required = false) String keywords,
            @RequestParam("paper") MultipartFile paper,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String filePath = storeFile(paper);

        PaperSubmissionDto dto = PaperSubmissionDto.builder()
                .title(title)
                .abstractText(abstractText)
                .authors(authors)
                .filePath(filePath)
                .build();

        PaperSubmissionDto saved = submissionService.submitPaper(dto, userDetails.getUsername());
        Map<String, Object> response = toLegacyPaper(saved);
        response.put("keywords", keywords);

        return ResponseEntity.ok(ApiResponse.success(response, "Paper submitted successfully"));
    }

    @PutMapping("/papers/{id}/status")
    public ResponseEntity<ApiResponse<Map<String, Object>>> updateStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> payload
    ) {
        String rawStatus = payload.getOrDefault("status", "");
        SubmissionStatus status = parseStatus(rawStatus);
        PaperSubmissionDto updated = submissionService.updateStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success(toLegacyPaper(updated), "Status updated successfully"));
    }

    @PostMapping("/papers/{id}/assign-reviewer")
    public ResponseEntity<ApiResponse<Map<String, Object>>> assignReviewer(
            @PathVariable Long id,
            @RequestBody Map<String, Object> payload
    ) {
        Object reviewerValue = payload.get("reviewerId");
        if (reviewerValue == null) {
            return ResponseEntity.badRequest().body(ApiResponse.error("reviewerId is required"));
        }

        Long reviewerId = Long.valueOf(String.valueOf(reviewerValue));
        PaperSubmissionDto updated = submissionService.assignReviewer(id, reviewerId);
        return ResponseEntity.ok(ApiResponse.success(toLegacyPaper(updated), "Reviewer assigned successfully"));
    }

    @PostMapping("/papers/{id}/review")
    public ResponseEntity<ApiResponse<Map<String, Object>>> submitReview(
            @PathVariable Long id,
            @RequestBody Map<String, Object> payload
    ) {
        String comments = String.valueOf(payload.getOrDefault("comments", "")).trim();
        Double marks = toDouble(payload.get("marks"));
        Double percentage = toDouble(payload.get("percentage"));
        String rawStatus = String.valueOf(payload.getOrDefault("status", "reviewed"));
        SubmissionStatus status = parseStatus(rawStatus);

        PaperSubmissionDto updated = submissionService.addReview(id, comments, marks, percentage, status);
        return ResponseEntity.ok(ApiResponse.success(toLegacyPaper(updated), "Review submitted successfully"));
    }

    @GetMapping("/users/reviewers")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getReviewers() {
        List<User> reviewers = userRepository.findByRole(Role.ROLE_REVIEWER);
        if (reviewers.isEmpty()) {
            reviewers = userRepository.findByRole(Role.ROLE_ADMIN);
        }

        List<Map<String, Object>> response = reviewers.stream().map(user -> {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", user.getId());
            item.put("_id", user.getId());
            item.put("name", user.getName());
            item.put("email", user.getEmail());
            item.put("affiliation", user.getAffiliation());
            item.put("expertise", user.getAffiliation() == null || user.getAffiliation().isBlank() ? "General" : user.getAffiliation());
            return item;
        }).toList();

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/registrations")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getRegistrations() {
        List<Map<String, Object>> response = registrationRepository.findAllWithUser().stream().map(reg -> {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", reg.getId());
            item.put("_id", reg.getId());
            item.put("ticketType", reg.getRegistrationType());
            item.put("createdAt", reg.getCreatedAt());
            Map<String, Object> userMap = new LinkedHashMap<>();
            userMap.put("name", reg.getUser().getName());
            userMap.put("email", reg.getUser().getEmail());
            item.put("user", userMap);
            return item;
        }).toList();

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/registrations")
    public ResponseEntity<ApiResponse<Map<String, Object>>> createRegistration(
            @RequestBody Map<String, String> payload,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String ticketType = payload.getOrDefault("ticketType", "full");

        Registration reg = Registration.builder()
                .user(user)
                .registrationType(ticketType)
                .paymentStatus("PENDING")
                .amount(ticketAmount(ticketType))
                .build();

        Registration saved = registrationRepository.save(reg);
        realtimeService.notifyRegistrationMilestone();

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("id", saved.getId());
        response.put("_id", saved.getId());
        response.put("ticketType", saved.getRegistrationType());
        response.put("createdAt", saved.getCreatedAt());
        return ResponseEntity.ok(ApiResponse.success(response, "Registration completed"));
    }

    @GetMapping("/conferences")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getConferences() {
        Map<String, Object> conference = new LinkedHashMap<>();
        conference.put("id", 1);
        conference.put("_id", 1);
        conference.put("title", "Academic Conference 2026");
        conference.put("location", "Main Convention Center");
        conference.put("date", LocalDateTime.now().plusDays(30).toString());
        return ResponseEntity.ok(ApiResponse.success(List.of(conference)));
    }

    @GetMapping("/schedules/conference/{conferenceId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getScheduleByConference(@PathVariable String conferenceId) {
        List<ScheduleItem> items = scheduleItemRepository.findAllByOrderByStartTimeAsc();

        List<Map<String, Object>> sessions;
        if (items.isEmpty()) {
            sessions = defaultSessions();
        } else {
            sessions = items.stream().map(this::toLegacyScheduleSession).toList();
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("conferenceId", conferenceId);
        response.put("sessions", sessions);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/schedules")
    public ResponseEntity<ApiResponse<Map<String, Object>>> createScheduleSession(@RequestBody Map<String, Object> payload) {
        ScheduleItem item = scheduleItemFromPayload(payload, null);
        ScheduleItem saved = scheduleItemRepository.save(item);
        return ResponseEntity.ok(ApiResponse.success(toLegacyScheduleSession(saved), "Session created successfully"));
    }

    @PutMapping("/schedules/{id}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> updateScheduleSession(
            @PathVariable Long id,
            @RequestBody Map<String, Object> payload
    ) {
        ScheduleItem existing = scheduleItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Schedule session not found"));

        ScheduleItem updated = scheduleItemFromPayload(payload, existing);
        ScheduleItem saved = scheduleItemRepository.save(updated);
        return ResponseEntity.ok(ApiResponse.success(toLegacyScheduleSession(saved), "Session updated successfully"));
    }

    @DeleteMapping("/schedules/{id}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> deleteScheduleSession(@PathVariable Long id) {
        ScheduleItem existing = scheduleItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Schedule session not found"));

        scheduleItemRepository.delete(existing);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("id", existing.getId());
        response.put("_id", existing.getId());
        return ResponseEntity.ok(ApiResponse.success(response, "Session deleted successfully"));
    }

    private Map<String, Object> toLegacyPaper(PaperSubmissionDto dto) {
        Map<String, Object> paper = new LinkedHashMap<>();
        paper.put("id", dto.getId());
        paper.put("_id", dto.getId());
        paper.put("title", dto.getTitle());
        paper.put("authors", dto.getAuthors());
        paper.put("abstract", dto.getAbstractText());
        paper.put("filePath", dto.getFilePath());
        paper.put("reviewComments", dto.getReviewComments());
        paper.put("marks", dto.getReviewMarks());
        paper.put("percentage", dto.getReviewPercentage());
        paper.put("feedback", dto.getReviewComments());
        paper.put("status", toLegacyStatus(dto.getStatus()));
        paper.put("submittedBy", dto.getSubmittedBy());
        paper.put("reviewer", dto.getReviewer());
        paper.put("createdAt", dto.getCreatedAt());
        paper.put("updatedAt", dto.getUpdatedAt());
        return paper;
    }

    private String toLegacyStatus(SubmissionStatus status) {
        if (status == null) {
            return "submitted";
        }

        return switch (status) {
            case PENDING -> "submitted";
            case UNDER_REVIEW -> "reviewed";
            case ACCEPTED -> "accepted";
            case REJECTED -> "rejected";
        };
    }

    private SubmissionStatus parseStatus(String status) {
        String normalized = status == null ? "" : status.trim().toLowerCase();
        return switch (normalized) {
            case "accepted" -> SubmissionStatus.ACCEPTED;
            case "rejected" -> SubmissionStatus.REJECTED;
            case "reviewed", "under_review", "under-review" -> SubmissionStatus.UNDER_REVIEW;
            case "submitted", "pending" -> SubmissionStatus.PENDING;
            default -> throw new IllegalArgumentException("Invalid status value: " + status);
        };
    }

    private String storeFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        try {
            String uploadDir = "uploads/";
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path path = Paths.get(uploadDir + filename);
            Files.write(path, file.getBytes());
            return "/api/files/download/" + filename;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file", e);
        }
    }

    private double ticketAmount(String ticketType) {
        return switch (String.valueOf(ticketType).toLowerCase()) {
            case "virtual" -> 99.0;
            case "single" -> 149.0;
            default -> 299.0;
        };
    }

    private List<Map<String, Object>> defaultSessions() {
        List<Map<String, Object>> sessions = new ArrayList<>();

        Map<String, Object> s1 = new LinkedHashMap<>();
        s1.put("time", "09:00 AM");
        s1.put("title", "Opening Keynote");
        s1.put("speaker", "Dr. Jane Smith");
        sessions.add(s1);

        Map<String, Object> s2 = new LinkedHashMap<>();
        s2.put("time", "11:00 AM");
        s2.put("title", "AI Research Panel");
        s2.put("speaker", "Panelists");
        sessions.add(s2);

        Map<String, Object> s3 = new LinkedHashMap<>();
        s3.put("time", "02:00 PM");
        s3.put("title", "Paper Presentation Session");
        s3.put("speaker", "Selected Authors");
        sessions.add(s3);

        return sessions;
    }

    private Map<String, Object> toLegacyScheduleSession(ScheduleItem item) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a");
        Map<String, Object> session = new LinkedHashMap<>();
        session.put("id", item.getId());
        session.put("_id", item.getId());
        session.put("title", item.getTitle());
        session.put("description", item.getDescription());
        session.put("startTime", item.getStartTime() == null ? null : item.getStartTime().toString());
        session.put("endTime", item.getEndTime() == null ? null : item.getEndTime().toString());
        session.put("time", item.getStartTime() == null ? "Time TBD" : item.getStartTime().format(formatter));
        session.put("speaker", item.getDescription() == null || item.getDescription().isBlank() ? "TBD" : item.getDescription());
        session.put("room", item.getLocation() == null || item.getLocation().isBlank() ? "TBD" : item.getLocation());
        session.put("track", item.getType() == null || item.getType().isBlank() ? "General" : item.getType());
        return session;
    }

    private ScheduleItem scheduleItemFromPayload(Map<String, Object> payload, ScheduleItem existing) {
        String title = String.valueOf(payload.getOrDefault("title", "")).trim();
        String description = String.valueOf(payload.getOrDefault("speaker", payload.getOrDefault("description", ""))).trim();
        String location = String.valueOf(payload.getOrDefault("room", payload.getOrDefault("location", ""))).trim();
        String type = String.valueOf(payload.getOrDefault("track", payload.getOrDefault("type", "General"))).trim();
        String start = String.valueOf(payload.getOrDefault("startTime", "")).trim();
        String end = String.valueOf(payload.getOrDefault("endTime", "")).trim();

        if (title.isBlank()) {
            throw new IllegalArgumentException("title is required");
        }
        if (start.isBlank() || end.isBlank()) {
            throw new IllegalArgumentException("startTime and endTime are required");
        }

        LocalDateTime startTime = LocalDateTime.parse(start);
        LocalDateTime endTime = LocalDateTime.parse(end);

        ScheduleItem target = existing == null ? new ScheduleItem() : existing;
        target.setTitle(title);
        target.setDescription(description.isBlank() ? null : description);
        target.setLocation(location.isBlank() ? null : location);
        target.setType(type.isBlank() ? "General" : type);
        target.setStartTime(startTime);
        target.setEndTime(endTime);
        return target;
    }

    private Double toDouble(Object value) {
        if (value == null) {
            return null;
        }
        try {
            String asString = String.valueOf(value).trim();
            if (asString.isEmpty()) {
                return null;
            }
            return Double.valueOf(asString);
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}
