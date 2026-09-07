package bibi.command;

import java.time.LocalDate;

import bibi.task.Task;
import bibi.task.TaskDateTime;

/**
 * Shows the deadlines due on one date and the events running on it.
 */
public class OnCommand extends FilterCommand {
    private final LocalDate queryDate;

    /**
     * Creates a command that will report on the given date.
     *
     * @param queryDate the date being asked about
     */
    public OnCommand(LocalDate queryDate) {
        this.queryDate = queryDate;
    }

    @Override
    protected boolean matches(Task task) {
        return task.occursOn(queryDate);
    }

    @Override
    protected String getHeader() {
        return "Here is what you have on " + getShownDate() + ":";
    }

    @Override
    protected String getNoMatchMessage() {
        return "You have nothing on " + getShownDate() + ".";
    }

    /**
     * Returns the queried date in the form the user sees elsewhere.
     *
     * @return the formatted date
     */
    private String getShownDate() {
        return TaskDateTime.formatDate(queryDate);
    }
}
