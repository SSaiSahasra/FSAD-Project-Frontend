package com.conference.platform.dto;

public class RealtimeNotificationDto {
    private String type;
    private String title;
    private String message;
    private String timestamp;

    public RealtimeNotificationDto() {}

    public RealtimeNotificationDto(String type, String title, String message, String timestamp) {
        this.type = type;
        this.title = title;
        this.message = message;
        this.timestamp = timestamp;
    }

    public static class RealtimeNotificationDtoBuilder {
        private String type;
        private String title;
        private String message;
        private String timestamp;

        public RealtimeNotificationDtoBuilder type(String type) {
            this.type = type;
            return this;
        }

        public RealtimeNotificationDtoBuilder title(String title) {
            this.title = title;
            return this;
        }

        public RealtimeNotificationDtoBuilder message(String message) {
            this.message = message;
            return this;
        }

        public RealtimeNotificationDtoBuilder timestamp(String timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public RealtimeNotificationDto build() {
            return new RealtimeNotificationDto(type, title, message, timestamp);
        }
    }

    public static RealtimeNotificationDtoBuilder builder() {
        return new RealtimeNotificationDtoBuilder();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
