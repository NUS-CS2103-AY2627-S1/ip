package surveyprogram.object;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents a task that becomes available after a time or another task.
 */
public class AfterTask extends Item {
    private static final DateTimeFormatter OUTPUT_FORMATTER =
            DateTimeFormatter.ofPattern("MMM dd yyyy, h:mm a", Locale.ENGLISH);

    private final LocalDateTime releaseAt;
    private final Item parent;

    /**
     * Creates a task that becomes available at a specified date-time.
     *
     * @param description task description
     * @param releaseAt earliest date-time at which the task can be changed
     */
    public AfterTask(String description, LocalDateTime releaseAt) {
        this(null, description, releaseAt, null);
    }

    /**
     * Creates a task that becomes available after its parent is complete.
     *
     * @param description task description
     * @param parent task that must be completed first
     */
    public AfterTask(String description, Item parent) {
        this(null, description, null, parent);
    }

    /**
     * Reconstructs a time-dependent task with a stable identifier.
     *
     * @param id stable task identifier
     * @param description task description
     * @param releaseAt earliest date-time at which the task can be changed
     */
    public AfterTask(String id, String description, LocalDateTime releaseAt) {
        this(id, description, releaseAt, null);
    }

    /**
     * Reconstructs a parent-dependent task with a stable identifier.
     *
     * @param id stable task identifier
     * @param description task description
     * @param parent task that must be completed first
     */
    public AfterTask(String id, String description, Item parent) {
        this(id, description, null, parent);
    }

    private AfterTask(String id, String description, LocalDateTime releaseAt, Item parent) {
        super(id == null ? java.util.UUID.randomUUID().toString() : id, description);
        assert (releaseAt == null) != (parent == null)
                : "An after-task must have exactly one availability condition";
        this.releaseAt = releaseAt;
        this.parent = parent;
    }

    /**
     * Returns whether the task's availability condition has been met.
     *
     * @param now current date-time
     * @return {@code true} when the release time has arrived or the parent is done
     */
    public boolean isAvailable(LocalDateTime now) {
        return releaseAt == null ? parent.isDone() : !now.isBefore(releaseAt);
    }

    /** Returns whether this task depends on another task. */
    public boolean hasParent() {
        return parent != null;
    }

    /** Returns the parent task, or {@code null} for a time-dependent task. */
    public Item getParent() {
        return parent;
    }

    /** Returns the release date-time, or {@code null} for a parent-dependent task. */
    public LocalDateTime getReleaseAt() {
        return releaseAt;
    }

    /** Returns the display form, including the availability condition. */
    @Override
    public String toString() {
        String lockMarker = isAvailable(LocalDateTime.now()) ? "" : "[L]";
        String condition = hasParent()
                ? " (after task: " + parent.getName() + ")"
                : " (after: " + releaseAt.format(OUTPUT_FORMATTER).replace("12:00 AM", "0:00") + ")";
        return "[A]" + lockMarker + super.toString() + condition;
    }
}
