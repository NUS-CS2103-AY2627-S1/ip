package olaf;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/**
 * Tests conversion of user input into validated commands.
 */
class ParserTest {
    private final Parser parser = new Parser();

    @Test
    void parse_noPayloadCommandsMixedCaseAndWhitespace_correctActionsReturned() throws CommandParseException {
        assertEquals(ParsedCommand.Action.EXIT, parser.parse(" \tByE  ").getAction());
        assertEquals(ParsedCommand.Action.LIST, parser.parse("  LiSt\t").getAction());
        assertEquals(ParsedCommand.Action.LIST, parser.parse("L\u0130ST").getAction());
    }

    @Test
    void parse_unknownCommand_commandListReturned() {
        for (String input : new String[] {"dance", "listing"}) {
            assertInvalidCommand(input,
                    "Unknown command. Use todo, deadline, event, find, list, mark, unmark, "
                            + "delete, reschedule, or bye.");
        }
    }

    @Test
    void parse_todoMixedCaseAndWhitespace_descriptionPreserved() throws CommandParseException {
        ParsedCommand command = parser.parse("  ToDo\t read  book  ");

        assertEquals(ParsedCommand.Action.ADD, command.getAction());
        assertEquals("[T][ ] read  book", command.getTask().toString());
    }

    @Test
    void parse_deadlineMixedCaseAndMarkers_validTaskReturned() throws CommandParseException {
        ParsedCommand command = parser.parse("  DeAdLiNe\treturn book \t/BY\t2026-09-01  ");

        assertEquals(ParsedCommand.Action.ADD, command.getAction());
        assertEquals("[D][ ] return book (by: Sep 01 2026)", command.getTask().toString());
    }

    @Test
    void parse_eventMixedCaseAndMarkers_validTaskReturned() throws CommandParseException {
        ParsedCommand command = parser.parse(" EvEnT meeting\t/FROM 2026-09-01 /TO\t2026-09-02 ");

        assertEquals(ParsedCommand.Action.ADD, command.getAction());
        assertEquals("[E][ ] meeting (from: Sep 01 2026 to: Sep 02 2026)", command.getTask().toString());
    }

    @Test
    void parse_eventEndBeforeStart_dateOrderErrorReturned() {
        assertErrorContains("event meeting /from 2026-09-02 /to 2026-09-01",
                Event.INVALID_DATE_RANGE_MESSAGE);
    }

    @Test
    void parse_markerInsideDescription_notTreatedAsDelimiter() throws CommandParseException {
        ParsedCommand command = parser.parse("deadline read /bylaws /by 2026-09-01");

        assertEquals("read /bylaws", command.getTask().getDescription());
    }

    @Test
    void parse_deadlineMalformed_usageMessageReturned() {
        String[] inputs = {"deadline", "deadline book", "deadline /by 2026-09-01",
                "deadline book /by", "deadline book/by 2026-09-01", "deadline book /by2026-09-01",
                "deadline book /by 2026-02-29", "deadline book /by 2026-9-01",
                "deadline book /by 01-09-2026", "deadline book /by 2026-09-01 /by 2026-09-02"};

        for (String input : inputs) {
            assertInvalidCommand(input, "Use 'deadline <description> /by <yyyy-MM-dd>' to add a deadline.");
        }
    }

    @Test
    void parse_eventMalformed_usageMessageReturned() {
        String[] inputs = {"event", "event meeting /from 2026-09-01", "event /from 2026-09-01 /to 2026-09-02",
                "event meeting /from /to 2026-09-02", "event meeting /from 2026-09-01 /to",
                "event meeting /to 2026-09-02 /from 2026-09-01",
                "event meeting/from 2026-09-01 /to 2026-09-02",
                "event meeting /from2026-09-01 /to 2026-09-02",
                "event meeting /from 2026-09-01/to 2026-09-02",
                "event meeting /from 2026-09-01 /to2026-09-02",
                "event meeting /from 2026-02-29 /to 2026-09-02",
                "event meeting /from 2026-09-01 /to 2026-02-29"};

        for (String input : inputs) {
            assertInvalidCommand(input,
                    "Use 'event <description> /from <yyyy-MM-dd> /to <yyyy-MM-dd>' to add an event.");
        }
    }

