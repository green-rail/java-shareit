package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    @Email(message = "Некорректный email")
    private String email;

    //@Pattern(regexp = "[a-zA-Z]+", message = "Имя не может быть пустым")
    @Pattern(regexp = "[a-zA-Z]+", message = "NAME")
    private String name;
}
