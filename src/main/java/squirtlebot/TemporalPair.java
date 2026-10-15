package squirtlebot;

import java.io.Serializable;
import java.time.temporal.Temporal;

/**
 * Stores a pair of dates and date times, interpreted as start and end date/date times for an event.
 *
 * @param startDate start date or datetime for an event.
 * @param endDate end date or datetime for an event.
 */
public record TemporalPair(Temporal startDate, Temporal endDate) implements Serializable {}
