package magnus.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import magnus.exception.CommandSyntaxException;
import magnus.exception.DuplicateTaskException;
import magnus.exception.MagnusException;
import magnus.task.DeadlineTask;
import magnus.task.TaskList;

/**
 * Tests validation and task creation for {@link DeadlineCommand}.
 */
public class DeadlineCommandTest {
    @Test
    public void execute_validArguments_addsDeadlineTask() throws MagnusException {
        TaskList tasks = new TaskList();
        DeadlineCommand command = new DeadlineCommand(tasks);

        String response = command.execute("submit report /by 20/09/2026 1730");

        assertEquals("\tClock set - I've added this Deadline task:\n\n\t[D][ ] submit report "
                + "(by: Sep 20, 2026 17:30)\n\n"
                + "\tYou now have 1 task in the list.", response);
        assertInstanceOf(DeadlineTask.class, tasks.getTask(0));
        assertEquals("D,0,submit report,20/09/2026 1730", tasks.getTask(0).serialize());
    }

    @Test
    public void execute_missingDescription_throwsCommandSyntaxException() {
        DeadlineCommand command = new DeadlineCommand(new TaskList());

        assertThrows(CommandSyntaxException.class, () -> command.execute(new String[0]));
        assertThrows(CommandSyntaxException.class, () -> command.execute("   "));
    }

    @Test
    public void execute_multipleArguments_throwsCommandSyntaxException() {
        DeadlineCommand command = new DeadlineCommand(new TaskList());

        assertThrows(CommandSyntaxException.class, () -> command.execute("report", "20/09/2026 1730"));
    }

    @Test
    public void execute_missingByField_throwsCommandSyntaxExceptionWithoutAddingTask() {
        TaskList tasks = new TaskList();
        DeadlineCommand command = new DeadlineCommand(tasks);

        assertThrows(CommandSyntaxException.class, () -> command.execute("submit report 20/09/2026 1730"));

        assertEquals(0, tasks.getTaskCount());
    }

    @Test
    public void execute_invalidDeadline_throwsCommandSyntaxExceptionWithoutAddingTask() {
        TaskList tasks = new TaskList();
        DeadlineCommand command = new DeadlineCommand(tasks);

        assertThrows(CommandSyntaxException.class, () -> command.execute("submit report /by 31/09/2026 1730"));

        assertEquals(0, tasks.getTaskCount());
    }

    @Test
    public void execute_duplicateDeadline_throwsDuplicateTaskException() throws MagnusException {
        TaskList tasks = new TaskList();
        DeadlineCommand command = new DeadlineCommand(tasks);
        command.execute("submit report /by 20/09/2026 1730");

        assertThrows(DuplicateTaskException.class, () ->
                command.execute("submit report /by 20/09/2026 1730"));

        assertEquals(1, tasks.getTaskCount());
    }
}
