package chattingheads.ui;

import java.util.List;
import java.util.Scanner;

import chattingheads.task.Task;
import chattingheads.task.TaskList;

/**
 * Handles user input and generates messages for the user.
 */
public class Ui {

    private static final String SEPARATOR =
            "--------------------------------------------------------------------------------";
    private final Scanner scanner = new Scanner(System.in);

    /**
     * Reads the next command entered by the user.
     *
     * @return User input.
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /**
     * Returns the startup message.
     *
     * @return Startup message.
     */
    public String getStartupMessage() {
        return """
                You may find yourself
                Living in a shotgun shack
                """ + SEPARATOR;
    }

    /**
     * Returns the shutdown message.
     *
     * @return Shutdown message.
     */
    public String getShutdownMessage() {
        return "Letting the days go \"bye!\"\nLet the water shut me down\n" + SEPARATOR;
    }

    /**
     * Returns a message stating the number of tasks currently in the task list.
     *
     * @param taskList Task list whose size is displayed.
     * @return Message stating the number of tasks in the task list.
     */
    public String getListStatus(TaskList taskList) {
        if (taskList.size() == 1) {
            return "Now you have 1 task in the list\n" + SEPARATOR;
        } else {
            return String.format(
                    "Now you have %s tasks in the list\n%s",
                    taskList.isEmpty() ? "no" : taskList.size(),
                    SEPARATOR
            );
        }
    }

    /**
     * Returns a message confirming that a task was added.
     *
     * @param task     Added task.
     * @param taskList Task list after the addition.
     * @return Message confirming that a task was added.
     */
    public String getAddStatus(Task task, TaskList taskList) {
        return String.format(
                "Put it right there on your list:\n%s\n%s",
                task,
                getListStatus(taskList)
        );
    }

    /**
     * Returns a message confirming that a task was deleted.
     *
     * @param task     Deleted task.
     * @param taskList Task list after the deletion.
     * @return Message confirming that a task was deleted.
     */
    public String getDeleteStatus(Task task, TaskList taskList) {
        return String.format(
                "Into the blue again\nAfter this task is gone:\n%s\n%s",
                task,
                getListStatus(taskList)
        );
    }

    /**
     * Returns an error message.
     *
     * @param e Exception containing the error message.
     * @return Error message.
     */
    public String getErrorMessage(Exception e) {
        return e.getMessage() + "\n" + SEPARATOR;
    }

    /**
     * Returns a message confirming that a task was marked as completed.
     *
     * @param task Marked task.
     * @return Message confirming that a task was marked as completed.
     */
    public String getMarkStatus(Task task) {
        return String.format(
                "Marked this task as complete:\n%s\nThere's a million ways to get things done\n",
                task)
                + SEPARATOR;
    }

    /**
     * Returns a message confirming that a task was marked as incomplete.
     *
     * @param task Unmarked task.
     * @return Message confirming that a task was marked as incomplete.
     */
    public String getUnmarkStatus(Task task) {
        return String.format(
                "Marked this task as incomplete:\n%s\nYou start a conversation, you can't even finish it\n",
                task)
                + SEPARATOR;
    }

    /**
     * Returns a message listing all tasks in the task list.
     *
     * @param taskList Task list to display.
     * @return Message listing all tasks in the task list.
     */
    public String getTaskListMessage(TaskList taskList) {
        StringBuilder message = new StringBuilder("Take a look at these tasks:");

        for (int i = 0; i < taskList.size(); i++) {
            message.append(String.format(
                    "%n%d. %s",
                    i + 1,
                    taskList.get(i)
            ));
        }
        message.append("\n").append(SEPARATOR);
        return message.toString();
    }

    /**
     * Returns a message listing all tasks that contain the specified keyword.
     *
     * @param taskList Task list to search.
     * @param keyword  Keyword to search for.
     * @return Message listing all tasks that contain the specified keyword.
     */
    public String getFoundTasksMessage(TaskList taskList, String keyword) {
        StringBuilder message = new StringBuilder("Take a look at these tasks:");
        List<Integer> foundTaskIndices = taskList.findIndices(keyword);

        for (int i : foundTaskIndices) {
            message.append(String.format(
                    "%n%d. %s",
                    i + 1,
                    taskList.get(i)
            ));
        }
        message.append("\n").append(SEPARATOR);
        return message.toString();
    }

    /**
     * Returns a message confirming that a task was rescheduled.
     *
     * @param task     Rescheduled task.
     * @param taskType Type of the task.
     * @return Message confirming that the task was rescheduled.
     */
    public String getRescheduleStatus(Task task, String taskType) {
        return String.format(
                "Rescheduled this %s:\n%s\nMaybe tomorrow, maybe the next day\nWhatever you think is best\n",
                taskType,
                task)
                + SEPARATOR;
    }
}
