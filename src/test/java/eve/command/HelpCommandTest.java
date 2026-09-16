package eve.command;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import eve.TestUtil;
import eve.Ui;
import eve.task.TaskList;

/** Tests {@link HelpCommand}: shows the full command list. */
public class HelpCommandTest {
    @Test
    public void execute_showsEveryCommandUsage() throws Exception {
        String output = TestUtil.captureStdOut(() ->
                new HelpCommand().execute(new TaskList(), new Ui(), null));

        assertTrue(output.contains("todo <description>"));
        assertTrue(output.contains("bye"));
    }
}
