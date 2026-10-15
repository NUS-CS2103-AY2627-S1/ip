package dingleberry.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/** Tests the observable behavior shared by all {@link Task} implementations. */
class TaskTest {
    /** Defines the year used by date-based task fixtures. */
    private static final int TEST_YEAR = 2026;
    /** Defines the month used by date-based task fixtures. */
    private static final int TEST_MONTH = 8;
    /** Defines the day used by date-based task fixtures. */
    private static final int TEST_DAY = 25;
    /** Defines the morning hour used by date-based task fixtures. */
    private static final int MORNING_HOUR = 9;
    /** Defines the deadline hour used by date-based task fixtures. */
    private static final int DEADLINE_HOUR = 18;
    /** Defines the event start hour used by date-based task fixtures. */
    private static final int EVENT_START_HOUR = 14;
    /** Defines the event end hour used by date-based task fixtures. */
    private static final int EVENT_END_HOUR = 15;
    /** Defines the event end minute used by date-based task fixtures. */
    private static final int EVENT_END_MINUTE = 30;

    @Test
    void taskDescriptionProvidedPreservesDescription() {
        Task task = new Todo("submit assignment");

        assertEquals("submit assignment", task.getDescription());
    }

    @Test
    void markAsDoneTaskNotDoneMarksTaskDone() {
        Task task = new Todo("submit assignment");

        task.markAsDone();

        assertTrue(task.isDone());
        assertEquals("X", task.getStatusIcon());
    }

    @Test
    void markAsDoneAlreadyDoneRemainsDone() {
        Task task = new Todo("submit assignment");
        task.markAsDone();

        task.markAsDone();

        assertTrue(task.isDone());
        assertEquals("X", task.getStatusIcon());
    }

    @Test
    void taskBeforeMarkAsDoneIsNotDone() {
        Task task = new Todo("submit assignment");

        assertFalse(task.isDone());
        assertEquals(" ", task.getStatusIcon());
    }

    @Test
    void unmarkDoneDoneTaskMarksTaskNotDone() {
        Task task = new Todo("submit assignment");
        task.markAsDone();

        task.unmarkDone();

        assertFalse(task.isDone());
        assertEquals(" ", task.getStatusIcon());
    }

    @Test
    void unmarkDoneAlreadyNotDoneRemainsNotDone() {
        Task task = new Todo("submit assignment");

        task.unmarkDone();

        assertFalse(task.isDone());
    }

    @Test
    void toStringTaskNotDoneIncludesPendingStatusAndDescription() {
        Task task = new Todo("submit assignment");

        assertEquals("[T][ ] submit assignment", task.toString());
    }

    @Test
    void toStringDoneTaskIncludesCompletedStatusAndDescription() {
        Task task = new Todo("submit assignment");
        task.markAsDone();

        assertEquals("[T][X] submit assignment", task.toString());
    }

    @Test
    void toStringEmptyDescriptionPreservesEmptyDescription() {
        Task task = new Todo("");

        assertEquals("[T][ ] ", task.toString());
    }

    @Test
    void toSaveFormatTaskNotDoneEncodesPendingStatusAndDescription() {
        Task task = new Todo("submit assignment");

        assertEquals("T | 0 | submit assignment", task.toSaveFormat());
    }

    @Test
    void toSaveFormatDoneTaskEncodesCompletedStatusAndDescription() {
        Task task = new Todo("submit assignment");
        task.markAsDone();

        assertEquals("T | 1 | submit assignment", task.toSaveFormat());
    }

        @Test
        void deadlineFormattingMorningUsesLowercaseAm() {
            Task task = new Deadlines("submit report",
                LocalDateTime.of(TEST_YEAR, TEST_MONTH, TEST_DAY,
                    MORNING_HOUR, 0));

            assertEquals("[D][ ] submit report (by: Aug 25 2026, 9:00 am)",
                task.toString());
        }

        @Test
        void deadlineFormattingIncludesDisplayAndSaveDateTime() {
        Task task = new Deadlines("submit report",
            LocalDateTime.of(TEST_YEAR, TEST_MONTH, TEST_DAY,
                DEADLINE_HOUR, 0));

        assertEquals("[D][ ] submit report (by: Aug 25 2026, 6:00 pm)",
            task.toString());
        assertEquals("D | 0 | submit report | 2026-08-25T18:00:00",
            task.toSaveFormat());
        }

        @Test
        void eventFormattingIncludesDisplayAndSaveDateTimes() {
        Task task = new Events("team meeting",
            LocalDateTime.of(TEST_YEAR, TEST_MONTH, TEST_DAY,
                EVENT_START_HOUR, 0),
            LocalDateTime.of(TEST_YEAR, TEST_MONTH, TEST_DAY,
                EVENT_END_HOUR, EVENT_END_MINUTE));

        assertEquals("[E][ ] team meeting (from: Aug 25 2026, 2:00 pm"
            + " to: Aug 25 2026, 3:30 pm)", task.toString());
        assertEquals("E | 0 | team meeting | 2026-08-25T14:00:00"
            + " | 2026-08-25T15:30:00", task.toSaveFormat());
        }
}
