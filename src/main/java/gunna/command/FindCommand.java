package gunna.command;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import gunna.DukeException;
import gunna.Storage;
import gunna.TaskList;
import gunna.Ui;

/**
 * Represents a command to find tasks on a specific date.
 */
public class FindCommand extends Command {
    private String dateStr;

    /**
     * Constructs a FindCommand with the specified date string.
     *
     * @param dateStr The date string in yyyy-MM-dd format.
     */
    public FindCommand(String dateStr) {
        this.dateStr = dateStr;
    }

    /**
     * Executes the find command by searching for tasks on the specified date.
     *
     * @param tasks The task list to search.
     * @param ui The UI to display the matching tasks.
     * @param storage The storage (not used).
     * @throws DukeException If the date string is empty or has an invalid format.
     */
    @Override
    public String execute(TaskList tasks, Ui ui, Storage storage) throws DukeException {
        if (dateStr.isEmpty()) {
            throw new DukeException("Date required.\n     Usage: on <yyyy-MM-dd>");
        }
        try {
            LocalDate searchDate = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String formattedDate = searchDate.format(DateTimeFormatter.ofPattern("MMM dd yyyy"));
            var matchingTasks = tasks.getTasksOnDate(searchDate);
            ui.showTasksOnDate(matchingTasks, formattedDate);

            if (matchingTasks.isEmpty()) {
                return "No tasks scheduled for " + formattedDate;
            } else {
                StringBuilder response = new StringBuilder("Schedule for " + formattedDate + ":");
                for (int i = 0; i < matchingTasks.size(); i++) {
                    response.append("\n").append(i + 1).append(".").append(matchingTasks.get(i));
                }
                return response.toString();
            }
        } catch (DateTimeParseException e) {
            throw new DukeException("Date format invalid. Use yyyy-MM-dd (e.g., 2019-12-31).");
        }
    }
}
