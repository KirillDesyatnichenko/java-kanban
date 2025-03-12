package ru.yandex.practicum.TaskManager.Service;

import ru.yandex.practicum.TaskManager.Model.Epic;
import ru.yandex.practicum.TaskManager.Model.SubTask;
import ru.yandex.practicum.TaskManager.Model.Task;

import java.util.ArrayList;

public interface TaskManager {
    // Методы для задач.
    Task createNewTask(Task task);

    ArrayList<Task> getAllTasks();

    void tasksCleaning();

    Task getTaskById(int id);

    void deleteTaskById(int id);

    void updateTask(Task task);

    // Методы для эпиков
    Epic createNewEpic(Epic epic);

    ArrayList<Epic> getAllEpic();

    void epicCleaning();

    Task getEpicById(int id);

    void deleteEpicById(int id);

    void updateEpic(Epic epic);

    // Методы для подзадач
    SubTask createNewSubTask(SubTask subTask);

    ArrayList<SubTask> getAllSubTasks();

    void subTasksCleaning();

    SubTask getSubTaskById(int id);

    void deleteSubTaskById(int id);

    void updateSubTask(SubTask subTask);

    ArrayList<SubTask> getSubTasksByEpicId(int epicId);
}