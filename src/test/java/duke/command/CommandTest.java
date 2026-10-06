package duke.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import duke.exception.DukeException;
import duke.place.Place;
import duke.storage.Storage;
import duke.task.Deadline;
import duke.task.Task;
import duke.task.TaskList;
import duke.task.ToDo;
import duke.ui.Ui;

/**
 * Unit tests for {@link Command} subclasses.
 */
public class CommandTest {

    @TempDir
    Path tempDir;

    private TaskList tasks;
    private Ui ui;
    private Storage storage;

    @BeforeEach
    public void setUp() {
        tasks = new TaskList();
        ui = new Ui();
        storage = new Storage(tempDir.resolve("test_tasks.txt").toString());
    }

    @Test
    public void addCommand_execute_addsTaskAndSaves() throws DukeException {
        Task todo = new ToDo("read book");
        Command command = new AddCommand(todo);
        command.execute(tasks, ui, storage);

        assertEquals(1, tasks.size());
        assertEquals(todo, tasks.getTask(0));
        assertFalse(command.isExit());
    }

    @Test
    public void addCommand_duplicateTask_exceptionThrown() throws DukeException {
        Task todo = new ToDo("read book");
        new AddCommand(todo).execute(tasks, ui, storage);

        Task duplicate = new ToDo("read book");
        org.junit.jupiter.api.Assertions.assertThrows(DukeException.class, () ->
                new AddCommand(duplicate).execute(tasks, ui, storage));
    }

    @Test
    public void markCommand_execute_marksTaskAsDone() throws DukeException {
        Task todo = new ToDo("read book");
        tasks.addTask(todo);
        Command command = new MarkCommand(0);
        command.execute(tasks, ui, storage);

        assertEquals("X", todo.getStatusIcon());
    }

    @Test
    public void unmarkCommand_execute_unmarksTask() throws DukeException {
        Task todo = new ToDo("read book");
        todo.markAsDone();
        tasks.addTask(todo);
        Command command = new UnmarkCommand(0);
        command.execute(tasks, ui, storage);

        assertEquals(" ", todo.getStatusIcon());
    }

    @Test
    public void deleteCommand_execute_removesTask() throws DukeException {
        Task todo = new ToDo("read book");
        Task deadline = new Deadline("submit report", "2026-09-01 1800");
        tasks.addTask(todo);
        tasks.addTask(deadline);

        Command command = new DeleteCommand(0);
        command.execute(tasks, ui, storage);

        assertEquals(1, tasks.size());
        assertEquals(deadline, tasks.getTask(0));
    }

    @Test
    public void exitCommand_isExit_returnsTrue() throws DukeException {
        Command command = new ExitCommand();
        command.execute(tasks, ui, storage);
        assertTrue(command.isExit());
    }

    @Test
    public void findCommand_execute_success() throws DukeException {
        Task todo = new ToDo("read book");
        tasks.addTask(todo);
        Command command = new FindCommand("book");
        command.execute(tasks, ui, storage);
        assertFalse(command.isExit());
    }

    @Test
    public void undoCommand_execute_revertsLastMutation() throws DukeException {
        Task todo = new ToDo("read book");
        Command addCommand = new AddCommand(todo);
        addCommand.execute(tasks, ui, storage);
        assertEquals(1, tasks.size());

        Command undoCommand = new UndoCommand();
        undoCommand.execute(tasks, ui, storage);
        assertEquals(0, tasks.size());
    }

    @Test
    public void addPlaceCommand_execute_addsPlaceAndSaves() throws DukeException {
        Place place = new Place("Sentosa", "Resort island");
        Command command = new AddPlaceCommand(place);
        command.execute(tasks, ui, storage);

        assertEquals(1, storage.getPlaceList().size());
        assertEquals(place, storage.getPlaceList().getPlace(0));
    }

