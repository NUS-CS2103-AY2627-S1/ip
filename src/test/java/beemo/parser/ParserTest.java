package beemo.parser;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Path;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import beemo.BeemoException;
import beemo.command.AddCommand;
import beemo.command.Command;
import beemo.command.DeleteCommand;
import beemo.command.ExitCommand;
import beemo.command.FindCommand;
import beemo.command.HelpCommand;
import beemo.command.ListCommand;
import beemo.command.MarkCommand;
import beemo.command.UnmarkCommand;
import beemo.storage.Storage;
import beemo.task.Deadline;
import beemo.task.Event;
import beemo.task.Task;
import beemo.task.TaskList;
import beemo.task.Todo;
import beemo.ui.Ui;

class ParserTest {
    @TempDir
    private Path tempDirectory;

    @Test
    void parse_supportedCommands_correctCommandTypes() throws BeemoException {
        assertAll(
                () -> assertInstanceOf(ExitCommand.class, Parser.parse("bye")),
                () -> assertInstanceOf(HelpCommand.class, Parser.parse("help")),
                () -> assertInstanceOf(ListCommand.class, Parser.parse("list")),
                () -> assertInstanceOf(MarkCommand.class, Parser.parse("mark 1")),
                () -> assertInstanceOf(UnmarkCommand.class, Parser.parse("unmark 1")),
                () -> assertInstanceOf(DeleteCommand.class, Parser.parse("delete 1")),
                () -> assertInstanceOf(FindCommand.class, Parser.parse("find book")),
                () -> assertInstanceOf(AddCommand.class, Parser.parse("todo read")),
                () -> assertInstanceOf(AddCommand.class,
                        Parser.parse("deadline return /by 2026-08-30")),
                () -> assertInstanceOf(AddCommand.class,
                        Parser.parse("event meeting /from 2pm /to 4pm")));
    }

    @Test
    void parse_commandWithIrregularWhitespace_commandParsed() throws BeemoException {
        Task task = executeAddCommand("  deadline   return book   /by   2026-08-30  ");

        Deadline deadline = assertInstanceOf(Deadline.class, task);
        assertEquals("return book", deadline.getDescription());
        assertEquals(LocalDate.of(2026, 8, 30), deadline.getDueDate());
    }

    @Test
    void parse_todoCommand_addsTodoWithDescription() throws BeemoException {
        Task task = executeAddCommand("todo read book");

        assertInstanceOf(Todo.class, task);
        assertEquals("read book", task.getDescription());
    }

    @Test
    void parse_deadlineCommand_addsDeadlineWithDate() throws BeemoException {
        Task task = executeAddCommand("deadline return book /by 2026-08-30");

        Deadline deadline = assertInstanceOf(Deadline.class, task);
        assertEquals("return book", deadline.getDescription());
        assertEquals(LocalDate.of(2026, 8, 30), deadline.getDueDate());
    }

    @Test
    void parse_eventCommand_addsEventWithTimes() throws BeemoException {
        Task task = executeAddCommand("event meeting /from 2026-09-16 /to 2026-09-17");

        Event event = assertInstanceOf(Event.class, task);
        assertEquals("meeting", event.getDescription());
        assertEquals("2026-09-16", event.getStartTime());
        assertEquals("2026-09-17", event.getEndTime());
    }

    @Test
    void parse_eventWithSameStartAndEndTime_exceptionThrown() {
        BeemoException exception = assertThrows(BeemoException.class,
                () -> Parser.parse("event meeting /from 4pm /to 4pm"));

        assertEquals("OOPS... An event must end after it starts. ╥‸╥ "
                + "Enter an '/to' time later than the '/from' time.", exception.getMessage());
    }

    @Test
    void parse_eventEndDateBeforeStartDate_exceptionThrown() {
        BeemoException exception = assertThrows(BeemoException.class,
                () -> Parser.parse("event test /from 2026-03-03 /to 2025-03-03"));

        assertEquals("OOPS... An event cannot end before it starts. ╥‸╥ "
                + "Enter an '/to' date later than or equal to the '/from' date.",
                exception.getMessage());
    }

    @Test
    void parse_eventEndDateTimeBeforeStartDateTime_exceptionThrown() {
        BeemoException exception = assertThrows(BeemoException.class,
                () -> Parser.parse(
                        "event midterm /from 2026-09-16 9pm /to 2026-09-16 1pm"));

        assertEquals("OOPS... An event must end after it starts. ╥‸╥ "
                + "Enter an '/to' date and time later than '/from'.",
                exception.getMessage());
    }

