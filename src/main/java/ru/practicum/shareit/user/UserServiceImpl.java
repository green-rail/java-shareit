package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.exception.DataConflictException;
import ru.practicum.shareit.error.exception.InvalidEntityException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoMapper;
import ru.practicum.shareit.user.exception.UserNotFoundException;
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
            .orElseThrow(() -> new UserNotFoundException(id)));
    }

    @Override
    public UserDto addUser(UserDto userDto) {
        userDto.userCreationErrorMessage().ifPresent(message -> {
            throw new InvalidEntityException(message);
        });
        return UserDtoMapper.toDto(userRepository.addUser(UserDtoMapper.fromDto(userDto)));
    }

    @Override
    public UserDto updateUser(Long id, UserDto userDto) {

        var user = userRepository.getUser(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        String email = user.getEmail();
        String name = user.getName();
        if (userDto.getEmail() != null) {
            if (!email.equals(userDto.getEmail())) {
                if (userRepository.emailExists(userDto.getEmail())) {
                    throw new DataConflictException(String.format("email %s уже существует", userDto.getEmail()));
                }
                user.setEmail(userDto.getEmail());
            }
        }
        if (userDto.getName() != null) {
            if (!name.equals(userDto.getName())) {
                user.setName(userDto.getName());
            }
        }

        return UserDtoMapper.toDto(userRepository.updateUser(id, user));
    }

    @Override
    public void removeUser(Long id) {
        if (userRepository.getUser(id).isEmpty()) {
            throw new UserNotFoundException(id);
        }
        userRepository.removeById(id);
    }
}
