package managers;

import model.Epic;
import model.Task;
import model.TaskType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {

    private File file;

    @BeforeEach
    void fileBackedTasksManagerTestSetUp() {
        file = new File("src/test/resources/tasks.csv");
        taskManager = new FileBackedTasksManager(file);
        taskManagerTestSetUp();
    }

    /*@AfterEach
    void fileBackedTasksManagerTearDown() {
        assertTrue(file.delete());
    }*/

    @Test
    void fileBackedTasksManagerLoadFromFile() {
        taskManager.getTask(task.getId());
        FileBackedTasksManager tasksManager = FileBackedTasksManager.load(file);

        List<Task> tasks = tasksManager.getAllTasks();

        assertNotNull(tasks, "Список пуст.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void loadFromFileOneEpicTest(){
        Epic epic1 = new Epic(2, TaskType.EPIC, "Epic name", "Epic description");

        taskManager.createEpic(epic1);
        Epic saveEpic = taskManager.getEpic(epic1.getId());
        List<Task> saveHistory = taskManager.getHistory();
        FileBackedTasksManager taskManager2 =
                FileBackedTasksManager.load(file);

        assertEquals(saveEpic,taskManager2.getAllEpics().get(0),"Неверная загрузка из файла");
        assertEquals(saveHistory,taskManager2.getHistory(),"Неверная загрузка из файла");
    }

    @Test
    void loadFromFileWithEmptyList() {
        taskManager.removeAllTasks();

        FileBackedTasksManager tasksManager = FileBackedTasksManager.load(file);
        List<Task> tasks = tasksManager.getAllTasks();

        assertNotNull(tasks, "Список задач не возвращается");
        assertEquals(0, tasks.size(), "Не верное количество задач");
        assertTrue(tasks.isEmpty(), "Список не пустой");
    }

    @Test
    void loadFromFileHistoryList(){
        List<Task> history = taskManager.getHistory();

        taskManager.save();
        FileBackedTasksManager backedTasksManager =
                FileBackedTasksManager.load(file);

        assertEquals(history, backedTasksManager.getHistory(), "Неверная загрузка из файла");
    }

    @Test
    void loadFromFileWithEmptyHistoryList(){
        taskManager.removeAllTasks();
        List<Task> history = taskManager.getHistory();

        taskManager.save();
        FileBackedTasksManager backedTasksManager =
                FileBackedTasksManager.load(file);

        assertEquals(history, backedTasksManager.getHistory(), "Неверная загрузка из пустого файла");
    }
}