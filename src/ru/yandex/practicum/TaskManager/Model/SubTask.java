package ru.yandex.practicum.TaskManager.Model;

public class SubTask extends Task {
    private Epic epic;

    public SubTask(String taskName, String description, int id, TaskStatus status, Epic epic) {
        super(taskName, description, id, status);
        this.epic = epic;
    }

    public Epic getEpic() {
        return epic;
    }
}
