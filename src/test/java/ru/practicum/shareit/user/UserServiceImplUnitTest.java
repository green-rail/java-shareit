package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.practicum.shareit.error.exception.DataConflictException;
import ru.practicum.shareit.error.exception.InvalidEntityException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

class UserServiceImplUnitTest {

    private static final User user = new User(
            1L,
            "User name",
            "user@email.com"
    );

    @Test
    void getAllUsers() {
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        UserService userService = new UserServiceImpl(userRepository);

        Mockito.when(userRepository.findAll()).thenReturn(Collections.emptyList());
        var result = userService.getAllUsers();
        assertThat(result, hasSize(0));

        Mockito.when(userRepository.findAll()).thenReturn(List.of(user));

        result = userService.getAllUsers();
        assertThat(result, hasSize(1));
        assertThat(result.get(0).getName(), equalTo(user.getName()));
    }

    @Test
    void getUser() {
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        UserService userService = new UserServiceImpl(userRepository);

        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.getUser(100L));

        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        UserDto response = userService.getUser(user.getId());
        assertThat(response.getId(), equalTo(user.getId()));
        assertThat(response.getName(), equalTo(user.getName()));
        assertThat(response.getEmail(), equalTo(user.getEmail()));
    }

    @Test
    void addUser() {

        UserRepository userRepository = Mockito.mock(UserRepository.class);
        UserService userService = new UserServiceImpl(userRepository);

        final UserDto dto = new UserDto(1L, null, "New user name");
        assertThrows(InvalidEntityException.class, () -> userService.addUser(dto));

        UserDto validDto = new UserDto(user.getId(), user.getEmail(), user.getName());

        Mockito.when(userRepository.save(any())).thenReturn(user);
        UserDto response = userService.addUser(validDto);
        assertThat(response.getName(), equalTo(user.getName()));
        assertThat(response.getEmail(), equalTo(user.getEmail()));
    }

    @Test
    void updateUser() {
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        UserService userService = new UserServiceImpl(userRepository);

        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.getUser(100L));

        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        UserDto userSameEmail = new UserDto(user.getId(), "updated@email.com", "updated name");
        Mockito.when(userRepository.existsByEmail(any())).thenReturn(true);

        assertThrows(DataConflictException.class, () -> userService.updateUser(user.getId(), userSameEmail));


        User updatedUser = new User(user.getId(), "updated@email.com", "updated name");
        UserDto userUpdate = new UserDto(user.getId(), updatedUser.getEmail(), updatedUser.getName());

        Mockito.when(userRepository.save(any())).thenReturn(updatedUser);
        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.of(updatedUser));

        UserDto response = userService.updateUser(user.getId(), userUpdate);
        assertThat(response.getName(), equalTo(updatedUser.getName()));
        assertThat(response.getEmail(), equalTo(updatedUser.getEmail()));
    }

    @Test
    void removeUser() {
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        UserService userService = new UserServiceImpl(userRepository);

        Mockito.when(userRepository.existsById(any())).thenReturn(false);
        assertThrows(UserNotFoundException.class, () -> userService.removeUser(user.getId()));

        Mockito.when(userRepository.existsById(any())).thenReturn(true);
        assertDoesNotThrow(() -> userService.removeUser(user.getId()));

    }
}