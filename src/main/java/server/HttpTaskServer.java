package server;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import managers.Manager;
import managers.TaskManager;
import model.Epic;
import model.Subtask;
import model.Task;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.InetSocketAddress;
import java.time.LocalDateTime;
import java.util.regex.Pattern;

import static java.nio.charset.StandardCharsets.UTF_8;

public class HttpTaskServer {
    private TaskManager taskManager;
    private HttpServer httpServer;
    private static final int PORT = 8080;

    public HttpTaskServer() throws IOException {
        taskManager = Manager.getFileBackedTasksManager();

        httpServer = HttpServer.create();
        httpServer.bind(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks", new tasksHandler());
        httpServer.start();
    }

    public void stop(int delay){
        httpServer.stop(delay);
    }

    protected String readText(HttpExchange h) throws IOException {
        return new String(h.getRequestBody().readAllBytes(), UTF_8);
    }

    protected void sendText(HttpExchange h, String text,int statusCode) throws IOException {
        byte[] response = text.getBytes(UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json");
        h.sendResponseHeaders(statusCode, response.length);
        h.getResponseBody().write(response);
    }

    private int parseId(String idFromString) {
        try {
            return Integer.parseInt(idFromString);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    class tasksHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            Gson gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                    .create();
                String path = httpExchange.getRequestURI().getPath();
                String uri = httpExchange.getRequestURI().toString();
                String method = httpExchange.getRequestMethod();

                    switch (method) {
                        case "GET":
                            String response;
                            int statusCode;
                            if (path.equals("/tasks")) {
                                response = gson.toJson(taskManager.getPrioritizedTasks());
                                statusCode = 200;
                                sendText(httpExchange, response, statusCode);
                                return;
                            }
                            if (path.equals("/tasks/history")) {
                                response = gson.toJson(taskManager.getHistory());
                                statusCode = 200;
                                sendText(httpExchange, response, statusCode);
                                return;
                            }
                            if (path.equals("/tasks/task")) {
                                response = gson.toJson(taskManager.getAllTasks());
                                statusCode = 200;
                                sendText(httpExchange, response, statusCode);
                                return;
                            }
                            if (path.equals("/tasks/epic")) {
                                response = gson.toJson(taskManager.getAllEpics());
                                statusCode = 200;
                                sendText(httpExchange, response, statusCode);
                                return;
                            }
                            if (path.equals("/tasks/subtask")) {
                                response = gson.toJson(taskManager.getAllSubtasks());
                                statusCode = 200;
                                sendText(httpExchange, response, statusCode);
                                return;
                            }
                            if (Pattern.matches("^/tasks/task/\\?id=\\d+$", uri)) {
                                String idFromString = uri.replaceFirst("/tasks/task/\\?id=", "");
                                int id = parseId(idFromString);
                                response = gson.toJson(taskManager.getTask(id));
                                if (response != null) {
                                    statusCode = 200;
                                    sendText(httpExchange, response, statusCode);
                                } else {
                                    statusCode = 400;
                                    sendText(httpExchange, "Задача не найдена. Введите корректный запрос.",
                                            statusCode);
                                }
                                return;
                            }
                            if (Pattern.matches("^/tasks/subtask/\\?id=\\d+$", uri)) {
                                String idFromString = uri.replaceFirst("/tasks/subtask/\\?id=", "");
                                int id = parseId(idFromString);
                                response = gson.toJson(taskManager.getSubtask(id));
                                if (response != null) {
                                    statusCode = 200;
                                    sendText(httpExchange, response, statusCode);
                                } else {
                                    statusCode = 400;
                                    sendText(httpExchange, "Подзадача не найдена. Введите корректный запрос.",
                                            statusCode);
                                }
                                return;
                            }
                            if (Pattern.matches("^/tasks/epic/\\?id=\\d+$", uri)) {
                                String idFromString = uri.replaceFirst("/tasks/epic/\\?id=", "");
                                int id = parseId(idFromString);
                                response = gson.toJson(taskManager.getEpic(id));
                                if (response != null) {
                                    statusCode = 200;
                                    sendText(httpExchange, response, statusCode);
                                } else {
                                    statusCode = 400;
                                    sendText(httpExchange, "Эпик не найден. Введите корректный запрос.",
                                            statusCode);
                                }
                                return;
                            }
                            break;

                        case "POST":
                            String body = readText(httpExchange);

                            if (path.equals("/tasks/task")) {
                                Type taskType = new TypeToken<Task>() {
                                }.getType();
                                Task task = gson.fromJson(body, taskType);

                                if (task.getId() != 0) {
                                    taskManager.updateTask(task);
                                } else {
                                    taskManager.createTask(task);
                                }

                                statusCode = 200;
                                sendText(httpExchange, "Задача создана.", statusCode);
                            }
                            if (path.equals("/tasks/epic")) {
                                Type taskType = new TypeToken<Epic>() {
                                }.getType();
                                Epic epic = gson.fromJson(body, taskType);

                                if (epic.getId() != 0) {
                                    taskManager.updateEpic(epic);
                                } else {
                                    taskManager.createEpic(epic);
                                }

                                statusCode = 200;
                                sendText(httpExchange, "Эпик создан.", statusCode);
                            }
                            if (path.equals("/tasks/subtask")) {
                                Type taskType = new TypeToken<Subtask>() {
                                }.getType();
                                Subtask subtask = gson.fromJson(body, taskType);

                                if (subtask.getId() != 0) {
                                    taskManager.updateSubtask(subtask);
                                } else {
                                    taskManager.createSubtask(subtask);
                                }

                                statusCode = 200;
                                sendText(httpExchange, "Подзадача создана.", statusCode);
                            }
                            break;

                        case "DELETE":
                            if (Pattern.matches("^/tasks/task/\\?id=\\d+$", uri)) {
                                String idFromString = uri.replaceFirst("/tasks/task/\\?id=", "");
                                int id = parseId(idFromString);
                                if (taskManager.getTask(id) != null) {
                                    taskManager.removeTask(id);
                                    statusCode = 200;
                                    response = "Задача удалена.";
                                    sendText(httpExchange, response, statusCode);
                                } else {
                                    response = "Задача не найдена. Введите корректный запрос.";
                                    statusCode = 400;
                                    sendText(httpExchange, response, statusCode);
                                }
                                return;
                            }
                            if (Pattern.matches("^/tasks/subtask/\\?id=\\d+$", uri)) {
                                String idFromString = uri.replaceFirst("/tasks/subtask/\\?id=", "");
                                int id = parseId(idFromString);
                                if (taskManager.getSubtask(id) != null) {
                                    taskManager.removeSubtask(id);
                                    statusCode = 200;
                                    response = "Подзадача удалена.";
                                    sendText(httpExchange, response, statusCode);
                                } else {
                                    response = "Подзадача не найдена. Введите корректный запрос.";
                                    statusCode = 400;
                                    sendText(httpExchange, response, statusCode);
                                }
                                return;
                            }
                            if (Pattern.matches("^/tasks/epic/\\?id=\\d+$", uri)) {
                                String idFromString = uri.replaceFirst("/tasks/epic/\\?id=", "");
                                int id = parseId(idFromString);
                                if (taskManager.getEpic(id) != null) {
                                    taskManager.removeEpic(id);
                                    statusCode = 200;
                                    response = "Эпик удален.";
                                    sendText(httpExchange, response, statusCode);
                                } else {
                                    response = "Эпик не найден. Введите корректный запрос.";
                                    statusCode = 400;
                                    sendText(httpExchange, response, statusCode);
                                }
                                return;
                            }
                            break;
                        default:
                            response = "Некорректный метод!";
                            System.out.println(response);
                            httpExchange.sendResponseHeaders(405, 0);
                    }
        }
    }

    public static void main(String[] args) throws IOException {
        new HttpTaskServer();
    }
}

