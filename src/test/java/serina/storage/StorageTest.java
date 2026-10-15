package serina.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import serina.exception.SerinaException;
import serina.task.Deadline;
import serina.task.Event;
import serina.task.Task;
import serina.task.TaskStatus;
import serina.task.Todo;

/** Tests persistence failures and save-file validation. */
public class StorageTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    public void loadTasks_invalidSecondRecord_reportsLineNumber() throws IOException {
        Path saveFile = temporaryDirectory.resolve("serina.txt");
        Files.write(saveFile, List.of("T | 0 | valid", "invalid"));

        SerinaException exception = assertThrows(SerinaException.class, () ->
                new Storage(saveFile).loadTasks());

        assertEquals("Captain Cutter, I couldn't load your saved tasks. Invalid record on line 2.",
                exception.getMessage());
    }

    @Test
    public void loadTasks_duplicateRecords_throwsSerinaException() throws IOException {
        Path saveFile = temporaryDirectory.resolve("serina.txt");
        Files.write(saveFile, List.of("T | 0 | Inspect engines", "T | 1 | inspect   ENGINES"));

        assertThrows(SerinaException.class, () -> new Storage(saveFile).loadTasks());
    }

    @Test
    public void saveAndLoad_specialCharacters_roundTrips() throws SerinaException {
        Path saveFile = temporaryDirectory.resolve("nested").resolve("serina.txt");
        Storage storage = new Storage(saveFile);
        List<Task> tasks = List.of(new Todo("review | path \\ notes"));

        storage.saveTasks(tasks);

        assertEquals(tasks.get(0).toString(), storage.loadTasks().get(0).toString());
    }

    @Test
    public void loadTasks_directoryPath_throwsSerinaException() {
        assertThrows(SerinaException.class, () -> new Storage(temporaryDirectory).loadTasks());
    }

    @Test
    public void loadTasks_missingEmptyAndBlankFiles_returnsEmptyList() throws IOException, SerinaException {
        Path missingFile = temporaryDirectory.resolve("missing.txt");
        Path emptyFile = temporaryDirectory.resolve("empty.txt");
        Path blankFile = temporaryDirectory.resolve("blank.txt");
        Files.createFile(emptyFile);
        Files.writeString(blankFile, "\n  \n\t\n");

        assertEquals(List.of(), new Storage(missingFile).loadTasks());
        assertEquals(List.of(), new Storage(emptyFile).loadTasks());
        assertEquals(List.of(), new Storage(blankFile).loadTasks());
    }

    @Test
    public void saveAndLoad_allTaskTypesAndUnicode_preservesOrderAndState() throws SerinaException {
        Path saveFile = temporaryDirectory.resolve("任务 data").resolve("serina.txt");
        Storage storage = new Storage(saveFile);
        List<Task> tasks = List.of(
                new Todo("检查引擎 🚀", TaskStatus.DONE),
                new Deadline("submit report", LocalDate.of(2028, 2, 29)),
                new Event("mission", LocalDate.of(2026, 9, 14),
                        LocalDate.of(2026, 9, 15), TaskStatus.DONE));

        storage.saveTasks(tasks);
        List<Task> loadedTasks = storage.loadTasks();

        assertEquals(tasks.stream().map(Task::toFileString).toList(),
                loadedTasks.stream().map(Task::toFileString).toList());
        assertInstanceOf(Todo.class, loadedTasks.get(0));
        assertInstanceOf(Deadline.class, loadedTasks.get(1));
        assertInstanceOf(Event.class, loadedTasks.get(2));
    }

    @Test
    public void saveTasks_existingFile_replacesContentAndLeavesNoTemporaryFile() throws IOException, SerinaException {
        Path saveFile = temporaryDirectory.resolve("serina.txt");
        Files.writeString(saveFile, "old content");
        Storage storage = new Storage(saveFile);

        storage.saveTasks(List.of(new Todo("replacement")));

        assertEquals("T | 0 | replacement\n", Files.readString(saveFile));
        try (var files = Files.list(temporaryDirectory)) {
            assertFalse(files.anyMatch(path -> path.getFileName().toString().startsWith("serina-")));
        }

        storage.saveTasks(List.of());
        assertEquals("", Files.readString(saveFile));
    }

    @Test
    public void loadTasks_blankLinesBeforeInvalidRecord_reportsPhysicalLineNumber() throws IOException {
        Path saveFile = temporaryDirectory.resolve("serina.txt");
        Files.writeString(saveFile, "\nT | 0 | valid\n\ninvalid\n");

        SerinaException exception = assertThrows(SerinaException.class, () ->
                new Storage(saveFile).loadTasks());

        assertTrue(exception.getMessage().endsWith("Invalid record on line 4."));
    }

    @Test
    public void loadTasks_malformedRecords_rejectsEveryInvalidShape() throws IOException {
        List<String> invalidRecords = List.of(
                "X | 0 | task", "T | 2 | task", "T | 0", "T | 0 | ", "T | 0 | task | extra",
                "D | 0 | task", "D | 0 | task | ", "D | 0 | task | 2026-02-30",
                "E | 0 | task | 2026-09-14", "E | 0 | task | 2026-09-14 | 2026-09-14",
                "E | 0 | task | 2026-09-15 | 2026-09-14", "E | 0 | task | 2026-09-14 | ");

        for (int index = 0; index < invalidRecords.size(); index++) {
            Path saveFile = temporaryDirectory.resolve("invalid-" + index + ".txt");
            Files.writeString(saveFile, invalidRecords.get(index));
            assertThrows(SerinaException.class, () -> new Storage(saveFile).loadTasks(),
                    invalidRecords.get(index));
        }
    }

    @Test
    public void loadTasks_capacityBoundary_acceptsOneHundredAndRejectsOneHundredOne()
            throws IOException, SerinaException {
        List<String> records = new java.util.ArrayList<>();
        for (int taskNumber = 1; taskNumber <= 101; taskNumber++) {
            records.add("T | 0 | task " + taskNumber);
        }
        Path oneHundredFile = temporaryDirectory.resolve("100.txt");
        Path oneHundredOneFile = temporaryDirectory.resolve("101.txt");
        Files.write(oneHundredFile, records.subList(0, 100));
        Files.write(oneHundredOneFile, records);

        assertEquals(100, new Storage(oneHundredFile).loadTasks().size());
        assertThrows(SerinaException.class, () -> new Storage(oneHundredOneFile).loadTasks());
    }

    @Test
    public void saveTasks_parentIsFile_retainsCauseAndOriginalFile() throws IOException {
        Path parentFile = temporaryDirectory.resolve("parent");
        Files.writeString(parentFile, "unchanged");
        Storage storage = new Storage(parentFile.resolve("serina.txt"));

        SerinaException exception = assertThrows(SerinaException.class, () ->
                storage.saveTasks(List.of(new Todo("task"))));

        assertTrue(exception.getCause() instanceof IOException);
        assertEquals("unchanged", Files.readString(parentFile));
    }
}
