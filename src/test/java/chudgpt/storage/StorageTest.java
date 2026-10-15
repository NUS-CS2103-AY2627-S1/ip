package chudgpt.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import chudgpt.exception.ChudException;
import chudgpt.task.Priority;
import chudgpt.task.Task;
import chudgpt.task.TaskList;
import chudgpt.task.ToDo;

/** Tests loading valid and malformed saved task records. */
public class StorageTest {

    @TempDir
    private Path temporaryDirectory;

    @Test
    public void load_validTaskTypes_tasksLoadedWithCompletionStatus() throws IOException, ChudException {
        Path saveFile = temporaryDirectory.resolve("tasks.txt");
        Files.write(saveFile, List.of(
                "T | 1 | read book",
                "D | 0 | return book | 2026-09-20",
                "E | 1 | project meeting | 2026-09-21 | 2026-09-22"));

        List<Task> tasks = new Storage(saveFile.toString()).load();

        assertEquals(3, tasks.size());
        assertEquals("[T][X][P:NONE   ] read book", tasks.get(0).toString());
        assertEquals("[D][ ][P:NONE   ] return book (by: 2026-09-20)", tasks.get(1).toString());
        assertEquals("[E][X][P:NONE   ] project meeting (from: 2026-09-21 to: 2026-09-22)", tasks.get(2).toString());
    }

    @Test
    public void load_malformedRecords_onlyValidTasksLoaded() throws IOException, ChudException {
        Path saveFile = temporaryDirectory.resolve("tasks.txt");
        Files.write(saveFile, List.of(
                "missing fields",
                "T | 2 | invalid status",
                "T | 0 | ",
                "D | 0 | missing date",
                "E | 0 | missing end | 2026-09-21",
                "X | 0 | unknown type",
                "T | 0 | valid task"));

        List<Task> tasks = new Storage(saveFile.toString()).load();

        assertEquals(1, tasks.size());
        assertEquals("[T][ ][P:NONE   ] valid task", tasks.get(0).toString());
    }

    @Test
    public void load_mixedFormats_migratesOnlyOnSaveAndRoundTrips() throws IOException, ChudException {
        Path saveFile = temporaryDirectory.resolve("tasks.txt");
        String original = String.join("\n", "T | 1 | HIGH", "D | 0 | old deadline | 2026-10-01",
                "E | 1 | old event | 2026-10-01 | 2026-10-02", "T | 0 | HIGH | new todo",
                "D | 1 | EXTREME | new deadline | 2026-10-03",
                "E | 0 | LOW | new event | 2026-10-04 | 2026-10-05",
                "T | 0 | MEDIUM | medium", "T | 0 | NONE | none");
        Files.writeString(saveFile, original);
        Storage storage = new Storage(saveFile.toString());

        TaskList tasks = new TaskList(storage.load());

        assertEquals(original, Files.readString(saveFile));
        assertEquals(8, tasks.size());
        assertEquals("[T][X][P:NONE   ] HIGH", tasks.getTask(0).toString());
        assertEquals(Priority.NONE, tasks.getTask(1).getPriority());
        assertEquals(Priority.NONE, tasks.getTask(2).getPriority());
        assertEquals(Priority.HIGH, tasks.getTask(3).getPriority());
        assertEquals(Priority.EXTREME, tasks.getTask(4).getPriority());
        assertEquals(Priority.LOW, tasks.getTask(5).getPriority());
        assertEquals(Priority.MEDIUM, tasks.getTask(6).getPriority());
        assertEquals(Priority.NONE, tasks.getTask(7).getPriority());

        storage.save(tasks);

        assertEquals(String.join(System.lineSeparator(), "T | 1 | NONE | HIGH",
                "D | 0 | NONE | old deadline | 2026-10-01",
                "E | 1 | NONE | old event | 2026-10-01 | 2026-10-02", "T | 0 | HIGH | new todo",
                "D | 1 | EXTREME | new deadline | 2026-10-03",
                "E | 0 | LOW | new event | 2026-10-04 | 2026-10-05",
                "T | 0 | MEDIUM | medium", "T | 0 | NONE | none"), Files.readString(saveFile));
        assertEquals(tasks.toString(), new TaskList(storage.load()).toString());
    }

    @Test
    public void load_malformedNewRecords_skipsOnlyMalformedRecords() throws IOException, ChudException {
        Path saveFile = temporaryDirectory.resolve("tasks.txt");
        Files.write(saveFile, List.of("T | 0 | HIGH | before", "T | 0 | | empty priority",
                "T | 0 | high | lowercase priority",
                "D | 0 | URGENT | bad priority | 2026-10-01",
                "E | 0 | 1 | bad priority | 2026-10-01 | 2026-10-02",
                "T | 2 | HIGH | invalid status", "T | 0 | HIGH | ",
                "T | 0 | HIGH | extra | field", "X | 0 | HIGH | unknown type",
                "D | 0 | LOW | missing date | ", "E | 0 | HIGH | missing end | 2026-10-01 | ",
                "T | 0 | LOW | after"));

        TaskList tasks = new TaskList(new Storage(saveFile.toString()).load());

        assertEquals("1. [T][ ][P:HIGH   ] before\n2. [T][ ][P:LOW    ] after", tasks.toString());
    }

