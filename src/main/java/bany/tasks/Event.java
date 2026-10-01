package bany.tasks;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import bany.parsers.DateTimeParser;
import bany.tags.Tag;

/** Represents a task that occurs between a start and end date-time. */
public class Event extends Task {

    /**
     * Creates an event from formatted date-time strings.
     *
     * @param description event description.
     * @param from event start in {@code dd-MM-yyyy HH:mm} format.
     * @param to event end in {@code dd-MM-yyyy HH:mm} format.
     * @param id task ID.
     */
    public Event(String description, String from, String to, int id) {
        this(description,
                DateTimeParser.createLocalDateTime(from),
                DateTimeParser.createLocalDateTime(to),
                id);
    }

    /**
     * Creates an event from typed date-time values.
     *
     * @param description event description.
     * @param from event start date-time.
     * @param to event end date-time.
     * @param id task ID.
     */
    public Event(String description, LocalDateTime from, LocalDateTime to, int id) {
        super(id, description, List.of(
                createDateTag("from", from),
                createDateTag("to", to)));
        validateDateRange(from, to);
    }

    /**
     * Ensures that the event's end is not before its start.
     *
     * @param from event start date-time.
     * @param to event end date-time.
     * @throws IllegalArgumentException if the end precedes the start.
     */
    private static void validateDateRange(LocalDateTime from, LocalDateTime to) {
        if (to.isBefore(from)) {
            throw new IllegalArgumentException("Event end date-time cannot be before its start.");
        }
    }

    /**
     * Returns the event start in the user-facing format used by the task file.
     *
     * @return formatted event start date-time.
     */
    public String getFrom() {
        return DateTimeParser.formatLocalDateTime(getFromDateTime());
    }

    /**
     * Returns the event end in the user-facing format used by the task file.
     *
     * @return formatted event end date-time.
     */
    public String getTo() {
        return DateTimeParser.formatLocalDateTime(getToDateTime());
    }

    /**
     * Returns the typed event start for date-time operations.
     *
     * @return event start date-time.
     */
    public LocalDateTime getFromDateTime() {
        return getDateTimeValue("from");
    }

    /**
     * Returns the typed event end for date-time operations.
     *
     * @return event end date-time.
     */
    public LocalDateTime getToDateTime() {
        return getDateTimeValue("to");
    }

    /**
     * Returns the date information appended to this task's display text.
     *
     * @return formatted event display text.
     */
    public String getDateString() {
        return " (from: " + getFrom() + " to: " + getTo() + ")";
    }

    /**
     * Returns the abbreviated type marker.
     *
     * @return {@code E}.
     */
    @Override
    public String getTypeShort() {
        return TaskType.EVENT.getTypeShort();
    }

    /**
     * Returns the full task type name.
     *
     * @return {@code EVENT}.
     */
    @Override
    public String getType() {
        return TaskType.EVENT.getType();
    }

    /**
     * Returns the names of the tags that define an event's schedule.
     *
     * @return list containing {@code from} and {@code to}.
     */
    @Override
    public List<String> getCriticalTags() {
        return List.of("from", "to");
    }

    /**
     * Replaces the event start through the common tag storage.
     *
     * @param from new event start date-time.
     */
    public void setFrom(LocalDateTime from) {
        updateTags(List.of(createDateTag("from", from)));
    }

    /**
     * Replaces the event end through the common tag storage.
     *
     * @param to new event end date-time.
     */
    public void setTo(LocalDateTime to) {
        updateTags(List.of(createDateTag("to", to)));
    }

    /** Validates both critical event tags and their date range. */
    @Override
    protected void validateTags(List<Tag> candidate) {
        Optional<Tag> fromTag = findTag(candidate, "from");
        Optional<Tag> toTag = findTag(candidate, "to");

        if (fromTag.isEmpty() || toTag.isEmpty()
                || fromTag.get().value().isEmpty() || toTag.get().value().isEmpty()) {
            throw new IllegalArgumentException("Event requires /from and /to values.");
        }

        LocalDateTime from = parseDateTime(fromTag.get());
        LocalDateTime to = parseDateTime(toTag.get());
        validateDateRange(from, to);
    }

    /** Reads and parses one of the event's date-time tags. */
    private LocalDateTime getDateTimeValue(String tagName) {
        Tag tag = getTag(tagName).orElseThrow(() ->
                new IllegalStateException("Event is missing its " + tagName + " tag."));
        return parseDateTime(tag);
    }

    /** Parses a date-time tag after checking that it has a body. */
    private static LocalDateTime parseDateTime(Tag tag) {
        String value = tag.value().orElseThrow(() ->
                new IllegalStateException("Event tag has no date-time value."));
        return DateTimeParser.createLocalDateTime(value);
    }

    /** Finds a tag by its normalized name in a candidate list. */
    private static Optional<Tag> findTag(List<Tag> candidate, String name) {
        return candidate.stream()
                .filter(tag -> tag.name().equals(name))
                .findFirst();
    }

    /** Creates a date-time tag after checking its value. */
    private static Tag createDateTag(String name, LocalDateTime value) {
        return new Tag(name, DateTimeParser.formatLocalDateTime(
                Objects.requireNonNull(value, "Event date-time cannot be null.")));
    }
}
