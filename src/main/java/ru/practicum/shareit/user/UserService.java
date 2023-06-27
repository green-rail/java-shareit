package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getAllUsers();
    UserDto getUser(Long id);
    UserDto addUser(UserDto userDto);
    UserDto updateUser(Long id, UserDto userDto);
    void removeUser(Long id);
}