    @Test
    void parse_todoWithoutDescription_usageMessageReturned() {
        assertInvalidCommand("todo \t", "Use 'todo <description>' to add a ToDo.");
    }

    @Test
    void parse_taskNumberCommandsMixedCase_correctActionsReturned() throws CommandParseException {
        assertTaskNumberCommand(" MaRk\t+1 ", ParsedCommand.Action.MARK, 1);
        assertTaskNumberCommand(" UnMaRk  2 ", ParsedCommand.Action.UNMARK, 2);
        assertTaskNumberCommand(" DeLeTe 3 ", ParsedCommand.Action.DELETE, 3);
    }

    @Test
    void parse_nonpositiveTaskNumbers_leftForTaskListValidation() throws CommandParseException {
        assertTaskNumberCommand("mark 0", ParsedCommand.Action.MARK, 0);
        assertTaskNumberCommand("unmark -1", ParsedCommand.Action.UNMARK, -1);
        assertTaskNumberCommand("delete -2147483648", ParsedCommand.Action.DELETE, Integer.MIN_VALUE);
    }

    @Test
    void parse_taskNumberSyntaxInvalid_usageMessageReturned() {
        String[] arguments = {"", "one", "1 2", "1.5", "2147483648", "\u0000+1"};
        for (String argument : arguments) {
            assertInvalidCommand("mark " + argument, "Use 'mark <task number>' to mark a task as done.");
            assertInvalidCommand("unmark " + argument, "Use 'unmark <task number>' to mark a task as not done.");
            assertInvalidCommand("delete " + argument, "Use 'delete <task number>' to delete a task.");
        }
    }

    @Test
    void parse_findKeyword_findCommandReturned() throws CommandParseException {
        ParsedCommand command = parser.parse("find book");

        assertEquals(ParsedCommand.Action.FIND, command.getAction());
        assertEquals("book", command.getKeyword());
    }

    @Test
    void parse_findCommandUsesMixedCase_findCommandReturned() throws CommandParseException {
        ParsedCommand command = parser.parse("FiNd book");

        assertEquals(ParsedCommand.Action.FIND, command.getAction());
        assertEquals("book", command.getKeyword());
    }

    @Test
    void parse_findMultiWordPhrase_completePhraseReturned() throws CommandParseException {
        ParsedCommand command = parser.parse("  find   project meeting  ");

        assertEquals("project meeting", command.getKeyword());
    }

    @Test
    void parse_findWithoutKeyword_commandParseExceptionThrown() {
        CommandParseException exception = assertThrows(CommandParseException.class,
                () -> parser.parse("find   "));

        assertEquals("Missing search keyword. Use 'find <keyword>' to find matching tasks.", exception.getMessage());
    }

    @Test
    void parse_rescheduleDeadlineMixedCaseAndWhitespace_dateAndTaskNumberReturned()
            throws CommandParseException {
        ParsedCommand command = parser.parse("  ReScHeDuLe\t+2  /BY\t2028-02-29  ");

        assertEquals(ParsedCommand.Action.RESCHEDULE_DEADLINE, command.getAction());
        assertEquals(2, command.getTaskNumber());
        assertEquals(LocalDate.of(2028, 2, 29), command.getScheduledDate());
    }

    @Test
    void parse_rescheduleEventMixedCaseAndWhitespace_datesAndTaskNumberReturned()
            throws CommandParseException {
        ParsedCommand command = parser.parse("  RESCHEDULE 3\t/FROM 2026-09-20 /To\t2026-09-22  ");

        assertEquals(ParsedCommand.Action.RESCHEDULE_EVENT, command.getAction());
        assertEquals(3, command.getTaskNumber());
        assertEquals(LocalDate.of(2026, 9, 20), command.getScheduledDate());
        assertEquals(LocalDate.of(2026, 9, 22), command.getEndDate());
    }

