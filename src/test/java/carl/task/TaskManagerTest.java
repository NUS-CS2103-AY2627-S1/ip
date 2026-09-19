package carl.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import carl.commands.TodoCommand;
import carl.exceptions.CarlStorageException;
import carl.ui.Ui;

public class TaskManagerTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    public void createSave_missingFile_createsEmptySaveFile() {
        Path saveFile = temporaryDirectory.resolve("data/save.txt");
        TaskManager manager = new TaskManager(saveFile);

        List<Task> tasks = manager.createSave();

        assertTrue(Files.exists(saveFile));
        assertTrue(tasks.isEmpty());
        assertNull(manager.getStartupWarning());
    }

    @Test
    public void loadSave_allValidTaskTypesAndStatuses_restoresExactData() throws Exception {
        Path saveFile = temporaryDirectory.resolve("save.txt");
        Files.writeString(saveFile, """
                T | 1 | completed todo
                D | 0 | pending deadline | 2026-10-01 1800
                E | 1 | completed event | 2026-10-01 1800 | 2026-10-01 1900
                """, StandardCharsets.UTF_8);
        TaskManager manager = new TaskManager(saveFile);

        List<Task> tasks = manager.loadSave();

        assertEquals(List.of(
                "T | 1 | completed todo",
                "D | 0 | pending deadline | 2026-10-01 1800",
                "E | 1 | completed event | 2026-10-01 1800 | 2026-10-01 1900"),
                tasks.stream().map(Task::toSaveFormat).toList());
        assertNull(manager.getStartupWarning());
    }

    @Test
    public void loadSave_malformedAndDuplicateLines_skipsOnlyInvalidLines() throws Exception {
        Path saveFile = temporaryDirectory.resolve("save.txt");
        Files.writeString(saveFile, """
                T | 0 | valid task
                D | 0 | impossible date | 2026-02-30 1800
                T | 1 | valid task
                E | 0 | zero length | 2026-10-01 1800 | 2026-10-01 1800
                """, StandardCharsets.UTF_8);
        TaskManager manager = new TaskManager(saveFile);

        List<Task> tasks = manager.loadSave();

        assertEquals(1, tasks.size());
        assertNotNull(manager.getStartupWarning());
        assertTrue(manager.getStartupWarning().contains("[2, 3, 4]"));
    }

    @Test
    public void loadSave_invalidTypesStatusesAndFieldCounts_skipsEveryInvalidLine() throws Exception {
        Path saveFile = temporaryDirectory.resolve("save.txt");
        Files.writeString(saveFile, """
                X | 0 | unknown type
                T | 2 | unknown status
                T | 0
                T | 0 | name | extra
                D | 0 | missing date
                E | 0 | missing end | 2026-10-01 1800
                """, StandardCharsets.UTF_8);
        TaskManager manager = new TaskManager(saveFile);

        List<Task> tasks = manager.loadSave();

        assertTrue(tasks.isEmpty());
        assertTrue(manager.getStartupWarning().contains("[1, 2, 3, 4, 5, 6]"));
    }

    @Test
    public void loadSave_missingFile_returnsEmptyListAndWarning() {
        TaskManager manager = new TaskManager(temporaryDirectory.resolve("missing.txt"));

        List<Task> tasks = manager.loadSave();

        assertTrue(tasks.isEmpty());
        assertTrue(manager.getStartupWarning().contains("could not read"));
    }

    @Test
    public void createSave_pathIsExistingDirectory_returnsEmptyListAndWarning() {
        TaskManager manager = new TaskManager(temporaryDirectory);

        List<Task> tasks = manager.createSave();

        assertTrue(tasks.isEmpty());
        assertNotNull(manager.getStartupWarning());
    }

    @Test
    public void saveAll_validTaskList_replacesFile() throws Exception {
        Path saveFile = temporaryDirectory.resolve("save.txt");
        TaskManager manager = new TaskManager(saveFile);
        TaskList tasks = new TaskList(List.of(new Todo("write tests")));

        manager.saveAll(tasks);

        assertEquals(tasks.toSaveString(), Files.readString(saveFile, StandardCharsets.UTF_8));
        assertFalse(Files.readString(saveFile, StandardCharsets.UTF_8).isBlank());
    }

    @Test
    public void saveAll_emptyTaskList_writesEmptyFile() throws Exception {
        Path saveFile = temporaryDirectory.resolve("save.txt");
        TaskManager manager = new TaskManager(saveFile);

        manager.saveAll(new TaskList(List.of()));

        assertEquals("", Files.readString(saveFile, StandardCharsets.UTF_8));
    }

    @Test
    public void saveAll_destinationIsDirectory_throwsStorageException() {
        TaskManager manager = new TaskManager(temporaryDirectory);
        TaskList tasks = new TaskList(List.of(new Todo("write tests")));

        assertThrows(CarlStorageException.class, () -> manager.saveAll(tasks));
    }

    @Test
    public void modifyingCommand_saveFails_restoresTaskList() {
        TaskManager manager = new TaskManager(temporaryDirectory);
        TaskList tasks = new TaskList(List.of());
        TodoCommand command = new TodoCommand("write tests");

        assertThrows(CarlStorageException.class, () ->
                command.onRun(new Ui(), manager, tasks, "todo write tests"));
        assertTrue(tasks.getAllTasks().isEmpty());
    }

    @Test
    public void constructor_nullPath_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new TaskManager(null));
    }
}
