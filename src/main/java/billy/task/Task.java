package billy.task;

import java.time.LocalDate;
import java.util.Locale;

/**
 * Represents a single task in the user's list.
 *
 * <p>A task bundles its description together with whether it has been completed,
 * so the two can never fall out of step with each other.
 *
 * <p>A task knows two ways to write itself out: {@link #toString()} for the
 * screen, and {@link #toSaveFormat()} for the file it is stored in between runs.
 */
public class Task {

    /**
     * Separates the fields of one task in the save file, e.g. {@code T | 1 | read book}.
     *
     * <p>Spaces around the bar make a saved file easy to read by eye;
     * {@link billy.storage.Storage Storage} trims them away again when reading.
     */
    public static final String FIELD_SEPARATOR = " | ";

    /** How a finished task is written in the save file. */
    public static final String FLAG_DONE = "1";

    /** How an unfinished task is written in the save file. */
    public static final String FLAG_NOT_DONE = "0";

    /** What the user wants to do, exactly as they typed it. */
    protected String description;

    /** Whether the task has been completed. */
    protected boolean isDone;

    /**
     * Creates a task that is not done yet.
     *
     * @param description what the user wants to do
     */
    public Task(String description) {
        // Parser refuses an empty description and Storage refuses an empty field,
        // so a blank one here means a task was built from somewhere that skipped
        // those checks rather than that the user typed nothing.
        assert description != null && !description.isBlank()
                : "A task must have a description, but was given: " + description;
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns whether another task describes the same thing as this one.
     *
     * <p>Sameness is what the user typed, not what Billy has done with it: the
     * type and the description, compared without regard to capitalization,
     * since nobody retypes a description with the same capitals by accident.
     * Whether either task is done is deliberately not part of it, because a
     * finished task and a new one with the same wording are a chore being
     * repeated rather than a mistake.
     *
     * <p>Not {@code equals}, on purpose. Two tasks that describe the same thing
     * are still two separate entries in the list, and the user may legitimately
     * want both; this answers "did you mean to type that twice?", which is a
     * weaker question than identity.
     *
     * <p>Subclasses that carry dates widen this, since two deadlines worded
     * alike but due on different days are not the same task.
     *
     * @param other the task to compare against
     * @return whether the two describe the same thing
     */
    public boolean isSameTask(Task other) {
        return other != null
                && getClass() == other.getClass()
                && description.equalsIgnoreCase(other.description);
    }

    /**
     * Returns whether this task has been completed.
     *
     * <p>Asked for by the commands that remark on the state of the whole list,
     * which need the flag itself rather than the symbol standing for it. Reading
     * {@link #getStatusIcon()} and comparing it to {@code "X"} would work, but
     * would tie those commands to how a task is drawn.
     *
     * @return whether the task is done
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Returns the symbol shown inside the status box.
     *
     * @return {@code "X"} if the task is done, or a single space if it is not
     */
    public String getStatusIcon() {
        return (isDone ? "X" : " "); // mark done task with X
    }

    /** Marks this task as completed. */
    public void markAsDone() {
        this.isDone = true;
    }

    /** Marks this task as not completed yet. */
    public void markAsNotDone() {
        this.isDone = false;
    }

    /**
     * Returns whether this task has anything to do with a particular day.
     *
     * <p>A plain task carries no date, so the answer is no. The kinds of task
     * that do carry dates answer for themselves, which lets Billy search the
     * list without having to ask what type each task is.
     *
     * @param day the day being asked about
     * @return whether this task falls on that day
     */
    public boolean occursOn(LocalDate day) {
        return false;
    }

    /**
     * Returns whether this task's description mentions a word.
     *
     * <p>Capitalization is ignored, as it is for command keywords, so looking
     * for {@code Book} finds {@code read book}. Any part of a word counts, so
     * {@code book} also finds {@code textbook}: insisting on whole words would
     * mean the user had to guess how the task was worded to find it again.
     *
     * <p>Only the description is searched. A date is asked about with
     * {@code on}, which understands what a day is, rather than by matching the
     * text a date happens to be printed as.
     *
     * <p>The locale is fixed so that a task is found the same way on every
     * computer, rather than depending on the language the machine is set to.
     *
     * @param keyword the word to look for
     * @return whether the description contains it
     */
    public boolean descriptionContains(String keyword) {
        return description.toLowerCase(Locale.ENGLISH)
                .contains(keyword.toLowerCase(Locale.ENGLISH));
    }

    /**
     * Returns the parts of this task that every task has, written as they appear
     * in the save file: whether it is done, then its description.
     *
     * <p>Subclasses put their own type letter in front and add their own extra
     * fields behind, mirroring the way {@link #toString()} is built up. Keeping
     * each class responsible for its own fields means adding a new kind of task
     * never means editing the ones that already exist.
     *
     * @return e.g. {@code 1 | read book}
     */
    public String toSaveFormat() {
        return (isDone ? FLAG_DONE : FLAG_NOT_DONE) + FIELD_SEPARATOR + description;
    }

    /**
     * Returns this task as a status box followed by its description,
     * e.g. {@code [X] read book}.
     */
    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }
}
