package stewie;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Locale;

import org.junit.jupiter.api.Test;

import stewie.model.Date;
import stewie.model.Deadline;
import stewie.model.Event;
import stewie.model.Task;
import stewie.model.ToDo;

/**
 * Verifies model state transitions, identity, display strings, and date boundaries.
 */
public class TaskModelTest {
    /**
     * Verifies all task types start unfinished and marking and unmarking are idempotent.
     */
    @Test
    public void changeStatus_preservesTaskDetails() {
        Task[] tasks = {new ToDo("Read Book"), new Deadline("Report", "2026-01-01"),
            new Event("Trip", "2026-01-01", "2026-01-02")};
        String[] expected = {"[T] [ ] Read Book", "[D] [ ] Report (by: 01 Jan 2026)",
            "[E] [ ] Trip (from: 01 Jan 2026 to: 02 Jan 2026)"};
        for (int index = 0; index < tasks.length; index++) {
            Task task = tasks[index];
            assertFalse(task.isDone());
            assertEquals(" ", task.getStatusIcon());
            assertEquals(expected[index], task.toString());
            task.markAsDone();
            task.markAsDone();
            assertTrue(task.isDone());
            assertEquals("X", task.getStatusIcon());
            assertEquals(expected[index].replace("[ ]", "[X]"), task.toString());
            task.markAsUndone();
            task.markAsUndone();
            assertFalse(task.isDone());
            assertEquals(expected[index], task.toString());
        }
    }

    /**
     * Verifies normalization preserves Unicode text while rejecting unsafe descriptions.
     */
    @Test
    public void createTask_validatesAndNormalizesDescriptions() {
        assertEquals("Read စာအုပ်", new ToDo("  Read\u2003  စာအုပ်  ").getDescription());
        for (String description : new String[] {null, "", "\u2003", "a\tb", "a\rb", "a\u007fb", "a|b"}) {
            assertThrows(IllegalArgumentException.class, () -> new ToDo(description));
        }
        for (String value : new String[] {null, "", "  "}) {
            assertEquals("Deadline date or time cannot be empty.", assertThrows(IllegalArgumentException.class,
                    () -> new Deadline("Report", value)).getMessage());
            assertEquals("Event start time cannot be empty.", assertThrows(IllegalArgumentException.class,
                    () -> new Event("Trip", value, "2026-01-02")).getMessage());
            assertEquals("Event end time cannot be empty.", assertThrows(IllegalArgumentException.class,
                    () -> new Event("Trip", "2026-01-01", value)).getMessage());
        }
    }

    /**
     * Verifies identity distinguishes descriptions, task types, and each event boundary.
     */
    @Test
    public void hasSameDetails_checksEveryIdentityField() {
        Task event = new Event("Trip", "2026-01-01", "2026-01-03");
        event.markAsDone();
        assertTrue(event.hasSameDetails(new Event("trip", "1/1/2026", "3/1/2026")));
        assertFalse(event.hasSameDetails(new Event("Trip", "2026-01-02", "2026-01-03")));
        assertFalse(event.hasSameDetails(new Event("Trip", "2026-01-01", "2026-01-04")));
        assertFalse(event.hasSameDetails(new Event("Other", "2026-01-01", "2026-01-03")));
        assertFalse(event.hasSameDetails(new ToDo("Trip")));
        assertFalse(event.hasSameDetails(null));
        assertFalse(new ToDo("one").hasSameDetails(new ToDo("two")));
    }

    /**
     * Verifies supported year endpoints and strict ordering, including equality.
     */
    @Test
    public void date_checksSupportedYearBoundaries() {
        Date first = new Date("0001-01-01");
        Date last = new Date("9999-12-31");
        assertEquals("01 Jan 0001", first.toString());
        assertEquals("31 Dec 9999", last.toString());
        assertTrue(first.isBefore(last));
        assertFalse(last.isBefore(first));
        assertFalse(first.isBefore(new Date("1/1/0001")));
        for (String value : new String[] {"+10000-01-01", "1900-02-29", "2026-13-01", "2026-01-00",
            "2026-01-01T10:00", "2026-01-01 extra"}) {
            assertThrows(IllegalArgumentException.class, () -> new Date(value), value);
        }
        assertEquals("29 Feb 2000", new Date("2000-02-29").toString());
    }

    /**
     * Verifies English dates remain stable under Burmese operating-system language settings.
     */
    @Test
    public void date_preservesEnglishFormattingInBurmeseLocale() {
        Locale original = Locale.getDefault();
        try {
            Locale.setDefault(Locale.forLanguageTag("my-MM"));
            assertEquals("28 Aug 2026", new Date("August 28, 2026").toString());
        } finally {
            Locale.setDefault(original);
        }
    }
}
