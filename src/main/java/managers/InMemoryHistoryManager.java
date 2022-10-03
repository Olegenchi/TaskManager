package managers;

import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class InMemoryHistoryManager implements HistoryManager {

    private final Map<Integer, Node> historyTaskView = new HashMap<>();
    private Node tail;
    private Node head;

    private Node getTail() {
        return tail;
    }

    private void setTail(Node tail) {
        this.tail = tail;
    }

    private Node getHead() {
        return head;
    }

    private void setHead(Node head) {
        this.head = head;
    }

    @Override
    public void addTaskHistoryList(Task task) {
        int taskId = task.getId();
        if (historyTaskView.containsKey(taskId)) {
            Node node = historyTaskView.get(taskId);
            removeNode(node);
        }
        Node lastNode = linkLast(task);
        historyTaskView.put(taskId, lastNode);
        tail = lastNode;
    }

    @Override
    public List<Task> getTaskHistoryList() {
        if (head == null) {
            return new ArrayList<>();
        }
        ArrayList<Task> getTasks = new ArrayList<>(historyTaskView.size());
        getTasks.add(head.getTask());
        Node nextNode = head.getNextNode();
        while (nextNode != null) {
            getTasks.add(nextNode.getTask());
            nextNode = nextNode.getNextNode();
        }
        return getTasks;
    }

    @Override
    public void removeTaskHistoryList(int id) {
        if (historyTaskView.containsKey(id)) {
            removeNode(historyTaskView.get(id));
        }
    }

    private Node linkLast(Task task) {
        Node node;
        if (tail == null) {
            node = new Node(null, null, task);
            head = node;
        } else {
            node = new Node(tail, null, task);
            tail.setNextNode(node);
        }
        return node;
    }

    private void removeNode(Node node) {
        Node previousNode = node.getPreviousNode();
        Node nextNode = node.getNextNode();
        historyTaskView.remove(node.getTask().getId());
        if ((previousNode == null) && (nextNode == null)) {
            head = null;
            tail = null;
        } else if (previousNode == null) {
            nextNode.setPreviousNode(null);
            head = nextNode;
        } else if (nextNode == null) {
            previousNode.setNextNode(null);
            tail = previousNode;
        } else {
            previousNode.setNextNode(nextNode);
            nextNode.setPreviousNode(previousNode);
        }
    }
}
