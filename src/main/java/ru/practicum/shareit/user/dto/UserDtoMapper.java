package ru.practicum.shareit.user.dto;

import ru.practicum.shareit.user.User;

public class UserDtoMapper {


    public static UserDto toDto(User user) {
        return new UserDto(user.getId(), user.getEmail(), user.getName());
    }

    public static User fromDto(UserDto userDto) {

        return new User(
                userDto.getId() == null ? -1L : userDto.getId(),
                userDto.getName(),
                userDto.getEmail());
    }
}
