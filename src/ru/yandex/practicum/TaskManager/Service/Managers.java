package ru.yandex.practicum.TaskManager.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Managers {
   // private static final String PATH_FILE = "src/history.csv";
   static Path tempFile;

    static {
        try {
            tempFile = Files.createTempFile("temp-", ".csv");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static InMemoryTaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static InMemoryHistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static FileBackedTaskManager getDefaultFileManager() {
        return new FileBackedTaskManager(tempFile);
    }
}
