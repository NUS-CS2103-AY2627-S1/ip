package bean.command;

import bean.exception.BeanListOutOfBoundsException;
import bean.task.BeanList;

/** Executes commands that delete tasks. */
public class DeleteCommand {

    /** Deletes the task at the index supplied in the command input. */
    public static String execute(String input, BeanList taskList) {
        int index = TaskIndexParser.parse(input, taskList.getSize());
        if (index == -1) {
            throw new BeanListOutOfBoundsException(
                    "Oops, you've keyed in an invalid Task index! (blank of "
                            + taskList.getSize() + ")", index);
        }

        return taskList.deleteTask(index) + "\n" + taskList.displayTasks();
    }
}
