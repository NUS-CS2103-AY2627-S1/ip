package bingus.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import bingus.exception.BingusException;
import bingus.task.Deadline;
import bingus.task.Event;
import bingus.task.Todo;

/**
 * Tests parsing of todo, deadline, and event arguments.
 */
class ParserTaskCreationTest {
    @Test
    void parseDeadline_inputsLengthOne_throwsException() {
        TaskParser parser = new TaskParser();
        String[] parts = {"deadline"};
        assertThrows(BingusException.class, () -> parser.parseDeadline(parts));
    }

    @Test
    void parseDeadline_inputWithoutDelimiter_throwsException() {
        TaskParser parser = new TaskParser();
        String[] parts = {"deadline", "no-delimiter"};
        assertThrows(BingusException.class, () -> parser.parseDeadline(parts));
    }

    @Test
    void parseDeadline_inputWithDelimiterAndLocalDateTime_returnsDeadline() {
        TaskParser parser = new TaskParser();
        String[] properParts = {"deadline", "soup /by 1212-12-12 1212"};
        Deadline deadline = parser.parseDeadline(properParts);
        assertEquals("soup", deadline.getDescription());
        assertEquals(
                LocalDateTime.of(1212, 12, 12, 12, 12),
                deadline.getBy());
    }

    @Test
    void parseDeadline_emptyDescription_throwsException() {
        TaskParser parser = new TaskParser();
        String[] parts = {"deadline", " /by 2026-08-30 2359"};

        BingusException exception = assertThrows(BingusException.class, () -> parser.parseDeadline(parts));

        assertEquals("I need a description for that deadline.\n"
                + "Try `deadline [DESCRIPTION] /by [DATETIME]`.", exception.getMessage());
    }

    @Test
    void parseDeadline_emptyDateTime_throwsException() {
        TaskParser parser = new TaskParser();
        String[] parts = {"deadline", "submit report /by "};

        BingusException exception = assertThrows(BingusException.class, () -> parser.parseDeadline(parts));

        assertEquals("I need a date and time for that deadline.\n"
                + "Try `deadline [DESCRIPTION] /by [DATETIME]`.", exception.getMessage());
    }

    @Test
    void parseDeadline_invalidCalendarDate_throwsException() {
        TaskParser parser = new TaskParser();
        String[] parts = {"deadline", "submit report /by 2026-02-30 1200"};

        assertThrows(BingusException.class, () -> parser.parseDeadline(parts));
    }

    @Test
    void parseDeadline_repeatedByMarker_throwsHelpfulException() {
        TaskParser parser = new TaskParser();
        String[] parts = {"deadline", "submit report /by 2026-09-20 1800 /by 2026-09-21 1800"};

        BingusException exception = assertThrows(BingusException.class, () -> parser.parseDeadline(parts));

        assertEquals("I can only use one `/by` date for a deadline.\n"
                + "Try `deadline [DESCRIPTION] /by [DATETIME]`.", exception.getMessage());
    }

    @Test
    void parseTodo_descriptionWithSurroundingWhitespace_returnsTrimmedTodo() {
        TaskParser parser = new TaskParser();
        String[] parts = {"todo", "  read JUnit guide  "};

        Todo todo = parser.parseTodo(parts);

        assertEquals("read JUnit guide", todo.getDescription());
    }

    @Test
    void parseTodo_blankDescription_throwsException() {
        TaskParser parser = new TaskParser();
        String[] parts = {"todo", "   "};

        assertThrows(BingusException.class, () -> parser.parseTodo(parts));
    }

    @Test
    void parseEvent_validTimes_returnsEventWithCorrectValues() {
        TaskParser parser = new TaskParser();
        String[] parts = {"event", "camp /from 2026-08-20 0900 /to 2026-08-22 1800"};

        Event event = parser.parseEvent(parts);

        assertEquals("camp", event.getDescription());
        assertEquals(LocalDateTime.of(2026, 8, 20, 9, 0), event.getFrom());
        assertEquals(LocalDateTime.of(2026, 8, 22, 18, 0), event.getTo());
    }

    @Test
    void parseEvent_endNotAfterStart_throwsException() {
        TaskParser parser = new TaskParser();
        String[] parts = {"event", "meeting /from 2026-08-20 0900 /to 2026-08-20 0900"};

        BingusException exception = assertThrows(BingusException.class, () -> parser.parseEvent(parts));

        assertEquals("The event must end after it starts.", exception.getMessage());
    }

    @Test
    void parseEvent_repeatedFromMarker_throwsHelpfulException() {
        TaskParser parser = new TaskParser();
        String[] parts = {"event", "meeting /from 2026-09-20 1400 /from 2026-09-20 1500 /to 2026-09-20 1600"};

        BingusException exception = assertThrows(BingusException.class, () -> parser.parseEvent(parts));

        assertEquals("I can only use one `/from` date for an event.\n"
                + "Try `event [DESCRIPTION] /from [FROM_DATETIME] /to [TO_DATETIME]`.", exception.getMessage());
    }

    @Test
    void parseEvent_repeatedToMarker_throwsHelpfulException() {
        TaskParser parser = new TaskParser();
        String[] parts = {"event", "meeting /from 2026-09-20 1400 /to 2026-09-20 1500 /to 2026-09-20 1600"};

        BingusException exception = assertThrows(BingusException.class, () -> parser.parseEvent(parts));

        assertEquals("I can only use one `/to` date for an event.\n"
                + "Try `event [DESCRIPTION] /from [FROM_DATETIME] /to [TO_DATETIME]`.", exception.getMessage());
    }
}

