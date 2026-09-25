package rice;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests the text formatting performed when tasks are saved and loaded. */
public class StorageTest {

    @TempDir
    Path temporaryDirectory;

    @Test
    void saveallTaskTypesWritesExpectedDelimitedRecords() throws Exception {
        Storage storage = new Storage(temporaryDirectory.toString(), "tasks.txt");

        Todo todo = new Todo("read book");

        Deadline deadline = new Deadline("submit report", "2026-12-12");
        deadline.mark();

        Event event = new Event("team meeting", "2026-12-13", "2026-12-14");

        storage.save(List.of(todo, deadline, event));

        List<String> savedLines = Files.readAllLines(temporaryDirectory.resolve("tasks.txt"));

        assertEquals(List.of(
                "T|0|read book",
                "D|1|submit report|2026-12-12",
                "E|0|team meeting|2026-12-13|2026-12-14"), savedLines);
    }

    @Test
    void loadExistingFileReadsSavedRecords() throws Exception {
        Path file = temporaryDirectory.resolve("tasks.txt");
        Files.write(file, List.of(
                "T|1|read book",
                "D|0|submit report|2026-12-12",
                "E|1|team meeting|2026-12-13|2026-12-14"));

        Storage storage = new Storage(temporaryDirectory.toString(), "tasks.txt");

        assertEquals(List.of(
                "T|1|read book",
                "D|0|submit report|2026-12-12",
                "E|1|team meeting|2026-12-13|2026-12-14"), storage.load());
    }
}
