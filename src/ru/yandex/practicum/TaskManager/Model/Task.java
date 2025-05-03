package ru.yandex.practicum.TaskManager.Model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Task {
    protected String taskName;
    protected String description;
    protected int id;
    protected TaskStatus status;
    protected Duration duration = Duration.ZERO;
    protected LocalDateTime startTime;


    public Task(String taskName, String description, int id, TaskStatus status, Duration duration, LocalDateTime startTime) {
        this.taskName = taskName;
        this.description = description;
        this.id = id;
        this.status = status;
        this.duration = duration;
        this.startTime = startTime;
    }

    public Task(String taskName, String description, TaskStatus status, Duration duration, LocalDateTime startTime) {
        this.taskName = taskName;
        this.description = description;
        this.status = status;
        this.duration = duration;
        this.startTime = startTime;
    }

    public Task(String taskName, String description, int id, TaskStatus status) {
        this.taskName = taskName;
        this.description = description;
        this.id = id;
        this.status = status;
    }

    public Task(String taskName, String description, TaskStatus status) {
        this.taskName = taskName;
        this.description = description;
        this.status = status;
    }

    public boolean timeIntersection(Task task) {
        LocalDateTime start1 = this.getStartTime();
        LocalDateTime end1 = this.getEndTime();

        LocalDateTime start2 = task.getStartTime();
        LocalDateTime end2 = task.getEndTime();

        if (start1 == null || end1 == null || start2 == null || end2 == null) {
            return false;
        }

        return !(start1.isAfter(end2) || start2.isAfter(end1));
    }

    public void setDurationOfMinutes(int minutes) {
        this.duration = Duration.ofMinutes(minutes);
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setStringStartTime(String startTime) {
        this.startTime = LocalDateTime.parse(startTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getStartTime() {
        if (startTime == null) {
            return null;
        }
        return startTime;
    }

    public LocalDateTime getEndTime() {
        if (startTime == null) {
            return null;
        }
        return startTime.plus(duration);
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