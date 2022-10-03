package managers;

import model.Task;

public class Node {
    private Node previousNode;
    private Node nextNode;
    private Task task;

    public Node(Node previousNode, Node nextNode, Task task) {
        this.previousNode = previousNode;
        this.nextNode = nextNode;
        this.task = task;
    }

    public Node getPreviousNode() {
        return previousNode;
    }

    public void setPreviousNode(Node previousNode) {
        this.previousNode = previousNode;
    }

    public Node getNextNode() {
        return nextNode;
    }

    public void setNextNode(Node nextNode) {
        this.nextNode = nextNode;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }
}