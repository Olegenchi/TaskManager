package managers;

import model.Status;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @BeforeEach
    void inMemoryTaskManagerTestSetUp() {
        taskManager = new InMemoryTaskManager();
        taskManagerTestSetUp();
    }

    @Test
    void inValidationTest(){
        Task inValidTask = new Task("A","B", Status.NEW,
                LocalDateTime.of(2022,8,10,12,0),50L);

        taskManager.createTask(inValidTask);
        taskManager.createTask(task);
        taskManager.createEpic(epic);

        assertEquals(taskManager.validationOfTasks(task),false,"Задачи не верно проходят валидацию");
    }
}