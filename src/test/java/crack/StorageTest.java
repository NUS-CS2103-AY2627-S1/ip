package crack;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import crack.task.Deadline;
import crack.task.Event;
import crack.task.Todo;

public class StorageTest {
    @TempDir
    private Path tempDir;

    @Test
    public void load_noSaveFileYet_comesBackEmpty() throws CrackException {
        Storage storage = new Storage(tempDir.resolve("data").resolve("data.txt").toString());
        assertTrue(storage.load().isEmpty());
    }

    @Test
    public void saveThenLoad_everyTaskTypeSurvives() throws CrackException {
        Path file = tempDir.resolve("data").resolve("data.txt");
        Storage storage = new Storage(file.toString());

        TaskList tasks = new TaskList();
        tasks.add(new Todo("buy milk"),
                Deadline.of("return book", "2/12/2020 1500"),
                Deadline.of("other thing", "2019-10-15"),
                Event.of("carnival", "1/12/2020 1400", "3/12/2020 1800"));
        tasks.get(0).markAsDone();
        storage.save(tasks);

        TaskList loaded = new Storage(file.toString()).load();
        assertEquals(4, loaded.size());
        assertEquals("[T][X] buy milk", loaded.get(0).toString());
        assertEquals("[D][ ] return book (by: Dec 2 2020, 3:00 PM)", loaded.get(1).toString());
        assertEquals("[D][ ] other thing (by: Oct 15 2019)", loaded.get(2).toString());
        assertEquals("[E][ ] carnival (from: Dec 1 2020, 2:00 PM to: Dec 3 2020, 6:00 PM)",
                loaded.get(3).toString());
    }

    @Test
    public void load_corruptedLines_skippedRatherThanCrashing() throws CrackException, IOException {
        Path file = tempDir.resolve("data.txt");
        Files.writeString(file, String.join(System.lineSeparator(),
                "T | 0 | keep me",
                "garbage line",
                "D | 9",
                "X | 0 | unknown type",
                "D | 0 | old style | Sunday",
                "E | 1 | party | 2020-12-05 2000 | 2020-12-05 2300"));

        TaskList loaded = new Storage(file.toString()).load();
        assertEquals(2, loaded.size());
        assertEquals("[T][ ] keep me", loaded.get(0).toString());
        assertEquals("[E][X] party (from: Dec 5 2020, 8:00 PM to: Dec 5 2020, 11:00 PM)",
                loaded.get(1).toString());
    }

    @Test
    public void saveThenLoad_descriptionContainingTheSeparator_survivesWhole() throws CrackException {
        Path file = tempDir.resolve("data").resolve("data.txt");

        TaskList tasks = new TaskList();
        tasks.add(new Todo("email bob | cc alice"),
                Deadline.of("ship v1 | final", "18-9-2026 2359"),
                Event.of("standup | daily", "14-9-2026 1400", "14-9-2026 1600"));
        new Storage(file.toString()).save(tasks);

        TaskList loaded = new Storage(file.toString()).load();
        assertEquals(3, loaded.size());
        assertEquals("[T][ ] email bob | cc alice", loaded.get(0).toString());
        assertEquals("[D][ ] ship v1 | final (by: Sep 18 2026, 11:59 PM)", loaded.get(1).toString());
        assertEquals("[E][ ] standup | daily (from: Sep 14 2026, 2:00 PM to: Sep 14 2026, 4:00 PM)",
                loaded.get(2).toString());
    }

    @Test
    public void load_lineWithNoDescription_isSkipped() throws CrackException, IOException {
        Path file = tempDir.resolve("data").resolve("data.txt");
        Files.createDirectories(file.getParent());
        Files.writeString(file, "T | 0 | \nT | 0 |    \nD | 0 |  | 2020-12-02 1500\nT | 0 | real one\n");

        TaskList loaded = new Storage(file.toString()).load();
        assertEquals(1, loaded.size());
        assertEquals("[T][ ] real one", loaded.get(0).toString());
    }

    @Test
    public void save_pathWithNoFolder_doesNotThrow() throws CrackException {
        Storage storage = new Storage(tempDir.resolve("bare.txt").toString());
        storage.save(new TaskList());
        assertTrue(storage.load().isEmpty());
    }
}
