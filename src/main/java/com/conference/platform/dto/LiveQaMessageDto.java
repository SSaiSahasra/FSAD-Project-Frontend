package com.conference.platform.dto;

public class LiveQaMessageDto {
    private String asker;
    private String question;
    private String timestamp;

    public LiveQaMessageDto() {}

    public LiveQaMessageDto(String asker, String question, String timestamp) {
        this.asker = asker;
        this.question = question;
        this.timestamp = timestamp;
    }

    public static class LiveQaMessageDtoBuilder {
        private String asker;
        private String question;
        private String timestamp;

        public LiveQaMessageDtoBuilder asker(String asker) {
            this.asker = asker;
            return this;
        }

        public LiveQaMessageDtoBuilder question(String question) {
            this.question = question;
            return this;
        }

        public LiveQaMessageDtoBuilder timestamp(String timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public LiveQaMessageDto build() {
            return new LiveQaMessageDto(asker, question, timestamp);
        }
    }

    public static LiveQaMessageDtoBuilder builder() {
        return new LiveQaMessageDtoBuilder();
    }

    public String getAsker() {
        return asker;
    }

    public void setAsker(String asker) {
        this.asker = asker;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
