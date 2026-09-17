package orbit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Exercises malformed commands and persistence failures without touching real user data. */
public class MoreTestingTest {
    @TempDir
    private Path temporary;

    @Test
    public void getResponse_whitespaceAndRepeatedParameters_validatesBeforeSaving() throws IOException {
        Path file = temporary.resolve("tasks.txt");
        Orbit orbit = new Orbit(new Scanner(""), file.toString());
        assertTrue(orbit.getResponse("  TODO\tread book  ").contains("I've added"));
        String saved = Files.readString(file);
        for (String input : List.of("deadline report /by 2026-02-30", "todo bad | record",
                "deadline report /by 2026-09-18 /by 2026-09-19", "event talk /from 2 /from 3 /to 4",
                "mark 2147483648", "delete 0", "bye extra", "list extra", "find")) {
            assertTrue(orbit.getResponse(input).startsWith("OOPS!!!"), input);
            assertEquals(saved, Files.readString(file));
        }
        assertTrue(orbit.getResponse("deadline report\t/by\t2026-09-18").contains("I've added"));
    }

    @Test
    public void load_malformedRecords_reportsCheckedError() throws IOException {
        Path file = temporary.resolve("tasks.txt");
        for (String record : List.of("D | 0 | report", "E | 0 | talk | 2", "T | 2 | book",
                "T | 0 | ", "D | 0 | report | 2026-02-30", "T | 0 | book | extra")) {
            Files.writeString(file, record);
            assertThrows(OrbitException.class, () -> new Storage(file.toString()).load());
        }
    }

    @Test
    public void getResponse_corruptFile_preservesOriginalAndShowsWarning() throws IOException {
        Path file = temporary.resolve("tasks.txt");
        Files.writeString(file, "D | 0 | report");
        Orbit orbit = new Orbit(new Scanner(""), file.toString());
        assertTrue(orbit.getStorageWarning().contains("Saving is disabled"));
        assertTrue(orbit.getResponse("todo book").contains("Saving is disabled"));
        assertEquals("D | 0 | report", Files.readString(file));
    }

    @Test
    public void getResponse_unwritableDestination_reportsUnsavedChangesAndCanRecover()
            throws IOException, OrbitException {
        Path parent = temporary.resolve("blocked");
        Files.writeString(parent, "not a directory");
        Orbit orbit = new Orbit(new Scanner(""), parent.resolve("tasks.txt").toString());
        assertTrue(orbit.getResponse("todo book").contains("Changes are in memory only"));
        Files.delete(parent);
        assertTrue(orbit.getResponse("todo report").contains("I've added"));
        assertEquals(2, new Storage(parent.resolve("tasks.txt").toString()).load().size());
    }

    @Test
    public void save_mixedTasks_roundTripsAndMissingFileStartsEmpty() throws IOException, OrbitException {
        Storage storage = new Storage(temporary.resolve("data/tasks.txt").toString());
        assertTrue(storage.load().isEmpty());
        Todo todo = new Todo("book");
        todo.markAsDone();
        List<Task> tasks = List.of(todo, new Deadline("report", "2026-09-18"), new Event("talk", "2", "3"));
        storage.save(tasks);
        assertEquals(tasks.stream().map(Task::toDataString).toList(),
                storage.load().stream().map(Task::toDataString).toList());
    }
}
