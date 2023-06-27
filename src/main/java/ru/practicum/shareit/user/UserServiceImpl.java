package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.exception.DataConflictException;
import ru.practicum.shareit.error.exception.EntityNotFoundException;
import ru.practicum.shareit.error.exception.InvalidEntityException;
import ru.practicum.shareit.error.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoMapper;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.getAllUsers().stream().map(UserDtoMapper::toDto).collect(Collectors.toUnmodifiableList());
    }

    @Override
    public UserDto getUser(Long id) {
        return UserDtoMapper.toDto(userRepository.getUser(id)
            .orElseThrow(() -> new EntityNotFoundException("Пользователь с таким id не найден.")));
    }

    @Override
    public UserDto addUser(UserDto userDto) {
        if (!userDto.isValidForCreate()) {
            throw new InvalidEntityException("Неверный пользователь");
        }
        return UserDtoMapper.toDto(userRepository.addUser(UserDtoMapper.fromDto(userDto)));
    }

    @Override
    public UserDto updateUser(Long id, UserDto userDto) {
        if (!UserValidator.isValidUserDto(userDto)) {
            throw new InvalidEntityException("Неверный пользователь");
        }

        var userOpt = userRepository.getUser(id);
        if (userOpt.isEmpty()) {
            throw new EntityNotFoundException("пользователь с таким id не найден");
        }
        var user = userOpt.get();

        String email = user.getEmail();
        String name = user.getName();
        if (userDto.getEmail() != null) {
            if (!email.equals(userDto.getEmail())) {
                if (userRepository.emailExists(userDto.getEmail())) {
                    throw new DataConflictException("email уже существует");
                }
                email = userDto.getEmail();
            }
        }
        if (userDto.getName() != null) {
            if (!name.equals(userDto.getName())) {
                name = userDto.getName();
            }
        }
        user.setEmail(email);
        user.setName(name);


        //if (!user.getEmail().equals(userDto.getEmail())) {
        //    if (userRepository.emailExists(userDto.getEmail())) {
        //        throw new ValidationException("email уже существует");
        //    }
        //}

        return UserDtoMapper.toDto(userRepository.updateUser(id, user));
    }

    @Override
    public void removeUser(Long id) {
        if (userRepository.getUser(id).isEmpty()) {
            throw new EntityNotFoundException("пользователь с таким id не найден");
        }
        userRepository.removeById(id);
    }
}
