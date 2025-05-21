package com.example.todolistapp.controller;

import com.example.todolistapp.dto.*;
import com.example.todolistapp.service.UserService;
import com.example.todolistapp.util.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authManager;
    private final UserService userSvc;
    private final JwtUtil jwtUtil;

    public AuthController(
            AuthenticationManager authManager,
            UserService userSvc,
            JwtUtil jwtUtil
    ) {
        this.authManager = authManager;
        this.userSvc = userSvc;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(
            @Valid @RequestBody UserRegistrationRequest req
    ) {
        userSvc.register(req);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Usuario registrado con éxito");
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest req
    ) {
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        req.getUsername(),
                        req.getPassword()
                )
        );

        UserDetails ud = (UserDetails) auth.getPrincipal();
        String token = jwtUtil.generateToken(
                ud.getUsername(),
                ud.getAuthorities().iterator().next().getAuthority()
        );
        String role = ud.getAuthorities().iterator().next().getAuthority();

        return ResponseEntity.ok(
                new AuthResponse(token, "Bearer", ud.getUsername(), role)
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("promote/{username}")
    public ResponseEntity<String> promote(
            @PathVariable String username
    ) {
        userSvc.promoteToAdmin(username);
        return ResponseEntity.ok("Usuario " + username + " promovido a ADMIN");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("delete/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userSvc.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
