package alexa;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Tests command recognition and argument validation performed by {@link Parser}. */
class ParserTest {
    private Parser parser;

    @BeforeEach
    void setUp() {
        parser = new Parser();
    }

    @Test
    void parseFindKeyword_surroundingWhitespace_returnsTrimmedKeyword() throws AlexaException {
        assertEquals("book", parser.parseFindKeyword("  book  "));
    }

    @Test
    void parseFindKeyword_blankKeyword_throwsException() {
        AlexaException exception = assertThrows(AlexaException.class, () -> parser.parseFindKeyword("  "));

        assertEquals("The keyword for a find command cannot be empty.", exception.getMessage());
    }

    @Test
    void isCommand_exactCommand_returnsTrue() {
        assertTrue(parser.isCommand("list", "list"));
    }

    @Test
    void isCommand_commandWithArgument_returnsTrue() {
        assertTrue(parser.isCommand("todo read book", "todo"));
    }

    @Test
    void isCommand_commandPrefixOnly_returnsFalse() {
        assertFalse(parser.isCommand("todone", "todo"));
    }

    @Test
    void getArgument_surroundingWhitespace_returnsTrimmedArgument() {
        assertEquals("read book", parser.getArgument("todo   read book   ", "todo"));
    }

    @Test
    void parseTodo_validDescription_createsTodo() throws AlexaException {
        Todo todo = parser.parseTodo("read book");

        assertEquals("[T][ ] read book", todo.toString());
        assertEquals("T | 0 | read book", todo.toStorageString());
    }

    @Test
    void parseTodo_blankDescription_throwsException() {
        AlexaException exception = assertThrows(AlexaException.class, () -> parser.parseTodo("   "));

        assertEquals("The description of a todo cannot be empty.", exception.getMessage());
    }

    @Test
    void parseDeadline_validIsoDate_createsDeadlineWithFriendlyDisplay() throws AlexaException {
        Deadline deadline = parser.parseDeadline("return book /by 2019-10-15");

        assertEquals("[D][ ] return book (by: Oct 15 2019)", deadline.toString());
        assertEquals("D | 0 | return book | 2019-10-15", deadline.toStorageString());
    }

    @Test
    void parseDeadline_missingDateMarker_throwsException() {
        AlexaException exception = assertThrows(AlexaException.class,
                () -> parser.parseDeadline("return book 2019-10-15"));

        assertEquals("A deadline needs a description and date: deadline DESCRIPTION /by yyyy-MM-dd.",
                exception.getMessage());
    }

    @Test
    void parseDeadline_invalidDate_throwsException() {
        AlexaException exception = assertThrows(AlexaException.class,
                () -> parser.parseDeadline("return book /by tomorrow"));

        assertEquals("The deadline date must use yyyy-MM-dd, for example 2019-10-15.", exception.getMessage());
    }

    @Test
    void parseEvent_validIsoDates_createsEventWithFriendlyDisplay() throws AlexaException {
        Event event = parser.parseEvent("project meeting /from 2019-10-16 /to 2019-10-17");

        assertEquals("[E][ ] project meeting (from: Oct 16 2019 to: Oct 17 2019)", event.toString());
        assertEquals("E | 0 | project meeting | 2019-10-16 | 2019-10-17", event.toStorageString());
    }

    @Test
    void parseEvent_missingEndDateMarker_throwsException() {
        AlexaException exception = assertThrows(AlexaException.class,
                () -> parser.parseEvent("project meeting /from 2019-10-16"));

        assertEquals("An event needs an end date after /to.", exception.getMessage());
    }

    @Test
    void parseTaskNumber_validTrimmedNumber_returnsNumber() throws AlexaException {
        assertEquals(2, parser.parseTaskNumber(" 2 ", "mark", 3));
    }

    @Test
    void parseTaskNumber_nonNumericText_throwsException() {
        AlexaException exception = assertThrows(AlexaException.class,
                () -> parser.parseTaskNumber("first", "mark", 3));

        assertEquals("Please provide a task number, for example: mark 1.", exception.getMessage());
    }

