package magnus.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import magnus.exception.CommandSyntaxException;
import magnus.task.EventTask;
import magnus.task.TaskList;
import magnus.task.ToDoTask;

/**
 * Tests date-range filtering and validation for {@link ListEventCommand}.
 */
public class ListEventCommandTest {
    @Test
    public void execute_inclusiveRange_returnsOnlyFullyEnclosedEvents() throws CommandSyntaxException {
        EventTask enclosedEvent = new EventTask(
                "conference", LocalDateTime.of(2026, 9, 20, 0, 0),
                LocalDateTime.of(2026, 9, 22, 23, 59));
        EventTask overlappingEvent = new EventTask(
                "long trip", LocalDateTime.of(2026, 9, 19, 23, 0),
                LocalDateTime.of(2026, 9, 21, 10, 0));
        TaskList tasks = new TaskList(List.of(
                enclosedEvent, overlappingEvent, new ToDoTask("buy tickets")));

        String response = new ListEventCommand(tasks).execute("20/09/2026 22/09/2026");

        String expected = String.join(System.lineSeparator(),
                "\tBoard surveyed - here are your Event tasks in that date range:",
                "",
                "\t1. [E][ ] conference (from: Sep 20, 2026 00:00 to: Sep 22, 2026 23:59)");
        assertEquals(expected, response);
    }

    @Test
    public void execute_noEventsInRange_announcesNoMatches() throws CommandSyntaxException {
        TaskList tasks = new TaskList(List.of(new ToDoTask("buy tickets")));

        String response = new ListEventCommand(tasks).execute("20/09/2026 22/09/2026");

        assertEquals("\tBoard surveyed - there are no Event tasks in that date range.", response);
    }

    @Test
    public void execute_missingDates_throwsCommandSyntaxException() {
        ListEventCommand command = new ListEventCommand(new TaskList());

        assertThrows(CommandSyntaxException.class, () -> command.execute(new String[0]));
        assertThrows(CommandSyntaxException.class, () -> command.execute("   "));
    }

    @Test
    public void execute_multipleArgumentFields_throwsCommandSyntaxException() {
        ListEventCommand command = new ListEventCommand(new TaskList());

        assertThrows(CommandSyntaxException.class, () ->
                command.execute("20/09/2026 21/09/2026", "extra"));
    }

    @Test
    public void execute_oneDate_throwsCommandSyntaxException() {
        ListEventCommand command = new ListEventCommand(new TaskList());

        CommandSyntaxException exception = assertThrows(
                CommandSyntaxException.class, () -> command.execute("20/09/2026"));

        assertEquals("\tThe board needs two dates - the list_event command requires exactly two dates.\n"
                + "\tUsage: list_event <dd/MM/yyyy> <dd/MM/yyyy>", exception.getMessage());
    }

    @Test
    public void execute_invalidDate_throwsCommandSyntaxException() {
        ListEventCommand command = new ListEventCommand(new TaskList());

        assertThrows(CommandSyntaxException.class, () ->
                command.execute("31/09/2026 01/10/2026"));
    }

    @Test
    public void execute_reversedRange_throwsCommandSyntaxException() {
        ListEventCommand command = new ListEventCommand(new TaskList());

        CommandSyntaxException exception = assertThrows(
                CommandSyntaxException.class, () -> command.execute("22/09/2026 20/09/2026"));

        assertEquals("\tThat move reverses the clock - the start date must be before or equal to the end date.",
                exception.getMessage());
    }
}
