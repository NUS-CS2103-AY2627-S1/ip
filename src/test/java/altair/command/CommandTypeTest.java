package altair.command;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link CommandType#from(String)}.
 *
 * <p>{@code from} is a good target for JUnit testing because it is a
 * <em>pure</em> function: it takes a {@code String}, returns a
 * {@link CommandType}, and has no side effects (no console, no files, no
 * randomness). That makes every result fully determined by the input, so each
 * test is a simple "given this input, expect this value" check.</p>
 *
 * <p>The tests are grouped by behavior: exact keyword recognition, keywords
 * followed by arguments, case-insensitivity, surrounding whitespace, rejecting
 * words that merely start with a keyword, and the fallbacks for empty, blank,
 * {@code null}, and unrecognized input.</p>
 */
public class CommandTypeTest {

    // ----- Exact keyword recognition (one check per command) -----

    @Test
    public void from_exactByeKeyword_returnsBye() {
        assertEquals(CommandType.BYE, CommandType.from("bye"));
    }

    @Test
    public void from_exactListKeyword_returnsList() {
        assertEquals(CommandType.LIST, CommandType.from("list"));
    }

    @Test
    public void from_exactFindKeyword_returnsFind() {
        assertEquals(CommandType.FIND, CommandType.from("find"));
    }

    @Test
    public void from_exactMarkKeyword_returnsMark() {
        assertEquals(CommandType.MARK, CommandType.from("mark"));
    }

    @Test
    public void from_exactUnmarkKeyword_returnsUnmark() {
        assertEquals(CommandType.UNMARK, CommandType.from("unmark"));
    }

    @Test
    public void from_exactDeleteKeyword_returnsDelete() {
        assertEquals(CommandType.DELETE, CommandType.from("delete"));
    }

    @Test
    public void from_exactTodoKeyword_returnsTodo() {
        assertEquals(CommandType.TODO, CommandType.from("todo"));
    }

    @Test
    public void from_exactDeadlineKeyword_returnsDeadline() {
        assertEquals(CommandType.DEADLINE, CommandType.from("deadline"));
    }

    @Test
    public void from_exactEventKeyword_returnsEvent() {
        assertEquals(CommandType.EVENT, CommandType.from("event"));
    }

    // ----- Keyword followed by arguments -----

    @Test
    public void from_markKeywordWithTaskNumber_returnsMark() {
        assertEquals(CommandType.MARK, CommandType.from("mark 2"));
    }

    @Test
    public void from_findKeywordWithKeyword_returnsFind() {
        assertEquals(CommandType.FIND, CommandType.from("find book"));
    }

    @Test
    public void from_todoKeywordWithDescription_returnsTodo() {
        assertEquals(CommandType.TODO, CommandType.from("todo borrow book"));
    }

    @Test
    public void from_deadlineKeywordWithDetails_returnsDeadline() {
        assertEquals(CommandType.DEADLINE, CommandType.from("deadline return book /by 2019-10-10"));
    }

    @Test
    public void from_eventKeywordWithDetails_returnsEvent() {
        assertEquals(CommandType.EVENT, CommandType.from("event camp /from 2019-10-10 /to 2019-10-12"));
    }

    @Test
    public void from_unmarkKeywordWithArgument_returnsUnmarkNotMark() {
        // "unmark" does not start with "mark", so it must not be mistaken for MARK.
        assertEquals(CommandType.UNMARK, CommandType.from("unmark 3"));
    }

    // ----- Case-insensitivity -----

    @Test
    public void from_uppercaseKeyword_returnsCommand() {
        assertEquals(CommandType.BYE, CommandType.from("BYE"));
    }

    @Test
    public void from_mixedCaseKeywordWithArguments_returnsCommand() {
        assertEquals(CommandType.TODO, CommandType.from("ToDo read chapter 1"));
    }

    // ----- Surrounding and internal whitespace -----

    @Test
    public void from_keywordWithSurroundingWhitespace_returnsCommand() {
        assertEquals(CommandType.LIST, CommandType.from("   list   "));
    }

    @Test
    public void from_tabBetweenKeywordAndArgument_returnsCommand() {
        // Any whitespace character (here a tab) separates the keyword from its arguments.
        assertEquals(CommandType.MARK, CommandType.from("mark\t3"));
    }

    // ----- Words that merely start with a keyword are not commands -----

    @Test
    public void from_keywordAsPrefixOfLongerWord_returnsUnknown() {
        assertEquals(CommandType.UNKNOWN, CommandType.from("listing"));
    }

    @Test
    public void from_findAsPrefixOfLongerWord_returnsUnknown() {
        assertEquals(CommandType.UNKNOWN, CommandType.from("finding nemo"));
    }

    @Test
    public void from_keywordImmediatelyFollowedByPunctuation_returnsUnknown() {
        assertEquals(CommandType.UNKNOWN, CommandType.from("mark,"));
    }

    // ----- Keyword must be the first word -----

    @Test
    public void from_keywordNotAtStart_returnsUnknown() {
        assertEquals(CommandType.UNKNOWN, CommandType.from("please list"));
    }

    // ----- Empty, blank, null, and unrecognized input -----

    @Test
    public void from_unrecognizedWord_returnsUnknown() {
        assertEquals(CommandType.UNKNOWN, CommandType.from("sing a song"));
    }

    @Test
    public void from_emptyString_returnsUnknown() {
        assertEquals(CommandType.UNKNOWN, CommandType.from(""));
    }

    @Test
    public void from_blankString_returnsUnknown() {
        assertEquals(CommandType.UNKNOWN, CommandType.from("    "));
    }

    @Test
    public void from_null_returnsUnknown() {
        assertEquals(CommandType.UNKNOWN, CommandType.from(null));
    }
}
