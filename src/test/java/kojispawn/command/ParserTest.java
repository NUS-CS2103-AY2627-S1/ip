package kojispawn.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import kojispawn.exception.KojisPawnException;
import kojispawn.task.Deadline;
import kojispawn.task.Event;
import kojispawn.task.Todo;

/**
 * Tests the conversion of user input into validated commands.
 */
public class ParserTest {
    private final Parser parser = new Parser();

    @Test
    public void parse_reservedStorageText_rejectsUnsafeTaskFields() {
        List<String> commands = List.of(
                "todo read A | B", "deadline A | B /by 2026-09-15",
                "event A | B /from morning /to evening",
                "event meeting /from A | B /to evening",
                "event meeting /from morning /to A | B", "todo first\nsecond", "todo first\rsecond");
        for (String command : commands) {
            KojisPawnException exception = assertThrows(KojisPawnException.class, () ->
                    parser.parse(command));
            assertEquals("Task text cannot contain the reserved separator ' | ' or line breaks.",
                    exception.getMessage());
        }
    }

    @Test
    public void parse_eventFields_preservesValidationMessages() {
        String[][] cases = {
            {"event /from 2pm /to 4pm", "An event without a description cannot enter the plan. "
                    + "Use: event DESCRIPTION /from START /to END."},
            {"event meeting /to 4pm", "Every event has an origin. Include /from START."},
            {"event meeting /from 2pm", "Even calculated events need an endpoint. Include /to END."},
            {"event meeting /to 4pm /from 2pm", "Causality matters. Place /from START before /to END."},
            {"event meeting /from /to 4pm", "The plan requires a starting value after /from."},
            {"event meeting /from 2pm /to", "The plan requires an ending value after /to."},
            {"event meeting /from2pm /to 4pm", "Every event has an origin. Include /from START."},
            {"event meeting /from 2pm /to4pm", "Even calculated events need an endpoint. Include /to END."}
        };
        for (String[] testCase : cases) {
            KojisPawnException exception = assertThrows(KojisPawnException.class, () ->
                    parser.parse(testCase[0]), testCase[0]);
            assertEquals(testCase[1], exception.getMessage());
        }
    }

    @Test
    public void parse_spacedEventAndLiteralPipe_preservesText() throws KojisPawnException {
        Command command = parser.parse("  event  A|B  /from   Mon 2pm  /to   4pm  ");

        assertEquals("[E][ ] A|B (from: Mon 2pm to: 4pm)", command.getTask().toString());
    }

    @Test
    public void parse_deadlineMarkerWithoutSpace_rejectsMalformedMarker() {
        KojisPawnException exception = assertThrows(KojisPawnException.class, () ->
                parser.parse("deadline task /by2026-09-15"));

        assertEquals("Even a deadline needs a boundary. Use: deadline DESCRIPTION /by DATE.",
                exception.getMessage());
    }

    @Test
    public void parse_repeatedTaskMarkers_rejectsDuplicateParameters() {
        String[][] cases = {
            {"deadline submit report /by 2026-09-15 /by 2026-09-16", "Specify /by only once."},
            {"event meeting /from morning /from noon /to evening", "Specify /from only once."},
            {"event meeting /from morning /to evening /to night", "Specify /to only once."}
        };
        for (String[] testCase : cases) {
            KojisPawnException exception = assertThrows(KojisPawnException.class, () ->
                    parser.parse(testCase[0]), testCase[0]);
            assertEquals(testCase[1], exception.getMessage());
        }
    }

    @Test
    public void parse_similarTextInEventValue_doesNotTreatItAsRepeatedMarker() throws KojisPawnException {
        Command command = parser.parse("event meeting /from morning /to /fromage");

        assertEquals("[E][ ] meeting (from: morning to: /fromage)", command.getTask().toString());
    }

    @Test
    public void parse_validTodo_returnsTodoCommand() throws KojisPawnException {
        Command command = parser.parse("todo read book");

        assertEquals(CommandType.TODO, command.getType());
        assertInstanceOf(Todo.class, command.getTask());
        assertEquals("[T][ ] read book", command.getTask().toString());
    }

    @Test
    public void parse_validDeadline_returnsDeadlineWithFormattedDate()
            throws KojisPawnException {
        Command command = parser.parse("deadline return book /by 2019-12-02");

        assertEquals(CommandType.DEADLINE, command.getType());
        assertInstanceOf(Deadline.class, command.getTask());
        assertEquals("[D][ ] return book (by: Dec 2 2019)", command.getTask().toString());
    }

    @Test
    public void parse_validOnCommand_returnsQueryDate() throws KojisPawnException {
        Command command = parser.parse("on 2019-12-02");

        assertEquals(CommandType.ON, command.getType());
        assertEquals(LocalDate.of(2019, 12, 2), command.getDate());
    }

