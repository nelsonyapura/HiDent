package com.odontologia.odontologia.service;

import com.odontologia.odontologia.model.User;
import com.odontologia.odontologia.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public void updateName(User user, String newName) {
        if (newName == null || newName.isBlank()) {
            throw new RuntimeException("El nombre no puede estar vacío");
        }
        user.setName(newName.trim());
        userRepository.save(user);
    }
}
