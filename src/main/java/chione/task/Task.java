package chione.task;

import java.time.LocalDate;
import java.util.Locale;

/**
 * A single item in the user's task list.
 *
 * <p>A task knows its own description and whether it has been completed, and
 * knows how to render itself for display. Keeping that knowledge here rather
 * than in {@link chione.Chione Chione} means the chatbot only has to decide <em>when</em> to
 * show a task, not <em>how</em> to format one.
 */
public class Task {
    /**
     * Separates the fields of one saved line, e.g. {@code "T | 1 | read book"}.
     *
     * <p>A bar is used rather than a comma, because a comma is far more likely to
     * turn up inside a description the user typed.
     *
     * <p>It is named here, beside the method that writes it, so that
     * {@link chione.Storage Storage} can split a line back apart on the same
     * separator instead of spelling it out a second time.
     */
    public static final String SEPARATOR = " | ";

    /** The done field of a saved line, for a task the user has completed. */
    public static final String DONE_FLAG = "1";

    /** The done field of a saved line, for a task the user has not completed. */
    public static final String NOT_DONE_FLAG = "0";

    /**
     * What the user wants to do, e.g. {@code "read book"}.
     *
     * <p>{@code protected} rather than {@code private} so that the subclasses
     * added in Level-4 (Todo, Deadline, Event) can read it directly.
     */
    protected String description;

    /** Whether the user has marked this task as completed. */
    protected boolean isDone;

    /**
     * Creates a task that starts out not done.
     *
     * @param description what the user wants to do
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns the single character shown inside the status brackets.
     *
     * @return {@code "X"} if this task is done, or a space if it is not
     */
    public String getStatusIcon() {
        return isDone ? "X" : " "; // a done task is marked with X
    }

    /** Marks this task as completed. */
    public void markAsDone() {
        this.isDone = true;
    }

    /** Marks this task as not completed, undoing a previous {@link #markAsDone()}. */
    public void markAsNotDone() {
        this.isDone = false;
    }

    /**
     * Returns this task in the form shown to the user, e.g. {@code "[X] read book"}.
     *
     * <p>Overriding {@code toString()} means a task can be dropped straight into
     * string concatenation without the caller building the format by hand.
     */
    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }

    /**
     * Reports whether this task falls on the given day.
     *
     * <p>A plain task has no date, so the answer here is always no. Deadlines and
     * events override this with their own answer. Asking the task itself, rather
     * than testing its type from outside, means the {@code on} command works for
     * any task type added later without being changed.
     *
     * @param date the day being asked about
     * @return {@code true} if this task falls on that day
     */
    public boolean occursOn(LocalDate date) {
        return false;
    }

    /**
     * Reports whether this task's description contains the given word.
     *
     * <p>The comparison ignores case, so searching for "Book" finds "read book".
     * The locale is fixed rather than left to the machine, so the same search
     * gives the same answer everywhere.
     *
     * @param keyword the word being searched for
     * @return {@code true} if the description contains it
     */
    public boolean hasKeyword(String keyword) {
        String lowerCaseDescription = description.toLowerCase(Locale.ENGLISH);
        return lowerCaseDescription.contains(keyword.toLowerCase(Locale.ENGLISH));
    }

    /**
     * Reports whether this task is a repeat of another one.
     *
     * <p>Sameness here is deliberately weaker than {@code equals}: two tasks
     * are repeats when they are the same kind of task, about the same thing
     * and, for dated tasks, at the same moment. Whether either one is done is
     * ignored, because re-adding a task you already finished is still a
     * repeat. {@code equals} is left alone on purpose: an {@code equals} that
     * ignores one of the object's own fields is a trap for any collection that
     * relies on it, whereas a method with this name says exactly what it tests.
     *
     * <p>The description is compared ignoring case, as {@link #hasKeyword}
     * does, so "Read book" and "read book" count as the same task.
     *
     * @param other the task being compared against
     * @return {@code true} if adding this task would repeat {@code other}
     */
    public boolean isDuplicateOf(Task other) {
        return getClass() == other.getClass()
                && description.equalsIgnoreCase(other.description);
    }

    /**
     * Returns this task as one line of the save file, e.g. {@code "1 | read book"}.
     *
     * <p>Each subclass prefixes its own type letter and appends its own extra
     * fields, so every class writes exactly the part it knows about and no single
     * method has to know about all three task types.
     *
     * @return the part every task shares: the done flag and the description
     */
    public String toSaveFormat() {
        return (isDone ? DONE_FLAG : NOT_DONE_FLAG) + SEPARATOR + description;
    }
}