    @Test
    public void parse_onMissingOrInvalidDate_returnsSpecificErrors() {
        String[][] cases = {
            {"on", "Specify a date. Use: on yyyy-MM-dd."},
            {"on 2019-02-30", "Query dates must use yyyy-MM-dd and describe a real calendar date."},
            {"on tomorrow", "Query dates must use yyyy-MM-dd and describe a real calendar date."}
        };

        for (String[] testCase : cases) {
            KojisPawnException exception = assertThrows(KojisPawnException.class, () ->
                    parser.parse(testCase[0]), testCase[0]);
            assertEquals(testCase[1], exception.getMessage());
        }
    }

    @Test
    public void parse_validEvent_returnsEventCommand() throws KojisPawnException {
        Command command = parser.parse("event project meeting /from 2pm /to 4pm");

        assertEquals(CommandType.EVENT, command.getType());
        assertInstanceOf(Event.class, command.getTask());
        assertEquals("[E][ ] project meeting (from: 2pm to: 4pm)",
                command.getTask().toString());
    }

    @Test
    public void parse_eventEndBeforeStart_rejectsReversedIsoDates() {
        String[] commands = {
            "event visit /from 2026-08-09 /to 2026-08-08",
            "event visit /from 2026-08-09 2pm /to 2026-08-08 4pm"
        };
        for (String command : commands) {
            KojisPawnException exception = assertThrows(KojisPawnException.class, () -> parser.parse(command));
            assertEquals("An event cannot end before it starts. Use /to on or after /from.",
                    exception.getMessage());
        }
    }

    @Test
    public void parse_eventImpossibleIsoDate_rejectsInvalidCalendarDate() {
        KojisPawnException exception = assertThrows(KojisPawnException.class, () ->
                parser.parse("event visit /from 2026-02-30 /to 2026-03-01"));

        assertEquals("Event dates must use yyyy-MM-dd and describe a real calendar date.",
                exception.getMessage());
    }

    @Test
    public void parse_eventDatesInOrder_acceptsLaterOrSameEndDate() throws KojisPawnException {
        Command laterDate = parser.parse("event visit /from 2026-08-08 /to 2026-08-09");
        Command sameDate = parser.parse("event visit /from 2026-08-08 2pm /to 2026-08-08 4pm");

        assertEquals("[E][ ] visit (from: 2026-08-08 to: 2026-08-09)", laterDate.getTask().toString());
        assertEquals("[E][ ] visit (from: 2026-08-08 2pm to: 2026-08-08 4pm)",
                sameDate.getTask().toString());
    }

    @Test
    public void parse_validMark_returnsOneBasedTaskNumber() throws KojisPawnException {
        Command command = parser.parse("mark 3");

        assertEquals(CommandType.MARK, command.getType());
        assertEquals(3, command.getTaskNumber());
    }

    @Test
    public void parse_taskNumberMissingOrNonnumeric_returnsActionSpecificErrors() {
        String[][] cases = {
            {"mark", "Specify which task to mark. Use: mark TASK_NUMBER."},
            {"unmark", "Specify which task to unmark. Use: unmark TASK_NUMBER."},
            {"delete", "Specify which task to delete. Use: delete TASK_NUMBER."},
            {"mark one", "Task positions are numbers, not guesses. Use: mark TASK_NUMBER."},
            {"unmark one", "Task positions are numbers, not guesses. Use: unmark TASK_NUMBER."},
            {"delete one", "Task positions are numbers, not guesses. Use: delete TASK_NUMBER."}
        };

        for (String[] testCase : cases) {
            KojisPawnException exception = assertThrows(KojisPawnException.class, () ->
                    parser.parse(testCase[0]), testCase[0]);
            assertEquals(testCase[1], exception.getMessage());
        }
    }

    @Test
    public void parse_validFind_returnsSearchKeyword() throws KojisPawnException {
        Command command = parser.parse("find return book");

        assertEquals(CommandType.FIND, command.getType());
        assertEquals("return book", command.getKeyword());
    }

    @Test
    public void parse_validUndo_returnsUndoCommand() throws KojisPawnException {
        Command command = parser.parse("undo");

        assertEquals(CommandType.UNDO, command.getType());
    }

    @Test
    public void parse_impossibleDeadlineDate_throwsException() {
        KojisPawnException exception = assertThrows(KojisPawnException.class, () ->
                parser.parse("deadline return book /by 2019-02-30"));

        assertEquals("Deadline dates must use yyyy-MM-dd and describe a real calendar date.",
                exception.getMessage());
    }

    @Test
    public void parse_unknownCommand_throwsException() {
        assertThrows(KojisPawnException.class, () -> parser.parse("dance now"));
    }

    @Test
    public void parse_nonPositiveTaskNumber_throwsException() {
        assertThrows(KojisPawnException.class, () -> parser.parse("delete 0"));
    }

    @Test
    public void parse_listWithExtraArguments_throwsException() {
        assertThrows(KojisPawnException.class, () -> parser.parse("list please"));
    }

    @Test
    public void parse_undoWithExtraArguments_throwsException() {
        assertThrows(KojisPawnException.class, () -> parser.parse("undo please"));
    }

    @Test
    public void parse_findWithoutKeyword_throwsException() {
        KojisPawnException exception = assertThrows(KojisPawnException.class, () ->
                parser.parse("find"));

        assertEquals("Specify a keyword. Use: find KEYWORD.", exception.getMessage());
    }
}
