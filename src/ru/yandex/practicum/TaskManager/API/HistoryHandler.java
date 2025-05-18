package ru.yandex.practicum.TaskManager.API;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.practicum.TaskManager.Model.Task;
import ru.yandex.practicum.TaskManager.Service.FileBackedTaskManager;
import ru.yandex.practicum.TaskManager.Service.InMemoryHistoryManager;

import java.io.IOException;
import java.util.List;

public class HistoryHandler extends BaseHttpHandler implements HttpHandler {
    private final FileBackedTaskManager taskManager;

    public HistoryHandler(FileBackedTaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String requestMethod = exchange.getRequestMethod();
        if ("GET".equals(requestMethod)) {
            List<Task> history = taskManager.getHistory();
            String response = gson.toJson(history);
            sendText(exchange, response);
        } else {
            exchange.sendResponseHeaders(500, -1);
            exchange.getResponseBody().close();
        }
    }
}
