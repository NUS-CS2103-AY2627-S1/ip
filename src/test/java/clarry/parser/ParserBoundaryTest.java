package clarry.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.Test;

import clarry.ClarryException;

/** Covers command dispatch and parser boundaries independently of storage. */
class ParserBoundaryTest {
    private final Parser parser = new Parser();

    @Test
    void parseCommandType_everyCommandAndUnsupportedWords_dispatchesCorrectly() {
        for (CommandType type : CommandType.values()) {
            if (type != CommandType.UNKNOWN) {
                assertEquals(type, parser.parseCommandType(type.name().toLowerCase(Locale.ROOT) + " arguments"));
            }
        }
        for (String input : List.of("", "TODO book", "todos book", "helpful", "dance")) {
            assertEquals(CommandType.UNKNOWN, parser.parseCommandType(input), input);
        }
    }

    @Test
    void normalizeInput_tabsAndSpaces_preservesPunctuationAndUnicode() throws ClarryException {
        assertEquals("todo café 鱼!", parser.normalizeInput(" \ttodo\t café  鱼!  "));
    }

    @Test
    void requireExactCommand_arguments_rejectsExtraText() throws ClarryException {
        for (String command : List.of("help", "list", "bye")) {
            parser.requireExactCommand("  " + command + "  ", command);
            assertThrows(ClarryException.class, () -> parser.requireExactCommand(command + " extra", command));
        }
    }

    @Test
    void parseDeadline_missingDescriptionsAndDates_rejectsIncompleteCommands() {
        for (String input : List.of("deadline", "deadline /by 2024-02-29", "deadline book /by",
                "deadline book /by ", "deadline book", "deadline book /by 2024-02-29 /by 2024-03-01")) {
            assertThrows(ClarryException.class, () -> parser.parseDeadline(input), input);
        }
    }

    @Test
    void parseDatedTasks_validLeapDayAndWhitespace_returnsCanonicalStorage() throws ClarryException {
        assertEquals("D | 0 | return book | 2024-02-29",
                parser.parseDeadline("deadline return book /by 2024-02-29").toFileFormat());
        String input = parser.normalizeInput("event\ttrip  /from  2024-02-29  23:00 /to 2024-03-01 01:00");
        assertEquals("E | 0 | trip | 2024-02-29 23:00 | 2024-03-01 01:00",
                parser.parseEvent(input).toFileFormat());
        assertEquals(LocalDate.of(2024, 2, 29), parser.parseDate("on 2024-02-29"));
    }

    @Test
    void parseIndex_firstLastAndZeroPaddedNumbers_returnsZeroBasedIndices() throws ClarryException {
        assertEquals(0, parser.parseIndex("mark 1", "mark", 3));
        assertEquals(2, parser.parseIndex("delete 003", "delete", 3));
        assertThrows(ClarryException.class, () -> parser.parseIndex("mark", "mark", 3));
        assertThrows(ClarryException.class, () -> parser.parseIndex("mark 1", "mark", 0));
    }

    @Test
    void parseDate_missingAndExtraArguments_rejectsInvalidInput() {
        for (String input : List.of("on", "on 2024-02-30", "on 2024-02-29 extra")) {
            assertThrows(ClarryException.class, () -> parser.parseDate(input), input);
        }
    }
}
