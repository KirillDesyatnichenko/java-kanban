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

    public int idGenerator() {
        id ++;
        return id;
    }

    // Методы для задач.
    public Task createNewTask(String taskName, String description) {
        int newId = idGenerator();
        TaskStatus newStatus = TaskStatus.NEW;
        Task task = new Task(taskName, description, newId, newStatus);
        tasks.put(newId, task);
        return task;
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

    public void updateTask(int taskId, Task task) {
        Task task1 = tasks.get(taskId);
        task1.setTaskName(task.getTaskName());
        task1.setDescription(task.getDescription());
        task1.setStatus(task.getStatus());
        tasks.put(taskId, task1);
    }

    // Методы для эпиков
    public Epic createNewEpic(String taskName, String description) {
        int newId = idGenerator();
        Epic epic = new Epic(taskName, description, newId);
        epics.put(newId, epic);
        return epic;
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
        for (SubTask subTask : epic.getSubTasks()) {
            subTasks.remove(subTask.getId());
        }
        epics.remove(id);
    }

    public void updateEpic(int epicId, Epic epic) {
        Epic epic1 = epics.get(epicId);
        epic1.setTaskName(epic.getTaskName());
        epic1.setDescription(epic.getDescription());
        ArrayList<SubTask> NewSubTaskList = epic.getSubTasks();
        epic1.setSubTask(NewSubTaskList);
        epics.put(epicId, epic1);
    }

    // Методы для подзадач
    public SubTask createNewSubTask(String taskName, String description, Epic epic) {
        int newId = idGenerator();
        TaskStatus newStatus = TaskStatus.NEW;
        SubTask subTask = new SubTask(taskName, description, newId, newStatus, epic);
        subTasks.put(newId, subTask);
        ArrayList<SubTask> subTaskList = epic.getSubTasks();
        subTaskList.add(subTask);
        epic.setSubTask(subTaskList);
        return subTask;
    }

    public ArrayList<SubTask> getAllSubTasks() {
        ArrayList<SubTask> allSubTasks = new ArrayList<>();
        for (SubTask value : subTasks.values()) {
            allSubTasks.add(value);
        }
        return allSubTasks;
    }

    public void subTasksCleaning () {
        subTasks.clear();
    }

    public SubTask getSubTaskById (int id) {
        return subTasks.get(id);
    }

    public void deleteSubTaskById(int id) {
        subTasks.remove(id);
    }

    public void updateSubTask(int subTaskId, SubTask subTask) {
        SubTask subTask1 = subTasks.get(subTaskId);
        subTask1.setTaskName(subTask.getTaskName());
        subTask1.setDescription(subTask.getDescription());
        subTask1.setStatus(subTask.getStatus());
        subTasks.put(subTaskId, subTask);

        ArrayList<SubTask> subTaskList = subTask.getEpic().getSubTasks();
        for (int i = 0; i < subTaskList.size(); i++) {
            if (subTaskList.get(i).getId() == subTaskId) {
                subTaskList.set(i, subTask);
                break;
            }
        }
    }

    public ArrayList<SubTask> getSubTasksByIdEpic(int epicId) {
        ArrayList<SubTask> epicSubTasks = epics.get(epicId).getSubTasks();
        return epicSubTasks;
    }
}