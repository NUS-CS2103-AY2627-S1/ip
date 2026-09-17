package panda.command;

import java.time.temporal.Temporal;

import panda.task.TaskList;
import panda.ui.Ui;

/**
 * Displays tasks occurring on, due on, or spanning across a specified date.
 */
public class DisplayDateCommand implements Command {
    private static final String MESSAGE_NO_MATCHING_TASKS =
            "Nothing scheduled that day. Your calendar is carrying you.";

    private final Temporal date;

    /**
     * Creates a command to display tasks on the specified date.
     *
     * @param date Date to filter tasks by as a {@link Temporal}.
     */
    public DisplayDateCommand(Temporal date) {
        this.date = date;
    }

    /**
     * Retrieves and displays all tasks occurring on or spanning across the specified date.
     *
     * @param taskList Active task list.
     */
    @Override
    public void execute(TaskList taskList) {
        displayTasksOnDate(taskList, date);
    }

    /**
     * Displays tasks matching the specified date, or an empty-list message if none match.
     *
     * @param taskList Active task list.
     * @param date Date to filter tasks by.
     */
    static void displayTasksOnDate(TaskList taskList, Temporal date) {
        String tasksOnDate = taskList.getTasksOnDate(date);
        String message = tasksOnDate.isEmpty() ? MESSAGE_NO_MATCHING_TASKS : tasksOnDate;
        Ui.printMessage(message);
    }
}
