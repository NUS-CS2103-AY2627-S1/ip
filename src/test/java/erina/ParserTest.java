package erina;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import erina.task.Deadline;
import erina.task.Event;
import erina.task.Todo;

/**
 * Tests for {@link Parser}, which turns user-typed text into objects.
 *
 * <p>Parser is a natural unit-testing target: every method is a pure
 * function from a string to a value or an error, with no state to set up.
 */
public class ParserTest {
    @Test
    public void parse_commandWithArgument_splitsAtFirstSpace() throws ErinaException {
        ParsedCommand parsed = Parser.parse("deadline return book /by 2019-10-15");
        assertEquals(Command.DEADLINE, parsed.command());
        assertEquals("return book /by 2019-10-15", parsed.argument());
    }

    @Test
    public void parse_commandAlone_hasEmptyArgument() throws ErinaException {
        ParsedCommand parsed = Parser.parse("  list  ");
        assertEquals(Command.LIST, parsed.command());
        assertEquals("", parsed.argument());
    }

    @Test
    public void parse_extraSpacesAndCapitals_stillRecognised() throws ErinaException {
        ParsedCommand parsed = Parser.parse("  MARK    2 ");
        assertEquals(Command.MARK, parsed.command());
        assertEquals("2", parsed.argument());
    }

    @Test
    public void parse_unknownCommandWord_throws() {
        assertThrows(ErinaException.class, () -> Parser.parse("blah"));
    }

    @Test
    public void checkNoArgument_extraText_throws() {
        assertThrows(ErinaException.class, () -> Parser.checkNoArgument(Command.LIST, "all"));
    }

    @Test
    public void checkNoArgument_nothingAfterCommand_passes() {
        assertDoesNotThrow(() -> Parser.checkNoArgument(Command.LIST, ""));
    }

    @Test
    public void parseTodo_normalDescription_returnsTodo() throws ErinaException {
        Todo todo = Parser.parseTodo("read book");
        assertEquals("[T][ ] read book", todo.toString());
    }

    @Test
    public void parseTodo_emptyDescription_throws() {
        ErinaException e = assertThrows(ErinaException.class, () -> Parser.parseTodo(""));
        assertEquals("Pardon me. The description of a todo cannot be empty.", e.getMessage());
    }

    @Test
    public void parseTodo_saveSeparatorCharacter_throws() {
        // "|" would split the task into extra fields when the file is loaded.
        assertThrows(ErinaException.class, () -> Parser.parseTodo("read | write"));
    }

    @Test
    public void parseDeadline_descriptionAndDate_returnsDeadline() throws ErinaException {
        Deadline deadline = Parser.parseDeadline("return book /by 2019-10-15");
        assertEquals("[D][ ] return book (by: Oct 15 2019)", deadline.toString());
    }

    @Test
    public void parseDeadline_missingByPart_throws() {
        assertThrows(ErinaException.class, () -> Parser.parseDeadline("return book"));
    }

    @Test
    public void parseDeadline_textInsteadOfDate_throws() {
        assertThrows(ErinaException.class, () -> Parser.parseDeadline("return book /by Sunday"));
    }

    @Test
    public void parseDeadline_byGivenTwice_throws() {
        String input = "return book /by 2019-10-15 /by 2019-10-16";
        ErinaException e = assertThrows(ErinaException.class, () -> Parser.parseDeadline(input));
        assertEquals("Pardon me. Please give /by only once.", e.getMessage());
    }

    @Test
    public void parseDeadline_nonExistentDate_throws() {
        assertThrows(ErinaException.class, () -> Parser.parseDeadline("pay rent /by 2023-02-30"));
    }

    @Test
    public void parseEvent_freeTextTimes_returnsEvent() throws ErinaException {
        Event event = Parser.parseEvent("project meeting /from Mon 2pm /to 4pm");
        assertEquals("[E][ ] project meeting (from: Mon 2pm to: 4pm)", event.toString());
    }

    @Test
    public void parseEvent_endDateBeforeStartDate_throws() {
        assertThrows(ErinaException.class, () -> Parser.parseEvent("camp /from 2019-10-16 /to 2019-10-15"));
    }

    @Test
    public void parseEvent_sameStartAndEndDate_returnsEvent() {
        assertDoesNotThrow(() -> Parser.parseEvent("career fair /from 2019-10-15 /to 2019-10-15"));
    }

    @Test
    public void parseEvent_missingToPart_throws() {
        assertThrows(ErinaException.class, () -> Parser.parseEvent("project meeting /from Mon 2pm"));
    }

    @Test
    public void parseDate_isoText_returnsThatDate() throws ErinaException {
        assertEquals(LocalDate.of(2019, 10, 15), Parser.parseDate("2019-10-15"));
    }

    @Test
    public void parseDate_impossibleDate_throws() {
        assertThrows(ErinaException.class, () -> Parser.parseDate("2019-13-40"));
    }

    @Test
    public void parseIndex_numberInRange_returnsZeroBasedIndex() throws ErinaException {
        // The user's 2 refers to the second task, which is index 1 internally.
        assertEquals(1, Parser.parseIndex("2", 3));
    }

    @Test
    public void parseIndex_notANumber_throws() {
        ErinaException e = assertThrows(ErinaException.class, () -> Parser.parseIndex("abc", 3));
        assertEquals("Pardon me. \"abc\" is not a task number.", e.getMessage());
    }

    @Test
    public void parseIndex_numberOutOfRange_throws() {
        assertThrows(ErinaException.class, () -> Parser.parseIndex("4", 3));
    }

    @Test
    public void parseIndex_emptyArgument_throws() {
        assertThrows(ErinaException.class, () -> Parser.parseIndex("", 3));
    }

    @Test
    public void parseDeadline_markerInsideWord_isNotCountedAsMarker() throws ErinaException {
        // "/bypass" is part of the description, not a second /by marker.
        Deadline deadline = Parser.parseDeadline("fix /bypass valve /by 2026-09-18");
        assertEquals("fix /bypass valve", deadline.getDescription());
    }

    @Test
    public void parseEvent_fromGivenTwice_throws() {
        assertThrows(ErinaException.class, () -> Parser.parseEvent("trip /from Mon /from Tue /to Wed"));
    }

    @Test
    public void parseIndex_noTasksYet_throws() {
        ErinaException e = assertThrows(ErinaException.class, () -> Parser.parseIndex("1", 0));
        assertEquals("Pardon me. There are no tasks yet, so there is no task 1.", e.getMessage());
    }

    @Test
    public void parseIndex_zero_throws() {
        // Task numbers count from 1, so 0 is out of range even with tasks present.
        assertThrows(ErinaException.class, () -> Parser.parseIndex("0", 3));
    }
}
