package bkxss;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

/** Tests the core free-time search logic. */
class FreeTimeFinderTest {
    private static final LocalDateTime SEARCH_START = LocalDateTime.of(2026, 9, 12, 9, 0);
    private static final LocalDateTime SEARCH_END = LocalDateTime.of(2026, 9, 12, 18, 0);

    @Test
    void findEarliestStart_noEvents_returnsSearchStart() {
        LocalDateTime result = FreeTimeFinder.findEarliestStart(
                SEARCH_START, SEARCH_END, Duration.ofHours(4), List.of()).orElseThrow();

        assertEquals(SEARCH_START, result);
    }

    @Test
    void findEarliestStart_unsortedOverlappingEvents_returnsStartAfterMergedEvents() {
        Event laterEvent = new Event("workshop", "2026-09-12 1100", "2026-09-12 1500");
        Event earlierEvent = new Event("lecture", "2026-09-12 0800", "2026-09-12 1200");

        LocalDateTime result = FreeTimeFinder.findEarliestStart(SEARCH_START, SEARCH_END,
                Duration.ofHours(3), List.of(laterEvent, earlierEvent)).orElseThrow();

        assertEquals(LocalDateTime.of(2026, 9, 12, 15, 0), result);
    }

    @Test
    void findEarliestStart_gapExactlyMatchesDuration_returnsGapStart() {
        Event firstEvent = new Event("lecture", "2026-09-12 0900", "2026-09-12 1000");
        Event secondEvent = new Event("workshop", "2026-09-12 1200", "2026-09-12 1800");

        LocalDateTime result = FreeTimeFinder.findEarliestStart(SEARCH_START, SEARCH_END,
                Duration.ofHours(2), List.of(firstEvent, secondEvent)).orElseThrow();

        assertEquals(LocalDateTime.of(2026, 9, 12, 10, 0), result);
    }

    @Test
    void findEarliestStart_noLargeEnoughGap_returnsEmpty() {
        Event morningEvent = new Event("lecture", "2026-09-12 0900", "2026-09-12 1200");
        Event afternoonEvent = new Event("workshop", "2026-09-12 1300", "2026-09-12 1800");

        assertTrue(FreeTimeFinder.findEarliestStart(SEARCH_START, SEARCH_END,
                Duration.ofHours(2), List.of(morningEvent, afternoonEvent)).isEmpty());
    }

    @Test
    void findEarliestStart_legacyEvent_throwsException() {
        Event legacyEvent = new Event("meeting", "Mon 2pm", "4pm");

        assertThrows(IllegalArgumentException.class, () -> FreeTimeFinder.findEarliestStart(
                SEARCH_START, SEARCH_END, Duration.ofHours(2), List.of(legacyEvent)));
    }

    @Test
    void findEarliestStart_nonPositiveDuration_rejectsInput() {
        for (Duration duration : List.of(Duration.ZERO, Duration.ofNanos(-1), Duration.ofHours(-2))) {
            assertThrows(IllegalArgumentException.class, () -> FreeTimeFinder.findEarliestStart(
                    SEARCH_START, SEARCH_END, duration, List.of()), duration.toString());
        }
    }

    @Test
    void findEarliestStart_equalOrReversedRange_rejectsInput() {
        for (LocalDateTime end : List.of(SEARCH_START, SEARCH_START.minusMinutes(1))) {
            assertThrows(IllegalArgumentException.class, () -> FreeTimeFinder.findEarliestStart(
                    SEARCH_START, end, Duration.ofHours(1), List.of()), end.toString());
        }
    }

    @Test
    void findEarliestStart_invalidEventRanges_rejectsInputBeforeReturningFreeSlot() {
        List<Event> invalidEvents = List.of(
                new Event("equal", "2026-09-12 1900", "2026-09-12 1900"),
                new Event("reversed", "2026-09-12 2000", "2026-09-12 1900"),
                new Event("invalid end", "2026-09-12 1900", "2026-09-12 2500"));

        for (Event event : invalidEvents) {
            assertThrows(IllegalArgumentException.class, () -> FreeTimeFinder.findEarliestStart(
                    SEARCH_START, SEARCH_END, Duration.ofHours(1), List.of(event)), event.toString());
        }
    }

