import ru.yandex.practicum.TaskManager.Model.Epic;
import ru.yandex.practicum.TaskManager.Model.SubTask;
import ru.yandex.practicum.TaskManager.Model.Task;
import ru.yandex.practicum.TaskManager.Model.TaskStatus;
import ru.yandex.practicum.TaskManager.Service.InMemoryTaskManager;
import ru.yandex.practicum.TaskManager.Service.Managers;


public class Main {

    public static void main(String[] args) {
        InMemoryTaskManager manager = Managers.getDefault();

        // Реализация пользовательского сценария
        System.out.println("Пользовательский сценарий");
        manager.createNewEpic(new Epic("Эпическая задача № 1", "Помыть деда", 0, TaskStatus.NEW));
        manager.createNewSubTask(new SubTask("Подзадача № 1", "Раздобыть воду", 0, TaskStatus.NEW, 1));
        manager.createNewSubTask(new SubTask("Подзадача № 2", "Найти подходящего деда", 0, TaskStatus.NEW, 1));
        manager.createNewSubTask(new SubTask("Подзадача № 3", "Обездвижить деда перед помывкой", 0, TaskStatus.NEW, 1));
        manager.createNewEpic(new Epic("Эпическая задача № 2", "Убежать от полиции после мытья деда", 0, TaskStatus.NEW));

        // Добавление задач в историю
        System.out.println("\nЗапросы задач\n");
        System.out.println(manager.getEpicById(5));
        System.out.println("\nИстория\n");
        System.out.println(manager.getHistory());
        System.out.println(manager.getSubTaskById(3));
        System.out.println("\nИстория\n");
        System.out.println(manager.getHistory());
        System.out.println(manager.getSubTaskById(2));
        System.out.println("\nИстория\n");
        System.out.println(manager.getHistory());
        System.out.println(manager.getEpicById(1));
        System.out.println("\nИстория\n");
        System.out.println(manager.getHistory());
        System.out.println(manager.getSubTaskById(4));
        System.out.println("\nИстория\n");
        System.out.println(manager.getHistory());
        System.out.println(manager.getSubTaskById(2));
        System.out.println("\nИстория\n");
        System.out.println(manager.getHistory());

        // Удаление субтаска с id 4 и проверка истории
        manager.deleteSubTaskById(4);
        System.out.println("\nИстория после удаления субтаска с id 4\n");
        System.out.println(manager.getHistory());

        // Удаление эпика с субтасками и проверка истории
        manager.deleteEpicById(1);
        System.out.println("\nИстория после удаления эпика с id 1 и его подзадачь\n");
        System.out.println(manager.getHistory());
    }

    private static void printAll(InMemoryTaskManager manager) {
        System.out.println("Задачи:");
        for (Task task : manager.getAllTasks()) {
            System.out.println(task);
        }

        System.out.println("Эпики:");
        for (Epic epic : manager.getAllEpic()) {
            System.out.println(epic);
            System.out.println("--> " + epic.getSubTasks());
        }

        System.out.println("Подзадачи:");
        for (SubTask subtask : manager.getAllSubTasks()) {
            System.out.println(subtask);
        }

        System.out.println("История:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
    }
}