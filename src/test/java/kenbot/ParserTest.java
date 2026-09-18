package kenbot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Tests {@link Parser}, which turns a typed line into a command and its
 * argument without acting on it.
 */
public class ParserTest {

    @Test
    public void parse_commandWithArgument_splitsAtTheFirstSpace() throws KenbotException {
        Parser.ParsedCommand parsed = Parser.parse("todo read book");
        assertEquals(CommandType.TODO, parsed.command());
        assertEquals("read book", parsed.argument());
    }

    @Test
    public void parse_commandWithoutArgument_givesAnEmptyArgument() throws KenbotException {
        Parser.ParsedCommand parsed = Parser.parse("list");
        assertEquals(CommandType.LIST, parsed.command());
        assertEquals("", parsed.argument());
    }

    /**
     * The argument keeps its own spaces, so only the command word is split off.
     */
    @Test
    public void parse_argumentWithSlashes_keptWhole() throws KenbotException {
        Parser.ParsedCommand parsed = Parser.parse("deadline return book /by 2019-10-15");
        assertEquals(CommandType.DEADLINE, parsed.command());
        assertEquals("return book /by 2019-10-15", parsed.argument());
    }

    @Test
    public void parse_unknownWord_throws() {
        assertThrows(KenbotException.class, () -> Parser.parse("blah"));
    }

    @Test
    public void parse_upperCaseCommand_throws() {
        assertThrows(KenbotException.class, () -> Parser.parse("TODO read book"));
    }

    /**
     * A bar would break the save file, so it is refused while the user is still
     * looking rather than silently losing part of the description later.
     */
    @Test
    public void parse_argumentContainingBar_throws() {
        KenbotException thrown = assertThrows(KenbotException.class, () -> Parser.parse("todo read | book"));
        assertEquals("Can't use '|' in a task, that's what"
                + " splits the fields in my save file.", thrown.getMessage());
    }

    @Test
    public void requireAtMostOne_markerAbsent_isAccepted() throws KenbotException {
        Parser.requireAtMostOne("read book", " /by ");
    }

    @Test
    public void requireAtMostOne_markerOnce_isAccepted() throws KenbotException {
        Parser.requireAtMostOne("read book /by 2019-10-15", " /by ");
    }

    @Test
    public void requireAtMostOne_markerTwice_throws() {
        assertThrows(KenbotException.class, () ->
                Parser.requireAtMostOne("x /by 2019-10-15 /by 2019-10-20", " /by "));
    }

    @Test
    public void requireAtMostOne_markerThreeTimes_throws() {
        assertThrows(KenbotException.class, () ->
                Parser.requireAtMostOne("x /to a /to b /to c", " /to "));
    }

    @Test
    public void requireAtMostOne_overlappingLooking_countsSeparately() throws KenbotException {
        // Two different markers in one line is normal for an event.
        Parser.requireAtMostOne("x /from a /to b", " /from ");
        Parser.requireAtMostOne("x /from a /to b", " /to ");
    }

    @Test
    public void requireAtMostOne_markerTwice_namesItInTheMessage() {
        KenbotException thrown = assertThrows(KenbotException.class, () ->
                Parser.requireAtMostOne("x /by a /by b", " /by "));
        assertTrue(thrown.getMessage().contains("/by"));
    }
}
