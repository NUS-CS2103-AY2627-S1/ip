package tasktracker.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import tasktracker.exception.TaskTrackerException;
import tasktracker.task.Deadline;
import tasktracker.task.Event;
import tasktracker.task.Fixed;
import tasktracker.task.ToDo;
import tasktracker.ui.Message;

public class ParserTest {

    // =========================================================================
    // Index Parsing Tests (mark / unmark / delete)
    // =========================================================================

    @Test
    public void parseIndex_validInteger_returnsParsedIndex() throws TaskTrackerException {
        assertEquals(1, Parser.parseIndex("1"));
        assertEquals(42, Parser.parseIndex("  42  "));
    }

    @Test
    public void parseIndex_emptyOrNull_throwsException() {
        TaskTrackerException exEmpty = assertThrows(TaskTrackerException.class, () -> {
            Parser.parseIndex("");
        });
        assertEquals(Message.ERR_MISSING_INDEX, exEmpty.getMessage());

        TaskTrackerException exBlank = assertThrows(TaskTrackerException.class, () -> {
            Parser.parseIndex("   ");
        });
        assertEquals(Message.ERR_MISSING_INDEX, exBlank.getMessage());
    }

    @Test
    public void parseIndex_nonNumeric_throwsException() {
        TaskTrackerException exAlpha = assertThrows(TaskTrackerException.class, () -> {
            Parser.parseIndex("abc");
        });
        assertEquals(Message.ERR_INVALID_INDEX, exAlpha.getMessage());

        TaskTrackerException exSpecial = assertThrows(TaskTrackerException.class, () -> {
            Parser.parseIndex("one");
        });
        assertEquals(Message.ERR_INVALID_INDEX, exSpecial.getMessage());
    }

    @Test
    public void parseIndex_containsPipeCharacter_throwsException() {
        TaskTrackerException ex = assertThrows(TaskTrackerException.class, () -> {
            Parser.parseIndex("1|2");
        });
        assertEquals(Message.ERR_NO_DELIMITER, ex.getMessage());
    }

    // =========================================================================
    // Find Keyword Parsing Tests
    // =========================================================================

    @Test
    public void parseFind_validKeyword_returnsTrimmedKeyword() throws TaskTrackerException {
        assertEquals("book", Parser.parseFind("book"));
        assertEquals("study session", Parser.parseFind("   study session   "));
    }

    @Test
    public void parseFind_emptyInput_throwsException() {
        TaskTrackerException ex = assertThrows(TaskTrackerException.class, () -> {
            Parser.parseFind("   ");
        });
        assertEquals(Message.ERR_EMPTY_FIND, ex.getMessage());
    }

    @Test
    public void parseFind_containsPipeCharacter_throwsException() {
        TaskTrackerException ex = assertThrows(TaskTrackerException.class, () -> {
            Parser.parseFind("read | book");
        });
        assertEquals(Message.ERR_NO_DELIMITER, ex.getMessage());
    }

    // =========================================================================
    // ToDo Parsing Tests
    // =========================================================================

    @Test
    public void parseToDo_validDescription_success() throws TaskTrackerException {
        ToDo todo = Parser.parseToDo("read textbook chapter 4");
        assertNotNull(todo);
        assertEquals("read textbook chapter 4", todo.getDescription());
    }

    @Test
    public void parseToDo_whitespacePadding_trimmedProperly() throws TaskTrackerException {
        ToDo todo = Parser.parseToDo("   submit survey   ");
        assertNotNull(todo);
        assertEquals("submit survey", todo.getDescription());
    }

    @Test
    public void parseToDo_emptyInput_throwsException() {
        TaskTrackerException ex = assertThrows(TaskTrackerException.class, () -> {
            Parser.parseToDo("   ");
        });
        assertEquals(Message.ERR_EMPTY_TODO, ex.getMessage());
    }

    @Test
    public void parseToDo_containsPipeCharacter_throwsException() {
        TaskTrackerException ex = assertThrows(TaskTrackerException.class, () -> {
            Parser.parseToDo("buy | milk");
        });
        assertEquals(Message.ERR_NO_DELIMITER, ex.getMessage());
    }

