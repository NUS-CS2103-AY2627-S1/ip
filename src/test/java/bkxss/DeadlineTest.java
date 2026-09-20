package bkxss;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.Test;

/** Tests deadline formatting and persistence parsing. */
class DeadlineTest {
    @Test
    void deadline_parseFormattedBy_impossibleDate_rejectsInsteadOfAdjusting() {
        assertThrows(DateTimeParseException.class, () -> Deadline.parseFormattedBy("Feb 30 2026 18:00"));
        assertThrows(DateTimeParseException.class, () -> Deadline.parseFormattedBy("Feb 29 2026 18:00"));
        assertEquals(LocalDateTime.of(2028, 2, 29, 18, 0), Deadline.parseFormattedBy("Feb 29 2028 18:00"));
    }

    @Test
    void deadline_nonEnglishLocale_keepsStorageFormatPortable() {
        Locale originalLocale = Locale.getDefault();
        try {
            Locale.setDefault(Locale.FRENCH);
            Deadline deadline = new Deadline("return book", LocalDateTime.of(2026, 12, 2, 18, 0));
            assertEquals("Dec 02 2026 18:00", deadline.getFormattedBy());
            assertEquals(deadline.getBy(), Deadline.parseFormattedBy(deadline.getFormattedBy()));
        } finally {
            Locale.setDefault(originalLocale);
        }
    }

    @Test
    void deadline_getFormattedBy_formatsDateForDisplay() {
        Deadline deadline = new Deadline("return book", LocalDateTime.of(2019, 12, 2, 18, 0));

        assertEquals("Dec 02 2019 18:00", deadline.getFormattedBy());
    }

    @Test
    void deadline_parseFormattedBy_validText_returnsOriginalDateTime() {
        String formattedDate = "Feb 28 2019 18:00";

        assertEquals(LocalDateTime.of(2019, 2, 28, 18, 0),
                Deadline.parseFormattedBy(formattedDate));
    }

    @Test
    void deadline_parseFormattedBy_invalidText_throwsException() {
        for (String text : List.of("not a date", "Apr 31 2026 18:00", "Dec 02 2026 24:00",
                "Dec 02 2026 18:60", "2026-12-02 1800", "Dec 02 2026 18:00 extra")) {
            assertThrows(DateTimeParseException.class, () -> Deadline.parseFormattedBy(text), text);
        }
    }

    @Test
    void deadline_completedLeapDayTask_preservesDateAndFormatsStatus() {
        LocalDateTime by = LocalDateTime.of(2028, 2, 29, 0, 5);
        Deadline deadline = new Deadline("return book", by);
        deadline.markAsDone();

        assertEquals(by, deadline.getBy());
        assertEquals(by, Deadline.parseFormattedBy("  Feb 29 2028 00:05  "));
        assertEquals("[D][X] return book (by: Feb 29 2028 00:05)", deadline.toString());
    }
}
