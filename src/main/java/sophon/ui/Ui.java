package sophon.ui;

import sophon.model.TaskList;

/**
 * Handles console output shown to the user.
 */
public class Ui {
    private static final String BANNER = "─── S O P H O N ───\n";


    /**
     * Shows Sophon's greeting and any startup warning.
     *
     * @param startupWarning warning to show after the greeting, or null if startup succeeded.
     */
    public String getGreeting(String startupWarning) {
        String greeting = "Hi. I'm Sophon.\n"
                + "I'm listening.\n"
                + "What do you wish to communicate?";

        if (startupWarning == null) {
            return BANNER + greeting;
        }
        return BANNER + greeting + startupWarning;
    }

    /**
     * Shows Sophon's farewell message.
     */
    public String getByeMessage() {
        return "Our conversation ends here.\n"
                + "Until we meet again.";
    }

    /**
     * Shows an error message.
     *
     * @param message error message to show.
     */
    public String getError(String message) {
        return message;
    }

    /**
     * Shows all tasks currently tracked by Sophon.
     *
     * @param tasks tasks to show.
     */
    public String getTaskList(TaskList tasks) {
        StringBuilder taskList = new StringBuilder();
        for (int i = 0; i < tasks.size(); i++) {
            taskList.append(String.format("%d.%s%n", i + 1, tasks.get(i)));
        }
        return "Current tasks under observation:\n"
                + taskList;
    }

    /**
     * Shows the tasks that match a user's search.
     *
     * @param tasks matching tasks to show.
     */
    public String getMatchingTasks(TaskList tasks) {
        StringBuilder taskList = new StringBuilder();
        for (int i = 0; i < tasks.size(); i++) {
            taskList.append(String.format("%d.%s%n", i + 1, tasks.get(i)));
        }
        return "These signals match your search:\n"
                + taskList;
    }
}
