package mary.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.util.List;

import org.junit.jupiter.api.Test;

import mary.parser.Parser;
import mary.storage.Storage;
import mary.task.Todo;

/**
 * Exercises malformed input and persistence failures through actual commands.
 */
class ErrorHandlingTest extends CommandTestSupport {
    /**
     * Accepts surrounding spaces, tabs, repeated separators, and valid leap dates.
     */
    @Test
    void execute_whitespaceAndLeapDate_acceptsValidCommands() throws Exception {
        execute(Parser.parse(" \ttodo\t read  café book  "));
        assertEquals("[T][ ] read  café book", tasks.get(0).toString());
        execute(Parser.parse("  mark\t 1  "));
        assertEquals("X", tasks.get(0).getStatusIcon());
        String reply = execute(Parser.parse("deadline leap /by 29/2/2024   1800"));
        assertFalse(reply.contains("Error:"));
        assertEquals(2, storage.load().size());
        assertTrue(execute(Parser.parse("  list  ")).contains("1.[T][X]"));
        assertTrue(Parser.parse(" bye  ").isExit());
    }

    /**
     * Rejects malformed syntax without mutation and still accepts a later valid command.
     */
    @Test
    void execute_malformedCommands_reportsErrorsWithoutSaving() {
        for (String input : List.of("todo", "deadline", "event", "on", "find", "list extra", "bye now",
                "todo a | b", "todo a\nb", "todo a\u0000b", "todoer read", "online", "deleteAll",
                "mark +1", "delete 1 2", "mark 999999999999999999999", "mark ١",
                "deadline read /by 30/2/2024 1200", "deadline read /by 1/1/2026 2460",
                "deadline read /from 1/1/2026 1200", "deadline read /to ignored /by 1/1/2026 1200",
                "event read /by ignored /from 1/1/2026 1200 /to 1/1/2026 1300",
                "deadline read /by 1/1/2026 1200 /by 2/1/2026 1200",
                "event camp /from 1/1/2026 1200 /to 1/1/2026 1200",
                "event camp /from 2/1/2026 1200 /to 1/1/2026 1200",
                "event camp /from 1/1/2026 1200 /from 2/1/2026 1200 /to 3/1/2026 1200")) {
            assertTrue(execute(Parser.parse(input)).startsWith(" Error:"), input);
            assertTrue(tasks.isEmpty(), input);
            assertFalse(Files.exists(directory.resolve("tasks.txt")), input);
        }
        assertTrue(execute(Parser.parse(null)).startsWith(" Error:"));
        execute(Parser.parse("todo valid"));
        assertEquals(1, tasks.size());
    }

    /**
     * Treats exact details as duplicates regardless of status but permits different dates or types.
     */
    @Test
    void execute_duplicateDetails_rejectsOnlyMatchingTask() throws Exception {
        for (String input : List.of("todo read", "deadline read /by 1/1/2026 1200",
                "event read /from 1/1/2026 1200 /to 1/1/2026 1300")) {
            execute(Parser.parse(input));
            execute(Parser.parse("mark " + tasks.size()));
            String before = Files.readString(directory.resolve("tasks.txt"));
            assertTrue(execute(Parser.parse(input)).contains("already exists"));
            assertEquals(before, Files.readString(directory.resolve("tasks.txt")));
        }
        execute(Parser.parse("deadline read /by 2/1/2026 1200"));
        execute(Parser.parse("event read /from 1/1/2026 1200 /to 1/1/2026 1400"));
        assertEquals(5, tasks.size());
    }

    /**
     * Every failed write preserves task identity, order and completion state.
     */
    @Test
    void execute_unwritableDestination_rollsBackAllMutations() {
        Todo task = new Todo("keep");
        tasks.add(task);
        storage = new Storage(directory.toString());
        for (String input : List.of("todo new", "delete 1", "mark 1", "sort")) {
            assertTrue(execute(Parser.parse(input)).contains("could not save"), input);
            assertEquals(List.of(task), tasks.getTasks());
            assertEquals(" ", task.getStatusIcon());
        }
        task.markAsDone();
        assertTrue(execute(Parser.parse("unmark 1")).contains("could not save"));
        assertEquals("X", task.getStatusIcon());
    }
}
