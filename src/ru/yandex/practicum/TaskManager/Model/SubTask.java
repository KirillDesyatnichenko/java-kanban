package ru.yandex.practicum.TaskManager.Model;

import java.time.Duration;
import java.time.LocalDateTime;

public class SubTask extends Task {
    protected int epicId;

    public SubTask(String taskName, String description, int id, TaskStatus status, Duration duration, LocalDateTime startTime, int epicId) {
        super(taskName, description, id, status, duration, startTime);
        this.epicId = epicId;
    }

    public SubTask(String taskName, String description, TaskStatus status, Duration duration, LocalDateTime startTime, int epicId) {
        super(taskName, description, status, duration, startTime);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }
}
