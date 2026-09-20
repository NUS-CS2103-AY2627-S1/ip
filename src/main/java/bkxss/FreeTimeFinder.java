package bkxss;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/** Finds the earliest continuous free period among scheduled events. */
public final class FreeTimeFinder {
    private FreeTimeFinder() {
    }

    /**
     * Finds the earliest start that can fit the requested duration inside the search range.
     * Events may overlap or extend beyond the search range. Every supplied event must use
     * parseable start and end times.
     *
     * @param searchStart beginning of the inclusive search range
     * @param searchEnd end of the exclusive search range
     * @param requiredDuration length of the desired free period
     * @param events events that occupy time
     * @return the earliest possible start, or an empty value if no period is long enough
     * @throws IllegalArgumentException if the range, duration, or event times are invalid
     */
    public static Optional<LocalDateTime> findEarliestStart(LocalDateTime searchStart,
            LocalDateTime searchEnd, Duration requiredDuration, List<Event> events) {
        validateArguments(searchStart, searchEnd, requiredDuration, events);
        List<Event> sortedEvents = events.stream()
                .sorted(Comparator.comparing(event -> event.getFromDateTime().orElseThrow()))
                .toList();
        LocalDateTime candidateStart = searchStart;

        for (Event event : sortedEvents) {
            LocalDateTime eventStart = event.getFromDateTime().orElseThrow();
            LocalDateTime eventEnd = event.getToDateTime().orElseThrow();
            if (!eventEnd.isAfter(candidateStart)) {
                continue;
            }
            if (!eventStart.isBefore(searchEnd)) {
                break;
            }
            if (canFit(candidateStart, eventStart, requiredDuration)) {
                return Optional.of(candidateStart);
            }
            if (eventEnd.isAfter(candidateStart)) {
                candidateStart = eventEnd;
            }
        }

        if (canFit(candidateStart, searchEnd, requiredDuration)) {
            return Optional.of(candidateStart);
        }
        return Optional.empty();
    }

    /** Rejects invalid inputs so schedule calculations remain deterministic. */
    private static void validateArguments(LocalDateTime searchStart, LocalDateTime searchEnd,
            Duration requiredDuration, List<Event> events) {
        if (!searchStart.isBefore(searchEnd)) {
            throw new IllegalArgumentException("Search start must be before search end");
        }
        if (requiredDuration.isZero() || requiredDuration.isNegative()) {
            throw new IllegalArgumentException("Required duration must be positive");
        }
        if (events.stream().anyMatch(event -> !event.hasScheduledTimes())) {
            throw new IllegalArgumentException("Every event must have valid scheduled times");
        }
    }

    /** Returns whether the period from start to end is at least the requested duration. */
    private static boolean canFit(LocalDateTime start, LocalDateTime end, Duration requiredDuration) {
        return !end.isBefore(start) && Duration.between(start, end).compareTo(requiredDuration) >= 0;
    }
}
