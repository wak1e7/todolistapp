package com.example.todolistapp.service;

import com.example.todolistapp.dto.UserRegistrationRequest;
import com.example.todolistapp.entity.User;
import com.example.todolistapp.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    private final UserRepository userRepo;
    private final PasswordEncoder encoder;

    public UserService(UserRepository userRepo, PasswordEncoder encoder) {
        this.userRepo = userRepo;
        this.encoder = encoder;
    }

    @Transactional
    public void register(UserRegistrationRequest req) {
        if (userRepo.existsByUsername(req.getUsername()))
            throw new IllegalArgumentException("El username ya existe");
        if (userRepo.existsByEmail(req.getEmail()))
            throw new IllegalArgumentException("El email ya está registrado");

        User u = User.builder()
                .username(req.getUsername())
                .email(req.getEmail())
                .password(encoder.encode(req.getPassword()))
                .role("ROLE_USER")
                .build();
        userRepo.save(u);
    }

    @Transactional
    public void promoteToAdmin(String username) {
        User u = userRepo.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        u.setRole("ROLE_ADMIN");
        userRepo.save(u);
    }

    @Transactional
    public void deleteUser(Long userId) {
        User u = userRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        userRepo.delete(u);
    }
}