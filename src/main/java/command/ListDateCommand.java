package command;

import java.time.LocalDate;

import exception.CbtException;
import parser.Parser;
import task.TaskList;

/** Displays deadlines and events occurring on one specified date. */
public class ListDateCommand implements Command {
    private final String dateText;

    /**
     * Creates a command using the user-entered date.
     *
     * @param dateText date on which tasks should be listed.
     */
    public ListDateCommand(String dateText) {
        this.dateText = dateText;
    }

    /**
     * Returns deadlines and events occurring on the requested date.
     *
     * @param taskList task list to search.
     * @return result indicating that no application state changed.
     * @throws CbtException if the date is blank or invalid.
     */
    @Override
    public CommandResult execute(TaskList taskList) throws CbtException {
        if (dateText.isBlank()) {
            throw new CbtException("Use: date DATE (for example, 2019-12-02)");
        }
        LocalDate date = Parser.parseDate(dateText);
        String message = "On the radar for " + Parser.formatDate(date) + ":";
        String formattedTasks = taskList.formatTasksOnDate(date);
        if (!formattedTasks.isEmpty()) {
            message += System.lineSeparator() + formattedTasks;
        }
        return new CommandResult(message, false, false);
    }
}
