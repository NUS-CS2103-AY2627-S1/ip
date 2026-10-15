package pip.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Tests {@link Task} and its subclasses {@link Todo}, {@link Deadline}, and
 * {@link Event}: initial state, markDone/markUndone toggling, exact display
 * formatting via toString(), and Event's start/end date validation.
 */
class TaskTest {

    @Nested
    class BaseTaskTests {
        @Test
        void newTask_isNotCompletedByDefault() {
            Task task = new Task("read book");
            assertFalse(task.isCompleted());
            assertEquals("read book", task.getName());
        }

        @Test
        void markDone_setsCompletedTrue() {
            Task task = new Task("read book");
            task.markDone();
            assertTrue(task.isCompleted());
        }

        @Test
        void markUndone_setsCompletedFalse() {
            Task task = new Task("read book");
            task.markDone();
            task.markUndone();
            assertFalse(task.isCompleted());
        }

        @Test
        void markDone_calledTwice_remainsCompleted() {
            Task task = new Task("read book");
            task.markDone();
            task.markDone();
            assertTrue(task.isCompleted());
        }

        @Test
        void toString_notCompleted_usesBlankStatusBox() {
            Task task = new Task("read book");
            assertEquals("[ ] read book", task.toString());
        }

        @Test
        void toString_completed_usesXStatusBox() {
            Task task = new Task("read book");
            task.markDone();
            assertEquals("[X] read book", task.toString());
        }
    }

    @Nested
    class TodoTests {
        @Test
        void toString_prependsTypeTagToBaseFormat() {
            Todo todo = new Todo("buy milk");
            assertEquals("[T][ ] buy milk", todo.toString());

            todo.markDone();
            assertEquals("[T][X] buy milk", todo.toString());
        }
    }

    @Nested
    class DeadlineTests {
        @Test
        void getDeadline_returnsDateSuppliedToConstructor() {
            LocalDate date = LocalDate.of(2024, 3, 15);
            Deadline deadline = new Deadline("submit report", date);
            assertEquals(date, deadline.getDeadline());
        }

        @Test
        void toString_formatsDateAsAbbreviatedMonthDayYear() {
            Deadline deadline = new Deadline("submit report", LocalDate.of(2024, 3, 5));
            assertEquals("[D][ ] submit report (by: Mar 05 2024)", deadline.toString());
        }

        @Test
        void toString_completedDeadline_showsXStatusBox() {
            Deadline deadline = new Deadline("submit report", LocalDate.of(2024, 3, 5));
            deadline.markDone();
            assertEquals("[D][X] submit report (by: Mar 05 2024)", deadline.toString());
        }
    }

    @Nested
    class EventTests {
        @Test
        void getStartAndGetEnd_returnDatesSuppliedToConstructor() {
            LocalDate start = LocalDate.of(2024, 5, 1);
            LocalDate end = LocalDate.of(2024, 5, 10);
            Event event = new Event("team trip", start, end);

            assertEquals(start, event.getStart());
            assertEquals(end, event.getEnd());
        }

        @Test
        void constructor_endBeforeStart_throwsIllegalArgumentException() {
            assertThrows(IllegalArgumentException.class,
                    () -> new Event("team trip", LocalDate.of(2024, 5, 10), LocalDate.of(2024, 5, 1)));
        }

        @Test
        void constructor_nullStartOrEnd_throwsIllegalArgumentException() {
            assertThrows(IllegalArgumentException.class,
                    () -> new Event("team trip", null, LocalDate.of(2024, 5, 1)));
            assertThrows(IllegalArgumentException.class,
                    () -> new Event("team trip", LocalDate.of(2024, 5, 1), null));
        }

        @Test
        void constructor_startEqualsEnd_isAllowed() {
            LocalDate sameDay = LocalDate.of(2024, 5, 1);
            Event event = new Event("one-day event", sameDay, sameDay);
            assertEquals(sameDay, event.getStart());
            assertEquals(sameDay, event.getEnd());
        }

        @Test
        void toString_formatsBothDatesAsAbbreviatedMonthDayYear() {
            Event event = new Event("team trip", LocalDate.of(2024, 5, 1), LocalDate.of(2024, 5, 10));
            assertEquals("[E][ ] team trip (from: May 01 2024, to: May 10 2024)", event.toString());
        }
    }
}
