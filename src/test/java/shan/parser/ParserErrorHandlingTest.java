package shan.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import shan.command.AddCommand;
import shan.command.ListCommand;
import shan.command.MarkCommand;
import shan.exception.InvalidArgumentException;
import shan.exception.InvalidCommandException;
import shan.exception.MissingArgumentException;
import shan.exception.ShanException;
import shan.task.Deadline;
import shan.task.ToDo;

class ParserErrorHandlingTest {
    @Test
    void parse_leadingTrailingAndRepeatedWhitespace_returnsCommand() throws ShanException {
        assertInstanceOf(ListCommand.class, Parser.parse("   list   "));
        assertInstanceOf(MarkCommand.class, Parser.parse("  mark     1  "));
        assertInstanceOf(AddCommand.class, Parser.parse(
                "  deadline   return book    /by    2019-12-02   18:00  "));
    }

    @Test
    void parse_nullInput_invalidCommandExceptionThrown() {
        assertThrows(InvalidCommandException.class, () -> Parser.parse(null));
    }

    @Test
    void parse_argumentFreeCommandWithArguments_invalidArgumentExceptionThrown() {
        assertThrows(InvalidArgumentException.class, () -> Parser.parse("list all"));
        assertThrows(InvalidArgumentException.class, () -> Parser.parse("bye now"));
        assertThrows(InvalidArgumentException.class, () -> Parser.parse("undo twice"));
    }

    @Test
    void parseTaskNumber_surroundingWhitespace_returnsTaskNumber() throws ShanException {
        assertEquals(12, Parser.parseTaskNumber("  12  "));
    }

    @Test
    void parseTaskNumber_nonPositiveSpecialOrOverflowValue_invalidArgumentExceptionThrown() {
        String[] invalidTaskNumbers = {"0", "-1", "+1", "1.5", "1 2", "999999999999999999999"};

        for (String invalidTaskNumber : invalidTaskNumbers) {
            assertThrows(InvalidArgumentException.class, () -> Parser.parseTaskNumber(invalidTaskNumber));
        }
    }

    @Test
    void parseTaskNumber_nullValue_missingArgumentExceptionThrown() {
        assertThrows(MissingArgumentException.class, () -> Parser.parseTaskNumber(null));
    }

    @Test
    void parseToDo_surroundingWhitespace_returnsTrimmedDescription() throws ShanException {
        ToDo task = Parser.parseToDo("   read book   ");

        assertEquals("[T][ ] read book", task.toString());
    }

    @Test
    void parseToDo_controlCharacter_invalidArgumentExceptionThrown() {
        assertThrows(InvalidArgumentException.class, () -> Parser.parseToDo("read\u0000book"));
    }

    @Test
    void parseDeadline_repeatedParameter_invalidArgumentExceptionThrown() {
        assertThrows(InvalidArgumentException.class, () -> Parser.parseDeadline(
                "return book /by 2019-12-02 18:00 /by 2019-12-03 18:00"));
    }

    @Test
    void parseDeadline_parameterTextInsideDescription_returnsDeadline() throws ShanException {
        Deadline task = Parser.parseDeadline(
                "stand/by task /by 2019-12-02 18:00");

        assertEquals("D | 0 | stand/by task | 2019-12-02 18:00", task.toFileString());
    }

    @Test
    void parseEvent_repeatedOrOutOfOrderParameters_invalidArgumentExceptionThrown() {
        assertThrows(InvalidArgumentException.class, () -> Parser.parseEvent(
                "meeting /from 2019-12-02 14:00 /from 2019-12-02 15:00"
                        + " /to 2019-12-02 16:00"));
        assertThrows(InvalidArgumentException.class, () -> Parser.parseEvent(
                "meeting /from 2019-12-02 14:00 /to 2019-12-02 16:00"
                        + " /to 2019-12-02 17:00"));
        assertThrows(InvalidArgumentException.class, () -> Parser.parseEvent(
                "meeting /to 2019-12-02 16:00 /from 2019-12-02 14:00"));
    }

    @Test
    void parseDateRange_repeatedParameter_invalidArgumentExceptionThrown() {
        assertThrows(InvalidArgumentException.class, () -> Parser.parseDateRange(
                "2019-12-02 /to 2019-12-03 /to 2019-12-04"));
    }
}
