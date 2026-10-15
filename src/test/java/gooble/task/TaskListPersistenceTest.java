package gooble.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import gooble.storage.Storage;

/** Tests persistence across task-list instances and storage format versions. */
class TaskListPersistenceTest {
    @Test
    void missingStorageFile_startsEmptyAndIsCreatedWhenSaving(@TempDir Path tempDir) throws Exception {
        Path storagePath = tempDir.resolve("data").resolve("Gooble.txt");
        TaskList tasks = new TaskList(new Storage(storagePath));

        assertEquals(0, tasks.size());
        tasks.add(new Task("first task"));

        assertTrue(Files.isRegularFile(storagePath));
    }

    @Test
    void malformedRecords_areSkippedAndLegacyRecordsRemainReadable(@TempDir Path tempDir) throws Exception {
        Path storagePath = tempDir.resolve("tasks.txt");
        Files.write(storagePath, List.of(
                "not a task",
                "T|2|cmVhZA==",
                "[Q] malformed",
                "[X] legacy task"), StandardCharsets.UTF_8);

        TaskList tasks = new TaskList(new Storage(storagePath));

        assertEquals(1, tasks.size());
        assertEquals("[X] legacy task", tasks.get(0).toString());
    }

    @Test
    void supportedTaskTypes_areRestoredWithTheirDetailsAndStatus(@TempDir Path tempDir) throws Exception {
        Path storagePath = tempDir.resolve("tasks.txt");
        TaskList savedTasks = new TaskList(new Storage(storagePath));
        savedTasks.add(new Todo("read | book ) [special]"));
        savedTasks.add(new Deadline("return book", DeadlineDateParser.parse("2/12/2019 1800")));
        savedTasks.add(new Event("project meeting", "Mon (2pm)", "4pm | room 1"));
        savedTasks.markAsDone(0);

        TaskList restoredTasks = new TaskList(new Storage(storagePath));

        assertEquals(3, restoredTasks.size());
        assertEquals("[T][X] read | book ) [special]", restoredTasks.get(0).toString());
        assertEquals("[D][ ] return book (by: Dec 02 2019, 6:00 PM)",
                restoredTasks.get(1).toString());
        assertEquals("[E][ ] project meeting (from: Mon (2pm) to: 4pm | room 1)",
                restoredTasks.get(2).toString());
    }
}
