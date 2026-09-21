package friday.storage;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import friday.task.Deadline;
import friday.task.Event;
import friday.task.Task;
import friday.task.ToDo;

/**
 * Tests persistence and failure recovery using fresh temporary files for every test invocation.
 */
class StorageTest {
    /**
     * JUnit owns and cleans this directory; tests never access the user's data/friday.txt.
     */
    @TempDir
    Path directory;

    @Test
    void load_missingFile_returnsEmptyListWithoutCreatingFolders() throws IOException {
        Path file = directory.resolve("missing/nested/friday.txt");
        assertTrue(new Storage(file).load().isEmpty());
        assertFalse(Files.exists(file.getParent()));
    }

    @Test
    void load_emptyFile_returnsEmptyList() throws IOException {
        Path file = directory.resolve("friday.txt");
        Files.writeString(file, "");
        assertTrue(new Storage(file).load().isEmpty());
    }

    @Test
    void save_missingParents_createsFoldersAndStoresCanonicalRecord() throws IOException {
        Path file = directory.resolve("missing/nested/friday.txt");
        Storage storage = new Storage(file);
        storage.save(List.of(new ToDo("first task")));
        assertEquals("T|0|first task" + System.lineSeparator(), Files.readString(file));
        assertEquals("first task", storage.load().getFirst().getDescription());
    }

