package jasper.command;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;

import org.junit.jupiter.api.Test;

import jasper.JasperException;
import jasper.task.TaskList;
import jasper.task.TaskStub;

public class MarkCommandTest {
    @Test
    public void constructor_singleIntegerArgument_success() {
        try {
            MarkCommand cmd = new MarkCommand("10");
            assertNotNull(cmd);
        } catch (JasperException e) {
            fail("Exception should not be thrown for valid single integer input");
        }
    }

    @Test
    public void constructor_rangeArgument_success() {
        try {
            MarkCommand cmd = new MarkCommand("1..3");
            assertNotNull(cmd);
        } catch (JasperException e) {
            fail("Exception should not be thrown for valid range input");
        }
    }

    @Test
    public void constructor_nonIntegerArgument_exceptionThrown() {
        assertThrows(JasperException.class, () -> new MarkCommand("one"));
    }

    @Test
    public void constructor_tooManyPartsArgument_exceptionThrown() {
        assertThrows(JasperException.class, () -> new MarkCommand("1..2..3"));
    }

    @Test
    public void execute_startExceedsStop_exceptionThrown() {
        TaskList tasks = new TaskList();
        try {
            Command cmd = new MarkCommand("5..2");
            assertThrows(JasperException.class, () -> cmd.execute(tasks));
        } catch (JasperException e) {
            fail("Constructor should not throw exception for syntactically valid integers");
        }
    }

    @Test
    public void execute_validSingleTaskIndex_success() {
        TaskStub stub = new TaskStub();
        TaskList tasks = new TaskList(List.of(stub));
        try {
            Command cmd = new MarkCommand("1");
            cmd.execute(tasks);
            assertTrue(stub.isDone());
        } catch (JasperException e) {
            fail("Exception should not be thrown for valid task execution");
        }
    }

    @Test
    public void execute_validRangeTaskIndex_success() {
        TaskStub stub1 = new TaskStub();
        TaskStub stub2 = new TaskStub();
        TaskList tasks = new TaskList(List.of(stub1, stub2));
        try {
            Command cmd = new MarkCommand("1..2");
            cmd.execute(tasks);
            assertTrue(stub1.isDone());
            assertTrue(stub2.isDone());
        } catch (JasperException e) {
            fail("Exception should not be thrown for valid range execution");
        }
    }

    @Test
    public void execute_invalidTaskIndex_exceptionThrown() {
        TaskList tasks = new TaskList(); // Using empty list to simulate out-of-bounds
        try {
            Command cmd = new MarkCommand("1");
            assertThrows(JasperException.class, () -> cmd.execute(tasks));
        } catch (JasperException e) {
            fail("Constructor should not throw exception for valid integer format");
        }
    }
}
