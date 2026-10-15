package anders.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import anders.AndersException;
import anders.command.AddCommand;
import anders.command.Command;
import anders.command.ExitCommand;
import anders.command.FindCommand;
import anders.command.MarkCommand;
import anders.command.TagCommand;
import anders.task.Deadline;
import anders.task.Event;
import anders.task.Task;
import anders.task.Todo;

/** Tests command parsing and validation, which drive Anders' core input flow. */
public class ParserTest {

    private final Parser parser = new Parser();

    @Test
    public void parseTask_todoCommand_createsTodoWithDescription() {
        Task task = parser.parseTask("todo read book");

        assertInstanceOf(Todo.class, task);
        assertEquals("read book", task.getDescription());
    }

    @Test
    public void parseTask_creationTags_createsNormalizedTagsForAllTaskTypes() {
        Todo todo = assertInstanceOf(Todo.class, parser.parseTask("todo read book /tags #Fun #school"));
        Deadline deadline = assertInstanceOf(Deadline.class,
                parser.parseTask("deadline return book /by 2019-12-02 /tags #Admin"));
        Event event = assertInstanceOf(Event.class, parser.parseTask(
                "event project meeting /from 2025-01-01 14:00 /to 2025-01-01 16:00 /tags #Project #meeting"));

        assertEquals(java.util.List.of("#fun", "#school"), todo.getTags());
        assertEquals(java.util.List.of("#admin"), deadline.getTags());
        assertEquals(java.util.List.of("#project", "#meeting"), event.getTags());
    }

