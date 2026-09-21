package chris;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/** Tests command argument parsing. */
public class ParserTest {
    private final Parser parser = new Parser();

    @Test
    public void parseTaskIndex_validNumber_returnsZeroBasedIndex() throws ChrisException {
        assertEquals(1, parser.parseTaskIndex("mark 2", "mark", 3));
    }

    @Test
    public void parseTaskIndex_missingOrInvalidNumber_throwsException() {
        assertThrows(ChrisException.class, () -> parser.parseTaskIndex("mark", "mark", 3));
        assertThrows(ChrisException.class, () -> parser.parseTaskIndex("mark two", "mark", 3));
        assertThrows(ChrisException.class, () -> parser.parseTaskIndex("mark 4", "mark", 3));
    }
}
