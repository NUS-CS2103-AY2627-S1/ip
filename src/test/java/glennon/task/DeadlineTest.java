package glennon.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Locale;

import org.junit.jupiter.api.Test;

/**
 * Tests deadline validation, schedule matching, and defining detail comparisons.
 */
class DeadlineTest {
    @Test
    void constructor_nullDateTime_throwsHelpfulException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, () -> new Deadline("read book", null));

        assertEquals("Deadline date and time must not be null.", exception.getMessage());
    }

    @Test
    void constructor_invalidDescription_rejectsInheritedValidationFailures() {
        LocalDateTime dueDateTime = LocalDateTime.of(2026, 9, 1, 9, 0);

        assertThrows(IllegalArgumentException.class, () -> new Deadline(null, dueDateTime));
        assertThrows(IllegalArgumentException.class, () -> new Deadline(" \t ", dueDateTime));
        assertThrows(IllegalArgumentException.class, () -> new Deadline("two\nlines", dueDateTime));
    }

    @Test
    void hasSameDetails_matchingDescriptionAndDateTime_ignoresCompletion() {
        LocalDateTime dueDateTime = LocalDateTime.of(2026, 9, 1, 9, 0);
        Deadline first = new Deadline("read book", dueDateTime);
        Deadline second = new Deadline(" read book ", dueDateTime);
        first.markAsDone();

        assertTrue(first.hasSameDetails(second));
        assertTrue(second.hasSameDetails(first));
    }

    @Test
    void hasSameDetails_differentDescriptionOrDateTime_returnsFalse() {
        LocalDateTime dueDateTime = LocalDateTime.of(2026, 9, 1, 9, 0);
        Deadline deadline = new Deadline("read book", dueDateTime);

        assertFalse(deadline.hasSameDetails(new Deadline("Read book", dueDateTime)));
        assertFalse(deadline.hasSameDetails(new Deadline("read book", dueDateTime.plusDays(1))));
        assertFalse(deadline.hasSameDetails(new Deadline("read book", dueDateTime.plusMinutes(1))));
        assertFalse(deadline.hasSameDetails(new Deadline("read  book", dueDateTime)));
    }

    @Test
    void hasSameDetails_nullOrOtherTaskType_returnsFalse() {
        LocalDateTime dueDateTime = LocalDateTime.of(2026, 9, 1, 9, 0);
        Deadline deadline = new Deadline("read book", dueDateTime);
        Event event = new Event("read book", dueDateTime, dueDateTime.plusHours(1));

        assertFalse(deadline.hasSameDetails(null));
        assertFalse(deadline.hasSameDetails(new Todo("read book")));
        assertFalse(deadline.hasSameDetails(event));
        assertFalse(event.hasSameDetails(deadline));
    }

    @Test
    void occursOn_dueDate_matchesOnlyThatDate() {
        Deadline deadline = new Deadline("read book", LocalDateTime.of(2026, 9, 1, 23, 59));

        assertTrue(deadline.occursOn(LocalDate.of(2026, 9, 1)));
        assertFalse(deadline.occursOn(LocalDate.of(2026, 8, 31)));
        assertFalse(deadline.occursOn(LocalDate.of(2026, 9, 2)));
    }

    @Test
    void constructor_preciseDateTime_preservesAllDateTimeFields() {
        LocalDateTime dueDateTime = LocalDateTime.of(2028, 2, 29, 23, 59, 59, 123456789);
        Deadline deadline = new Deadline(" leap day mission ", dueDateTime);

        assertEquals(dueDateTime, deadline.getDueDateTime());
        assertEquals("leap day mission", deadline.getDescription());
        assertFalse(deadline.isDone());
        assertTrue(deadline.occursOn(LocalDate.of(2028, 2, 29)));
        assertFalse(deadline.hasSameDetails(new Deadline("leap day mission", dueDateTime.plusNanos(1))));
    }

    @Test
    void toString_midnightAndNoon_usesEnglishTwelveHourTimeRegardlessOfDefaultLocale() {
        Locale originalLocale = Locale.getDefault(Locale.Category.FORMAT);
        try {
            Locale.setDefault(Locale.Category.FORMAT, Locale.CHINESE);
            Deadline midnight = new Deadline("midnight", LocalDateTime.of(2028, 2, 29, 0, 0));
            Deadline noon = new Deadline("noon", LocalDateTime.of(2028, 2, 29, 12, 0));
            noon.markAsDone();

            assertEquals("[D][ ] midnight (by: Feb 29 2028, 12:00 AM)", midnight.toString());
            assertEquals("[D][X] noon (by: Feb 29 2028, 12:00 PM)", noon.toString());
        } finally {
            Locale.setDefault(Locale.Category.FORMAT, originalLocale);
        }
    }

    @Test
    void hasSameDetails_subclassWithIdenticalDetails_isDistinctConcreteType() {
        LocalDateTime dueDateTime = LocalDateTime.of(2026, 9, 1, 9, 0);
        Deadline deadline = new Deadline("read book", dueDateTime);
        Deadline specializedDeadline = new SpecializedDeadline("read book", dueDateTime);

        assertFalse(deadline.hasSameDetails(specializedDeadline));
        assertFalse(specializedDeadline.hasSameDetails(deadline));
    }

    /**
     * Supplies a deadline subtype to verify exact concrete-type comparisons.
     */
    private static final class SpecializedDeadline extends Deadline {
        private SpecializedDeadline(String description, LocalDateTime dueDateTime) {
            super(description, dueDateTime);
        }
    }
}
