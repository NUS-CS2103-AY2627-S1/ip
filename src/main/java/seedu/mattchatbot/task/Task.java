package seedu.mattchatbot.task;

import java.time.LocalDate;

import seedu.mattchatbot.MattChatBotException;

/**
 * A single item in the user's list, together with whether it has been done.
 * <p>
 * This is the parent of the three concrete task types introduced by the
 * A-Inheritance extension. It is abstract because every real task is a todo,
 * a deadline or an event: a task with no type would have nothing to show in
 * its type box.
 */
public abstract class Task {

    /**
     * What goes between two fields of a task in the save file.
     * <p>
     * Declared here, next to the encoding it belongs to, because every task
     * type appends its own fields with it and Storage reads them back.
     */
    public static final String FIELD_SEPARATOR = " | ";

    /** What the user asked to be reminded of. */
    protected String description;

    /** Whether the user has marked this task as done. */
    protected boolean isDone;

    /**
     * Creates a task that starts off as not done.
     *
     * @param description what the user typed
     */
    public Task(String description) {
        // Parser rejects an empty description and Storage rejects a blank one
        // in the save file, so a task with nothing to show should be
        // impossible to build; it would display as an empty "[T][ ] " line.
        assert description != null && !description.isBlank() : "a task always has a description";
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns the single character shown in the type box, e.g. "T" for a todo.
     * <p>
     * Each subclass supplies its own, so adding a new task type does not mean
     * editing a type-checking branch elsewhere in the code.
     *
     * @return the one-character type marker
     */
    public abstract String getTypeIcon();

    /**
     * Returns what this kind of task is called, e.g. {@code deadline}, for use
     * in messages written to the user.
     *
     * @return the type's name in lower case
     */
    public abstract String getTypeName();

    /**
     * Applies an update to this task, rejecting parts it does not have.
     * <p>
     * A plain task has only a description, so this base version turns down
     * every date. The types that carry dates override it to accept their own
     * and turn down the rest. Asking the task itself keeps the rules about
     * which markers suit which type with the type, rather than in an
     * instanceof check somewhere else that would grow with every new type.
     * <p>
     * Every override rejects before it changes anything, so a refused update
     * leaves the task exactly as it was.
     *
     * @param update the parts to change
     * @throws MattChatBotException if the update names a part this task lacks
     */
    public void applyUpdate(TaskUpdate update) throws MattChatBotException {
        if (update.hasBy()) {
            throw new MattChatBotException("A " + getTypeName()
                    + " has no due date, so there is no /by to amend.");
        }
        if (update.hasFrom() || update.hasTo()) {
            throw new MattChatBotException("A " + getTypeName()
                    + " has no start or end time, so there is no /from or /to to amend.");
        }
        applyDescription(update);
    }

    /**
     * Applies the update's description, if it gave one.
     *
     * @param update the parts to change
     */
    protected void applyDescription(TaskUpdate update) {
        if (update.hasDescription()) {
            description = update.description();
        }
    }

    /**
     * Returns the single character shown inside the status box.
     *
     * @return "X" if the task is done, a space otherwise
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /** Marks this task as done. */
    public void markAsDone() {
        this.isDone = true;
    }

    /** Marks this task as not done yet. */
    public void markAsNotDone() {
        this.isDone = false;
    }

    /**
     * Returns what the user asked to be reminded of.
     *
     * @return the task's description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns whether another task records the same thing as this one.
     * <p>
     * Whether it has been done is left out on purpose: an entry already on the
     * list is a repeat whether or not it has been ticked off. The types that
     * carry dates widen this to include them, so that the same errand on two
     * different days is two entries rather than one.
     *
     * @param other the task to compare with
     * @return true if the two record the same thing
     */
    public boolean isSameTask(Task other) {
        return other != null
                && getTypeIcon().equals(other.getTypeIcon())
                && description.equals(other.description);
    }

    /**
     * Returns whether this task happens on the given date.
     * <p>
     * Todos have no date, so the default is false; the task types that do
     * carry dates override this. Asking the task itself avoids having to test
     * its type with instanceof from outside.
     *
     * @param date the date being asked about
     * @return true if this task falls on that date
     */
    public boolean isOn(LocalDate date) {
        return false;
    }

    /**
     * Returns the task encoded for the save file, e.g. {@code T | 1 | read book}.
     * <p>
     * Subclasses append their own fields, so the encoding of each task type
     * lives with that type rather than in one big switch inside Storage.
     *
     * @return the line to write to the save file
     */
    public String toFileFormat() {
        return getTypeIcon() + FIELD_SEPARATOR + (isDone ? "1" : "0")
                + FIELD_SEPARATOR + description;
    }

    /**
     * Returns the task as the user sees it, e.g. {@code [T][X] read book}.
     * Subclasses that carry dates append them to this.
     */
    @Override
    public String toString() {
        return "[" + getTypeIcon() + "][" + getStatusIcon() + "] " + description;
    }
}