    @Test
    public void load_invalidDate_skipsRecordAndReportsLineWarning() throws IOException, ChudException {
        Path saveFile = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(saveFile, "D | 0 | HIGH | book | 2026-02-30");

        Storage.LoadResult result = new Storage(saveFile.toString()).loadWithWarnings();

        assertTrue(result.tasks().isEmpty());
        assertEquals(List.of("Line 1 was skipped: OOPS!!! Input a date in the format yyyy-mm-dd"),
                result.warnings());
    }

    @Test
    public void load_malformedAndDuplicateRecords_loadsValidRecordsAndCreatesBackupOnSave() throws Exception {
        Path saveFile = temporaryDirectory.resolve("tasks.txt");
        String original = String.join("\n", "T | 0 | NONE | first", "malformed",
                "T | 1 | HIGH | FIRST", "E | 0 | NONE | event | 2026-10-02 | 2026-10-01",
                "T | 0 | LOW | last");
        Files.writeString(saveFile, original);
        Storage storage = new Storage(saveFile.toString());

        Storage.LoadResult result = storage.loadWithWarnings();

        assertEquals(2, result.tasks().size());
        assertEquals(List.of(
                "Line 2 was skipped: The record does not contain enough fields.",
                "Line 3 duplicates the task on line 1 and was skipped.",
                "Line 4 was skipped: Event end date must be later than its start date."), result.warnings());

        storage.save(new TaskList(result.tasks()));

        assertEquals(original, Files.readString(temporaryDirectory.resolve("tasks.txt.bak")));
        assertEquals("T | 0 | NONE | first" + System.lineSeparator() + "T | 0 | LOW | last",
                Files.readString(saveFile));
    }

    @Test
    public void save_targetIsDirectory_exceptionThrownWithoutSuccessResult() throws IOException {
        Path saveTarget = temporaryDirectory.resolve("tasks");
        Files.createDirectory(saveTarget);
        Storage storage = new Storage(saveTarget.toString());

        ChudException exception = assertThrows(ChudException.class, () ->
                storage.save(new TaskList(List.of(new ToDo("task")))));

        assertTrue(exception.getMessage().startsWith("Could not save tasks to "));
        assertTrue(exception.getMessage().endsWith("Your changes are still available in this session."));
    }

    @Test
    public void save_shortFileName_savesSuccessfully() throws Exception {
        Path saveFile = temporaryDirectory.resolve("x");
        Storage storage = new Storage(saveFile.toString());

        storage.save(new TaskList(List.of(new ToDo("task"))));

        assertEquals("T | 0 | NONE | task", Files.readString(saveFile));
    }

    @Test
    public void constructor_nullOrBlankPath_assertionErrorThrown() {
        assertThrows(AssertionError.class, () -> new Storage(null));
        assertThrows(AssertionError.class, () -> new Storage(""));
        assertThrows(AssertionError.class, () -> new Storage("   "));
    }

    @Test
    public void load_missingFile_returnsImmutableEmptyResult() throws Exception {
        Storage.LoadResult result = new Storage(temporaryDirectory.resolve("missing.txt").toString())
                .loadWithWarnings();

        assertTrue(result.tasks().isEmpty());
        assertTrue(result.warnings().isEmpty());
        assertThrows(UnsupportedOperationException.class, () -> result.tasks().add(new ToDo("task")));
        assertThrows(UnsupportedOperationException.class, () -> result.warnings().add("warning"));
    }

    @Test
    public void loadResult_mutableInputs_copiesInputsDefensively() {
        List<Task> tasks = new ArrayList<>(List.of(new ToDo("task")));
        List<String> warnings = new ArrayList<>(List.of("warning"));

        Storage.LoadResult result = new Storage.LoadResult(tasks, warnings);
        tasks.clear();
        warnings.clear();

        assertEquals(1, result.tasks().size());
        assertEquals(List.of("warning"), result.warnings());
    }

    @Test
    public void load_pathIsDirectory_exceptionThrown() throws Exception {
        Path directory = temporaryDirectory.resolve("tasks");
        Files.createDirectory(directory);

        ChudException exception = assertThrows(ChudException.class, () ->
                new Storage(directory.toString()).load());

        assertTrue(exception.getMessage().startsWith("The task data path is not a regular file: "));
    }

    @Test
    public void save_nullTaskList_assertionErrorThrownWithoutCreatingFile() {
        Path saveFile = temporaryDirectory.resolve("tasks.txt");
        Storage storage = new Storage(saveFile.toString());

        assertThrows(AssertionError.class, () -> storage.save(null));
        assertFalse(Files.exists(saveFile));
    }
}
