package wodan.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import wodan.WodanException;

/**
 * Unit tests for {@link CommandWord} parsing.
 */
public class CommandWordTest {
    @Test
    public void parse_mixedCaseKnownWords_matchingConstants() throws WodanException {
        assertEquals(CommandWord.TODO, CommandWord.parse("todo"));
        assertEquals(CommandWord.DEADLINE, CommandWord.parse("Deadline"));
        assertEquals(CommandWord.EVENT, CommandWord.parse("EVENT"));
        assertEquals(CommandWord.LIST, CommandWord.parse("list"));
        assertEquals(CommandWord.MARK, CommandWord.parse("mark"));
        assertEquals(CommandWord.UNMARK, CommandWord.parse("unmark"));
        assertEquals(CommandWord.DELETE, CommandWord.parse("delete"));
        assertEquals(CommandWord.ON, CommandWord.parse("on"));
        assertEquals(CommandWord.FIND, CommandWord.parse("find"));
        assertEquals(CommandWord.TAG, CommandWord.parse("tag"));
        assertEquals(CommandWord.BYE, CommandWord.parse("Bye"));
    }

    @Test
    public void parse_unknownWord_exceptionThrown() {
        WodanException ex = assertThrows(WodanException.class, () -> CommandWord.parse("blah"));
        assertEquals(
                "That rune is unknown. Speak todo, deadline, event, list, mark, "
                        + "unmark, delete, on, find, tag, or bye.",
                ex.getMessage());
    }
}
