package mary.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import mary.task.Todo;

/**
 * Tests one-based deletion, renumbering, persistence, and invalid input.
 */
class DeleteCommandTest extends CommandTestSupport {
    /**
     * Tests execute: middle task; removes only selected task and saves.
     */
    @Test
    void execute_middleTask_removesOnlySelectedTaskAndSaves() throws Exception {
        tasks.add(new Todo("a"));
        tasks.add(new Todo("b"));
        tasks.add(new Todo("c"));
        assertEquals(" Whisked away! I've removed this task:\n   [T][ ] b\n Tasks on your list: 2.\n",
                execute(new DeleteCommand("delete 2")));
        assertEquals("[T][ ] a", tasks.get(0).toString());
        assertEquals("[T][ ] c", tasks.get(1).toString());
        assertEquals(2, storage.load().size());
    }

    /**
     * Tests execute: last remaining task; saves empty list.
     */
    @Test
    void execute_lastRemainingTask_savesEmptyList() throws Exception {
        tasks.add(new Todo("a"));
        execute(new DeleteCommand("delete 1"));
        assertTrue(tasks.isEmpty());
        assertTrue(storage.load().isEmpty());
    }

    /**
     * Tests execute: missing or invalid number; does not remove task.
     */
    @Test
    void execute_missingOrInvalidNumber_doesNotRemoveTask() {
        tasks.add(new Todo("keep"));
        String[] inputs = {"delete", "delete ", "delete abc", "delete 1.5",
            "delete 0", "delete -1", "delete 2", "delete 2147483648"};
        for (String input : inputs) {
            assertTrue(execute(new DeleteCommand(input)).startsWith(" Error:"), input);
            assertEquals(1, tasks.size(), input);
        }
    }

    /**
     * Tests execute: empty list; explains valid number lookup.
     */
    @Test
    void execute_emptyList_explainsValidNumberLookup() {
        assertEquals(" Error: task 1 does not exist; use 'list' to see valid task numbers.\n",
                execute(new DeleteCommand("delete 1")));
    }
}
