package ru.practicum.shareit.core.exception;

public class ItemNotAvailableException extends RuntimeException {
    public ItemNotAvailableException(String message) {
        super(message);
    }
}
