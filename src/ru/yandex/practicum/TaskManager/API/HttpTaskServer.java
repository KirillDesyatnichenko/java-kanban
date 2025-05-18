package ru.yandex.practicum.TaskManager.API;

import com.sun.net.httpserver.HttpServer;
import ru.yandex.practicum.TaskManager.Service.FileBackedTaskManager;
import ru.yandex.practicum.TaskManager.Service.InMemoryHistoryManager;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private static HttpServer server;
    private static FileBackedTaskManager taskManager;

    public HttpTaskServer(FileBackedTaskManager taskManager, InMemoryHistoryManager historyTaskManager) {
        HttpTaskServer.taskManager = taskManager;
    }



    public static void main(String[] args) {
        server.start();

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