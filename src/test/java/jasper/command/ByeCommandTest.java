package jasper.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;

import org.junit.jupiter.api.Test;

import jasper.JasperException;
import jasper.task.Task;
import jasper.task.TaskList;
import jasper.task.TaskStub;

public class ByeCommandTest {
    @Test
    public void constructor_emptyArgument_success() {
        try {
            ByeCommand cmd = new ByeCommand("");
            assertNotNull(cmd);
        } catch (JasperException e) {
            fail();
        }
    }

    @Test
    public void constructor_nonemptyArgument_exceptionThrown() {
        try {
            new ByeCommand("extra");
            fail();
        } catch (JasperException e) {
            assertEquals("Usage: bye", e.getMessage());
        }
    }

    @Test
    public void isQuit_emptyArgument_returnsTrue() {
        try {
            Task stub = new TaskStub();
            TaskList tasks = new TaskList(List.of(stub));
            CommandResult result = new ByeCommand("").execute(tasks);
            assertTrue(result.isQuit());
        } catch (JasperException e) {
            fail();
        }
    }
}
