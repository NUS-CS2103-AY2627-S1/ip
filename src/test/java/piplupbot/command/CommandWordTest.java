package piplupbot.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.junit.jupiter.api.Test;

import piplupbot.PiplupBotException;

/**
 * Tests {@link CommandWord}, which decides which command a typed line names.
 *
 * <p>This is the first decision the bot makes about any line, so getting it
 * wrong shows up as the wrong command running rather than as an error. The rule
 * it applies is not quite "does the line start with the keyword": a command that
 * takes an argument matches when the keyword is followed by a space, while a
 * command that takes none matches the keyword alone. The two halves of that rule
 * are what most of the cases below check, because each half is invisible without
 * the other -- and both are easy to lose by simplifying the matching to
 * {@code startsWith}.</p>
 */
public class CommandWordTest {

    // ---------- Recognising a command ----------

    @Test
    public void fromInput_keywordAlone_returnsCommandWord() throws PiplupBotException {
        assertEquals(CommandWord.LIST, CommandWord.fromInput("list"));
        assertEquals(CommandWord.BYE, CommandWord.fromInput("bye"));
    }

    @Test
    public void fromInput_keywordWithArgument_returnsCommandWord() throws PiplupBotException {
        assertEquals(CommandWord.TODO, CommandWord.fromInput("todo read book"));
        assertEquals(CommandWord.DEADLINE, CommandWord.fromInput("deadline return book /by 2019-10-15 1800"));
        assertEquals(CommandWord.EVENT, CommandWord.fromInput("event meeting /from x /to y"));
        assertEquals(CommandWord.MARK, CommandWord.fromInput("mark 2"));
        assertEquals(CommandWord.UNMARK, CommandWord.fromInput("unmark 2"));
        assertEquals(CommandWord.DELETE, CommandWord.fromInput("delete 3"));
        assertEquals(CommandWord.FIND, CommandWord.fromInput("find book"));
    }

    /**
     * A command that expects an argument is still recognized without one, which
     * is what lets the parser answer a bare {@code mark} with a hint about the
     * missing number instead of the far less helpful "I don't know what that
     * means".
     */
    @Test
    public void fromInput_commandExpectingArgumentGivenNone_returnsCommandWord()
            throws PiplupBotException {
        assertEquals(CommandWord.MARK, CommandWord.fromInput("mark"));
        assertEquals(CommandWord.TODO, CommandWord.fromInput("todo"));
        assertEquals(CommandWord.DELETE, CommandWord.fromInput("delete"));
        assertEquals(CommandWord.FIND, CommandWord.fromInput("find"));
    }

    /**
     * The other half of the rule: a command that takes no argument matches the
     * keyword alone, so trailing words make the line unknown rather than being
     * quietly ignored. Silently accepting {@code bye now} would hide a typo the
     * user would want to know about.
     */
    @Test
    public void fromInput_commandTakingNoArgumentGivenOne_exceptionThrown() {
        assertThrows(PiplupBotException.class, () -> CommandWord.fromInput("list now"));
        assertThrows(PiplupBotException.class, () -> CommandWord.fromInput("bye now"));
    }

    /**
     * A keyword must be the whole word, not the start of a longer one, so the
     * space in the matching rule is doing real work. Without it, {@code todos
     * are due} would be read as a todo named "s are due".
     */
    @Test
    public void fromInput_keywordAsPrefixOfLongerWord_exceptionThrown() {
        assertThrows(PiplupBotException.class, () -> CommandWord.fromInput("todos"));
        assertThrows(PiplupBotException.class, () -> CommandWord.fromInput("listing"));
        assertThrows(PiplupBotException.class, () -> CommandWord.fromInput("byebye"));
        assertThrows(PiplupBotException.class, () -> CommandWord.fromInput("marked 2"));
    }

    /** Keywords are matched exactly, so capitals are not recognized. */
    @Test
    public void fromInput_keywordInCapitals_exceptionThrown() {
        assertThrows(PiplupBotException.class, () -> CommandWord.fromInput("List"));
        assertThrows(PiplupBotException.class, () -> CommandWord.fromInput("TODO read book"));
    }

    @Test
    public void fromInput_wordThatNamesNoCommand_exceptionThrown() {
        assertThrows(PiplupBotException.class, () -> CommandWord.fromInput("blah"));
    }

    /**
     * The refusal has to leave the user able to try again, so it quotes what
     * they typed and lists every keyword. Checking the list in full also checks
     * the grammar that builds it -- the commas, and the "or" before the last
     * one.
     */
    @Test
    public void fromInput_unknownWord_messageQuotesInputAndListsEveryKeyword() {
        PiplupBotException exception =
                assertThrows(PiplupBotException.class, () -> CommandWord.fromInput("blah"));

        String[] lines = exception.getMessageLines();
        assertEquals(2, lines.length);
        assertEquals("Pip... I don't know what \"blah\" means.", lines[0]);
        assertEquals(
                "Try: todo, deadline, event, list, find, sort, mark, unmark, delete, or bye.",
                lines[1]);
    }

    // ---------- Reading what follows the keyword ----------

    @Test
    public void extractArgument_keywordWithArgument_returnsRestOfLine() {
        assertEquals("read book", CommandWord.TODO.extractArgument("todo read book"));
        assertEquals("2", CommandWord.MARK.extractArgument("mark 2"));
    }

    @Test
    public void extractArgument_extraSpacesAroundArgument_returnsTrimmedArgument() {
        assertEquals("read book", CommandWord.TODO.extractArgument("todo    read book   "));
    }

    /**
     * A bare keyword leaves nothing after it. Returning empty rather than
     * failing is what lets each command decide for itself whether a missing
     * argument is a problem: {@code todo} explains it needs a description, while
     * {@code mark} explains it needs a number.
     */
    @Test
    public void extractArgument_keywordAlone_returnsEmptyText() {
        assertEquals("", CommandWord.MARK.extractArgument("mark"));
        assertEquals("", CommandWord.TODO.extractArgument("todo"));
    }

    /**
     * Only the keyword at the front is removed. An implementation that searched
     * for the word instead of skipping its length would damage a description
     * that happens to contain it.
     */
    @Test
    public void extractArgument_argumentRepeatingTheKeyword_removesOnlyTheFirst() {
        assertEquals("todo list", CommandWord.TODO.extractArgument("todo todo list"));
    }

    // ---------- The keywords themselves ----------

    /**
     * Every keyword is distinct and lower case. Two constants sharing a keyword
     * would make the second unreachable, since the first to match wins -- the
     * kind of mistake a copy-and-pasted constant makes easy and nothing else
     * would report.
     */
    @Test
    public void getKeyword_everyCommandWord_isLowerCaseAndUnique() {
        Set<String> seenKeywords = new HashSet<>();
        for (CommandWord commandWord : CommandWord.values()) {
            String keyword = commandWord.getKeyword();
            // Locale.ROOT rather than the machine's own language, for the same
            // reason DateTimes pins Locale.ENGLISH: a Turkish machine lower-cases
            // "I" to a dotless letter, which would fail this test for no real fault.
            assertEquals(keyword.toLowerCase(Locale.ROOT), keyword,
                    "Keywords are matched exactly, so they must be lower case");
            assertTrue(seenKeywords.add(keyword), "Two command words share the keyword " + keyword);
        }
    }
}