    @Test
    void saveAndLoad_allTypesAndEscapedText_preservesEveryField() throws IOException {
        Path file = directory.resolve("friday.txt");
        Storage storage = new Storage(file);
        List<Task> tasks = List.of(
                new ToDo("read | books \\ notes\nnext\rline café 读书"),
                new Deadline("return | book \\ café", "2/12/2019 1800"),
                new Event("meeting\nnext\rline", "2019-12-02 14:00", "2019-12-03 16:00"));
        tasks.get(0).markAsDone();
        tasks.get(2).markAsDone();
        storage.save(tasks);
        List<Task> loadedTasks = storage.load();
        assertEquals(tasks.size(), loadedTasks.size());
        for (int i = 0; i < tasks.size(); i++) {
            assertEquals(tasks.get(i).getClass(), loadedTasks.get(i).getClass());
            assertEquals(tasks.get(i).getDescription(), loadedTasks.get(i).getDescription());
            assertEquals(tasks.get(i).isDone(), loadedTasks.get(i).isDone());
        }
        Deadline deadline = assertInstanceOf(Deadline.class, loadedTasks.get(1));
        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0), deadline.getBy());
        Event event = assertInstanceOf(Event.class, loadedTasks.get(2));
        assertEquals(LocalDateTime.of(2019, 12, 2, 14, 0), event.getFrom());
        assertEquals(LocalDateTime.of(2019, 12, 3, 16, 0), event.getTo());
        assertEquals(3, Files.readAllLines(file).size(), "Escaped newlines must not split records.");
        assertTrue(Files.readString(file).contains("|2019-12-02T18:00"));
    }

    @Test
    void loadAndSave_literalEscapedRecord_matchesSpecifiedWireFormat() throws IOException {
        Path file = directory.resolve("friday.txt");
        String record = "T|1|read \\| books \\\\ notes\\nnext\\rline café";
        Files.writeString(file, record + "\n");
        Storage storage = new Storage(file);
        Task task = storage.load().getFirst();
        assertInstanceOf(ToDo.class, task);
        assertEquals("read | books \\ notes\nnext\rline café", task.getDescription());
        assertTrue(task.isDone());
        storage.save(List.of(task));
        assertEquals(record + System.lineSeparator(), Files.readString(file));
    }

    @Test
    void save_changedStatusesAndDeletions_replacesSnapshotRatherThanAppending() throws IOException {
        Path file = directory.resolve("friday.txt");
        Storage storage = new Storage(file);
        ToDo first = new ToDo("first");
        first.markAsDone();
        ToDo second = new ToDo("second");
        storage.save(List.of(first, second, new ToDo("remove me")));
        first.unmarkAsDone();
        second.markAsDone();
        storage.save(List.of(first, second));
        assertEquals(List.of("T|0|first", "T|1|second"), Files.readAllLines(file));
        List<Task> loadedTasks = storage.load();
        assertEquals(2, loadedTasks.size());
        assertFalse(loadedTasks.get(0).isDone());
        assertTrue(loadedTasks.get(1).isDone());
        storage.save(List.of());
        assertEquals("", Files.readString(file));
        assertTrue(storage.load().isEmpty());
    }

    @ParameterizedTest
    @EmptySource
    @ValueSource(strings = {"nonsense", "Q|0|unknown", "T|2|bad status", "T|true|bad status",
            "T|0|", "T|0|   ", "T|0|extra|field", "D|0|missing date", "D|0|empty date|",
            "E|0|missing end|start", "E|0|event||end", "E|0|event|start|end|extra",
            "T|0|bad\\q", "T|0|trailing\\", "D|0|old deadline|Sunday",
            "D|0|impossible date|2019-02-29", "D|0|bad time|2019-12-02T24:00",
            "E|0|old event|Mon 2pm|4pm", "E|0|backwards|2019-12-03|2019-12-02",
            "E|0|bad end|2019-12-02|2019-02-29"})
    void load_invalidSecondRecord_rejectsWholeFileAndPreservesBytes(String invalid) throws IOException {
        Path file = directory.resolve("friday.txt");
        Files.writeString(file, "T|0|valid first record\n" + invalid + "\n");
        byte[] originalBytes = Files.readAllBytes(file);
        IOException error = assertThrows(IOException.class, () -> new Storage(file).load());
        assertEquals("Invalid task record on line 2.", error.getMessage());
        assertArrayEquals(originalBytes, Files.readAllBytes(file));
    }

    @Test
    void load_invalidUtf8_throwsWithoutChangingBytes() throws IOException {
        Path file = directory.resolve("friday.txt");
        byte[] invalidBytes = {(byte) 0xC3, (byte) 0x28};
        Files.write(file, invalidBytes);
        assertThrows(IOException.class, () -> new Storage(file).load());
        assertArrayEquals(invalidBytes, Files.readAllBytes(file));
    }

    @Test
    void load_directoryInPlaceOfFile_throwsWithoutDeletingDirectory() throws IOException {
        Path file = directory.resolve("friday.txt");
        Files.createDirectory(file);
        assertThrows(IOException.class, () -> new Storage(file).load());
        assertTrue(Files.isDirectory(file));
    }

    @Test
    void saveAndLoad_moreThanOneHundredTasks_preservesAllTasksInOrder() throws IOException {
        Storage storage = new Storage(directory.resolve("friday.txt"));
        List<Task> tasks = new ArrayList<>();
        for (int i = 0; i < 125; i++) {
            tasks.add(new ToDo("task " + i));
        }
        storage.save(tasks);
        assertEquals(tasks.stream().map(Task::getDescription).toList(),
                storage.load().stream().map(Task::getDescription).toList());
    }

    @Test
    void save_parentIsAFile_throwsWithoutOverwritingIt() throws IOException {
        Path parent = directory.resolve("not-a-directory");
        Files.writeString(parent, "unchanged");
        assertThrows(IOException.class,
                () -> new Storage(parent.resolve("friday.txt")).save(List.of(new ToDo("task"))));
        assertEquals("unchanged", Files.readString(parent));
    }

    @Test
    void save_destinationIsNonemptyDirectory_preservesContentsAndCleansTemporaryFile() throws IOException {
        Path destination = directory.resolve("friday.txt");
        Files.createDirectory(destination);
        Files.writeString(destination.resolve("keep.txt"), "unchanged");
        assertThrows(IOException.class, () -> new Storage(destination).save(List.of(new ToDo("task"))));
        assertEquals("unchanged", Files.readString(destination.resolve("keep.txt")));
        assertOnlySavePathRemains();
    }

    @Test
    void save_serializationFailsAfterPartialWrite_preservesExistingSaveAndCleansTemporaryFile() throws IOException {
        Path file = directory.resolve("friday.txt");
        Storage storage = new Storage(file);
        storage.save(List.of(new ToDo("keep this task")));
        byte[] originalBytes = Files.readAllBytes(file);
        assertThrows(IllegalArgumentException.class,
                () -> storage.save(List.of(new ToDo("partial write"), new Task("unsupported"))));
        assertArrayEquals(originalBytes, Files.readAllBytes(file));
        assertOnlySavePathRemains();
        storage.save(List.of(new ToDo("retry")));
        assertEquals(List.of("T|0|retry"), Files.readAllLines(file));
        assertOnlySavePathRemains();
    }

    @Test
    void loadAndSave_supportedLegacyDates_normalizesDatesWithoutLosingStatusOrTime() throws IOException {
        Path file = directory.resolve("friday.txt");
        Files.writeString(file, "D|1|return book|2019-12-02\n"
                + "E|0|meeting|2/12/2019 1800|2019-12-02 20:00\n");
        Storage storage = new Storage(file);
        List<Task> loadedTasks = storage.load();
        Deadline deadline = assertInstanceOf(Deadline.class, loadedTasks.get(0));
        assertEquals(LocalDateTime.of(2019, 12, 2, 0, 0), deadline.getBy());
        assertTrue(deadline.isDone());
        storage.save(loadedTasks);
        assertEquals(List.of("D|1|return book|2019-12-02T00:00",
                "E|0|meeting|2019-12-02T18:00|2019-12-02T20:00"), Files.readAllLines(file));
    }

    /**
     * Checks that a failed save leaves no partial temporary snapshot in the data folder.
     */
    private void assertOnlySavePathRemains() throws IOException {
        try (var files = Files.list(directory)) {
            assertEquals(List.of("friday.txt"), files.map(path -> path.getFileName().toString()).toList());
        }
    }
}
