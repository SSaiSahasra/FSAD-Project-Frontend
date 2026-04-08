package com.conference.platform.dto;

public class LiveStatsDto {
    private long registeredUsers;
    private long papersSubmitted;
    private long activeSessions;
    private String lastUpdated;

    public LiveStatsDto() {}

    public LiveStatsDto(long registeredUsers, long papersSubmitted, long activeSessions, String lastUpdated) {
        this.registeredUsers = registeredUsers;
        this.papersSubmitted = papersSubmitted;
        this.activeSessions = activeSessions;
        this.lastUpdated = lastUpdated;
    }

    public static class LiveStatsDtoBuilder {
        private long registeredUsers;
        private long papersSubmitted;
        private long activeSessions;
        private String lastUpdated;

        public LiveStatsDtoBuilder registeredUsers(long registeredUsers) {
            this.registeredUsers = registeredUsers;
            return this;
        }

        public LiveStatsDtoBuilder papersSubmitted(long papersSubmitted) {
            this.papersSubmitted = papersSubmitted;
            return this;
        }

        public LiveStatsDtoBuilder activeSessions(long activeSessions) {
            this.activeSessions = activeSessions;
            return this;
        }

        public LiveStatsDtoBuilder lastUpdated(String lastUpdated) {
            this.lastUpdated = lastUpdated;
            return this;
        }

        public LiveStatsDto build() {
            return new LiveStatsDto(registeredUsers, papersSubmitted, activeSessions, lastUpdated);
        }
    }

    public static LiveStatsDtoBuilder builder() {
        return new LiveStatsDtoBuilder();
    }

    public long getRegisteredUsers() {
        return registeredUsers;
    }

    public void setRegisteredUsers(long registeredUsers) {
        this.registeredUsers = registeredUsers;
    }

    public long getPapersSubmitted() {
        return papersSubmitted;
    }

    public void setPapersSubmitted(long papersSubmitted) {
        this.papersSubmitted = papersSubmitted;
    }

    public long getActiveSessions() {
        return activeSessions;
    }

    public void setActiveSessions(long activeSessions) {
        this.activeSessions = activeSessions;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
