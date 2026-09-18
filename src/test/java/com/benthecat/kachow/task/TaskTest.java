package com.benthecat.kachow.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.benthecat.kachow.parser.DateTimeParser;
import com.benthecat.kachow.parser.DateTimeParser.ParsedDateTime;

/** Tests task invariants, calendar boundaries, and independent completion-state copies. */
class TaskTest {
    private static final LocalDate DATE = LocalDate.of(2026, 9, 11);
    private static final ParsedDateTime START = DateTimeParser.parse("2026-09-11 1000");
    private static final ParsedDateTime END = DateTimeParser.parse("2026-09-13 1100");

    @Test
    void constructor_validDescription_normalizesWhitespaceAndPreservesPunctuation() {
        Todo task = new Todo(" \tRead\u00a0\u2003book: C++/Java — 阅读!  ");

        assertEquals("Read book: C++/Java — 阅读!", task.getDescription());
        assertEquals("[T][ ] Read book: C++/Java — 阅读!", task.getStatusText());
        assertFalse(task.isDone());
    }

    @Test
    void constructor_invalidDescription_rejectsBlankAndUnsafeRecords() {
        for (String description : List.of("", " \t ", "\u00a0", "one|two", "one\ntwo", "one\rtwo",
                "one\u0000two", "one\u001btwo", "one\u2028two", "one\u2029two")) {
            assertThrows(IllegalArgumentException.class, () -> new Todo(description), description);
            assertThrows(IllegalArgumentException.class, () -> new Deadline(description, DATE), description);
            assertThrows(IllegalArgumentException.class, () -> new Event(description, START, END), description);
        }
        assertThrows(IllegalArgumentException.class, () -> new Todo(null));
    }

    @Test
    void markAndUnmark_repeatedCalls_areIdempotentForEveryType() {
        for (Task task : List.of(new Todo("read"), new Deadline("submit", DATE),
                new Event("meeting", START, END))) {
            task.markAsDone();
            task.markAsDone();
            assertTrue(task.isDone());
            assertTrue(task.getStatusText().contains("[X]"));
            task.markAsNotDone();
            task.markAsNotDone();
            assertFalse(task.isDone());
            assertTrue(task.getStatusText().contains("[ ]"));
        }
    }

    @Test
    void withDoneStatus_allTaskTypes_preservesDetailsWithoutSharingMutableStatus() {
        for (Task original : List.of(new Todo("read"), new Deadline("submit", DATE),
                new Event("meeting", START, END))) {
            Task completed = original.withDoneStatus(true);
            assertNotSame(original, completed);
            assertEquals(original.getClass(), completed.getClass());
            assertTrue(original.hasSameDetails(completed));
            assertFalse(original.isDone());
            assertTrue(completed.isDone());

            Task incomplete = completed.withDoneStatus(false);
            assertNotSame(completed, incomplete);
            assertTrue(completed.isDone());
            assertFalse(incomplete.isDone());
            assertEquals(original.getStatusText(), incomplete.getStatusText());
            incomplete.markAsDone();
            assertFalse(original.isDone());
        }
    }

    @Test
    void hasSameDetails_typeDescriptionAndDates_defineDuplicateIdentity() {
        Todo todo = new Todo("read book");
        assertTrue(todo.hasSameDetails(new Todo(" READ   BOOK ", true)));
        assertFalse(todo.hasSameDetails(null));
        assertFalse(todo.hasSameDetails(new Todo("read books")));
        assertFalse(todo.hasSameDetails(new Deadline("read book", DATE)));

        Deadline deadline = new Deadline("read book", DATE);
        assertTrue(deadline.hasSameDetails(new Deadline("READ BOOK", DATE, true)));
        assertFalse(deadline.hasSameDetails(new Deadline("read book", DATE.plusDays(1))));
        assertFalse(deadline.hasSameDetails(new Deadline("read book", DATE.atStartOfDay())));
        assertFalse(deadline.hasSameDetails(todo));

        Event event = new Event("meeting", START, END);
        assertTrue(event.hasSameDetails(new Event("MEETING", START, END, true)));
        assertFalse(event.hasSameDetails(new Event("meeting", DateTimeParser.parse("2026-09-11 1100"), END)));
        assertFalse(event.hasSameDetails(new Event("meeting", START, DateTimeParser.parse("2026-09-13 1200"))));
    }

    @Test
    void occursOn_calendarBoundaries_includesBothEventDatesAndExcludesAdjacentDates() {
        Event event = new Event("conference", START, END);
        assertFalse(event.occursOn(DATE.minusDays(1)));
        assertTrue(event.occursOn(DATE));
        assertTrue(event.occursOn(DATE.plusDays(1)));
        assertTrue(event.occursOn(DATE.plusDays(2)));
        assertFalse(event.occursOn(DATE.plusDays(3)));
        assertFalse(new Todo("read").occursOn(DATE));
        Deadline deadline = new Deadline("submit", DATE.atTime(23, 59));
        assertTrue(deadline.occursOn(DATE));
        assertFalse(deadline.occursOn(DATE.minusDays(1)));
        assertFalse(deadline.occursOn(DATE.plusDays(1)));
    }

    @Test
    void eventConstructor_invalidRangeOrMissingEndpoint_rejectsInvalidState() {
        assertThrows(IllegalArgumentException.class, () -> new Event("equal", START, START));
        assertThrows(IllegalArgumentException.class, () -> new Event("reversed", END, START));
        assertThrows(IllegalArgumentException.class, () -> new Event("equal date",
                new ParsedDateTime(DATE), new ParsedDateTime(DATE)));
        assertThrows(NullPointerException.class, () -> new Event("missing start", null, END));
        assertThrows(NullPointerException.class, () -> new Event("missing end", START, null));
        Event oneMinute = new Event("short", START, DateTimeParser.parse("2026-09-11 1001"));
        assertEquals(START, oneMinute.getFrom());
        assertEquals(DATE.atTime(10, 1), oneMinute.getTo().toLocalDateTime());
    }

    @Test
    void deadlineConstructors_dateAndTimeOverloads_preserveValuesAndCompletion() {
        LocalDateTime due = DATE.atTime(18, 30);
        List<Deadline> deadlines = List.of(new Deadline("submit", DATE),
                new Deadline("submit", DATE, true), new Deadline("submit", due),
                new Deadline("submit", due, true), new Deadline("submit", new ParsedDateTime(due)),
                new Deadline("submit", new ParsedDateTime(due), true));
        for (int i = 0; i < deadlines.size(); i++) {
            Deadline deadline = deadlines.get(i);
            assertEquals(DATE, deadline.getBy());
            assertEquals(i % 2 == 1, deadline.isDone());
            assertEquals(i < 2 ? Optional.empty() : Optional.of(LocalTime.of(18, 30)), deadline.getTime());
        }
        assertEquals("[D][X] submit (by: Sep 11 2026, 6:30 PM)", deadlines.get(3).getStatusText());
        assertThrows(NullPointerException.class, () -> new Deadline("submit", (ParsedDateTime) null));
    }
}
