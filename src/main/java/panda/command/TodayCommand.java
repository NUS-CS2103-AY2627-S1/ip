package panda.command;

import java.time.LocalDate;

import panda.task.TaskList;

/**
 * Displays tasks occurring on, due on, or spanning across today's date.
 */
public class TodayCommand implements Command {

    /**
     * Retrieves and displays tasks that fall on or span across the current system date.
     *
     * @param taskList Active task list.
     */
    @Override
    public void execute(TaskList taskList) {
        LocalDate today = LocalDate.now();
        DisplayDateCommand.displayTasksOnDate(taskList, today);
    }
}
