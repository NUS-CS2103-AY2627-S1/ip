package anniechat.task;

/** Represents a task that takes place between a start and end time. */
public class Event extends Task {
    // Example: event project meeting /from Mon 2pm /to 4pm
    String taskDesc;
    String taskOnly;
    String start;
    String end;

    /**
     * Creates an event task from a command containing start and end times.
     *
     * @param desc command containing the event description and times.
     */
    public Event(String desc) {
        super(desc);
        String longDesc = desc.substring(6);
        taskOnly = longDesc.split("/from")[0];
        String day = desc.split("/from")[1];
        start = day.split("/to")[0];
        end = day.split("/to")[1];
        taskDesc = taskOnly + "(from: " + start
                + " to: " + end + ")";
    }

    @Override
    public String getTaskDesc() {
        return taskDesc;
    }

    @Override
    public String getTaskIcon() {
        return "E";
    }

    @Override
    public String toSaveFormat() {
        return this.getTaskIcon() + " | " + (isDone ? "1" : "0") + " | " + this.taskOnly
                + " | " + start + " | " + end;
    }
}
