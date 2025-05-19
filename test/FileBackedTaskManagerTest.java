import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;
import ru.yandex.practicum.TaskManager.Model.*;
import ru.yandex.practicum.TaskManager.Service.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

class FileBackedTaskManagerTest {
    String pathStr = "src/Test.csv";
    private final Path tempPath = Paths.get(Paths.get(pathStr).toUri());

    @BeforeEach
    public void setUp() throws IOException {
        if (!Files.exists(tempPath)) {
            Files.createFile(tempPath); // Создаем временный файл перед каждым тестом
        }
    }

    @AfterEach
    public void cleanUp() throws IOException {
        if (Files.exists(tempPath)) { // Удаление временного файла после каждого теста
            Files.delete(tempPath);
        }
    }

    @Test
    void testEmptyFileLoad() {
        FileBackedTaskManager loadedManager2 = FileBackedTaskManager.loadFromFile(tempPath);
        assertEquals(Collections.emptyList(), loadedManager2.getAllTasks(), "Должен вернуть пустой список задач.");
        assertEquals(Collections.emptyList(), loadedManager2.getAllEpic(), "Должен вернуть пустой список эпиков.");
        assertEquals(Collections.emptyList(), loadedManager2.getAllSubTasks(), "Должен вернуть пустой список субзадач.");
    }

    @Test
    void testCreateAndSaveSingleTask() {
        FileBackedTaskManager loadedManager1 = FileBackedTaskManager.loadFromFile(tempPath);
        Task task = new Task("Задача №1", "Описание первой задачи.", 1, TaskStatus.NEW, Duration.ofMinutes(60), LocalDateTime.of(2025, 5, 2, 10, 0));
        loadedManager1.createNewTask(task);

        FileBackedTaskManager loadedManager2 = FileBackedTaskManager.loadFromFile(tempPath);

        List<Task> allTasks = loadedManager2.getAllTasks();
        assertEquals(1, allTasks.size(), "Ожидается одна задача после загрузки.");
        assertEquals(task.getTaskName(), allTasks.get(0).getTaskName(), "Название задачи должно совпадать.");
        assertEquals(task.getDescription(), allTasks.get(0).getDescription(), "Описание задачи должно совпадать.");
    }

    @Test
    void testMultipleTasksCreationAndLoading() {
        FileBackedTaskManager loadedManager1 = FileBackedTaskManager.loadFromFile(tempPath);
        Task task1 = new Task("Задача №1", "Описание первой задачи.", 1, TaskStatus.IN_PROGRESS, Duration.ofMinutes(60), LocalDateTime.of(2025, 5, 2, 10, 0));
        Task task2 = new Task("Задача №2", "Описание второй задачи.", 2, TaskStatus.DONE, Duration.ofMinutes(60), LocalDateTime.of(2025, 5, 3, 10, 0));
        loadedManager1.createNewTask(task1);
        loadedManager1.createNewTask(task2);

        FileBackedTaskManager loadedManager2 = FileBackedTaskManager.loadFromFile(tempPath);

        List<Task> allTasks = loadedManager2.getAllTasks();
        assertEquals(2, allTasks.size(), "Ожидаются две задачи после загрузки.");
        assertEquals(Arrays.asList(task1.getTaskName(), task2.getTaskName()), Arrays.asList(allTasks.get(0).getTaskName(), allTasks.get(1).getTaskName()));
    }

    @Test
    void testDeleteTaskAndSave() {
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempPath);
        Task task = new Task("Задача №1", "Описание первой задачи.", 1, TaskStatus.NEW, Duration.ofMinutes(60), LocalDateTime.of(2025, 5, 2, 10, 0));
        loadedManager.createNewTask(task);
        loadedManager.deleteTaskById(task.getTaskId());

        FileBackedTaskManager loadedManager2 = FileBackedTaskManager.loadFromFile(tempPath);

