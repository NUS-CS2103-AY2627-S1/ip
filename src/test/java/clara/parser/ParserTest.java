package clara.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import clara.exception.ClaraException;
import clara.task.Deadline;
import clara.task.Event;
import clara.task.Priority;
import clara.task.Todo;

// NOTE: AI-assisted test suite implementation. See CITATIONS.md [C-010], [C-017].

/**
 * Unit tests for {@link Parser}.
 */
public class ParserTest {

    @Test
    public void requireNoArguments_emptyArguments_success() {
        try {
            Parser.requireNoArguments("list", "");
        } catch (ClaraException ex) {
            fail("Should not throw an exception for empty arguments");
        }
    }

    @Test
    public void requireNoArguments_nonEmptyArguments_exceptionThrown() {
        ClaraException ex = assertThrows(ClaraException.class, () -> {
            Parser.requireNoArguments("list", "extra_arg");
        });
        assertEquals("list does not accept arguments", ex.getMessage());
    }

    @Test
    public void parseTaskIndex_validInteger_success() throws ClaraException {
        assertEquals(3, Parser.parseTaskIndex("mark", "3"));
        assertEquals(1, Parser.parseTaskIndex("delete", "1"));
    }

    @Test
    public void parseTaskIndex_nonNumericArgument_exceptionThrown() {
        ClaraException ex = assertThrows(ClaraException.class, () -> {
            Parser.parseTaskIndex("mark", "abc");
        });
        assertEquals("Use: mark <task number>.", ex.getMessage());
    }

    @Test
    public void parseFindQuery_blankQuery_exceptionThrown() {
        ClaraException ex = assertThrows(ClaraException.class, () -> {
            Parser.parseFindQuery("   ");
        });
        assertEquals("A find command needs search text.", ex.getMessage());
    }

    // NOTE: AI-assisted task priority feature. See CITATIONS.md [C-018].
    @Test
    public void parsePriority_validPriority_success() throws ClaraException {
        assertEquals(Priority.LOW, Parser.parsePriority("low"));
        assertEquals(Priority.MEDIUM, Parser.parsePriority("medium"));
        assertEquals(Priority.HIGH, Parser.parsePriority("high"));
    }

    @Test
    public void parsePriority_invalidPriority_exceptionThrown() {
        ClaraException ex = assertThrows(ClaraException.class, () -> {
            Parser.parsePriority("urgent");
        });
        assertEquals("Use: priority <task number> <low|medium|high>.", ex.getMessage());
    }

    @Test
    public void parseTodo_validDescription_success() throws ClaraException {
        Todo todo = Parser.parseTodo("read a book");
        assertNotNull(todo);
        assertEquals("read a book", todo.getTaskName());
        assertFalse(todo.isDone());
    }

    @Test
    public void parseTodo_blankDescription_exceptionThrown() {
        ClaraException ex = assertThrows(ClaraException.class, () -> {
            Parser.parseTodo("   ");
        });
        assertEquals("A todo needs a description.", ex.getMessage());
    }

    @Test
    public void parseTodo_pipeCharacterInDescription_exceptionThrown() {
        ClaraException ex = assertThrows(ClaraException.class, () -> {
            Parser.parseTodo("read book | chapter 1");
        });
        assertEquals("The character '|' is reserved and cannot be used in task details.", ex.getMessage());
    }

    @Test
    public void parseDeadline_validFormat_success() throws ClaraException {
        Deadline deadline = Parser.parseDeadline("submit assignment /by 2025-10-31 2359");
        assertNotNull(deadline);
        assertEquals("submit assignment", deadline.getTaskName());
        assertEquals(LocalDateTime.of(2025, 10, 31, 23, 59), deadline.getDeadlineTime());
    }

    @Test
    public void parseDeadline_missingByDelimiter_exceptionThrown() {
        ClaraException ex = assertThrows(ClaraException.class, () -> {
            Parser.parseDeadline("submit assignment 2025-10-31 2359");
        });
        assertEquals("Use: deadline <name> /by <time>.", ex.getMessage());
    }

