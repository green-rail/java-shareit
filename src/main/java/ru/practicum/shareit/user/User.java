package ru.practicum.shareit.user;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@Builder
public class User {
    @NonNull
    Long id;

    @NotBlank
    String name;

    @Email
    @NonNull
    String email;
}
