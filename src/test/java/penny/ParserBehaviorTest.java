package penny;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Exercises command sequences, filtering, replay, and invalid input boundaries.
 */
public class ParserBehaviorTest {
    @Test
    void handleInput_taskLifecycle_updatesListAndReplies() throws PennyException {
        TaskList tasks = new TaskList();
        assertEquals(new ParseResult("Added: [T][ ] read", false),
                Parser.handleInput(tasks, "ToDo\tread", false));
        assertEquals(new ParseResult("Marked as done: [T][X] read", false),
                Parser.handleInput(tasks, "mark 1", false));
        assertTrue(tasks.get(0).isDone());
        assertEquals(new ParseResult("Marked as undone: [T][ ] read", false),
                Parser.handleInput(tasks, "unmark 1", false));
        assertFalse(tasks.get(0).isDone());
        assertEquals(new ParseResult("1. [T][ ] read", false), Parser.handleInput(tasks, "list", false));
        assertEquals(new ParseResult("Deleted: [T][ ] read", false),
                Parser.handleInput(tasks, "delete 1", false));
        assertTrue(tasks.isEmpty());
        assertEquals(new ParseResult("Bye! See you soon!", true), Parser.handleInput(tasks, "bye", false));
    }

    @Test
    void handleInput_filters_preserveOriginalNumbers() throws PennyException {
        TaskList tasks = new TaskList();
        Parser.handleInput(tasks, "todo read", false);
        assertEquals(new ParseResult("Added: [D][ ] report (by: 15 09 2026)", false),
                Parser.handleInput(tasks, "deadline report /by 15092026", false));
        assertEquals(new ParseResult("Added: [E][ ] report meeting (from: 15 09 2026 | to: 16 09 2026)", false),
                Parser.handleInput(tasks, "event report meeting /from 15092026 /to 16092026", false));
        String matches = "2. [D][ ] report (by: 15 09 2026)\n"
                + "3. [E][ ] report meeting (from: 15 09 2026 | to: 16 09 2026)";
        assertEquals(new ParseResult(matches, false), Parser.handleInput(tasks, "find report  ", false));
        assertEquals(new ParseResult(matches, false), Parser.handleInput(tasks, "due 15092026 1200", false));
        assertEquals(new ParseResult("1. [T][ ] read\n" + matches, false),
                Parser.handleInput(tasks, "list", false));
        assertError(tasks, "find absent", "No matching tasks on your list");
        assertError(tasks, "due 17092026", "There are no tasks due on 17 09 2026");
    }

    @Test
    void handleInput_loading_replaysMutationsSilently() throws PennyException {
        TaskList tasks = new TaskList();
        String[] commands = {"list", "find", "due", "todo read", "deadline report /by 15092026",
            "event meeting /from 15092026 /to 16092026", "mark 2", "unmark 2", "delete 1"};
        for (String command : commands) {
            assertEquals(new ParseResult("", false), Parser.handleInput(tasks, command, true), command);
        }
        assertEquals(2, tasks.size());
        assertEquals("report", tasks.get(0).getDescription());
        assertFalse(tasks.get(0).isDone());
        assertEquals("meeting", tasks.get(1).getDescription());
    }

    @Test
    void handleInput_invalidQueries_reportsErrors() {
        TaskList tasks = new TaskList();
        assertError(tasks, "", "Sorry I didn't quite catch that.");
        assertError(tasks, "find", "Find keyword cannot be empty");
        assertError(tasks, "find read", "There are no tasks on your list");
        assertError(tasks, "due 15092026", "There are no tasks on your list");
        assertError(tasks, "due bad", "Use 'ddMMyyyy HHmm' or 'ddMMyyyy'.");
    }

    @Test
    void handleInput_invalidIndices_doNotChangeTasks() throws PennyException {
        TaskList tasks = new TaskList();
        tasks.add(ToDoTask.create("read"));
        for (String command : new String[]{"mark", "unmark", "delete"}) {
            for (String argument : new String[]{"", "abc", "2147483648", "1.5", "0", "-1", "2"}) {
                assertThrows(PennyException.class, () -> Parser.handleInput(tasks, command + " " + argument, false));
                assertEquals(1, tasks.size());
                assertFalse(tasks.get(0).isDone());
            }
        }
        assertError(tasks, "unmark 1", "Task is already marked as undone");
        Parser.handleInput(tasks, "mark 1", false);
        assertError(tasks, "mark 1", "Task is already marked as done");
        assertTrue(tasks.get(0).isDone());
    }

    @Test
    void handleInput_duplicateAcrossTypes_preservesOriginalTask() throws PennyException {
        for (String command : new String[]{"todo read", "deadline read /by 15092026",
            "event read /from 15092026 /to 16092026"}) {
            TaskList tasks = new TaskList();
            tasks.add(ToDoTask.create("read"));
            assertError(tasks, command, "This task is already on your list.");
            assertEquals(1, tasks.size());
            assertEquals("[T][ ] read", tasks.get(0).toString());
        }
    }

    @Test
    void isInteger_boundariesAndMalformedInput_returnsExpectedResult() {
        for (String input : new String[]{"0", "-1", "+1", "2147483647", "-2147483648"}) {
            assertTrue(Parser.isInteger(input), input);
        }
        for (String input : new String[]{null, "", " ", "abc", "1.0", " 1", "2147483648", "-2147483649"}) {
            assertFalse(Parser.isInteger(input), input);
        }
    }

    /**
     * Checks the user-facing error for an invalid command.
     */
    private static void assertError(TaskList tasks, String input, String message) {
        assertEquals(message,
                assertThrows(PennyException.class, () -> Parser.handleInput(tasks, input, false)).getMessage());
    }
}
