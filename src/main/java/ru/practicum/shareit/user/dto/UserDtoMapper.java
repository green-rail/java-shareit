package ru.practicum.shareit.user.dto;

import ru.practicum.shareit.user.User;

public class UserDtoMapper {

    private UserDtoMapper(){}

    public static UserDto toDto(User user) {
        return new UserDto(user.getId(), user.getEmail(), user.getName());
    }

    public static User fromDto(UserDto userDto) {
        return User.builder()
            .id(userDto.getId() == null ? -1L : userDto.getId())
            .email(userDto.getEmail())
            .name(userDto.getName())
            .build();
    }
}
