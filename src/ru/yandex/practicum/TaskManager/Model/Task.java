package ru.yandex.practicum.TaskManager.Model;

import java.util.Objects;

public class Task {
    protected String taskName;
    protected String description;
    protected int id;
    protected TaskStatus status;

    public Task (String taskName, String description, int id, TaskStatus status) {
        this.taskName = taskName;
        this.description = description;
        this.id = id;
        this.status = status;
    }

    public Task (String taskName, String description, TaskStatus status) {
        this.taskName = taskName;
        this.description = description;
        this.status = status;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public int getTaskId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Задача{" +
                "Название задачи:'" + getTaskName() + '\'' +
                ", Описание задачи:'" + getDescription() + '\'' +
                ", id: " + getTaskId() + '\'' +
                ", Статус задачи: " + getStatus() + '\'' +
                '}';
    }
}