    @Test
    public void parseDeadline_blankDescriptionOrTime_exceptionThrown() {
        ClaraException blankDescriptionException = assertThrows(ClaraException.class, () -> {
            Parser.parseDeadline(" /by 2025-10-31 2359");
        });
        assertEquals("Use: deadline <name> /by <time>.", blankDescriptionException.getMessage());

        ClaraException blankTimeException = assertThrows(ClaraException.class, () -> {
            Parser.parseDeadline("submit assignment /by ");
        });
        assertEquals("Use: deadline <name> /by <time>.", blankTimeException.getMessage());
    }

    @Test
    public void parseDeadline_invalidDateFormat_exceptionThrown() {
        ClaraException ex = assertThrows(ClaraException.class, () -> {
            Parser.parseDeadline("submit assignment /by 31-10-2025");
        });
        assertEquals("Time format: yyyy-MM-dd HHmm (e.g. 2025-12-25 1357).", ex.getMessage());
    }

    @Test
    public void parseDeadline_nonExistentDate_exceptionThrown() {
        ClaraException ex = assertThrows(ClaraException.class, () -> {
            Parser.parseDeadline("submit assignment /by 2025-02-30 1200");
        });
        assertEquals("Time format: yyyy-MM-dd HHmm (e.g. 2025-12-25 1357).", ex.getMessage());
    }

    @Test
    public void parseDeadline_pipeCharacter_exceptionThrown() {
        ClaraException ex = assertThrows(ClaraException.class, () -> {
            Parser.parseDeadline("submit | assignment /by 2025-10-31 2359");
        });
        assertEquals("The character '|' is reserved and cannot be used in task details.", ex.getMessage());
    }

    @Test
    public void parseEvent_validFormat_success() throws ClaraException {
        Event event = Parser.parseEvent("team meeting /from 2025-11-01 1400 /to 2025-11-01 1600");
        assertNotNull(event);
        assertEquals("team meeting", event.getTaskName());
        assertEquals(LocalDateTime.of(2025, 11, 1, 14, 0), event.getFromTime());
        assertEquals(LocalDateTime.of(2025, 11, 1, 16, 0), event.getToTime());
    }

    @Test
    public void parseEvent_missingDelimiters_exceptionThrown() {
        ClaraException missingFromDelimiterException = assertThrows(ClaraException.class, () -> {
            Parser.parseEvent("team meeting 2025-11-01 1400 /to 2025-11-01 1600");
        });
        assertEquals("Use: event <name> /from <time> /to <time>.", missingFromDelimiterException.getMessage());

        ClaraException missingToDelimiterException = assertThrows(ClaraException.class, () -> {
            Parser.parseEvent("team meeting /from 2025-11-01 1400 2025-11-01 1600");
        });
        assertEquals("Use: event <name> /from <time> /to <time>.", missingToDelimiterException.getMessage());
    }

    @Test
    public void parseEvent_blankFields_exceptionThrown() {
        ClaraException ex = assertThrows(ClaraException.class, () -> {
            Parser.parseEvent("   /from 2025-11-01 1400 /to 2025-11-01 1600");
        });
        assertEquals("Use: event <name> /from <time> /to <time>.", ex.getMessage());
    }

    @Test
    public void parseEvent_invalidDateFormat_exceptionThrown() {
        ClaraException ex = assertThrows(ClaraException.class, () -> {
            Parser.parseEvent("team meeting /from invalid /to 2025-11-01 1600");
        });
        assertEquals("Time format: yyyy-MM-dd HHmm (e.g. 2025-12-25 1357).", ex.getMessage());
    }

    @Test
    public void parseEvent_startNotBeforeEnd_exceptionThrown() {
        ClaraException ex = assertThrows(ClaraException.class, () -> {
            Parser.parseEvent("team meeting /from 2025-11-01 1600 /to 2025-11-01 1600");
        });
        assertEquals("An event's start time must be before its end time.", ex.getMessage());
    }

    @Test
    public void parseEvent_pipeCharacter_exceptionThrown() {
        ClaraException ex = assertThrows(ClaraException.class, () -> {
            Parser.parseEvent("meeting | standup /from 2025-11-01 1400 /to 2025-11-01 1600");
        });
        assertEquals("The character '|' is reserved and cannot be used in task details.", ex.getMessage());
    }
}
