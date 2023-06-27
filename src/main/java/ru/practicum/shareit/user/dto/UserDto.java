package ru.practicum.shareit.user.dto;

import lombok.Value;

import javax.validation.constraints.Email;
@Value
public class UserDto {
    Long id;

    @Email(message = "Некорректный email")
    String email;

    String name;

    public boolean isValidForCreate() {
        return email != null;
    }
}
