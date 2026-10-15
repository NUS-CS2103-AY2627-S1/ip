package kafka.ui;

import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;

import kafka.task.Task;
import kafka.task.TaskList;

/**
 * Handles console input and displays messages to the user.
 */
public class Ui {
    /** Outer boundary shared by console messages and the GUI's divider removal. */
    public static final String CONSOLE_DIVIDER = "_".repeat(60);
    private static final String UNKNOWN_COMMAND_MESSAGE =
            "(づ｡◕‿‿◕｡)づ Sowwy I don't know that command... pwease try todo, deadline, event, "
            + "list, find, mark, unmark, delete, rename, snooze, or bye.";
    private static final String BANNER = "        /\\_/\\\n"
            + "       ( o.o )     K A F K A\n"
            + "       > 0 <        \n";
    private final Scanner scanner;

    /**
     * Creates a console UI that reads from standard input.
     */
    public Ui() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * Reads the user's next command from the console.
     *
     * @return Complete line entered by the user.
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /**
     * Displays a response in the console.
     *
     * @param response Formatted response to display.
     */
    public void showResponse(String response) {
        System.out.println(response);
    }

    /**
     * Returns the formatted task list for display by any user interface.
     *
     * @param tasks Task list to display.
     * @return Formatted task-list response.
     */
    public String formatTaskList(TaskList tasks) {
        List<Task> displayedTasks = tasks.getTasks();
        StringBuilder response = new StringBuilder(CONSOLE_DIVIDER)
                .append('\n')
                .append("Here's your to-dos, my fav hustler >////<")
                .append('\n');
        if (displayedTasks.isEmpty()) {
            response.append('\n').append("You have no tasks lined up king >0<").append('\n');
        }
        for (int i = 0; i < displayedTasks.size(); i++) {
            response.append(i + 1)
                    .append('.')
                    .append(displayedTasks.get(i).display())
                    .append('\n');
        }
        return response.append(CONSOLE_DIVIDER).toString();
    }

    /**
     * Returns matching tasks with their original one-based task numbers.
     *
     * @param tasks Complete task list used by editing commands.
     * @param matchingTasks Tasks whose displayed text contains the keyword.
     * @return Formatted matching-task response.
     */
    public String formatMatchingTasks(TaskList tasks, List<Task> matchingTasks) {
        StringBuilder response = new StringBuilder(CONSOLE_DIVIDER)
                .append('\n')
                .append("(*°ω°) I worked hard to find the matching tasks in your list king:")
                .append('\n');
        List<Task> allTasks = tasks.getTasks();
        for (int i = 0; i < allTasks.size(); i++) {
            if (!matchingTasks.contains(allTasks.get(i))) {
                continue;
            }
            response.append(i + 1)
                    .append('.')
                    .append(allTasks.get(i).display())
                    .append('\n');
        }
        return response.append(CONSOLE_DIVIDER).toString();
    }

    /**
     * Returns confirmation that a task was added.
     *
     * @param task Task that joined the list.
     * @param taskCount Number of tasks now stored.
     * @return Formatted task-added response.
     */
    public String formatTaskAdded(Task task, int taskCount) {
        String taskWord = taskCount == 1 ? "task" : "tasks";
        return CONSOLE_DIVIDER + "\n"
                + "Yippee!!! I've added this task:\n"
                + "  " + task.display() + "\n"
                + "Now you have " + taskCount + " " + taskWord
                + " in the list. What a legend. ᕦ(˘ω˘)ᕤ\n"
                + CONSOLE_DIVIDER;
    }

    /**
     * Returns confirmation that a task was marked as completed.
     *
     * @param taskDisplay Updated display text for the completed task.
     * @return Formatted task-marked response.
     */
    public String formatTaskMarked(String taskDisplay) {
        return CONSOLE_DIVIDER + "\n"
                + "Ur such a baddie (๑♡⌓♡๑)!! I've marked this task as done:\n"
                + "  " + taskDisplay + "\n"
                + CONSOLE_DIVIDER;
    }

    /**
     * Returns confirmation that a task was marked as incomplete.
     *
     * @param taskDisplay Updated display text for the incomplete task.
     * @return Formatted task-unmarked response.
     */
    public String formatTaskUnmarked(String taskDisplay) {
        return CONSOLE_DIVIDER + "\n"
                + "Awww issok my g ✧(ꈍᴗꈍ)✧, I've marked this task as not done yet:\n"
                + "  " + taskDisplay + "\n"
                + CONSOLE_DIVIDER;
    }

