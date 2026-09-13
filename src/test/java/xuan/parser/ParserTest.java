package xuan.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import xuan.exception.XuanException;

public class ParserTest {

    @Test
    public void getCommandWord_commandWithArgument_returnsCommandWord() {
        Parser parser = new Parser();

        String result = parser.getCommandWord("todo read book");

        assertEquals("todo", result);
    }

    @Test
    public void getCommandWord_commandWithoutArgument_returnsCommandWord() {
        Parser parser = new Parser();

        String result = parser.getCommandWord("list");

        assertEquals("list", result);
    }

    @Test
    public void getCommandWord_inputWithExtraSpaces_returnsCommandWord() {
        Parser parser = new Parser();

        String result = parser.getCommandWord("   todo read book   ");

        assertEquals("todo", result);
    }

    @Test
    public void getCommandWord_emptyInput_returnsEmptyString() {
        Parser parser = new Parser();

        String result = parser.getCommandWord("   ");

        assertEquals("", result);
    }

    @Test
    public void validateNoArguments_extraArgument_throwsXuanException() {
        Parser parser = new Parser();

        assertThrows(XuanException.class, () ->
                parser.validateNoArguments("list abc", "list"));
    }

    @Test
    public void getDeadlineDescription_repeatedByParameter_throwsXuanException() {
        Parser parser = new Parser();

        assertThrows(XuanException.class, () ->
                parser.getDeadlineDescription(
                        "deadline test /by 2026-09-10 /by 2026-09-11"));
    }

    @Test
    public void getEventDescription_repeatedFromParameter_throwsXuanException() {
        Parser parser = new Parser();

        assertThrows(XuanException.class, () ->
                parser.getEventDescription(
                        "event meeting /from 2pm /from 3pm /to 4pm"));
    }

    @Test
    public void getTaskNumber_validNumber_returnsNumber() throws XuanException {
        Parser parser = new Parser();

        int result = parser.getTaskNumber("mark 3", 5);

        assertEquals(3, result);
    }

    @Test
    public void getTaskNumber_nonNumericNumber_throwsXuanException() {
        Parser parser = new Parser();

        assertThrows(XuanException.class, () ->
                parser.getTaskNumber("mark abc", 5));
    }

    @Test
    public void getDescription_validDescription_returnsDescription()
            throws XuanException {
        Parser parser = new Parser();

        String result = parser.getDescription("todo read book", 4);

        assertEquals("read book", result);
    }

    @Test
    public void getDescription_emptyDescription_throwsXuanException() {
        Parser parser = new Parser();

        assertThrows(XuanException.class, () ->
                parser.getDescription("todo   ", 4));
    }

    @Test
    public void getDeadlineDate_validDate_returnsDate() throws XuanException {
        Parser parser = new Parser();

        LocalDate result =
                parser.getDeadlineDate("deadline test /by 2026-09-20");

        assertEquals(LocalDate.parse("2026-09-20"), result);
    }

    @Test
    public void getDeadlineDate_invalidDate_throwsXuanException() {
        Parser parser = new Parser();

        assertThrows(XuanException.class, () ->
                parser.getDeadlineDate("deadline test /by 2026-02-30"));
    }



}
