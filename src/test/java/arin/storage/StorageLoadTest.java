package arin.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import arin.task.Deadline;
import arin.task.Event;
import arin.task.Task;
import arin.task.Todo;

/**
 * Checks restart persistence and safe rejection of malformed saved records.
 */
class StorageLoadTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void loadTasks_missingFile_returnsEmptyListWithoutCreatingFile() throws Exception {
        Path taskFile = temporaryDirectory.resolve("missing/arin.txt");
        assertTrue(new Storage(taskFile).loadTasks().isEmpty());
        assertTrue(Files.notExists(taskFile));
    }

    @Test
    void loadTasks_savedTasks_preservesTypesTextStatusOrderAndDuplicates() throws Exception {
        Path taskFile = temporaryDirectory.resolve("arin.txt");
        Todo completedTask = new Todo("Read 日本語 📚");
        completedTask.markTask();
        List<Task> original = List.of(completedTask, new Deadline("return book", LocalDate.of(2028, 2, 29)),
                new Event("meeting", "Sep 16 2pm", "Sep 17 4pm"), new Todo("Read 日本語 📚"));
        new Storage(taskFile).saveTasks(original);

        List<Task> restored = new Storage(taskFile).loadTasks();

        assertEquals(original.stream().map(Task::storageToString).toList(),
                restored.stream().map(Task::storageToString).toList());
        assertEquals(original.stream().map(Task::toString).toList(), restored.stream().map(Task::toString).toList());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "T", "T | X", "T | done | task", "T |   | ", "Z | X | task",
        "D | X | task | 2026-02-30", "D | X | task", "T | X | task | extra",
        "E | X | task | 2pm 4pm", "E | X | task |  | 4pm", "E | X | task | 2pm | "})
    void loadTasks_malformedRecord_reportsLineAndPreservesFile(String record) throws Exception {
        Path taskFile = temporaryDirectory.resolve("arin.txt");
        String content = "T | X | valid task\n" + record + "\n";
        Files.writeString(taskFile, content);

        IOException exception = assertThrows(IOException.class, () -> new Storage(taskFile).loadTasks());

        assertTrue(exception.getMessage().contains("line 2"));
        assertEquals(content, Files.readString(taskFile));
    }

    @Test
    void saveTasks_targetIsDirectory_preservesContentsAndCleansTemporaryFile() throws Exception {
        Path target = Files.createDirectory(temporaryDirectory.resolve("arin.txt"));
        Path existingFile = target.resolve("keep.txt");
        Files.writeString(existingFile, "do not replace");

        assertThrows(IOException.class, () -> new Storage(target).saveTasks(List.of(new Todo("new task"))));

        assertEquals("do not replace", Files.readString(existingFile));
        try (var paths = Files.list(temporaryDirectory)) {
            assertEquals(List.of(target), paths.toList());
        }
    }
}
