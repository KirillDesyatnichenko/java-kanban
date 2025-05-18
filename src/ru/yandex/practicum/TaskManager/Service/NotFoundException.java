package ru.yandex.practicum.TaskManager.Service;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