        List<Task> allTasks = loadedManager2.getAllTasks();
        assertEquals(0, allTasks.size(), "Список задач должен остаться пустым после удаления и перезагрузки.");
    }

    @Test
    void testUpdateTaskAndSave() {
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempPath);
        Task task = new Task("Задача №1", "Описание первой задачи.", 1, TaskStatus.NEW, Duration.ofMinutes(60), LocalDateTime.of(2025, 5, 2, 10, 0));
        loadedManager.createNewTask(task);
        task.setStatus(TaskStatus.DONE);
        loadedManager.updateTask(task);

        FileBackedTaskManager loadedManager2 = FileBackedTaskManager.loadFromFile(tempPath);

        List<Task> allTasks = loadedManager2.getAllTasks();
        assertEquals(1, allTasks.size(), "Ожидается одна задача после обновления и загрузки.");
        assertEquals(TaskStatus.DONE, allTasks.get(0).getStatus(), "Статус задачи должен быть обновлен.");
    }

    @Test
    void testAddSubTaskAndSave() {
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempPath);
        Epic epic = new Epic("Эпик №1", "Описание первого эпика.", 1);
        loadedManager.createNewEpic(epic);
        SubTask subTask = new SubTask("Субзадача №1", "Описание первой субзадачи.", 2, TaskStatus.NEW, Duration.ofMinutes(60), LocalDateTime.of(2025, 5, 2, 10, 0), epic.getTaskId());
        loadedManager.createNewSubTask(subTask);

        FileBackedTaskManager loadedManager2 = FileBackedTaskManager.loadFromFile(tempPath);

        List<Epic> epics = loadedManager2.getAllEpic();
        List<SubTask> subTasks = loadedManager2.getAllSubTasks();
        assertEquals(1, epics.size(), "Ожидается один эпик после загрузки.");
        assertEquals(1, subTasks.size(), "Ожидается одна субзадача после загрузки.");
        assertEquals(subTask.getTaskName(), subTasks.get(0).getTaskName(), "Название субзадачи должно совпасть.");
    }

    @Test
    void testRemoveSubTaskAndSave() {
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempPath);
        Epic epic = new Epic("Эпик №1", "Описание первого эпика.", 1);
        loadedManager.createNewEpic(epic);
        SubTask subTask = new SubTask("Субзадача №1", "Описание первой субзадачи.", 2, TaskStatus.NEW, Duration.ofMinutes(60), LocalDateTime.of(2025, 5, 2, 10, 0), epic.getTaskId());
        loadedManager.createNewSubTask(subTask);
        loadedManager.deleteSubTaskById(subTask.getTaskId());

        FileBackedTaskManager loadedManager2 = FileBackedTaskManager.loadFromFile(tempPath);

        List<SubTask> subTasks = loadedManager2.getAllSubTasks();
        assertEquals(0, subTasks.size(), "Список субзадач должен остаться пустым после удаления и перезагрузки.");
    }

    @Test
    void testMultipleEpicsCreationAndSaving() {
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempPath);
        Epic epic1 = new Epic("Эпик №1", "Описание первого эпика.", 1);
        Epic epic2 = new Epic("Эпик №2", "Описание второго эпика.", 2);
        loadedManager.createNewEpic(epic1);
        loadedManager.createNewEpic(epic2);

        FileBackedTaskManager loadedManager2 = FileBackedTaskManager.loadFromFile(tempPath);

        List<Epic> epics = loadedManager2.getAllEpic();
        assertEquals(2, epics.size(), "Ожидаются два эпика после загрузки.");
        assertEquals(Arrays.asList(epic1.getTaskName(), epic2.getTaskName()),
                Arrays.asList(epics.get(0).getTaskName(), epics.get(1).getTaskName()));
    }

    @Test
    void testCleanUpAllTasksAndSave() {
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempPath);
        Task task1 = new Task("Задача №1", "Описание первой задачи.", 1, TaskStatus.NEW, Duration.ofMinutes(60), LocalDateTime.of(2025, 5, 2, 10, 0));
        Task task2 = new Task("Задача №2", "Описание второй задачи.", 2, TaskStatus.DONE, Duration.ofMinutes(60), LocalDateTime.of(2025, 5, 3, 10, 0));
        loadedManager.createNewTask(task1);
        loadedManager.createNewTask(task2);
        loadedManager.tasksCleaning();

        FileBackedTaskManager loadedManager2 = FileBackedTaskManager.loadFromFile(tempPath);

        List<Task> allTasks = loadedManager2.getAllTasks();
        assertEquals(0, allTasks.size(), "Ожидается пустой список задач после очистки и перезагрузки.");
    }

    @Test
    void testFullStateRestoration() {
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempPath);
        Task task1 = new Task("Задача №1", "Описание первой задачи.", 1, TaskStatus.NEW, Duration.ofMinutes(60), LocalDateTime.of(2025, 5, 2, 10, 0));
        Epic epic1 = new Epic("Эпик №1", "Описание первого эпика.", 2);
        SubTask subTask1 = new SubTask("Субзадача №1", "Описание первой субзадачи.", 3, TaskStatus.DONE, Duration.ofMinutes(60), LocalDateTime.of(2025, 5, 3, 10, 0), epic1.getTaskId());
        loadedManager.createNewTask(task1);
        loadedManager.createNewEpic(epic1);
        loadedManager.createNewSubTask(subTask1);

        FileBackedTaskManager loadedManager2 = FileBackedTaskManager.loadFromFile(tempPath);

        List<Task> allTasks = loadedManager2.getAllTasks();
        List<Epic> epics = loadedManager2.getAllEpic();
        List<SubTask> subTasks = loadedManager2.getAllSubTasks();

        assertEquals(1, allTasks.size(), "Ожидается одна задача после полной загрузки.");
        assertEquals(1, epics.size(), "Ожидается один эпик после полной загрузки.");
        assertEquals(1, subTasks.size(), "Ожидается одна субзадача после полной загрузки.");
    }
}