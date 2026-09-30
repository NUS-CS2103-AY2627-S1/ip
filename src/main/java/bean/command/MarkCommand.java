package bean.command;

import bean.exception.InvalidSyntaxException;
import bean.task.BeanList;

/** Executes commands that mark tasks as complete. */
public class MarkCommand {

    /** Marks the task at the index supplied in the command input. */
    public static String execute(String input, BeanList taskList) {
        int index = TaskIndexParser.parse(input, taskList.getSize());
        if (index == -1) {
            throw new InvalidSyntaxException(
                    "Oops, please provide a task index. Usage: mark INDEX", input);
        }

        return taskList.markTask(index);
    }
}
