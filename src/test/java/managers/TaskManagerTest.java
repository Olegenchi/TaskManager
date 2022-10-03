package managers;

import model.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {

    protected T taskManager;
    protected Task task;
    protected Epic epic;
    protected Subtask subtask;

    void taskManagerTestSetUp() {
        task = new Task(1, TaskType.TASK, "Task name", "Task description", Status.NEW,
                LocalDateTime.of(2022,8,16,15,0),30L);
        taskManager.createTask(task);
        epic = new Epic(2, TaskType.EPIC, "Epic name", "Epic description",
                LocalDateTime.of(2022,8,16,16,30),
                30L);
        taskManager.createEpic(epic);
        subtask = new Subtask(3, TaskType.SUBTASK, "Subtask name", "Subtask description",
                Status.NEW, LocalDateTime.of(2022,8,16,16,30),
                30L, epic.getId());
        taskManager.createSubtask(subtask);
        epic.addSubtask(subtask);
    }

    @Test
    void createTask() {
        final Task savedTask = taskManager.getTask(task.getId());

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = taskManager.getAllTasks();

        assertNotNull(tasks, "Список задач пуст.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void updateTask() {
        task.setStatus(Status.DONE);

        taskManager.updateTask(task);

        assertEquals(taskManager.getTask(task.getId()).getStatus(),Status.DONE,"Задача не обновилась.");
    }

    @Test
    void getTask() {
        assertEquals(task, taskManager.getTask(1));
    }

    @Test
    void removeTask() {
        int id = task.getId();

        taskManager.removeTask(id);

        assertThrows(NullPointerException.class,()->taskManager.getTask(id),"Задача не удалена.");
    }

    @Test
    void getAllTasks() {
        List<Task> tasks = taskManager.getAllTasks();

        assertNotNull(tasks, "Не удалось получить задачи.");
        assertEquals(1, tasks.size(), "Не верное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void removeAllTasks() {
        List<Task> tasks = taskManager.getAllTasks();

        tasks.clear();

        assertEquals(0, tasks.size(), "Не верное количество задач.");
    }

    @Test
    void createSubtask() {
        final Subtask savedSubtask = taskManager.getSubtask(subtask.getId());

        assertNotNull(savedSubtask, "Подзадача не найдена.");
        assertEquals(subtask, savedSubtask, "Подзадачи не совпадают.");

        final List<Subtask> subtasks = taskManager.getAllSubtasks();

        assertNotNull(subtasks, "Список подзачач пуст.");
        assertEquals(1, subtasks.size(), "Неверное количество подзадач.");
        assertEquals(subtask, subtasks.get(0), "Подзадачи не совпадают.");
    }

    @Test
    void getSubtask() {
        assertEquals(subtask, taskManager.getSubtask(3));
    }

    @Test
    void removeSubtask() {
        int id = subtask.getId();

        taskManager.removeSubtask(id);
        assertThrows(NullPointerException.class,()->taskManager.getSubtask(id),"Задача не удалена.");
    }

    @Test
    void getAllSubtasks() {
        List<Subtask> subtasks = taskManager.getAllSubtasks();

        assertNotNull(subtasks, "Не удалось получить подзадачи.");
        assertEquals(1, subtasks.size(), "Не верное количество подзадач.");
        assertEquals(subtask, subtasks.get(0), "Подзадачи не совпадают.");
    }

    @Test
    void createEpic() {
        final Epic savedEpic = taskManager.getEpic(epic.getId());

        assertNotNull(savedEpic, "Эпик не найден.");
        assertEquals(epic, savedEpic, "Эпики не совпадают.");

        final List<Epic> epics = taskManager.getAllEpics();

        assertNotNull(epics, "Список эпиков пуст.");
        assertEquals(1, epics.size(), "Неверное количество эпиков.");
        assertEquals(epic, epics.get(0), "Эпики не совпадают.");
    }

    @Test
    void createEpicWithoutSubtasks() {
        epic.removeSubtask(subtask);
        epic.removeSubtask(subtask);
        final Epic savedEpic = taskManager.getEpic(epic.getId());

        assertNotNull(savedEpic, "Эпик не найден.");
        assertEquals(epic, savedEpic, "Эпики не совпадают.");

        final List<Epic> epics = taskManager.getAllEpics();

        assertNotNull(epics, "Список эпиков пуст.");
        assertEquals(1, epics.size(), "Неверное количество эпиков.");
        assertEquals(epic, epics.get(0), "Эпики не совпадают.");
    }

    @Test
    void getEpicWithoutSubtasks() {
        epic.removeSubtask(subtask);
        epic.removeSubtask(subtask);

        List<Subtask> epicsSubTasks = taskManager.getEpicSubtasks(epic);

        assertNotNull(epicsSubTasks, "Задачи не возвращаются.");
        assertTrue(epicsSubTasks.isEmpty(), "Не верное количество подзадач.");
    }

    @Test
    void getEpic() {
        assertEquals(epic, taskManager.getEpic(2));
    }

    @Test
    void removeEpic() {
        int id = epic.getId();

        taskManager.removeEpic(id);

        assertThrows(NullPointerException.class,()->taskManager.getEpic(id),"Задача не удалена.");
    }

    @Test
    void getAllEpics() {
        List<Epic> epics = taskManager.getAllEpics();

        assertNotNull(epics, "Не удалось получить подзадачи.");
        assertEquals(1, epics.size(), "Не верное количество подзадач.");
        assertEquals(epic, epics.get(0), "Подзадачи не совпадают.");
    }
}