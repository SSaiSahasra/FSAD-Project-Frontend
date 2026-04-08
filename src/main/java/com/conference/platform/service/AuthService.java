package com.conference.platform.service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.conference.platform.dto.AuthResponse;
import com.conference.platform.dto.LoginRequest;
import com.conference.platform.dto.RegisterRequest;
import com.conference.platform.entity.Role;
import com.conference.platform.entity.User;
import com.conference.platform.exception.ApiException;
import com.conference.platform.repository.UserRepository;
import com.conference.platform.security.JwtService;

@Service
public class AuthService {

        private static final String GOOGLE_TOKEN_INFO_URL = "https://oauth2.googleapis.com/tokeninfo?id_token=%s";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

        @Value("${google.client-id:}")
        private String googleClientId;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ApiException("Email already in use");
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .affiliation(request.getAffiliation())
                .bio(request.getBio())
                .build();

        userRepository.save(user);

        org.springframework.security.core.userdetails.User userDetails = 
                (org.springframework.security.core.userdetails.User) org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .authorities(user.getRole().toString())
                .build();

        var jwtToken = jwtService.generateToken(userDetails);
        return AuthResponse.builder()
                .token(jwtToken)
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ApiException("User not found"));

        return buildAuthResponse(user);
    }

        @Transactional
        public AuthResponse authenticateWithGoogle(String idToken) {
                Map<String, Object> payload = fetchGoogleTokenInfo(idToken);

                String email = String.valueOf(payload.getOrDefault("email", "")).trim();
                String name = String.valueOf(payload.getOrDefault("name", "Google User")).trim();
                String emailVerified = String.valueOf(payload.getOrDefault("email_verified", "false"));
                String issuer = String.valueOf(payload.getOrDefault("iss", ""));
                String audience = String.valueOf(payload.getOrDefault("aud", ""));

                if (email.isEmpty()) {
                        throw new ApiException("Google token is missing email");
                }
                if (!"true".equalsIgnoreCase(emailVerified)) {
                        throw new ApiException("Google email is not verified");
                }
                if (!("accounts.google.com".equalsIgnoreCase(issuer) || "https://accounts.google.com".equalsIgnoreCase(issuer))) {
                        throw new ApiException("Invalid Google token issuer");
                }
                if (!googleClientId.isBlank() && !googleClientId.equals(audience)) {
                        throw new ApiException("Google token audience mismatch");
                }

                User user = userRepository.findByEmail(email)
                                .orElseGet(() -> userRepository.save(User.builder()
                                                .name(name.isBlank() ? "Google User" : name)
                                                .email(email)
                                                .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                                                .role(Role.ROLE_USER)
                                                .build()));

                return buildAuthResponse(user);
        }

        private Map<String, Object> fetchGoogleTokenInfo(String idToken) {
                try {
                        RestTemplate restTemplate = new RestTemplate();
                        String url = String.format(GOOGLE_TOKEN_INFO_URL, idToken);
                        ResponseEntity<Object> response = restTemplate.getForEntity(url, Object.class);
                        Object bodyObject = response.getBody();

                        if (!(bodyObject instanceof Map<?, ?> bodyRaw) || bodyRaw.isEmpty()) {
                                throw new ApiException("Unable to validate Google token");
                        }

                        Map<String, Object> body = new HashMap<>();
                        for (Map.Entry<?, ?> entry : bodyRaw.entrySet()) {
                                body.put(String.valueOf(entry.getKey()), entry.getValue());
                        }

                        return body;
                } catch (RestClientException ex) {
                        throw new ApiException("Invalid Google token");
                }
        }

        private AuthResponse buildAuthResponse(User user) {
                org.springframework.security.core.userdetails.User userDetails =
                                (org.springframework.security.core.userdetails.User) org.springframework.security.core.userdetails.User.builder()
                                                .username(user.getEmail())
                                                .password(user.getPassword())
                                                .authorities(user.getRole().toString())
                                                .build();

                var jwtToken = jwtService.generateToken(userDetails);
                return AuthResponse.builder()
                                .token(jwtToken)
                                .name(user.getName())
                                .email(user.getEmail())
                                .role(user.getRole())
                                .build();
    }
}
