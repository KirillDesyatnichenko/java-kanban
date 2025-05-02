package ru.yandex.practicum.TaskManager.Model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Epic extends Task {

    private ArrayList<SubTask> subTaskList = new ArrayList<>();
    private LocalDateTime endTime;

    public Epic(String taskName, String description, int id) {
        super(taskName, description, id, TaskStatus.NEW);
    }

    public Epic(String taskName, String description) {
        super(taskName, description, TaskStatus.NEW);
    }


    @Override
    public boolean timeIntersection(Task task) {
        LocalDateTime start1 = this.getStartTime();
        LocalDateTime end1 = this.getEndTime();

        LocalDateTime start2 = task.getStartTime();
        LocalDateTime end2 = task.getEndTime();

        if (start1 == null || end1 == null || start2 == null || end2 == null) {
            return false;
        }

        return !(start1.isAfter(end2) || start2.isAfter(end1));
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

    public void calculateEpicTime() {
        if (subTaskList != null && !subTaskList.isEmpty()) {
            LocalDateTime earliestStartTime =  null;
            LocalDateTime latestEndTime = null;
            duration = Duration.ZERO;

            for (SubTask subtask : subTaskList) {
                LocalDateTime startTime = subtask.getStartTime();
                LocalDateTime endTime = subtask.getEndTime();

                if (subtask.getDuration() != null) {
                    duration = duration.plus(subtask.getDuration());
                }

                if (startTime != null) {
                    if (earliestStartTime == null || startTime.isBefore(earliestStartTime)) {
                        earliestStartTime = startTime;
                    }
                }

                if (endTime != null) {
                    if (latestEndTime == null || endTime.isAfter(latestEndTime)) {
                        latestEndTime = endTime;
                    }
                }
            }

            if (earliestStartTime != null) {
                setStartTime(earliestStartTime);
            }

            if (latestEndTime != null) {
                this.endTime = latestEndTime;
            }
        } else {
            this.startTime = null;
            this.endTime = null;
            duration = Duration.ZERO;
        }
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }
}