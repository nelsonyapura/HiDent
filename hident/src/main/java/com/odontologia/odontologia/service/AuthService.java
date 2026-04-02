package com.odontologia.odontologia.service;

import com.odontologia.odontologia.dto.LoginRequest;
import com.odontologia.odontologia.model.User;
import com.odontologia.odontologia.repository.UserRepository;
import com.odontologia.odontologia.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public String login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!user.isEnabled()) {
            throw new RuntimeException("La cuenta está desactivada");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Contraseña incorrecta");
        }

        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        return jwtService.generateToken(user);
    }
}
