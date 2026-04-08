package com.conference.platform.service;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.conference.platform.dto.LiveQaMessageDto;
import com.conference.platform.dto.LiveStatsDto;
import com.conference.platform.dto.RealtimeChatMessageDto;
import com.conference.platform.dto.RealtimeNotificationDto;
import com.conference.platform.repository.PaperSubmissionRepository;
import com.conference.platform.repository.RegistrationRepository;
import com.conference.platform.repository.ScheduleItemRepository;
import com.conference.platform.repository.UserRepository;

@Service
public class RealtimeService {

    private static final List<String> DEFAULT_NOTIFICATIONS = List.of(
            "A keynote starts in 10 minutes",
            "New paper submitted to AI track",
            "Q&A is now open for Session A",
            "Reviewer assignments were updated"
    );

    private final SimpMessagingTemplate messagingTemplate;
    private final UserRepository userRepository;
    private final PaperSubmissionRepository paperSubmissionRepository;
    private final ScheduleItemRepository scheduleItemRepository;
    private final RegistrationRepository registrationRepository;

    public RealtimeService(SimpMessagingTemplate messagingTemplate,
                           UserRepository userRepository,
                           PaperSubmissionRepository paperSubmissionRepository,
                           ScheduleItemRepository scheduleItemRepository,
                           RegistrationRepository registrationRepository) {
        this.messagingTemplate = messagingTemplate;
        this.userRepository = userRepository;
        this.paperSubmissionRepository = paperSubmissionRepository;
        this.scheduleItemRepository = scheduleItemRepository;
        this.registrationRepository = registrationRepository;
    }

    public LiveStatsDto getCurrentStats() {
        long users = userRepository.count();
        long submissions = paperSubmissionRepository.count();
        long sessions = scheduleItemRepository.count();

        // Keep the dashboard alive even when schedule rows are not seeded yet.
        if (sessions == 0) {
            sessions = 6;
        }

        return LiveStatsDto.builder()
                .registeredUsers(users)
                .papersSubmitted(submissions)
                .activeSessions(sessions)
                .lastUpdated(Instant.now().toString())
                .build();
    }

    public RealtimeChatMessageDto normalizeChat(RealtimeChatMessageDto incoming) {
        String sender = incoming.getSender() == null || incoming.getSender().isBlank() ? "Guest" : incoming.getSender().trim();
        String content = incoming.getContent() == null ? "" : incoming.getContent().trim();

        return RealtimeChatMessageDto.builder()
                .sender(sender)
                .content(content)
                .timestamp(Instant.now().toString())
                .build();
    }

    public LiveQaMessageDto normalizeQa(LiveQaMessageDto incoming) {
        String asker = incoming.getAsker() == null || incoming.getAsker().isBlank() ? "Attendee" : incoming.getAsker().trim();
        String question = incoming.getQuestion() == null ? "" : incoming.getQuestion().trim();

        return LiveQaMessageDto.builder()
                .asker(asker)
                .question(question)
                .timestamp(Instant.now().toString())
                .build();
    }

    @Scheduled(fixedDelay = 5000)
    public void broadcastStats() {
        messagingTemplate.convertAndSend("/topic/stats", getCurrentStats());
    }

    @Scheduled(fixedDelay = 11000)
    public void broadcastNotificationPulse() {
        int randomIndex = ThreadLocalRandom.current().nextInt(DEFAULT_NOTIFICATIONS.size());
        String message = DEFAULT_NOTIFICATIONS.get(randomIndex);

        RealtimeNotificationDto notification = RealtimeNotificationDto.builder()
                .type("info")
                .title("Conference Live")
                .message(message)
                .timestamp(Instant.now().toString())
                .build();

        messagingTemplate.convertAndSend("/topic/notifications", notification);
    }

    public void notifyRegistrationMilestone() {
        long registrations = registrationRepository.count();
        if (registrations > 0 && registrations % 10 == 0) {
            RealtimeNotificationDto notification = RealtimeNotificationDto.builder()
                    .type("success")
                    .title("Milestone Reached")
                    .message("" + registrations + " attendees have registered")
                    .timestamp(Instant.now().toString())
                    .build();

            messagingTemplate.convertAndSend("/topic/notifications", notification);
        }
    }
}