    // =========================================================================
    // Deadline Parsing Tests
    // =========================================================================

    @Test
    public void parseDeadline_validInput_success() throws TaskTrackerException {
        Deadline deadline = Parser.parseDeadline("submit assignment /by 2026-10-15 2359");
        assertNotNull(deadline);
        assertEquals("submit assignment", deadline.getDescription());
    }

    @Test
    public void parseDeadline_whitespacePadding_trimmedProperly() throws TaskTrackerException {
        Deadline deadline = Parser.parseDeadline("   return library books   /by   2026-11-01 1800  ");
        assertNotNull(deadline);
        assertEquals("return library books", deadline.getDescription());
    }

    @Test
    public void parseDeadline_emptyInput_throwsException() {
        assertThrows(TaskTrackerException.class, () -> {
            Parser.parseDeadline("");
        });
        assertThrows(TaskTrackerException.class, () -> {
            Parser.parseDeadline("   ");
        });
    }

    @Test
    public void parseDeadline_missingByDelimiter_throwsException() {
        TaskTrackerException ex = assertThrows(TaskTrackerException.class, () -> {
            Parser.parseDeadline("submit assignment 2026-10-15 2359");
        });
        assertEquals(Message.ERR_MISSING_BY, ex.getMessage());
    }

    @Test
    public void parseDeadline_missingDescription_throwsException() {
        assertThrows(TaskTrackerException.class, () -> {
            Parser.parseDeadline(" /by 2026-10-15 2359");
        });
    }

    @Test
    public void parseDeadline_emptyDate_throwsException() {
        assertThrows(TaskTrackerException.class, () -> {
            Parser.parseDeadline("submit assignment /by ");
        });
    }

    @Test
    public void parseDeadline_containsPipeCharacter_throwsException() {
        TaskTrackerException ex = assertThrows(TaskTrackerException.class, () -> {
            Parser.parseDeadline("submit | report /by 2026-10-15 2359");
        });
        assertEquals(Message.ERR_NO_DELIMITER, ex.getMessage());
    }

    // =========================================================================
    // Event Parsing Tests
    // =========================================================================

    @Test
    public void parseEvent_validInput_success() throws TaskTrackerException {
        String input = "project meeting /from 2026-10-15 1400 /to 2026-10-15 1600";
        Event event = Parser.parseEvent(input);

        assertNotNull(event);
        assertEquals("project meeting", event.getDescription());
    }

    @Test
    public void parseEvent_emptyInput_throwsException() {
        assertThrows(TaskTrackerException.class, () -> {
            Parser.parseEvent("");
        });
        assertThrows(TaskTrackerException.class, () -> {
            Parser.parseEvent("   ");
        });
    }

    @Test
    public void parseEvent_missingFromDelimiter_throwsException() {
        TaskTrackerException ex = assertThrows(TaskTrackerException.class, () -> {
            Parser.parseEvent("project meeting /to 2026-10-15 1600");
        });
        assertEquals(Message.ERR_MISSING_FROM, ex.getMessage());
    }

    @Test
    public void parseEvent_missingToDelimiter_throwsException() {
        TaskTrackerException ex = assertThrows(TaskTrackerException.class, () -> {
            Parser.parseEvent("project meeting /from 2026-10-15 1400");
        });
        assertEquals(Message.ERR_MISSING_TO, ex.getMessage());
    }

    @Test
    public void parseEvent_invertedDelimiters_throwsException() {
        TaskTrackerException ex = assertThrows(TaskTrackerException.class, () -> {
            Parser.parseEvent("project meeting /to 2026-10-15 1600 /from 2026-10-15 1400");
        });
        assertEquals(Message.ERR_OUT_OF_ORDER, ex.getMessage());
    }

    @Test
    public void parseEvent_missingDescription_throwsException() {
        assertThrows(TaskTrackerException.class, () -> {
            Parser.parseEvent(" /from 2026-10-15 1400 /to 2026-10-15 1600");
        });
    }