    @Test
    public void parseTask_deadlineCommand_createsDeadlineWithDueDate() {
        Task task = parser.parseTask("deadline return book /by 2/12/2019 1800");

        Deadline deadline = assertInstanceOf(Deadline.class, task);
        assertEquals("return book", deadline.getDescription());
        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0), deadline.getBy());
        assertEquals("2/12/2019 1800", deadline.getByText());
        assertEquals(true, deadline.toString().contains("Dec 02 2019 6.00 pm"));
    }

    @Test
    public void parseTask_eventCommand_createsEventWithTimeRange() {
        Task task = parser.parseTask("event project meeting /from 2025-01-01 14:00 /to 2025-01-01 16:00");

        Event event = assertInstanceOf(Event.class, task);
        assertEquals("project meeting", event.getDescription());
        assertEquals(LocalDateTime.of(2025, 1, 1, 14, 0), event.getFrom());
        assertEquals(LocalDateTime.of(2025, 1, 1, 16, 0), event.getTo());
    }

    @Test
    public void parseTask_eventCommand_acceptsSlashDates() {
        Event event = assertInstanceOf(Event.class,
                parser.parseTask("event project meeting /from 1/1/2025 1400 /to 1/1/2025 1600"));

        assertEquals(LocalDateTime.of(2025, 1, 1, 14, 0), event.getFrom());
        assertEquals(LocalDateTime.of(2025, 1, 1, 16, 0), event.getTo());
    }

    @Test
    public void parseTask_nonTaskCommand_failsFastWithAssertion() {
        assertThrows(AssertionError.class, () -> parser.parseTask("list"));
    }

    @Test
    public void parse_supportedCommands_createsExpectedCommandTypes() throws AndersException {
        assertInstanceOf(ExitCommand.class, Parser.parse("bye"));
        assertInstanceOf(AddCommand.class, Parser.parse("todo read book"));
        assertInstanceOf(MarkCommand.class, Parser.parse("mark 1"));
        assertInstanceOf(FindCommand.class, Parser.parse("find book"));
        assertInstanceOf(TagCommand.class, Parser.parse("tag 1 #fun"));
        assertInstanceOf(TagCommand.class, Parser.parse("untag 1 #fun"));
    }

    @Test
    public void commandWordAndArguments_commandWithArguments_areSeparated() {
        assertEquals("deadline", parser.commandWord("deadline return book /by Friday"));
        assertEquals("return book /by Friday", parser.arguments("deadline return book /by Friday"));
        assertEquals("list", parser.commandWord("list"));
        assertEquals("", parser.arguments("list"));
    }

    @Test
    public void parse_commandWithExtraWhitespace_acceptsCommand() throws AndersException {
        String input = "  event   project meeting   /from   2025-01-01 14:00   /to   2025-01-01 16:00  ";
        Command command = Parser.parse(input);

        assertInstanceOf(AddCommand.class, command);
    }

    @Test
    public void validate_invalidCommands_throwHelpfulExceptions() {
        assertThrows(AndersException.class, () -> parser.validate(""));
        assertThrows(AndersException.class, () -> parser.validate("todo"));
        assertThrows(AndersException.class, () -> parser.validate("deadline return book"));
        assertThrows(AndersException.class, () -> parser.validate("event meeting /from 2pm"));
        assertThrows(AndersException.class, () -> parser.validate("unknown command"));
        assertThrows(AndersException.class, () -> parser.validate("find"));
    }

    @Test
    public void validate_noArgumentCommands_rejectExtraArguments() {
        assertThrows(AndersException.class, () -> parser.validate("list extra"));
        assertThrows(AndersException.class, () -> parser.validate("bye extra"));
    }

    @Test
    public void validate_invalidDeadline_hasHelpfulFormatMessage() {
        AndersException exception = assertThrows(AndersException.class, () ->
                parser.validate("deadline return book /by tomorrow"));

        assertTrue(exception.getMessage().contains("d/M/yyyy"));
    }

    @Test
    public void validate_invalidTags_throwHelpfulExceptions() {
        String missingTagCommand = "todo read book /tags";
        String invalidTagCommand = "tag 1 fun";
        AndersException missingTags = assertThrows(AndersException.class, () ->
                parser.validate(missingTagCommand));
        AndersException invalidTag = assertThrows(AndersException.class, () ->
                parser.validate(invalidTagCommand));

        assertEquals("The /tags clause must contain at least one tag.", missingTags.getMessage());
        assertTrue(invalidTag.getMessage().contains("must start with #"));
    }

    @Test
    public void validate_tagCommands_requireTaskNumberAndTags() {
        assertThrows(AndersException.class, () -> parser.validate("tag"));
        assertThrows(AndersException.class, () -> parser.validate("untag 1"));
        assertThrows(AndersException.class, () -> parser.validate("tag 1 #fun #"));
        assertThrows(AndersException.class, () -> parser.validate("find #fun!"));
    }

    @Test
    public void parse_invalidDatesAndReversedEvents_returnsHelpfulErrors() {
        AndersException invalidDate = assertThrows(AndersException.class, () ->
                Parser.parse("deadline report /by 31/2/2026"));
        assertTrue(invalidDate.getMessage().contains("real date"));
        AndersException invalidEvent = assertThrows(AndersException.class, () ->
                Parser.parse("event meeting /from 2026-10-02 1600 /to 2026-10-02 1400"));
        assertEquals("An event must end at or after it starts.", invalidEvent.getMessage());
        assertThrows(AndersException.class, () ->
                Parser.parse("event meeting /from 2/10/2026 /to 31/11/2026"));
    }

    @Test
    public void parse_isoDeadlineTimes_acceptsBothFormatsWithTags() throws AndersException {
        for (String value : List.of("2026-10-02 1800", "2026-10-02 18:00")) {
            String input = "deadline submit report /by " + value + " /tags #School";

            assertInstanceOf(AddCommand.class, Parser.parse(input));
            Deadline deadline = assertInstanceOf(Deadline.class, parser.parseTask(input));
            assertEquals("submit report", deadline.getDescription());
            assertEquals(LocalDateTime.of(2026, 10, 2, 18, 0), deadline.getBy());
            assertEquals(List.of("#school"), deadline.getTags());
        }
    }

    @Test
    public void parse_invalidIsoDeadlineTimes_returnsHelpfulErrors() {
        for (String value : List.of("2026-02-30 1800", "2026-02-30 18:00",
                "2026-10-02 2400", "2026-10-02 18:60")) {
            AndersException exception = assertThrows(AndersException.class, () ->
                    Parser.parse("deadline report /by " + value));

            assertTrue(exception.getMessage().contains("real date"));
            assertTrue(exception.getMessage().contains("yyyy-MM-dd"));
            assertTrue(exception.getMessage().contains("HHmm"));
            assertTrue(exception.getMessage().contains("HH:mm"));
        }
    }

}
