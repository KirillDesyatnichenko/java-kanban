package ru.yandex.practicum.TaskManager.Model;

import java.util.ArrayList;

public class Epic extends Task {

    private ArrayList<SubTask> subTaskList = new ArrayList<>();

    public Epic(String taskName, String description, int id) {
        super(taskName, description, id);
    }


    public ArrayList<SubTask> getSubTasks() {
        return subTaskList;
    }

    public void setSubTask(ArrayList<SubTask> subTask) {
        this.subTaskList = subTask;
    }

    @Override
    public TaskStatus getStatus() {
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