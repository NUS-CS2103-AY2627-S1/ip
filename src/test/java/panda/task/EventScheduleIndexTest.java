package panda.task;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import panda.exception.task.EventClashException;

/**
 * Contains unit tests for {@link EventScheduleIndex} interval operations.
 */
public class EventScheduleIndexTest {
    private final List<Task> tasks = new ArrayList<>();
    private final EventScheduleIndex index = new EventScheduleIndex(tasks);

    @Test
    public void add_adjacentEvent_addsEvent() throws Exception {
        Event indexedEvent = createEvent("lecture", 10, 0, 11, 0);
        Event adjacentEvent = createEvent("lunch", 11, 0, 12, 0);

        addToIndex(indexedEvent);

        assertDoesNotThrow(() -> addToIndex(adjacentEvent));
    }

    @Test
    public void add_eventOverlappingMultipleEvents_throwsWithChronologicalConflicts() throws Exception {
        Event laterEvent = createEvent("laboratory", 11, 0, 12, 0);
        Event earlierEvent = createEvent("lecture", 9, 0, 10, 0);
        Event proposedEvent = createEvent("workshop", 9, 30, 11, 30);

        addToIndex(laterEvent);
        addToIndex(earlierEvent);

        EventClashException exception = assertThrows(
                EventClashException.class, () -> index.add(proposedEvent));

        assertEquals(List.of(earlierEvent, laterEvent), exception.getConflictingEvents());
    }

    @Test
    public void add_eventDuringDateOnlyEvent_throwsEventClashException() throws Exception {
        Event allDayEvent = new Event("conference",
                LocalDate.of(2026, 9, 10),
                LocalDate.of(2026, 9, 10));
        Event eveningEvent = createEvent("dinner", 18, 0, 19, 0);

        addToIndex(allDayEvent);

        EventClashException exception = assertThrows(
                EventClashException.class, () -> index.add(eveningEvent));

        assertEquals(List.of(allDayEvent), exception.getConflictingEvents());
    }

    @Test
    public void remove_indexedEvent_freesInterval() throws Exception {
        Event indexedEvent = createEvent("lecture", 10, 0, 11, 0);
        Event sameTimeEvent = createEvent("consultation", 10, 0, 11, 0);

        addToIndex(indexedEvent);
        index.remove(indexedEvent);
        tasks.remove(indexedEvent);

        assertDoesNotThrow(() -> addToIndex(sameTimeEvent));
    }

    @Test
    public void add_overlappingEvent_throwsEventClashException() throws Exception {
        Event indexedEvent = createEvent("lecture", 10, 0, 11, 0);
        Event overlappingEvent = createEvent("consultation", 10, 30, 11, 30);

        addToIndex(indexedEvent);

        EventClashException exception = assertThrows(
                EventClashException.class, () -> index.add(overlappingEvent));

        assertEquals(List.of(indexedEvent), exception.getConflictingEvents());
    }

    private void addToIndex(Event event) throws EventClashException {
        index.add(event);
        tasks.add(event);
    }

    private static Event createEvent(String description, int startHour, int startMinute,
            int endHour, int endMinute) throws Exception {
        return new Event(description,
                LocalDateTime.of(2026, 9, 10, startHour, startMinute),
                LocalDateTime.of(2026, 9, 10, endHour, endMinute));
    }
}
