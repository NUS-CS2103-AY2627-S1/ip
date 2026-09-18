package alfred.task;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/**
 * Represents a task the user has asked Alfred to remember, together with
 * whether it has been completed and how much it matters. Each kind of task is
 * a subclass that puts its own type box, such as {@code [T]}, in front of the
 * display form defined here.
 */
public abstract class Task {
    private final String name;

    private boolean isDone;

    private Priority priority = Priority.NONE;

    /**
     * Creates a task that starts out not done.
     *
     * @param name the description of the task.
     */
    protected Task(String name) {
        this.name = name;
        this.isDone = false;
    }

    /**
     * Tells whether this task falls on the given day. A task with no date
     * falls on no day; the kinds that carry a date override this.
     *
     * @param date the day being asked about.
     * @return true if this task falls on that day.
     */
    public boolean occursOn(LocalDate date) {
        return false;
    }

    /**
     * Tells whether this task's description contains the given keyword,
     * ignoring case.
     *
     * @param keyword the text being searched for.
     * @return true if the description contains it.
     */
    public boolean matches(String keyword) {
        return name.toLowerCase().contains(keyword.toLowerCase());
    }

    /**
     * Tells whether another task is the same task as this one: one of the same
     * kind, with the same description ignoring case. Being done or having a
     * priority does not make a task different; the kinds that carry dates add
     * those to the comparison.
     *
     * @param other the task to compare with.
     * @return true if the two describe the same task.
     */
    public boolean isSameTask(Task other) {
        return getClass() == other.getClass() && name.equalsIgnoreCase(other.name);
    }

    /** Marks this task as completed. */
    public void markDone() {
        isDone = true;
    }

    /** Marks this task as not completed. */
    public void unmarkDone() {
        isDone = false;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public Priority getPriority() {
        return priority;
    }

    /**
     * Returns this task's fields, in the order they are saved: the type letter,
     * the status, the description, whatever the kind of task carries of its own,
     * and last of all the priority, if it has one.
     *
     * @return a list holding this task's fields, in the order they are saved.
     */
    public abstract List<String> toFileFields();

    /**
     * Returns the fields of one save line. The priority goes last and is left
     * out when there is none, so a list without priorities saves as the file
     * earlier versions wrote.
     *
     * @param type the letter naming the kind of task, such as {@code D}.
     * @param extraFields the fields this kind adds after the description, in the
     *        order they are saved, and none for a kind that adds none.
     * @return an unmodifiable list holding the whole line's fields, in order.
     */
    protected List<String> buildFileFields(String type, String... extraFields) {
        assert type.length() == 1 : "a save line's type is one letter, not '" + type + "'";

        Stream<String> fields = Stream.concat(Stream.of(type, isDone ? "1" : "0", name),
                Arrays.stream(extraFields));
        if (priority != Priority.NONE) {
            fields = Stream.concat(fields, Stream.of(priority.name()));
        }
        return fields.toList();
    }

    /**
     * Returns the status box, the priority box if the task has one, and the
     * description, for example {@code [X][HIGH] read book}.
     *
     * @return the display form of this task, without any type box.
     */
    @Override
    public String toString() {
        return (isDone ? "[X]" : "[ ]") + priority.box() + " " + name;
    }
}
