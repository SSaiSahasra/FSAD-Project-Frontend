package com.conference.platform.dto;

public class RealtimeChatMessageDto {
    private String sender;
    private String content;
    private String timestamp;

    public RealtimeChatMessageDto() {}

    public RealtimeChatMessageDto(String sender, String content, String timestamp) {
        this.sender = sender;
        this.content = content;
        this.timestamp = timestamp;
    }

    public static class RealtimeChatMessageDtoBuilder {
        private String sender;
        private String content;
        private String timestamp;

        public RealtimeChatMessageDtoBuilder sender(String sender) {
            this.sender = sender;
            return this;
        }

        public RealtimeChatMessageDtoBuilder content(String content) {
            this.content = content;
            return this;
        }

        public RealtimeChatMessageDtoBuilder timestamp(String timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public RealtimeChatMessageDto build() {
            return new RealtimeChatMessageDto(sender, content, timestamp);
        }
    }

    public static RealtimeChatMessageDtoBuilder builder() {
        return new RealtimeChatMessageDtoBuilder();
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
