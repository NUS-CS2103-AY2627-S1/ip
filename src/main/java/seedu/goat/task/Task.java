package seedu.goat.task;

import java.time.LocalDate;
import java.util.Locale;

/**
 * A single item on the user's task list.
 * <p>
 * A task pairs a description with whether it has been completed. Keeping both in one
 * object replaces the parallel arrays used before this increment, so a task's text and
 * its status can no longer drift apart.
 */
public abstract class Task {

    /**
     * What the user wants to do. Protected rather than private so that the task-type
     * subclasses added in Level-4 can reuse it when building their own display text.
     */
    protected final String description;

    /** Whether the user has marked this task as completed. */
    protected boolean isDone;

    /**
     * Creates a task that starts out not done.
     *
     * @param description what the user wants to do.
     */
    public Task(String description) {
        assert description != null && !description.isBlank() : "Blank descriptions are rejected first";
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns whether this task is completed.
     *
     * @return true if completed.
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Sets whether this task is completed.
     *
     * @param isDone whether the task is completed.
     */
    public void setDone(boolean isDone) {
        this.isDone = isDone;
    }

    /** Marks this task as completed. */
    public void markAsDone() {
        this.isDone = true;
    }

    /** Marks this task as not completed. */
    public void markAsNotDone() {
        this.isDone = false;
    }

    /**
     * Returns the completion marker shown in listings.
     *
     * @return {@code [X]} if this task is done, {@code [ ]} otherwise.
     */
    public String getStatusIcon() {
        return isDone ? "[X]" : "[ ]";
    }

    /**
     * Returns whether this task falls on a given date.
     * <p>
     * A plain task carries no date, so the base answer is no. Subclasses that do carry
     * dates override this, which keeps callers from having to test a task's type.
     *
     * @param date the date being asked about.
     * @return true if this task falls on that date.
     */
    public boolean isOn(LocalDate date) {
        return false;
    }

    /**
     * Returns whether this task's description contains a keyword.
     * <p>
     * Matching is case insensitive, because a user searching for a word should not have
     * to remember how they capitalized it when adding the task.
     *
     * @param keyword the text to look for.
     * @return true if the description contains the keyword.
     */
    public boolean hasKeyword(String keyword) {
        return description.toLowerCase(Locale.ROOT).contains(keyword.toLowerCase(Locale.ROOT));
    }

    /**
     * Returns whether another task duplicates this one.
     * <p>
     * Two tasks match when they are the same kind of task and describe the same thing.
     * Descriptions are compared with spacing and capitalization set aside, so that
     * "Read  Book" is recognized as the task the user already has. Subclasses that
     * carry dates narrow this further, since the same words on a different day are a
     * different commitment. Completion is not consulted: having finished a task does
     * not entitle the list to hold a second copy of it.
     *
     * @param other the task to compare against, never null.
     * @return true if the two tasks are duplicates of one another.
     */
    public boolean isDuplicateOf(Task other) {
        assert other != null : "Callers compare against tasks already in the list";
        return getClass() == other.getClass()
                && normalize(description).equals(normalize(other.description));
    }

    /**
     * Returns a description reduced to the form used for comparing two of them.
     *
     * @param text the description as the user typed it.
     * @return the description trimmed, with runs of spaces collapsed, in lower case.
     */
    private static String normalize(String text) {
        return text.trim().replaceAll("\\s+", " ").toLowerCase(Locale.ROOT);
    }

    /**
     * Returns the completion flag as it is written to the save file.
     *
     * @return {@code "1"} if this task is done, {@code "0"} otherwise.
     */
    protected String getFileStatus() {
        return isDone ? "1" : "0";
    }

    /**
     * Returns this task encoded as a single line of the save file.
     * <p>
     * Declared here and implemented by each subclass so that Storage never has to test
     * a task's runtime type to work out which fields to write.
     *
     * @return the encoded task, such as {@code D | 0 | return book | Sunday}
     */
    public abstract String toFileString();

    @Override
    public String toString() {
        return getStatusIcon() + " " + description;
    }
}
