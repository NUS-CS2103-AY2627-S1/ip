package dennis.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Tests for {@link CommandType#from(String)}, which decides what kind of
 * command a line of input is by looking at its first word.
 *
 * <p>Pure function with no state, so every case is a direct input/output
 * check. The interesting behaviour is the handling of {@code null}, blank,
 * and surrounding whitespace, plus the fact that only an exact first-word
 * match counts (so {@code "listing"} is not {@code list}).</p>
 */
public class CommandTypeTest {

    @Test
    public void from_eachKeyword_mapsToItsType() {
        assertEquals(CommandType.BYE, CommandType.from("bye"));
        assertEquals(CommandType.LIST, CommandType.from("list"));
        assertEquals(CommandType.MARK, CommandType.from("mark"));
        assertEquals(CommandType.UNMARK, CommandType.from("unmark"));
        assertEquals(CommandType.DELETE, CommandType.from("delete"));
        assertEquals(CommandType.TODO, CommandType.from("todo"));
        assertEquals(CommandType.DEADLINE, CommandType.from("deadline"));
        assertEquals(CommandType.EVENT, CommandType.from("event"));
        assertEquals(CommandType.ON, CommandType.from("on"));
        assertEquals(CommandType.WITHIN, CommandType.from("within"));
        assertEquals(CommandType.FIND, CommandType.from("find"));
    }

    @Test
    public void from_keywordWithArguments_usesOnlyTheFirstWord() {
        assertEquals(CommandType.MARK, CommandType.from("mark 3"));
        assertEquals(CommandType.TODO, CommandType.from("todo read book"));
        assertEquals(CommandType.DEADLINE,
                CommandType.from("deadline x /by 2019-12-01"));
    }

    @Test
    public void from_surroundingWhitespace_isIgnored() {
        assertEquals(CommandType.LIST, CommandType.from("   list   "));
    }

    @Test
    public void from_wordSeparatedByTab_stillMatches() {
        assertEquals(CommandType.BYE, CommandType.from("bye\tnow"));
    }

    @Test
    public void from_null_returnsUnknown() {
        assertEquals(CommandType.UNKNOWN, CommandType.from(null));
    }

    @Test
    public void from_emptyString_returnsUnknown() {
        assertEquals(CommandType.UNKNOWN, CommandType.from(""));
    }

    @Test
    public void from_whitespaceOnly_returnsUnknown() {
        assertEquals(CommandType.UNKNOWN, CommandType.from("    "));
    }

    @Test
    public void from_unrecognisedWord_returnsUnknown() {
        assertEquals(CommandType.UNKNOWN, CommandType.from("sing a song"));
    }

    @Test
    public void from_keywordAsPrefixOnly_returnsUnknown() {
        // "listing" is not the word "list".
        assertEquals(CommandType.UNKNOWN, CommandType.from("listing"));
    }

    @Test
    public void from_wrongCase_returnsUnknown() {
        // Matching is case-sensitive.
        assertEquals(CommandType.UNKNOWN, CommandType.from("BYE"));
    }
}
