package gooble.ui;

import java.util.List;
import java.util.function.Consumer;

import gooble.task.Task;
import gooble.task.TaskList;

/**
 * Handles Gooble's console presentation.
 */
public class Ui {
    private static final String DIVIDER = "_".repeat(60);
    private static final String BANNER = "  ____            _     _\n"
            + " / ___| ___   ___ | |__ | | ___\n"
            + "| |  _ / _ \\ / _ \\| '_ \\| |/ _ \\\n"
            + "| |_| | (_) | (_) | |_) | |  __/\n"
            + " \\____|\\___/ \\___/|_.__/|_|\\___|\n";
    private final Consumer<String> output;

    /** Creates a console user interface. */
    public Ui() {
        this(System.out::print);
    }

    /** Creates a user interface that writes to the supplied output. */
    public Ui(Consumer<String> output) {
        this.output = output;
    }

    /** Displays one line of text. */
    public void showMessage(String message) {
        output.accept(message + System.lineSeparator());
    }

    /** Prints a horizontal divider used to separate console messages. */
    public void showDivider() {
        showMessage(DIVIDER);
    }

    /** Prints Gooble's welcome banner and greeting. */
    public void showWelcome() {
        showDivider();
        output.accept(BANNER);
        showMessage("Hello! I'm Gooble.");
        showMessage("What can I do for you?");
        showDivider();
    }

    /**
     * Prints every task in its current list order.
     *
     * @param tasks task list to display
     */
    public void showTasks(TaskList tasks) {
        showMessage("Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            showNumberedTask(i, tasks.get(i));
        }
    }

    /** Prints tasks matching a search keyword in their original task-list order. */
    public void showMatchingTasks(List<Task> matchingTasks) {
        showMessage("Here are the matching tasks in your list tehee:");
        for (int i = 0; i < matchingTasks.size(); i++) {
            showNumberedTask(i, matchingTasks.get(i));
        }
    }

    /** Displays a task with its one-based position in a list. */
    private void showNumberedTask(int zeroBasedIndex, Task task) {
        String formattedTask = task.toString().replaceFirst("\\]\\[", "] [");
        showMessage((zeroBasedIndex + 1) + "." + formattedTask);
    }

    /** Reports that a task was marked complete. */
    public void showMarkedDone(Task task) {
        showMessage("Nice! I've marked this task as done:");
        showMessage("  " + task);
    }

    /** Reports that a task was marked incomplete. */
    public void showMarkedNotDone(Task task) {
        showMessage("OK, I've marked this task as not done yet:");
        showMessage("  " + task);
    }

    /** Reports a removed task and the remaining task count. */
    public void showDeleted(Task task, int remainingCount) {
        showMessage("Noted. I've removed this task:");
        showMessage("  " + task);
        showMessage("Now you have " + remainingCount + " tasks in the list.");
    }

    /** Reports a newly added task using Gooble's standard confirmation. */
    public void showAdded(Task task) {
        showMessage("Got it. I've added this task:");
        showMessage("  " + task);
    }

    /** Reports the number of tasks currently in the list. */
    public void showTaskCount(int totalCount) {
        showMessage("Now you have " + totalCount + " tasks in the list.");
    }

    /** Prints the application's exit message. */
    public void showBye() {
        showMessage("Bye. Hope to see you again soon!");
    }

    /** Displays the available commands and their expected formats. */
    public void showHelp() {
        showHelp("");
    }

    /** Displays general help or detailed help for one command. */
    public void showHelp(String topic) {
        String command = topic.toLowerCase(java.util.Locale.ROOT);
        if (command.isEmpty()) {
            showGeneralHelp();
            return;
        }
        showMessage(commandHelpFor(command, topic));
    }

