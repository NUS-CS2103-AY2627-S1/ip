package bro.task;

/** Represents a task that occurs between a start and end time. */
public class Events extends Task {
    private final String dateline;
    private final String startTime;

    /** Creates an event task with its start time, end time, and description. */
    public Events(String startTime, String deadline, String description) {
        super(description, TaskType.EVENT);
        this.dateline = deadline;
        this.startTime = startTime;
    }

    /** Returns the event start time for collaborators in other packages. */
    public String getStartTime() {
        return startTime;
    }

    /** Returns the event end time for collaborators in other packages. */
    public String getEndTime() {
        return dateline;
    }

    /** Returns the event task in Bro's list format. */
    @Override
    public String toString() {
        return formatTask(getDescription() + "(from: " + startTime + " to: " + dateline + ")");
    }

}
