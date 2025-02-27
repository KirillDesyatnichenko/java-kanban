import ru.yandex.practicum.TaskManager.Model.Epic;
import ru.yandex.practicum.TaskManager.Model.SubTask;
import ru.yandex.practicum.TaskManager.Model.Task;
import ru.yandex.practicum.TaskManager.Model.TaskStatus;
import ru.yandex.practicum.TaskManager.Service.TaskManager;

public class Main {

    public static void main(String[] args) {
        TaskManager manager = new TaskManager();

        // Тестирование программы (В рамках ТЗ). Создаем задачи.
        Task task1 = manager.createNewTask("Задача № 1", "Не свиснуть кукухой при написании фин.задания");
        Task task2 = manager.createNewTask("Задача № 2", "Подготовить фин.задание и отправить на ревью");

        Epic epic1 = manager.createNewEpic("Эпическая задача № 1", "Помыть деда");
        SubTask epic1SubTask1 = manager.createNewSubTask("Подзадача № 1", "Раздобыть воду", epic1);
        SubTask epic1SubTask2 = manager.createNewSubTask("Подзадача № 2", "Найти подходящего деда", epic1);

        Epic epic2 = manager.createNewEpic("Эпическая задача № 2", "Убежать от полиции после мытья деда");
        SubTask epic2SubTask1 = manager.createNewSubTask("Подзадача № 1", "Неделю тренироваться бегать", epic2);

        // Распечатываем списки.
        System.out.println("Добавление задач, эпиков и подзадач");
        System.out.println(manager.getAllTasks());
        System.out.println(manager.getAllEpic());
        System.out.println(manager.getAllSubTasks());

        // Изменение статусов задачи № 1, и подзадачи № 1 эпика № 1 и проверка
        manager.updateTask(1, new Task("Задача № 1", "Не свиснуть кукухой при написании фин.задания", 1, TaskStatus.IN_PROGRESS));
        manager.updateSubTask(4, new SubTask("Подзадача № 1", "Раздобыть воду", 4, TaskStatus.DONE, epic1));

        System.out.println("   ");
        System.out.println("Изменены статусы задачи № 1, и подзадачи № 1 эпика № 1");
        System.out.println(manager.getAllTasks());
        System.out.println(manager.getAllEpic());
        System.out.println(manager.getAllSubTasks());

        // Удаление задачи № 2 и эпика № 2
        manager.deleteTaskById(2);
        manager.deleteEpicById(6);

        System.out.println("   ");
        System.out.println("Удалены задача 2 и эпик 2");
        System.out.println(manager.getAllTasks());
        System.out.println(manager.getAllEpic());
        System.out.println(manager.getAllSubTasks());
    }
}