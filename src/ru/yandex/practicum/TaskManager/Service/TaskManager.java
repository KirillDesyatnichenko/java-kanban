package ru.yandex.practicum.TaskManager.Service;

import ru.yandex.practicum.TaskManager.Model.Epic;
import ru.yandex.practicum.TaskManager.Model.SubTask;
import ru.yandex.practicum.TaskManager.Model.Task;

import java.util.List;

public interface TaskManager {
    // Методы для задач.
    Task createNewTask(Task task);

    List<Task> getAllTasks();

    void tasksCleaning();

    Task getTaskById(int id);

    void deleteTaskById(int id);

    void updateTask(Task task);

    // Методы для эпиков
    Epic createNewEpic(Epic epic);

    List<Epic> getAllEpic();

    void epicCleaning();

    Task getEpicById(int id);

    void deleteEpicById(int id);

    void updateEpic(Epic epic);

    // Методы для подзадач
    SubTask createNewSubTask(SubTask subTask);

    List<SubTask> getAllSubTasks();

    void subTasksCleaning();

    SubTask getSubTaskById(int id);

    void deleteSubTaskById(int id);

    void updateSubTask(SubTask subTask);

    List<SubTask> getSubTasksByEpicId(int epicId);

    List<Task> getHistory();
}