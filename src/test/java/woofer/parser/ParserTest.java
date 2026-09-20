package woofer.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import woofer.exception.WooferException;
import woofer.task.Deadline;
import woofer.task.Event;
import woofer.task.Task;
import woofer.task.Todo;

/**
 * Tests the parsing of Woofer commands.
 */
public class ParserTest {
    private final Parser parser = new Parser();

    /**
     * Verifies that a todo command creates a todo with the expected description.
     *
     * @throws WooferException when the valid command cannot be parsed.
     */
    @Test
    public void parseTodoCommandCreatesTodo() throws WooferException {
        Task task = parser.parseTask("todo read book");

        assertInstanceOf(Todo.class, task);
        assertEquals("read book", task.getDescription());
        assertEquals("[T][ ] read book", task.getDisplayText());
    }

    /**
     * Verifies that a deadline command parses its date as a local date.
     *
     * @throws WooferException when the valid command cannot be parsed.
     */
    @Test
    public void parseDeadlineCommandParsesDate() throws WooferException {
        Task task = parser.parseTask("deadline return book /by 2019-12-02");

        Deadline deadline = assertInstanceOf(Deadline.class, task);
        assertEquals(LocalDate.of(2019, 12, 2), deadline.getBy());
        assertEquals("[D][ ] return book (by: Dec 02 2019)", deadline.getDisplayText());
    }

    /**
     * Verifies that malformed dates are rejected with a Woofer exception.
     */
    @Test
    public void parseDeadlineCommandRejectsInvalidDate() {
        assertThrows(WooferException.class, () -> parser.parseTask(
                "deadline return book /by 02/12/2019"));
    }

    /**
     * Verifies that malformed task commands explain their complete required format.
     */
    @Test
    public void malformedTaskCommandReportsRequiredFormat() {
        WooferException exception = assertThrows(
                WooferException.class, () -> parser.parseTask("todo"));

        assertEquals("Required format: todo <description>.", exception.getMessage());
    }

    /**
     * Verifies that malformed deadline dates explain the complete required format.
     */
    @Test
    public void malformedDeadlineDateReportsRequiredFormat() {
        WooferException exception = assertThrows(WooferException.class, () -> parser.parseTask(
                "deadline return book /by 02/12/2019"));

        assertEquals("Required format: deadline <description> /by <yyyy-MM-dd>.",
                exception.getMessage());
    }

    /**
     * Verifies that a find command is recognized and its keyword is extracted.
     *
     * @throws WooferException when the valid command cannot be parsed.
     */
    @Test
    public void parseFindCommandExtractsKeyword() throws WooferException {
        assertEquals(Parser.CommandType.FIND, parser.parseCommandType("find book"));
        assertEquals("book", parser.parseFindKeyword("find book"));
    }

    /**
     * Verifies that the undo command is recognized separately from task commands.
     *
     * @throws WooferException when the valid command cannot be parsed.
     */
    @Test
    public void parseUndoCommandRecognizesUndo() throws WooferException {
        assertEquals(Parser.CommandType.UNDO, parser.parseCommandType("undo"));
    }

    /**
     * Verifies that a find command without a keyword is rejected.
     */
    @Test
    public void parseFindCommandRejectsBlankKeyword() {
        assertThrows(WooferException.class, () -> parser.parseFindKeyword("find "));
    }

    /**
     * Verifies that a task number is extracted from a mark command.
     *
     * @throws WooferException when the valid command cannot be parsed.
     */
    @Test
    public void parseTaskNumberExtractsMarkNumber() throws WooferException {
        assertEquals(3, parser.parseTaskNumber("mark 3", Parser.CommandType.MARK));
    }

    /**
     * Verifies that a missing task number explains the complete required format.
     */
    @Test
    public void missingTaskNumberReportsRequiredFormat() {
        WooferException exception = assertThrows(WooferException.class, () ->
                parser.parseTaskNumber("mark", Parser.CommandType.MARK));

        assertEquals("Required format: mark <number>.", exception.getMessage());
    }

    /**
     * Checks spaces and tabs across command kinds and date separators.
     *
     * @throws WooferException when valid input is rejected.
     */
    @Test
    public void extraWhitespaceIsAccepted() throws WooferException {
        assertEquals(Parser.CommandType.LIST, parser.parseCommandType("  list  "));
        assertEquals("walk dog", parser.parseTask("  todo\t walk   dog ").getDescription());
        assertEquals(2, parser.parseTaskNumber(" mark\t 2 ", Parser.CommandType.MARK));
        assertEquals("walk dog", parser.parseFindKeyword(" find  walk   dog "));
        assertInstanceOf(Deadline.class, parser.parseTask("deadline task   /by\t 2026-09-16"));
    }

