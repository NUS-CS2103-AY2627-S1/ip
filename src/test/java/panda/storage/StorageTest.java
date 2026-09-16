package panda.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import panda.model.Deadline;
import panda.model.Event;
import panda.model.Task;
import panda.model.Todo;

class StorageTest {
    private static final String SAVE_FILE_PROPERTY = "panda.save.path";

    @TempDir
    private Path temporaryDirectory;

    @AfterEach
    void clearSaveFileOverride() {
        System.clearProperty(SAVE_FILE_PROPERTY);
    }

    @Test
    void loadTasks_missingFile_returnsEmptyList() {
        setSaveFile("missing", "panda.txt");

        assertEquals(List.of(), Storage.loadTasks());
    }

    @Test
    void saveTasks_thenLoadTasks_roundTripsTypesAndStatuses() {
        Path saveFile = setSaveFile("data", "panda.txt");
        Todo completedTodo = new Todo("read book");
        completedTodo.markAsDone();
        Event completedEvent = new Event("meeting", LocalDateTime.of(2026, 10, 20, 9, 0),
                LocalDateTime.of(2026, 10, 20, 10, 0));
        completedEvent.markAsDone();
        List<Task> tasks = List.of(completedTodo, new Deadline("report", LocalDateTime.of(2026, 10, 21, 0, 0)),
                completedEvent);

        Storage.saveTasks(tasks);

        assertEquals(List.of("T | 1 | read book", "D | 0 | report | 2026-10-21T00:00:00",
                "E | 1 | meeting | 2026-10-20T09:00:00 | 2026-10-20T10:00:00"), readLines(saveFile));
        assertEquals(List.of("[T][X] read book", "[D][ ] report (by: Oct 21 2026)",
                "[E][X] meeting (from: Oct 20 2026 9:00 AM to: Oct 20 2026 10:00 AM)"),
                Storage.loadTasks().stream().map(Task::toString).toList());
    }

    @Test
    void loadTasks_malformedRecords_skipsOnlyInvalidLines() throws IOException {
        Path saveFile = setSaveFile("data", "panda.txt");
        Files.createDirectories(saveFile.getParent());
        Files.writeString(saveFile, "T | 0 | valid todo\ninvalid\nD | 3 | invalid status | 2026-10-20T00:00:00\n"
                + "E | 1 | valid event | 2026-10-20T09:00:00 | 2026-10-20T10:00:00\n");

        assertEquals(List.of("[T][ ] valid todo",
                        "[E][X] valid event (from: Oct 20 2026 9:00 AM to: Oct 20 2026 10:00 AM)"),
                Storage.loadTasks().stream().map(Task::toString).toList());
    }

    @Test
    void saveAndLoadTasks_saveFileIsDirectory_throwsRuntimeException() throws IOException {
        Path saveFile = setSaveFile("saved-directory");
        Files.createDirectories(saveFile);

        assertThrows(RuntimeException.class, () -> Storage.saveTasks(List.of(new Todo("read book"))));
        assertThrows(RuntimeException.class, Storage::loadTasks);
    }

    private Path setSaveFile(String... pathParts) {
        Path saveFile = temporaryDirectory;
        for (String pathPart : pathParts) {
            saveFile = saveFile.resolve(pathPart);
        }
        System.setProperty(SAVE_FILE_PROPERTY, saveFile.toString());
        return saveFile;
    }

    private List<String> readLines(Path path) {
        try {
            return Files.readAllLines(path);
        } catch (IOException e) {
            throw new AssertionError("Unable to read test save file", e);
        }
    }
}
