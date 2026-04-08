package com.conference.platform.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "registrations")
public class Registration extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String registrationType;
    private String paymentStatus;
    private Double amount;

    public Registration() {}

    public Registration(Long id, User user, String registrationType, String paymentStatus, Double amount) {
        this.id = id;
        this.user = user;
        this.registrationType = registrationType;
        this.paymentStatus = paymentStatus;
        this.amount = amount;
    }

    public static class RegistrationBuilder {
        private Long id;
        private User user;
        private String registrationType;
        private String paymentStatus;
        private Double amount;

        public RegistrationBuilder id(Long id) { this.id = id; return this; }
        public RegistrationBuilder user(User user) { this.user = user; return this; }
        public RegistrationBuilder registrationType(String registrationType) { this.registrationType = registrationType; return this; }
        public RegistrationBuilder paymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; return this; }
        public RegistrationBuilder amount(Double amount) { this.amount = amount; return this; }
        public Registration build() { return new Registration(id, user, registrationType, paymentStatus, amount); }
    }

    public static RegistrationBuilder builder() { return new RegistrationBuilder(); }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getRegistrationType() { return registrationType; }
    public void setRegistrationType(String registrationType) { this.registrationType = registrationType; }
    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
}
