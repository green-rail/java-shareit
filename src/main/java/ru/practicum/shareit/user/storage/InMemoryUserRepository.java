package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.error.exception.DataConflictException;
import ru.practicum.shareit.user.User;

import java.util.*;

@Repository
public class InMemoryUserRepository implements UserRepository {

    private final Map<Long, User> users = new HashMap<>();
    private long nextUserIndex = 1;

    @Override
    public List<User> getAllUsers() {
        return List.copyOf(users.values());
    }

    @Override
    public User addUser(User user) {

        Optional<User> matchedUser = users.values().stream()
            .filter(u -> u.getEmail().equals(user.getEmail()))
            .findFirst();

        if (matchedUser.isPresent()) {
            if (matchedUser.get().getEmail().equals(user.getEmail())) {
                throw new DataConflictException("email уже существует");
            }
        }

        user.setId(nextUserIndex);
        users.put(nextUserIndex, user);
        nextUserIndex++;
        return user;
    }

    @Override
    public User updateUser(Long id, User user) {
        user.setId(id);
        users.put(id, user);
        return user;
    }

    @Override
    public void removeById(Long id) {
        users.remove(id);
    }

    @Override
    public Optional<User> getUser(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public boolean emailExists(String email) {
        return users.values().stream().anyMatch(u -> u.getEmail().equals(email));
    }

    @Override
    public boolean indexExists(Long id) {
        return users.containsKey(id);
    }
}
