import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.TaskManager.Model.Epic;
import ru.yandex.practicum.TaskManager.Model.SubTask;
import ru.yandex.practicum.TaskManager.Model.Task;
import ru.yandex.practicum.TaskManager.Model.TaskStatus;
import ru.yandex.practicum.TaskManager.Service.InMemoryTaskManager;
import ru.yandex.practicum.TaskManager.Service.Managers;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    @Test
    void testAddAndGetTasksFromHistory() {
        InMemoryTaskManager manager = Managers.getDefault();
        Task task1 = manager.createNewTask(new Task("Task 1", "T", 1, TaskStatus.NEW, Duration.ofMinutes(60), LocalDateTime.of(2025, 5, 2, 10, 0)));
        Epic epic1 = manager.createNewEpic(new Epic("Epic 1", "E", 2));
        SubTask epic1SubTask1 = manager.createNewSubTask(new SubTask("Subtask 1", "S1", 3, TaskStatus.NEW, Duration.ofMinutes(60), LocalDateTime.of(2025, 5, 3, 10, 0), 2));

        manager.getTaskById(1);
        List<Task> history = manager.getHistory();
        assertNotNull(history, "Список истории не инициализирован");
        assertEquals(task1, history.get(0), "В историю добавлена ошибочная задача");
        assertEquals(1, history.size(), "Ошибочное количество задач");

        manager.getSubTaskById(3);
        manager.getEpicById(2);
        List<Task> history1 = manager.getHistory();
        assertTrue(history1.contains(task1), "Не сохранена предидущая версия задачи и её данных");
        assertEquals(epic1SubTask1, history1.get(1), "В историю добавлена ошибочная задача");
        assertEquals(epic1, history1.get(2), "В историю добавлена ошибочная задача");
        assertEquals(3, history1.size(), "Ошибочное количество задач");

        for (int i = 1; i < 5; i++) {
            manager.getTaskById(1);
            manager.getSubTaskById(3);
            manager.getEpicById(2);
        }
        List<Task> history2 = manager.getHistory();
        Assertions.assertEquals(3, history2.size(), "удаление задач со старым ID корректно");
        Assertions.assertEquals(epic1, history2.get(history2.size() - 1), "В историю некорректно добавлена последняя открытая задача");
    }
}