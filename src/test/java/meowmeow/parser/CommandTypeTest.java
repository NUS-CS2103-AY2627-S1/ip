package meowmeow.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import meowmeow.MeowmeowException;

/**
 * Tests for {@link CommandType} - the enum that recognises <em>which</em>
 * command an input line invokes and strips that command's keyword off the
 * front.
 *
 * <p>Worth testing because matching is subtle: it is case-insensitive, a
 * bare keyword must match but a keyword run together with more letters
 * (e.g. "todos") must not, and {@link CommandType#argumentsOf(String)} has
 * to trim and handle the "keyword only" case.
 */
public class CommandTypeTest {

    // ---- fromInput: which command does this line invoke? ----

    @Test
    public void fromInput_keywordWithArguments_returnsThatCommand() throws MeowmeowException {
        assertEquals(CommandType.TODO, CommandType.fromInput("todo read book"));
        assertEquals(CommandType.DEADLINE, CommandType.fromInput("deadline x /by y"));
        assertEquals(CommandType.MARK, CommandType.fromInput("mark 2"));
    }

    @Test
    public void fromInput_bareKeyword_returnsThatCommand() throws MeowmeowException {
        assertEquals(CommandType.LIST, CommandType.fromInput("list"));
        assertEquals(CommandType.BYE, CommandType.fromInput("bye"));
    }

    @Test
    public void fromInput_differentCasing_stillMatches() throws MeowmeowException {
        assertEquals(CommandType.TODO, CommandType.fromInput("TODO read book"));
        assertEquals(CommandType.MARK, CommandType.fromInput("MaRk 2"));
        assertEquals(CommandType.BYE, CommandType.fromInput("Bye"));
    }

    @Test
    public void fromInput_find_returnsFindCommand() throws MeowmeowException {
        assertEquals(CommandType.FIND, CommandType.fromInput("find book"));
        assertEquals(CommandType.FIND, CommandType.fromInput("FIND book"));
    }

    @Test
    public void fromInput_unmark_notConfusedWithMark() throws MeowmeowException {
        // "mark" is a substring of "unmark" but not a prefix, so "unmark 1"
        // must resolve to UNMARK, not MARK.
        assertEquals(CommandType.UNMARK, CommandType.fromInput("unmark 1"));
    }

    @Test
    public void fromInput_priority_returnsPriorityCommand() throws MeowmeowException {
        assertEquals(CommandType.PRIORITY, CommandType.fromInput("priority 2 high"));
    }

    @Test
    public void fromInput_unknownWord_helpTextListsPriority() {
        // helpText() is built from the enum's own values, so a new command
        // word (like PRIORITY) must appear in it without any extra upkeep.
        MeowmeowException exception = assertThrows(MeowmeowException.class, () ->
                CommandType.fromInput("dance"));
        assertTrue(exception.getMessage().contains("priority"));
    }

    @Test
    public void fromInput_keywordRunTogetherWithText_exceptionThrown() {
        // "todos" is not "todo" followed by a space, so it is not the todo command.
        assertThrows(MeowmeowException.class, () -> CommandType.fromInput("todos"));
    }

    @Test
    public void fromInput_unknownWord_exceptionThrown() {
        assertThrows(MeowmeowException.class, () -> CommandType.fromInput("dance"));
    }

    @Test
    public void fromInput_emptyString_exceptionThrown() {
        assertThrows(MeowmeowException.class, () -> CommandType.fromInput(""));
    }

    // ---- argumentsOf: everything after the keyword, trimmed ----

    @Test
    public void argumentsOf_textAfterKeyword_returnedTrimmed() {
        assertEquals("read book", CommandType.TODO.argumentsOf("todo read book"));
        assertEquals("2", CommandType.MARK.argumentsOf("mark   2  "));
    }

    @Test
    public void argumentsOf_bareKeyword_returnsEmptyString() {
        assertEquals("", CommandType.LIST.argumentsOf("list"));
        assertEquals("", CommandType.MARK.argumentsOf("mark"));
    }

    @Test
    public void argumentsOf_keywordThenOnlySpaces_returnsEmptyString() {
        assertEquals("", CommandType.TODO.argumentsOf("todo    "));
    }

    @Test
    public void argumentsOf_findKeyword_returnsKeywordTrimmed() {
        assertEquals("book", CommandType.FIND.argumentsOf("find  book "));
    }

    // ---- keyword: the word a user types ----

    @Test
    public void keyword_returnsTheInvokingWord() {
        assertEquals("todo", CommandType.TODO.keyword());
        assertEquals("unmark", CommandType.UNMARK.keyword());
        assertEquals("find", CommandType.FIND.keyword());
        assertEquals("priority", CommandType.PRIORITY.keyword());
    }
}
