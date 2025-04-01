package ru.yandex.practicum.TaskManager.Test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.TaskManager.Model.Epic;
import ru.yandex.practicum.TaskManager.Model.SubTask;
import ru.yandex.practicum.TaskManager.Model.Task;
import ru.yandex.practicum.TaskManager.Model.TaskStatus;
import ru.yandex.practicum.TaskManager.Service.InMemoryTaskManager;
import ru.yandex.practicum.TaskManager.Service.Managers;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    InMemoryTaskManager manager;

    @BeforeEach
    void starterPackage() {
        manager = Managers.getDefault();
    }

    @Test
    void testTaskEqualityBasedOnId() {
        Task task1 = manager.createNewTask(new Task("Task 1", "T", 1, TaskStatus.NEW));
        Task task2 = manager.getTaskById(1);
        Task task3 = new Task("Task 1", "T", 1, TaskStatus.NEW);
        assertEquals(task1, task2, "Одинаковые по ID задачи не равны");
        assertEquals(task2, task3, "Одинаковые по ID задачи не равны");
    }

    @Test
    void testEpicEqualityBasedOnId() {
        Epic epic1 = manager.createNewEpic(new Epic("Epic 1", "E", 1, TaskStatus.NEW));
        Epic epic2 = manager.getEpicById(1);
        Epic epic3 = new Epic("Epic 1", "E", 1, TaskStatus.NEW);
        assertEquals(epic1, epic2, "Одинаковые по ID эпики не равны");
        assertEquals(epic2, epic3, "Одинаковые по ID эпики не равны");
    }

    @Test
    void testSubTaskEqualityBasedOnId() {
        Epic epic1 = manager.createNewEpic(new Epic("Epic 1", "E", 1, TaskStatus.NEW));
        SubTask subTask1 = manager.createNewSubTask(new SubTask("Subtask 1", "S1", 2, TaskStatus.NEW, 1));
        SubTask subTask2 = manager.getSubTaskById(2);
        SubTask subTask3 = new SubTask("Subtask 1", "S1", 2, TaskStatus.NEW, 1);
        assertEquals(subTask1,subTask2, "Одинаковые по ID подзадачи не равны");
        assertEquals(subTask2,subTask3, "Одинаковые по ID подзадачи не равны");
    }

    @Test
    void testTaskFieldsAreUnchangedAfterAddingToManager() {
        Task task1 = manager.createNewTask(new Task("Task 1", "T", 1, TaskStatus.NEW));
        Task testTask = manager.getTaskById(1);
        assertEquals("Task 1", testTask.getTaskName(),"Имя задачи ошибочно");
        assertEquals("T", testTask.getDescription(),"Описание задачи ошибочно");
        assertEquals(1, testTask.getTaskId(),"Id задачи ошибочен");
        assertEquals(TaskStatus.NEW, testTask.getStatus(),"Статус задачи ошибочен");
    }

    @Test
    void testEpicFieldsAreUnchangedAfterAddingToManager() {
        Epic epic1 = manager.createNewEpic(new Epic("Epic 1", "E", 1, TaskStatus.NEW));
        Epic testEpic = manager.getEpicById(1);
        assertEquals("Epic 1", testEpic.getTaskName(),"Имя задачи ошибочно");
        assertEquals("E", testEpic.getDescription(),"Описание задачи ошибочно");
        assertEquals(1, testEpic.getTaskId(),"Id задачи ошибочен");
        assertEquals(TaskStatus.NEW, testEpic.getStatus(),"Статус задачи ошибочен");
    }

    @Test
    void testSubtaskFieldsAreUnchangedAfterAddingToManager() {
        Epic epic1 = manager.createNewEpic(new Epic("Epic 1", "E", 1, TaskStatus.NEW));
        SubTask subTask1 = manager.createNewSubTask(new SubTask("Subtask 1", "S1", 2, TaskStatus.NEW, 1));
        SubTask testSubTask = manager.getSubTaskById(2);
        assertEquals("Subtask 1", testSubTask.getTaskName(),"Имя задачи ошибочно");
        assertEquals("S1", testSubTask.getDescription(),"Описание задачи ошибочно");
        assertEquals(2, testSubTask.getTaskId(),"Id задачи ошибочен");
        assertEquals(TaskStatus.NEW, testSubTask.getStatus(),"Статус задачи ошибочен");
        assertEquals(1, testSubTask.getEpicId(),"Id эпика ошибочен");
    }

    @Test
    void testGetMapAddAndDeleteTask() {
        Task task1 = manager.createNewTask(new Task("Task 1", "T", 1, TaskStatus.NEW));
        Task task2 = manager.createNewTask(new Task("Task 2", "T2", 2, TaskStatus.IN_PROGRESS));

        ArrayList<Task> testTasks = manager.getAllTasks();

        assertEquals(2, testTasks.size(), "Размер списка задач ошибочен");
        assertTrue(testTasks.contains(task1), "В списке отсутствует задача 1");
        assertTrue(testTasks.contains(task2), "В списке отсутствует задача 2");

        Task updatedTask = new Task("NewTask 1", "Новое описание", 1, TaskStatus.DONE);
        manager.updateTask(updatedTask);
        Task actualTask = manager.getTaskById(1);
        assertEquals("NewTask 1", actualTask.getTaskName(), "Имя новой задачи ошибочно");
        assertEquals("Новое описание", actualTask.getDescription(), "Описание новой задачи ошибочно");
        assertEquals(TaskStatus.DONE, actualTask.getStatus(), "Статус новой задачи ошибочен");

        manager.deleteTaskById(1);
        assertFalse(manager.containsTask(1),"Задача не удалена");
    }

    @Test
    void testGetMapAddAndDeleteEpicAndSubtaskTheirInteraction() {
        Epic epic1 = manager.createNewEpic(new Epic("Epic 1", "E", 1, TaskStatus.NEW));
        SubTask subTask1 = manager.createNewSubTask(new SubTask("Subtask 1", "S1", 2, TaskStatus.NEW, 1));
        SubTask subTask2 = manager.createNewSubTask(new SubTask("Subtask 2", "S2", 3, TaskStatus.NEW, 1));
        Epic epic2 = manager.createNewEpic(new Epic("Epic 1", "E", 4, TaskStatus.NEW));
        SubTask subTask3 = manager.createNewSubTask(new SubTask("Subtask 3", "S3", 5, TaskStatus.NEW, 4));
        SubTask subTask4 = manager.createNewSubTask(new SubTask("Subtask 4", "S2", 6, TaskStatus.NEW, 4));

        ArrayList<Epic> testEpics = manager.getAllEpic();
        ArrayList<SubTask> testSubTasks = manager.getAllSubTasks();

        assertEquals(2, testEpics.size(), "Размер списка эпиков ошибочен");
        assertTrue(testEpics.contains(epic1), "В списке отсутствует эпик 1");
        assertTrue(testEpics.contains(epic2), "В списке отсутствует эпик 2");

        assertEquals(4, testSubTasks.size(), "Размер списка подзадач ошибочен");
        assertTrue(testSubTasks.contains(subTask1), "В списке отсутствует подзадача 1");
        assertTrue(testSubTasks.contains(subTask2), "В списке отсутствует подзадача 2");
        assertTrue(testSubTasks.contains(subTask3), "В списке отсутствует подзадача 3");
        assertTrue(testSubTasks.contains(subTask4), "В списке отсутствует подзадача 4");

        ArrayList<SubTask> epic1SubTasks = epic1.getSubTasks();
        assertTrue(epic1SubTasks.contains(subTask1), "В эпике 1 отсутствует подзадача 1");
        assertTrue(epic1SubTasks.contains(subTask2), "В эпике 1 отсутствует  подзадача 2");

        ArrayList<SubTask> epic2SubTasks = epic2.getSubTasks();
        assertTrue(epic2SubTasks.contains(subTask3), "В эпике 2 отсутствует  подзадача 1");
        assertTrue(epic2SubTasks.contains(subTask4), "В эпике 2 отсутствует подзадача 2");


        Epic testUpdateEpic = new Epic("NewEpic 1", "Новое описание", 1, TaskStatus.NEW);
        manager.updateEpic(testUpdateEpic);
        Epic actualEpic = manager.getEpicById(1);
        assertEquals("NewEpic 1", actualEpic.getTaskName(), "Имя нового эпика ошибочно");
        assertEquals("Новое описание", actualEpic.getDescription(), "Описание нового эпика ошибочно");
        assertEquals(TaskStatus.NEW, actualEpic.getStatus(), "Статус нового эпика ошибочен");

        SubTask testUpdateSubTask1 = new SubTask("NewSubtask 1", "Новое описание", 2, TaskStatus.DONE, 1);
        manager.updateSubTask(testUpdateSubTask1);
        SubTask actualSubTask1 = manager.getSubTaskById(2);
        assertEquals("NewSubtask 1", actualSubTask1.getTaskName(), "Имя новой подзадачи ошибочен");
        assertEquals("Новое описание", actualSubTask1.getDescription(), "Описание новой подзадачи ошибочно");
        assertEquals(TaskStatus.DONE, actualSubTask1.getStatus(), "Статус новой подзадачи ошибочен");
        assertEquals(TaskStatus.IN_PROGRESS, actualEpic.getStatus(), "Статус эпика после апдейта подзадачи ошибочен");

        SubTask testUpdateSubTask2 = new SubTask("NewSubtask 2", "Новое описание", 3, TaskStatus.DONE, 1);
        manager.updateSubTask(testUpdateSubTask2);
        SubTask actualSubTask2 = manager.getSubTaskById(3);
        assertEquals("NewSubtask 2", actualSubTask2.getTaskName(), "Имя новой подзадачи ошибочен");
        assertEquals("Новое описание", actualSubTask2.getDescription(), "Описание новой подзадачи ошибочно");
        assertEquals(TaskStatus.DONE, actualSubTask2.getStatus(), "Статус новой подзадачи ошибочен");
        assertEquals(TaskStatus.DONE, actualEpic.getStatus(), "Статус эпика после апдейта подзадачи не верен");

        manager.deleteEpicById(4);
        assertFalse(manager.containsEpic(2),"Эпик не удалён");
        assertFalse(manager.containsSubTasks(5),"Подзадача 1 эпика не удалена");
        assertFalse(manager.containsSubTasks(6),"Подзадача 2 эпика не удалена");

        manager.deleteSubTaskById(3);
        assertFalse(actualEpic.getSubTasks().contains(subTask3),"Подзадача 3 из списка эпика не удалена");
        assertFalse(manager.containsSubTasks(3),"Подзадача 3 эпика не удалена");
    }

    @Test
    public void testTasksCleaning() {
        Task task1 = manager.createNewTask(new Task("Task 1", "T1", 1, TaskStatus.NEW));
        Task task2 = manager.createNewTask(new Task("Task 2", "T2", 2, TaskStatus.IN_PROGRESS));

        assertEquals(2, manager.getAllTasks().size(), "Задачи не добавлены");

        manager.tasksCleaning();

        assertEquals(0, manager.getAllTasks().size(), "Задачи не удалены");
    }

    @Test
    public void testEpicCleaning() {
        Epic epic1 = manager.createNewEpic(new Epic("Epic 1", "E", 1, TaskStatus.NEW));
        SubTask subTask1 = manager.createNewSubTask(new SubTask("Subtask 1", "S1", 2, TaskStatus.NEW, 1));
        SubTask subTask2 = manager.createNewSubTask(new SubTask("Subtask 2", "S2", 3, TaskStatus.NEW, 1));

        assertEquals(1, manager.getAllEpic().size(), "Эпик не добавлен");
        assertEquals(2, manager.getAllSubTasks().size(), "Подзадачи не добавлены");


        manager.epicCleaning();

        assertEquals(0, manager.getAllEpic().size(), "Эпик не удален");
        assertEquals(0, manager.getAllSubTasks().size(), "Подзадачи не удалены");
    }

    @Test
    public void testSubTasksCleaning() {
        Epic epic1 = manager.createNewEpic(new Epic("Epic 1", "E", 1, TaskStatus.NEW));
        SubTask subTask1 = manager.createNewSubTask(new SubTask("Subtask 1", "S1", 2, TaskStatus.NEW, 1));
        SubTask subTask2 = manager.createNewSubTask(new SubTask("Subtask 2", "S2", 3, TaskStatus.NEW, 1));

        assertEquals(1, manager.getAllEpic().size(), "Эпик не добавлен");
        assertEquals(2, manager.getAllSubTasks().size(),"Подзадачи не добавлены");

        manager.subTasksCleaning();

        assertEquals(1, manager.getAllEpic().size(), "Эпик удален");
        assertEquals(0, manager.getAllSubTasks().size(), "Подзадачи не удалены");
    }
}