package ru.practicum.shareit.item.exception;

public class OwnerMismatchException extends RuntimeException {
    public OwnerMismatchException(String message) {
        super(message);
    }
}
