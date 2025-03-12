package ru.yandex.practicum.TaskManager.Tests;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.TaskManager.Service.HistoryManager;
import ru.yandex.practicum.TaskManager.Service.InMemoryTaskManager;
import ru.yandex.practicum.TaskManager.Service.Managers;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {

    @Test
    void testShouldReturnInitializedManagers() {
        InMemoryTaskManager manager = Managers.getDefault();
        HistoryManager historyManager = Managers.getDefaultHistory();
        assertNotNull(manager, "Менеджер задачь не проинециализирован");
        assertNotNull(historyManager, "Менеджер истории не проинециализирован");
    }
}