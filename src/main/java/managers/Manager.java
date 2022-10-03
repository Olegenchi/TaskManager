package managers;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

public class Manager {

    public static TaskManager getDefault() throws IOException, URISyntaxException, InterruptedException {
        return new HTTPTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static FileBackedTasksManager getFileBackedTasksManager() {
        return FileBackedTasksManager.load(new File("src/main/resources/tasks.csv"));
    }
}
