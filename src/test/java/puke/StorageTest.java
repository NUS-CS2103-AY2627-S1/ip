package puke;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;

class StorageTest {
    @Test
    void savesAndLoadsTasks() throws Exception {
        Path file = Files.createTempFile("puke", ".txt");
        try {
            Storage storage = new Storage(file.toString());
            Task[] tasks = {new Task("read book"), new Deadline("submit report", "2026-08-24")};
            tasks[0].mark();
            storage.save(tasks, tasks.length);
            var loaded = storage.load();
            assertEquals(2, loaded.size());
            assertEquals("[T][X] read book", loaded.get(0).toString());
            assertEquals("[D][ ] submit report (by: Aug 24 2026)", loaded.get(1).toString());
        } finally {
            Files.deleteIfExists(file);
        }
    }

    @Test
    void loadsAnEmptyListWhenStorageFileDoesNotExist() throws Exception {
        Path file = Files.createTempFile("puke", ".txt");
        Files.deleteIfExists(file);
        assertEquals(0, new Storage(file.toString()).load().size());
    }

    @Test
    void skipsMalformedRecordsAndKeepsValidTasks() throws Exception {
        Path file = Files.createTempFile("puke", ".txt");
        try {
            Files.write(file, List.of(
                    "T|0|read book",
                    "D|0|broken deadline|not-a-date",
                    "E|1|project meeting|2pm|4pm"));
            var loaded = new Storage(file.toString()).load();
            assertEquals(2, loaded.size());
            assertEquals("[T][ ] read book", loaded.get(0).toString());
            assertEquals("[E][X] project meeting (from: 2pm to: 4pm)",
                    loaded.get(1).toString());
        } finally {
            Files.deleteIfExists(file);
        }
    }
}
