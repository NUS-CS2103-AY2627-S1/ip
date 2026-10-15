package bern.logic;

import bern.task.Task;

/**
 * Stores an event's interval within one day and the timetable column assigned to it.
 */
public final class ScheduleEntry {
    private final Task task;
    private final int startMinute;
    private final int endMinute;
    private final int column;

    /**
     * Creates a timetable placement whose end is exclusive.
     *
     * @param task The original task represented by this placement.
     * @param startMinute The inclusive start, in minutes after midnight.
     * @param endMinute The exclusive end in minutes after midnight, at most 1440 for the end of the day.
     * @param column The zero-based display column.
     */
    public ScheduleEntry(Task task, int startMinute, int endMinute, int column) {
        this.task = task;
        this.startMinute = startMinute;
        this.endMinute = endMinute;
        this.column = column;
    }

    /**
     * Returns the original task represented by this segment.
     *
     * @return The task object supplied to the constructor.
     */
    public Task getTask() {
        return task;
    }

    /**
     * Returns the segment's inclusive start within its day.
     *
     * @return The start in minutes after midnight.
     */
    public int getStartMinute() {
        return startMinute;
    }

    /**
     * Returns the segment's exclusive end within its day.
     *
     * @return The end in minutes after midnight, with 1440 representing the end of the day.
     */
    public int getEndMinute() {
        return endMinute;
    }

    /**
     * Returns the timetable column assigned to this segment.
     *
     * @return The zero-based column index.
     */
    public int getColumn() {
        return column;
    }
}
