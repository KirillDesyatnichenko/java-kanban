package ru.yandex.practicum.TaskManager.Service;

import ru.yandex.practicum.TaskManager.Model.Epic;
import ru.yandex.practicum.TaskManager.Model.SubTask;
import ru.yandex.practicum.TaskManager.Model.Task;
import ru.yandex.practicum.TaskManager.Model.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private int id = 0;
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private HashMap<Integer, SubTask> subTasks = new HashMap<>();

    private int idGenerator() {
        id ++;
        return id;
    }

    // Методы для задач.
    public Task createNewTask(Task task) {
        int newId = idGenerator();
        TaskStatus Status = task.getStatus();
        String taskName = task.getTaskName();
        String description = task.getDescription();
        Task taskNew = new Task(taskName, description, newId, Status);
        tasks.put(newId, taskNew);
        return taskNew;
    }

    public ArrayList<Task> getAllTasks() {
        ArrayList<Task> allTasks = new ArrayList<>();
        for (Task value : tasks.values()) {
            allTasks.add(value);
        }
        return allTasks;
    }

    public void tasksCleaning () {
        tasks.clear();
    }

    public Task getTaskById (int id) {
        return tasks.get(id);
    }

    public void deleteTaskById(int id) {
        tasks.remove(id);
    }

    public void updateTask(Task task) {
        int taskId = task.getTaskId();
        if (!tasks.containsKey(taskId)) {
            System.out.println("Задача не найдена");
            return;
        }
            Task newTask = tasks.get(taskId);
            newTask.setTaskName(task.getTaskName());
            newTask.setDescription(task.getDescription());
            newTask.setStatus(task.getStatus());
            tasks.put(taskId, newTask);
    }

    // Методы для эпиков
    public Epic createNewEpic(Epic epic) {
        int newId = idGenerator();
        TaskStatus newStatus = epic.getStatus();
        String epicName = epic.getTaskName();
        String description = epic.getDescription();
        Epic newEpic = new Epic(epicName, description, newId, newStatus);
        epics.put(newId, newEpic);
        return newEpic;
    }

    public ArrayList<Epic> getAllEpic() {
        ArrayList<Epic> allEpic = new ArrayList<>();
        for (Epic value : epics.values()) {
            allEpic.add(value);
        }
        return allEpic;
    }

    public void epicCleaning () {
        epics.clear();
        subTasks.clear();
    }

    public Task getEpicById (int id) {
        return epics.get(id);
    }

    public void deleteEpicById(int id) {
        Epic epic = epics.get(id);

        if (epic != null) {
            for (SubTask subTask : epic.getSubTasks()) {
                subTasks.remove(subTask.getTaskId());
            }
        }
        epics.remove(id);
    }

    public void updateEpic(Epic epic) {
        int epicId = epic.getTaskId();
        if (!epics.containsKey(epicId)) {
            System.out.println("Эпик не найден");
            return;
        }
            Epic newEpic = epics.get(epicId);
            newEpic.setTaskName(epic.getTaskName());
            newEpic.setDescription(epic.getDescription());
            epics.put(epicId, newEpic);
    }

    // Методы для подзадач
    public SubTask createNewSubTask(SubTask subTask) {
        int epicId = subTask.getEpicId();
        if (!epics.containsKey(epicId)) {
            System.out.println("Эпик не найден");
        }
            int newId = idGenerator();
            TaskStatus newStatus = subTask.getStatus();
            String subTaskName = subTask.getTaskName();
            String description = subTask.getDescription();
            SubTask newSubTask = new SubTask(subTaskName, description, newId, newStatus, epicId);
            subTasks.put(newId, newSubTask);

            Epic epic = epics.get(epicId);
            epic.addNewSubTask(newSubTask);
            epic.setStatus(epic.getStatus());

            return newSubTask;
    }

    public ArrayList<SubTask> getAllSubTasks() {
        ArrayList<SubTask> allSubTasks = new ArrayList<>();
        for (SubTask value : subTasks.values()) {
            allSubTasks.add(value);
        }
        return allSubTasks;
    }

    public void subTasksCleaning () {
        for (Epic epic : epics.values()) {
            epic.subTaskListCleaning ();
            epic.setStatus(epic.getStatus());
        }
        subTasks.clear();
    }

    public SubTask getSubTaskById (int id) {
        return subTasks.get(id);
    }

    public void deleteSubTaskById(int id) {
        if (!subTasks.containsKey(id)) {
            System.out.println("Субзадача не найдена");
            return;
        }
        SubTask subTask = subTasks.get(id);
        Epic epic = epics.get(subTask.getEpicId());
        epic.deleteSubTaskById(id);
        epic.setStatus(epic.getStatus());
        subTasks.remove(id);
    }

    public void updateSubTask(SubTask subTask) {
        int subTaskId = subTask.getTaskId();

        if (!subTasks.containsKey(subTaskId)) {
            System.out.println("Субзадача не найдена");
            return;
        }
        SubTask newSubTask = subTasks.get(subTaskId);

        if (newSubTask.getEpicId() != (subTask.getEpicId())) {
            System.out.println("Id эпика не совпадают");
            return;
        }
        Epic epic = epics.get(subTask.getEpicId());
        epic.deleteSubTaskById(subTaskId);
        epic.addNewSubTask(subTask);
        epic.setStatus(epic.getStatus());
        subTasks.put(subTaskId, subTask);
    }

    public ArrayList<SubTask> getSubTasksByEpicId(int epicId) {
        if (!epics.containsKey(epicId)) {
            System.out.println("Эпик не найден");
        }
        return epics.get(epicId).getSubTasks();
    }
}