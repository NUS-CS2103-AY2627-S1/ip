package alzara.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Tests for {@link CommandType#from(String)}. Each recognised command word
 * gets one bare-word test to confirm its branch classifies correctly; the
 * shared prefix-matching mechanism (used identically by mark/unmark/todo/
 * deadline/event/delete) and the space-boundary check are each tested once
 * rather than once per command, since repeating them would just be testing
 * the same mechanism over and over.
 */
class CommandTypeTest {

    // --- bare-word mapping, one per recognised command ---

    @Test
    void from_bye_returnsBye() {
        assertEquals(CommandType.BYE, CommandType.from("bye"));
    }

    @Test
    void from_list_returnsList() {
        assertEquals(CommandType.LIST, CommandType.from("list"));
    }

    @Test
    void from_mark_returnsMark() {
        assertEquals(CommandType.MARK, CommandType.from("mark"));
    }

    @Test
    void from_unmark_returnsUnmark() {
        assertEquals(CommandType.UNMARK, CommandType.from("unmark"));
    }

    @Test
    void from_todo_returnsTodo() {
        assertEquals(CommandType.TODO, CommandType.from("todo"));
    }

    @Test
    void from_deadline_returnsDeadline() {
        assertEquals(CommandType.DEADLINE, CommandType.from("deadline"));
    }

    @Test
    void from_event_returnsEvent() {
        assertEquals(CommandType.EVENT, CommandType.from("event"));
    }

    @Test
    void from_delete_returnsDelete() {
        assertEquals(CommandType.DELETE, CommandType.from("delete"));
    }

    @Test
    void from_find_returnsFind() {
        assertEquals(CommandType.FIND, CommandType.from("find"));
    }

    @Test
    void from_view_returnsView() {
        assertEquals(CommandType.VIEW, CommandType.from("view"));
    }

    // --- shared prefix-matching mechanism, tested once ---

    // mark/unmark/todo/deadline/event/delete all match "<word> " followed by
    // an argument via the same equals(x) || startsWith(x + " ") pattern -
    // todo is just the representative example.
    @Test
    void from_commandWithTrailingArgument_returnsMatchingType() {
        assertEquals(CommandType.TODO, CommandType.from("todo read book"));
    }

    // --- space-boundary edge case ---

    // startsWith("mark ") requires the space; a word that merely *starts with*
    // the command name shouldn't match it.
    @Test
    void from_wordStartingWithCommandNameButNoSpace_returnsUnknown() {
        assertEquals(CommandType.UNKNOWN, CommandType.from("marking"));
    }

    // --- case-insensitivity ---

    // Every command word, not just "bye", is matched case-insensitively.
    @Test
    void from_byeDifferentCase_returnsBye() {
        assertEquals(CommandType.BYE, CommandType.from("BYE"));
    }

    @Test
    void from_listDifferentCase_returnsList() {
        assertEquals(CommandType.LIST, CommandType.from("LIST"));
    }

    @Test
    void from_commandWithTrailingArgumentDifferentCase_returnsMatchingType() {
        assertEquals(CommandType.TODO, CommandType.from("TODO read book"));
    }

    // --- list accepts no trailing argument at all ---

    // Unlike the other six commands, list's check is only equals("list"),
    // with no startsWith variant.
    @Test
    void from_listWithTrailingText_returnsUnknown() {
        assertEquals(CommandType.UNKNOWN, CommandType.from("list all"));
    }

    // --- fallback ---

    @Test
    void from_unrecognisedText_returnsUnknown() {
        assertEquals(CommandType.UNKNOWN, CommandType.from("/list"));
    }

    @Test
    void from_emptyString_returnsUnknown() {
        assertEquals(CommandType.UNKNOWN, CommandType.from(""));
    }
}
