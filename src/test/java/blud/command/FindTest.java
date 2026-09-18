package blud.command;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import blud.model.TaskList;
import blud.model.ToDo;

/** Tests searching tasks by description keyword. */
public class FindTest {
    /** Verifies that matching is partial, case-insensitive, and preserves task order. */
    @Test
    public void execute_partialCaseInsensitiveMatch_returnsMatchingTasks() {
        TaskList taskList = new TaskList(List.of(
                new ToDo(new String[] {"todo read book"}),
                new ToDo(new String[] {"todo wash car"}),
                new ToDo(new String[] {"todo return notebook"})));

        String actual = new Find("find BOOK").execute(taskList);

        assertEquals("Here are the matching tasks in your list:\n"
                + "1. [T][ ] read book\n"
                + "2. [T][ ] return notebook", actual);
    }

    /** Verifies that an empty find keyword is rejected. */
    @Test
    public void constructor_emptyKeyword_throwsError() {
        IllegalArgumentException exception = org.junit.jupiter.api.Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new Find("find"));

        assertEquals("Please provide a keyword to find.", exception.getMessage());
    }
}