    /** Verifies that invalid records cannot be introduced through task commands. */
    @Test
    public void unsafeAndMalformedTasksAreRejected() {
        String[] commands = {
            "todo a|b", "todo first\nsecond", "todo bad\u0000text",
            "deadline test /by 2026-02-30", "deadline test /by 2025-02-29",
            "deadline test /by 2026-09-16 /by 2026-09-17",
            "event test /from 2026-09-16 /to 2026-09-16",
            "event test /from 2026-09-17 /to 2026-09-16",
            "event test /from extra /from 2026-09-16 /to 2026-09-17",
            "event test /to extra /from 2026-09-16 /to 2026-09-17",
            "event test /from 2026-09-16 /to", "deadline /by 2026-09-16"
        };
        for (String command : commands) {
            assertThrows(WooferException.class, () -> parser.parseTask(command), command);
        }
    }

    /** Verifies that empty commands and unwanted arguments produce user-facing errors. */
    @Test
    public void invalidCommandsAreRejected() {
        for (String command : new String[]{null, "", "  ", "list extra", "undo 1", "bye now"}) {
            assertThrows(WooferException.class, () -> parser.parseCommandType(command));
        }
        for (String number : new String[]{"0", "-1", "+1", "1.5", "1 2", "9999999999999999"}) {
            assertThrows(WooferException.class, () ->
                    parser.parseTaskNumber("delete " + number, Parser.CommandType.DELETE));
        }
    }

    /**
     * Checks every command routes to the appropriate service operation.
     *
     * @throws WooferException when valid commands fail.
     */
    @Test
    public void classifiesEveryCommand() throws WooferException {
        String[] commands = {"bye", "undo", "list", "find dog", "delete 1", "mark 1", "unmark 1", "todo dog"};
        Parser.CommandType[] types = {Parser.CommandType.EXIT, Parser.CommandType.UNDO, Parser.CommandType.LIST,
            Parser.CommandType.FIND, Parser.CommandType.DELETE, Parser.CommandType.MARK,
            Parser.CommandType.UNMARK, Parser.CommandType.ADD};
        for (int i = 0; i < commands.length; i++) {
            assertEquals(types[i], parser.parseCommandType(commands[i]), commands[i]);
        }
        assertEquals(1, parser.parseTaskNumber("unmark 1", Parser.CommandType.UNMARK));
        assertEquals(Integer.MAX_VALUE,
                parser.parseTaskNumber("delete 2147483647", Parser.CommandType.DELETE));
        assertThrows(WooferException.class, () -> parser.parseTaskNumber("list", Parser.CommandType.LIST));
    }

    /**
     * Checks valid leap days, event dates, and non-English descriptions are preserved.
     *
     * @throws WooferException when valid commands fail.
     */
    @Test
    public void parsesLeapDayEventAndUnicode() throws WooferException {
        Event event = assertInstanceOf(Event.class,
                parser.parseTask("event 遛狗 /from 2028-02-29 /to 2028-03-01"));
        assertEquals("遛狗", event.getDescription());
        assertEquals(LocalDate.of(2028, 2, 29), event.getFrom());
        assertEquals(LocalDate.of(2028, 3, 1), event.getTo());
        assertEquals("buy food & treats!", parser.parseTask("todo buy food & treats!").getDescription());
    }

    /** Checks missing values, attached command prefixes, and unknown commands report required syntax. */
    @Test
    public void malformedVariantsReportSyntax() {
        String[] commands = {"todowalk", "deadlinefoo", "eventfoo", "dance", "TODO walk",
            "deadline", "deadline task /by", "deadline task /by /from 2026-09-17",
            "event", "event /from 2026-09-17 /to 2026-09-18",
            "event task /from /to 2026-09-18", "event task /from 2026-09-17",
            "event task /to 2026-09-18 /from 2026-09-17",
            "event task /from invalid /to 2026-09-18"};
        for (String command : commands) {
            WooferException error = assertThrows(WooferException.class, () -> parser.parseTask(command), command);
            assertTrue(error.getMessage().startsWith("Required format:"), command);
        }
    }

}
