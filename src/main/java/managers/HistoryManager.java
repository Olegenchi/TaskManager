package managers;

import model.Task;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public interface HistoryManager {

    List<Task> getTaskHistoryList();

    void addTaskHistoryList(Task task);

    void removeTaskHistoryList(int id);
}