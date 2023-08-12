package ru.practicum.shareit.user.dto;

import lombok.Value;

@Value
public class UserDto {
    Long id;

    String email;

    String name;
}
