package hermes.parser;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class KeywordTest {

    @Test
    public void of_eachCommandWord_returnsItsKeyword() {
        assertEquals(Keyword.BYE, Keyword.of("bye"));
        assertEquals(Keyword.LIST, Keyword.of("list"));
        assertEquals(Keyword.MARK, Keyword.of("mark"));
        assertEquals(Keyword.UNMARK, Keyword.of("unmark"));
        assertEquals(Keyword.DELETE, Keyword.of("delete"));
        assertEquals(Keyword.TODO, Keyword.of("todo"));
        assertEquals(Keyword.DEADLINE, Keyword.of("deadline"));
        assertEquals(Keyword.EVENT, Keyword.of("event"));
        assertEquals(Keyword.DUE, Keyword.of("due"));
        assertEquals(Keyword.SORT, Keyword.of("sort"));
        assertEquals(Keyword.FIND, Keyword.of("find"));
        assertEquals(Keyword.UNDO, Keyword.of("undo"));
    }

    @Test
    public void of_unrecognisedWord_returnsUnknown() {
        assertEquals(Keyword.UNKNOWN, Keyword.of("dance"));
        assertEquals(Keyword.UNKNOWN, Keyword.of(""));
        // Command words are matched exactly, so case matters.
        assertEquals(Keyword.UNKNOWN, Keyword.of("BYE"));
    }

    @Test
    public void getExample_everyKeyword_isACommandThatParses() {
        // Error messages quote these examples, so each must be something the user could type.
        Parser parser = new Parser();
        for (Keyword keyword : Keyword.values()) {
            assertDoesNotThrow(() -> parser.parse(keyword.getExample()), keyword.name());
        }
    }
}
