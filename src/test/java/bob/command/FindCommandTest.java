package bob.command;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import bob.exception.BobException;
import bob.task.ToDo;

/**
 * Tests the behavior of {@link FindCommand}.
 */
public class FindCommandTest extends CommandTestBase {

    @Test
    public void execute_matchingKeyword_displaysMatchingTasksWithPreservedIndices() throws BobException {
        tasks.add(new ToDo("read book")); // index 1
        tasks.add(new ToDo("return book")); // index 2
        tasks.add(new ToDo("buy milk")); // index 3
        tasks.add(new ToDo("read another book")); // index 4

        FindCommand command = new FindCommand("book");
        command.execute(tasks, ui, storage);

        String output = ui.getLastResponse();
        assertTrue(output.contains("These jobs match:"));
        assertTrue(output.contains("1.[T][ ] read book"));
        assertTrue(output.contains("2.[T][ ] return book"));
        assertTrue(output.contains("4.[T][ ] read another book"));
        assertFalse(output.contains("buy milk"));
        assertFalse(output.contains("3."));
    }

    @Test
    public void isExit_returnsFalse() {
        FindCommand command = new FindCommand("test");
        assertFalse(command.isExit());
    }
}
