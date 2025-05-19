package ru.yandex.practicum.TaskManager.API;

import com.sun.net.httpserver.HttpServer;
import ru.yandex.practicum.TaskManager.Service.Managers;
import ru.yandex.practicum.TaskManager.Service.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private HttpServer server;
    private final TaskManager taskManager;

    public HttpTaskServer(TaskManager taskManager) {
        this.taskManager = taskManager;
    }


    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefaultFileManager();
        HttpTaskServer httpTaskServer = new HttpTaskServer(taskManager);
        httpTaskServer.start();
    }

    public void start() {
        try {
            server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);

            server.createContext("/tasks", new TaskHandler(taskManager));
            server.createContext("/subtasks", new SubtaskHandler(taskManager));
            server.createContext("/epics", new EpicHandler(taskManager));
            server.createContext("/history", new HistoryHandler(taskManager));
            server.createContext("/prioritized", new PrioritizedHandler(taskManager));

            server.start();
            System.out.println("Сервер запущен. Порт " + PORT);
        } catch (IOException e) {
            System.err.println("Ошибка запуска сервера: " + e.getMessage());
        }
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
            System.out.println("Сервер завершил работу.");
        }
    }
}