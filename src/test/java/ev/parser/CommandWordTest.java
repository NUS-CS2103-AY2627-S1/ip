package ev.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ev.EvException;

public class CommandWordTest {

    @Test
    public void fromKeyword_everyKeyword_matchingWordReturned() throws EvException {
        assertEquals(CommandWord.TODO, CommandWord.fromKeyword("todo"));
        assertEquals(CommandWord.DEADLINE, CommandWord.fromKeyword("deadline"));
        assertEquals(CommandWord.EVENT, CommandWord.fromKeyword("event"));
        assertEquals(CommandWord.LIST, CommandWord.fromKeyword("list"));
        assertEquals(CommandWord.ON, CommandWord.fromKeyword("on"));
        assertEquals(CommandWord.FIND, CommandWord.fromKeyword("find"));
        assertEquals(CommandWord.MARK, CommandWord.fromKeyword("mark"));
        assertEquals(CommandWord.UNMARK, CommandWord.fromKeyword("unmark"));
        assertEquals(CommandWord.DELETE, CommandWord.fromKeyword("delete"));
        assertEquals(CommandWord.UPDATE, CommandWord.fromKeyword("update"));
        assertEquals(CommandWord.BYE, CommandWord.fromKeyword("bye"));
    }

    @Test
    public void fromKeyword_unknownWord_exceptionNamesItAndTheAlternatives() {
        EvException thrown = assertThrows(EvException.class, () -> CommandWord.fromKeyword("blah"));
        assertTrue(thrown.getMessage().contains("blah"));
        assertTrue(thrown.getMessage().contains(CommandWord.listKeywords()));
    }

    @Test
    public void fromKeyword_wrongCase_exceptionThrown() {
        assertThrows(EvException.class, () -> CommandWord.fromKeyword("Todo"));
        assertThrows(EvException.class, () -> CommandWord.fromKeyword("BYE"));
    }

    @Test
    public void fromKeyword_empty_exceptionThrown() {
        assertThrows(EvException.class, () -> CommandWord.fromKeyword(""));
    }

    @Test
    public void listKeywords_everyWord_listedInOrderWithoutTrailingSeparator() {
        String keywords = CommandWord.listKeywords();
        assertEquals("todo, deadline, event, list, on, find, mark, unmark, delete, update, bye", keywords);
    }
}
