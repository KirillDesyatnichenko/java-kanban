package ru.yandex.practicum.TaskManager.API;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.practicum.TaskManager.Model.*;
import ru.yandex.practicum.TaskManager.Service.NotFoundException;
import ru.yandex.practicum.TaskManager.Service.TaskManager;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;

    public TaskHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String requestMethod = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        try {
            if ("GET".equals(requestMethod)) {
                if (exchange.getRequestURI().getPath().equals("/tasks")) {
                    List<Task> tasks = taskManager.getAllTasks();
                    String response = gson.toJson(tasks);
                    sendText(exchange, response);
                } else {
                    String id = exchange.getRequestURI().getPath().substring("/tasks/".length());
                    int taskId = Integer.parseInt(id);
                    Task task = taskManager.getTaskById(taskId);
                    String response = gson.toJson(task);
                    sendText(exchange, response);
                }
            } else if ("POST".equals(requestMethod)) {
                if (exchange.getRequestURI().getPath().equals("/tasks")) {
                    InputStreamReader isr = new InputStreamReader(exchange.getRequestBody());
                    Task task = gson.fromJson(isr, Task.class);
                    Task createdTask = taskManager.createNewTask(task);
                    String response = gson.toJson(createdTask);
                    sendText(exchange, response);
                } else if (path.matches("/tasks/[0-9]+")) {
                    InputStreamReader isr = new InputStreamReader(exchange.getRequestBody());
                    Task updateTask = gson.fromJson(isr, Task.class);
                    taskManager.updateTask(updateTask);
                    exchange.sendResponseHeaders(201, -1);
                    exchange.getResponseBody().close();
                }
            } else if ("DELETE".equals(requestMethod)) {
                String id = exchange.getRequestURI().getPath().substring("/tasks/".length());
                int taskId = Integer.parseInt(id);
                taskManager.deleteTaskById(taskId);
                exchange.sendResponseHeaders(201, -1);
                exchange.getResponseBody().close();
            } else {
                exchange.sendResponseHeaders(500, -1);
                exchange.getResponseBody().close();
            }

        } catch (NotFoundException e) {
            sendNotFound(exchange);
        } catch (IllegalArgumentException e) {
            sendHasInteractions(exchange);
        }
    }
}