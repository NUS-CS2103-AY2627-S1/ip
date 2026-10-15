package zabud.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import zabud.commands.tokens.DateTimeToken;

/**
 * Tests valid and invalid temporal command values and their help text.
 */
class DateTimeTokenTest {
    @Test
    void parsesDateTimeAndBothSupportedPartialForms() {
        DateTimeToken date = new DateTimeToken("by", "2/12/2019");
        DateTimeToken time = new DateTimeToken("by", "900");
        DateTimeToken both = new DateTimeToken("by", "2/12/2019 1800");

        assertEquals(LocalDate.of(2019, 12, 2), date.getDate());
        assertNull(date.getTime());
        assertNull(time.getDate());
        assertEquals(LocalTime.of(9, 0), time.getTime());
        assertEquals(LocalDate.of(2019, 12, 2), both.getDate());
        assertEquals(LocalTime.of(18, 0), both.getTime());
    }

    @Test
    void rejectsBlankImpossibleAndMalformedValues() {
        for (String value : List.of("", "31/2/2019", "29/2/2019", "2400", "1260", "2019-12-02", "noon",
                "2/12/2019 1800 extra", "2/12/19", "1/1/2020 9")) {
            assertFalse(new DateTimeToken("by", value).isValid(), value);
        }
    }

    @Test
    void producesDeduplicatedTokenHelp() {
        assertEquals("/by DATE_OR_TIME", Token.composeHints(new DateTimeToken("by", "")));
        assertEquals(" - DATE_OR_TIME: enter a date as DD/MM/YYYY, a 24-hour time as HHMM, "
                        + "or both as DD/MM/YYYY HHMM.",
                Token.composeRequirements(new DateTimeToken("from", ""), new DateTimeToken("to", "")));
        assertTrue(new DateTimeToken("/by", "1200").isValid());
    }
}
