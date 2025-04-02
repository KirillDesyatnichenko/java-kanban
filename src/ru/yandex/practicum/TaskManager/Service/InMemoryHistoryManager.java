package ru.yandex.practicum.TaskManager.Service;

import ru.yandex.practicum.TaskManager.Model.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {

    private Node<Task> head = null;
    private Node<Task> tail = null;
    private Map<Integer, Node<Task>> historyMap = new HashMap<>();

    private List<Task> getTasks() {
        List<Task> taskList = new ArrayList<>();
        Node<Task> nodeIterator = head;

        if (head == null) {
            return taskList;
        }

        while (nodeIterator != null) {
            taskList.add(nodeIterator.task);
            nodeIterator = nodeIterator.next;
        }

        return taskList;
    }

    private void linkLast(Task task) {
        Node<Task> newNode;
        Node<Task> oldTail = tail;
        newNode = new Node<>(oldTail, task, null);
        tail = newNode;
        if (oldTail == null) {
            head = newNode;
        } else {
            oldTail.next = newNode;
        }
        historyMap.put(task.getTaskId(), newNode);
    }

     private void removeNode(Node<Task> node) {
        Node<Task> prevNode = node.prev;
        Node<Task> nextNode = node.next;

        if (prevNode == null && nextNode == null) {
            head = null;
            tail = null;
        } else if (prevNode == null) {
            head = nextNode;
            nextNode.prev = null;
        } else if (nextNode == null) {
            tail = prevNode;
            prevNode.next = null;
        } else {
            prevNode.next = nextNode;
            nextNode.prev = prevNode;
        }
    }

    @Override
    public void remove(int id) {
        if (historyMap.containsKey(id)) {
            removeNode(historyMap.get(id));
            historyMap.remove(id);
        }
    }

    @Override
    public void add(Task task) {
        remove(task.getTaskId());
        linkLast(task);
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }
}