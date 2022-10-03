package managers;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import server.KVServer;

import java.io.IOException;
import java.net.URISyntaxException;

public class HTTPTaskManagerTest extends TaskManagerTest<HTTPTaskManager> {
    private static KVServer kvServer;

    @BeforeAll
    static void beforeAll() throws IOException {
        kvServer = new KVServer();
        kvServer.start();
    }

    @BeforeEach
    void httpTaskManagerTestSetUp() throws IOException, URISyntaxException, InterruptedException {
        taskManager = new HTTPTaskManager();
        taskManagerTestSetUp();
    }

    @AfterAll
    static void afterAll() {
        kvServer.stop();
    }
}