    @Test
    void parseTaskNumber_outsideTaskList_throwsException() {
        AlexaException exception = assertThrows(AlexaException.class,
                () -> parser.parseTaskNumber("4", "delete", 3));

        assertEquals("There is no task 4. Use list to see the task numbers.", exception.getMessage());
    }

    @Test
    void isCommand_tabSeparatedArgument_returnsTrue() {
        assertTrue(parser.isCommand("todo\tread book", "todo"));
    }

    @Test
    void parseTodo_descriptionWithStorageDelimiter_throwsException() {
        AlexaException exception = assertThrows(AlexaException.class, () -> parser.parseTodo("read | book"));

        assertEquals("A task description cannot contain the | character.", exception.getMessage());
    }

    @Test
    void parseDeadline_multipleSpacesAroundMarker_createsDeadline() throws AlexaException {
        Deadline deadline = parser.parseDeadline("return book   /by   2026-10-15");

        assertEquals("[D][ ] return book (by: Oct 15 2026)", deadline.toString());
    }

    @Test
    void parseDeadline_repeatedDateMarker_throwsException() {
        AlexaException exception = assertThrows(AlexaException.class,
                () -> parser.parseDeadline("return book /by 2026-10-15 /by 2026-10-16"));

        assertEquals("A deadline can contain /by only once.", exception.getMessage());
    }

    @Test
    void parseEvent_endDateNotLaterThanStartDate_throwsException() {
        AlexaException equalDateException = assertThrows(AlexaException.class,
                () -> parser.parseEvent("meeting /from 2026-10-15 /to 2026-10-15"));
        AlexaException earlierDateException = assertThrows(AlexaException.class,
                () -> parser.parseEvent("meeting /from 2026-10-16 /to 2026-10-15"));

        assertEquals("The event end date must be later than the start date.", equalDateException.getMessage());
        assertEquals("The event end date must be later than the start date.", earlierDateException.getMessage());
    }

    @Test
    void parseEvent_repeatedEndDateMarker_throwsException() {
        AlexaException exception = assertThrows(AlexaException.class,
                () -> parser.parseEvent("meeting /from 2026-10-15 /to 2026-10-16 /to 2026-10-17"));

        assertEquals("An event can contain /to only once.", exception.getMessage());
    }

    @Test
    void parseDeadline_blankDescriptionAndDate_throwsSpecificExceptions() {
        AlexaException blankDescription = assertThrows(AlexaException.class,
                () -> parser.parseDeadline(" /by 2026-10-15"));
        AlexaException blankDate = assertThrows(AlexaException.class,
                () -> parser.parseDeadline("return book /by "));

        assertEquals("The description of a deadline cannot be empty.", blankDescription.getMessage());
        assertEquals("The deadline date cannot be empty.", blankDate.getMessage());
    }

    @Test
    void parseEvent_repeatedStartMarkerAndInvalidDate_throwsSpecificExceptions() {
        AlexaException repeatedStart = assertThrows(AlexaException.class,
                () -> parser.parseEvent("meeting /from 2026-10-15 /from 2026-10-16"
                        + " /to 2026-10-17"));
        AlexaException invalidDate = assertThrows(AlexaException.class,
                () -> parser.parseEvent("meeting /from tomorrow /to 2026-10-17"));

        assertEquals("An event can contain /from only once.", repeatedStart.getMessage());
        assertEquals("The event start date must use yyyy-MM-dd, for example 2019-10-15.",
                invalidDate.getMessage());
    }

    @Test
    void parseTaskNumber_zeroAndNegativeNumber_throwsOutOfRangeException() {
        AlexaException zeroException = assertThrows(AlexaException.class,
                () -> parser.parseTaskNumber("0", "delete", 2));
        AlexaException negativeException = assertThrows(AlexaException.class,
                () -> parser.parseTaskNumber("-1", "delete", 2));

        assertEquals("There is no task 0. Use list to see the task numbers.", zeroException.getMessage());
        assertEquals("There is no task -1. Use list to see the task numbers.",
                negativeException.getMessage());
    }
}
