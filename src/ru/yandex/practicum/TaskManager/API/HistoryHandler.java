package ru.yandex.practicum.TaskManager.API;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.practicum.TaskManager.Model.Task;
import ru.yandex.practicum.TaskManager.Service.TaskManager;

import java.io.IOException;
import java.util.List;

public class HistoryHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;

    public HistoryHandler(TaskManager taskManager) {
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
