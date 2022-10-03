package managers;

import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HistoryManagerTest {

    private Task task;
    private Epic epic;
    private Subtask subtask;
    private HistoryManager historyManager;

    @BeforeEach
    void historyManagerSetUp() {
        historyManager = Manager.getDefaultHistory();

        task = new Task(1, TaskType.TASK, "Task name", "Task description", Status.NEW);
        epic = new Epic(2, TaskType.EPIC, "Epic name", "Epic description", Status.NEW);
        subtask = new Subtask(3, TaskType.SUBTASK, "Subtask name", "Subtask description",
                Status.NEW, epic.getId());
    }

    @Test
    void addTaskHistoryList() {
        historyManager.addTaskHistoryList(task);

        final List<Task> history = historyManager.getTaskHistoryList();

        assertNotNull(history, "История не пустая.");
        assertEquals(1, history.size(), "История не пустая. В истории 1 элемент.");
    }

    @Test
    void getTaskHistoryList() {
        List<Task> history = historyManager.getTaskHistoryList();

        assertNotNull(history, "Пустая история задач.");
        assertTrue(history.isEmpty(), "Пустая история задач.");
    }

    @Test
    void removeTaskHistoryList() {
        historyManager.addTaskHistoryList(task);

        List<Task> history = historyManager.getTaskHistoryList();
        assertNotNull(history, "История не пустая.");
        assertEquals(1, history.size(), "История не пустая, в истории 1 элемент.");

        historyManager.removeTaskHistoryList(task.getId());
        history = historyManager.getTaskHistoryList();
        assertNotNull(history, "Пустая история задач.");
        assertTrue(history.isEmpty(), "Пустая история задач.");
    }

    @Test
    void addTwice() {
        historyManager.addTaskHistoryList(task);
        historyManager.addTaskHistoryList(task);

        final List<Task> history = historyManager.getTaskHistoryList();

        assertNotNull(history, "История не пустая.");
        assertEquals(1, history.size(), "История не пустая. В истории 1 элемент.");
    }

    @Test
    void removeFirst() {
        historyManager.addTaskHistoryList(task);
        historyManager.addTaskHistoryList(epic);
        historyManager.addTaskHistoryList(subtask);

        List<Task> history = historyManager.getTaskHistoryList();
        assertNotNull(history, "История не пустая.");
        assertEquals(3, history.size(), "История не пустая, в истории 3 элемента.");

        historyManager.removeTaskHistoryList(task.getId());
        history = historyManager.getTaskHistoryList();
        assertNotNull(history, "История не пустая.");
        assertEquals(2, history.size(), "История не пустая. В истории 2 элемента.");
        assertEquals(epic, history.get(0), "История не пустая. Получаем эпик по id.");
        assertEquals(subtask, history.get(1), "История не пустая. Получает сабтаск по id.");
    }

    @Test
    void removeMiddle() {
        historyManager.addTaskHistoryList(task);
        historyManager.addTaskHistoryList(epic);
        historyManager.addTaskHistoryList(subtask);

        List<Task> history = historyManager.getTaskHistoryList();
        assertNotNull(history, "История не пустая.");
        assertEquals(3, history.size(), "История не пустая, в истории 3 элемента.");

        historyManager.removeTaskHistoryList(epic.getId());
        history = historyManager.getTaskHistoryList();
        assertNotNull(history, "История не пустая.");
        assertEquals(2, history.size(), "История не пустая. В истории 2 элемента.");
        assertEquals(task, history.get(0), "История не пустая. Получаем таск по id.");
        assertEquals(subtask, history.get(1), "История не пустая. Получает сабтаск по id.");
    }

    @Test
    void removeLast() {
        historyManager.addTaskHistoryList(task);
        historyManager.addTaskHistoryList(epic);
        historyManager.addTaskHistoryList(subtask);

        List<Task> history = historyManager.getTaskHistoryList();
        assertNotNull(history, "История не пустая.");
        assertEquals(3, history.size(), "История не пустая, в истории 3 элемента.");

        historyManager.removeTaskHistoryList(subtask.getId());
        history = historyManager.getTaskHistoryList();
        assertNotNull(history, "История не пустая.");
        assertEquals(2, history.size(), "История не пустая. В истории 2 элемента.");
        assertEquals(task, history.get(0), "История не пустая. Получаем таск по id.");
        assertEquals(epic, history.get(1), "История не пустая. Получает эпик по id.");
    }
}