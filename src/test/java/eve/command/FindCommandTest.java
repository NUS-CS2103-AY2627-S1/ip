package eve.command;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import eve.TestUtil;
import eve.Ui;
import eve.task.TaskList;
import eve.task.ToDo;

/** Tests {@link FindCommand}: showing tasks whose description matches a keyword. */
public class FindCommandTest {
    @Test
    public void execute_matchingKeyword_showsMatch() throws Exception {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("read book"));
        tasks.add(new ToDo("join sports club"));

        String output = TestUtil.captureStdOut(() -> new FindCommand("book").execute(tasks, new Ui(), null));

        assertTrue(output.contains("read book"));
        assertFalse(output.contains("sports club"));
    }

    @Test
    public void execute_noMatch_showsNoMatchesMessage() throws Exception {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("read book"));

        String output = TestUtil.captureStdOut(() ->
                new FindCommand("nonexistent").execute(tasks, new Ui(), null));

        assertTrue(output.toLowerCase().contains("no matches"));
    }
}