    @Test
    public void parseEvent_emptyTimes_throwsException() {
        assertThrows(TaskTrackerException.class, () -> {
            Parser.parseEvent("meeting /from  /to 2026-10-15 1600");
        });
        assertThrows(TaskTrackerException.class, () -> {
            Parser.parseEvent("meeting /from 2026-10-15 1400 /to ");
        });
    }

    @Test
    public void parseEvent_endBeforeStartChronology_throwsException() {
        TaskTrackerException ex = assertThrows(TaskTrackerException.class, () -> {
            Parser.parseEvent("meeting /from 2026-10-15 1800 /to 2026-10-15 1400");
        });
        assertEquals(Message.ERR_EVENT_CHRONOLOGY, ex.getMessage());
    }

    @Test
    public void parseEvent_containsPipeCharacter_throwsException() {
        TaskTrackerException ex = assertThrows(TaskTrackerException.class, () -> {
            Parser.parseEvent("meet | team /from 2026-10-15 1400 /to 2026-10-15 1600");
        });
        assertEquals(Message.ERR_NO_DELIMITER, ex.getMessage());
    }

    @Test
    public void parseEvent_bothFromAndToMissingValues_throwsMissingFromException() {
        TaskTrackerException ex = assertThrows(TaskTrackerException.class, () -> {
            Parser.parseEvent("kirk anniversary /from /to");
        });
        assertEquals(Message.ERR_MISSING_FROM, ex.getMessage());
    }

    @Test
    public void parseEvent_sameStartAndEndTime_success() throws TaskTrackerException {
        String input = "instant workshop /from 2026-10-15 1400 /to 2026-10-15 1400";
        Event event = Parser.parseEvent(input);

        assertNotNull(event);
        assertEquals("instant workshop", event.getDescription());
    }

    @Test
    public void parseEvent_pipeInDateFields_throwsException() {
        TaskTrackerException exFrom = assertThrows(TaskTrackerException.class, () -> {
            Parser.parseEvent("meeting /from 2026-10-15 | 1400 /to 2026-10-15 1600");
        });
        assertEquals(Message.ERR_NO_DELIMITER, exFrom.getMessage());

        TaskTrackerException exTo = assertThrows(TaskTrackerException.class, () -> {
            Parser.parseEvent("meeting /from 2026-10-15 1400 /to 2026-10-15 | 1600");
        });
        assertEquals(Message.ERR_NO_DELIMITER, exTo.getMessage());
    }

    @Test
    public void parseEvent_whitespacePadding_trimmedProperly() throws TaskTrackerException {
        String input = "   project meeting    /from    2026-10-15 1400    /to    2026-10-15 1600   ";
        Event event = Parser.parseEvent(input);

        assertNotNull(event);
        assertEquals("project meeting", event.getDescription());
    }

    @Test
    public void parseEvent_delimitersWithoutSurroundingSpaces_treatedAsText() {
        assertThrows(TaskTrackerException.class, () -> {
            Parser.parseEvent("project/from/to");
        });
    }

    @Test
    public void parseEvent_invalidCalendarDate_throwsException() {
        // Non-existent day (Feb 30)
        assertThrows(TaskTrackerException.class, () -> {
            Parser.parseEvent("annual retreat /from 2026-02-30 1000 /to 2026-02-30 1800");
        });

        // 2026 is not a leap year (Feb 29 invalid)
        assertThrows(TaskTrackerException.class, () -> {
            Parser.parseEvent("leap meetup /from 2026-02-29 1000 /to 2026-02-29 1200");
        });
    }

    @Test
    public void parseEvent_invalidTimeFormatOrValues_throwsException() {
        // Hour out of bounds (25:00)
        assertThrows(TaskTrackerException.class, () -> {
            Parser.parseEvent("night hackathon /from 2026-10-15 2500 /to 2026-10-16 0200");
        });

        // Minute out of bounds (60 mins)
        assertThrows(TaskTrackerException.class, () -> {
            Parser.parseEvent("standup /from 2026-10-15 0960 /to 2026-10-15 1000");
        });

        // Non-standard date pattern (slashes instead of dashes)
        assertThrows(TaskTrackerException.class, () -> {
            Parser.parseEvent("meeting /from 15/10/2026 1400 /to 15/10/2026 1600");
        });
    }

