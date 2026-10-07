package mary.command;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import mary.task.Todo;

/**
 * Tests completion changes, repetition, numbering errors, and persistence.
 */
class MarkCommandTest extends CommandTestSupport {
    /**
     * Tests execute: mark and unmark; updates only selected task and saves.
     */
    @Test
    void execute_markAndUnmark_updatesOnlySelectedTaskAndSaves() throws Exception {
        tasks.add(new Todo("first"));
        tasks.add(new Todo("second"));
        assertEquals(" Pawsome! Task completed:\n   [T][X] second\n",
                execute(new MarkCommand("mark 2")));
        assertEquals(" ", tasks.get(0).getStatusIcon());
        assertEquals("X", storage.load().get(1).getStatusIcon());
        execute(new MarkCommand("mark 2"));
        assertEquals("X", tasks.get(1).getStatusIcon());
        assertEquals(" Back on your list, ready for another pounce:\n   [T][ ] second\n",
                execute(new MarkCommand("unmark 2")));
        execute(new MarkCommand("unmark 2"));
        assertEquals(" ", storage.load().get(1).getStatusIcon());
    }

    /**
     * Tests execute: invalid number; reports error without changing status.
     */
    @Test
    void execute_invalidNumber_reportsErrorWithoutChangingStatus() {
        tasks.add(new Todo("keep"));
        String[] inputs = {"mark abc", "mark 0", "mark -1", "mark 2",
            "unmark 0", "unmark 1.5", "mark 2147483648"};
        for (String input : inputs) {
            assertTrue(execute(new MarkCommand(input)).startsWith(" Error:"), input);
            assertEquals(" ", tasks.get(0).getStatusIcon());
        }
    }

    /**
     * Tests execute: missing number; reports usage instead of crashing.
     */
    @Test
    void execute_missingNumber_reportsUsageInsteadOfCrashing() {
        String[] inputs = {"mark", "unmark", "mark ", "unmark ",
            "markSomething", "unmarkSomething"};
        for (String input : inputs) {
            String output = assertDoesNotThrow(() -> execute(new MarkCommand(input)), input);
            assertEquals(" Error: use 'mark N' or 'unmark N', where N is a task number.\n", output);
        }
    }
}
