package clover.ui;

import java.util.List;
import java.util.Scanner;

import clover.task.Task;
import clover.tutoree.Tutoree;

/**
 * Handles Clover's console input and output.
 */
public class Ui {
    private static final String DIVIDER = "____________________________________________________________";

    private final Scanner scanner;
    private final StringBuilder response;

    /**
     * Creates a UI that reads commands from standard input.
     */
    public Ui() {
        scanner = new Scanner(System.in);
        response = new StringBuilder();
    }

    /**
     * Returns whether another command can be read from standard input.
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Returns the next command entered by the user.
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /**
     * Displays Clover's welcome banner and greeting.
     */
    public void showWelcome() {
        String banner = "  _____    _         ____    __      __   ______    _____\n"
                + " / ____|  | |       / __ \\   \\ \\    / /  |  ____|  |  __ \\\n"
                + "| |       | |      | |  | |   \\ \\  / /   | |__     | |__) |\n"
                + "| |       | |      | |  | |    \\ \\/ /    |  __|    |  _  /\n"
                + "| |____   | |____  | |  | |     \\  /     | |____   | | \\ \\\n"
                + " \\_____|  |______|  \\____/       \\/      |______|  |_|  \\_\\\n";
        showMessages(
                DIVIDER,
                banner,
                "Hello! I'm Clover.",
                "What can I do for you?\n",
                DIVIDER);
    }

    /**
     * Displays the standard divider line.
     */
    public void showLine() {
        showMessage(DIVIDER);
    }

    /**
     * Displays an error message.
     */
    public void showError(String message) {
        showMessage("The forest path is unclear. " + message);
    }

    /** Displays a concise reference for every command accepted by Clover. */
    public void showHelp() {
        showMessages(
                "How to use Clover",
                "Type lowercase words exactly. Replace <UPPERCASE> placeholders with your details.",
                "[Square brackets show optional parts; do not type the brackets when writing the command.]",
                "",
                "TASKS",
                "• todo <DESCRIPTION> [/for <NAME>]",
                "  Add a task. /for links it to a tutoree.",
                "• deadline <DESCRIPTION> /by <DATE> [/for <NAME>]",
                "  Add a deadline. DATE uses yyyy-MM-dd.",
                "• event <DESCRIPTION> /from <DATE> /to <DATE> [/for <NAME>]",
                "  Add an event. The end date must be after the start date.",
                "• list",
                "  Show all tasks.",
                "• find <KEYWORD>",
                "  Search task descriptions.",
                "• mark <NUMBER>",
                "  Complete a task.",
                "• unmark <NUMBER>",
                "  Reopen a completed task.",
                "• delete <NUMBER>",
                "  Remove a task.",
                "",
                "TUTOREE DIRECTORY",
                "• add-tutoree <NAME> /address <ADDRESS> /fee <AMOUNT>[/RATE]",
                "  Add a tutoree. RATE may be /hour, /session, /lesson, or /month.",
                "• list-tutorees",
                "  Show all tutorees.",
                "• find-tutoree <KEYWORD>",
                "  Search tutoree names.",
                "",
                "OTHER",
                "• help",
                "  Show this command guide.",
                "• bye",
                "  Close Clover.");
    }

    /**
     * Displays every task currently in the list.
     */
    public void showTaskList(List<Task> tasks) {
        if (tasks.isEmpty()) {
            showMessage("There are no study quests in the grove yet.");
            return;
        }
        showMessage("The grove has gathered your study quests:");
        showNumberedTasks(tasks);
    }

    /**
     * Confirms that a task was added and shows the updated task count.
     */
    public void showTaskAdded(Task task, int taskCount) {
        showMessages(
                "A new study quest has taken root: " + task,
                "The grove now holds " + taskCount + " quest" + (taskCount == 1 ? "" : "s") + ".");
    }

    /**
     * Confirms that a task was marked as done.
     */
    public void showTaskMarked(Task task) {
        showMessage("The grove celebrates! This quest is complete: " + task);
    }

    /**
     * Confirms that a task was marked as not done.
     */
    public void showTaskUnmarked(Task task) {
        showMessage("This quest needs a little more tending: " + task);
    }

    /**
     * Confirms that a task was deleted and shows the updated task count.
     */
    public void showTaskDeleted(Task task, int taskCount) {
        showMessages(
                "This trail has been cleared: " + task,
                "The grove now holds " + taskCount + " quest" + (taskCount == 1 ? "" : "s") + ".");
    }

    /**
     * Displays all the tasks whose description contains a given keyword.
     */
    public void showFindResults(List<Task> result) {
        if (result.isEmpty()) {
            showMessage("No matching tasks found.");
            return;
        }
        showMessage("The grove found these matching quests:");
        showNumberedTasks(result);
    }

    /** Confirms that a tutoree was added and shows the updated tutoree count. */
    public void showTutoreeAdded(Tutoree tutoree, int tutoreeCount) {
        showMessages(
                "A new learning companion has arrived in the grove:",
                tutoree.getName(),
                "Address: " + tutoree.getAddress(),
                "Fee: " + tutoree.getFee(),
                "The grove now knows " + tutoreeCount + " learning companion"
                        + (tutoreeCount == 1 ? "" : "s") + ".");
    }

    /** Displays every tutoree currently in the directory. */
    public void showTutoreeList(List<Tutoree> tutorees) {
        if (tutorees.isEmpty()) {
            showMessage("There are no learning companions in the grove yet.");
            return;
        }
        showMessage("Here are the learning companions in the grove:");
        showNumberedTutorees(tutorees);
    }

    /** Displays tutorees whose names match the user's search keyword. */
    public void showTutoreeFindResults(List<Tutoree> tutorees) {
        if (tutorees.isEmpty()) {
            showMessage("No matching learning companions found.");
            return;
        }
        showMessage("The grove found these matching learning companions:");
        showNumberedTutorees(tutorees);
    }

    /**
     * Displays the closing message.
     */
    public void showGoodbye() {
        showMessage("The grove closes for now. Goodbye, and may your path through the grove be gentle.");
    }

    /**
     * Clears messages collected for the next GUI response.
     */
    public void clearResponse() {
        response.setLength(0);
    }

    /**
     * Returns the messages produced since the response was last cleared.
     */
    public String buildResponse() {
        return response.toString().stripTrailing();
    }

    /**
     * Prints a message and keeps a copy so it can be displayed by the GUI.
     */
    private void showMessage(String message) {
        System.out.println(message);
        response.append(message).append(System.lineSeparator());
    }

    /**
     * Displays a sequence of related messages in their supplied order.
     *
     * @param messages the messages to display
     */
    private void showMessages(String... messages) {
        for (String message : messages) {
            showMessage(message);
        }
    }

    /** Displays the supplied tasks in one-based numbered order. */
    private void showNumberedTasks(List<Task> tasks) {
        for (int taskIndex = 0; taskIndex < tasks.size(); taskIndex++) {
            showMessage((taskIndex + 1) + "." + tasks.get(taskIndex));
        }
    }

    /** Displays supplied tutorees in one-based numbered order with all stored details. */
    private void showNumberedTutorees(List<Tutoree> tutorees) {
        for (int tutoreeIndex = 0; tutoreeIndex < tutorees.size(); tutoreeIndex++) {
            Tutoree tutoree = tutorees.get(tutoreeIndex);
            showMessages((tutoreeIndex + 1) + ". " + tutoree.getName(),
                    "   Address: " + tutoree.getAddress(),
                    "   Fee: " + tutoree.getFee());
        }
    }

    /**
     * Releases resources held by this UI.
     */
    public void close() {
        scanner.close();
    }
}
