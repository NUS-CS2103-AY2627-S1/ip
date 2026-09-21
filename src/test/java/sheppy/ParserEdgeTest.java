package sheppy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/** Covers command boundaries and malformed arguments. */
class ParserEdgeTest {
    /** Rejects incomplete, repeated and invalid task fields. */
    @ParameterizedTest
    @ValueSource(strings = {"todo", "deadline", "event", "deadline x /by", "event x /from a",
        "event x /to b /from a", "event x /from a /to b /from c", "deadline x /by 2026-02-30",
        "deadline x /by 2026-13-01", "todo a|b", "list"})
    void invalidTasks_throwDomainException(String command) {
        assertThrows(SheppyException.class, () -> Parser.parseTask(command));
    }

    /** Rejects missing, excess, nonnumeric and overflowing task numbers. */
    @ParameterizedTest
    @ValueSource(strings = {"mark", "delete x", "unmark 1 2", "mark 2147483648", "delete 1.5"})
    void invalidNumbers_throwDomainException(String command) {
        assertThrows(SheppyException.class, () -> Parser.parseTaskNumber(command));
    }

    /** Normalizes whitespace consistently across parser entry points. */
    @Test
    void whitespace_acceptsValidCommands() throws SheppyException {
        assertEquals(CommandType.MARK, Parser.parseCommand("  mark\t1 "));
        assertEquals(1, Parser.parseTaskNumber(" mark\t1 "));
        assertEquals("read book", Parser.parseFindKeyword(" find   read   book "));
        assertEquals("[D][ ] report (by: Feb 29 2028)",
                Parser.parseTask(" deadline  report  /by  2028-02-29 ").toString());
        assertEquals(CommandType.UNKNOWN, Parser.parseCommand("sort extra"));
        assertEquals(CommandType.UNKNOWN, Parser.parseCommand("bye extra"));
        assertThrows(SheppyException.class, () -> Parser.parseFindKeyword("find "));
    }
}
