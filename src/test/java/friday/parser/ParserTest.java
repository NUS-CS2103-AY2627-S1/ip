package friday.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import friday.task.Deadline;
import friday.task.Event;
import friday.task.ToDo;

/**
 * Tests command boundaries, task construction, and argument validation independently of the UI.
 */
class ParserTest {
    @ParameterizedTest
    @CsvSource({"bye,BYE", "hello,HELLO", "thanks,THANKS", "help,HELP", "list,LIST",
            "todo,TODO", "deadline,DEADLINE", "event,EVENT", "on,ON", "delete,DELETE",
            "mark,MARK", "unmark,UNMARK", "todo   read book,TODO"})
    void parseCommandType_supportedCommand_returnsExpectedType(String input, Parser.CommandType expected) {
        assertEquals(expected, Parser.parseCommandType(input));
    }

    @ParameterizedTest
    @EmptySource
    @ValueSource(strings = {"blah", "TODO read book", " todo read book", "todo\tread book",
            "todoLater read book", "deadlineX book", "eventually meeting", "onward", "marking 1",
            "unmarked 1", "deleted 1", "bye now", "hello ", "thanks extra", "help now", "list "})
    void parseCommandType_unknownOrMalformedCommand_throwsException(String command) {
        assertError(() -> Parser.parseCommandType(command), "Command not recognized. Type help for available commands.");
    }

    @Test
    void parseTask_todoWithDelimiterText_preservesDescriptionAndStartsUnmarked() {
        ToDo todo = assertInstanceOf(ToDo.class, Parser.parseTask("todo   read | café /by tomorrow  "));
        assertEquals("read | café /by tomorrow", todo.getDescription());
        assertFalse(todo.isDone());
    }

