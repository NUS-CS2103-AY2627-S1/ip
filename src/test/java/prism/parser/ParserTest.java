package prism.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import prism.PrismException;

public class ParserTest {

    @Test
    public void parseTodoDescription_validInput_returnsTrimmedDescription() throws PrismException {
        String input = "todo read book";
        String description = Parser.parseTodoDescription(input);
        assertEquals("read book", description);
    }

    @Test
    public void parseTodoDescription_paddedInput_returnsTrimmedDescription() throws PrismException {
        String input = "todo    return library book   ";
        String description = Parser.parseTodoDescription(input);
        assertEquals("return library book", description);
    }

    @Test
    public void parseTodoDescription_emptyDescription_throwsException() {
        String input = "todo";
        PrismException exception = assertThrows(PrismException.class, () -> Parser.parseTodoDescription(input));
        assertEquals("!!! The description of a todo cannot be empty.", exception.getMessage());
    }

    @Test
    public void parseTodoDescription_onlyWhitespace_throwsException() {
        String input = "todo     ";
        assertThrows(PrismException.class, () -> Parser.parseTodoDescription(input));
    }

    @Test
    public void parseFindKeyword_validInput_returnsTrimmedKeyword() throws PrismException {
        assertEquals("book", Parser.parseFindKeyword("find    book   "));
    }

    @Test
    public void parseFindKeyword_emptyKeyword_throwsException() {
        assertThrows(PrismException.class, () -> Parser.parseFindKeyword("find   "));
    }

    @Test
    public void parseCommandType_supportedCommands_returnsExpectedTypes() throws PrismException {
        assertEquals(Parser.CommandType.LIST, Parser.parseCommandType("list"));
        assertEquals(Parser.CommandType.MARK, Parser.parseCommandType("mark 1"));
        assertEquals(Parser.CommandType.DEADLINE, Parser.parseCommandType("deadline task /by tomorrow"));
        assertEquals(Parser.CommandType.SNOOZE, Parser.parseCommandType("snooze 1 /to tomorrow"));
    }

    @Test
    public void parseCommandType_unknownCommand_throwsException() {
        PrismException exception = assertThrows(PrismException.class, () ->
                Parser.parseCommandType("archive everything"));
        assertEquals("!!! I'm sorry, but I don't know what that means", exception.getMessage());
    }

    @Test
    public void parseIndex_validInput_returnsZeroBasedIndex() throws PrismException {
        assertEquals(2, Parser.parseIndex("delete 3", "delete"));
    }

    @Test
    public void parseIndex_invalidInput_throwsHelpfulException() {
        PrismException exception = assertThrows(PrismException.class, () ->
                Parser.parseIndex("delete", "delete"));
        assertEquals("!!! Please tell me which task number to delete, e.g. 'delete 2'.", exception.getMessage());
    }

    @Test
    public void parseDeadlineArgs_validInput_returnsParts() throws PrismException {
        assertEquals("return book", Parser.parseDeadlineArgs("deadline return book /by 2025-12-02 1800")[0]);
        assertEquals("2025-12-02 1800", Parser.parseDeadlineArgs("deadline return book /by 2025-12-02 1800")[1]);
    }

    @Test
    public void parseEventArgs_missingEndpoint_throwsException() {
        assertThrows(PrismException.class, () -> Parser.parseEventArgs("event meeting /from 2025-12-02 1800"));
    }

    @Test
    public void parseQueryDate_supportedFormats_returnsDate() throws PrismException {
        assertEquals("2025-12-02", Parser.parseQueryDate("date 2/12/2025").toString());
        assertEquals("2025-12-02", Parser.parseQueryDate("date 2025-12-02").toString());
    }

    @Test
    public void parseQueryDate_invalidDate_throwsException() {
        PrismException exception = assertThrows(PrismException.class, () ->
                Parser.parseQueryDate("date 2025-02-30"));
        assertEquals("!!! That is not a valid calendar date. Please use yyyy-MM-dd, "
                + "such as '2025-12-02'.", exception.getMessage());
        assertThrows(PrismException.class, () -> Parser.parseQueryDate("date 2025-99-99"));
        assertThrows(PrismException.class, () -> Parser.parseQueryDate("date 31/04/2025"));
    }

    @Test
    public void parseQueryDate_validLeapDay_returnsDate() throws PrismException {
        assertEquals("2024-02-29", Parser.parseQueryDate("date 2024-02-29").toString());
    }
}
