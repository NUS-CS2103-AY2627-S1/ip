package nyonbot.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import nyonbot.Parser;
import nyonbot.model.NyonException;
import nyonbot.model.Task;
import nyonbot.model.TaskList;

class TaskMutationCommandTest {
    private TaskList tasks;

    @BeforeEach
    void setUp() {
        tasks = new TaskList();
        tasks.add(new Task("first task"));
        tasks.add(new Task("second task"));
    }

    @Test
    void markCommand_validIndex_marksTask() throws NyonException {
        new MarkCommand(arguments("mark 2"), tasks).execute();

        assertFalse(tasks.get(0).isDone());
        assertTrue(tasks.get(1).isDone());
    }

    @Test
    void markCommand_taskName_marksTask() throws NyonException {
        new MarkCommand(arguments("mark first task"), tasks).execute();

        assertTrue(tasks.get(0).isDone());
    }

    @Test
    void markCommand_invalidIndex_throwsNyonException() {
        assertThrows(NyonException.class, () -> new MarkCommand(arguments("mark 0"), tasks).execute());
        assertThrows(NyonException.class, () -> new MarkCommand(arguments("mark 3"), tasks).execute());
    }

    @Test
    void markCommand_missingDescription_throwsNyonException() {
        assertThrows(NyonException.class, () -> new MarkCommand(arguments("mark "), tasks).execute());
    }

    @Test
    void unmarkCommand_validIndex_unmarksTask() throws NyonException {
        tasks.get(0).completeTask();

        new UnmarkCommand(arguments("unmark 1"), tasks).execute();

        assertFalse(tasks.get(0).isDone());
    }

    @Test
    void unmarkCommand_taskName_unmarksTask() throws NyonException {
        tasks.get(1).completeTask();

        new UnmarkCommand(arguments("unmark second task"), tasks).execute();

        assertFalse(tasks.get(1).isDone());
    }

    @Test
    void unmarkCommand_invalidIndex_throwsNyonException() {
        assertThrows(NyonException.class, () -> new UnmarkCommand(arguments("unmark 3"), tasks).execute());
    }

    @Test
    void deleteCommand_validIndex_removesTask() throws NyonException {
        new DeleteCommand(arguments("delete 1"), tasks).execute();

        assertEquals(1, tasks.size());
        assertEquals("second task", tasks.get(0).getName());
    }

    @Test
    void deleteCommand_taskName_removesTask() throws NyonException {
        new DeleteCommand(arguments("delete second task"), tasks).execute();

        assertEquals(1, tasks.size());
        assertEquals("first task", tasks.get(0).getName());
    }

    @Test
    void deleteCommand_invalidIndex_throwsNyonException() {
        assertThrows(NyonException.class, () -> new DeleteCommand(arguments("delete 0"), tasks).execute());
        assertThrows(NyonException.class, () -> new DeleteCommand(arguments("delete 3"), tasks).execute());
    }

    @Test
    void deleteCommand_unknownName_throwsNyonException() {
        assertThrows(NyonException.class, () -> new DeleteCommand(
            arguments("delete missing task"), tasks).execute()
        );
    }

    private static HashMap<String, String> arguments(String input) {
        return Parser.getInstance().parseArguments(input);
    }
}
