package ru.practicum.shareit.user;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@Builder
public class User {
    @NonNull
    Long id;

    @NotBlank
    @With
    String name;

    @Email
    @With
    String email;
}
