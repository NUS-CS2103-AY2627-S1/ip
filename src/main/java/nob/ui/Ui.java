package nob.ui;

import nob.exception.NobException;
import nob.task.Task;
import nob.task.TaskList;

/**
 * Handles messages shown to the user in the console.
 */
public class Ui {
    /** The divider used around each app response block. */
    private static final String DIVIDER = "____________________________________________________________";

    /** Displays Nob's greeting when the application starts. */
    public void showWelcome() {
        String banner = """
                 _   _       _
                | \\ | | ___ | |__
                |  \\| |/ _ \\| '_ \\
                | |\\  | (_) | |_) |
                |_| \\_|\\___/|_.__/

                  (•_•)
                  ( •_•)>⌐■-■
                  (⌐■_■)

                """;
        String greeting = """
                WASSUP! I'm Nob :)
                How can I help you?
                """;

        showDivider();
        System.out.print(banner);
        System.out.print(greeting);
        showDivider();
    }

    /** Displays the divider between console response blocks. */
    public void showDivider() {
        System.out.println(DIVIDER);
    }

    /** Displays Nob's goodbye message. */
    public void showGoodbye() {
        System.out.println("Goodbye! Hope to see you soon mate!");
    }

    /** Displays a message explaining that saved tasks could not be loaded. */
    public void showLoadingError() {
        System.out.println("I couldn't load the saved tasks, so I'm starting with an empty list.");
    }

    /** Displays an error message caused by invalid user input. */
    public void showError(String message) {
        System.out.println(message);
    }

    /** Displays a message after the entire task list has been cleared. */
    public void showTasksCleared() {
        System.out.println("Noted. I've cleared the entire task list.");
    }

    /** Displays every task in the supplied list. */
    public void showTaskList(TaskList tasks) throws NobException {
        if (tasks.getTaskCount() == 0) {
            System.out.println("Your task list is empty right now. Add a task using the 'todo', "
                    + "'deadline' or 'event' commands.");
            return;
        }

        System.out.println("Here are the tasks in your list:");
        for (int index = 1; index <= tasks.getTaskCount(); index++) {
            System.out.println(index + "." + tasks.getTask(index));
        }
    }

    /**
     * Displays tasks whose descriptions contain the specified keyword.
     *
     * @param tasks The task list to search.
     * @param keyword Keyword to find in task descriptions.
     * @throws NobException If a task cannot be read from the list.
     */
    public void showMatchingTasks(TaskList tasks, String keyword) throws NobException {
        boolean hasMatchingTask = false;
        for (int index = 1; index <= tasks.getTaskCount(); index++) {
            Task task = tasks.getTask(index);
            if (task.hasKeyword(keyword)) {
                if (!hasMatchingTask) {
                    System.out.println("Here are the matching tasks in your list:");
                    hasMatchingTask = true;
                }
                System.out.println(index + "." + task);
            }
        }

        if (!hasMatchingTask) {
            System.out.println("No matching tasks found.");
        }
    }

    /** Displays a message when the task-list capacity has been reached. */
    public void showTaskLimit(int capacity) {
        System.out.println("I can only store up to " + capacity + " tasks.");
    }

    /** Displays a newly added task and the new task-list size. */
    public void showTaskAdded(Task task, int taskCount) {
        System.out.println("Alrighty. I've added this task:");
        System.out.println("  " + task);
        System.out.println("Now you have " + taskCount + " tasks in the list.");
    }

    /** Displays a task after its completion status changes. */
    public void showTaskMarked(Task task, boolean isDone) {
        if (isDone) {
            System.out.println("LET'S GOOO! I've marked this task as done:");
        } else {
            System.out.println("Okay... I've marked this task as not done yet:");
        }
        System.out.println("  " + task);
    }

    /** Displays a task after it is removed and the remaining task count. */
    public void showTaskDeleted(Task task, int taskCount) {
        System.out.println("Noted. I've removed the task:");
        System.out.println("  " + task);
        System.out.println("Now you have " + taskCount + " tasks left in the list.");
    }

    /** Displays a message when the task data file cannot be saved. */
    public void showSavingError() {
        System.out.println("I couldn't save the task list to disk. Your changes are kept for this run.");
    }

    /** Displays a message when console input cannot be read. */
    public void showInputError() {
        System.out.println("I couldn't read your input.");
    }

    /** Displays the help text listing all available commands. */
    public void showHelp() {
        System.out.println("Here are the commands you can use:");
        System.out.println("  list");
        System.out.println("  todo DESCRIPTION");
        System.out.println("  deadline DESCRIPTION /by DATE_OR_TIME");
        System.out.println("  event DESCRIPTION /from START /to END");
        System.out.println("  find KEYWORD");
        System.out.println("  mark TASK_NUMBER");
        System.out.println("  unmark TASK_NUMBER");
        System.out.println("  delete TASK_NUMBER");
        System.out.println("  clear");
        System.out.println("  bye");
    }

    /** Displays a hint that a command is missing the space before its description. */
    public void showMissingSpaceHint(String commandName, String command) {
        String description = command.substring(commandName.length()).trim();
        System.out.println("It looks like you are missing a space after '" + commandName + "'.");
        System.out.println("Did you mean: " + commandName + " " + description);
    }

    /** Displays suggestions after an unrecognised command. */
    public void showUnknownCommandHint() {
        System.out.println("That isn't any of the commands I know. Did you mean one of these?");
        System.out.println("  todo DESCRIPTION");
        System.out.println("  deadline DESCRIPTION /by DATE_OR_TIME");
        System.out.println("  event DESCRIPTION /from START /to END");
    }
}