    @Test
    void findEarliestStart_durationEqualsOrExceedsWholeRange_checksExactBoundary() {
        assertEquals(SEARCH_START, FreeTimeFinder.findEarliestStart(
                SEARCH_START, SEARCH_END, Duration.ofHours(9), List.of()).orElseThrow());
        assertTrue(FreeTimeFinder.findEarliestStart(
                SEARCH_START, SEARCH_END, Duration.ofHours(9).plusNanos(1), List.of()).isEmpty());
        assertTrue(FreeTimeFinder.findEarliestStart(
                SEARCH_START, SEARCH_END, Duration.ofSeconds(Long.MAX_VALUE), List.of()).isEmpty());
    }

    @Test
    void findEarliestStart_eventsOutsideOrTouchingSearchRange_leaveWholeRangeFree() {
        List<Event> events = List.of(
                new Event("before", "2026-09-12 0700", "2026-09-12 0800"),
                new Event("ends at start", "2026-09-12 0800", "2026-09-12 0900"),
                new Event("starts at end", "2026-09-12 1800", "2026-09-12 1900"),
                new Event("after", "2026-09-12 1900", "2026-09-12 2000"));

        assertEquals(SEARCH_START, FreeTimeFinder.findEarliestStart(
                SEARCH_START, SEARCH_END, Duration.ofHours(9), events).orElseThrow());
    }

    @Test
    void findEarliestStart_eventCoversEntireRange_returnsEmpty() {
        Event event = new Event("all day", "2026-09-12 0800", "2026-09-12 1900");

        assertTrue(FreeTimeFinder.findEarliestStart(
                SEARCH_START, SEARCH_END, Duration.ofMinutes(1), List.of(event)).isEmpty());
    }

    @Test
    void findEarliestStart_nestedAndAdjacentEvents_doesNotInventGapOrMoveBackwards() {
        List<Event> events = List.of(
                new Event("outer", "2026-09-12 0800", "2026-09-12 1200"),
                new Event("inner", "2026-09-12 1000", "2026-09-12 1100"),
                new Event("adjacent", "2026-09-12 1200", "2026-09-12 1400"));

        assertEquals(SEARCH_START.plusHours(5), FreeTimeFinder.findEarliestStart(
                SEARCH_START, SEARCH_END, Duration.ofHours(4), events).orElseThrow());
    }

    @Test
    void findEarliestStart_multipleSuitableGaps_returnsEarliestWithoutSortingInput() {
        Event late = new Event("late", "2026-09-12 1500", "2026-09-12 1600");
        Event early = new Event("early", "2026-09-12 1100", "2026-09-12 1200");
        ArrayList<Event> events = new ArrayList<>(List.of(late, early));

        assertEquals(SEARCH_START, FreeTimeFinder.findEarliestStart(
                SEARCH_START, SEARCH_END, Duration.ofHours(2), events).orElseThrow());
        assertEquals(List.of(late, early), events);
    }

    @Test
    void findEarliestStart_crossMidnightRange_supportsMinutePrecision() {
        LocalDateTime start = LocalDateTime.of(2028, 2, 29, 23, 30);
        LocalDateTime end = LocalDateTime.of(2028, 3, 1, 1, 0);
        Event event = new Event("late meeting", "2028-02-29 2300", "2028-03-01 0015");

        assertEquals(LocalDateTime.of(2028, 3, 1, 0, 15), FreeTimeFinder.findEarliestStart(
                start, end, Duration.ofMinutes(45), List.of(event)).orElseThrow());
        assertTrue(FreeTimeFinder.findEarliestStart(
                start, end, Duration.ofMinutes(46), List.of(event)).isEmpty());
    }
}
