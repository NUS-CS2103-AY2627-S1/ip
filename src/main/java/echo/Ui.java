package echo;

/**
 * Handles command-line output messages for Echo.
 */
public class Ui {
    private static final String NAME = "ECHO";
    private static final String LINE = "\n" + "-".repeat(30) + "\n";
    private static final String ENDLINE = "\n" + "-".repeat(30);

    /**
     * Returns Echo's welcome message.
     */
    public static String getWelcome() {
        String banner = String.format(LINE + "%s TASK MANAGEMENT SYSTEM" + ENDLINE,
                NAME);
        return String.format("%s\n\nSYSTEM ONLINE.\n\nWELCOME, USER.\nAWAITING INPUT.",
                banner);
    }

    /**
     * @return Echo's farewell message
     */
    public static String getFarewell() {
        return "SESSION TERMINATED. GOODBYE." + LINE;
    }

    /**
     * Displays confirmation that a task was added.
     *
     * @param task the task that was added
     * @param type the type of task that was added
     */
    public String getAddedTaskString(Task task, String type) {
        return String.format("%s TASK ADDED:\n  %s",
                type, task);
    }

    /**
     * Return string representing confirmation that a task was deleted and the number of tasks remaining.
     *
     * @param deletedTask the task that was deleted
     * @param size the number of tasks remaining
     */
    public String getDeleteTask(Task deletedTask, int size) {
        return String.format("TASK TERMINATED:\n  %s\n%d TASKS REMAIN.",
                deletedTask, size);
    }

    /**
     * Returns string of confirmation that a task's completion status changed.
     *
     * @param task the task whose status changed
     * @param isMarked whether the task is now marked as complete
     */
    public String getMarkString(Task task, boolean isMarked) {
        if (isMarked) {
            return String.format("TASK STATUS:\n  %s - COMPLETED", task);
        } else {
            return String.format("TASK STATUS:\n  %s - PENDING", task);
        }
    }

    /**
     * Returns a string of every task in the given task list.
     *
     * @param list the task list to return as a string
     */
    public String getListString(TodoList list) {
        return list.toString();
    }

    /**
     * Returns a string of every task in the given search result list.
     *
     * @param list the task list to return as string
     */
    public String getSearchListString(TodoList list) {
        return "\nPROCESSING:" + "\n" + "MATCHING TASKS FOUND:\n" + list.toString();
    }
}
