package erina.task;

import java.util.ArrayList;
import java.util.List;

/**
 * A single item on the user's list.
 *
 * <p>A task is a description together with whether it has been completed.
 * Keeping both in one object, rather than parallel lists of strings and
 * booleans, means the two can never fall out of step.
 *
 * <p>This class is abstract because every task the user can create is one of
 * the specific kinds below it ({@link Todo}, {@link Deadline}, {@link Event}).
 * The description and completion flag are handled once, here; each kind
 * supplies only what makes it different: its {@link #getTypeCode() type code}
 * and any {@link #getExtraSaveFields() extra fields} it needs to save.
 */
public abstract class Task {
    /** Separator between the fields of one saved task. */
    public static final String SAVE_SEPARATOR = " | ";

    /** What the user wants to do. Fixed once the task is created. */
    private final String description;

    /** Whether the user has marked this task as completed. */
    private boolean isDone;

    /**
     * Creates a task that is not yet done.
     *
     * @param description what the user wants to do
     */
    public Task(String description) {
        // Callers always pass real text; a missing description is a bug, not user error.
        assert description != null : "task needs a description";
        this.description = description;
        this.isDone = false;
    }

    /** Marks this task as completed. */
    public void markAsDone() {
        this.isDone = true;
    }

    /** Marks this task as not yet completed. */
    public void markAsNotDone() {
        this.isDone = false;
    }

    /**
     * Returns whether the user has marked this task as completed.
     *
     * @return {@code true} if this task is done
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Returns what the user wants to do.
     *
     * @return the description, as the user typed it
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the single letter that identifies this kind of task, used both
     * in the display form ({@code [T]}) and as the first field of the save
     * form.
     *
     * @return {@code "T"}, {@code "D"} or {@code "E"}
     */
    public abstract String getTypeCode();

    /**
     * Returns the fields, beyond the description, that this kind of task
     * needs in order to be rebuilt from the save file.
     *
     * <p>The default is none, which is right for a plain to-do. Kinds with
     * extra detail, such as a due date, override this.
     *
     * @return the extra fields, in the order they are saved
     */
    protected List<String> getExtraSaveFields() {
        return List.of();
    }

    /**
     * Returns this task in the form it is written to the save file, for
     * example {@code T | 1 | read book}.
     *
     * <p>The save form is machine-facing and stable; {@link #toString()} is
     * user-facing and free to change, which is why they are separate. Both
     * are assembled here so that the layout is decided in one place, and a
     * new kind of task only has to say what its extra fields are.
     *
     * @return this task as one line of the save file
     */
    public String toSaveString() {
        List<String> fields = new ArrayList<>();
        fields.add(getTypeCode());
        fields.add(isDone ? "1" : "0");
        fields.add(description);
        fields.addAll(getExtraSaveFields());
        return String.join(SAVE_SEPARATOR, fields);
    }

    /**
     * Returns whether the other task has the same details as this one: the
     * same kind, the same description ignoring case, and the same extra
     * fields such as dates.
     *
     * <p>Whether either task is done is ignored, since that is progress on a
     * task rather than part of what the task is. {@code equals} is
     * deliberately not overridden: two entries with the same details are
     * still two separate entries in a list.
     *
     * @param other the task to compare with
     * @return {@code true} if both describe the same task
     */
    public boolean isSameTask(Task other) {
        return getTypeCode().equals(other.getTypeCode())
                && description.equalsIgnoreCase(other.description)
                && getExtraSaveFields().equals(other.getExtraSaveFields());
    }

    /**
     * Returns the symbol shown inside the status box.
     *
     * @return {@code "X"} if this task is done, a single space otherwise
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /**
     * Returns the type box, status box and description shared by every kind
     * of task, for example {@code [T][X] read book}.
     *
     * <p>Subclasses append any extra detail of their own.
     */
    @Override
    public String toString() {
        return "[" + getTypeCode() + "][" + getStatusIcon() + "] " + description;
    }
}
