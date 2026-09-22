package panda.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.api.io.TempDir;

import panda.exception.DataLoadingException;
import panda.exception.DataSavingException;
import panda.exception.InvalidDateException;
import panda.exception.InvalidEventPeriodException;
import panda.exception.PandaException;
import panda.task.Deadline;
import panda.task.Event;
import panda.task.Task;
import panda.task.Todo;

/**
 * Tests persistence, malformed-record recovery, and storage failure handling.
 */
class StorageTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    void load_missingFile_returnsEmptyResult() throws DataLoadingException {
        Path dataFile = temporaryDirectory.resolve("missing").resolve("tasks.txt");

        Storage.LoadResult result = new Storage(dataFile.toString()).load();

        assertTrue(result.tasks().isEmpty());
        assertTrue(result.errors().isEmpty());
        assertFalse(Files.exists(dataFile));
    }

    @Test
    void load_emptyOrBlankFile_returnsEmptyResult() throws IOException, DataLoadingException {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(dataFile, System.lineSeparator() + "   ", StandardCharsets.UTF_8);

        Storage.LoadResult result = new Storage(dataFile.toString()).load();

        assertTrue(result.tasks().isEmpty());
        assertTrue(result.errors().isEmpty());
    }

    @Test
    void saveAndLoad_allTaskTypes_roundTripsOrderStatusAndUnicode()
            throws InvalidDateException, DataSavingException, DataLoadingException {
        Path dataFile = temporaryDirectory.resolve("nested").resolve("tasks.txt");
        Todo todo = new Todo("阅读 A | B \\ notes");
        Deadline deadline = new Deadline("submit report", "2028-02-29 08:05");
        Event event = new Event("camp", "2026-09-14 09:00", "2026-09-16 17:30");
        deadline.mark();
        Storage storage = new Storage(dataFile.toString());

        storage.save(List.of(todo, deadline, event));
        Storage.LoadResult result = storage.load();

        assertTrue(result.errors().isEmpty());
        assertEquals(3, result.tasks().size());
        assertInstanceOf(Todo.class, result.tasks().get(0));
        assertInstanceOf(Deadline.class, result.tasks().get(1));
        assertInstanceOf(Event.class, result.tasks().get(2));
        assertEquals("阅读 A | B \\ notes", result.tasks().get(0).getName());
        assertFalse(result.tasks().get(0).isDone());
        assertTrue(result.tasks().get(1).isDone());
        assertEquals("submit report (by: Feb 29 2028 08:05)",
                result.tasks().get(1).getDisplayText());
        assertEquals("camp (from: Sep 14 2026 09:00 to: Sep 16 2026 17:30)",
                result.tasks().get(2).getDisplayText());
    }

    @Test
    void save_existingFile_replacesOldContents() throws IOException, DataSavingException {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(dataFile, "old data", StandardCharsets.UTF_8);
        Storage storage = new Storage(dataFile.toString());

        storage.save(List.of(new Todo("new task")));

        assertEquals("T | 0 | new task",
                Files.readString(dataFile, StandardCharsets.UTF_8));
    }

    @Test
    void save_emptyList_clearsExistingFile() throws IOException, DataSavingException {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(dataFile, "T | 0 | old task", StandardCharsets.UTF_8);

        new Storage(dataFile.toString()).save(List.of());

        assertEquals("", Files.readString(dataFile, StandardCharsets.UTF_8));
    }

    @Test
    void load_malformedRecords_reportsEveryErrorAndKeepsValidRecords()
            throws IOException, DataLoadingException {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        String storedData = String.join(System.lineSeparator(),
                "T | 0 | valid todo",
                "X | 0 | unknown type",
                "T | 2 | invalid status",
                "T | 0 |   ",
                "T | 0 | todo | extra",
                "D | 0 | deadline",
                "D | 0 | deadline |   ",
                "E | 0 | event | 2026-09-14 09:00",
                "E | 0 | event |   | 2026-09-14 10:00",
                "E | 0 | event | 2026-09-14 09:00 |   ",
                "D | 0 | invalid date | 2025-02-29 10:00",
                "E | 0 | reversed event | 2026-09-14 11:00 | 2026-09-14 10:00",
                "D | 1 | valid deadline | 2026-09-14 10:00");
        Files.writeString(dataFile, storedData, StandardCharsets.UTF_8);

        Storage.LoadResult result = new Storage(dataFile.toString()).load();

        assertEquals(2, result.tasks().size());
        assertEquals("valid todo", result.tasks().get(0).getName());
        assertEquals("valid deadline", result.tasks().get(1).getName());
        assertTrue(result.tasks().get(1).isDone());
        assertEquals(11, result.errors().size());
        assertEquals("Line 2 has an invalid task type; expected T, D, or E.",
                result.errors().get(0).getMessage());
        assertEquals("Line 3 has an invalid completion status; expected 0 or 1.",
                result.errors().get(1).getMessage());
        assertEquals("Line 4 has no task description.",
                result.errors().get(2).getMessage());
        assertInstanceOf(InvalidDateException.class, result.errors().get(9));
        assertInstanceOf(InvalidEventPeriodException.class, result.errors().get(10));
    }

    @Test
    void load_escapeSequences_decodesRecognizedAndPreservesUnknownEscapes()
            throws IOException, DataLoadingException {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        String storedData = "T | 0 | pipe \\| slash \\\\ unknown \\q trailing \\";
        Files.writeString(dataFile, storedData, StandardCharsets.UTF_8);

        Storage.LoadResult result = new Storage(dataFile.toString()).load();

        assertTrue(result.errors().isEmpty());
        assertEquals("pipe | slash \\ unknown \\q trailing \\.",
                result.tasks().get(0).getName() + ".");
    }

    @Test
    void loadResult_mutableSourceAndReturnedLists_cannotChangeResult() {
        ArrayList<Task> sourceTasks = new ArrayList<>(List.of(new Todo("one")));
        ArrayList<PandaException> sourceErrors = new ArrayList<>();
        Storage.LoadResult result = new Storage.LoadResult(sourceTasks, sourceErrors);

        sourceTasks.add(new Todo("two"));
        sourceErrors.add(new DataLoadingException(1, "bad data."));

        assertEquals(1, result.tasks().size());
        assertTrue(result.errors().isEmpty());
        assertThrows(UnsupportedOperationException.class, () ->
                result.tasks().add(new Todo("three")));
        assertThrows(UnsupportedOperationException.class, () ->
                result.errors().add(new DataLoadingException(2, "bad data.")));
    }

    @Test
    @EnabledOnOs(OS.WINDOWS)
    void load_directoryPath_throwsDataLoadingExceptionWithCause() throws IOException {
        Path directory = temporaryDirectory.resolve("data-directory");
        Files.createDirectory(directory);

        DataLoadingException exception = assertThrows(DataLoadingException.class, () ->
                new Storage(directory.toString()).load());

        assertTrue(exception.getMessage().contains(directory.toString()));
        assertNotNull(exception.getCause());
    }

    @Test
    void save_blockedParentPath_throwsDataSavingExceptionWithCause() throws IOException {
        Path blockedParent = temporaryDirectory.resolve("not-a-directory");
        Files.writeString(blockedParent, "blocking file", StandardCharsets.UTF_8);
        Path dataFile = blockedParent.resolve("tasks.txt");

        DataSavingException exception = assertThrows(DataSavingException.class, () ->
                new Storage(dataFile.toString()).save(List.of(new Todo("task"))));

        assertEquals("OOPS!!! This panda could not save its bamboo archive.",
                exception.getMessage());
        assertNotNull(exception.getCause());
    }
}
