package herta.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import herta.exception.HertaException;

/**
 * Tests find, filter, upcoming, and sort argument parsing.
 */
class QueryCommandParserTest {
    private final Parser parser = new Parser();

    @Test
    void parseFindKeyword_nonEmptyKeyword_returnsTrimmedKeyword() throws HertaException {
        assertEquals("read book", parser.parseFindKeyword("find   read book  "));
    }

    @Test
    void parseFindKeyword_emptyKeyword_throwsHelpfulException() {
        HertaException exception = assertThrows(HertaException.class, () ->
                parser.parseFindKeyword("find   "));

        assertEquals("Find something specific. Use: find <keyword>.", exception.getMessage());
    }

    @Test
    void parseFilterDate_supportedFormats_returnsExpectedDate() throws HertaException {
        assertEquals(LocalDate.of(2019, 10, 15),
                parser.parseFilterDate("filter /on 2019-10-15"));
        assertEquals(LocalDate.of(2019, 10, 15),
                parser.parseFilterDate("filter /on 15/10/2019"));
    }

    @Test
    void parseFilterDate_missingOnKeyword_throwsHelpfulException() {
        HertaException exception = assertThrows(HertaException.class, () ->
                parser.parseFilterDate("filter 2019-10-15"));

        assertEquals("Your filter needs /on. Try: filter /on <date>.", exception.getMessage());
    }

    @Test
    void parseFilterDate_invalidDate_throwsHelpfulException() {
        String[] invalidInputs = {
            "filter /on 31/02/2019",
            "filter /on 2019-02-30",
            "filter /on 2-12-2019"
        };

        for (String input : invalidInputs) {
            HertaException exception = assertThrows(HertaException.class, () ->
                    parser.parseFilterDate(input));
            assertEquals("That date won't do. Try 2019-10-15 or 15/10/2019.", exception.getMessage());
        }
    }

    @Test
    void parseFilterDate_nullInput_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> parser.parseFilterDate(null));
    }

    @Test
    void parseFilterDate_missingDuplicateAndLookalikeMarkers_returnsGuidance() {
        HertaException duplicateMarker = assertThrows(HertaException.class, () ->
                parser.parseFilterDate("filter /on 2019-10-15 /on 2019-10-16"));
        HertaException missingDate = assertThrows(HertaException.class, () ->
                parser.parseFilterDate("filter /on"));
        HertaException markerLikeText = assertThrows(HertaException.class, () ->
                parser.parseFilterDate("filter /only 2019-10-15"));

        assertEquals("One /on marker is enough. Try: filter /on <date>.",
                duplicateMarker.getMessage());
        assertEquals("You gave me /on without a date. Try: filter /on <date>.",
                missingDate.getMessage());
        assertEquals("Your filter needs /on. Try: filter /on <date>.", markerLikeText.getMessage());
    }

    @Test
    void parseUpcomingDays_positiveNumber_returnsNumber() throws HertaException {
        assertEquals(30, parser.parseUpcomingDays("upcoming 30"));
    }

    @Test
    void parseUpcomingDays_nonPositiveOrNonNumericInput_throwsHelpfulException() {
        for (String input : new String[] {"upcoming 0", "upcoming -1", "upcoming many"}) {
            HertaException exception = assertThrows(HertaException.class, () ->
                    parser.parseUpcomingDays(input));
            assertEquals("That day count is not useful. Use: upcoming <days>, with a positive number in range.",
                    exception.getMessage());
        }
    }

    @Test
    void validateSortCommand_onlyAcceptsDateSorting() throws HertaException {
        parser.validateSortCommand("sort date");

        HertaException exception = assertThrows(HertaException.class, () ->
                parser.validateSortCommand("sort time"));
        assertEquals("That sorting option is not supported. Try: sort date.", exception.getMessage());
    }
}
