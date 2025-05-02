package ru.yandex.practicum.TaskManager.Service;

import ru.yandex.practicum.TaskManager.Model.Epic;
import ru.yandex.practicum.TaskManager.Model.SubTask;
import ru.yandex.practicum.TaskManager.Model.Task;
import ru.yandex.practicum.TaskManager.Model.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private int id = 0;
    private Map<Integer, Task> tasks = new HashMap<>();
    private Map<Integer, Epic> epics = new HashMap<>();
    private Map<Integer, SubTask> subTasks = new HashMap<>();
    private Set<Task> prioritizedTasks = new TreeSet<>(Comparator.<Task>comparingInt(Task::getTaskId)
            .thenComparing(Task::getStartTime));

    private HistoryManager historyManager = Managers.getDefaultHistory();

    private int idGenerator() {
        List<Integer> allIds = new ArrayList<>();
        allIds.addAll(tasks.keySet());
        allIds.addAll(epics.keySet());
        allIds.addAll(subTasks.keySet());

        return allIds.isEmpty() ? 1 : allIds.stream().max(Integer::compareTo).orElseThrow() + 1;
    }

    private void addToPrioritizedTasks(Task task) {
        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }
    }

    public boolean checkingIntersection(Task task) {
        return prioritizedTasks.stream()
                .anyMatch(existingTask -> existingTask.timeIntersection(task));
    }

    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    // Методы для задач.
    @Override
    public Task createNewTask(Task task) {
        int newId = idGenerator();
        TaskStatus status = task.getStatus();
        String taskName = task.getTaskName();
        String description = task.getDescription();
        Duration duration = task.getDuration();
        LocalDateTime startTime = task.getStartTime();
        Task newTask = new Task(taskName, description, newId, status, duration, startTime);
        tasks.put(newId, newTask);
        if (checkingIntersection(newTask)) {
            throw new IllegalArgumentException("Время выполнения задачи пересекается с существующими задачами");
        } else {
        addToPrioritizedTasks(newTask);
        return newTask;
        }
    }

    @Override
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public void tasksCleaning() {
        tasks.keySet().forEach(historyManager::remove);
        prioritizedTasks.removeIf(task -> task.getTaskId() == id);
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
        prioritizedTasks.removeIf(task -> task.getTaskId() == id);
    }

    @Override
    public void updateTask(Task task) {
        int taskId = task.getTaskId();
        if (!tasks.containsKey(taskId)) {
            System.out.println("Задача не найдена");
            return;
        }
            Task newTask = tasks.get(taskId);
            prioritizedTasks.remove(newTask);
            newTask.setTaskName(task.getTaskName());
            newTask.setDescription(task.getDescription());
            newTask.setStatus(task.getStatus());
            newTask.setDuration(task.getDuration());
            newTask.setStartTime(task.getStartTime());
        if (checkingIntersection(newTask)) {
            throw new IllegalArgumentException("Время выполнения задачи пересекается с существующими задачами");
        } else {
            addToPrioritizedTasks(newTask);
            }
    }

    // Методы для эпиков
    @Override
    public Epic createNewEpic(Epic epic) {
        int newId = idGenerator();
        String epicName = epic.getTaskName();
        String description = epic.getDescription();
        Epic newEpic = new Epic(epicName, description, newId);
        epics.put(newId, newEpic);
        return newEpic;
    }

    @Override
    public ArrayList<Epic> getAllEpic() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public void epicCleaning() {
        for (Integer id : epics.keySet()) {
            historyManager.remove(id);
            prioritizedTasks.removeIf(task -> task.getTaskId() == id);
        }

        for (Integer id : subTasks.keySet()) {
            historyManager.remove(id);
            prioritizedTasks.removeIf(task -> task.getTaskId() == id);
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
                prioritizedTasks.remove(subTask);
            }
        }
        epics.remove(id);
        historyManager.remove(id);
        prioritizedTasks.removeIf(task -> task.getTaskId() == id);
    }

    @Override
    public void updateEpic(Epic epic) {
        int epicId = epic.getTaskId();
        if (!epics.containsKey(epicId)) {
            System.out.println("Эпик не найден");
            return;
        }
            Epic newEpic = epics.get(epicId);
            prioritizedTasks.remove(newEpic);
            newEpic.setTaskName(epic.getTaskName());
            newEpic.setDescription(epic.getDescription());
            addToPrioritizedTasks(newEpic);
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
            Duration duration = subTask.getDuration();
            LocalDateTime startTime = subTask.getStartTime();
            SubTask newSubTask = new SubTask(subTaskName, description, newId, newStatus, duration, startTime, epicId);
        if (checkingIntersection(newSubTask)) {
            throw new IllegalArgumentException("Время выполнения задачи пересекается с существующими задачами");
        } else {
            subTasks.put(newId, newSubTask);
            addToPrioritizedTasks(newSubTask);
        }
            Epic epic = epics.get(epicId);
            prioritizedTasks.remove(epic);
            epic.addNewSubTask(newSubTask);
            epic.setStatus(epic.statusCalculation());
            epic.calculateEpicTime();
            addToPrioritizedTasks(epic);
            return newSubTask;
    }

    @Override
    public ArrayList<SubTask> getAllSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public void subTasksCleaning() {
        for (Epic epic : epics.values()) {
            prioritizedTasks.remove(epic);
            epic.subTaskListCleaning();
            epic.setStatus(epic.statusCalculation());
            epic.calculateEpicTime();
            addToPrioritizedTasks(epic);
        }

        for (Integer id : subTasks.keySet()) {
            historyManager.remove(id);
            prioritizedTasks.removeIf(task -> task.getTaskId() == id);
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
        prioritizedTasks.remove(subTask);
        Epic epic = epics.get(subTask.getEpicId());
        prioritizedTasks.remove(epic);
        epic.deleteSubTaskById(id);
        subTasks.remove(id);
        historyManager.remove(id);
        epic.setStatus(epic.statusCalculation());
        epic.calculateEpicTime();
        addToPrioritizedTasks(epic);
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        int subTaskId = subTask.getTaskId();

        if (!subTasks.containsKey(subTaskId)) {
            System.out.println("Субзадача не найдена");
            return;
        }
        SubTask newSubTask = subTasks.get(subTaskId);
        prioritizedTasks.remove(newSubTask);
        newSubTask.setTaskName(subTask.getTaskName());
        newSubTask.setDescription(subTask.getDescription());
        newSubTask.setStatus(subTask.getStatus());
        newSubTask.setDuration(subTask.getDuration());
        newSubTask.setStartTime(subTask.getStartTime());
        if (checkingIntersection(newSubTask)) {
            throw new IllegalArgumentException("Время выполнения задачи пересекается с существующими задачами");
        } else {
            addToPrioritizedTasks(newSubTask);
        }

        if (newSubTask.getEpicId() != (subTask.getEpicId())) {
            System.out.println("Id эпика не совпадают");
            return;
        }
        Epic epic = epics.get(subTask.getEpicId());
        prioritizedTasks.remove(epic);
        epic.deleteSubTaskById(subTaskId);
        epic.addNewSubTask(newSubTask);
        epic.setStatus(epic.statusCalculation());
        epic.calculateEpicTime();
        addToPrioritizedTasks(epic);
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
        if (checkingIntersection(task)) {
            throw new IllegalArgumentException("Время выполнения задачи пересекается с существующими задачами");
        } else {
            tasks.put(id, task);
            addToPrioritizedTasks(task);
        }
    }

    protected  void setEpic(int id, Epic epic) {
        epics.put(id, epic);
    }

    protected void setSubTask(int id, SubTask subTask) {
        if (checkingIntersection(subTask)) {
            throw new IllegalArgumentException("Время выполнения задачи пересекается с существующими задачами");
        } else {
            subTasks.put(id, subTask);
            addToPrioritizedTasks(subTask);
        }
    }

    protected void addAllSubtasksInEpics() {
        ArrayList<SubTask> allSubTasks = getAllSubTasks();
        for (SubTask subTask : allSubTasks) {
            Epic epic = epics.get(subTask.getEpicId());
            prioritizedTasks.remove(epic);
            ArrayList<SubTask> subTaskList = epic.getSubTasks();

            if (!subTaskList.contains(subTask)) {
                epic.addNewSubTask(subTask);
                epic.setStatus(epic.statusCalculation());
                epic.calculateEpicTime();
                addToPrioritizedTasks(epic);
            }
        }
    }
}