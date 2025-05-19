import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import ru.yandex.practicum.TaskManager.API.DurationAdapter;
import ru.yandex.practicum.TaskManager.API.HttpTaskServer;
import ru.yandex.practicum.TaskManager.API.LocalDateTimeAdapter;
import ru.yandex.practicum.TaskManager.Model.Epic;
import ru.yandex.practicum.TaskManager.Model.SubTask;
import ru.yandex.practicum.TaskManager.Model.Task;
import ru.yandex.practicum.TaskManager.Model.TaskStatus;
import ru.yandex.practicum.TaskManager.Service.FileBackedTaskManager;
import ru.yandex.practicum.TaskManager.Service.Managers;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class HttpTaskServerTest {
    private static final FileBackedTaskManager taskManager = Managers.getDefaultFileManager();
    HttpTaskServer taskServer = new HttpTaskServer(taskManager);
    Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .create();

    @BeforeEach
    public void setUp() {
        taskManager.tasksCleaning();
        taskManager.subTasksCleaning();
        taskManager.epicCleaning();
        taskServer.start();
    }

    @AfterEach
    public void shutDown() throws Exception {
        taskServer.stop();
    }


    @Test
    public void testCreateTask() throws Exception {
        Task task = new Task("Новая задача", "Описание новой задачи",
                TaskStatus.NEW, Duration.ofMinutes(60),
                LocalDateTime.of(2025, 5, 2, 10, 0));

        String taskJson = gson.toJson(task);
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        Task newTask = new Task("Новая задача", "Описание новой задачи", 1,
                TaskStatus.NEW, Duration.ofMinutes(60),
                LocalDateTime.of(2025, 5, 2, 10, 0));

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        List<Task> tasks = taskManager.getAllTasks();
        assertTrue(tasks.contains(newTask));

        List<Task> prioritized = taskManager.getPrioritizedTasks();
        assertTrue(prioritized.contains(newTask));

        URI getUri = URI.create("http://localhost:8080/tasks/1");
        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(getUri)
                .header("Content-Type", "application/json")
                .GET()
                .build();

        HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, getResponse.statusCode());

        Task receivedTask = gson.fromJson(getResponse.body(), Task.class);
        assertEquals(receivedTask.getTaskName(), newTask.getTaskName());
        assertEquals(receivedTask.getDescription(), newTask.getDescription());
        assertEquals(receivedTask.getStatus(), newTask.getStatus());
        assertEquals(receivedTask.getDuration(), newTask.getDuration());
        assertEquals(receivedTask.getStartTime(), newTask.getStartTime());
    }

    @Test
    public void testCreateEpicAndSubTask() throws Exception {
        Epic epic = new Epic("Эпик №1", "Описание первого эпика.", 0);
        SubTask subTask = new SubTask("Субзадача №1", "Описание первой субзадачи.", 0
                , TaskStatus.NEW, Duration.ofMinutes(60),
                LocalDateTime.of(2025, 5, 2, 10, 0), 1);

        String epicJson = gson.toJson(epic);
        String subTaskJson = gson.toJson(subTask);
        HttpClient client = HttpClient.newHttpClient();

        URI epicsUri = URI.create("http://localhost:8080/epics");
        HttpRequest createEpicRequest = HttpRequest.newBuilder()
                .uri(epicsUri)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();

        URI subTasksUri = URI.create("http://localhost:8080/subtasks");
        HttpRequest createSubtaskRequest = HttpRequest.newBuilder()
                .uri(subTasksUri)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(subTaskJson))
                .build();

        HttpResponse<String> responseEpic = client.send(createEpicRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, responseEpic.statusCode());
        HttpResponse<String> responseSubtask = client.send(createSubtaskRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, responseSubtask.statusCode());

        Epic newEpic = new Epic("Эпик №1", "Описание первого эпика.", 1);
        SubTask newSubTask = new SubTask("Субзадача №1", "Описание первой субзадачи.", 2
                , TaskStatus.NEW, Duration.ofMinutes(60),
                LocalDateTime.of(2025, 5, 2, 10, 0), 1);

        List<Epic> epics = taskManager.getAllEpic();
        assertTrue(epics.contains(newEpic));

        List<SubTask> subTasks = taskManager.getAllSubTasks();
        assertTrue(subTasks.contains(newSubTask));

        List<SubTask> epicSubTasks = taskManager.getSubTasksByEpicId(1);
        assertTrue(epicSubTasks.contains(newSubTask));

        URI getUri = URI.create("http://localhost:8080/subtasks/2");
        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(getUri)
                .header("Content-Type", "application/json")
                .GET()
                .build();

        HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, getResponse.statusCode());

        Task receivedSubTask = gson.fromJson(getResponse.body(), SubTask.class);
        assertEquals(receivedSubTask.getTaskName(), newSubTask.getTaskName());
        assertEquals(receivedSubTask.getDescription(), newSubTask.getDescription());
        assertEquals(receivedSubTask.getStatus(), newSubTask.getStatus());
        assertEquals(receivedSubTask.getDuration(), newSubTask.getDuration());
        assertEquals(receivedSubTask.getStartTime(), newSubTask.getStartTime());
    }

    @Test
    public void testUpdateTaskAndGetHistoryTask() throws Exception {
        Task task = new Task("Новая задача", "Описание новой задачи",
                TaskStatus.NEW, Duration.ofMinutes(60),
                LocalDateTime.of(2025, 5, 2, 10, 0));

        String taskJson = gson.toJson(task);
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Task updateTask = new Task("Новое название", "Новое описание", 1,
                TaskStatus.IN_PROGRESS, Duration.ofMinutes(120),
                LocalDateTime.of(2025, 5, 2, 10, 0));

        String updateTaskJson = gson.toJson(updateTask);
        URI updateUri = URI.create("http://localhost:8080/tasks/1");
        HttpRequest updateRequest = HttpRequest.newBuilder()
                .uri(updateUri)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(updateTaskJson))
                .build();

        HttpResponse<String> updateResponse = client.send(updateRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, updateResponse.statusCode());

        Task updatedTask = taskManager.getTaskById(1);
        assertEquals(updatedTask.getTaskName(), updateTask.getTaskName());
        assertEquals(updatedTask.getDescription(), updateTask.getDescription());
        assertEquals(updatedTask.getStatus(), updateTask.getStatus());
        assertEquals(updatedTask.getDuration(), updateTask.getDuration());
        assertEquals(updatedTask.getStartTime(), updateTask.getStartTime());

        URI historyUri = URI.create("http://localhost:8080/history");
        HttpRequest historyRequest = HttpRequest.newBuilder()
                .uri(historyUri)
                .header("Content-Type", "application/json")
                .GET()
                .build();

        HttpResponse<String> historyResponse = client.send(historyRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, historyResponse.statusCode());

        Type listType = new TypeToken<List<Task>>(){}.getType();
        List<Task> actualTasks = gson.fromJson(historyResponse.body(), listType);
        assertEquals(1, actualTasks.size());
    }

    @Test
    public void testDeleteTask() throws Exception {
        Task task = new Task("Новая задача", "Описание новой задачи",
                TaskStatus.NEW, Duration.ofMinutes(60),
                LocalDateTime.of(2025, 5, 2, 10, 0));

        String taskJson = gson.toJson(task);
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        Task newTask = new Task("Новая задача", "Описание новой задачи", 1,
                TaskStatus.NEW, Duration.ofMinutes(60),
                LocalDateTime.of(2025, 5, 2, 10, 0));

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        List<Task> tasks = taskManager.getAllTasks();
        assertTrue(tasks.contains(newTask));

        URI deleteUri = URI.create("http://localhost:8080/tasks/1");
        HttpRequest deleteRequest = HttpRequest.newBuilder()
                .uri(deleteUri)
                .DELETE()
                .build();

        HttpResponse<Void> deleteResponse = client.send(deleteRequest, HttpResponse.BodyHandlers.discarding());
        assertEquals(201, deleteResponse.statusCode());

        List<Task> tasksAfterDelete = taskManager.getAllTasks();
        assertEquals(0, tasksAfterDelete.size());
    }

    @Test
    public void testDeleteEpicAndSubtasks() throws Exception {
        Epic epic = new Epic("Эпик №1", "Описание первого эпика.", 0);
        SubTask subTask = new SubTask("Субзадача №1", "Описание первой субзадачи.", 0
                , TaskStatus.NEW, Duration.ofMinutes(60),
                LocalDateTime.of(2025, 5, 2, 10, 0), 1);

        String epicJson = gson.toJson(epic);
        String subTaskJson = gson.toJson(subTask);
        HttpClient client = HttpClient.newHttpClient();

        URI epicsUri = URI.create("http://localhost:8080/epics");
        HttpRequest createEpicRequest = HttpRequest.newBuilder()
                .uri(epicsUri)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();

        URI subTasksUri = URI.create("http://localhost:8080/subtasks");
        HttpRequest createSubtaskRequest = HttpRequest.newBuilder()
                .uri(subTasksUri)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(subTaskJson))
                .build();

        HttpResponse<String> responseEpic = client.send(createEpicRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, responseEpic.statusCode());
        HttpResponse<String> responseSubtask = client.send(createSubtaskRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, responseSubtask.statusCode());

        Epic newEpic = new Epic("Эпик №1", "Описание первого эпика.", 1);
        SubTask newSubTask = new SubTask("Субзадача №1", "Описание первой субзадачи.", 2
                , TaskStatus.NEW, Duration.ofMinutes(60),
                LocalDateTime.of(2025, 5, 2, 10, 0), 1);

        List<Epic> epics = taskManager.getAllEpic();
        assertTrue(epics.contains(newEpic));

        List<SubTask> subTasks = taskManager.getAllSubTasks();
        assertTrue(subTasks.contains(newSubTask));

        URI deleteSubtaskUri = URI.create("http://localhost:8080/subtasks/2");
        HttpRequest deleteSubtaskRequest = HttpRequest.newBuilder()
                .uri(deleteSubtaskUri)
                .DELETE()
                .build();

        HttpResponse<Void> deleteSubtaskResponse = client.send(deleteSubtaskRequest, HttpResponse.BodyHandlers.discarding());
        assertEquals(201, deleteSubtaskResponse.statusCode());

        List<SubTask> subTasksAfterDelete = taskManager.getAllSubTasks();
        assertEquals(0, subTasksAfterDelete.size());

        URI deleteEpicUri = URI.create("http://localhost:8080/epics/1");
        HttpRequest deleteEpicRequest = HttpRequest.newBuilder()
                .uri(deleteEpicUri)
                .DELETE()
                .build();

        HttpResponse<Void> deleteEpicResponse = client.send(deleteEpicRequest, HttpResponse.BodyHandlers.discarding());
        assertEquals(201, deleteEpicResponse.statusCode());

        List<Epic> epicsAfterDelete = taskManager.getAllEpic();
        assertEquals(0, epicsAfterDelete.size());
    }
}