    @Test
    void parse_eventWithValidDateTimes_addsEvent() throws BeemoException {
        Task task = executeAddCommand(
                "event midterm /from 2026-09-16 1pm /to 2026-09-16 9pm");

        Event event = assertInstanceOf(Event.class, task);
        assertEquals("2026-09-16 1pm", event.getStartTime());
        assertEquals("2026-09-16 9pm", event.getEndTime());
    }

    @Test
    void parse_eventWithDuplicateTimeDelimiter_exceptionThrown() {
        BeemoException duplicateFromException = assertThrows(BeemoException.class,
                () -> Parser.parse("event conf /from 1pm /to 2pm /from 3pm"));
        BeemoException duplicateToException = assertThrows(BeemoException.class,
                () -> Parser.parse("event conf /from 1pm /to 2pm /to 3pm"));

        String expectedMessage = "OOPS... An event must contain exactly one '/from' and one '/to'. ╥‸╥ "
                + "Remove the duplicate time delimiter.";
        assertAll(
                () -> assertEquals(expectedMessage, duplicateFromException.getMessage()),
                () -> assertEquals(expectedMessage, duplicateToException.getMessage()));
    }

    @Test
    void parse_eventWithValidClockTimes_addsEvent() throws BeemoException {
        Task task = executeAddCommand("event meeting /from 2pm /to 4pm");

        Event event = assertInstanceOf(Event.class, task);
        assertEquals("2pm", event.getStartTime());
        assertEquals("4pm", event.getEndTime());
    }

    @Test
    void parse_overnightEvent_addsEvent() throws BeemoException {
        Task task = executeAddCommand("event night shift /from 11pm /to 1am");

        Event event = assertInstanceOf(Event.class, task);
        assertEquals("11pm", event.getStartTime());
        assertEquals("1am", event.getEndTime());
    }

    @Test
    void parse_eventWithInvalidDateOrTime_exceptionThrown() {
        String expectedMessage = "OOPS... Event dates and times are invalid. ╥‸╥ "
                + "Use yyyy-MM-dd, a clock time such as 2pm, or yyyy-MM-dd 2pm.";

        assertAll(
                () -> assertEquals(expectedMessage, assertThrows(BeemoException.class,
                        () -> Parser.parse(
                                "event CS2100 Quiz /from 2026-02-29 90pm /to 100pm"))
                        .getMessage()),
                () -> assertEquals(expectedMessage, assertThrows(BeemoException.class,
                        () -> Parser.parse("event hello /from hello /to hello"))
                        .getMessage()),
                () -> assertEquals(expectedMessage, assertThrows(BeemoException.class,
                        () -> Parser.parse(
                                "event test3 /from 2026 09 30 /to 2026 10 01"))
                        .getMessage()),
                () -> assertEquals(expectedMessage, assertThrows(BeemoException.class,
                        () -> Parser.parse("event meeting /from 2026-09-16 /to 4pm"))
                        .getMessage()));
    }

    @Test
    void parse_invalidCommands_exceptionThrown() {
        assertAll(
                () -> assertThrows(BeemoException.class, () -> Parser.parse("   ")),
                () -> assertThrows(BeemoException.class, () -> Parser.parse("blah")),
                () -> assertThrows(BeemoException.class, () -> Parser.parse("mark")),
                () -> assertThrows(BeemoException.class, () -> Parser.parse("mark two")),
                () -> assertThrows(BeemoException.class, () -> Parser.parse("find")),
                () -> assertThrows(BeemoException.class, () -> Parser.parse("todo")),
                () -> assertThrows(BeemoException.class,
                        () -> Parser.parse("deadline return book")),
                () -> assertThrows(BeemoException.class,
                        () -> Parser.parse("deadline return book /by Sunday")),
                () -> assertThrows(BeemoException.class,
                        () -> Parser.parse("event meeting /from Monday")));
    }

    private Task executeAddCommand(String input) throws BeemoException {
        Command command = Parser.parse(input);
        TaskList tasks = new TaskList();
        Storage storage = new Storage(tempDirectory.resolve("data/tasks.txt"));
        command.execute(tasks, new Ui(), storage);
        assertEquals(1, tasks.size());
        return tasks.asList().get(0);
    }
}
