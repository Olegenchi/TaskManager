package model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Subtask extends Task {
    private int epicId;

    public Subtask(int id, TaskType taskType, String name, String description, Status status,
                LocalDateTime startTime, Long duration, int epicId) {
        super(id, taskType, name, description, status, startTime, duration);
        this.epicId = epicId;
    }

    public Subtask(int id, TaskType taskType, String name, String description, Status status, int epicId) {
        super(id, taskType, name, description, status);
        this.epicId = epicId;
    }

    public Subtask(int id, String name, String description, Status status, int epicId) {
        super(id, name, description, status);
        this.epicId = epicId;
    }

    public Subtask(String name, String description, Status status, int epicId) {
        super(epicId, name, description, status);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Subtask)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        return getEpicId() == ((Subtask) o).getEpicId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getEpicId());
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "\n\t id= " + getId() +
                "\n\t name= " + getName() +
                "\n\t description= " + getDescription() +
                "\n\t status= " + getStatus() +
                "\n\t}";
    }
}