package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    List<User> getAllUsers();

    User addUser(User user);

    User updateUser(Long id, User user);
    void removeById(Long id);

    Optional<User> getUser(Long id);

    boolean emailExists(String email);
    boolean indexExists(Long id);
}
