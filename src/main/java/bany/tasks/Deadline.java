package bany.tasks;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import bany.parsers.DateTimeParser;
import bany.tags.Tag;

/** Represents a task that must be completed by a specified date-time. */
public class Deadline extends Task {

    /**
     * Creates a deadline from a formatted date-time string.
     *
     * @param description deadline description.
     * @param by deadline in {@code dd-MM-yyyy HH:mm} format.
     * @param id task ID.
     */
    public Deadline(String description, String by, int id) {
        this(description, DateTimeParser.createLocalDateTime(by), id);
    }

    /**
     * Creates a deadline from a typed date-time value.
     *
     * @param description deadline description.
     * @param by deadline date-time.
     * @param id task ID.
     */
    public Deadline(String description, LocalDateTime by, int id) {
        super(id, description, List.of(createByTag(by)));
    }

    /**
     * Returns the deadline in the user-facing format used by the task file.
     *
     * @return formatted deadline date-time.
     */
    public String getBy() {
        return DateTimeParser.formatLocalDateTime(getByDateTime());
    }

    /**
     * Returns the typed deadline value for date-time operations.
     *
     * @return deadline date-time.
     */
    public LocalDateTime getByDateTime() {
        Tag byTag = getTag("by").orElseThrow(() ->
                new IllegalStateException("Deadline is missing its by tag."));
        String by = byTag.value().orElseThrow(() ->
                new IllegalStateException("Deadline by tag has no value."));
        return DateTimeParser.createLocalDateTime(by);
    }

    /**
     * Returns the date information appended to this task's display text.
     *
     * @return formatted deadline display text.
     */
    public String getDateString() {
        return " (by: " + getBy() + ")";
    }

    /**
     * Returns the full task type name.
     *
     * @return {@code DEADLINE}.
     */
    @Override
    public String getType() {
        return TaskType.DEADLINE.getType();
    }

    /**
     * Returns the names of the tags that define a deadline's schedule.
     *
     * @return list containing {@code by}.
     */
    @Override
    public List<String> getCriticalTags() {
        return List.of("by");
    }

    /**
     * Returns the abbreviated type marker.
     *
     * @return {@code D}.
     */
    @Override
    public String getTypeShort() {
        return TaskType.DEADLINE.getTypeShort();
    }

    /**
     * Replaces the deadline date-time through the common tag storage.
     *
     * @param by new deadline date-time.
     */
    public void setBy(LocalDateTime by) {
        updateTags(List.of(createByTag(by)));
    }

    /** Validates the critical deadline tag after a proposed update. */
    @Override
    protected void validateTags(List<Tag> candidate) {
        Optional<Tag> byTag = candidate.stream()
                .filter(tag -> tag.name().equals("by"))
                .findFirst();
        if (byTag.isEmpty() || byTag.get().value().isEmpty()) {
            throw new IllegalArgumentException("Deadline requires a /by value.");
        }
        try {
            DateTimeParser.createLocalDateTime(byTag.get().value().get());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Deadline /by value is invalid.", e);
        }
    }

    /** Creates the initial deadline tag after validating its value. */
    private static Tag createByTag(LocalDateTime by) {
        return new Tag("by", DateTimeParser.formatLocalDateTime(
                Objects.requireNonNull(by, "Deadline date-time cannot be null.")));
    }
}
