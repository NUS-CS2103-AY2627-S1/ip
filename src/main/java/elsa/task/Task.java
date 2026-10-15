package elsa.task;

import java.time.LocalDate;
import java.util.Arrays;

/**
 * A single task in the user's list, holding its description and whether it is done.
 * Kinds of task that carry extra information, such as {@link Deadline}, extend this class.
 */
public class Task {
    /**
     * The text describing what is to be done.
     * Protected rather than private so that the kinds of task that extend this
     * class (todos, deadlines, events) can reuse it.
     */
    protected String description;

    /**
     * Whether the task has been completed.
     * Protected for the same reason as {@link #description}.
     */
    protected boolean isDone;

    /**
     * Creates a task that is not done yet.
     *
     * @param description the text describing what is to be done
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns the single character shown inside the status brackets.
     *
     * @return "X" when the task is done, a space otherwise
     */
    public String getStatusIcon() {
        return (isDone ? "X" : " "); // mark done task with X
    }

    /** Records this task as completed. */
    public void markAsDone() {
        this.isDone = true;
    }

    /** Records this task as not completed after all. */
    public void markAsNotDone() {
        this.isDone = false;
    }

    /**
     * Returns whether this task describes the same thing as another task.
     *
     * <p>Two tasks are the same when they are of the same kind and describe the
     * same thing, whether or not either has been marked as done: adding again a
     * task that is already ticked off is a slip just as adding an unfinished one
     * twice is.
     *
     * <p>This is deliberately not equals. Java expects equals to agree with
     * hashCode and to weigh every field, and this comparison ignores one on
     * purpose, so giving it a name of its own leaves that expectation intact.
     * The kinds of task that carry extra fields widen the comparison to cover
     * them.
     *
     * @param other the task to compare against
     * @return true if both are of the same kind and describe the same thing
     */
    public boolean isSameTask(Task other) {
        return getClass() == other.getClass() && description.equals(other.description);
    }

    /**
     * Returns whether this task falls on the given date.
     * A plain task carries no date, so the answer is always no. The kinds of task
     * that do carry dates override this and answer for themselves, in the same way
     * they each write themselves differently in toString().
     *
     * @param date the date being asked about
     * @return true if this task falls on that date
     */
    public boolean occursOn(LocalDate date) {
        return false;
    }

    /**
     * Returns whether this task's description contains any of the given keywords.
     *
     * <p>Any rather than all, so that "find book milk" answers "show me anything
     * about either", which is what someone scanning a list wants. Asking for all
     * of them would narrow the list instead, and a user who wants that can search
     * for the narrower word on its own.
     *
     * <p>Only the description is searched, not the type marker or the dates, so
     * that "find D" looks for the letter D in what the user wrote rather than
     * returning every deadline. The search ignores case, because someone looking
     * for a task they wrote themselves should not have to remember how they
     * capitalised it.
     *
     * <p>Every kind of task carries a description, so unlike {@link #occursOn}
     * this is answered the same way for all of them and none of them override it.
     *
     * @param keywords the texts to look for, one or more
     * @return true if the description contains at least one of them
     */
    public boolean matches(String... keywords) {
        String searchable = description.toLowerCase();
        // anyMatch stops at the first keyword that fits, and answers false for a
        // stream with nothing in it, which is the right answer here too: a task
        // cannot contain a keyword nobody named.
        return Arrays.stream(keywords)
                .anyMatch(keyword -> searchable.contains(keyword.toLowerCase()));
    }

    /**
     * Returns the task as it should appear to the user, for example "[X] read book".
     * Subclasses add their own type marker and extra information around this.
     *
     * @return the status icon in brackets, followed by the description
     */
    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }

    /**
     * Returns the task as one line of the data file, for example "1 | read book".
     * Only the parts every task has are written here; each kind of task adds its
     * own type letter and extra fields around this, the same way toString() does.
     *
     * @return whether the task is done, as 1 or 0, followed by its description
     */
    public String toSaveFormat() {
        // The markers and the separator are named in TaskFormat, which also reads
        // them back, so the two directions cannot drift apart.
        return (isDone ? TaskFormat.DONE : TaskFormat.NOT_DONE)
                + TaskFormat.SEPARATOR + description;
    }
}