    private void showGeneralHelp() {
        showMessage("GOOBLE COMMANDS\n"
                    + "────────────────────────────────────────────────────────────────────────\n\n"
                    + "TASK MANAGEMENT\n\n"
                    + "COMMAND                                      DESCRIPTION\n"
                    + "add <description>                            Add a simple task\n"
                    + "todo <description>                           Add a todo task\n"
                    + "deadline <description> /by <date>            Add a task with a deadline\n"
                    + "event <description> /from <date> /to <date>  Add an event\n\n"
                    + "VIEWING TASKS\n\n"
                    + "COMMAND                                      DESCRIPTION\n"
                    + "list                                         Show all tasks\n"
                    + "find <keyword>                               Search tasks\n"
                    + "list from <date> to <date>                   Filter events by date\n\n"
                    + "UPDATING TASKS\n\n"
                    + "COMMAND                                      DESCRIPTION\n"
                    + "mark <number>                                Mark a task as complete\n"
                    + "unmark <number>                              Mark a task as incomplete\n"
                    + "delete <number>                              Delete a task\n"
                    + "tag <number> <#tag>                          Add a tag\n"
                    + "untag <number>                               Remove all tags\n\n"
                    + "OTHER\n\n"
                    + "COMMAND                                      DESCRIPTION\n"
                    + "help                                         Show this guide\n"
                    + "bye                                          Exit Gooble\n\n"
                    + "TIP\n"
                    + "Type help <command> for a detailed example.");
    }

    private String commandHelpFor(String command, String topic) {
        return switch (command) {
            case "add" -> commandHelp("ADD COMMAND", "add <description>",
                "Add a simple task.", "add read chapter 3");
            case "todo" -> commandHelp("TODO COMMAND", "todo <description>",
                "Add a todo task.", "todo buy groceries");
            case "deadline" -> commandHelp("DEADLINE COMMAND", "deadline <description> /by <date>",
                "Add a task with a deadline.", "deadline submit report /by 2026-09-20 1800");
            case "event" -> commandHelp("EVENT COMMAND", "event <description> /from <date> /to <date>",
                "Add an event.", "event project meeting /from 2026-09-15 1400 /to 2026-09-15 1500");
            case "list" -> commandHelp("LIST COMMAND", "list",
                "Show all tasks.", "list");
            case "find" -> commandHelp("FIND COMMAND", "find <keyword>",
                "Search tasks by keyword.", "find report");
            case "mark" -> commandHelp("MARK COMMAND", "mark <number>",
                "Mark a task as complete.", "mark 2");
            case "unmark" -> commandHelp("UNMARK COMMAND", "unmark <number>",
                "Mark a task as incomplete.", "unmark 2");
            case "delete" -> commandHelp("DELETE COMMAND", "delete <number>",
                "Delete a task.", "delete 2");
            case "tag" -> commandHelp("TAG COMMAND", "tag <number> <#tag>",
                "Add a tag to a task.", "tag 2 #urgent");
            case "untag" -> commandHelp("UNTAG COMMAND", "untag <number>",
                "Remove all tags from a task.", "untag 2");
            case "bye" -> commandHelp("BYE COMMAND", "bye", "Exit Gooble.", "bye");
            default -> "No help is available for `" + topic + "`.\n\n"
                + "Type help to see all available commands.";
        };
    }

    private String commandHelp(String title, String format, String description, String example) {
        return title + "\n"
                + "────────────────────────────────────────\n\n"
                + "FORMAT\n  " + format + "\n\n"
                + description + "\n\n"
                + "EXAMPLE\n  " + example;
    }

    /** Reports the legacy add command's confirmation. */
    public void showAddedGeneral(String description) {
        showMessage("added: " + description);
    }

    /** Reports that a tag was added to a task. */
    public void showTagged(Task task) {
        showMessage("I've tagged this task:");
        showMessage("  " + task);
    }

    /** Reports that all tags were removed from a task. */
    public void showUntagged(Task task) {
        showMessage("I've removed the tags from this task:");
        showMessage("  " + task);
    }
}
