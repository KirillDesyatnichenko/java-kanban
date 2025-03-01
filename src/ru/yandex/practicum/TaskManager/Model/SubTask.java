package ru.yandex.practicum.TaskManager.Model;

public class SubTask extends Task {
    protected int epicId;

    public SubTask(String taskName, String description, int id, TaskStatus status, int epicId) {
        super(taskName, description, id, status);
        this.epicId = epicId;
    }

    public SubTask(String taskName, String description, TaskStatus status, int epicId) {
        super(taskName, description, status);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }
}
