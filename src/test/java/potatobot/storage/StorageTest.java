package potatobot.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import potatobot.exception.PotatoBotException;
import potatobot.model.task.Deadline;
import potatobot.model.task.Event;
import potatobot.model.task.Task;
import potatobot.model.task.TaskList;
import potatobot.model.task.Todo;

public class StorageTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    public void saveAndLoad_allTypesAndStatuses_roundTripsUnicodeAndOrder()
            throws IOException, PotatoBotException {
        Storage storage = new Storage(temporaryDirectory.resolve("round-trip.txt").toString(), "shown.txt");
        TaskList tasks = new TaskList();
        tasks.add(new Task("买土豆 🥔"), new Todo("read (Todo)"),
                new Deadline("report (Deadline, by: draft)", "2024-02-29"),
                new Event("meeting (Event, from: draft)", "2026-12-31", "2027-01-01"));
        tasks.markDone(0);
        tasks.markDone(2);
        tasks.markDone(3);
        storage.save(tasks);
        List<Task> restored = storage.load();
        assertEquals("shown.txt", storage.getDisplayFilePath());
        assertEquals(tasks.size(), restored.size());
        for (int i = 0; i < tasks.size(); i++) {
            assertEquals(tasks.get(i).getClass(), restored.get(i).getClass());
            assertEquals(tasks.get(i).toString(), restored.get(i).toString());
            assertEquals(tasks.get(i).getStatusIcon(), restored.get(i).getStatusIcon());
        }
    }

    @Test
    public void save_emptyList_clearsPreviousContents() throws IOException, PotatoBotException {
        Path saveFile = temporaryDirectory.resolve("empty.txt");
        Files.writeString(saveFile, "[X] obsolete");
        Storage storage = new Storage(saveFile.toString());
        storage.save(new TaskList());
        assertEquals("", Files.readString(saveFile));
        assertTrue(storage.load().isEmpty());
        assertEquals(saveFile.toString(), storage.getDisplayFilePath());
    }

    @Test
    public void load_exactCapacity_acceptsAllTasks() throws IOException, PotatoBotException {
        Path saveFile = temporaryDirectory.resolve("full.txt");
        Files.writeString(saveFile, String.join("\n", Collections.nCopies(TaskList.MAX_SIZE, "[ ] task")));
        assertEquals(TaskList.MAX_SIZE, new Storage(saveFile.toString()).load().size());
    }

    @Test
    public void load_invalidStoredDates_rejectsFile() throws IOException {
        Path saveFile = temporaryDirectory.resolve("invalid.txt");
        for (String details : new String[] {
            "report (Deadline, by: invalid)", "meeting (Event, from: invalid to: Sep 02 2026)",
            "meeting (Event, from: Sep 02 2026 to: invalid)",
            "meeting (Event, from: Sep 02 2026 to: Sep 01 2026)"
        }) {
            Files.writeString(saveFile, "[ ] " + details);
            assertThrows(PotatoBotException.class, () -> new Storage(saveFile.toString()).load());
        }
    }

    @Test
    public void load_markersWithoutClosingParenthesis_preservesPlainTask() throws IOException, PotatoBotException {
        Path saveFile = temporaryDirectory.resolve("plain.txt");
        Files.writeString(saveFile, "[ ] text (Deadline, by: draft\n[ ] text (Event, from: draft\n[ ] plain)");
        List<Task> tasks = new Storage(saveFile.toString()).load();
        assertEquals(3, tasks.size());
        assertEquals("text (Deadline, by: draft", tasks.get(0).toString());
        assertEquals("text (Event, from: draft", tasks.get(1).toString());
        assertEquals("plain)", tasks.get(2).toString());
        for (Task task : tasks) {
            assertEquals(Task.class, task.getClass());
        }
    }

    @Test
    public void load_directoryInsteadOfFile_reportsIoFailure() {
        assertThrows(IOException.class, () -> new Storage(temporaryDirectory.toString()).load());
    }

    @Test
    public void save_invalidFileOrParent_reportsIoFailure() throws IOException {
        assertThrows(IOException.class,
                () -> new Storage(temporaryDirectory.toString()).save(new TaskList()));
        Path parentFile = temporaryDirectory.resolve("parent-file");
        Files.writeString(parentFile, "preserve me");
        assertThrows(IOException.class,
                () -> new Storage(parentFile.resolve("tasks.txt").toString()).save(new TaskList()));
        assertEquals("preserve me", Files.readString(parentFile));
    }

    @Test
    public void load_missingFile_emptyListReturned() throws IOException, PotatoBotException {
        Storage storage = new Storage(temporaryDirectory.resolve("missing.txt").toString());

        List<Task> loadedTasks = storage.load();

        assertTrue(loadedTasks.isEmpty());
    }

    @Test
    public void save_nestedFile_fileCreatedWithTaskContents()
            throws IOException, PotatoBotException {
        Path saveFile = temporaryDirectory.resolve("nested/data/tasks.txt");
        Storage storage = new Storage(saveFile.toString());
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        storage.save(tasks);

        assertEquals("[ ] read book (Todo)", Files.readString(saveFile));
    }

    @Test
    public void save_existingFile_previousContentsReplaced()
            throws IOException, PotatoBotException {
        Path saveFile = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(saveFile, "obsolete data that must be removed");
        Storage storage = new Storage(saveFile.toString());
        TaskList tasks = new TaskList();
        tasks.add(new Task("replacement"));

        storage.save(tasks);

        assertEquals("[ ] replacement", Files.readString(saveFile));
    }

    @Test
    public void load_validFile_taskTypesAndStatusesRestored()
            throws IOException, PotatoBotException {
        Path saveFile = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(
                saveFile,
                "[X] read book (Todo)\n"
                        + "[ ] submit report (Deadline, by: Aug 31 2026)\n"
                        + "[ ] conference (Event, from: Aug 31 2026 to: Sep 02 2026)");
        Storage storage = new Storage(saveFile.toString());

        List<Task> loadedTasks = storage.load();

        assertEquals(3, loadedTasks.size());
        assertInstanceOf(Todo.class, loadedTasks.get(0));
        assertInstanceOf(Deadline.class, loadedTasks.get(1));
        assertInstanceOf(Event.class, loadedTasks.get(2));
        assertEquals("X", loadedTasks.get(0).getStatusIcon());
        assertEquals(" ", loadedTasks.get(1).getStatusIcon());
        assertEquals(" ", loadedTasks.get(2).getStatusIcon());
    }

    @Test
    public void load_fileContainingBlankLines_blankLinesIgnored()
            throws IOException, PotatoBotException {
        Path saveFile = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(saveFile, "\n[X] first\n\n[ ] second\n");
        Storage storage = new Storage(saveFile.toString());

        List<Task> loadedTasks = storage.load();

        assertEquals(2, loadedTasks.size());
        assertEquals("first", loadedTasks.get(0).toString());
        assertEquals("second", loadedTasks.get(1).toString());
    }

    @Test
    public void load_invalidCompletionStatus_exceptionThrown() throws IOException {
        Path saveFile = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(saveFile, "[?] corrupted task");
        Storage storage = new Storage(saveFile.toString());

        PotatoBotException exception = assertThrows(
                PotatoBotException.class, storage::load);

        assertEquals(
                "Invalid completion status in line: [?] corrupted task",
                exception.getMessage());
    }

    @Test
    public void load_incompleteEventDetails_exceptionThrown() throws IOException {
        Path saveFile = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(saveFile, "[ ] conference (Event, from: Aug 31 2026)");
        Storage storage = new Storage(saveFile.toString());

        assertThrows(PotatoBotException.class, storage::load);
    }

    @Test
    public void load_moreThanMaximumTasks_exceptionThrown() throws IOException {
        Path saveFile = temporaryDirectory.resolve("tasks.txt");
        String fileContents = String.join(
                System.lineSeparator(),
                Collections.nCopies(TaskList.MAX_SIZE + 1, "[ ] task"));
        Files.writeString(saveFile, fileContents);
        Storage storage = new Storage(saveFile.toString());

        PotatoBotException exception = assertThrows(
                PotatoBotException.class, storage::load);

        assertEquals(
                "The save file contains more than 100 tasks.",
                exception.getMessage());
    }
}
