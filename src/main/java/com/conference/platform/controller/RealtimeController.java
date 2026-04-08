package com.conference.platform.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.conference.platform.dto.ApiResponse;
import com.conference.platform.dto.LiveQaMessageDto;
import com.conference.platform.dto.LiveStatsDto;
import com.conference.platform.dto.RealtimeChatMessageDto;
import com.conference.platform.service.RealtimeService;

@RestController
@RequestMapping("/api/realtime")
public class RealtimeController {

    private final RealtimeService realtimeService;

    public RealtimeController(RealtimeService realtimeService) {
        this.realtimeService = realtimeService;
    }

    @GetMapping("/stats")
    public ApiResponse<LiveStatsDto> getLiveStats() {
        return ApiResponse.success(realtimeService.getCurrentStats());
    }

    @MessageMapping("/chat.send")
    @SendTo("/topic/chat")
    public RealtimeChatMessageDto sendChatMessage(RealtimeChatMessageDto message) {
        return realtimeService.normalizeChat(message);
    }

    @MessageMapping("/qa.ask")
    @SendTo("/topic/qna")
    public LiveQaMessageDto askQuestion(LiveQaMessageDto message) {
        return realtimeService.normalizeQa(message);
    }
}
