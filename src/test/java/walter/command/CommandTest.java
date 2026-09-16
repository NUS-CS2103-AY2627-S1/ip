package walter.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import walter.DukeException;
import walter.place.Place;
import walter.storage.Storage;
import walter.task.Deadline;
import walter.task.Event;
import walter.task.Task;
import walter.task.TaskList;
import walter.task.Todo;
import walter.ui.Ui;

/**
 * Tests command coordination where it adds coverage beyond TaskList and Storage tests.
 */
public class CommandTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    public void execute_addCommand_taskAddedAndPersisted() throws IOException, DukeException {
        TaskList tasks = new TaskList();
        Storage storage = createStorage();
        Task task = new Todo("read book");

        new AddCommand(task).execute(tasks, new Ui(), storage);

        assertSame(task, tasks.get(0));
        assertEquals("read book", storage.load().get(0).getDescription());
    }

    @Test
    public void execute_duplicateTask_rejectedWithoutSavingOrSuccessOutput()
            throws DukeException {
        TaskList tasks = new TaskList();
        RecordingStorage storage = new RecordingStorage(
                temporaryDirectory.resolve("walter.txt"));
        ByteArrayOutputStream outputBytes = new ByteArrayOutputStream();
        Ui ui = new Ui(new PrintStream(outputBytes, true, StandardCharsets.UTF_8));
        Deadline deadline = new Deadline("Submit report", LocalDate.of(2026, 12, 10));
        Event event = new Event("Meeting", "2pm");
        Todo todo = new Todo("Read book");

        new AddCommand(deadline).execute(tasks, ui, storage);
        new AddCommand(event).execute(tasks, ui, storage);
        new AddCommand(todo).execute(tasks, ui, storage);
        assertEquals(3, storage.getSaveCount());
        outputBytes.reset();

        assertThrows(DukeException.class, () ->
                new AddCommand(new Deadline("Submit report", LocalDate.of(2026, 12, 10)))
                        .execute(tasks, ui, storage));
        assertThrows(DukeException.class, () ->
                new AddCommand(new Event("Meeting", "2pm")).execute(tasks, ui, storage));
        assertThrows(DukeException.class, () ->
                new AddCommand(new Todo("read  BOOK")).execute(tasks, ui, storage));

        assertEquals(3, tasks.size());
        assertEquals(3, storage.getSaveCount());
        assertEquals("", outputBytes.toString(StandardCharsets.UTF_8));
    }

    @Test
    public void execute_markAndUnmarkCommands_statusUpdatedAndPersisted()
            throws IOException, DukeException {
        Task task = new Todo("read book");
        TaskList tasks = new TaskList(List.of(task));
        Storage storage = createStorage();

        new MarkCommand(0).execute(tasks, new Ui(), storage);
        assertTrue(task.isDone());
        assertTrue(storage.load().get(0).isDone());

        new UnmarkCommand(0).execute(tasks, new Ui(), storage);
        assertFalse(task.isDone());
        assertFalse(storage.load().get(0).isDone());
    }

    @Test
    public void execute_repeatedStateTransitions_rejectedWithoutSaving() throws DukeException {
        Task task = new Todo("read book");
        TaskList tasks = new TaskList(List.of(task));
        RecordingStorage storage = new RecordingStorage(
                temporaryDirectory.resolve("walter.txt"));

        assertThrows(DukeException.class, () ->
                new UnmarkCommand(0).execute(tasks, new Ui(), storage));
        assertEquals(0, storage.getSaveCount());
        assertFalse(task.isDone());

        new MarkCommand(0).execute(tasks, new Ui(), storage);
        assertEquals(1, storage.getSaveCount());
        assertThrows(DukeException.class, () ->
                new MarkCommand(0).execute(tasks, new Ui(), storage));
        assertEquals(1, storage.getSaveCount());
        assertTrue(task.isDone());

        new UnmarkCommand(0).execute(tasks, new Ui(), storage);
        assertEquals(2, storage.getSaveCount());
        assertThrows(DukeException.class, () ->
                new UnmarkCommand(0).execute(tasks, new Ui(), storage));
        assertEquals(2, storage.getSaveCount());
        assertFalse(task.isDone());
    }

    @Test
    public void execute_deleteCommand_taskRemovedAndPersistenceUpdated()
            throws IOException, DukeException {
        Task first = new Todo("first");
        Task second = new Todo("second");
        TaskList tasks = new TaskList(List.of(first, second));
        Storage storage = createStorage();

        new DeleteCommand(0).execute(tasks, new Ui(), storage);

        assertEquals(1, tasks.size());
        assertSame(second, tasks.get(0));
        assertEquals("second", storage.load().get(0).getDescription());
    }

    @Test
    public void execute_mutatingCommandsWhenSaveFails_stateAndOutputRemainUnchanged()
            throws DukeException {
        Task first = new Todo("first");
        Task second = new Todo("second");
        TaskList tasks = new TaskList(List.of(first, second));
        FailingStorage storage = new FailingStorage(temporaryDirectory.resolve("walter.txt"));
        ByteArrayOutputStream outputBytes = new ByteArrayOutputStream();
        Ui ui = new Ui(new PrintStream(outputBytes, true, StandardCharsets.UTF_8));
        AddCommand addCommand = new AddCommand(new Todo("third"));

        assertThrows(DukeException.class, () -> addCommand.execute(tasks, ui, storage));
        assertThrows(DukeException.class, () -> new DeleteCommand(0).execute(tasks, ui, storage));
        assertThrows(DukeException.class, () -> new MarkCommand(0).execute(tasks, ui, storage));
        second.markAsDone();
        assertThrows(DukeException.class, () -> new UnmarkCommand(1).execute(tasks, ui, storage));

        Storage seededPlaceStorage = new Storage(temporaryDirectory.resolve("walter.txt"));
        Place originalPlace = new Place("home", "original address");
        seededPlaceStorage.savePlaces(List.of(originalPlace));
        assertThrows(DukeException.class, () ->
                new AddPlaceCommand(new Place("work", "new address"))
                        .execute(tasks, ui, storage));
        assertThrows(DukeException.class, () ->
                new DeletePlaceCommand(0).execute(tasks, ui, storage));

        assertEquals(List.of(first, second), tasks.getTasks());
        assertFalse(first.isDone());
        assertTrue(second.isDone());
        assertEquals(List.of("home"), seededPlaceStorage.loadPlaces().stream()
                .map(Place::getName)
                .toList());
        assertEquals("", outputBytes.toString(StandardCharsets.UTF_8));
    }

    @Test
    public void execute_onCommand_taskStateUnchangedAndStorageNotSaved() throws DukeException {
        LocalDate date = LocalDate.of(2026, 8, 30);
        Deadline deadline = new Deadline("submit report", date);
        TaskList tasks = new TaskList(List.of(deadline));
        RecordingStorage storage = new RecordingStorage(
                temporaryDirectory.resolve("walter.txt"));

        new OnCommand(date).execute(tasks, new Ui(), storage);

        assertSame(deadline, tasks.get(0));
        assertEquals(0, storage.getSaveCount());
    }

    @Test
    public void execute_findCommand_taskStateUnchangedAndStorageNotSaved() throws DukeException {
        Task matchingTask = new Todo("Read Book");
        matchingTask.markAsDone();
        Task otherTask = new Todo("buy milk");
        TaskList tasks = new TaskList(List.of(matchingTask, otherTask));
        List<Task> originalTasks = tasks.getTasks();
        RecordingStorage storage = new RecordingStorage(
                this.temporaryDirectory.resolve("walter.txt"));

        new FindCommand("book").execute(tasks, new Ui(), storage);

        assertEquals(originalTasks, tasks.getTasks());
        assertTrue(matchingTask.isDone());
        assertFalse(otherTask.isDone());
        assertEquals(0, storage.getSaveCount());
    }

    @Test
    public void execute_listCommand_tasksDisplayedWithoutSaving() throws DukeException {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));
        RecordingStorage storage = new RecordingStorage(
                temporaryDirectory.resolve("walter.txt"));
        ByteArrayOutputStream outputBytes = new ByteArrayOutputStream();
        Ui ui = new Ui(new PrintStream(outputBytes, true, StandardCharsets.UTF_8));

        new ListCommand().execute(tasks, ui, storage);

        assertEquals("Here are your tasks:\n1. [T][ ] read book\n",
                outputBytes.toString(StandardCharsets.UTF_8));
        assertEquals(0, storage.getSaveCount());
    }

    @Test
    public void execute_exitCommand_goodbyeDisplayedAndExitSignalled() {
        ExitCommand command = new ExitCommand();
        ByteArrayOutputStream outputBytes = new ByteArrayOutputStream();
        Ui ui = new Ui(new PrintStream(outputBytes, true, StandardCharsets.UTF_8));

        command.execute(new TaskList(), ui, createStorage());

        assertTrue(command.isExit());
        assertEquals("All right. The operation is closed. Stay focused, Jesse.\n",
                outputBytes.toString(StandardCharsets.UTF_8));
    }

    @Test
    public void execute_placeCommands_addListAndDeletePersistedPlaces() throws DukeException {
        Storage storage = createStorage();
        TaskList tasks = new TaskList();
        ByteArrayOutputStream outputBytes = new ByteArrayOutputStream();
        Ui ui = new Ui(new PrintStream(outputBytes, true, StandardCharsets.UTF_8));
        Place place = new Place("NUS Library", "12 Computing Drive");

        new AddPlaceCommand(place).execute(tasks, ui, storage);
        new ListPlacesCommand().execute(tasks, ui, storage);
        new DeletePlaceCommand(0).execute(tasks, ui, storage);

        assertTrue(storage.loadPlaces().isEmpty());
        assertEquals("Good. I've recorded this location:\n"
                + "NUS Library — 12 Computing Drive\n"
                + "The location list now contains 1 place.\n"
                + "Here are the recorded locations:\n"
                + "1. NUS Library — 12 Computing Drive\n"
                + "Done. I've removed this location:\n"
                + "NUS Library — 12 Computing Drive\n"
                + "The location list now contains 0 places.\n",
                outputBytes.toString(StandardCharsets.UTF_8));
    }

    /**
     * Returns storage bound to this test's temporary directory.
     */
    private Storage createStorage() {
        return new Storage(temporaryDirectory.resolve("data").resolve("walter.txt"));
    }

    /**
     * Records save attempts so read-only command behavior can be verified without a mocking library.
     */
    private static class RecordingStorage extends Storage {
        private int saveCount;

        RecordingStorage(Path saveFile) {
            super(saveFile);
        }

        @Override
        public void save(List<Task> tasks) {
            saveCount++;
        }

        int getSaveCount() {
            return saveCount;
        }
    }

    /**
     * Simulates a persistence failure without relying on platform-specific file permissions.
     */
    private static class FailingStorage extends Storage {
        FailingStorage(Path saveFile) {
            super(saveFile);
        }

        @Override
        public void save(List<Task> tasks) throws DukeException {
            throw new DukeException("Walter could not save your tasks.");
        }

        @Override
        public void savePlaces(List<Place> places) throws DukeException {
            throw new DukeException("Walter could not save your places.");
        }
    }
}
