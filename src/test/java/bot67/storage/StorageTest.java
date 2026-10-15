package bot67.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import bot67.task.Deadline;
import bot67.task.Event;
import bot67.task.Task;
import bot67.task.Todo;

/** Exercises real save files, including damaged data and failed replacements. */
class StorageTest {
    @TempDir
    private Path directory;

    @Test
    void saveAndReload_preservesEveryTaskTypeAndStatus() throws IOException {
        Storage storage = new Storage(directory.resolve("data/tasks.txt"));
        assertTrue(storage.load().isEmpty());
        Task todo = new Todo("todo read book");
        todo.mark();
        List<Task> tasks = List.of(todo, new Deadline("deadline report /by 2028-02-29 12:30"),
                new Event("event meeting /from Mon 2pm /to 4pm"));
        storage.save(tasks);
        assertEquals(tasks.stream().map(Task::toFileFormat).toList(),
                storage.load().stream().map(Task::toFileFormat).toList());
        storage.save(List.of(todo));
        assertEquals(1, storage.load().size());
        try (var files = Files.list(directory.resolve("data"))) {
            assertEquals(List.of("tasks.txt"), files.map(path -> path.getFileName().toString()).toList());
        }
    }

    @Test
    void defaultStorage_importsLegacyTasksAndSavesToBot67File() throws IOException {
        Path bot67File = directory.resolve("data/bot67.txt");
        Path legacyFile = directory.resolve("data/duke.txt");
        Files.createDirectories(legacyFile.getParent());
        Files.writeString(legacyFile, "T | 1 | retained task\n");

        Storage storage = new Storage(bot67File, legacyFile);
        assertEquals("[T][X] retained task", storage.load().getFirst().getDescription());
        assertEquals(legacyFile, storage.getLoadFile());
        storage.save(storage.load());
        assertTrue(Files.exists(bot67File));
        assertTrue(Files.exists(legacyFile));
    }

    @Test
    void load_rejectsCorruptRecordsWithoutChangingTheFile() throws IOException {
        Path file = directory.resolve("tasks.txt");
        for (String record : List.of("T | 2 | bad status", "T | 0 | ", "X | 0 | unknown",
                "T | 0 | a | b", "D | 0 | date | 2026-02-30 12:00", "D | 0 | date | ",
                "E | 0 | meeting | 2026-10-16 | 2026-10-15", "")) {
            String original = "T | 0 | valid\n" + record + "\n";
            Files.writeString(file, original);
            IOException error = assertThrows(IOException.class, () -> new Storage(file).load());
            assertEquals("Invalid saved task at line 2.", error.getMessage());
            assertEquals(original, Files.readString(file));
        }
    }

    @Test
    void load_rejectsInvalidEncodingAndDirectoryPaths() throws IOException {
        Path file = directory.resolve("tasks.txt");
        Files.write(file, new byte[]{(byte) 0xc3, (byte) 0x28});
        assertThrows(IOException.class, () -> new Storage(file).load());
        assertThrows(IOException.class, () -> new Storage(directory).load());
    }

    @Test
    void save_rejectsBlockedPathsWithoutRemovingExistingData() throws IOException {
        Path parentFile = directory.resolve("data");
        Files.writeString(parentFile, "keep me");
        Storage storage = new Storage(parentFile.resolve("tasks.txt"));
        assertThrows(IOException.class, () -> storage.save(List.of(new Todo("todo task"))));
        assertEquals("keep me", Files.readString(parentFile));
        Path target = Files.createDirectory(directory.resolve("tasks.txt"));
        Path sentinel = target.resolve("original.txt");
        Files.writeString(sentinel, "keep me too");
        assertThrows(IOException.class, () -> new Storage(target).save(List.of()));
        assertEquals("keep me too", Files.readString(sentinel));
    }
}
