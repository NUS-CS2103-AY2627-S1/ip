package jimbo.ui;

import java.util.ArrayList;

import jimbo.exception.JimboException;
import jimbo.task.Task;
import jimbo.task.TaskList;

/**
 * Builds the user-facing text for every outcome Jimbo can produce: the
 * welcome/goodbye messages, the task list, confirmation messages for task
 * operations, and error messages. Methods return the message as a
 * {@code String} rather than printing it, so the same message text can be
 * shown in a console loop, a GUI, or asserted on directly in a test.
 */
public class Ui {
    private static final String LINE = "____________________________________________________________";

    /**
     * The list of commands Jimbo understands, shown at startup and whenever
     * the user types "help".
     */
    private static final String COMMAND_LIST = "Here's what I can nom on for you:\n"
            + "- todo <description>: add a plain task\n"
            + "- deadline <description> /by <d/M/yyyy HHmm>: add a task with a deadline\n"
            + "- event <description> /from <time> /to <time>: add a task with a start and end time\n"
            + "- list: show everything on my plate\n"
            + "- mark <n> / unmark <n>: mark a task done / not done\n"
            + "- update <n> /desc|/by|/from|/to <value>: change a task's details\n"
            + "- delete <n>: remove a task\n"
            + "- find <keyword>: search your tasks\n"
            + "- bye: I'll head off";

    /**
     * ASCII art banner shown above the greeting in the console UI. Left out
     * of the GUI's welcome message ({@link #showGuiWelcome()}) since the
     * chat window already shows Jimbo's picture.
     */
    private static final String BANNER = "   ___   _____  ___  ___ ______   _____ \n"
            + "  |_  | |_   _| |  \\/  | | ___ \\ |  _  |\n"
            + "    | |   | |   | .  . | | |_/ / | | | |\n"
            + "    | |   | |   | |\\/| | | ___ \\ | | | |\n"
            + "/\\__/ /  _| |_  | |  | | | |_/ / \\ \\_/ /\n"
            + "\\____/   \\___/  \\_|  |_/ \\____/   \\___/ \n";

    /**
     * Returns the horizontal divider line used to separate sections of
     * console output.
     */
    public String showLine() {
        return LINE;
    }

    /**
     * Returns the startup banner and welcome message, for the console UI.
     */
    public String showWelcome() {
        return BANNER + "\n" + showGuiWelcome();
    }

    /**
     * Returns the welcome message without the ASCII banner, for the GUI,
     * where the chat window's Jimbo avatar already establishes who's
     * talking.
     */
    public String showGuiWelcome() {
        return "*chomp chomp* Hiii, I'm Jimbo! Got any tasks for me to nom on?"
                + "\n\n" + COMMAND_LIST;
    }

    /**
     * Returns the goodbye message shown when the user types "bye".
     */
    public String showGoodbye() {
        return "Nom nom nom... byeee! *burp* Come back with more tasks soon!";
    }

    /**
     * Returns the command list on its own, e.g. in response to the "help"
     * command, for when the user needs a reminder after the welcome message
     * has scrolled out of view.
     */
    public String showHelp() {
        return COMMAND_LIST;
    }

    /**
     * Returns the numbered task list, e.g. in response to the "list" command.
     */
    public String showTaskList(TaskList tasks) {
        StringBuilder sb = new StringBuilder("Here's what's on my plate right now:");
        for (int i = 0; i < tasks.size(); i++) {
            sb.append("\n").append(i + 1).append(") ").append(tasks.get(i));
        }
        return sb.toString();
    }

    /**
     * Returns the confirmation shown after a task is marked or unmarked as
     * done.
     */
    public String showTaskMarked(Task task, boolean isDone) {
        String heading = isDone
                ? "Yum! Gobbled this one right up:"
                : "Aww, spat this one back out — not done yet:";
        return heading + "\n  " + task;
    }

    /**
     * Returns the confirmation shown after a task is added, including the
     * updated task count.
     */
    public String showTaskAdded(Task task, int taskCount) {
        return "*chomp* Added this to my plate:\n  " + task
                + "\nI've got " + taskCount + " tasks to munch through now.";
    }

    /**
     * Returns the confirmation shown after a task is deleted, including the
     * updated task count.
     */
    public String showTaskDeleted(Task task, int taskCount) {
        return "Pfft, tossed this one out:\n  " + task
                + "\n" + taskCount + " tasks left on my plate.";
    }

    /**
     * Returns the confirmation shown after a task's details are updated.
     */
    public String showTaskUpdated(Task task) {
        return "Mmm, tastes different now:\n  " + task;
    }

    /**
     * Returns the numbered list of tasks matching a search keyword, e.g. in
     * response to the "find" command.
     */
    public String showMatchingTasks(ArrayList<Task> matches) {
        if (matches.isEmpty()) {
            return "Sniff sniff... nothing on my plate smells like that.";
        }
        StringBuilder sb = new StringBuilder("Ooh, I sniffed out these tasty matches:");
        for (int i = 0; i < matches.size(); i++) {
            sb.append("\n").append(i + 1).append(") ").append(matches.get(i));
        }
        return sb.toString();
    }

    /**
     * Returns an error message in response to a {@link JimboException}.
     */
    public String showError(String message) {
        return "Yuck! " + message;
    }
}
