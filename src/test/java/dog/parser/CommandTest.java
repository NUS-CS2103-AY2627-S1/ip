package dog.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Map.Entry;

import org.junit.jupiter.api.Test;

public class CommandTest {

    // =========================================================================
    // Valid alias recognition
    // =========================================================================

    @Test
    public void fromInput_listCommand_returnsListCommand() {
        Entry<Command, String> result;

        // alias "list"
        result = Command.fromInput("list");
        assertNotNull(result);
        assertEquals(Command.LIST, result.getKey());
        assertEquals("", result.getValue());

        // alias "l"
        result = Command.fromInput("l");
        assertNotNull(result);
        assertEquals(Command.LIST, result.getKey());
        assertEquals("", result.getValue());

        // alias "ls"
        result = Command.fromInput("ls");
        assertNotNull(result);
        assertEquals(Command.LIST, result.getKey());
        assertEquals("", result.getValue());
    }

    @Test
    public void fromInput_markCommand_returnsMarkCommand() {
        Entry<Command, String> result;

        // alias "mark"
        result = Command.fromInput("mark 0");
        assertNotNull(result);
        assertEquals(Command.MARK, result.getKey());
        assertEquals("0", result.getValue());

        // alias "m"
        result = Command.fromInput("m 1");
        assertNotNull(result);
        assertEquals(Command.MARK, result.getKey());
        assertEquals("1", result.getValue());

        // alias "done"
        result = Command.fromInput("done 2");
        assertNotNull(result);
        assertEquals(Command.MARK, result.getKey());
        assertEquals("2", result.getValue());

        // alias "complete"
        result = Command.fromInput("complete 3");
        assertNotNull(result);
        assertEquals(Command.MARK, result.getKey());
        assertEquals("3", result.getValue());
    }

    @Test
    public void fromInput_deleteCommand_returnsDeleteCommand() {
        Entry<Command, String> result;

        // alias "delete"
        result = Command.fromInput("delete 0");
        assertNotNull(result);
        assertEquals(Command.DELETE, result.getKey());
        assertEquals("0", result.getValue());

        // alias "del"
        result = Command.fromInput("del 1");
        assertNotNull(result);
        assertEquals(Command.DELETE, result.getKey());
        assertEquals("1", result.getValue());

        // alias "remove"
        result = Command.fromInput("remove 2");
        assertNotNull(result);
        assertEquals(Command.DELETE, result.getKey());
        assertEquals("2", result.getValue());

        // alias "rm"
        result = Command.fromInput("rm 3");
        assertNotNull(result);
        assertEquals(Command.DELETE, result.getKey());
        assertEquals("3", result.getValue());
    }

    @Test
    public void fromInput_todoCommand_returnsTodoCommand() {
        Entry<Command, String> result;

        // alias "todo"
        result = Command.fromInput("todo cry");
        assertNotNull(result);
        assertEquals(Command.TODO, result.getKey());
        assertEquals("cry", result.getValue());

        // alias "t"
        result = Command.fromInput("t cry more");
        assertNotNull(result);
        assertEquals(Command.TODO, result.getKey());
        assertEquals("cry more", result.getValue());

        // alias "task"
        result = Command.fromInput("task cry harder");
        assertNotNull(result);
        assertEquals(Command.TODO, result.getKey());
        assertEquals("cry harder", result.getValue());

        // alias "add"
        result = Command.fromInput("add cry to sleep");
        assertNotNull(result);
        assertEquals(Command.TODO, result.getKey());
        assertEquals("cry to sleep", result.getValue());
    }

    @Test
    public void fromInput_deadlineCommand_returnsDeadlineCommand() {
        Entry<Command, String> result;

        // alias "deadline"
        result = Command.fromInput("deadline iP submission /by 2026-09-18");
        assertNotNull(result);
        assertEquals(Command.DEADLINE, result.getKey());
        assertEquals("iP submission /by 2026-09-18", result.getValue());

        // alias "d"
        result = Command.fromInput("d iP submission /by 2026-09-18");
        assertNotNull(result);
        assertEquals(Command.DEADLINE, result.getKey());
        assertEquals("iP submission /by 2026-09-18", result.getValue());
    }

    @Test
    public void fromInput_eventCommand_returnsEventCommand() {
        Entry<Command, String> result;

        // alias "event"
        result = Command.fromInput("event tutorial /from 2026-09-18 /to 2026-09-18");
        assertNotNull(result);
        assertEquals(Command.EVENT, result.getKey());
        assertEquals("tutorial /from 2026-09-18 /to 2026-09-18", result.getValue());

        // alias "e"
        result = Command.fromInput("e tutorial /from 2026-09-18 /to 2026-09-18");
        assertNotNull(result);
        assertEquals(Command.EVENT, result.getKey());
        assertEquals("tutorial /from 2026-09-18 /to 2026-09-18", result.getValue());
    }

    @Test
    public void fromInput_findCommand_returnsFindCommand() {
        Entry<Command, String> result;

        // alias "find"
        result = Command.fromInput("find out");
        assertNotNull(result);
        assertEquals(Command.FIND, result.getKey());
        assertEquals("out", result.getValue());

        // alias "f"
        result = Command.fromInput("f around");
        assertNotNull(result);
        assertEquals(Command.FIND, result.getKey());
        assertEquals("around", result.getValue());

        // alias "search"
        result = Command.fromInput("search cry");
        assertNotNull(result);
        assertEquals(Command.FIND, result.getKey());
        assertEquals("cry", result.getValue());
    }

