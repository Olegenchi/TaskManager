package managers;

import exception.TaskIntersectionException;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {

    protected Map<Integer, Task> tasks;
    protected Map<Integer, Subtask> subtasks;
    protected Map<Integer, Epic> epics;
    protected TreeSet<Task> prioritizedTasks;
    private int id = 0;

    protected HistoryManager historyManager = Manager.getDefaultHistory();

    public InMemoryTaskManager() {
        tasks = new HashMap<>();
        subtasks = new HashMap<>();
        epics = new HashMap<>();
        prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));
    }

    @Override
    public void createTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            return;
        }
        if (!validationOfTasks(task)) {
            throw new TaskIntersectionException("Задача " + task.getName() +
                    " пересекается по времени с другими задачами. Укажите другое время.");
        }
        task.setId(generateNextId());
        tasks.put(task.getId(), task);
        prioritizedTasks.add(task);
    }

    @Override
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            return;
        }
        if (!validationOfTasks(task)) {
            throw new TaskIntersectionException("Задача " + task.getName() +
                    " пересекается по времени с другими задачами. Укажите другое время.");
        }
        tasks.put(task.getId(), task);
    }

    @Override
    public Task getTask(int id) {
        Task task = tasks.get(id);
        historyManager.addTaskHistoryList(task);
        return task;
    }

    @Override
    public void removeTask(int id) {
        Task task = tasks.get(id);
        tasks.remove(id);
        historyManager.removeTaskHistoryList(id);
        prioritizedTasks.remove(task);
    }

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public void removeAllTasks() {
        tasks.clear();
    }

    @Override
    public void createSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            return;
        }
        if (!validationOfTasks(subtask)) {
            throw new TaskIntersectionException("Подзадача " + subtask.getName() +
                    " пересекается по времени с другими задачами. Укажите другое время.");
        }
        subtask.setId(generateNextId());
        subtasks.put(subtask.getId(), subtask);
        prioritizedTasks.add(subtask);
        epics.get(subtask.getEpicId()).addSubtask(subtask);
        updateEpic(getEpic(subtask.getEpicId()));
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        Subtask foundTask = subtasks.get(subtask.getId());
        if (!Objects.equals(subtask.getEpicId(), foundTask.getEpicId())) {
            Epic epicToRemoveSubtask = getEpic(foundTask.getEpicId());
            Epic epicToAddSubtask = getEpic(subtask.getEpicId());
            epicToRemoveSubtask.removeSubtask(foundTask);
            epicToAddSubtask.addSubtask(subtask);
            updateEpic(epicToRemoveSubtask);
            updateEpic(epicToAddSubtask);
        }
        if (!validationOfTasks(subtask)) {
            throw new TaskIntersectionException("Подзадача " + subtask.getName() +
                    " пересекается по времени с другими задачами. Укажите другое время.");
        }
        subtasks.put(subtask.getId(), subtask);
        prioritizedTasks.add(subtask);
        updateEpic(getEpic(subtask.getEpicId()));
        updateEpicDurationAndStartTime(subtask.getEpicId());
    }

    @Override
    public Subtask getSubtask(int id) {
        Subtask subtask = subtasks.get(id);
        historyManager.addTaskHistoryList(subtask);
        return subtask;
    }

    @Override
    public void removeSubtask(int id) {
        Subtask subtask = subtasks.get(id);
        Epic epic = getEpic(subtask.getEpicId());
        epic.removeSubtask(subtask);
        updateEpic(epic);
        subtasks.remove(id);
        prioritizedTasks.remove(subtask);
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void createEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            return;
        }
        epic.setId(generateNextId());
        epic.setStatus(computeEpicStatus(epic));
        epics.put(epic.getId(), epic);
    }

    @Override
    public void updateEpic(Epic epic) {
        epic.setStatus(computeEpicStatus(epic));
        epics.put(epic.getId(), epic);
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = epics.get(id);
        historyManager.addTaskHistoryList(epic);
        return epic;
    }

    @Override
    public List<Subtask> getEpicSubtasks(Epic epic) {
        return epic.getSubtasks();
    }

    @Override
    public void removeEpic(int id) {
        List<Subtask> subtasks = getEpic(id).getSubtasks();
        for (Subtask s : subtasks) {
            this.subtasks.remove(s.getId());
            this.prioritizedTasks.remove(s);
        }
        epics.remove(id);
    }

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getTaskHistoryList();
    }

    private Status computeEpicStatus(Epic epic) {
        List<Subtask> allSubtasks = epic.getSubtasks();
        if (allSubtasks.isEmpty()) {
            return Status.NEW;
        }
        int newStatus = 0;
        int doneStatus = 0;

        for (Subtask subtask1 : allSubtasks) {
            if (subtask1.getStatus() == Status.NEW) {
                newStatus++;
            } else if (subtask1.getStatus() == Status.DONE) {
                doneStatus++;
            }
        }
        if (newStatus == allSubtasks.size()) {
            return Status.NEW;
        }
        if (doneStatus == allSubtasks.size()) {
            return Status.DONE;
        }
        return Status.IN_PROGRESS;
    }

    private void updateEpicDurationAndStartTime(int epicId) {
        Epic epic = epics.get(epicId);
        List<Subtask> subtasksList = epic.getSubtasks();
        if (subtasksList.isEmpty()) {
            epic.setDuration(0L);
            return;
        }

        LocalDateTime startEpic = LocalDateTime.MAX;
        LocalDateTime endEpic = LocalDateTime.MIN;
        Long durationEpic = 0L;

        for (Subtask list : subtasksList) {
            Subtask subtask = subtasks.get(list);
            LocalDateTime startTime = subtask.getStartTime();
            LocalDateTime endTime = subtask.getEndTime();
            if (startTime.isBefore(startEpic)) {
                startEpic = startTime;
            }
            if (endTime.isAfter(endEpic)) {
                endEpic = endTime;
            }
            durationEpic += subtask.getDuration();
        }

        epic.setStartTime(startEpic);
        epic.setEndTime(endEpic);
        epic.setDuration(durationEpic);
    }

    @Override
    public Set<Task> getPrioritizedTasks() {
        return prioritizedTasks;
    }

    public boolean validationOfTasks(Task task) {
        if (prioritizedTasks.isEmpty()) {
            return true;
        } else {
            Task lower = prioritizedTasks.lower(task);
            Task higher = prioritizedTasks.higher(task);
            if (prioritizedTasks.contains(task)){
                return false;
            }
            if (lower == null) {
                return task.getEndTime().isBefore(prioritizedTasks.first().getStartTime());
            } else if (higher == null) {
                return task.getStartTime().isAfter(prioritizedTasks.last().getEndTime());
            }
            return lower.getEndTime().isBefore(task.getStartTime()) &&
                    higher.getStartTime().isAfter(task.getEndTime());
        }
    }

    private int generateNextId() {
        return ++id;
    }
}
