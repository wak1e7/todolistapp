package com.example.todolistapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TodoRequest {
    @NotBlank
    @Size(max = 100)
    private String title;

    @Size(max = 1000)
    private String description;

    private Boolean completed;
}
