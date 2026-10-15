package carlo.ui;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

import carlo.task.Task;

/**
 * Handles all interaction with the user: printing output to the console
 * and reading commands from standard input.
 */
public class Ui {
    private static final DateTimeFormatter ON_DATE_DISPLAY_FORMAT = DateTimeFormatter.ofPattern("MMM d yyyy");
    private static final String LINE = "____________________________________________________________";

    private final Scanner scanner;

    public Ui() {
        this.scanner = new Scanner(System.in);
    }

    /** Prints the startup banner and greeting. */
    public void showGreeting() {
        System.out.println(LINE);
        System.out.println("""
                  ██████╗                     \s
                 ██╔════╝                     \s
                 ██║       █████╗ ██████╗ ██╗      ██████╗\s
                 ██║      ██╔══██╗██╔══██╗██║     ██╔═══██╗
                 ██║      ███████║██████╔╝██║     ██║   ██║
                 ██║      ██╔══██║██╔══██╗██║     ██║   ██║
                 ╚███████╗██║  ██║██║  ██║███████╗╚██████╔╝
                  ╚══════╝╚═╝  ╚═╝╚═╝  ╚══════╝ ╚═════╝\s
                Cheers! My name is Carlo!
                I can help you to list down anything!""");
        System.out.println(LINE);
        showHelp();
        System.out.println(LINE);
    }

    /** Prints usage instructions. */
    public void showHelp() {
        System.out.println("""
                Input your items in order! ({task} {name} {time})
                    possible list items {task}:
                        "todo": tasks without any date/time attached
                        "deadline": for tasks that need to be done by a specific time (do specify using /by ___ )
                        "event": for tasks that start and end at specific times (do specify using /from ___ /to ___)
                    Dates can be given as yyyy-mm-dd or yyyy-mm-dd HHmm, e.g. 2019-12-02 or 2019-12-02 1800!
                Say 'list' and I will show you your list!
                Say 'on {date}' (e.g. on 2019-12-02, or on today/tomorrow/yesterday) to see what's happening that day!
                Say 'find {keyword}' (e.g. find book) to search for tasks by keyword!
                When you're done, just say 'bye'!""");
    }

    /** Prints the divider line used before and after each command's output. */
    public void showLine() {
        System.out.println(LINE);
    }

    /** Prints the farewell message. */
    public void showGoodbye() {
        System.out.println(" Byeeee! Love always!");
    }

    /** Prints an error message followed by the help text. */
    public void showError(String message) {
        System.out.println("Ohno!! " + message);
        showHelp();
    }

    /** Prints a message shown when saved tasks could not be read from disk. */
    public void showLoadingError() {
        System.out.println(" Uh oh, I couldn't read your saved tasks! Starting with an empty list...");
    }

    /** Reads the next full line of user input. */
    public String readCommand() {
        return scanner.nextLine();
    }

    /** Returns whether there is another line of input to read. */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /** Prints the full task list. */
    public void showTaskList(List<Task> tasks) {
        System.out.println(" Here's your list!");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println(" " + (i + 1) + "." + tasks.get(i));
        }
    }

    /** Prints the tasks occurring on a given date, or a "nothing on" message. */
    public void showTasksOnDate(LocalDate date, List<Task> tasks, boolean[] matches) {
        System.out.println(" Here's what's happening on " + date.format(ON_DATE_DISPLAY_FORMAT) + ":");
        boolean foundAny = false;
        for (int i = 0; i < tasks.size(); i++) {
            if (matches[i]) {
                System.out.println(" " + (i + 1) + "." + tasks.get(i));
                foundAny = true;
            }
        }
        if (!foundAny) {
            System.out.println(" Nothing on that day! Free day yay!");
        }
    }

    /** Prints confirmation that a task was added. */
    public void showTaskAdded(Task task, int taskCount) {
        System.out.println(" added: " + task);
        System.out.println(" You now have " + taskCount + " tasks in your list!");
    }

    /** Prints confirmation that a task was marked as done. */
    public void showTaskMarked(Task task) {
        System.out.println(" YAY! thank you, next!");
        System.out.println("   " + task);
    }

    /** Prints confirmation that a task was marked as not done. */
    public void showTaskUnmarked(Task task) {
        System.out.println(" Awman... okay unmarked for now...");
        System.out.println("   " + task);
    }

    /** Prints confirmation that a task was deleted. */
    public void showTaskDeleted(Task task, int taskCount) {
        System.out.println(" Okay okay, I've removed this task!");
        System.out.println("   " + task);
        System.out.println(" You now have " + taskCount + " tasks in your list!");
    }

    /**
     * Prints the tasks that matched a search keyword, numbered independently
     * of their position in the full task list, or a "nothing found" message
     * if none matched.
     *
     * @param tasks the full list of tasks that was searched
     * @param matches a boolean for each task in {@code tasks}, in the same
     *        order, indicating whether that task matched the search keyword
     */
    public void showMatchingTasks(List<Task> tasks, boolean[] matches) {
        System.out.println(" Here are the matching tasks in your list:");
        int count = 0;
        for (int i = 0; i < tasks.size(); i++) {
            if (matches[i]) {
                count++;
                System.out.println(" " + count + "." + tasks.get(i));
            }
        }
        if (count == 0) {
            System.out.println(" I couldn't find any matching tasks!");
        }
    }
}
