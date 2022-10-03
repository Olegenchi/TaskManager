package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import exception.ManagerSaveException;

import java.io.IOException;
import java.net.BindException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;

public class KVTaskClient {
    private final String serverURL;
    private final String apiToken;
    private final HttpClient client;
    private final Gson gson;

    public KVTaskClient(String serverURL) throws URISyntaxException, IOException, InterruptedException {
        this.serverURL = serverURL;
        try {
            new KVServer().start();
        } catch (BindException e) {
            System.out.println("Server already start!");
        }
        client = HttpClient.newHttpClient();
        HttpRequest httpRequest = HttpRequest.newBuilder().uri(new URI(serverURL + "/register/")).GET().build();
        HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new RuntimeException("Не удается обработать запрос.");
        }
        this.apiToken = response.body();
        gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
    }

    public void put(String key, String value) {
        try {
            final HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(serverURL + "/save/" + key + "?API_TOKEN=" + apiToken))
                    .version(HttpClient.Version.HTTP_1_1)
                    .POST(HttpRequest.BodyPublishers.ofString(value))
                    .build();
            final HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());
            if (response.statusCode() != 200) {
                throw new ManagerSaveException("Не удается сохранить данные.");
            }
        } catch (IOException | InterruptedException e) {
            throw new ManagerSaveException("Обработка запроса прервана.");
        }
    }

    public String load(String key) {
        try {
            final HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(serverURL + "/load/" + key + "?API_TOKEN=" + apiToken))
                    .version(HttpClient.Version.HTTP_1_1)
                    .header("Accept", "text/json")
                    .GET()
                    .build();
            final HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new RuntimeException("Не удается обработать запрос.");
            }
            return response.body();
        } catch (IOException | InterruptedException e) {
            throw new ManagerSaveException("Обработка запроса прервана.");
        }
    }
}

