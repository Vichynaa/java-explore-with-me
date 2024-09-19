package ru.practicum.explore.user.dto;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class NewUserRequest {
    @Email
    private String email;
    private String name;
}
