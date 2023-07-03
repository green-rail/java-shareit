package ru.practicum.shareit.user.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }

    public UserNotFoundException(Long id) {
        super(String.format("пользователь с id [%d] не найден", id));
    }
}
