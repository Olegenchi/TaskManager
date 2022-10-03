package model;

import managers.InMemoryTaskManager;
import managers.Manager;
import managers.TaskManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    private TaskManager taskManager = new InMemoryTaskManager();
    private Epic epic;
    private Subtask subtask1;
    private Subtask subtask2;

    @BeforeEach
    void epicTestSetUp(){
        epic = new Epic(1, TaskType.EPIC, "Epic name", "Epic description");
        subtask1 = new Subtask(2, TaskType.SUBTASK, "Subtask name", "Subtask description",
                Status.NEW, LocalDateTime.of(2022,8,10,12,0),30L,
                epic.getId());
        subtask2 = new Subtask(3, TaskType.SUBTASK, "Subtask name", "Subtask description",
                Status.NEW, LocalDateTime.of(2022,8,10,12,30),30L,
                epic.getId());
    }
    @Test
    void shouldBeAddNewEpic() {
        taskManager.createEpic(epic);

        assertThrows(IndexOutOfBoundsException.class, () -> epic.getSubtasks().get(0),
                "Подзадача не может находиться в пустом эпике.");
        assertEquals(epic.getStatus(), Status.NEW, "Не правильно обновляется статус эпика.");
    }
    @Test
    void shouldBeAddEpicWithSubTasksNew(){
        epic.addSubtask(subtask1);
        epic.addSubtask(subtask2);

        taskManager.createEpic(epic);

        assertNotNull(epic.getSubtasks().get(0),"Подзадачи на возвращаются.");
        assertNotNull(epic.getSubtasks().get(1),"Подзадачи на возвращаются.");
        assertEquals(epic.getSubtasks().size(),2,"Неверное количество подзадач.");
        assertEquals(subtask1.getEpicId(), epic.getId(),"Id эпика подзадачи не равно id эпика.");
        assertEquals(epic.getStatus(),Status.NEW,"Не правильно обновляется статус эпика.");
    }
    @Test
    void shouldBeAddEpicWithSubTasksDone(){
        subtask1.setStatus(Status.DONE);
        subtask2.setStatus(Status.DONE);
        epic.addSubtask(subtask1);
        epic.addSubtask(subtask2);

        taskManager.createEpic(epic);

        assertNotNull(epic.getSubtasks().get(0),"Подзадачи на возвращаются.");
        assertNotNull(epic.getSubtasks().get(1),"Подзадачи на возвращаются.");
        assertEquals(epic.getSubtasks().size(),2,"Неверное количество подзадач.");
        assertEquals(subtask1.getEpicId(), epic.getId(),"Id эпика подзадачи не равно id эпика.");
        assertEquals(epic.getStatus(),Status.DONE,"Не правильно обновляется статус эпика.");
    }
    @Test
    void shouldBeAddEpicWithSubTasksNewAndDone(){
        subtask1.setStatus(Status.DONE);
        subtask2.setStatus(Status.NEW);
        epic.addSubtask(subtask1);
        epic.addSubtask(subtask2);

        taskManager.createEpic(epic);

        assertNotNull(epic.getSubtasks().get(0),"Подзадачи на возвращаются.");
        assertNotNull(epic.getSubtasks().get(1),"Подзадачи на возвращаются.");
        assertEquals(epic.getSubtasks().size(),2,"Неверное количество подзадач.");
        assertEquals(subtask1.getEpicId(), epic.getId(),"Id эпика подзадачи не равно id эпика.");
        assertEquals(epic.getStatus(),Status.IN_PROGRESS,"Не правильно обновляется статус эпика.");
    }
    @Test
    void shouldBeAddEpicWithSubTasksInProgress(){
        subtask1.setStatus(Status.IN_PROGRESS);
        subtask2.setStatus(Status.IN_PROGRESS);
        epic.addSubtask(subtask1);
        epic.addSubtask(subtask2);

        taskManager.createEpic(epic);

        assertNotNull(epic.getSubtasks().get(0),"Подзадачи на возвращаются.");
        assertNotNull(epic.getSubtasks().get(1),"Подзадачи на возвращаются.");
        assertEquals(epic.getSubtasks().size(),2,"Неверное количество подзадач.");
        assertEquals(subtask1.getEpicId(), epic.getId(),"Id эпика подзадачи не равно id эпика.");
        assertEquals(epic.getStatus(),Status.IN_PROGRESS,"Не правильно обновляется статус эпика.");
    }


}