    @Test
    void parseTask_deadlineWithWhitespace_constructsValidatedDateTime() {
        Deadline deadline = assertInstanceOf(Deadline.class,
                Parser.parseTask("deadline  return book  /by 2/12/2019 1800  "));
        assertEquals("return book", deadline.getDescription());
        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0), deadline.getBy());
        assertFalse(deadline.isDone());
    }

    @Test
    void parseTask_eventAcrossDates_preservesBothEndpoints() {
        Event event = assertInstanceOf(Event.class,
                Parser.parseTask("event  meeting /from 2019-12-02 14:00 /to 2019-12-03 16:00"));
        assertEquals("meeting", event.getDescription());
        assertEquals(LocalDateTime.of(2019, 12, 2, 14, 0), event.getFrom());
        assertEquals(LocalDateTime.of(2019, 12, 3, 16, 0), event.getTo());
        assertFalse(event.isDone());
    }

    @ParameterizedTest
    @ValueSource(strings = {"todo", "todo   "})
    void parseTask_missingTodoDescription_throwsHelpfulException(String command) {
        assertError(() -> Parser.parseTask(command), "The description of a todo cannot be empty.");
    }

    @ParameterizedTest
    @ValueSource(strings = {"deadline", "deadline book", "deadline /by 2019-12-02",
            "deadline book /by ", "deadline book/by 2019-12-02"})
    void parseTask_malformedDeadline_throwsHelpfulException(String command) {
        assertError(() -> Parser.parseTask(command),
                "Invalid deadline format. Use: deadline DESCRIPTION /by DEADLINE");
    }

    @ParameterizedTest
    @ValueSource(strings = {"event", "event meeting /from 2019-12-02",
            "event /from 2019-12-02 /to 2019-12-03", "event meeting /from /to 2019-12-03",
            "event meeting /from 2019-12-02 /to ", "event meeting /to 2019-12-03 /from 2019-12-02"})
    void parseTask_malformedEvent_throwsHelpfulException(String command) {
        assertError(() -> Parser.parseTask(command),
                "Invalid event format. Use: event DESCRIPTION /from START /to END");
    }

    @ParameterizedTest
    @ValueSource(strings = {"deadline book /by 2019-02-29", "event meeting /from 2019-12-02 /to Sunday"})
    void parseTask_invalidTaskDate_propagatesDateValidation(String command) {
        assertError(() -> Parser.parseTask(command), "Invalid date/time. Use yyyy-MM-dd, yyyy-MM-dd HH:mm, "
                + "or d/M/yyyy HHmm (e.g., 2/12/2019 1800).");
    }

    @Test
    void parseTask_backwardsEvent_throwsHelpfulException() {
        assertError(() -> Parser.parseTask("event meeting /from 2019-12-03 /to 2019-12-02"),
                "An event cannot end before it starts.");
    }

    @ParameterizedTest
    @ValueSource(strings = {"bye", "hello", "thanks", "help", "list", "on 2024-02-29",
            "find book", "delete 1", "mark 1", "unmark 1"})
    void parseTask_nonAddingCommand_throwsException(String command) {
        assertError(() -> Parser.parseTask(command), "This command does not add a task.");
    }

    @ParameterizedTest
    @ValueSource(strings = {"mark", "unmark", "delete"})
    void parseTaskNumber_invalidSyntax_throwsCommandSpecificError(String word) {
        String message = "Invalid " + word + " format. Use: " + word + " TASK_NUMBER";
        assertError(() -> Parser.parseTaskNumber(word), message);
        for (String argument : List.of("", "   ", "abc", "1 2", "1.0", "2147483648", "-2147483649", "-1")) {
            assertError(() -> Parser.parseTaskNumber(word + " " + argument), message);
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"mark", "unmark", "delete"})
    void parseTaskNumber_validIntegerSpellings_preservesValueWithoutCheckingTaskExistence(String word) {
        for (String argument : List.of("1", "+1", "01", "  1  ")) {
            assertEquals(1, Parser.parseTaskNumber(word + " " + argument), argument);
        }
        for (int number : new int[] {Integer.MIN_VALUE, -2, 0, Integer.MAX_VALUE}) {
            assertEquals(number, Parser.parseTaskNumber(word + " " + number));
        }
    }

    @Test
    void parseTaskNumber_nonSelectingCommand_throwsException() {
        assertError(() -> Parser.parseTaskNumber("list"), "This command does not select a task number.");
    }

    @Test
    void parseDate_leapDayWithSurroundingWhitespace_returnsCalendarDate() {
        assertEquals(LocalDate.of(2024, 2, 29), Parser.parseDate("on  2024-02-29  "));
    }

    @ParameterizedTest
    @ValueSource(strings = {"on", "on   ", "on Sunday", "on 2019-02-29", "on 2/12/2019",
            "on 2019-12-02 18:00", "on 2019-1-2"})
    void parseDate_missingOrInvalidDate_throwsHelpfulException(String command) {
        assertError(() -> Parser.parseDate(command), "Invalid date. Use: on yyyy-MM-dd (e.g., on 2019-12-02).");
    }

    @ParameterizedTest
    @ValueSource(strings = {"find", "find book", "find   read book  "})
    void parseCommandType_findCommand_returnsFind(String command) {
        assertEquals(Parser.CommandType.FIND, Parser.parseCommandType(command));
    }

    @Test
    void parseFindKeyword_surroundingWhitespace_preservesCaseAndInternalSpaces() {
        assertEquals("Read  book", Parser.parseFindKeyword("find   Read  book  "));
        assertEquals(".*", Parser.parseFindKeyword("find .*"));
        assertEquals("book", Parser.parseFindKeyword("find \u2003book\u2003"));
        assertEquals("读书", Parser.parseFindKeyword("find 读书"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"find", "find   ", "find \t", "find \u2003", "list", "todo book"})
    void parseFindKeyword_missingKeywordOrWrongCommand_throwsUsageError(String command) {
        assertError(() -> Parser.parseFindKeyword(command), "Invalid find format. Use: find KEYWORD");
    }

    @ParameterizedTest
    @ValueSource(strings = {"findbook", "FIND book", " find book", "find\tbook"})
    void parseCommandType_malformedFind_rejectsUnknownCommand(String command) {
        assertError(() -> Parser.parseCommandType(command), "Command not recognized. Type help for available commands.");
    }

    /** Checks exception messages as part of the existing console contract. */
    private static void assertError(Executable operation, String message) {
        assertEquals(message, assertThrows(IllegalArgumentException.class, operation).getMessage());
    }
}
