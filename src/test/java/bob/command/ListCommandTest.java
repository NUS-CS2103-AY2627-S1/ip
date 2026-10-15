package bob.command;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

import bob.task.ToDo;

/**
 * Tests the behavior of {@link ListCommand}.
 */
public class ListCommandTest extends CommandTestBase {

    @Test
    public void isExit_returnsFalse() {
        ListCommand command = new ListCommand();
        assertFalse(command.isExit());
    }

    @Test
    public void execute_setsListWithoutError() {
        tasks.add(new ToDo("task 1"));
        ListCommand command = new ListCommand();
        assertDoesNotThrow(() -> command.execute(tasks, ui, storage));
        assertEquals("Today's job board:\n1: [T][ ] task 1", ui.getLastResponse());
    }
}
