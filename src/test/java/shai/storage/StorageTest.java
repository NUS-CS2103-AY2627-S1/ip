package shai.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import shai.exception.ShaiException;
import shai.task.Deadline;
import shai.task.Event;
import shai.task.Reminder;
import shai.task.TaskList;
import shai.task.ToDo;

/** Tests persistence, escaping, restoration, and malformed-data handling. */
class StorageTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void loadTasks_missingFile_returnsEmptyTaskList() throws ShaiException {
        Storage storage = storage();

        TaskList tasks = storage.loadTasks();

        assertEquals(0, tasks.size());
    }

    @Test
    void saveAndLoadTasks_allTaskTypes_roundTripsFieldsAndStatus() throws ShaiException {
        TaskList original = new TaskList();
        ToDo toDo = new ToDo("review | draft \\ backup");
        toDo.markAsDone();
        Deadline deadline = new Deadline("return book", LocalDateTime.of(2019, 12, 2, 18, 0));
        Event event = new Event("team meeting", LocalDateTime.of(2019, 12, 3, 14, 0),
                LocalDateTime.of(2019, 12, 3, 16, 0));
        original.add(toDo);
        original.add(deadline);
        original.add(event);

        Storage storage = storage();
        storage.saveTasks(original);
        TaskList loaded = storage.loadTasks();

        assertEquals(3, loaded.size());
        assertEquals(toDo.toString(), loaded.get(0).toString());
        assertTrue(loaded.get(0).isDone());
        assertEquals(deadline.toString(), loaded.get(1).toString());
        assertEquals(event.toString(), loaded.get(2).toString());
        assertFalse(loaded.get(1).isDone());
        assertFalse(loaded.get(2).isDone());
    }

    @Test
    void saveAndLoadTasks_customReminders_roundTripsReminderSettings() throws ShaiException {
        TaskList original = new TaskList();
        original.add(new Deadline("submit report", LocalDateTime.of(2026, 9, 15, 17, 0), 180));
        original.add(new Event("team meeting", LocalDateTime.of(2026, 9, 18, 14, 0),
                LocalDateTime.of(2026, 9, 18, 16, 0), Reminder.DISABLED));

        Storage storage = storage();
        storage.saveTasks(original);
        TaskList loaded = storage.loadTasks();

        assertEquals(180, ((Deadline) loaded.get(0)).getReminderMinutesBefore());
        assertEquals(Reminder.DISABLED, ((Event) loaded.get(1)).getReminderMinutesBefore());
    }

    @Test
    void saveTasks_existingFile_replacesPreviousContents() throws ShaiException {
        Storage storage = storage();
        TaskList first = new TaskList();
        first.add(new ToDo("first task"));
        storage.saveTasks(first);

        TaskList second = new TaskList();
        second.add(new ToDo("second task"));
        storage.saveTasks(second);

        TaskList loaded = storage.loadTasks();
        assertEquals(1, loaded.size());
        assertEquals("second task", loaded.get(0).getDescription());
    }

    @Test
    void loadTasks_legacyTimedTasks_receiveDefaultReminders() throws Exception {
        Path file = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(file, String.join(System.lineSeparator(),
                "D | 0 | submit report | 2026-09-15 1700",
                "E | 0 | team meeting | 2026-09-18 1400 | 2026-09-18 1600"));

        TaskList loaded = new Storage(file.toString()).loadTasks();

        assertEquals(Reminder.DEFAULT_MINUTES_BEFORE, ((Deadline) loaded.get(0)).getReminderMinutesBefore());
        assertEquals(Reminder.DEFAULT_MINUTES_BEFORE, ((Event) loaded.get(1)).getReminderMinutesBefore());
    }

    @Test
    void loadTasks_malformedLine_throwsLineSpecificError() throws Exception {
        Path file = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(file, "X | 0 | invalid task\n");

        ShaiException exception = assertThrows(ShaiException.class, () -> new Storage(file.toString()).loadTasks());

        assertEquals("I couldn't load your lineup, King. The play on line 1 is invalid.", exception.getMessage());
    }

    @Test
    void loadTasks_unknownEscapeSequence_throwsLineSpecificError() throws Exception {
        Path file = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(file, "T | 0 | invalid \\q\n");

        ShaiException exception = assertThrows(ShaiException.class, () -> new Storage(file.toString()).loadTasks());

        assertEquals("I couldn't load your lineup, King. The play on line 1 is invalid.", exception.getMessage());
    }

    @Test
    void loadTasks_malformedRecords_rejectsInvalidFields() throws Exception {
        Path file = temporaryDirectory.resolve("tasks.txt");
        List<String> malformedLines = List.of(
                "T | 2 | invalid status",
                "T | 0 | ",
                "T | 0 | too many fields | extra",
                "D | 0 | deadline | not-a-date",
                "D | 0 | deadline | 2026-09-15 1700 | 2147483648",
                "E | 0 | event | 2026-09-15 1600 | 2026-09-15 1400",
                "E | 0 | event | 2026-09-15 1400 | 2026-09-15 1600 | invalid");

        for (String malformedLine : malformedLines) {
            Files.writeString(file, malformedLine);

            ShaiException exception = assertThrows(ShaiException.class, () ->
                    new Storage(file.toString()).loadTasks());

            assertEquals("I couldn't load your lineup, King. The play on line 1 is invalid.",
                    exception.getMessage());
        }
    }

    @Test
    void loadTasks_trailingEscapeCharacter_rejectsMalformedLine() throws Exception {
        Path file = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(file, "T | 0 | description\\");

        ShaiException exception = assertThrows(ShaiException.class, () ->
                new Storage(file.toString()).loadTasks());

        assertEquals("I couldn't load your lineup, King. The play on line 1 is invalid.",
                exception.getMessage());
    }

    @Test
    void loadTasks_directoryPath_throwsUsefulError() {
        ShaiException exception = assertThrows(ShaiException.class, () ->
                new Storage(temporaryDirectory.toString()).loadTasks());

        assertEquals("I couldn't load your lineup, King. Check the task file.", exception.getMessage());
    }

    @Test
    void saveTasks_nullTaskList_throwsUsefulError() {
        ShaiException exception = assertThrows(ShaiException.class, () -> storage().saveTasks(null));

        assertEquals("I couldn't save your lineup, King. Check the task file.", exception.getMessage());
    }

    private Storage storage() {
        return new Storage(temporaryDirectory.resolve("tasks.txt").toString());
    }
}
