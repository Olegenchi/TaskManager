package model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Task {
    protected Integer id;
    protected String name;
    protected String description;
    protected Status status;
    protected TaskType taskType;
    protected LocalDateTime startTime;
    protected Long duration;

    public Task(Integer id, TaskType taskType, String name, String description, Status status,
                LocalDateTime startTime, Long duration) {
        this.id = id;
        this.taskType = taskType;
        this.name = name;
        this.description = description;
        this.status = status;
        this.startTime = startTime;
        this.duration = duration;
    }

    public Task(Integer id, TaskType taskType, String name, String description, Status status) {
        this(id, taskType, name, description, status, null, null);
    }

    public Task(Integer id, String name, String description, Status status) {
        this(id, null, name, description, status);

    }

    public Task(String name, String description, Status status, LocalDateTime startTime, Long duration) {
        this(null, null, name, description, status, startTime, duration);
    }

    public Task(Integer id, TaskType taskType, String name, String description) {
        this(id, taskType, name, description, null, null, null);
    }

    public Task(Integer id, TaskType taskType, String name, String description,
                LocalDateTime startTime, Long duration) {
        this(id, taskType, name, description, null, startTime, duration);
    }

    public Integer getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public TaskType getTaskType() {
        return taskType;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public Long getDuration() {
        return duration;
    }

    public LocalDateTime getEndTime() {
        return startTime.plusMinutes(duration);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id && Objects.equals(name, task.name) && Objects.equals(description, task.description)
                && status == task.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, id, status);
    }

    @Override
    public String toString() {
        return "Task{" +
                "\n\t id= " + id +
                "\n\t name= " + name +
                "\n\t description= " + description +
                "\n\t status= " + status +
                "\n\t}";
    }
}