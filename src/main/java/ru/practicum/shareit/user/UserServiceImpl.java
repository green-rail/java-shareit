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
        return userRepository.findAll().stream().map(UserDtoMapper::toDto).collect(Collectors.toUnmodifiableList());
    }

    @Override
    public UserDto getUser(Long id) {
        return UserDtoMapper.toDto(userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException(id)));
    }

    @Override
    public UserDto addUser(UserDto userDto) {
        userDto.userCreationErrorMessage().ifPresent(message -> {
            throw new InvalidEntityException(message);
        });
        return UserDtoMapper.toDto(userRepository.save(UserDtoMapper.fromDto(userDto)));
    }

    @Override
    public UserDto updateUser(Long id, UserDto userDto) {

        var user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        if (userDto.getEmail() != null && !user.getEmail().equals(userDto.getEmail())) {
            if (userRepository.existsByEmail(userDto.getEmail())) {
                throw new DataConflictException(String.format("email %s уже существует", userDto.getEmail()));
            }
            user.setEmail(userDto.getEmail());
        }
        if (userDto.getName() != null && !user.getName().equals(userDto.getName())) {
            user.setName(userDto.getName());
        }

        return UserDtoMapper.toDto(userRepository.save(user));
    }

    @Override
    public void removeUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException(id);
        }
        userRepository.deleteById(id);
    }
}
