package thomas.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import thomas.ThomasException;
import thomas.place.PlaceList;
import thomas.storage.PlaceStorage;
import thomas.storage.Storage;
import thomas.task.Deadline;
import thomas.task.Event;
import thomas.task.TaskList;
import thomas.task.Todo;
import thomas.ui.Ui;

class CommandExecutionTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void todoCommand_execute_addsAndPersistsTodo() throws Exception {
        TaskList tasks = new TaskList();
        new TodoCommand("read book").execute(tasks, new Ui(), storage());

        assertEquals(1, tasks.size());
        assertInstanceOf(Todo.class, tasks.get(0));
        assertEquals("read book", tasks.get(0).getDescription());
        assertEquals(1, storage().load().size());
    }

    @Test
    void deadlineAndEventCommands_execute_addTasks() throws Exception {
        TaskList tasks = new TaskList();
        Storage storage = storage();

        new DeadlineCommand("submit", "2026-08-26").execute(tasks, new Ui(), storage);
        new EventCommand("meeting", "2026-08-26", "2026-08-27").execute(tasks, new Ui(), storage);

        assertInstanceOf(Deadline.class, tasks.get(0));
        assertInstanceOf(Event.class, tasks.get(1));
        assertEquals(2, storage.load().size());
    }

    @Test
    void markAndUnmarkCommands_execute_changeTaskStatus() throws Exception {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("finish"));
        Storage storage = storage();
        Parser parser = new Parser();

        new MarkCommand("1", parser).execute(tasks, new Ui(), storage);
        assertTrue(tasks.get(0).isDone());
        new UnmarkCommand("1", parser).execute(tasks, new Ui(), storage);
        assertFalse(tasks.get(0).isDone());
    }

    @Test
    void deleteCommand_execute_removesTask() throws Exception {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("remove"));

        new DeleteCommand("1", new Parser()).execute(tasks, new Ui(), storage());

        assertTrue(tasks.getTasks().isEmpty());
    }

    @Test
    void todoCommand_emptyDescription_throwsWithoutAddingTask() {
        TaskList tasks = new TaskList();

        assertThrows(ThomasException.class, () ->
                new TodoCommand("").execute(tasks, new Ui(), storage()));
        assertEquals(0, tasks.size());
    }

    @Test
    void exitCommand_isExit_returnsTrue() {
        assertTrue(new ExitCommand().isExit());
        assertFalse(new ListCommand().isExit());
        assertFalse(new UnknownCommand().isExit());
    }

    @Test
    void unknownCommand_execute_throwsException() {
        assertThrows(ThomasException.class, () ->
                new UnknownCommand().execute(new TaskList(), new Ui(), storage()));
    }

    @Test
    void findCommand_execute_withKeywordDoesNotMutate() throws Exception {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Deadline("return book", "2026-06-06"));

        new FindCommand("book").execute(tasks, new Ui(), storage());

        assertEquals(2, tasks.size());
        assertEquals("read book", tasks.get(0).getDescription());
        assertEquals("return book", tasks.get(1).getDescription());
    }

    @Test
    void findCommand_execute_emptyKeywordThrows() {
        assertThrows(ThomasException.class, () ->
                new FindCommand("").execute(new TaskList(), new Ui(), storage()));
    }

    @Test
    void duplicateTaskCommands_execute_throwWithoutAdding() throws Exception {
        TaskList tasks = new TaskList();
        Storage storage = storage();
        new TodoCommand("same").execute(tasks, new Ui(), storage);

        assertThrows(ThomasException.class, () ->
                new TodoCommand("same").execute(tasks, new Ui(), storage));
        assertEquals(1, tasks.size());
    }

    @Test
    void onListAndHelpCommands_executeWithoutMutatingTasks() throws Exception {
        TaskList tasks = new TaskList();
        tasks.add(new Deadline("submit", "2026-08-26"));

        new OnCommand("2026-08-26").execute(tasks, new Ui(), storage());
        new ListCommand().execute(tasks, new Ui(), storage());
        new HelpCommand().execute(tasks, new Ui(), storage());

        assertEquals(1, tasks.size());
        assertThrows(ThomasException.class, () ->
                new OnCommand("").execute(tasks, new Ui(), storage()));
    }

    @Test
    void placeCommands_executeCompleteLifecycle() throws Exception {
        PlaceList places = new PlaceList();
        PlaceStorage storage = placeStorage();
        Parser parser = new Parser();

        executePlace("place Sushi /type Restaurant /at Town /rating 4 /price 12",
                parser, places, storage);
        executePlace("editplace 1 /rating 5", parser, places, storage);
        executePlace("listplace", parser, places, storage);
        executePlace("findplace sushi", parser, places, storage);
        executePlace("deleteplace 1", parser, places, storage);
        executePlace("confirmdeleteplace 1", parser, places, storage);

        assertEquals(0, places.size());
        assertEquals(0, storage.load().size());
    }

    @Test
    void placeCommands_invalidState_throwExceptions() throws Exception {
        PlaceList places = new PlaceList();
        PlaceStorage storage = placeStorage();

        assertThrows(ThomasException.class, () ->
                new FindPlaceCommand("").execute(places, new Ui(), storage));
        assertThrows(ThomasException.class, () ->
                new DeletePlaceCommand(1).execute(places, new Ui(), storage));
        assertThrows(ThomasException.class, () ->
                new ConfirmDeletePlaceCommand(1).execute(places, new Ui(), storage));
    }

    private Storage storage() {
        return new Storage(temporaryDirectory.toString(), "tasks.txt");
    }

    private PlaceStorage placeStorage() {
        return new PlaceStorage(temporaryDirectory.resolve("places.txt").toString());
    }

    private void executePlace(String input, Parser parser, PlaceList places, PlaceStorage storage)
            throws Exception {
        PlaceCommand command = (PlaceCommand) parser.parse(input);
        command.execute(places, new Ui(), storage);
    }
}
