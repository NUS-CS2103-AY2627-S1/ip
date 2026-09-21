package peter.task;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Represents a task tracked by Peter.
 */
public abstract class Task {
    /** Separates the fields of a task within its line in the data file. */
    public static final String FIELD_DELIMITER = " | ";

    /** Data-file status flag marking a completed task. */
    public static final String STATUS_FLAG_DONE = "1";

    /** Data-file status flag marking a task that is not yet completed. */
    public static final String STATUS_FLAG_NOT_DONE = "0";

    /**
     * What the task says, as the user typed it. It must never contain
     * {@link #FIELD_DELIMITER}, which would split it across two fields when
     * the task is saved.
     */
    private final String description;

    /** Whether the task has been completed. */
    private boolean isDone;

    /** Fixed task type, used to pick the icon shown in the task list. */
    private final TaskType type;

    /**
     * Creates a task that starts out incomplete.
     *
     * @param description what the task says.
     * @param type kind of task being created.
     */
    public Task(String description, TaskType type) {
        // Parser and Storage both reject a blank description and one holding
        // the data-file delimiter before creating a task, so these are the
        // invariants promised by the fields above rather than input checks.
        // A task that broke them would print as an empty row, or save a line
        // that the next startup would read back as a corrupt record.
        assert description != null && !description.isBlank() : "task description must not be blank";
        assert !description.contains(" | ") : "task description must not contain the storage delimiter";
        assert type != null : "every task must have a type to display an icon for";

        this.description = description;
        this.type = type;
        this.isDone = false;
    }

    /**
     * Returns the one-letter icon identifying this task's type, such as
     * {@code T} for a todo.
     */
    public String getTaskTypeIcon() {
        return type.getIcon();
    }

    /**
     * Returns {@code X} when the task is done and a single space otherwise, so
     * that {@code [ ]} and {@code [X]} stay the same width in the task list.
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /**
     * Returns what the task says.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns whether the task has been completed.
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Returns the schedule to display after the description, including its
     * leading space and brackets, or an empty string for a task with no
     * schedule.
     *
     * @return displayable schedule text.
     */
    public abstract String getScheduleDetails();

    /**
     * Converts this task into the line format used in the data file.
     *
     * <p>Every task line opens with the same three fields, so they are built
     * here rather than by each subclass; a subclass supplies only the
     * schedule fields that follow.
     *
     * @return the serialized task.
     */
    public final String toDataString() {
        List<String> fields = new ArrayList<>(List.of(getTaskTypeIcon(),
                isDone ? STATUS_FLAG_DONE : STATUS_FLAG_NOT_DONE, description));
        fields.addAll(getScheduleDataFields());
        return String.join(FIELD_DELIMITER, fields);
    }

    /**
     * Returns this task's schedule as the data-file fields that follow the
     * description, in the order they are stored.
     *
     * <p>Dates are written in ISO form so that they can be read back exactly,
     * rather than in the friendlier display format.
     *
     * @return schedule fields, which are none for a task with no schedule.
     */
    protected abstract List<String> getScheduleDataFields();

    /**
     * Checks whether this task is scheduled on a given date. Tasks without a
     * schedule return {@code false}; scheduled task types override this method.
     *
     * @param date date to check.
     * @return whether the task occurs on the date.
     */
    public boolean occursOn(LocalDate date) {
        return false;
    }

    /**
     * Returns whether this task's description contains the given keyword,
     * ignoring case.
     *
     * <p>Only the description is searched, so a keyword matching a task's
     * dates or type icon does not count. Matching is on any substring rather
     * than whole words, so {@code book} also finds {@code bookshop}.
     *
     * @param keyword keyword to look for.
     * @return whether the description contains the keyword.
     */
    public boolean hasKeyword(String keyword) {
        return description.toLowerCase(Locale.ROOT)
                .contains(keyword.toLowerCase(Locale.ROOT));
    }

    /**
     * Marks the task as completed. Marking an already-completed task has no
     * further effect.
     */
    public void markAsDone() {
        isDone = true;
    }

    /**
     * Marks the task as not yet completed. Unmarking an incomplete task has no
     * further effect.
     */
    public void unmarkAsDone() {
        isDone = false;
    }
}
