package margit.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** Tests command recognition and command-specific argument validation in {@link Parser}. */
class ParserTest {
    @Test
    void parse_simpleCommands_correctCommandType() {
        assertEquals(Parser.CommandType.BYE, Parser.parse("bye").type);
        assertEquals(Parser.CommandType.LIST, Parser.parse("list").type);
        assertEquals(Parser.CommandType.SORT, Parser.parse("sort").type);
        assertEquals(Parser.CommandType.UNKNOWN, Parser.parse("unknown command").type);
    }

    @Test
    void parse_onCommand_trimsDateArgument() {
        Parser.Command command = Parser.parse("on   2019-12-02  ");

        assertEquals(Parser.CommandType.ON, command.type);
        assertEquals("2019-12-02", command.argument);
    }

    @Test
    void parse_findCommand_trimsKeywordArgument() {
        Parser.Command command = Parser.parse("find   book  ");

        assertEquals(Parser.CommandType.FIND, command.type);
        assertEquals("book", command.argument);
    }

    @Test
    void parse_findWithoutKeyword_returnsFindCommandWithEmptyArgument() {
        Parser.Command command = Parser.parse("find");

        assertEquals(Parser.CommandType.FIND, command.type);
        assertEquals("", command.argument);
    }

    @Test
    void parse_indexedCommands_validAndInvalidIndices() {
        Parser.Command markedTask = Parser.parse("mark  2");
        Parser.Command unmarkedTask = Parser.parse("unmark 1");
        Parser.Command deletedTask = Parser.parse("delete two");

        assertEquals(Parser.CommandType.MARK, markedTask.type);
        assertEquals(1, markedTask.taskIndex);
        assertEquals(Parser.CommandType.UNMARK, unmarkedTask.type);
        assertEquals(0, unmarkedTask.taskIndex);
        assertEquals(Parser.CommandType.DELETE, deletedTask.type);
        assertNull(deletedTask.taskIndex);
    }

    @Test
    void parse_indexedCommand_zeroTaskNumberPreservesOutOfRangeIndex() {
        Parser.Command command = Parser.parse("delete 0");

        assertEquals(-1, command.taskIndex);
    }

    @Test
    void parse_todoDeadlineAndEventCommands_trimsArguments() {
        assertEquals("buy milk", Parser.parse("todo   buy milk  ").argument);
        assertEquals("submit /by 2019-12-02", Parser.parse("deadline submit /by 2019-12-02").argument);
        assertEquals("meeting /from 2019-12-01 /to 2019-12-02",
                Parser.parse("event meeting /from 2019-12-01 /to 2019-12-02").argument);
    }

    @Test
    void parseDeadline_validArguments_returnsDescriptionAndDate() {
        Parser.DeadlineArguments deadline = Parser.parseDeadline("submit report /by 2019-12-02");

        assertEquals(Parser.DeadlineStatus.VALID, deadline.status);
        assertEquals("submit report", deadline.description);
        assertEquals("2019-12-02", deadline.byRaw);
    }

    @Test
    void parseDeadline_missingSeparator_returnsMissingSeparatorStatus() {
        assertEquals(Parser.DeadlineStatus.MISSING_SEPARATOR, Parser.parseDeadline("").status);
        assertEquals(Parser.DeadlineStatus.MISSING_SEPARATOR, Parser.parseDeadline("submit report").status);
    }

    @Test
    void parseDeadline_missingDescriptionOrDate_returnsMissingComponentStatus() {
        assertEquals(Parser.DeadlineStatus.MISSING_COMPONENT, Parser.parseDeadline("/by 2019-12-02").status);
        assertEquals(Parser.DeadlineStatus.MISSING_COMPONENT, Parser.parseDeadline("submit report /by").status);
    }

    @Test
    void parseEvent_validArguments_returnsDescriptionAndTimes() {
        Parser.EventArguments event = Parser.parseEvent("meeting /from 2019-12-01 1800 /to 2019-12-02 0900");

        assertTrue(event.isValid);
        assertEquals("meeting", event.description);
        assertEquals("2019-12-01 1800", event.fromRaw);
        assertEquals("2019-12-02 0900", event.toRaw);
    }

    @Test
    void parseEvent_missingOrReversedMarkers_returnsInvalidArguments() {
        assertFalse(Parser.parseEvent("meeting /from 2019-12-01 1800").isValid);
        assertFalse(Parser.parseEvent("meeting /to 2019-12-02 0900 /from 2019-12-01 1800").isValid);
    }

    @Test
    void parseEvent_missingDescriptionOrTime_returnsInvalidArguments() {
        assertFalse(Parser.parseEvent("/from 2019-12-01 1800 /to 2019-12-02 0900").isValid);
        assertFalse(Parser.parseEvent("meeting /from /to 2019-12-02 0900").isValid);
        assertFalse(Parser.parseEvent("meeting /from 2019-12-01 1800 /to").isValid);
    }

    @Test
    void parseTodo_emptyAndNonEmptyDescriptions_returnsExpectedValidity() {
        Parser.TodoArguments missingDescription = Parser.parseTodo("");
        Parser.TodoArguments validDescription = Parser.parseTodo("buy milk");

        assertFalse(missingDescription.isValid);
        assertTrue(validDescription.isValid);
        assertEquals("buy milk", validDescription.description);
    }
}
