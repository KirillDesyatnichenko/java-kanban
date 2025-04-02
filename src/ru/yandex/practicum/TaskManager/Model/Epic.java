package ru.yandex.practicum.TaskManager.Model;

import java.util.ArrayList;

public class Epic extends Task {

    private ArrayList<SubTask> subTaskList = new ArrayList<>();

    public Epic(String taskName, String description, int id, TaskStatus status) {
        super(taskName, description, id, status);
    }

    public Epic(String taskName, String description) {
        super(taskName, description, TaskStatus.NEW);
    }


    public ArrayList<SubTask> getSubTasks() {
        return new ArrayList<>(subTaskList);
    }

    public void deleteSubTaskById(int id) {
        for (int i = 0; i < subTaskList.size(); i++) {
            SubTask subTask = subTaskList.get(i);
            if (subTask.getTaskId() == id) {
                subTaskList.remove(i);
                break;
            }
        }
    }

    public void subTaskListCleaning() {
        subTaskList.clear();
    }

    public void addNewSubTask(SubTask subTask) {
        subTaskList.add(subTask);
    }

    public TaskStatus statusCalculation() {
        int i = 0;
        int j = 0;

        for (SubTask sub : subTaskList) {
            if (sub.getStatus().equals(TaskStatus.NEW)) {
                i++;
            } else if (sub.getStatus().equals(TaskStatus.DONE)) {
                j++;
            }
        }

        if (i == subTaskList.size()) {
            return TaskStatus.NEW;
        } else if (j == subTaskList.size()) {
            return TaskStatus.DONE;
        } else {
            return TaskStatus.IN_PROGRESS;
        }
    }
}