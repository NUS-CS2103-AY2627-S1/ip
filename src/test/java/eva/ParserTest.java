package eva;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class ParserTest {

    @Test
    void parseTodo_validInput_returnsTodoCommand() throws EvaException {
        Parser.ParsedCommand command =
                Parser.parse("todo read book");

        assertEquals(Parser.CommandType.TODO, command.getType());
        assertEquals("read book", command.getValue(0));
    }

    @Test
    void parseMark_validInput_returnsTaskNumber() throws EvaException {
        Parser.ParsedCommand command = Parser.parse("mark 3");

        assertEquals(Parser.CommandType.MARK, command.getType());
        assertEquals(3, command.getTaskNumber());
    }

    @Test
    void parseMark_extraSpaces_returnsTaskNumber() throws EvaException {
        Parser.ParsedCommand command = Parser.parse("  mark   3  ");

        assertEquals(Parser.CommandType.MARK, command.getType());
        assertEquals(3, command.getTaskNumber());
    }

    @Test
    void parseDeadline_extraSpaces_returnsDeadlineCommand()
            throws EvaException {
        Parser.ParsedCommand command = Parser.parse(
                " deadline  submit report  /by  2026-09-30 ");

        assertEquals(Parser.CommandType.DEADLINE, command.getType());
        assertEquals("submit report", command.getValue(0));
        assertEquals("2026-09-30", command.getValue(1));
    }

    @Test
    void parseSort_validInput_returnsSortCommand() throws EvaException {
        Parser.ParsedCommand command = Parser.parse("sort");

        assertEquals(Parser.CommandType.SORT, command.getType());
    }

    @Test
    void parseDeadline_missingBy_throwsEvaException() {
        assertThrows(EvaException.class, () -> Parser.parse("deadline submit project"));
    }

    @Test
    void parseUnknownCommand_throwsEvaException() {
        assertThrows(EvaException.class, () -> Parser.parse("hello"));
    }

    @Test
    void parseEmptyCommand_returnsHelpfulError() {
        EvaException error = assertThrows(
                EvaException.class, () -> Parser.parse("   "));

        assertEquals("Please enter a command.", error.getMessage());
    }

    @Test
    void parseMark_overflowReturnsHelpfulError() {
        String input = "mark 999999999999999999999";
        EvaException error = assertThrows(
                EvaException.class, () -> Parser.parse(input));

        assertEquals("Please specify a valid task number to mark.",
                error.getMessage());
    }

    @Test
    void parseSimpleCommands_returnsCorrectTypes() throws EvaException {
        assertEquals(Parser.CommandType.BYE,
                Parser.parse("bye").getType());
        assertEquals(Parser.CommandType.LIST,
                Parser.parse("list").getType());
        assertEquals(Parser.CommandType.SORT,
                Parser.parse("sort").getType());
    }

    @Test
    void parseUnmarkAndDelete_returnsTaskNumbers() throws EvaException {
        Parser.ParsedCommand unmark = Parser.parse("unmark 2");
        Parser.ParsedCommand delete = Parser.parse("delete 4");

        assertEquals(Parser.CommandType.UNMARK, unmark.getType());
        assertEquals(2, unmark.getTaskNumber());
        assertEquals(Parser.CommandType.DELETE, delete.getType());
        assertEquals(4, delete.getTaskNumber());
    }

    @Test
    void parseEvent_returnsDescriptionAndTimes() throws EvaException {
        Parser.ParsedCommand command = Parser.parse(
                "event team meeting /from 09:00 /to 10:00");

        assertEquals(Parser.CommandType.EVENT, command.getType());
        assertEquals("team meeting", command.getValue(0));
        assertEquals("09:00", command.getValue(1));
        assertEquals("10:00", command.getValue(2));
    }

    @Test
    void parseFind_returnsKeyword() throws EvaException {
        Parser.ParsedCommand command = Parser.parse("find report");

        assertEquals(Parser.CommandType.FIND, command.getType());
        assertEquals("report", command.getValue(0));
    }

    @Test
    void parseAi_returnsQuestion() throws EvaException {
        Parser.ParsedCommand command = Parser.parse(
                "@ai how do I add a deadline?");

        assertEquals(Parser.CommandType.AI, command.getType());
        assertEquals("how do I add a deadline?", command.getValue(0));
    }

    @Test
    void parseMissingArguments_throwsEvaException() {
        assertThrows(EvaException.class, () -> Parser.parse("todo"));
        assertThrows(EvaException.class, () -> Parser.parse("find"));
        assertThrows(EvaException.class, () -> Parser.parse("mark"));
        assertThrows(EvaException.class, () -> Parser.parse("event meeting"));
        assertThrows(EvaException.class, () -> Parser.parse("@ai"));
    }

    @Test
    void parseInvalidCommandFormats_throwsEvaException() {
        assertThrows(EvaException.class, () -> Parser.parse("mark abc"));
        assertThrows(EvaException.class, () -> Parser.parse("sort now"));
        assertThrows(EvaException.class, () -> Parser.parse("marking 2"));
        assertThrows(EvaException.class, () -> Parser.parse("event meeting /from 09:00"));
    }

    @Test
    void parseNullCommand_throwsEvaException() {
        assertThrows(EvaException.class, () -> Parser.parse(null));
    }
}