    // =========================================================================
    // FixedDurationTask Tests
    // =========================================================================

    @Test
    public void parseFixedDurationTask_validInput_success() throws TaskTrackerException {
        String input = "study for finals /needs 2 hours";
        Fixed task = Parser.parseFixedDurationTask(input);

        assertNotNull(task);
        assertEquals("study for finals", task.getDescription());
        assertEquals("2 hours", task.getDuration());
    }

    @Test
    public void parseFixedDurationTask_whitespacePadding_trimmedProperly() throws TaskTrackerException {
        String input = "   workout session    /needs    45 minutes   ";
        Fixed task = Parser.parseFixedDurationTask(input);

        assertNotNull(task);
        assertEquals("workout session", task.getDescription());
        assertEquals("45 minutes", task.getDuration());
    }

    @Test
    public void parseFixedDurationTask_emptyInput_throwsException() {
        assertThrows(TaskTrackerException.class, () -> {
            Parser.parseFixedDurationTask("");
        });
        assertThrows(TaskTrackerException.class, () -> {
            Parser.parseFixedDurationTask("   ");
        });
    }

    @Test
    public void parseFixedDurationTask_missingNeedsDelimiter_throwsException() {
        TaskTrackerException ex = assertThrows(TaskTrackerException.class, () -> {
            Parser.parseFixedDurationTask("read book 2 hours");
        });
        assertEquals(Message.ERR_MISSING_NEEDS, ex.getMessage());
    }

    @Test
    public void parseFixedDurationTask_missingDescription_throwsException() {
        TaskTrackerException ex = assertThrows(TaskTrackerException.class, () -> {
            Parser.parseFixedDurationTask(" /needs 2 hours");
        });
        assertEquals(Message.ERR_EMPTY_FIXED, ex.getMessage());
    }

    @Test
    public void parseFixedDurationTask_emptyDuration_throwsException() {
        TaskTrackerException ex = assertThrows(TaskTrackerException.class, () -> {
            Parser.parseFixedDurationTask("read book /needs ");
        });
        assertEquals(Message.ERR_EMPTY_DURATION, ex.getMessage());
    }

    @Test
    public void parseFixedDurationTask_containsPipeInDescription_throwsException() {
        TaskTrackerException ex = assertThrows(TaskTrackerException.class, () -> {
            Parser.parseFixedDurationTask("read | book /needs 2 hours");
        });
        assertEquals(Message.ERR_NO_DELIMITER, ex.getMessage());
    }

    @Test
    public void parseFixedDurationTask_containsPipeInDuration_throwsException() {
        TaskTrackerException ex = assertThrows(TaskTrackerException.class, () -> {
            Parser.parseFixedDurationTask("read book /needs 2 | hours");
        });
        assertEquals(Message.ERR_NO_DELIMITER, ex.getMessage());
    }

    @Test
    public void parseFixedDurationTask_delimiterWithoutSpaces_throwsMissingNeedsException() {
        TaskTrackerException ex = assertThrows(TaskTrackerException.class, () -> {
            Parser.parseFixedDurationTask("read book/needs 2 hours");
        });
        assertEquals(Message.ERR_MISSING_NEEDS, ex.getMessage());
    }

    @Test
    public void parseFixedDurationTask_multipleNeedsDelimiters_handledCorrectly()
            throws TaskTrackerException {
        Fixed task = Parser.parseFixedDurationTask("review /needs notes /needs 2 hours");
        assertEquals("review", task.getDescription());
        assertEquals("notes /needs 2 hours", task.getDuration());
    }

    @Test
    public void parseFixedDurationTask_spacesOnlyDuration_throwsException() {
        assertThrows(TaskTrackerException.class, () -> {
            Parser.parseFixedDurationTask("read book /needs    ");
        });
    }
}
