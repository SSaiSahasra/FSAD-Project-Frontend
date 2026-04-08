package com.conference.platform.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.conference.platform.entity.Role;
import com.conference.platform.entity.User;
import com.conference.platform.repository.UserRepository;

@Configuration
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);
    private static final String EXTRA_ADMIN_EMAIL = "ssahasra0507@gmail.com";
    private static final String EXTRA_ADMIN_PASSWORD = "sahasra";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            log.info("Seeding initial users...");

            // Create Admin
            User admin = User.builder()
                    .name("Admin User")
                    .email("admin@conference.com")
                    .password(passwordEncoder.encode("admin123"))
                    .role(Role.ROLE_ADMIN)
                    .affiliation("Conference Org")
                    .build();
            userRepository.save(admin);

            // Create standard User
            User user = User.builder()
                    .name("Regular User")
                    .email("user@example.com")
                    .password(passwordEncoder.encode("user123"))
                    .role(Role.ROLE_USER)
                    .affiliation("Example University")
                    .build();
            userRepository.save(user);

            log.info("Seeding complete.");
        }

        ensureExtraAdmin();
    }

    private void ensureExtraAdmin() {
        userRepository.findByEmail(EXTRA_ADMIN_EMAIL)
                .ifPresentOrElse(existingUser -> {
                    boolean changed = false;

                    if (existingUser.getRole() != Role.ROLE_ADMIN) {
                        existingUser.setRole(Role.ROLE_ADMIN);
                        changed = true;
                        log.info("Promoted {} to admin role.", EXTRA_ADMIN_EMAIL);
                    }

                    existingUser.setPassword(passwordEncoder.encode(EXTRA_ADMIN_PASSWORD));
                    changed = true;

                    if (changed) {
                        userRepository.save(existingUser);
                        log.info("Ensured admin password for {}.", EXTRA_ADMIN_EMAIL);
                    }
                }, () -> {
                    User extraAdmin = User.builder()
                            .name("Secondary Admin")
                            .email(EXTRA_ADMIN_EMAIL)
                            .password(passwordEncoder.encode(EXTRA_ADMIN_PASSWORD))
                            .role(Role.ROLE_ADMIN)
                            .affiliation("Conference Org")
                            .build();
                    userRepository.save(extraAdmin);
                    log.info("Created additional admin account for {}.", EXTRA_ADMIN_EMAIL);
                });
    }
}
