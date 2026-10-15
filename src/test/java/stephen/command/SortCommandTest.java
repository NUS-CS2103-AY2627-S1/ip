package stephen.command;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import stephen.storage.Storage;
import stephen.task.Task;
import stephen.task.TaskList;
import stephen.task.Todo;
import stephen.ui.Ui;

/** Tests sorting through the command layer and persistence boundary. */
class SortCommandTest {
    @TempDir
    private Path testDirectory;

    /** Verifies that executing sort updates both memory and saved task order. */
    @Test
    void executeUnsortedTasksSortsAndSavesNewOrder() throws Exception {
        Path dataFile = testDirectory.resolve("tasks.txt");
        TaskList tasks = new TaskList(List.of(
                new Todo("zebra"),
                new Todo("Alpha"),
                new Todo("middle")));

        new SortCommand().execute(tasks, new Ui(), new Storage(dataFile));

        List<String> expected = List.of(
                "T | 0 | Alpha",
                "T | 0 | middle",
                "T | 0 | zebra");
        assertEquals(expected, tasks.getTasks().stream().map(Task::toDataString).toList());
        assertEquals(expected, Files.readAllLines(dataFile, StandardCharsets.UTF_8));
    }
}
