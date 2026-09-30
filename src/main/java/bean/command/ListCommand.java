package bean.command;

import bean.task.BeanList;

/** Executes commands that list all tasks. */
public class ListCommand {

    /** Returns a formatted display of all tasks. */
    public static String execute(BeanList taskList) {
        return taskList.displayTasks();
    }
}