    @Test
    public void addPlaceCommand_duplicatePlace_exceptionThrown() throws DukeException {
        Place place = new Place("Sentosa", "Resort island");
        new AddPlaceCommand(place).execute(tasks, ui, storage);

        Place duplicate = new Place("Sentosa", "Resort island");
        org.junit.jupiter.api.Assertions.assertThrows(DukeException.class, () ->
                new AddPlaceCommand(duplicate).execute(tasks, ui, storage));
    }

    @Test
    public void markCommand_invalidIndex_exceptionThrown() {
        Command command = new MarkCommand(0);
        org.junit.jupiter.api.Assertions.assertThrows(DukeException.class, () ->
                command.execute(tasks, ui, storage));
    }

    @Test
    public void unmarkCommand_invalidIndex_exceptionThrown() {
        Command command = new UnmarkCommand(0);
        org.junit.jupiter.api.Assertions.assertThrows(DukeException.class, () ->
                command.execute(tasks, ui, storage));
    }

    @Test
    public void deleteCommand_invalidIndex_exceptionThrown() {
        Command command = new DeleteCommand(0);
        org.junit.jupiter.api.Assertions.assertThrows(DukeException.class, () ->
                command.execute(tasks, ui, storage));
    }

    @Test
    public void listCommand_execute_displaysTasks() throws DukeException {
        Command listCommand = new ListCommand();
        listCommand.execute(tasks, ui, storage);
        assertEquals("There are no tasks in your list.", ui.getLastResponse());

        tasks.addTask(new ToDo("read book"));
        listCommand.execute(tasks, ui, storage);
        assertEquals("1. [T][ ] read book", ui.getLastResponse());
    }

    @Test
    public void findCommand_noMatches_displaysEmptyList() throws DukeException {
        tasks.addTask(new ToDo("read book"));
        Command command = new FindCommand("swimming");
        command.execute(tasks, ui, storage);
        assertEquals("Here are the matching tasks in your list:", ui.getLastResponse());
    }

    @Test
    public void undoCommand_emptyHistory_exceptionThrown() {
        Command undoCommand = new UndoCommand();
        org.junit.jupiter.api.Assertions.assertThrows(DukeException.class, () ->
                undoCommand.execute(tasks, ui, storage));
    }

    @Test
    public void deletePlaceCommand_execute_removesPlace() throws DukeException {
        Place p1 = new Place("Place 1");
        Place p2 = new Place("Place 2");
        storage.getPlaceList().addPlace(p1);
        storage.getPlaceList().addPlace(p2);

        Command command = new DeletePlaceCommand(0);
        command.execute(tasks, ui, storage);

        assertEquals(1, storage.getPlaceList().size());
        assertEquals(p2, storage.getPlaceList().getPlace(0));
    }

    @Test
    public void deletePlaceCommand_invalidIndex_exceptionThrown() {
        Command command = new DeletePlaceCommand(0);
        org.junit.jupiter.api.Assertions.assertThrows(DukeException.class, () ->
                command.execute(tasks, ui, storage));
    }

    @Test
    public void listPlacesCommand_execute_displaysPlaces() throws DukeException {
        storage.getPlaceList().addPlace(new Place("Marina Bay Sands"));
        Command command = new ListPlacesCommand();
        command.execute(tasks, ui, storage);

        assertEquals("Here are the places in your list:\n1. Marina Bay Sands", ui.getLastResponse());
    }

    @Test
    public void findPlaceCommand_execute_success() throws DukeException {
        storage.getPlaceList().addPlace(new Place("Changi Airport", "Jewel"));
        Command command = new FindPlaceCommand("jewel");
        command.execute(tasks, ui, storage);

        assertEquals("Here are the matching places in your list:\n1. Changi Airport (details: Jewel)",
                ui.getLastResponse());
    }

    @Test
    public void findPlaceCommand_noMatches_displaysEmptyMessage() throws DukeException {
        storage.getPlaceList().addPlace(new Place("Sentosa"));
        Command command = new FindPlaceCommand("airport");
        command.execute(tasks, ui, storage);
        assertEquals("There are no matching places found.", ui.getLastResponse());
    }
}
