package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private List<Subtask> subtasks = new ArrayList<>();
    private LocalDateTime endTime;

    public Epic(int id, TaskType taskType, String name, String description, Status status,
                LocalDateTime startTime, Long duration) {
        super(id, taskType, name, description, status, startTime, duration);
    }

    public Epic (int id, TaskType taskType, String name, String description, Status status) {
        super(id, taskType, name, description, status);
    }

    public Epic(int id, String name, String description, Status status) {
        super(id, name, description, status);
    }

    public Epic(String name, String description, Status status, LocalDateTime startTime, Long duration) {
        super(name, description, status, startTime, duration);
    }

    public Epic(int id, TaskType taskType, String name, String description) {
        super(id, taskType, name, description);
    }

    public Epic(int id, TaskType taskType, String name, String description, LocalDateTime startTime, long duration) {
        super(id, taskType, name, description, startTime, duration);
    }

    public List<Subtask> getSubtasks() {
        return subtasks;
    }

    public void addSubtask(Subtask subtask) {
        subtasks.add(subtask);
    }

    public void removeSubtask(Subtask subtask) {
        subtasks.remove(subtask);
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "\n\t id= " + getId() +
                "\n\t name= " + getName() +
                "\n\t description= " + getDescription() +
                "\n\t status= " + getStatus() +
                "\n\t subtasks= " + subtasks +
                "\n\t}";
    }
}
