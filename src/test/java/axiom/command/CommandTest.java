package axiom.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class CommandTest {

    @Test
    void fromInput_exactKeyword_returnsCommand() {
        assertEquals(Command.BYE, Command.fromInput("bye"));
        assertEquals(Command.LIST, Command.fromInput("list"));
        assertEquals(Command.SORT, Command.fromInput("sort"));
        assertEquals(Command.FIND, Command.fromInput("find"));
        assertEquals(Command.MARK, Command.fromInput("mark"));
        assertEquals(Command.UNMARK, Command.fromInput("unmark"));
        assertEquals(Command.DELETE, Command.fromInput("delete"));
        assertEquals(Command.TODO, Command.fromInput("todo"));
        assertEquals(Command.DEADLINE, Command.fromInput("deadline"));
        assertEquals(Command.EVENT, Command.fromInput("event"));
    }

    @Test
    void fromInput_keywordWithArgument_returnsCommand() {
        assertEquals(Command.TODO, Command.fromInput("todo read book"));
        assertEquals(Command.MARK, Command.fromInput("mark 1"));
        assertEquals(Command.DEADLINE, Command.fromInput("deadline homework /by 2019-06-06"));
    }

    @Test
    void fromInput_leadingAndTrailingSpaces_returnsCommand() {
        assertEquals(Command.LIST, Command.fromInput("  list  "));
        assertEquals(Command.TODO, Command.fromInput("\ttodo read book"));
    }

    @Test
    void fromInput_unknownInput_returnsUnknown() {
        assertEquals(Command.UNKNOWN, Command.fromInput("blah"));
        assertEquals(Command.UNKNOWN, Command.fromInput(""));
        assertEquals(Command.UNKNOWN, Command.fromInput("tod"));
    }

    @Test
    void tokens_repeatedWhitespace_collapsesToSingleSeparators() {
        assertEquals(3, Command.tokens("  todo   read   book ").length);
        assertEquals("todo", Command.tokens("  todo   read   book ")[0]);
        assertEquals("book", Command.tokens("  todo   read   book ")[2]);
    }

    @Test
    void fromInput_partialKeywordMatch_returnsUnknown() {
        assertEquals(Command.UNKNOWN, Command.fromInput("byee"));
        assertEquals(Command.UNKNOWN, Command.fromInput("listing"));
    }

    @Test
    void matches_exactKeywordOrWithArgument_returnsTrue() {
        assertTrue(Command.TODO.matches("todo"));
        assertTrue(Command.TODO.matches("todo read book"));
        assertTrue(Command.MARK.matches("mark 1"));
    }

    @Test
    void matches_unrelatedInput_returnsFalse() {
        assertFalse(Command.TODO.matches("deadline homework"));
        assertFalse(Command.TODO.matches("tod"));
        assertFalse(Command.BYE.matches("byee"));
    }

    @Test
    void getArgument_keywordOnly_returnsEmptyString() {
        assertEquals("", Command.TODO.getArgument("todo"));
        assertEquals("", Command.MARK.getArgument("mark"));
    }

    @Test
    void getArgument_keywordWithArgument_returnsTrimmedArgument() {
        assertEquals("read book", Command.TODO.getArgument("todo read book"));
        assertEquals("1", Command.MARK.getArgument("mark 1"));
        assertEquals("return book", Command.TODO.getArgument("todo   return book"));
    }

    @Test
    void getKeyword_returnsKeyword() {
        assertEquals("todo", Command.TODO.getKeyword());
        assertEquals("delete", Command.DELETE.getKeyword());
    }
}
