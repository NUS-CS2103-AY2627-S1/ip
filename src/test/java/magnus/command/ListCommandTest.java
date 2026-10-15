package magnus.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

import magnus.exception.CommandSyntaxException;
import magnus.task.TaskList;
import magnus.task.ToDoTask;

/**
 * Tests output and argument validation for {@link ListCommand}.
 */
public class ListCommandTest {
    @Test
    public void execute_populatedTaskList_returnsNumberedTasks() throws CommandSyntaxException {
        TaskList tasks = new TaskList(List.of(
                new ToDoTask("read book"), new ToDoTask("write notes")));

        String response = new ListCommand(tasks).execute();

        String expected = String.join(System.lineSeparator(),
                "\tHere's the current position - your full task list:",
                "",
                "\t1. [T][ ] read book",
                "\t2. [T][ ] write notes");
        assertEquals(expected, response);
    }

    @Test
    public void execute_emptyTaskList_announcesNoTasks() throws CommandSyntaxException {
        String response = new ListCommand(new TaskList()).execute();

        assertEquals("\tThere are no tasks on the board yet.", response);
    }

    @Test
    public void execute_withArguments_throwsCommandSyntaxException() {
        ListCommand command = new ListCommand(new TaskList());

        CommandSyntaxException exception = assertThrows(
                CommandSyntaxException.class, () -> command.execute("extra"));

        assertEquals("\tThat move has extra pieces - the list command does not accept arguments.\n"
                + "\tUsage: list", exception.getMessage());
    }
}
