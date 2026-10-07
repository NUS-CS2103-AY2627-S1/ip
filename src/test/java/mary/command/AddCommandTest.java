package mary.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;

import org.junit.jupiter.api.Test;

import mary.storage.Storage;

/**
 * Tests additions, persistence, validation, and storage failure feedback.
 */
class AddCommandTest extends CommandTestSupport {
    /**
     * Tests execute: valid todo; adds and saves task.
     */
    @Test
    void execute_validTodo_addsAndSavesTask() throws Exception {
        assertEquals(" Purr-fect! I've added this task:\n   [T][ ] read book\n Tasks on your list: 1.\n",
                execute(new AddCommand("todo read book")));
        assertEquals(1, tasks.size());
        assertEquals("[T][ ] read book", storage.load().get(0).toString());
    }

    /**
     * Tests execute: deadline and event; persists both subtypes.
     */
    @Test
    void execute_deadlineAndEvent_persistsBothSubtypes() throws Exception {
        execute(new AddCommand("deadline read /by 2/12/2019 1800"));
        execute(new AddCommand("event camp /from 2/12/2019 1400 /to 4/12/2019 1600"));
        assertEquals(tasks.getTasks().stream().map(Object::toString).toList(),
                storage.load().stream().map(Object::toString).toList());
        assertEquals(2, tasks.size());
    }

    /**
     * Tests execute: invalid description; reports error without adding or saving.
     */
    @Test
    void execute_invalidDescription_reportsErrorWithoutAddingOrSaving() {
        assertTrue(execute(new AddCommand("todo ")).startsWith(" Error:"));
        assertTrue(tasks.isEmpty());
        assertFalse(Files.exists(directory.resolve("tasks.txt")));
    }

    /**
     * Tests execute: save failure; reports error without success message.
     */
    @Test
    void execute_saveFailure_reportsErrorWithoutSuccessMessage() {
        storage = new Storage(directory.toString());
        String output = execute(new AddCommand("todo read"));
        assertTrue(output.contains("Error: could not save tasks"));
        assertFalse(output.contains("Purr-fect! I've added this task:"));
    }
}
