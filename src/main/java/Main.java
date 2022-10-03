import managers.TaskManager;
import managers.Manager;

import model.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException, URISyntaxException, InterruptedException {

        TaskManager taskManager = Manager.getDefault();

        Task task1 = new Task(1, TaskType.TASK,"Перевести вещи в другую квартиру", "Переезд",
                Status.NEW, LocalDateTime.of(2022,8,10,12,0), 120L);
        taskManager.createTask(task1);
        Task task2 = new Task(2, TaskType.TASK,"Сдать в срок (провалено)", "Сдать проект по java",
                Status.NEW, LocalDateTime.of(2022,7,11,12,0), 60L);
        taskManager.createTask(task2);

        Epic epic = new Epic(3,"Пн", "Тренировка в тренажерном зале", Status.NEW);
        taskManager.createEpic(epic);
        Subtask subTask1 = new Subtask(4, TaskType.SUBTASK,"Пн", "Тренировка груди",
                Status.NEW, LocalDateTime.of(2022,7,10,12,30), 30L,
                epic.getId());
        taskManager.createSubtask(subTask1);
        Subtask subTask2 = new Subtask(5, TaskType.SUBTASK,"Ср", "Тренировка ног",
                Status.NEW, LocalDateTime.of(2022,8,12,12,30), 30L,
                epic.getId());
        taskManager.createSubtask(subTask2);
        Subtask subTask3 = new Subtask(6, TaskType.SUBTASK, "Пт", "Тренировка спины",
                Status.NEW, LocalDateTime.of(2022,9,14,12,30), 60L,
                epic.getId());
        taskManager.createSubtask(subTask3);
        System.out.println(epic);

        Epic epic2 = new Epic(7,"Для чего-то", "Что-то сделать", Status.NEW);
        taskManager.createEpic(epic2);

        Task task2update = new Task(1,"Новое описание", "Обновление задачи", Status.IN_PROGRESS);
        taskManager.updateTask(task2update);

        System.out.println();
        taskManager.getTask(1);
        taskManager.getTask(2);
        taskManager.getEpic(3);
        taskManager.getSubtask(4);
        taskManager.getSubtask(5);
        List<Task> history = Manager.getDefaultHistory().getTaskHistoryList();
        System.out.println(history);

        taskManager.removeTask(task1.getId());
        taskManager.removeEpic(epic.getId());
        System.out.println(history);
    }
}