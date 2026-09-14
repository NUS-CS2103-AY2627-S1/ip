package nova.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import nova.NovaException;

public class CommandTypeTest {

    @Test
    public void fromInput_exactKeyword_returnsThatType() throws NovaException {
        assertEquals(CommandType.BYE, CommandType.fromInput("bye"));
        assertEquals(CommandType.LIST, CommandType.fromInput("list"));
        assertEquals(CommandType.ON, CommandType.fromInput("on"));
    }

    @Test
    public void fromInput_keywordFollowedByArguments_returnsThatType() throws NovaException {
        assertEquals(CommandType.TODO, CommandType.fromInput("todo read book"));
        assertEquals(CommandType.DEADLINE, CommandType.fromInput("deadline x /by 2019-12-02"));
        assertEquals(CommandType.UNMARK, CommandType.fromInput("unmark 2"));
    }

    @Test
    public void fromInput_keywordRunIntoTheArgument_exceptionThrown() {
        assertThrows(NovaException.class, () -> CommandType.fromInput("todoread book"));
        assertThrows(NovaException.class, () -> CommandType.fromInput("marker 1"));
    }

    @Test
    public void fromInput_unknownWord_exceptionThrown() {
        NovaException e = assertThrows(NovaException.class, () -> CommandType.fromInput("blah"));
        assertEquals("I don't know that command. Type help to see what I understand.", e.getMessage());
    }

    @Test
    public void fromInput_emptyInput_exceptionThrown() {
        assertThrows(NovaException.class, () -> CommandType.fromInput(""));
    }

    @Test
    public void argumentsIn_keywordWithArguments_returnsTrimmedRemainder() {
        assertEquals("read book", CommandType.TODO.argumentsIn("todo read book"));
        assertEquals("2", CommandType.MARK.argumentsIn("mark   2  "));
    }

    @Test
    public void argumentsIn_keywordAlone_returnsEmptyString() {
        assertEquals("", CommandType.TODO.argumentsIn("todo"));
    }
}
