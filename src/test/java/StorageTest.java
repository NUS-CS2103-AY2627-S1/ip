import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests persistence and malformed task-data handling. */
class StorageTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void savesAndLoadsAllTaskTypesAndStates() throws IOException {
        Storage storage = new Storage(temporaryDirectory.resolve("data").resolve("duke.txt"));
        Todo todo = new Todo("read book");
        todo.markAsDone();
        Event event = new Event("project meeting", "Mon 2pm", "4pm");
        event.markAsDone();

        storage.save(List.of(todo, new Deadline("return book", LocalDate.of(2019, 10, 15)), event));

        Storage.LoadResult result = storage.load();
        assertEquals(3, result.getTasks().size());
        assertEquals("[T][X] read book", result.getTasks().get(0).toString());
        assertEquals("[D][ ] return book (by: Oct 15 2019)", result.getTasks().get(1).toString());
        assertEquals("[E][X] project meeting (from: Mon 2pm to: 4pm)", result.getTasks().get(2).toString());
        assertEquals(0, result.getWarnings().size());
    }

    @Test
    void missingFileLoadsEmptyList() throws IOException {
        Storage.LoadResult result = new Storage(temporaryDirectory.resolve("missing.txt")).load();
        assertEquals(0, result.getTasks().size());
        assertEquals(0, result.getWarnings().size());
    }

    @Test
    void rejectsMalformedRecordsAndRequiredFields() throws IOException {
        Path file = temporaryDirectory.resolve("duke.txt");
        Storage storage = new Storage(file);
        List<String> invalidRecords = List.of(
                "X\t0\ttask\n", "T\t2\ttask\n", "T\t0\tbad\\q\n", "T\t0\t   \n",
                "E\t0\tevent\t\tend\n");

        for (String record : invalidRecords) {
            Files.writeString(file, record, StandardCharsets.UTF_8);
            assertThrows(IOException.class, storage::load);
        }
    }

    @Test
    void preservesLegacyDeadlineWarning() throws IOException {
        Path file = temporaryDirectory.resolve("duke.txt");
        Files.writeString(file, "D\t0\tlegacy task\tSunday\n", StandardCharsets.UTF_8);

        Storage.LoadResult result = new Storage(file).load();
        assertEquals(0, result.getTasks().size());
        assertEquals(1, result.getWarnings().size());
        assertEquals("OOPS!!! Skipped saved deadline at line 1 because its date is not in yyyy-MM-dd format.",
                result.getWarnings().get(0));
    }
}