    @Test
    void parse_rescheduleNonpositiveTaskNumbers_leftForTaskListValidation() throws CommandParseException {
        assertTaskNumberCommand("reschedule 0 /by 2026-09-20", ParsedCommand.Action.RESCHEDULE_DEADLINE, 0);
        assertTaskNumberCommand("reschedule -1 /from 2026-09-20 /to 2026-09-22",
                ParsedCommand.Action.RESCHEDULE_EVENT, -1);
    }

    @Test
    void parse_rescheduleReversedEventRange_leftForEventValidation() throws CommandParseException {
        ParsedCommand command = parser.parse("reschedule 1 /from 2026-09-22 /to 2026-09-20");

        assertEquals(LocalDate.of(2026, 9, 22), command.getScheduledDate());
        assertEquals(LocalDate.of(2026, 9, 20), command.getEndDate());
    }

    @Test
    void parse_rescheduleMalformed_usageMessageReturned() {
        String[] inputs = {"reschedule", "reschedule 1", "reschedule /by 2026-09-20",
                "reschedule one /by 2026-09-20", "reschedule 2147483648 /by 2026-09-20",
                "reschedule 1.5 /by 2026-09-20", "reschedule 1 2 /by 2026-09-20",
                "reschedule 1 /by", "reschedule 1 /by 2026-02-29", "reschedule 1 /by 2026-9-20",
                "reschedule 1 /by 20-09-2026", "reschedule 1 /by tomorrow",
                "reschedule 1 /by 2026-09-20T12:00", "reschedule 1/by 2026-09-20",
                "reschedule 1 /by2026-09-20", "reschedule 1 /bylaws 2026-09-20",
                "reschedule 1 /by 2026-09-20 extra", "reschedule 1 /by 2026-09-20 /by 2026-09-21",
                "reschedule 1 /from 2026-09-20", "reschedule 1 /to 2026-09-22",
                "reschedule 1 /from /to 2026-09-22", "reschedule 1 /from 2026-09-20 /to",
                "reschedule 1 /from 2026-02-29 /to 2026-09-22",
                "reschedule 1 /from 2026-09-20 /to 2026-02-29",
                "reschedule 1 /to 2026-09-22 /from 2026-09-20",
                "reschedule 1 /from 2026-09-20 /from 2026-09-22",
                "reschedule 1 /by 2026-09-20 /to 2026-09-22",
                "reschedule 1 /from 2026-09-20 /by 2026-09-22",
                "reschedule 1 /from2026-09-20 /to 2026-09-22",
                "reschedule 1 /from 2026-09-20/to 2026-09-22",
                "reschedule 1 /from 2026-09-20 /to2026-09-22",
                "reschedule 1 /from 2026-09-20 /to 2026-09-22 extra",
                "reschedule 1 /from 2026-09-20 /to 2026-09-22 /to 2026-09-23",
                "reschedule 1 /from 2026-09-20 /to 2026-09-22 /by 2026-09-24"};

        for (String input : inputs) {
            assertInvalidCommand(input, "Use 'reschedule <task number> /by <yyyy-MM-dd>' for a deadline or "
                    + "'reschedule <task number> /from <yyyy-MM-dd> /to <yyyy-MM-dd>' for an event.");
        }
    }

    private void assertInvalidCommand(String input, String expectedMessage) {
        CommandParseException exception = assertThrows(CommandParseException.class, () -> parser.parse(input));
        assertTrue(exception.getMessage().endsWith(expectedMessage), input + ": " + exception.getMessage());
    }

    @Test
    void parse_blankInput_specificPromptReturned() {
        for (String input : new String[] {"", "  \t", "\u2003", "\u00a0\u2002\u202f", "\u00a0\u001c\u00a0"}) {
            assertErrorContains(input, "Please enter a command");
        }
    }

    @Test
    void parse_noPayloadCommandHasArguments_specificGuidanceReturned() {
        for (String input : new String[] {"list extra", "BYE extra", "bye 1"}) {
            assertErrorContains(input, "accepts no arguments");
        }
    }

