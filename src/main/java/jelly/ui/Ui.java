package jelly.ui;

import jelly.model.TaskList;

/** Handles Jelly's interaction with the command-line user. */
public class Ui {
    /** Message shown when the GUI conversation starts. */
    public static final String GUI_WELCOME_MESSAGE = "Hi, I'm Jelly! What can I do for you?";

    /** Jelly's farewell message, shared with {@code Jelly#computeCommandResult} for the GUI. */
    public static final String BYE_MESSAGE = "Bye! Stay jiggly~";

    /** Lists Jelly's available commands. */
    public static final String HELP_MESSAGE =
            """
                    Your Jelly commands:
                    todo <description>
                    deadline <description> /by yyyy-MM-dd HHmm
                    event <description> /from yyyy-MM-dd HHmm /to yyyy-MM-dd HHmm
                    list
                    find <keyword>
                    mark <task number>
                    unmark <task number>
                    tag <task number> <tag>
                    untag <task number>
                    delete <task number>
                    help
                    bye""";

    /** Prints Jelly's greeting and prompt. */
    public void showWelcome() {
        String banner = "╭──────────────────────╮\n"
                + "│      J E L L Y       │\n"
                + "│                      │\n"
                + "│       .-\"\"\"\"-.       │\n"
                + "│     .'  o  o  '.     │\n"
                + "│    /      ∆     \\    │\n"
                + "│    \\    '---'   /    │\n"
                + "│     '._      _.'     │\n"
                + "│        `----`        │\n"
                + "╰──────────────────────╯";

        System.out.println(banner);
        System.out.println("\nHello! I'm Jelly, your squishy little assistant!");
        System.out.println("Let's make your tasks stick! :)");
    }

    /**
     * Formats the current tasks with their one-based list numbers.
     *
     * @param tasks the task list to format.
     * @return the formatted task list.
     */
    public String formatTaskList(TaskList tasks) {
        StringBuilder output = new StringBuilder();

        output.append("Your Jelly Jar :)\n\n");

        for (int i = 0; i < tasks.size(); i++) {
            output.append(i + 1)
                    .append(".")
                    .append(tasks.getTask(i))
                    .append("\n");
        }

        return output.toString();
    }

    /** Prints tasks whose descriptions contain the supplied keyword. */
    public void showMatchingTasks(TaskList tasks, String keyword) {
        System.out.println("Here are the tasks Jelly found in your jar:");
        for (int taskNumber : tasks.findMatchingTaskNumbers(keyword)) {
            System.out.println(taskNumber + "." + tasks.getTask(taskNumber - 1));
        }
    }

    /** Prints the standard formatted error message. */
    public void showError(String message) {
        System.out.println(" " + message);
    }

    /** Prints the message used when loading saved tasks fails. */
    public void showLoadingError() {
        System.out.println("Jelly couldn't find your saved tasks.");
        System.out.println("Your jar is empty for now—let's add something useful!");
    }

    /** Prints the message used when saving tasks fails. */
    public void showSavingError() {
        System.out.println("Jelly couldn't seal your tasks in the jar.");
    }

    /** Prints Jelly's farewell. */
    public void showBye() {
        System.out.println(BYE_MESSAGE);
    }
}
