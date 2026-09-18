package blud.command;

import blud.model.Task;
import blud.model.TaskList;

/** Formats the current task list for a list command response. */
public class ListTasks {
    private static final String TASK_LIST_PREFACE = "Here are the tasks in your list:";
    private final TaskList taskList;

    /** Creates a list operation for the supplied task list. */
    public ListTasks(TaskList taskList) {
        this.taskList = taskList;
    }

    /** Returns all tasks in their current display order. */
    public String execute() {
        StringBuilder response = new StringBuilder(TASK_LIST_PREFACE);
        for (int index = 0; index < taskList.getSize(); index++) {
            Task task = taskList.getTask(index);
            assert task != null : "Every task in the list must be displayable";
            response.append("\n").append(index + 1).append(". ").append(task);
        }
        return response.toString();
    }
}
