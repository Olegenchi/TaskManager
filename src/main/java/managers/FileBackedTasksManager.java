package managers;

import exception.ManagerSaveException;
import model.*;

import java.io.*;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.List;

public class FileBackedTasksManager extends InMemoryTaskManager {
    private File storageFile = new File("src/main/resources/tasks.csv");

    public FileBackedTasksManager() {
        super();
    }

    public FileBackedTasksManager(File file) {
        this.storageFile = file;
    }

    public void save() {
        StringBuilder sb = new StringBuilder();
        sb.append("id,type,name,status,description,epic,startTime,duration\n");
        for (Task task : tasks.values()) {
            sb.append(toString(task)).append("\n");
        }
        for (Epic epic : epics.values()) {
            sb.append(toString(epic)).append("\n");
        }
        for (Subtask subtask : subtasks.values()) {
            sb.append(toString(subtask)).append("\n");
        }
        sb.append("\n");
        sb.append(historyToString(historyManager));
        sb.setLength(sb.length() - 1);
        try (FileWriter fileWriter = new FileWriter(storageFile)) {
            fileWriter.write(sb.toString());
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения в файл.");
        }
    }

    public static FileBackedTasksManager load(File file) {
        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager(file);
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            String str = bufferedReader.readLine();
            while (bufferedReader.ready()) {
                str = bufferedReader.readLine();
                if (!str.isEmpty() || !str.isBlank()) {
                    Task task = fromString(str);
                    if (task instanceof Epic) {
                        fileBackedTasksManager.epics.put(task.getId(), (Epic) task);
                    } else if (task instanceof Subtask) {
                        fileBackedTasksManager.subtasks.put(task.getId(), (Subtask) task);
                    } else {
                        fileBackedTasksManager.tasks.put(task.getId(), task);
                    }
                } else {
                    fileBackedTasksManager.historyFromString(bufferedReader.readLine());
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Не удалось восстановить данные из файла.");
        }
        return fileBackedTasksManager;
    }

    private static String toString(Task task) {
        String taskToString;
        String id = Integer.toString(task.getId());
        String name = task.getName();
        String status = String.valueOf(task.getStatus());
        String description = task.getDescription();
        String startTime = String.valueOf(task.getStartTime());
        String duration = String.valueOf(task.getDuration());

        if (task instanceof Epic) {
            taskToString = String.join(", ", id, String.valueOf(TaskType.EPIC),
                    name, description, status, startTime, duration);
        } else if (task instanceof Subtask) {
            String epicId = Integer.toString(((Subtask) task).getEpicId());
            taskToString = String.join(", ", id, String.valueOf(TaskType.SUBTASK),
                    name, description, status, startTime, duration, epicId);
        } else {
            taskToString = String.join(", ", id, String.valueOf(TaskType.TASK),
                    name, description, status, startTime, duration);
        }
        return taskToString;
    }

    private static Task fromString(String value) {
        Task task;
        String[] taskFromString = value.split(", ");
        int id = Integer.parseInt(taskFromString[0]);
        TaskType type = TaskType.valueOf(taskFromString[1]);
        String name = taskFromString[2];
        String description = taskFromString[3];
        Status status = Status.valueOf(taskFromString[4]);
        LocalDateTime startTime = LocalDateTime.parse(taskFromString[5]);
        Long duration = Long.valueOf(taskFromString[6]);

        switch (type) {
            case TASK:
                task = new Task(id, type, name, description, status, startTime, duration);
                task.setId(id);
                break;
            case EPIC:
                task = new Epic(id, type, name, description, status, startTime, duration);
                task.setId(id);
                break;
            case SUBTASK:
                int epicId = Integer.parseInt(taskFromString[7]);
                task = new Subtask(id, type, name, description, status, startTime, duration, epicId);
                task.setId(id);
                break;
            default:
                throw new IllegalArgumentException("Неизвестный тип задачи.");
        }
        return task;
    }

    public String historyToString(HistoryManager manager) {
        List<Task> historyListOfTasks = manager.getTaskHistoryList();
        StringBuilder stringBuilder = new StringBuilder();

        for (Task stringHistory : historyListOfTasks) {
            stringBuilder.append(stringHistory.getId()).append(",");
        }
        return stringBuilder.toString();
    }

    public void historyFromString(String value) {
        String[] tasksFromHistory = value.split(",");

        for (String s : tasksFromHistory) {
            int tasksId = Integer.parseInt(s);
            if (tasks.containsKey(tasksId)) {
                historyManager.addTaskHistoryList(tasks.get(tasksId));
            } else if (epics.containsKey(tasksId)) {
                historyManager.addTaskHistoryList(epics.get(tasksId));
            } else {
                historyManager.addTaskHistoryList(subtasks.get(tasksId));
            }
        }
        historyManager.getTaskHistoryList();
    }

    @Override
    public void createTask(Task task) {
        super.createTask(task);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public Task getTask(int id) {
        Task task = super.getTask(id);
        if (task != null) {
            save();
        }
        return task;
    }

    @Override
    public void removeTask(int id) {
        super.removeTask(id);
        save();
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }

    @Override
    public void createSubtask(Subtask subtask) {
        super.createSubtask(subtask);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public Subtask getSubtask(int id) {
        Subtask subtask = super.getSubtask(id);
        if (subtask != null) {
            save();
        }
        return subtask;
    }

    @Override
    public void removeSubtask(int id) {
        super.removeSubtask(id);
        save();
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = super.getEpic(id);
        if (epic != null) {
            save();
        }
        return epic;
    }

    @Override
    public void removeEpic(int id) {
        super.removeEpic(id);
        save();
    }

    @Override
    public List<Task> getHistory() {
        return super.getHistory();
    }

    public static void main(String[] args) {
        TaskManager fileBackedTasksManagerNew = new FileBackedTasksManager();

        Task task1 = new Task(1, TaskType.TASK,"Перевести вещи в другую квартиру", "Переезд",
                Status.NEW, LocalDateTime.of(2022,8,10,12,0), 120L);
        fileBackedTasksManagerNew.createTask(task1);
        Task task2 = new Task(2, TaskType.TASK,"Сдать в срок (провалено)", "Сдать проект по java",
                Status.NEW, LocalDateTime.of(2022,7,11,12,0), 60L);
        fileBackedTasksManagerNew.createTask(task2);
        Epic epic1 = new Epic("Пн", "Тренировка в тренажерном зале",
                Status.NEW, LocalDateTime.of(2022,8,12,12,30), 30L);
        fileBackedTasksManagerNew.createEpic(epic1);
        Subtask subTask1 = new Subtask(4, TaskType.SUBTASK,"Пн", "Тренировка груди",
                Status.NEW, LocalDateTime.of(2022,10,10,12,30), 30L,
                epic1.getId());
        fileBackedTasksManagerNew.createSubtask(subTask1);
        Subtask subTask2 = new Subtask(5, TaskType.SUBTASK,"Ср", "Тренировка ног",
                Status.NEW, LocalDateTime.of(2022,8,12,12,30), 30L,
                epic1.getId());
        fileBackedTasksManagerNew.createSubtask(subTask2);
        Subtask subTask3 = new Subtask(6, TaskType.SUBTASK, "Пт", "Тренировка спины",
                Status.NEW, LocalDateTime.of(2022,9,14,12,30), 60L,
                epic1.getId());
        fileBackedTasksManagerNew.createSubtask(subTask3);

        fileBackedTasksManagerNew.getSubtask(subTask1.getId());
        fileBackedTasksManagerNew.getEpic(epic1.getId());
        fileBackedTasksManagerNew.getTask(task1.getId());
        fileBackedTasksManagerNew.getTask(task2.getId());
        fileBackedTasksManagerNew.getSubtask(subTask2.getId());
        fileBackedTasksManagerNew.getSubtask(subTask3.getId());

        TaskManager fileBackedTasksManager = load(new File("src/main/resources/tasks.csv"));
        System.out.println("Просмотренные задачи:");
        for (Task task : fileBackedTasksManager.getAllTasks()) {
            System.out.println(task.getId() + " " + task.getName());
        }
        System.out.println("Просмотренные эпики:");
        for (Epic epic : fileBackedTasksManager.getAllEpics()) {
            System.out.println(epic.getId() + " " + epic.getName());
        }
        System.out.println("Просмотренные сабтаски:");
        for (Subtask subtask : fileBackedTasksManager.getAllSubtasks()) {
            System.out.println(subtask.getId() + " " + subtask.getName());
        }
        System.out.println("История просмотра:");
        for (Task task : fileBackedTasksManager.getHistory()) {
            System.out.println(task.getId() + " " + task.getName());
        }
    }
}

