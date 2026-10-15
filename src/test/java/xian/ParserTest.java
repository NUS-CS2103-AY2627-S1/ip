package xian;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import org.junit.jupiter.api.Test;

public class ParserTest {

    @Test
    public void getCommandWord_singleWordInput_returnsSameWord() {
        assertEquals("list", Parser.getCommandWord("list"));
    }

    @Test
    public void getCommandWord_multiWordInput_returnsFirstWord() {
        assertEquals("todo", Parser.getCommandWord("todo read book"));
    }

    @Test
    public void getArguments_validInput_returnsRemainder() throws XianException {
        assertEquals("read book", Parser.getArguments("todo read book"));
    }

    @Test
    public void getArguments_noArguments_throwsXianException() {
        assertThrows(XianException.class, () -> Parser.getArguments("todo"));
    }

    @Test
    public void getArguments_blankArguments_throwsXianException() {
        assertThrows(XianException.class, () -> Parser.getArguments("todo    "));
    }

    @Test
    public void normalizeInput_extraWhitespace_returnsNormalizedInput() throws XianException {
        assertEquals("todo read book", Parser.normalizeInput("  todo   read   book  "));
    }

    @Test
    public void normalizeInput_blankInput_throwsXianException() {
        assertThrows(XianException.class, () -> Parser.normalizeInput("   "));
    }

    @Test
    public void normalizeInput_nullInput_throwsXianException() {
        assertThrows(XianException.class, () -> Parser.normalizeInput(null));
    }

    @Test
    public void parseIndex_validNumber_returnsCorrectInt() {
        assertEquals(3, Parser.parseIndex("3"));
    }

    @Test
    public void parseIndex_invalidNumber_throwsNumberFormatException() {
        assertThrows(NumberFormatException.class, () -> Parser.parseIndex("abc"));
    }

    @Test
    public void parseTodo_validInput_returnsTodoWithCorrectDescription() throws XianException {
        Task t = Parser.parseTodo("read book");
        assertInstanceOf(Todo.class, t);
        assertEquals("read book", t.getDescription());
    }

    @Test
    public void parseTodo_storageDelimiterInDescription_throwsXianException() {
        assertThrows(XianException.class, () -> Parser.parseTodo("read | book"));
    }

    @Test
    public void parseUpdate_validInput_returnsUpdateParts() throws XianException {
        String[] updateParts = Parser.parseUpdate("2 by 12/9/2026 1800");

        assertEquals("2", updateParts[0]);
        assertEquals("by", updateParts[1]);
        assertEquals("12/9/2026 1800", updateParts[2]);
    }

    @Test
    public void parseUpdate_missingValue_throwsXianException() {
        assertThrows(XianException.class, () -> Parser.parseUpdate("2 desc"));
    }

    @Test
    public void parseDeadline_validInput_returnsDeadlineWithCorrectFields() throws XianException {
        Task t = Parser.parseDeadline("project work /by 2/12/2019 1800");
        assertInstanceOf(Deadline.class, t);
        assertEquals("project work", t.getDescription());
        assertEquals("[D][ ] project work (by: 2/12/2019 1800)", t.toString());
    }

    @Test
    public void parseDeadline_missingByKeyword_throwsXianException() {
        assertThrows(XianException.class, () -> Parser.parseDeadline("project work"));
    }

    @Test
    public void parseDeadline_invalidDateFormat_throwsDateTimeParseException() {
        assertThrows(DateTimeParseException.class, () -> Parser.parseDeadline("project work /by not-a-date"));
    }

    @Test
    public void parseEvent_validInput_returnsEventWithCorrectFields() throws XianException {
        Task t = Parser.parseEvent("partyyy /from 21/3/2021 1200 /to 21/3/2021 1800");
        assertInstanceOf(Event.class, t);
        assertEquals("partyyy", t.getDescription());
        assertEquals("[E][ ] partyyy (from: 21/3/2021 1200 to: 21/3/2021 1800)", t.toString());
    }

    @Test
    public void parseEvent_missingToKeyword_throwsXianException() {
        assertThrows(XianException.class, () -> Parser.parseEvent("partyyy /from 21/3/2021 1200"));
    }

    @Test
    public void validateEventTimeRange_endBeforeStart_throwsXianException() {
        LocalDateTime from = LocalDateTime.of(2021, 3, 21, 18, 0);
        LocalDateTime to = LocalDateTime.of(2021, 3, 21, 12, 0);

        assertThrows(XianException.class, () -> Parser.validateEventTimeRange(from, to));
    }
}
