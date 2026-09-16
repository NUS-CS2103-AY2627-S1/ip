package crow.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import crow.exception.CrowException;
import crow.task.Deadline;
import crow.task.Event;
import crow.task.Todo;

class ParserTest {
    @Test
    void parseCommandAndArguments_validInput_returnsSeparateParts() {
        String input = "  deadline return book /by 2/12/2019 1800  ";

        assertEquals(CommandType.DEADLINE, Parser.parseCommandType(input));
        assertEquals("return book /by 2/12/2019 1800", Parser.parseArguments(input));
    }

    @Test
    void parseArguments_noArguments_returnsEmptyString() {
        assertEquals("", Parser.parseArguments("list"));
    }

    @Test
    void parseTodo_validDescription_createsTodo() throws CrowException {
        Todo todo = Parser.parseTodo("  read    book  ");

        assertEquals("read book", todo.getDescription());
    }

    @Test
    void parseTodo_emptyDescription_throwsException() {
        CrowException exception = assertThrows(CrowException.class, () -> Parser.parseTodo(""));

        assertEquals("Error: Todo description cannot be empty.", exception.getMessage());
    }

    @Test
    void parseDeadline_validArguments_createsDeadline() throws CrowException {
        Deadline deadline = Parser.parseDeadline("return   book   /BY   2/12/2019   1800");

        assertEquals("return book", deadline.getDescription());
        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0), deadline.getDeadlineDateTime());
    }

    @Test
    void parseDeadline_invalidDate_throwsException() {
        assertThrows(CrowException.class,
                () -> Parser.parseDeadline("return book /by 31/2/2019 1800"));
    }

    @Test
    void parseDeadline_missingBy_throwsException() {
        assertThrows(CrowException.class, () -> Parser.parseDeadline("return book"));
        assertThrows(CrowException.class,
                () -> Parser.parseDeadline("return book /by 2/12/2019 1800 /by 3/12/2019 1800"));
    }

    @Test
    void parseEvent_validArguments_createsEvent() throws CrowException {
        Event event = Parser.parseEvent("meeting   /FROM  3/12/2019  1400   /TO  3/12/2019  1600");

        assertEquals("meeting", event.getDescription());
        assertEquals(LocalDateTime.of(2019, 12, 3, 14, 0), event.getStartDateTime());
        assertEquals(LocalDateTime.of(2019, 12, 3, 16, 0), event.getEndDateTime());
    }

    @Test
    void parseEvent_missingRange_throwsException() {
        assertThrows(CrowException.class, () -> Parser.parseEvent("meeting /from 3/12/2019 1400"));
        assertThrows(CrowException.class, () -> Parser.parseEvent("meeting /to 3/12/2019 1600"));
        assertThrows(CrowException.class, () -> Parser.parseEvent(
                "meeting /from 3/12/2019 1400 /from 3/12/2019 1500 /to 3/12/2019 1600"));
        assertThrows(CrowException.class, () -> Parser.parseEvent(
                "meeting /from 3/12/2019 1400 /to 3/12/2019 1600 /to 3/12/2019 1700"));
    }

    @Test
    void parseEvent_nonIncreasingTimes_throwsException() {
        assertThrows(CrowException.class,
                () -> Parser.parseEvent("meeting /from 3/12/2019 1600 /to 3/12/2019 1600"));
        assertThrows(CrowException.class,
                () -> Parser.parseEvent("meeting /from 3/12/2019 1700 /to 3/12/2019 1600"));
    }

    @Test
    void parseTaskDescription_storageDelimiter_throwsException() {
        assertThrows(CrowException.class, () -> Parser.parseTodo("read | book"));
        assertThrows(CrowException.class,
                () -> Parser.parseDeadline("return | book /by 2/12/2019 1800"));
    }

    @Test
    void parseFindKeyword_nonEmptyKeyword_returnsKeyword() throws CrowException {
        assertEquals("read book", Parser.parseFindKeyword("read book"));
    }

    @Test
    void parseFindKeyword_emptyKeyword_throwsException() {
        CrowException exception = assertThrows(CrowException.class,
                () -> Parser.parseFindKeyword(""));

        assertEquals("Error: Search keyword cannot be empty.", exception.getMessage());
    }

    @Test
    void parseTaskIndex_validOneBasedNumber_returnsZeroBasedIndex() throws CrowException {
        assertEquals(1, Parser.parseTaskIndex("2", 3));
    }

    @Test
    void parseTaskIndex_invalidNumber_throwsException() {
        assertThrows(CrowException.class, () -> Parser.parseTaskIndex("abc", 3));
        assertThrows(CrowException.class, () -> Parser.parseTaskIndex("0", 3));
        assertThrows(CrowException.class, () -> Parser.parseTaskIndex("4", 3));
    }
}
