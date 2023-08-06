package ru.practicum.shareit.user.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.user.User;

@UtilityClass
public class UserDtoMapper {

    public UserDto toDto(User user) {
        return new UserDto(user.getId(), user.getEmail(), user.getName());
    }

    public User fromDto(UserDto userDto) {

        return new User(
                userDto.getId(),
                userDto.getName(),
                userDto.getEmail());
    }
}
