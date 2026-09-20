package atom.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import atom.exception.AtomInvalidDateException;
import atom.exception.AtomInvalidTypeException;
import atom.exception.AtomMismatchedArgumentsException;
import atom.storage.Storage;
import atom.task.TaskList;
import atom.task.ToDo;

class CommandTest {
    private static final Path SAVE_FILE = Paths.get("./data/atom.txt");
    private byte[] originalSaveFile;

    @BeforeEach
    void isolateStorage() throws Exception {
        originalSaveFile = Files.readAllBytes(SAVE_FILE);
        Files.writeString(SAVE_FILE, "");
    }

    @AfterEach
    void restoreStorage() throws Exception {
        Files.write(SAVE_FILE, originalSaveFile);
    }

    @Test
    void commandNames_areCorrect() throws Exception {
        assertEquals("todo", new ToDoCommand("x").getCommandName());
        assertEquals("deadline", new DeadlineCommand("x", "by 2024-01-01").getCommandName());
        assertEquals("event", new EventCommand("x", "from 2024-01-01", "to 2024-01-02").getCommandName());
        assertEquals("list", new ListCommand().getCommandName());
        assertEquals("find", new FindCommand("x").getCommandName());
        assertEquals("schedule", new ScheduleCommand("2024-01-01").getCommandName());
    }

    @Test
    void constructors_validateArguments() {
        assertThrows(AtomMismatchedArgumentsException.class, () -> new ToDoCommand());
        assertThrows(AtomInvalidTypeException.class, () -> new MarkCommand("x"));
        assertThrows(AtomInvalidTypeException.class, () -> new DeadlineCommand("x", "2024-01-01"));
        assertThrows(AtomInvalidTypeException.class, () ->
                new EventCommand("x", "from 2024-01-01", "2024-01-02"));
    }

    @Test
    void schedule_rejectsInvalidDate() {
        TaskList tasks = new TaskList(new ArrayList<>());
        AtomInvalidDateException exception = assertThrows(AtomInvalidDateException.class, () ->
                new ScheduleCommand("not-a-date").execute(tasks, new Storage()));
        assertEquals("Oh no! Invalid date provided. Please use yyyy-MM-dd or yyyy-MM-dd HH:mm.",
                exception.getMessage());
    }

    @Test
    void list_displaysIntroductoryMessage() {
        TaskList tasks = new TaskList(new ArrayList<>());
        tasks.add(new ToDo("read book"));

        assertEquals("Here are the tasks in your list:\n1. [T][ ] read book",
                new ListCommand().execute(tasks, new Storage()));
    }

    @Test
    void list_displaysDifferentMessageWhenEmpty() {
        TaskList tasks = new TaskList(new ArrayList<>());

        assertEquals("There are no tasks in your list.",
                new ListCommand().execute(tasks, new Storage()));
    }

    @Test
    void find_displaysDifferentMessageWhenThereAreNoResults() {
        TaskList tasks = new TaskList(new ArrayList<>());

        assertEquals("There are no matching tasks in your list.",
                new FindCommand("read").execute(tasks, new Storage()));
    }

    @Test
    void taskCommands_executeAgainstTaskList() {
        TaskList tasks = new TaskList(new ArrayList<>());
        Storage storage = new Storage();
        new ToDoCommand("read book").execute(tasks, storage);
        new DeadlineCommand("submit", "by 2024-10-02 09:00").execute(tasks, storage);
        new EventCommand("meeting", "from 2024-10-02 14:00", "to 2024-10-02 16:00").execute(tasks, storage);

        assertEquals(3, tasks.size());
        assertEquals("read book", tasks.get(0).getDescription());
        assertEquals("submit", tasks.get(1).getDescription());
        assertEquals("meeting", tasks.get(2).getDescription());
        new MarkCommand("1").execute(tasks, storage);
        assertEquals("[T][X] read book", tasks.get(0).toString());
        new UnmarkCommand("1").execute(tasks, storage);
        assertEquals("[T][ ] read book", tasks.get(0).toString());
        new DeleteCommand("1").execute(tasks, storage);
        assertEquals(2, tasks.size());
    }
}
