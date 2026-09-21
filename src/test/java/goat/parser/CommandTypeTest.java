package goat.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import goat.GoatException;

/**
 * Tests the keyword lookup.
 *
 * This is the single place unrecognized input is rejected, so it is worth
 * checking that it matches exactly -- not by prefix, not ignoring case --
 * since anything looser would quietly accept typos as real commands.
 *
 * The test sits in goat.parser because the enum is package-private; a test in
 * the same package can still reach it, which is the usual arrangement for
 * testing something that is deliberately not part of a public interface.
 */
public class CommandTypeTest {

    @Test
    public void fromKeyword_everyKnownKeyword_returnsMatchingConstant() throws GoatException {
        assertEquals(CommandType.BYE, CommandType.fromKeyword("bye"));
        assertEquals(CommandType.LIST, CommandType.fromKeyword("list"));
        assertEquals(CommandType.MARK, CommandType.fromKeyword("mark"));
        assertEquals(CommandType.UNMARK, CommandType.fromKeyword("unmark"));
        assertEquals(CommandType.TODO, CommandType.fromKeyword("todo"));
        assertEquals(CommandType.DEADLINE, CommandType.fromKeyword("deadline"));
        assertEquals(CommandType.EVENT, CommandType.fromKeyword("event"));
        assertEquals(CommandType.DELETE, CommandType.fromKeyword("delete"));
        assertEquals(CommandType.FIND, CommandType.fromKeyword("find"));
        assertEquals(CommandType.SORT, CommandType.fromKeyword("sort"));
    }

    @Test
    public void fromKeyword_theTableAboveIsComplete() {
        // Guards against adding a constant and forgetting to test it: the
        // count above has to match the number of constants.
        assertEquals(10, CommandType.values().length);
    }

    @Test
    public void fromKeyword_unknownWord_exceptionThrown() {
        assertThrows(GoatException.class, () -> CommandType.fromKeyword("blah"));
    }

    @Test
    public void fromKeyword_empty_exceptionThrown() {
        assertThrows(GoatException.class, () -> CommandType.fromKeyword(""));
    }

    @Test
    public void fromKeyword_wrongCase_exceptionThrown() {
        assertThrows(GoatException.class, () -> CommandType.fromKeyword("Bye"));
        assertThrows(GoatException.class, () -> CommandType.fromKeyword("LIST"));
    }

    @Test
    public void fromKeyword_keywordWithSurroundingSpaces_exceptionThrown() {
        // Parser trims before it gets here, so the lookup itself matches exactly.
        assertThrows(GoatException.class, () -> CommandType.fromKeyword(" bye"));
    }

    @Test
    public void fromKeyword_prefixOfAKeyword_exceptionThrown() {
        assertThrows(GoatException.class, () -> CommandType.fromKeyword("mar"));
    }

    @Test
    public void fromKeyword_keywordWithSuffix_exceptionThrown() {
        assertThrows(GoatException.class, () -> CommandType.fromKeyword("marked"));
    }
}
