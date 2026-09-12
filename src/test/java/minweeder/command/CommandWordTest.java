package minweeder.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import minweeder.exception.MinweederException;

public class CommandWordTest {

    @Test
    public void getCommandWord_lowercaseWord_returnsMatchingEnum() throws MinweederException {
        assertEquals(CommandWord.TODO, CommandWord.getCommandWord("todo"));
    }

    @Test
    public void getCommandWord_mixedCaseWord_isCaseInsensitive() throws MinweederException {
        assertEquals(CommandWord.DEADLINE, CommandWord.getCommandWord("DeAdLiNe"));
    }

    @Test
    public void getCommandWord_unknownWord_throwsException() {
        assertThrows(MinweederException.class, () -> CommandWord.getCommandWord("frobnicate"));
    }

    @Test
    public void getCommandWord_findWord_returnsMatchingEnum() throws MinweederException {
        assertEquals(CommandWord.FIND, CommandWord.getCommandWord("find"));
    }

    @Test
    public void getCommandWord_loanWord_returnsMatchingEnum() throws MinweederException {
        assertEquals(CommandWord.LOAN, CommandWord.getCommandWord("loan"));
    }
}
