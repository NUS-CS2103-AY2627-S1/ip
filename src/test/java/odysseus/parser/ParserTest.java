package odysseus.parser;
import odysseus.OdysseusException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ParserTest {

    // To do tests
    @Test
    public void parseTodo_validDesc_returnsTodo() throws OdysseusException {
        String input = "read book";
        assertEquals("T | 0 | read book",
                Parser.parseTodo(input).toSaveFormat());
    }

    @Test
    public void parseTodo_emptyDesc_throwsOdysseusException() {
        assertThrows(OdysseusException.class, () -> Parser.parseTodo(""));
    }

    // Deadline tests

    @Test
    public void parseDeadline_validInput_returnsDeadline() throws OdysseusException {
        String input = "return book /by 2026-06-06";
        assertEquals("D | 0 | return book | 2026-06-06",
                 Parser.parseDeadline(input).toSaveFormat());
    }

    @Test
    public void parseDeadline_missingBy_throwsOdysseusException() {
        String input = "return book";
        OdysseusException e =
                assertThrows(OdysseusException.class, () -> Parser.parseDeadline(input));
        assertTrue(e.getMessage().contains("/by"));
    }

    @Test
    public void parseDeadline_missingDesc_throwsOdysseusException() {
        String input = " /by 2026-06-06";
        assertThrows(OdysseusException.class, () -> Parser.parseDeadline(input));
    }

    @Test
    public void parseDeadline_invalidDate_throwsOdysseusException() {
        String input = "return book /by 2026-32-56";
        OdysseusException e = assertThrows(OdysseusException.class, () ->
                Parser.parseDeadline(input));
        assertTrue(e.getMessage().contains("YYYY-MM-DD"));
    }

    // Event tests

    @Test
    public void parseEvent_validInput_returnsEvent() throws OdysseusException {
        String line = "proj /from mon /to tue";
        assertEquals("E | 0 | proj | mon | tue",
                Parser.parseEvent(line).toSaveFormat());
    }

    @Test
    public void parseEvent_missingFrom_throwsOdysseusException() {
        String line = "proj";
        OdysseusException e = assertThrows(OdysseusException.class, () -> Parser.parseEvent(line));
        assertTrue(e.getMessage().contains("/from"));
    }

    @Test
    public void parseEvent_missingTo_throwsOdysseusException() {
        String line = "proj /from mon";
        OdysseusException e = assertThrows(OdysseusException.class, () -> Parser.parseEvent(line));
        assertTrue(e.getMessage().contains("/to"));
    }

    // ParseIndex tests
    @Test
    public void parseIndex_validNumber_returnsZeroBasedIndex() throws OdysseusException {
        assertEquals(1, Parser.parseIndex(new String[]{"mark","2"}, 5));
    }

    @Test
    public void parseIndex_notNumber_throwsOdysseusException() {
        OdysseusException e = assertThrows(OdysseusException.class, () ->
                Parser.parseIndex(new String[]{"mark", "abc"}, 5));
        assertTrue(e.getMessage().contains("number"));
    }

    @Test
    public void parseIndex_outOfRange_throwsOdysseusException() {
        OdysseusException e = assertThrows(OdysseusException.class, () ->
                Parser.parseIndex(new String[]{"mark", "10"}, 5));
        assertTrue(e.getMessage().contains("no task"));
    }

    @Test
    public void parseIndex_noNumber_throwsOdysseusException() {
        OdysseusException e = assertThrows(OdysseusException.class, () ->
            Parser.parseIndex(new String[]{"mark"}, 5));
        assertTrue(e.getMessage().contains("number"));
    }
}
