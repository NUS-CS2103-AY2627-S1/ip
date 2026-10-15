package bern.task;

import java.util.ArrayList;

import bern.datetime.DateTime;

/**
 * Represents a task due on a specific date, optionally at a specific time.
 */
public class Deadline extends Task implements IDateTimeComparable {
    private final DateTime deadlineDateTime;

    /**
     * Creates an incomplete task with the given deadline.
     *
     * @param name The name of the task.
     * @param deadlineDateTime The date and optional time when the task is due.
     */
    Deadline(String name, DateTime deadlineDateTime) {
        super(name);
        this.deadlineDateTime = deadlineDateTime;
    }

    /**
     * Returns a symbol that represents the Deadline task type.
     *
     * @return The symbol representing the task type.
     */
    @Override
    protected String getSymbol() {
        return "D";
    }

    /**
     * Returns the deadline formatted for display and storage.
     *
     * @return The formatted deadline date and optional time.
     */
    public String getDateTimeString() {
        return deadlineDateTime.toString();
    }

    /**
     * Returns the task type, completion marker, and name followed by its deadline.
     *
     * @return A string representation of a Deadline.
     */
    @Override
    public String toString() {
        return String.format("%s (by: %s)", super.toString(), deadlineDateTime);
    }

    /**
     * Returns the task symbol, completion flag, name, and deadline in save-file order.
     *
     * @return The fields used to save this deadline.
     */
    @Override
    public ArrayList<String> toDataList() {
        ArrayList<String> out = super.toDataList();
        out.add(deadlineDateTime.toString());
        return out;
    }

    @Override
    protected boolean isOnDay(DateTime dateTime) {
        return deadlineDateTime.compareDate(dateTime) == 0;
    }

    /**
     * Returns the deadline as the reference used for chronological ordering.
     *
     * @return The date and optional time when the task is due.
     */
    @Override
    public DateTime getReferenceDateTime() {
        return deadlineDateTime;
    }

    @Override
    public int compareDateTime(IDateTimeComparable timeComparable) {
        return getReferenceDateTime().compareTo(timeComparable.getReferenceDateTime());
    }
}
