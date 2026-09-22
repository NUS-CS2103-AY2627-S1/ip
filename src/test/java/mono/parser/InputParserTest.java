package mono.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import mono.exception.EmptyInputException;
import mono.parser.InputParser.ToolInput;

/**
 * Tests parsing a command line into its tool name and arguments.
 */
public class InputParserTest {
    @Test
    public void parse_toolWithoutArguments_returnsToolNameAndEmptyArguments()
            throws EmptyInputException {
        assertEquals(new ToolInput("list", ""), InputParser.parse("list"));
    }

    @Test
    public void parse_toolWithArguments_returnsToolNameAndArguments() throws EmptyInputException {
        assertEquals(
                new ToolInput("todo", "read a book"),
                InputParser.parse("todo read a book"));
    }

    @Test
    public void parse_inputWithSurroundingAndRepeatedSpaces_trimsParsedParts()
            throws EmptyInputException {
        assertEquals(
                new ToolInput("deadline", "submit report /by 2026-09-01"),
                InputParser.parse("  deadline   submit report /by 2026-09-01   "));
    }

    @Test
    public void parse_emptyInput_throwsEmptyInputException() {
        EmptyInputException exception = assertThrows(
                EmptyInputException.class, () -> InputParser.parse(""));

        assertEquals("Input cannot be empty!", exception.getMessage());
    }

    @Test
    public void parse_whitespaceOnlyInput_throwsEmptyInputException() {
        assertThrows(EmptyInputException.class, () -> InputParser.parse(" \t\n "));
    }

    @Test
    public void parse_nullInput_throwsEmptyInputException() {
        assertThrows(EmptyInputException.class, () -> InputParser.parse(null));
    }
}
