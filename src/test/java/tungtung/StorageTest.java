package tungtung;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Checks persistence boundaries, malformed files, and competing writers. */
class StorageTest {
    @TempDir
    private Path directory;

    @Test
    void save_acceptedDescriptionMatrix_roundTripsExactly() throws Exception {
        Parser parser = new Parser();
        ArrayList<Task> accepted = new ArrayList<>();
        String[] fragments = {"a", " ", "|", "\t", "文", "\n", "\r"};
        for (String first : fragments) {
            for (String second : fragments) {
                for (String third : fragments) {
                    String description = "task" + first + second + third;
                    for (String command : List.of("todo " + description,
                            "deadline " + description + " /by 2026-09-18",
                            "event " + description + " /from 2026-09-18 /to 2026-09-19")) {
                        try {
                            Task task = parser.parseTask(command);
                            task.setDone();
                            accepted.add(task);
                        } catch (TungTungException exception) {
                            // Rejected input must never reach storage; every accepted input must round-trip.
                        }
                    }
                }
            }
        }
        assertTrue(accepted.size() > 100);
        Storage storage = new Storage(directory.resolve("tasks.txt").toString());
        storage.load();
        storage.save(accepted);
        ArrayList<Task> restored = storage.load();
        assertEquals(accepted.size(), restored.size());
        for (int index = 0; index < accepted.size(); index++) {
            assertEquals(accepted.get(index).description, restored.get(index).description);
            assertEquals(accepted.get(index).toString(), restored.get(index).toString());
        }
    }

    @Test
    void save_invalidTask_preservesExistingFile() throws Exception {
        Path file = directory.resolve("tasks.txt");
        Storage storage = new Storage(file.toString());
        storage.load();
        storage.save(new ArrayList<>(List.of(new ToDo("precious"))));
        byte[] original = Files.readAllBytes(file);
        for (Task invalid : List.of(new ToDo("line\nbreak"), new ToDo("line\rbreak"),
                new ToDo(" "), new ToDo("buy | sell"),
                new Deadline("report |", LocalDate.of(2026, 9, 18)))) {
            assertThrows(IOException.class, () -> storage.save(new ArrayList<>(List.of(invalid))));
            assertArrayEquals(original, Files.readAllBytes(file));
        }
    }

    @Test
    void save_staleSession_preservesOtherSessionChanges() throws Exception {
        Path file = directory.resolve("tasks.txt");
        Storage first = new Storage(file.toString());
        Storage second = new Storage(file.toString());
        first.load();
        second.load();
        first.save(new ArrayList<>(List.of(new ToDo("first session"))));
        byte[] original = Files.readAllBytes(file);
        assertThrows(IOException.class, () -> second.save(new ArrayList<>(List.of(new ToDo("second session")))));
        assertArrayEquals(original, Files.readAllBytes(file));
    }

    @Test
    void save_externalDeletion_doesNotRecreateStaleData() throws Exception {
        Path file = directory.resolve("tasks.txt");
        Storage storage = new Storage(file.toString());
        storage.load();
        storage.save(new ArrayList<>(List.of(new ToDo("original"))));
        Files.delete(file);
        assertThrows(IOException.class, () -> storage.save(new ArrayList<>()));
        assertTrue(Files.notExists(file));
    }

    @Test
    void save_lockHeld_rejectsConcurrentWrite() throws Exception {
        Path file = directory.resolve("tasks.txt");
        Storage storage = new Storage(file.toString());
        storage.load();
        try (FileChannel channel = FileChannel.open(directory.resolve("tasks.txt.lock"),
                StandardOpenOption.CREATE, StandardOpenOption.WRITE);
                FileLock lock = channel.lock()) {
            assertTrue(lock.isValid());
            assertThrows(IOException.class, () -> storage.save(new ArrayList<>(List.of(new ToDo("blocked")))));
            assertTrue(Files.notExists(file));
        }
    }

    @Test
    void load_malformedData_rejectsWithoutChangingFile() throws Exception {
        Path file = directory.resolve("tasks.txt");
        for (String line : List.of("T | 2 | task", "T | 0 | ", "Q | 0 | task", "T | 0 | task | extra",
                "D | 0 | task | 2026-02-30", "E | 0 | task | 2026-09-19 | 2026-09-18", "broken")) {
            Files.writeString(file, line);
            Storage storage = new Storage(file.toString());
            assertThrows(IOException.class, storage::load);
            assertThrows(IOException.class, () -> storage.save(new ArrayList<>()));
            assertEquals(line, Files.readString(file));
        }
    }

    @Test
    void load_directoryInsteadOfFile_reportsError() throws Exception {
        Path file = directory.resolve("tasks.txt");
        Files.createDirectory(file);
        assertThrows(IOException.class, () -> new Storage(file.toString()).load());
    }
    @Test
    void load_invalidUtf8_preservesBytesAndRejectsSave() throws Exception {
        Path file = directory.resolve("tasks.txt");
        byte[] invalid = {(byte) 0xc3, (byte) 0x28};
        Files.write(file, invalid);
        Storage storage = new Storage(file.toString());
        assertThrows(IOException.class, storage::load);
        assertThrows(IOException.class, () -> storage.save(new ArrayList<>()));
        assertArrayEquals(invalid, Files.readAllBytes(file));
    }

    @Test
    void save_externalEdit_preservesEditedBytes() throws Exception {
        Path file = directory.resolve("tasks.txt");
        Files.writeString(file, "T | 0 | original\n");
        Storage storage = new Storage(file.toString());
        storage.load();
        Files.writeString(file, "T | 0 | external change\n");
        assertThrows(IOException.class, () -> storage.save(new ArrayList<>()));
        assertEquals("T | 0 | external change\n", Files.readString(file));
    }

    @Test
    void load_legacyLineEndings_preservesTasksOnSave() throws Exception {
        Path file = directory.resolve("tasks.txt");
        for (String newline : List.of("\n", "\r\n", "\r")) {
            Files.writeString(file, "T | 1 | completed" + newline
                    + "D | 0 | report | 2026-09-18" + newline
                    + "E | 0 | meeting | 2026-09-18 | 2026-09-19" + newline);
            Storage storage = new Storage(file.toString());
            ArrayList<Task> tasks = storage.load();
            assertEquals(3, tasks.size());
            assertEquals("[T][X] completed", tasks.get(0).toString());
            storage.save(tasks);
            assertEquals(3, storage.load().size());
        }
    }
}
