package com.example.todolistapp.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank
    private String username;  // podrías permitir email si lo prefieres

    @NotBlank
    private String password;
}
