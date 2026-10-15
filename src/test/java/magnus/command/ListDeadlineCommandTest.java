package magnus.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import magnus.exception.CommandSyntaxException;
import magnus.task.DeadlineTask;
import magnus.task.TaskList;
import magnus.task.ToDoTask;

/**
 * Tests date filtering and validation for {@link ListDeadlineCommand}.
 */
public class ListDeadlineCommandTest {
    @Test
    public void execute_matchingDate_returnsOnlyDeadlinesOnThatDate() throws CommandSyntaxException {
        DeadlineTask morningDeadline = new DeadlineTask(
                "submit report", LocalDateTime.of(2026, 9, 20, 9, 0));
        DeadlineTask eveningDeadline = new DeadlineTask(
                "return book", LocalDateTime.of(2026, 9, 20, 20, 0));
        TaskList tasks = new TaskList(List.of(
                morningDeadline,
                new DeadlineTask("pay bill", LocalDateTime.of(2026, 9, 21, 9, 0)),
                new ToDoTask("buy milk"),
                eveningDeadline));

        String response = new ListDeadlineCommand(tasks).execute("20/09/2026");

        String expected = String.join(System.lineSeparator(),
                "\tClock check - here are your Deadline tasks for that date:",
                "",
                "\t1. [D][ ] submit report (by: Sep 20, 2026 09:00)",
                "\t2. [D][ ] return book (by: Sep 20, 2026 20:00)");
        assertEquals(expected, response);
    }

    @Test
    public void execute_noDeadlinesOnDate_announcesNoMatches() throws CommandSyntaxException {
        TaskList tasks = new TaskList(List.of(new ToDoTask("buy milk")));

        String response = new ListDeadlineCommand(tasks).execute("20/09/2026");

        assertEquals("\tClock check complete - there are no Deadline tasks for that date.", response);
    }

    @Test
    public void execute_missingDate_throwsCommandSyntaxException() {
        ListDeadlineCommand command = new ListDeadlineCommand(new TaskList());

        assertThrows(CommandSyntaxException.class, () -> command.execute(new String[0]));
        assertThrows(CommandSyntaxException.class, () -> command.execute("   "));
    }

    @Test
    public void execute_multipleArguments_throwsCommandSyntaxException() {
        ListDeadlineCommand command = new ListDeadlineCommand(new TaskList());

        assertThrows(CommandSyntaxException.class, () -> command.execute("20/09/2026", "21/09/2026"));
    }

    @Test
    public void execute_malformedDate_throwsCommandSyntaxException() {
        ListDeadlineCommand command = new ListDeadlineCommand(new TaskList());

        CommandSyntaxException exception = assertThrows(
                CommandSyntaxException.class, () -> command.execute("2026-09-20"));

        assertEquals("\tThe clock rejects that date - enter it in dd/MM/yyyy format, "
                + "for example 20/09/2026.", exception.getMessage());
    }

    @Test
    public void execute_nonExistentDate_throwsCommandSyntaxException() {
        ListDeadlineCommand command = new ListDeadlineCommand(new TaskList());

        assertThrows(CommandSyntaxException.class, () -> command.execute("31/09/2026"));
    }
}
