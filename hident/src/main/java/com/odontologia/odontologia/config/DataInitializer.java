package com.odontologia.odontologia.config;

import com.odontologia.odontologia.model.User;
import com.odontologia.odontologia.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    @Bean
    CommandLineRunner initAdminUser(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.findByUsername("admin").isEmpty()) {
                User admin = new User();
                admin.setName("Administrador");
                admin.setDni("00000000");
                admin.setPhone("000000000");
                admin.setEmail("admin@hident.com");
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setRole("ADMIN");
                admin.setStatus(true);

                userRepository.save(admin);

                log.info("Default admin user created (username: admin, password: admin123)");
            } else {
                log.info("Admin user already exists, skipping creation");
            }
        };
    }
}