    @Test
    public void fromInput_byeCommand_returnsByeCommand() {
        Entry<Command, String> result;

        // alias "bye"
        result = Command.fromInput("bye");
        assertNotNull(result);
        assertEquals(Command.BYE, result.getKey());
        assertEquals("", result.getValue());

        // alias "exit"
        result = Command.fromInput("exit");
        assertNotNull(result);
        assertEquals(Command.BYE, result.getKey());
        assertEquals("", result.getValue());

        // alias "q"
        result = Command.fromInput("q");
        assertNotNull(result);
        assertEquals(Command.BYE, result.getKey());
        assertEquals("", result.getValue());
    }

    @Test
    public void fromInput_woofCommand_returnsWoofCommand() {
        Entry<Command, String> result;

        // alias "woof"
        result = Command.fromInput("woof");
        assertNotNull(result);
        assertEquals(Command.WOOF, result.getKey());
        assertEquals("", result.getValue());

        // alias "arf"
        result = Command.fromInput("arf");
        assertNotNull(result);
        assertEquals(Command.WOOF, result.getKey());
        assertEquals("", result.getValue());

        // alias "bark"
        result = Command.fromInput("bark");
        assertNotNull(result);
        assertEquals(Command.WOOF, result.getKey());
        assertEquals("", result.getValue());
    }

    // =========================================================================
    // Case insensitivity
    // =========================================================================

    @Test
    public void fromInput_upperCaseInput_returnsCorrectCommand() {
        Entry<Command, String> result = Command.fromInput("LIST");
        assertNotNull(result);
        assertEquals(Command.LIST, result.getKey());

        result = Command.fromInput("TODO SCREAM");
        assertNotNull(result);
        assertEquals(Command.TODO, result.getKey());
        assertEquals("SCREAM", result.getValue());
    }

    @Test
    public void fromInput_mixedCaseInput_returnsCorrectCommand() {
        Entry<Command, String> result;

        result = Command.fromInput("DeLeTe 4");
        assertNotNull(result);
        assertEquals(Command.DELETE, result.getKey());

        result = Command.fromInput("MaRk 5");
        assertNotNull(result);
        assertEquals(Command.MARK, result.getKey());

        result = Command.fromInput("tODo CRYyyy");
        assertNotNull(result);
        assertEquals(Command.TODO, result.getKey());
        assertEquals("CRYyyy", result.getValue());
    }

    // =========================================================================
    // Suffix extraction
    // =========================================================================

    @Test
    public void fromInput_suffixWithSingleSuffix_preservesSuffix() {
        Entry<Command, String> result = Command.fromInput("todo buy milk");
        assertNotNull(result);
        assertEquals(Command.TODO, result.getKey());
        assertEquals("buy milk", result.getValue());
    }

    @Test
    public void fromInput_suffixContainingPipes_preservesSuffix() {
        Entry<Command, String> result = Command.fromInput("find a | b | c");
        assertNotNull(result);
        assertEquals(Command.FIND, result.getKey());
        assertEquals("a | b | c", result.getValue());
    }

    @Test
    public void fromInput_suffixWithLeadingSpace_preservesSuffix() {
        Entry<Command, String> result = Command.fromInput("todo     do homework");
        assertNotNull(result);
        assertEquals(Command.TODO, result.getKey());
        assertEquals("    do homework", result.getValue());
    }

    @Test
    public void fromInput_suffixWithCommandName_returnsCorrectCommand() {
        Entry<Command, String> result;

        result = Command.fromInput("todo list");
        assertNotNull(result);
        assertEquals(Command.TODO, result.getKey());
        assertEquals("list", result.getValue());

        result = Command.fromInput("find bye");
        assertNotNull(result);
        assertEquals(Command.FIND, result.getKey());
        assertEquals("bye", result.getValue());

        result = Command.fromInput("todo bark");
        assertNotNull(result);
        assertEquals(Command.TODO, result.getKey());
        assertEquals("bark", result.getValue());

        result = Command.fromInput("search t d e del woof woof bye exit q ls mark");
        assertNotNull(result);
        assertEquals(Command.FIND, result.getKey());
        assertEquals("t d e del woof woof bye exit q ls mark", result.getValue());
    }

    // =========================================================================
    // No-match cases
    // =========================================================================

    @Test
    public void fromInput_whitespacesOnly_returnsNull() {
        Entry<Command, String> result;

        result = Command.fromInput("");
        assertNull(result);
    }

    @Test
    public void fromInput_unknownCommand_returnsNull() {
        Entry<Command, String> result;

        result = Command.fromInput("help");
        assertNull(result);

        result = Command.fromInput("good doggy");
        assertNull(result);
    }

    @Test
    public void fromInput_partialAlias_returnsNull() {
        Entry<Command, String> result;

        result = Command.fromInput("lis");
        assertNull(result);

        result = Command.fromInput("listt");
        assertNull(result);

        result = Command.fromInput("dele");
        assertNull(result);

        result = Command.fromInput("deleteee");
        assertNull(result);
    }
}
