package yapper;

import java.io.PrintStream;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

/**
 * Reads console input and displays Yapper's messages and task listings.
 */
public class Ui {
    /** Horizontal divider printed between console responses. */
    private static final String SEPARATOR = "____________________________________________________________";
    /** Reader for command lines from standard input. */
    private final Scanner scanner = new Scanner(System.in);

    /** Destination for all response text. */
    private final PrintStream output;

    /**
     * Creates a console UI using standard output.
     */
    public Ui() {
        this(System.out);
    }

    /**
     * Creates a UI with an independent output destination.
     *
     * @param output Stream receiving response text.
     */
    public Ui(PrintStream output) {
        this.output = output;
    }

    /**
     * Returns whether another command is available, including piped input.
     *
     * @return Whether the input contains another line.
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Reads and trims the next command.
     *
     * @return Next input line with leading and trailing whitespace removed.
     * @throws java.util.NoSuchElementException If no input line remains.
     */
    public String readCommand() {
        return scanner.nextLine().trim();
    }

    /**
     * Separates responses visually.
     */
    public void showSeparator() {
        output.println(SEPARATOR);
    }

    /**
     * Displays the banner and greeting.
     */
    public void showWelcome() {
        String banner = "__   __                               \n"
                + "\\ \\ / /_ _ _ __  _ __   ___ _ __     \n"
                + " \\ V / _` | '_ \\| '_ \\ / _ \\ '__|    \n"
                + "  | | (_| | |_) | |_) |  __/ |       \n"
                + "  |_|\\__,_| .__/| .__/ \\___|_|       \n"
                + "           |_|   |_|                  \n";

        output.println(SEPARATOR);
        output.print(banner);
        output.println(" WELL, HELLO THERE! I'm Yapper, your spectacularly talkative task companion!");
        output.println(" I am positively BURSTING with enthusiasm to organize your life. What shall we do?");
        output.println(SEPARATOR);

    }

    /**
     * Displays the farewell on exit or end of input.
     */
    public void showGoodbye() {
        output.println(" FAREWELL, magnificent human! Yapper shall now stop talking--an historic occasion!");
        output.println(" Return soon, because your tasks and I will have an absolutely enormous"
                + " amount to discuss.");
        output.println(SEPARATOR);
    }

    /**
     * Prints the confirmation shown after a task is added.
     *
     * @param task Task that was added.
     * @param taskCount Current number of tasks.
     */
    public void printTaskAdded(Task task, int taskCount) {
        output.println(" OHOHO, MAGNIFICENT! I have triumphantly added this dazzling new task:");
        output.println("   " + task);
        output.println(" Your legendary list now contains " + taskCount
                + " task(s). Yes, I counted them personally!");
    }

    /**
     * Prints the confirmation shown after a task is deleted.
     *
     * @param task Task that was deleted.
     * @param taskCount Current number of tasks.
     */
    public void printTaskDeleted(Task task, int taskCount) {
        output.println(" BEHOLD! With one decisive flourish, I have removed this task from existence:");
        output.println("   " + task);
        output.println(" Your freshly streamlined list now contains " + taskCount
                + " task(s). Delightfully tidy!");
    }

    /**
     * Prints all tasks currently stored by the chatbot.
     *
     * @param tasks Tasks to display in order with one-based numbers.
     */
    public void printTaskList(List<Task> tasks) {
        output.println(" ATTENTION, EVERYONE! Yapper proudly presents your complete and glorious task list:");
        for (int i = 0; i < tasks.size(); i++) {
            output.println(" " + (i + 1) + "." + tasks.get(i));
        }
    }

    /**
     * Displays total, completed, and pending task counts without changing the list.
     *
     * @param tasks Tasks whose completion states should be counted.
     */
    public void printStatistics(List<Task> tasks) {
        int completed = 0;
        for (Task task : tasks) {
            if ("X".equals(task.getStatusIcon())) {
                completed++;
            }
        }
        output.println(" YOUR TASK STATISTICS:");
        output.println(" Total: " + tasks.size());
        output.println(" Completed: " + completed);
        output.println(" Pending: " + (tasks.size() - completed));
    }

    /**
     * Prints the running list of commands supported by the chatbot.
     */
    public void printHelp() {
        output.println(" PREPARE YOURSELF! Here is Yapper's impressively comprehensive command repertoire:");
        output.println("  todo <description>");
        output.println("  deadline <description> /by <yyyy-MM-dd HHmm>");
        output.println("  event <description> /from <yyyy-MM-dd HHmm> /to <yyyy-MM-dd HHmm>");
        output.println("  on <yyyy-MM-dd>");
        output.println("  find <keyword>");
        output.println("  list");
        output.println("  stats");
        output.println("  mark <task number>");
        output.println("  unmark <task number>");
        output.println("  delete <task number>");
        output.println("  help");
        output.println("  bye");
    }

    /**
     * Displays a command validation error.
     *
     * @param message Explanation to display.
     */
    public void showError(String message) {
        output.println(" WHOOPS-A-DAISY! Yapper has encountered a tiny dramatic complication: " + message);
    }

    /**
     * Displays search matches using original task numbers for later commands.
     *
     * @param keyword Nonempty search text validated by the parser.
     * @param tasks Task list to search without modifying it.
     */
    public void printMatchingTasks(String keyword, TaskList tasks) {
        output.println(" HERE ARE YOUR MATCHING TASKS:");
        List<Integer> indices = tasks.indicesMatching(keyword);
        for (int index : indices) {
            output.println(" " + (index + 1) + "." + tasks.get(index));
        }
        if (indices.isEmpty()) {
            output.println(" No matching tasks found. Try another keyword!");
        }
    }

    /**
     * Explains why startup stopped without overwriting unreadable data.
     *
     * @param message Explanation of the load failure.
     * @param path Storage file the user should repair or remove.
     */
    public void showLoadingError(String message, Path path) {
        output.println(" WHOOPS-A-DAISY! Yapper could not load your saved tasks: " + message);
        output.println(" Please repair or remove " + path + " before trying again.");
        showSeparator();
    }

    /**
     * Reports a failure to persist a task change.
     *
     * @param message Explanation of the save failure.
     */
    public void showSavingError(String message) {
        output.println(" WHOOPS-A-DAISY! Yapper could not save your tasks: " + message);
    }

    /**
     * Confirms the task's new completion state.
     *
     * @param task Task to display.
     * @param isDone Whether to show the completed or incomplete confirmation.
     */
    public void showTaskStatus(Task task, boolean isDone) {
        if (isDone) {
            output.println(" CONFETTI CANNONS! This task is now officially, unmistakably, spectacularly DONE:");
        } else {
            output.println(" PLOT TWIST! This task has returned to the thrilling realm of NOT DONE YET:");
        }
        output.println("  " + task);
    }

    /**
     * Displays date matches with their original task numbers.
     *
     * @param date Calendar date to display and match.
     * @param tasks Complete task list from which to select matches.
     */
    public void printTasksOnDate(LocalDate date, TaskList tasks) {
        output.println(" HERE ARE YOUR SCHEDULED TASKS ON "
                + date.format(java.time.format.DateTimeFormatter.ofPattern("MMM d uuuu")) + ":");
        List<Integer> indices = tasks.indicesOn(date);
        for (int index : indices) {
            output.println(" " + (index + 1) + "." + tasks.get(index));
        }
        if (indices.isEmpty()) {
            output.println(" None--your calendar is gloriously clear!");
        }
    }
}
