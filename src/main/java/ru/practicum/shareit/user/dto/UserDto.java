package ru.practicum.shareit.user.dto;

import lombok.Value;

import javax.validation.constraints.Email;
import java.util.Optional;

@Value
public class UserDto {
    Long id;

    @Email(message = "Некорректный email")
    String email;

    String name;

    public Optional<String> userCreationErrorMessage() {
        return Optional.ofNullable(email == null ? "некорректный пользователь: отсутствует email" : null);
    }
}
