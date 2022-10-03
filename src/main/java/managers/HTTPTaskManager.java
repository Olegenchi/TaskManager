package managers;

import com.google.gson.reflect.TypeToken;
import server.KVTaskClient;
import com.google.gson.*;
import model.Epic;
import model.Subtask;
import model.Task;
import server.LocalDateTimeAdapter;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

public class HTTPTaskManager extends FileBackedTasksManager {
    private static String url;
    private final KVTaskClient client;
    private final Gson gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    public HTTPTaskManager(String url) throws URISyntaxException, IOException, InterruptedException {
        super(new File("src/main/resources/tasks.csv"));
        this.client = new KVTaskClient(url);
    }

    public HTTPTaskManager() throws IOException, InterruptedException, URISyntaxException {
        HTTPTaskManager.url = "http://localhost:8080";
        this.client = new KVTaskClient(url);
    }

    @Override
    public void save() {
        List<Task> taskList = getAllTasks();
        List<Epic> epicList = getAllEpics();
        List<Subtask> subtaskList = getAllSubtasks();

        if (!isNull(taskList)) {
            client.put("task", gson.toJson(taskList, new TypeToken<ArrayList<Task>>() {
            }.getType()));
        }
        if (!isNull(epicList)) {
            client.put("epic", gson.toJson(epicList, new TypeToken<ArrayList<Epic>>() {
            }.getType()));
        }
        if (!isNull(subtaskList)) {
            client.put("subtask", gson.toJson(subtaskList, new TypeToken<ArrayList<Subtask>>() {
            }.getType()));
        }
        if (!getHistory().isEmpty()) {
            client.put("history", gson.toJson(historyToString(Manager.getDefaultHistory())));
        }
    }

    public void load() {
        List<Task> loadTasks = gson.fromJson(client.load("task"), new TypeToken<ArrayList<Task>>() {
        }.getType());
        for (Task task : loadTasks) {
            tasks.put(task.getId(), task);
        }
        List<Epic> loadEpics = gson.fromJson(client.load("epic"), new TypeToken<ArrayList<Epic>>() {
        }.getType());
        for (Epic epic : loadEpics) {
            epics.put(epic.getId(), epic);
        }
        List<Subtask> loadSubtasks = gson.fromJson(client.load("subtask"), new TypeToken<ArrayList<Subtask>>() {
        }.getType());
        for (Subtask subtask : loadSubtasks) {
            subtasks.put(subtask.getId(), subtask);
        }
        String[] historyFromServer = client.load("history").trim().split(",");
        for (String number : historyFromServer) {
            if (!number.isBlank()) {
                getTask(Integer.parseInt(number));
            }
        }
        for (Task task : getAllTasks()) {
            getPrioritizedTasks().add(task);
        }
        for (Subtask subtask : getAllSubtasks()) {
            getPrioritizedTasks().add(subtask);
        }
    }
}

