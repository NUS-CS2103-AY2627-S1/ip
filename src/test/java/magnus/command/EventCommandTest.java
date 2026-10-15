package magnus.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import magnus.exception.CommandSyntaxException;
import magnus.exception.DuplicateTaskException;
import magnus.exception.MagnusException;
import magnus.task.EventTask;
import magnus.task.TaskList;

/**
 * Tests validation and task creation for {@link EventCommand}.
 */
public class EventCommandTest {
    @Test
    public void execute_validArguments_addsEventTask() throws MagnusException {
        TaskList tasks = new TaskList();
        EventCommand command = new EventCommand(tasks);

        String response = command.execute(
                "project meeting /from 20/09/2026 0900 /to 20/09/2026 1030");

        assertEquals("\tThe position is prepared - I've added this Event task:\n\n\t[E][ ] project meeting "
                + "(from: Sep 20, 2026 09:00 to: Sep 20, 2026 10:30)\n\n"
                + "\tYou now have 1 task in the list.", response);
        assertInstanceOf(EventTask.class, tasks.getTask(0));
        assertEquals("E,0,project meeting,20/09/2026 0900-20/09/2026 1030",
                tasks.getTask(0).serialize());
    }

    @Test
    public void execute_missingDescription_throwsCommandSyntaxException() {
        EventCommand command = new EventCommand(new TaskList());

        assertThrows(CommandSyntaxException.class, () -> command.execute(new String[0]));
        assertThrows(CommandSyntaxException.class, () -> command.execute("   "));
    }

    @Test
    public void execute_multipleArguments_throwsCommandSyntaxException() {
        EventCommand command = new EventCommand(new TaskList());

        assertThrows(CommandSyntaxException.class, () -> command.execute("meeting", "today", "tomorrow"));
    }

    @Test
    public void execute_missingTimeField_throwsCommandSyntaxExceptionWithoutAddingTask() {
        TaskList tasks = new TaskList();
        EventCommand command = new EventCommand(tasks);

        assertThrows(CommandSyntaxException.class, () ->
                command.execute("meeting /from 20/09/2026 0900"));

        assertEquals(0, tasks.getTaskCount());
    }

    @Test
    public void execute_endBeforeStart_throwsCommandSyntaxExceptionWithoutAddingTask() {
        TaskList tasks = new TaskList();
        EventCommand command = new EventCommand(tasks);

        assertThrows(CommandSyntaxException.class, () -> command.execute(
                "meeting /from 20/09/2026 1030 /to 20/09/2026 0900"));

        assertEquals(0, tasks.getTaskCount());
    }

    @Test
    public void execute_duplicateEvent_throwsDuplicateTaskException() throws MagnusException {
        TaskList tasks = new TaskList();
        EventCommand command = new EventCommand(tasks);
        String arguments = "meeting /from 20/09/2026 0900 /to 20/09/2026 1030";
        command.execute(arguments);

        assertThrows(DuplicateTaskException.class, () -> command.execute(arguments));

        assertEquals(1, tasks.getTaskCount());
    }
}
