package ru.yandex.practicum.TaskManager.Service;

import ru.yandex.practicum.TaskManager.Model.Task;

import java.util.List;

public interface HistoryManager {

    void add(Task task);

    List<Task> getHistory();
}
