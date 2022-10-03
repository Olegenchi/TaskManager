package managers;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.List;
import java.util.Set;

public interface TaskManager {

    void createTask(Task task);

    void updateTask(Task task);

    Task getTask(int id);

    void removeTask(int id);

    List<Task> getAllTasks();

    void removeAllTasks();

    void createSubtask(Subtask subtask);

    void updateSubtask(Subtask subtask);

    Subtask getSubtask(int id);

    void removeSubtask(int id);

    List<Subtask> getAllSubtasks();

    void createEpic(Epic epic);

    void updateEpic(Epic epic);

    Epic getEpic(int id);

    List<Subtask> getEpicSubtasks(Epic epic);

    void removeEpic(int id);

    List<Epic> getAllEpics();

    List<Task> getHistory();

    Set<Task> getPrioritizedTasks();
}