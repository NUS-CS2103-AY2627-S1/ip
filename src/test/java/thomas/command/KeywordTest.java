package thomas.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import thomas.ThomasException;

/**
 * Tests Keyword.of, the one place that decides whether a typed word is a
 * command.
 * Every constant is looked up, because the word a user types is held as its own
 * field rather than derived from the constant's name: a constant renamed, or a
 * keyword misspelled in its own declaration, would leave that command
 * unreachable from the keyboard with nothing failing to compile.
 * The refusals pin two decisions: that matching is case sensitive, and that the
 * message names no command in particular, since an unknown word gives nothing to
 * name.
 */
public class KeywordTest {

    private static final String MESSAGE_UNKNOWN = "Cinders and ashes! I don't know that signal. What does it mean?";

    // ---- every keyword is reachable ----

    @Test
    public void of_bye_returnsBye() throws ThomasException {
        assertEquals(Keyword.BYE, Keyword.of("bye"));
    }

    @Test
    public void of_list_returnsList() throws ThomasException {
        assertEquals(Keyword.LIST, Keyword.of("list"));
    }

    @Test
    public void of_on_returnsOn() throws ThomasException {
        assertEquals(Keyword.ON, Keyword.of("on"));
    }

    @Test
    public void of_find_returnsFind() throws ThomasException {
        assertEquals(Keyword.FIND, Keyword.of("find"));
    }

    @Test
    public void of_mark_returnsMark() throws ThomasException {
        assertEquals(Keyword.MARK, Keyword.of("mark"));
    }

    @Test
    public void of_unmark_returnsUnmark() throws ThomasException {
        // "unmark" contains "mark", so a prefix or substring match would give
        // the wrong constant here. The match is on the whole word.
        assertEquals(Keyword.UNMARK, Keyword.of("unmark"));
    }

    @Test
    public void of_delete_returnsDelete() throws ThomasException {
        assertEquals(Keyword.DELETE, Keyword.of("delete"));
    }

    @Test
    public void of_undo_returnsUndo() throws ThomasException {
        assertEquals(Keyword.UNDO, Keyword.of("undo"));
    }

    @Test
    public void of_todo_returnsTodo() throws ThomasException {
        assertEquals(Keyword.TODO, Keyword.of("todo"));
    }

    @Test
    public void of_deadline_returnsDeadline() throws ThomasException {
        assertEquals(Keyword.DEADLINE, Keyword.of("deadline"));
    }

    @Test
    public void of_event_returnsEvent() throws ThomasException {
        assertEquals(Keyword.EVENT, Keyword.of("event"));
    }

    @Test
    public void of_everyConstant_isReachableByItsOwnWord() throws ThomasException {
        // A guard for a keyword added later without a case above: each constant's
        // word is its name in lower case today, and this fails if one drifts
        // without the case for it being written.
        for (Keyword keyword : Keyword.values()) {
            assertEquals(keyword, Keyword.of(keyword.name().toLowerCase()));
        }
    }

    // ---- words that are not keywords ----

    @Test
    public void of_unknownWord_exceptionThrown() {
        ThomasException e = assertThrows(ThomasException.class, () -> Keyword.of("blah"));
        assertEquals(MESSAGE_UNKNOWN, e.getMessage());
    }

    @Test
    public void of_upperCaseKeyword_exceptionThrown() {
        // Case sensitive, as noted in Keyword.of: loosening this is a change in
        // behavior, and this case is what would notice it.
        ThomasException e = assertThrows(ThomasException.class, () -> Keyword.of("LIST"));
        assertEquals(MESSAGE_UNKNOWN, e.getMessage());
    }

    @Test
    public void of_capitalizedKeyword_exceptionThrown() {
        assertThrows(ThomasException.class, () -> Keyword.of("Bye"));
    }

    @Test
    public void of_keywordWithTrailingSpace_exceptionThrown() {
        // The parser tidies spacing before asking, so a space arriving here is
        // part of the word and the word is not a keyword.
        assertThrows(ThomasException.class, () -> Keyword.of("list "));
    }

    @Test
    public void of_prefixOfAKeyword_exceptionThrown() {
        assertThrows(ThomasException.class, () -> Keyword.of("dead"));
    }

    @Test
    public void of_keywordWithMoreLetters_exceptionThrown() {
        assertThrows(ThomasException.class, () -> Keyword.of("lists"));
    }

    @Test
    public void of_emptyWord_exceptionThrown() {
        assertThrows(ThomasException.class, () -> Keyword.of(""));
    }
}
