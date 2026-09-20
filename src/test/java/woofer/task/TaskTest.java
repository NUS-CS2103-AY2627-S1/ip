package woofer.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.Test;

/** Tests task status, type icons, date invariants, and stable display formatting. */
public class TaskTest {
    /** Checks marking and unmarking are idempotent for every task type. */
    @Test
    public void completionTransitionsForEveryType() {
        LocalDate date = LocalDate.of(2028, 2, 29);
        List<Task> tasks = List.of(new Todo("walk"), new Deadline("food", date),
                new Event("trip", date, date.plusDays(1)));
        String[] icons = {"T", "D", "E"};
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            assertFalse(task.isDone());
            assertEquals(icons[i], task.getTypeIcon());
            assertEquals(" ", task.getStatusIcon());
            task.markAsDone();
            task.markAsDone();
            assertTrue(task.isDone());
            assertEquals("X", task.getStatusIcon());
            assertTrue(task.getDisplayText().startsWith("[" + icons[i] + "][X] "));
            task.markAsNotDone();
            task.markAsNotDone();
            assertFalse(task.isDone());
        }
        assertEquals("[T][ ] walk", tasks.getFirst().getDisplayText());
    }

    /** Checks dates render in English even under a Chinese default locale. */
    @Test
    public void dateFormattingIsIndependentOfSystemLanguage() {
        Locale original = Locale.getDefault();
        try {
            Locale.setDefault(Locale.CHINESE);
            LocalDate start = LocalDate.of(2028, 2, 29);
            Deadline deadline = new Deadline("food", start);
            Event event = new Event("trip", start, start.plusDays(1));
            assertEquals(start, deadline.getBy());
            assertEquals(start, event.getFrom());
            assertEquals(start.plusDays(1), event.getTo());
            assertEquals("[D][ ] food (by: Feb 29 2028)", deadline.getDisplayText());
            assertEquals("[E][ ] trip (from: Feb 29 2028 to: Mar 01 2028)", event.getDisplayText());
        } finally {
            Locale.setDefault(original);
        }
    }

    /** Checks invalid date ranges are rejected even when callers bypass the command parser. */
    @Test
    public void invalidEventRangesAreRejected() {
        LocalDate date = LocalDate.of(2026, 9, 17);
        assertThrows(IllegalArgumentException.class, () -> new Event("same", date, date));
        assertThrows(IllegalArgumentException.class, () -> new Event("backwards", date, date.minusDays(1)));
    }
}
