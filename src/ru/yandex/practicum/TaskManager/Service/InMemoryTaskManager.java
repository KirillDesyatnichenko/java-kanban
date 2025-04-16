package ru.yandex.practicum.TaskManager.Service;

import ru.yandex.practicum.TaskManager.Model.Epic;
import ru.yandex.practicum.TaskManager.Model.SubTask;
import ru.yandex.practicum.TaskManager.Model.Task;
import ru.yandex.practicum.TaskManager.Model.TaskStatus;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private int id = 0;
    private Map<Integer, Task> tasks = new HashMap<>();
    private Map<Integer, Epic> epics = new HashMap<>();
    private Map<Integer, SubTask> subTasks = new HashMap<>();

    private HistoryManager historyManager = Managers.getDefaultHistory();

    private int idGenerator() {
        List<Integer> allIds = new ArrayList<>();
        allIds.addAll(tasks.keySet());
        allIds.addAll(epics.keySet());
        allIds.addAll(subTasks.keySet());

        if (allIds.isEmpty()) {
            return 1;
        }

        id = Collections.max(allIds);
        id++;
        return id;
    }

    // Методы для задач.
    @Override
    public Task createNewTask(Task task) {
        int newId = idGenerator();
        TaskStatus status = task.getStatus();
        String taskName = task.getTaskName();
        String description = task.getDescription();
        Task taskNew = new Task(taskName, description, newId, status);
        tasks.put(newId, taskNew);
        return taskNew;
    }

    @Override
    public ArrayList<Task> getAllTasks() {
        ArrayList<Task> allTasks = new ArrayList<>();
        for (Task value : tasks.values()) {
            allTasks.add(value);
        }
        return allTasks;
    }

    @Override
    public void tasksCleaning() {
        for (Integer id : tasks.keySet()) {
            historyManager.remove(id);
        }
        tasks.clear();
    }

    @Override
    public Task getTaskById(int id) {
        historyManager.add(tasks.get(id));
        return tasks.get(id);
    }

    @Override
    public void deleteTaskById(int id) {
        tasks.remove(id);
        historyManager.remove(id);
    }

    @Override
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
    }

    // Методы для эпиков
    @Override
    public Epic createNewEpic(Epic epic) {
        int newId = idGenerator();
        TaskStatus newStatus = epic.getStatus();
        String epicName = epic.getTaskName();
        String description = epic.getDescription();
        Epic newEpic = new Epic(epicName, description, newId, newStatus);
        epics.put(newId, newEpic);
        return newEpic;
    }

    @Override
    public ArrayList<Epic> getAllEpic() {
        ArrayList<Epic> allEpic = new ArrayList<>();
        for (Epic value : epics.values()) {
            allEpic.add(value);
        }
        return allEpic;
    }

    @Override
    public void epicCleaning() {
        for (Integer id : epics.keySet()) {
            historyManager.remove(id);
        }

        for (Integer id : subTasks.keySet()) {
            historyManager.remove(id);
        }
        epics.clear();
        subTasks.clear();
    }

    @Override
    public Epic getEpicById(int id) {
        historyManager.add(epics.get(id));
        return epics.get(id);
    }

    @Override
    public void deleteEpicById(int id) {
        Epic epic = epics.get(id);

        if (epic != null) {
            for (SubTask subTask : epic.getSubTasks()) {
                subTasks.remove(subTask.getTaskId());
                historyManager.remove(subTask.getTaskId());
            }
        }
        epics.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void updateEpic(Epic epic) {
        int epicId = epic.getTaskId();
        if (!epics.containsKey(epicId)) {
            System.out.println("Эпик не найден");
            return;
        }
            Epic newEpic = epics.get(epicId);
            newEpic.setTaskName(epic.getTaskName());
            newEpic.setDescription(epic.getDescription());
    }

    // Методы для подзадач
    @Override
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
            epic.setStatus(epic.statusCalculation());

            return newSubTask;
    }

    @Override
    public ArrayList<SubTask> getAllSubTasks() {
        ArrayList<SubTask> allSubTasks = new ArrayList<>();
        for (SubTask value : subTasks.values()) {
            allSubTasks.add(value);
        }
        return allSubTasks;
    }

    @Override
    public void subTasksCleaning() {
        for (Epic epic : epics.values()) {
            epic.subTaskListCleaning();
            epic.setStatus(epic.statusCalculation());
        }

        for (Integer id : subTasks.keySet()) {
            historyManager.remove(id);
        }
        subTasks.clear();
    }

    @Override
    public SubTask getSubTaskById(int id) {
        historyManager.add(subTasks.get(id));
        return subTasks.get(id);
    }

    @Override
    public void deleteSubTaskById(int id) {
        if (!subTasks.containsKey(id)) {
            System.out.println("Субзадача не найдена");
            return;
        }
        SubTask subTask = subTasks.get(id);
        Epic epic = epics.get(subTask.getEpicId());
        epic.deleteSubTaskById(id);
        subTasks.remove(id);
        historyManager.remove(id);
        epic.setStatus(epic.statusCalculation());
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        int subTaskId = subTask.getTaskId();

        if (!subTasks.containsKey(subTaskId)) {
            System.out.println("Субзадача не найдена");
            return;
        }
        SubTask newSubTask = subTasks.get(subTaskId);
        newSubTask.setTaskName(subTask.getTaskName());
        newSubTask.setDescription(subTask.getDescription());
        newSubTask.setStatus(subTask.getStatus());

        if (newSubTask.getEpicId() != (subTask.getEpicId())) {
            System.out.println("Id эпика не совпадают");
            return;
        }
        Epic epic = epics.get(subTask.getEpicId());
        epic.deleteSubTaskById(subTaskId);
        epic.addNewSubTask(newSubTask);
        epic.setStatus(epic.statusCalculation());
    }

    @Override
    public ArrayList<SubTask> getSubTasksByEpicId(int epicId) {
        if (!epics.containsKey(epicId)) {
            System.out.println("Эпик не найден");
        }
        return epics.get(epicId).getSubTasks();
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    public boolean containsTask(int id) {
        return tasks.containsKey(id);
    }

    public boolean containsEpic(int id) {
        return epics.containsKey(id);
    }

    public boolean containsSubTasks(int id) {
        return subTasks.containsKey(id);
    }

    protected  void setTask(int id, Task task) {
        tasks.put(id, task);
    }

    protected  void setEpic(int id, Epic epic) {
        epics.put(id, epic);
    }

    protected void setSubTask(int id, SubTask subTask) {
        subTasks.put(id, subTask);
    }

    protected void addAllSubtasksInEpics() {
        ArrayList<SubTask> allSubTasks = getAllSubTasks();
        for (SubTask subTask : allSubTasks) {
            Epic epic = epics.get(subTask.getEpicId());
            ArrayList<SubTask> subTaskList = epic.getSubTasks();

            if (!subTaskList.contains(subTask)) {
                epic.addNewSubTask(subTask);
                epic.setStatus(epic.statusCalculation());
            }
        }
    }
}