    /**
     * Returns confirmation that a task was deleted.
     *
     * @param task Task removed from the list.
     * @param taskCount Number of tasks still stored.
     * @return Formatted task-deleted response.
     */
    public String formatTaskDeleted(Task task, int taskCount) {
        String taskWord = taskCount == 1 ? "task" : "tasks";
        return CONSOLE_DIVIDER + "\n"
                + "Aight. I've yeeted this task:\n"
                + "  " + task.display() + "\n"
                + "Now you have " + taskCount + " " + taskWord + " in the list.\n"
                + CONSOLE_DIVIDER;
    }

    /**
     * Returns confirmation that a task was renamed.
     *
     * @param oldDisplay Task display before renaming.
     * @param newDisplay Task display after renaming.
     * @return Formatted task-renamed response.
     */
    public String formatTaskRenamed(String oldDisplay, String newDisplay) {
        return CONSOLE_DIVIDER + "\n"
                + "Gotcha I've renamed this task 0w0:\n"
                + "  " + oldDisplay + "\n"
                + "to:\n"
                + "  " + newDisplay + "\n"
                + CONSOLE_DIVIDER;
    }

    /**
     * Returns confirmation that a task was rescheduled.
     *
     * @param oldDisplay Task display before rescheduling.
     * @param newDisplay Task display after rescheduling.
     * @return Formatted task-snoozed response.
     */
    public String formatTaskSnoozed(String oldDisplay, String newDisplay) {
        return CONSOLE_DIVIDER + "\n"
                + "Gotcha, I've rescheduled this task:\n"
                + "  " + oldDisplay + "\n"
                + "to:\n"
                + "  " + newDisplay + " (♥ω♥*)\n"
                + CONSOLE_DIVIDER;
    }

    /**
     * Returns the response for an unrecognized command.
     *
     * @return Formatted unknown-command response.
     */
    public String formatUnknownCommand() {
        return CONSOLE_DIVIDER + "\n" + UNKNOWN_COMMAND_MESSAGE + "\n" + CONSOLE_DIVIDER;
    }

    /**
     * Returns an expected error for display by any user interface.
     *
     * @param message User-facing explanation of the problem.
     * @return Formatted error response.
     */
    public String formatError(String message) {
        return CONSOLE_DIVIDER + "\n" + message + "\n" + CONSOLE_DIVIDER;
    }

    /**
     * Asks for explicit permission before replacing a corrupted task file.
     * Repeats the prompt until the user enters yes or no.
     *
     * @param filePath Corrupted task file that would be replaced.
     * @return {@code true} only when the user approves the overwrite.
     */
    public boolean shouldOverwriteStorage(Path filePath) {
        while (true) {
            System.out.println("The task data file may be corrupted:");
            System.out.println("  " + filePath);
            System.out.print("Overwrite it with an empty task list and continue? (yes/no): ");

            if (!scanner.hasNextLine()) {
                return false;
            }
            String response = scanner.nextLine().trim();
            if (response.equalsIgnoreCase("yes") || response.equalsIgnoreCase("y")) {
                return true;
            }
            if (response.equalsIgnoreCase("no") || response.equalsIgnoreCase("n")) {
                return false;
            }
            System.out.println("Please enter yes or no. ✧(ꈍᴗꈍ)✧");
        }
    }

    /**
     * Returns directions to the file that needs to be inspected or repaired.
     *
     * @param filePath Task file the user should inspect.
     * @return Formatted storage-file directions.
     */
    public String formatStorageFileLocation(Path filePath) {
        return "Your task data was not changed.\n"
                + "Please inspect or repair this file before restarting Kafka:\n"
                + "  " + filePath + "\n"
                + CONSOLE_DIVIDER;
    }

    /**
     * Returns confirmation that the user-approved corrupted file was replaced.
     *
     * @return Formatted storage-overwrite confirmation.
     */
    public String formatStorageOverwritten() {
        return "The corrupted task file was replaced. Starting with an empty list.\n"
                + CONSOLE_DIVIDER;
    }

    /**
     * Returns Kafka's banner and welcome message.
     *
     * @return Formatted greeting response.
     */
    public String formatGreeting() {
        return CONSOLE_DIVIDER + "\n"
                + BANNER
                + "Heyy skinny legend! (⊃✿ ･ิω･ิ)⊃ I'm Kafka.\n"
                + "What can ur kitten do for you meow? (≧◡≦)\n"
                + CONSOLE_DIVIDER;
    }

    /**
     * Returns Kafka's farewell response.
     *
     * @return Formatted farewell response.
     */
    public String formatFarewell() {
        return "Bye babe~ Hope we bump into each other soon!(˶˘ ³˘(⌒❤‿❤⌒)\n" + CONSOLE_DIVIDER;
    }

    /**
     * Releases the console scanner when the chatbot session ends.
     */
    public void close() {
        scanner.close();
    }
}