    @Test
    void parse_requiredFieldMissing_fieldIdentified() {
        String[][] cases = {
                {"todo\u2003", "Missing description"},
                {"todo \u00a0\u001c\u00a0", "Missing description"},
                {"find\u00a0", "Missing search keyword"},
                {"deadline /by 2026-09-01", "Missing description"},
                {"deadline book", "Missing /by date field"},
                {"deadline book /by\u2003", "Missing deadline date"},
                {"event meeting /to 2026-09-02", "Missing /from date field"},
                {"event meeting /from 2026-09-01", "Missing /to date field"},
                {"event meeting /from /to 2026-09-02", "Missing event start date"},
                {"event meeting /from 2026-09-01 /to", "Missing event end date"},
                {"reschedule", "Missing task number"},
                {"reschedule /by 2026-09-01", "Missing task number"},
                {"reschedule 1 /by", "Missing deadline date"},
                {"reschedule 1 /from /to 2026-09-02", "Missing event start date"},
                {"reschedule 1 /from 2026-09-01 /to", "Missing event end date"}
        };
        for (String[] testCase : cases) {
            assertErrorContains(testCase[0], testCase[1]);
        }
    }

    @Test
    void parse_invalidTaskNumber_reasonIdentified() {
        for (String command : new String[] {"mark", "unmark", "delete", "reschedule"}) {
            String schedule = command.equals("reschedule") ? " /by 2026-09-01" : "";
            assertErrorContains(command, "Missing task number");
            assertErrorContains(command + " one" + schedule, "must be a whole number");
            assertErrorContains(command + " 1.5" + schedule, "must be a whole number");
            assertErrorContains(command + " 2147483648" + schedule, "too large or too small");
            assertErrorContains(command + " -2147483649" + schedule, "too large or too small");
        }
    }

    @Test
    void parse_invalidDates_formatAndCalendarFailuresDistinguished() {
        assertErrorContains("deadline book /by 2026-9-01", "Use yyyy-MM-dd");
        assertErrorContains("deadline book /by 2026-02-29", "not a valid calendar date");
        assertErrorContains("event meeting /from 2026-02-29 /to 2026-09-01", "Invalid event start date");
        assertErrorContains("event meeting /from 2026-09-01 /to 2026-09-31", "Invalid event end date");
        assertErrorContains("reschedule 1 /by tomorrow", "Invalid deadline date");
        assertErrorContains("reschedule 1 /from 2026-09-01 /to 2026-09-31", "Invalid event end date");
    }

    @Test
    void parse_lineBreakAnywhere_commandRejectedBeforeTrimming() {
        for (String separator : new String[] {"\n", "\r", "\r\n", "\u000b", "\f", "\u0085", "\u2028", "\u2029"}) {
            assertErrorContains("todo read" + separator + "book", "single line");
            assertErrorContains(separator + "bye", "single line");
            assertErrorContains("list" + separator, "single line");
        }
    }

    @Test
    void parse_unicodeWhitespaceAndValidDates_commandsAccepted() throws CommandParseException {
        assertEquals("read  book", parser.parse("\u00a0todo\u2003read  book\u202f")
                .getTask().getDescription());
        assertEquals("[E][ ] meeting (from: Feb 29 2028 to: Feb 29 2028)",
                parser.parse("event meeting\u2003/from\u00a02028-02-29 /to 2028-02-29").getTask().toString());
        assertEquals("[D][ ] book (by: Jan 01 2000)",
                parser.parse("deadline book /by 2000-01-01").getTask().toString());
    }

    private void assertErrorContains(String input, String expectedText) {
        CommandParseException exception = assertThrows(CommandParseException.class, () -> parser.parse(input));
        assertTrue(exception.getMessage().contains(expectedText), input + ": " + exception.getMessage());
    }

    private void assertTaskNumberCommand(String input, ParsedCommand.Action expectedAction, int expectedNumber)
            throws CommandParseException {
        ParsedCommand command = parser.parse(input);
        assertEquals(expectedAction, command.getAction());
        assertEquals(expectedNumber, command.getTaskNumber());
    }
}
