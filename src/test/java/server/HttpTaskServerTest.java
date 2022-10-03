package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import managers.FileBackedTasksManager;
import managers.TaskManager;
import model.*;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


public class HttpTaskServerTest {
    protected static Gson gson;
    protected HttpTaskServer httpTaskServer;
    protected static HttpClient client;
    protected TaskManager taskManager = new FileBackedTasksManager();
    protected Task task;
    protected Epic epic;
    protected Subtask subtask;

    @BeforeAll
    static void setUpGsonAndManager() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new TypeAdapter<LocalDateTime>() {

            @Override
            public void write(JsonWriter jsonWriter, LocalDateTime localDateTime) throws IOException {
                if (localDateTime != null) {
                    jsonWriter.value(localDateTime.format(dateTimeFormatter));
                } else {
                    jsonWriter.value("");
                }
            }

            @Override
            public LocalDateTime read(JsonReader jsonReader) throws IOException {
                String localDateTime = jsonReader.nextString();
                if (!localDateTime.isBlank()) {
                    return LocalDateTime.parse(localDateTime, dateTimeFormatter);
                } else {
                    return null;
                }
            }
        }).create();

        client = HttpClient.newHttpClient();
    }

    @BeforeEach
    void init() throws IOException {
        httpTaskServer = new HttpTaskServer();
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

    @AfterEach
    void stop(){
        httpTaskServer.stop(0);
    }

    @Test
    void createTaskTest() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/task");
        String json = gson.toJson(task);

        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
    }

    @Test
    void createSubTaskTest() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/subtask");
        String json = gson.toJson(subtask);

        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
    }

    @Test
    void createEpicTest() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/epic");
        String json = gson.toJson(epic);

        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
    }

    @Test
    void getTaskTest() throws IOException, InterruptedException {
        HttpClient client1 = HttpClient.newHttpClient();
        URI url1 = URI.create("http://localhost:8080/tasks/task");
        String json = gson.toJson(task);

        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request1 = HttpRequest.newBuilder().uri(url1).POST(body).build();
        HttpResponse<String> response = client1.send(request1, HttpResponse.BodyHandlers.ofString());

        HttpClient client2 = HttpClient.newHttpClient();
        URI url2 = URI.create("http://localhost:8080/tasks/task/?id=1");

        HttpRequest request2 = HttpRequest.newBuilder().uri(url2).GET().build();
        HttpResponse<String> response2 = client2.send(request2, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response2.statusCode());

        Type taskType = new TypeToken<Task>() {}.getType();
        Task task = gson.fromJson(response2.body(), taskType);

        assertNotNull(task, "Задача не возвращается");
        assertEquals(task, this.task, "Задачи не совпадают");
    }

    @Test
    void getSubTaskTest() throws IOException, InterruptedException {
        HttpClient client2 = HttpClient.newHttpClient();
        URI url2 = URI.create("http://localhost:8080/tasks/subtask/?id=3");

        HttpRequest request2 = HttpRequest.newBuilder().uri(url2).GET().build();
        HttpResponse<String> response2 = client2.send(request2, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response2.statusCode());

        Type taskType = new TypeToken<Subtask>() {}.getType();
        Task task = gson.fromJson(response2.body(), taskType);

        assertNotNull(task, "Задача не возвращается");
        assertEquals(task, this.subtask, "Задачи не совпадают");
    }

    @Test
    void getEpicTest() throws IOException, InterruptedException {
        HttpClient client1 = HttpClient.newHttpClient();
        URI url1 = URI.create("http://localhost:8080/tasks/epic");
        String json = gson.toJson(epic);

        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request1 = HttpRequest.newBuilder().uri(url1).POST(body).build();
        HttpResponse<String> response = client1.send(request1, HttpResponse.BodyHandlers.ofString());

        HttpClient client2 = HttpClient.newHttpClient();
        URI url2 = URI.create("http://localhost:8080/tasks/epic/?id=2");

        HttpRequest request2 = HttpRequest.newBuilder().uri(url2).GET().build();
        HttpResponse<String> response2 = client2.send(request2, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response2.statusCode());

        Type taskType = new TypeToken<Epic>() {}.getType();
        Task task = gson.fromJson(response2.body(), taskType);

        assertNotNull(task, "Задача не возвращается");
        assertEquals(task, this.epic, "Задачи не совпадают");
    }

}