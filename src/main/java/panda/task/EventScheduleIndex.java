package panda.task;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

import panda.exception.task.EventClashException;

/**
 * Indexes non-overlapping events chronologically and finds schedule overlaps.
 */
final class EventScheduleIndex {
    private final NavigableMap<LocalDateTime, Event> eventsByStart = new TreeMap<>();
    private final List<Task> tasks;

    /**
     * Creates an empty event schedule index associated with the supplied task list.
     *
     * @param tasks Tasks whose event numbers are reported for schedule clashes.
     */
    EventScheduleIndex(List<Task> tasks) {
        assert tasks != null : "Task list associated with schedule index must not be null";
        this.tasks = tasks;
    }

    /**
     * Adds an event to the index.
     *
     * @param event Valid event to index.
     * @throws EventClashException If the event overlaps an indexed event.
     */
    void add(Event event) throws EventClashException {
        assert event != null : "Event added to schedule index must not be null";
        List<Event> conflictingEvents = findOverlappingEvents(event);
        if (!conflictingEvents.isEmpty()) {
            throw new EventClashException(tasks, conflictingEvents);
        }
        eventsByStart.put(event.getEffectiveStartDateTime(), event);
    }

    /**
     * Removes an event from the index.
     *
     * @param event Indexed event to remove.
     */
    void remove(Event event) {
        assert event != null : "Event removed from schedule index must not be null";
        eventsByStart.remove(event.getEffectiveStartDateTime(), event);
    }

    /**
     * Finds indexed events whose half-open intervals overlap the supplied event.
     *
     * @param event Event interval to query.
     * @return Conflicting events ordered chronologically by start time.
     */
    private List<Event> findOverlappingEvents(Event event) {
        assert event != null : "Event queried in schedule index must not be null";

        LocalDateTime queryStart = event.getEffectiveStartDateTime();
        LocalDateTime queryEnd = event.getEffectiveEndDateTime();
        List<Event> conflictingEvents = new ArrayList<>();

        Map.Entry<LocalDateTime, Event> precedingEntry = eventsByStart.lowerEntry(queryStart);
        if (precedingEntry != null
                && precedingEntry.getValue().getEffectiveEndDateTime().isAfter(queryStart)) {
            conflictingEvents.add(precedingEntry.getValue());
        }

        for (Map.Entry<LocalDateTime, Event> entry
                : eventsByStart.tailMap(queryStart, true).entrySet()) {
            if (!entry.getKey().isBefore(queryEnd)) {
                break;
            }
            conflictingEvents.add(entry.getValue());
        }
        return List.copyOf(conflictingEvents);
    }
}
