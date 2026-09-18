package myriad.task;

import java.time.LocalDateTime;
import java.util.Locale;

/**
 * Represents a task with a description and a done/not-done status.
 */
public abstract class Task {
    /** Whether the user has marked this task done. Starts false. */
    private boolean isDone;

    /** What the task is, exactly as the user typed it. */
    private final String description;

    /**
     * Creates a task with the given description. The task starts not done.
     *
     * @param description what the task is.
     */
    public Task(String description) {
        // Only non-null is assumed: a hand-edited save file may hold an empty description.
        assert description != null : "Parser and Storage always pass a description string";
        this.description = description;
    }

    /**
     * Returns whether this task is done.
     *
     * @return whether this task is done.
     */
    public boolean isDone() {
        return this.isDone;
    }

    /**
     * Sets whether this task is done. Used both by the {@code mark} and
     * {@code unmark} commands and by {@code Storage} when restoring a task's
     * saved status.
     *
     * @param isDone true to mark done, false to mark not done yet.
     */
    public void setDone(boolean isDone) {
        this.isDone = isDone;
    }

    /**
     * Returns this task's data as a {@code |}-delimited line for saving to
     * disk, e.g. {@code 1 | read book} for a done task. Deliberately separate
     * from {@link #toString()}: that method's bracket-and-icon format is for
     * display and may change independently, whereas this format needs to
     * stay stable and parseable so a saved line can be read back into a
     * {@code Task} later. Subclasses prepend their type letter and any extra
     * fields.
     *
     * @return one line of save format, without a trailing newline.
     */
    public String toSaveFormat() {
        return String.format("%d | %s", isDone ? 1 : 0, description);
    }

    /**
     * Returns whether this task occurs during {@code query}, for the
     * {@code show} command. A date-only query stands for its whole day, so it
     * matches anything overlapping that day. Defined once here in terms of
     * {@link #overlaps}, and final, so the {@code show} and {@code stats}
     * commands can never disagree about when a task takes place.
     *
     * @param query the date, or date and time, being asked about.
     * @return whether this task overlaps {@code query}; always false for a {@link ToDo}.
     */
    public final boolean occursDuring(TaskDateTime query) {
        return overlaps(query.rangeStart(), query.rangeEnd());
    }

    /**
     * Returns whether this task overlaps the period from {@code from} to
     * {@code to}, the check behind both the {@code show} command and the
     * {@code stats} command's due-soon section. Tasks with no date of their
     * own ({@link ToDo}) never match, hence the default false here;
     * {@link Deadline} and {@link Event} override this with their own
     * date-based check.
     *
     * @param from the start of the period.
     * @param to   the end of the period.
     * @return whether this task overlaps that period; always false for a plain {@code Task}.
     */
    public boolean overlaps(LocalDateTime from, LocalDateTime to) {
        return false;
    }

    /**
     * Returns whether this task's description contains {@code keyword},
     * ignoring case, for the {@code find} command. Only the description is
     * searched, not the type marker or any dates, so {@code find 2019} won't
     * match a deadline merely because it falls in that year.
     *
     * Both sides are lower-cased with {@link Locale#ROOT} rather than the
     * machine's default locale, whose rules can differ: in Turkish, for
     * example, "I" lower-cases to a dotless "ı", so {@code find TITLE} would
     * miss "title".
     *
     * @param keyword the text to look for within the description.
     * @return whether the description contains {@code keyword}, ignoring case.
     */
    public boolean descriptionContains(String keyword) {
        return description.toLowerCase(Locale.ROOT).contains(keyword.toLowerCase(Locale.ROOT));
    }

    /**
     * Returns whether {@code other} describes the same task as this one: the
     * same type and exactly the same description. Subclasses with dates
     * extend this to compare them too. Whether either task is done is
     * ignored, since marking a task done does not make it a different task.
     *
     * This is a separate method rather than an override of
     * {@link Object#equals}, because a task's done status can change, and
     * equality that ignored part of a mutable object's state would surprise
     * anyone putting tasks in a set or map.
     *
     * @param other the task to compare with.
     * @return whether the two tasks have the same type and details.
     */
    public boolean hasSameDetails(Task other) {
        return getType() == other.getType() && description.equals(other.description);
    }

    /**
     * Returns which kind of task this is, e.g. {@link TaskType#TODO} for a
     * {@link ToDo}.
     *
     * @return this task's type.
     */
    public abstract TaskType getType();

    /**
     * Returns the task's status and description, e.g. {@code [X] read book}
     * if done, or {@code [ ] read book} if not done.
     */
    @Override
    public String toString() {
        if (isDone) {
            return String.format("[X] %s", this.description);
        } else {
            return String.format("[ ] %s", this.description);
        }
    }
}
