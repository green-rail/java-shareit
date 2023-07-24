package ru.practicum.shareit.item.exception;

public class ItemUnavailableException extends RuntimeException {
    public ItemUnavailableException(String message) {
        super(message);
    }

    public ItemUnavailableException(Long itemId) {
        super(String.format("предмет [%d] недоступен", itemId));
    }
}
