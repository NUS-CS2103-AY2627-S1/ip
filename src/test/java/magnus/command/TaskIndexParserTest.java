package magnus.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import magnus.exception.CommandSyntaxException;
import magnus.exception.MagnusException;
import magnus.exception.TaskNotFoundException;

/**
 * Tests task-number validation shared by task-specific commands.
 */
public class TaskIndexParserTest {
    @Test
    public void parseTaskIndex_validTaskNumber_returnsZeroBasedIndex() throws MagnusException {
        int taskIndex = TaskIndexParser.parseTaskIndex(new String[] { "2" }, "mark", 3);

        assertEquals(1, taskIndex);
    }

    @Test
    public void parseTaskIndex_missingTaskNumber_throwsCommandSyntaxException() {
        CommandSyntaxException exception = assertThrows(
                CommandSyntaxException.class, () -> TaskIndexParser.parseTaskIndex(new String[0], "mark", 3));

        assertEquals("\tThat move is incomplete - please tell me the task number.\n"
                + "\tUsage: mark <task number>", exception.getMessage());
    }

    @Test
    public void parseTaskIndex_extraArgument_throwsCommandSyntaxException() {
        CommandSyntaxException exception = assertThrows(
                CommandSyntaxException.class, () -> TaskIndexParser.parseTaskIndex(
                        new String[] { "1", "2" }, "unmark", 3));

        assertEquals("\tToo many pieces in that move - the unmark command accepts only one argument.\n"
                + "\tUsage: unmark <task number>", exception.getMessage());
    }

    @Test
    public void parseTaskIndex_nonNumericTaskNumber_throwsCommandSyntaxException() {
        CommandSyntaxException exception = assertThrows(
                CommandSyntaxException.class, () -> TaskIndexParser.parseTaskIndex(
                        new String[] { "first" }, "delete", 3));

        assertEquals("\tThat square is not a task number - 'first' is not valid.\n"
                + "\tUsage: delete <task number>", exception.getMessage());
    }

    @Test
    public void parseTaskIndex_signedTaskNumber_throwsCommandSyntaxException() {
        assertThrows(CommandSyntaxException.class, () -> TaskIndexParser.parseTaskIndex(
                new String[] { "+1" }, "delete", 3));
    }

    @Test
    public void parseTaskIndex_taskNumberBelowRange_throwsTaskNotFoundException() {
        TaskNotFoundException exception = assertThrows(
                TaskNotFoundException.class, () -> TaskIndexParser.parseTaskIndex(
                        new String[] { "0" }, "mark", 3));

        assertEquals("\tThat task is off the board - I can't find task number 0.", exception.getMessage());
    }

    @Test
    public void parseTaskIndex_taskNumberAboveRange_throwsTaskNotFoundException() {
        TaskNotFoundException exception = assertThrows(
                TaskNotFoundException.class, () -> TaskIndexParser.parseTaskIndex(
                        new String[] { "4" }, "mark", 3));

        assertEquals("\tThat task is off the board - I can't find task number 4.", exception.getMessage());
    }

    @Test
    public void parseTaskIndex_excessivelyLargeTaskNumber_throwsCommandSyntaxException() {
        String taskNumber = "999999999999999999999999999999999999";

        CommandSyntaxException exception = assertThrows(
                CommandSyntaxException.class, () -> TaskIndexParser.parseTaskIndex(
                        new String[] { taskNumber }, "delete", 3));

        assertEquals("\tThat square is not a task number - '" + taskNumber
                + "' is not valid.\n\tUsage: delete <task number>", exception.getMessage());
    }
}
