package ru.yandex.practicum.TaskManager.Service;

import ru.yandex.practicum.TaskManager.Model.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private Path path;

    public FileBackedTaskManager(Path path) {
        this.path = path;
    }

    public void save() {
        try (FileWriter wr = new FileWriter(String.valueOf(path))) {
            wr.write("id,type,name,status,description,epicId" + "\n");

            for (Task task : getAllTasks()) {
                wr.write(TaskToString(task) + "\n");
            }
            for (Epic epic : getAllEpic()) {
                wr.write(TaskToString(epic) + "\n");
            }
            for (SubTask subTask : getAllSubTasks()) {
                wr.write(TaskToString(subTask) + "\n");
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при записи файла.");
        }
    }

    private String TaskToString(Task task) {
        String typeTask = task.getClass().getSimpleName();
        String value;

        if (typeTask.equals("SubTask")) {
            SubTask subTask = (SubTask) task;
            value = String.format("%d,%s,%s,%s,%s,%d",
                    subTask.getTaskId(),
                    typeTask.toUpperCase(),
                    subTask.getTaskName(),
                    subTask.getStatus(),
                    subTask.getDescription(),
                    subTask.getEpicId()
            );
        } else {
            value = String.format("%d,%s,%s,%s,%s",
                    task.getTaskId(),
                    typeTask.toUpperCase(),
                    task.getTaskName(),
                    task.getStatus(),
                    task.getDescription()
            );
        }
        return value;
    }

    private static Task fromString(String value) {

        String[] values = value.split(",");

        int id = Integer.parseInt(values[0]);
        String type = values[1];
        String name = values[2];
        TaskStatus status = TaskStatus.valueOf(values[3]);
        String description = values[4];

        if (TypeOfTasks.valueOf(type).equals(TypeOfTasks.TASK)) {
            return new Task(name, description, id, status);
        }

        if (TypeOfTasks.valueOf(type).equals(TypeOfTasks.EPIC)) {
            return new Epic(name, description, id, status);
        }

        if (TypeOfTasks.valueOf(type).equals(TypeOfTasks.SUBTASK)) {
            int idEpic = Integer.parseInt(values[5]);
            return new SubTask(name, description, id, status, idEpic);
        } else {
            throw new IllegalArgumentException("Тип задачи не поддерживается");
        }
    }

    public static FileBackedTaskManager loadFromFile(Path path) throws IOException {
        FileBackedTaskManager fileBackedTasksManager = new FileBackedTaskManager(path);
        try { List<String> lines = Files.readAllLines(path);
        for (String line : lines) {
            if (!line.isBlank() && !line.equals("id,type,name,status,description,epicId")) {
                Task task = fromString(line);

                if (task instanceof Epic) {
                    fileBackedTasksManager.setEpic(task.getTaskId(), (Epic) task);
                } else if (task instanceof SubTask) {
                    fileBackedTasksManager.setSubTask(task.getTaskId(), (SubTask) task);
                } else {
                    fileBackedTasksManager.setTask(task.getTaskId(), task);
                }
            }
        }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при чтении файла.");
        }
        fileBackedTasksManager.addAllSubtasksInEpics();
        return fileBackedTasksManager;
    }

    @Override
    public Task createNewTask(Task task) {
        Task newTask = super.createNewTask(task);
        save();
        return newTask;
    }

    @Override
    public void tasksCleaning() {
        super.tasksCleaning();
        save();
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public Epic createNewEpic(Epic epic) {
        Epic newEpic = super.createNewEpic(epic);
        save();
        return newEpic;
    }

    @Override
    public void epicCleaning() {
        super.epicCleaning();
        save();
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public SubTask createNewSubTask(SubTask subTask) {
        SubTask newSubTask = super.createNewSubTask(subTask);
        save();
        return newSubTask;
    }

    @Override
    public void subTasksCleaning() {
        super.subTasksCleaning();
        save();
    }

    @Override
    public void deleteSubTaskById(int id) {
        super.deleteSubTaskById(id);
        save();
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        super.updateSubTask(subTask);
        save();
    }

    // Реализовывать пользовательский сценарий не стал,
    // так как он уже реализован в тестах FileBackedTaskManagerTest в разной вариации.
}