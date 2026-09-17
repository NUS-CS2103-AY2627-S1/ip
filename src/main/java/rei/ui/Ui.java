package rei.ui;

import java.io.PrintStream;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

import rei.task.Task;

/** Handles all console input and output for Rei. */
public class Ui {
    private static final String DIVIDER = "------------------------------------------------------------";
    private static final DateTimeFormatter CURRENT_DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd MMMM yyyy, hh:mm a z", Locale.ENGLISH);
    private static final DateTimeFormatter TASK_DATE_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);

    private final Scanner scanner;
    private final PrintStream output;

    /** Creates a UI that reads commands from standard input. */
    public Ui() {
        scanner = new Scanner(System.in);
        output = System.out;
    }

    /** Creates an output-only UI used when another interface needs Rei's response as text. */
    private Ui(PrintStream output) {
        scanner = null;
        this.output = output;
    }

    /** Returns an output-only UI that writes responses to the supplied stream. */
    public static Ui forOutput(PrintStream output) {
        return new Ui(output);
    }

    /** Returns whether another command is available from the user. */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /** Reads the next command and removes surrounding whitespace. */
    public String readCommand() {
        return scanner.nextLine().trim();
    }

    /** Displays Rei's welcome banner and greeting. */
    public void showGreeting() {
        String banner = "\n██████╗ ███████╗██╗\n"
                + "██╔══██╗██╔════╝██║\n"
                + "██████╔╝█████╗  ██║\n"
                + "██╔══██╗██╔══╝  ██║\n"
                + "██║  ██║███████╗██║\n"
                + "╚═╝  ╚═╝╚══════╝╚═╝";
        output.println(banner);
        divider();
        output.println(ZonedDateTime.now().format(CURRENT_DATE_FORMAT));
        divider();
        output.println("Hey, I'm Rei, your calm study companion.");
        output.println("One task at a time. Try: todo read book");
        divider();
    }

    /** Displays the divider between command responses. */
    public void divider() {
        output.println(DIVIDER);
    }

    /** Displays an error message. */
    public void showError(String message) {
        output.println(message);
    }

    /** Displays a response produced by the chatbot core. */
    public void showResponse(String response) {
        output.println(response);
    }

    /** Displays a warning about invalid saved task records. */
    public void showSkippedTasksWarning(int skippedLines) {
        output.println("Warning: I skipped " + skippedLines
                + " invalid line(s) in the data file.");
        divider();
    }

    /** Displays a warning when saved tasks cannot be loaded. */
    public void showLoadingError() {
        output.println("Warning: Oh man I could not read the data file. Gonna create an empty list.");
        divider();
    }

    /** Displays the farewell message. */
    public void showExit() {
        output.println("See you soon. Make room for a little rest, too.");
    }

    /** Displays confirmation that a task was added. */
    public void showTaskAdded(Task task, int taskCount) {
        output.println("One less thing to remember. I've added: [" + task.getTaskType() + "]"
                + "[" + task.getStatusIcon() + "] " + task);
        output.println("You have a total of " + taskCount + " tasks in the list.");
    }

    /** Displays confirmation that a task was deleted. */
    public void showTaskDeleted(Task task, int taskCount) {
        output.println("Making room. Removed this task from your list:");
        output.println("  [" + task.getTaskType() + "]"
                + "[" + task.getStatusIcon() + "] " + task);
        output.println("Now you have " + taskCount + " tasks in the list.");
    }

    /** Displays confirmation that a task's completion status changed. */
    public void showTaskStatusChanged(Task task, boolean isDone) {
        if (isDone) {
            output.println("A little progress counts. Marked as done!\n");
        } else {
            output.println("No rush. Put it back on your list.\n");
        }
        output.println("[" + task.getTaskType() + "]"
                + "[" + task.getStatusIcon() + "] " + task);
    }

    /** Displays confirmation that a tag was added to a task. */
    public void showTaskTagged(Task task, int taskNumber, String tag) {
        output.println("Added " + tag + " to task " + taskNumber + ":");
        output.println("[" + task.getTaskType() + "]"
                + "[" + task.getStatusIcon() + "] " + task);
    }

    /** Displays all tasks in their current list order. */
    public void showTasks(List<Task> tasks) {
        if (tasks.isEmpty()) {
            output.println("Your list is clear. Add a task whenever you are ready.");
            return;
        }
        output.println("Here are the tasks in your list:");
        output.println("No. of tasks: " + tasks.size());
        for (int i = 0; i < tasks.size(); i++) {
            showNumberedTask(i + 1, tasks.get(i));
        }
    }

    /** Displays the heading for tasks occurring on a date. */
    public void showTasksOnDateHeading(LocalDate date) {
        output.println("Tasks occurring on " + date.format(TASK_DATE_FORMAT) + ":");
    }

    /** Displays one task with its position in the complete task list. */
    public void showNumberedTask(int taskNumber, Task task) {
        output.println(taskNumber + ".[" + task.getTaskType() + "]"
                + "[" + task.getStatusIcon() + "] " + task);
    }

    /** Displays that no deadline or event occurs on the requested date. */
    public void showNoTasksOnDate() {
        output.println("No deadlines or events occur on this date.");
    }

    /** Displays the heading for a keyword search result. */
    public void showMatchingTasksHeading() {
        output.println("Here are the matching tasks in your list:");
    }

    /** Displays that no task description contains the requested keyword. */
    public void showNoMatchingTasks() {
        output.println("No matching tasks found.");
    }

    /** Releases the input scanner when Rei exits. */
    public void close() {
        scanner.close();
    }
}
