package ru.yandex.practicum.TaskManager.API;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.practicum.TaskManager.Model.*;
import ru.yandex.practicum.TaskManager.Service.FileBackedTaskManager;
import ru.yandex.practicum.TaskManager.Service.NotFoundException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class EpicHandler extends BaseHttpHandler implements HttpHandler {
    private final FileBackedTaskManager taskManager;

    public EpicHandler(FileBackedTaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String requestMethod = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        try {
            if ("GET".equals(requestMethod)) {
                if (path.equals("/epics")) {
                    List<Epic> epics = taskManager.getAllEpic();
                    String response = gson.toJson(epics);
                    sendText(exchange, response);

                } else if (path.startsWith("/epics/") && !path.contains("subtasks")) {
                    String idStr = path.substring("/epics/".length());
                    int epicId = Integer.parseInt(idStr);
                    Epic epic = taskManager.getEpicById(epicId);
                    String response = gson.toJson(epic);
                    sendText(exchange, response);

                } else if (path.matches("/epics/[0-9]+/subtasks")) {
                    String[] parts = path.split("/");
                    int epicId = Integer.parseInt(parts[2]);
                    List<SubTask> subtasks = taskManager.getSubTasksByEpicId(epicId);
                    String response = gson.toJson(subtasks);
                    sendText(exchange, response);
                }
            } else if ("POST".equals(requestMethod)) {
                if (exchange.getRequestURI().getPath().equals("/epics")) {
                    InputStreamReader reader = new InputStreamReader(exchange.getRequestBody());
                    Epic epic = gson.fromJson(reader, Epic.class);
                    Epic createdEpic = taskManager.createNewEpic(epic);
                    String response = gson.toJson(createdEpic);
                    sendText(exchange, response);
                } else if (path.matches("/epics/[0-9]+")) {
                    InputStreamReader isr = new InputStreamReader(exchange.getRequestBody());
                    Epic updateEpic = gson.fromJson(isr, Epic.class);
                    taskManager.updateEpic(updateEpic);
                    exchange.sendResponseHeaders(201, -1);
                    exchange.getResponseBody().close();
                }
            } else if ("DELETE".equals(requestMethod)) {
                String idStr = path.substring("/epics/".length());
                int epicId = Integer.parseInt(idStr);
                taskManager.deleteEpicById(epicId);
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