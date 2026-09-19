package lizzy.ui;

import java.io.InputStream;
import java.io.PrintStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

import lizzy.task.Task;
import lizzy.task.TaskList;

/**
 * Handles all console input and output for Lizzy.
 */
public class Ui {
    private static final String DIVIDER = "____________________________________________________________";
    private static final DateTimeFormatter DISPLAY_DATE_FORMAT =
            DateTimeFormatter.ofPattern("MMM d yyyy", Locale.ENGLISH);

    /**
     * Reads one command at a time from standard input.
     */
    private final Scanner scanner;
    /**
     * Writes user-facing output to the current interface.
     */
    private final PrintStream outputStream;

    /**
     * Creates a UI that reads commands from standard input.
     */
    public Ui() {
        this(System.in, System.out);
    }

    /**
     * Creates a UI with caller-provided input and output streams.
     *
     * @param inputStream the source of user commands
     * @param outputStream the destination for user-facing output
     */
    public Ui(InputStream inputStream, PrintStream outputStream) {
        scanner = new Scanner(inputStream);
        this.outputStream = outputStream;
    }

    /**
     * Displays Lizzy's greeting and banner.
     */
    public void showWelcome() {
        String banner = "    __    _\n"
                + "   / /   (_)_______  __  __\n"
                + "  / /   / /_  /_  / / / / /\n"
                + " / /___/ / / /_/ /_/ /_/ /\n"
                + "/_____/_/ /___/___/\\__, /\n"
                + "                  /____/\n";
        showDivider();
        outputStream.print(banner);
        outputStream.println("Hello! I'm Lizzy.");
        outputStream.println("What brings you here today?");
        outputStream.println("Type help whenever you would like a quick command guide.");
        showDivider();
    }

    /**
     * Returns whether another user command is available.
     *
     * @return {@code true} if standard input contains another line
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Reads and trims the next user command.
     *
     * @return the next command without leading or trailing whitespace
     */
    public String readCommand() {
        return scanner.nextLine().strip();
    }

    /**
     * Displays the standard divider line.
     */
    public void showDivider() {
        outputStream.println(DIVIDER);
    }

    /**
     * Displays Lizzy's farewell.
     */
    public void showGoodbye() {
        outputStream.println("Goodbye! May your plans prosper—and leave you a little leisure.");
    }

    /**
     * Displays the supported command formats and their purposes.
     */
    public void showHelp() {
        outputStream.println("A brief guide, should memory prove uncooperative:");
        outputStream.println("Add tasks:");
        outputStream.println("  • todo <description> — undated task");
        outputStream.println("  • deadline <description> /by <yyyy-MM-dd> — task due on a date");
        outputStream.println("  • event <description> /on <yyyy-MM-dd> — one-day event");
        outputStream.println("  • event <description> /on <yyyy-MM-dd> /from <HH:mm> /to <HH:mm> — timed event");
        outputStream.println("  • event <description> /from <yyyy-MM-dd> /to <yyyy-MM-dd> — date-range event");
        outputStream.println("  • within <description> /from <yyyy-MM-dd> /to <yyyy-MM-dd>"
                + " — flexible-period task");
        outputStream.println();
        outputStream.println("View tasks:");
        outputStream.println("  • list — show every task");
        outputStream.println("  • find <keyword> — partially search descriptions");
        outputStream.println("  • on <yyyy-MM-dd> — show one day's schedule");
        outputStream.println();
        outputStream.println("Manage tasks:");
        outputStream.println("  • mark <number> — complete a task");
        outputStream.println("  • unmark <number> — reopen a task");
        outputStream.println("  • delete <number> — remove a task");
        outputStream.println("  • bye — close Lizzy");
        outputStream.println("Dates use yyyy-MM-dd (for example, 2026-09-10); times use HH:mm"
                + " (for example, 14:30).");
        outputStream.println("A little precision saves much puzzlement.");
    }

    /**
     * Displays a user-facing error message.
     *
     * @param message the message to display
     */
    public void showError(String message) {
        outputStream.println(message);
    }

    /**
     * Displays every task in the supplied task list.
     *
     * @param tasks the tasks to display
     */
    public void showTaskList(TaskList tasks) {
        if (tasks.size() == 0) {
            outputStream.println("Your list is blissfully free of obligations.");
            return;
        }

        outputStream.println("Let us see what has found its way onto your list:");
        for (int i = 0; i < tasks.size(); i++) {
            outputStream.println((i + 1) + "." + tasks.get(i));
        }
    }

    /**
     * Displays tasks that matched a description search while retaining their list numbers.
     *
     * @param tasks the complete task list
     * @param matchingTaskNumbers the one-based numbers of the matching tasks
     */
    public void showMatchingTasks(TaskList tasks, List<Integer> matchingTaskNumbers) {
        if (matchingTaskNumbers.isEmpty()) {
            outputStream.println("Not a single task answers to that description.");
            return;
        }

        outputStream.println("These tasks answer to your search:");
        for (int taskNumber : matchingTaskNumbers) {
            outputStream.println(taskNumber + "." + tasks.get(taskNumber - 1));
        }
    }

    /**
     * Displays confirmation after adding a todo.
     *
     * @param task the todo that was added
     * @param numberOfTasks the new number of tasks in the list
     */
    public void showTodoAdded(Task task, int numberOfTasks) {
        outputStream.println("Here comes another matter to keep track of:");
        outputStream.println("  " + task);
        showTaskCount(numberOfTasks);
    }

    /**
     * Displays confirmation after adding a deadline.
     *
     * @param task the deadline that was added
     * @param numberOfTasks the new number of tasks in the list
     */
    public void showDeadlineAdded(Task task, int numberOfTasks) {
        outputStream.println("A deadline, then. We'd better not keep it waiting.");
        outputStream.println("  " + task);
        showTaskCount(numberOfTasks);
    }

    /**
     * Displays confirmation after adding an event.
     *
     * @param task the event that was added
     * @param numberOfTasks the new number of tasks in the list
     */
    public void showEventAdded(Task task, int numberOfTasks) {
        outputStream.println("An engagement! I've added it to your list:");
        outputStream.println("  " + task);
        showTaskCount(numberOfTasks);
    }

    /**
     * Displays confirmation after adding a task with an inclusive completion period.
     *
     * @param task the period task that was added
     * @param numberOfTasks the new number of tasks in the list
     */
    public void showPeriodTaskAdded(Task task, int numberOfTasks) {
        outputStream.println("A period to work within—I've added it to your list:");
        outputStream.println("  " + task);
        showTaskCount(numberOfTasks);
    }

    /**
     * Displays confirmation after a task is marked as complete.
     *
     * @param task the task marked as complete
     */
    public void showTaskMarked(Task task) {
        outputStream.println("Very good! That is one matter settled:");
        outputStream.println("  " + task);
    }

    /**
     * Displays confirmation after a task is marked as incomplete.
     *
     * @param task the task marked as incomplete
     */
    public void showTaskUnmarked(Task task) {
        outputStream.println("Ah, it seems this matter is not quite settled:");
        outputStream.println("  " + task);
    }

    /**
     * Displays confirmation after a task is deleted.
     *
     * @param task the task that was deleted
     * @param numberOfTasks the number of tasks remaining in the list
     */
    public void showTaskDeleted(Task task, int numberOfTasks) {
        outputStream.println("And away it goes—one less matter on the list:");
        outputStream.println("  " + task);
        showTaskCount(numberOfTasks);
    }

    /**
     * Displays dated tasks that are relevant on the specified date.
     *
     * @param tasks the task list to search
     * @param date the requested calendar date
     */
    public void showTasksOnDate(TaskList tasks, LocalDate date) {
        boolean hasFoundTask = false;
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).occursOn(date)) {
                if (!hasFoundTask) {
                    outputStream.println("On " + date.format(DISPLAY_DATE_FORMAT)
                            + ", your schedule has the following in store:");
                    hasFoundTask = true;
                }
                outputStream.println((i + 1) + "." + tasks.get(i));
            }
        }
        if (!hasFoundTask) {
            outputStream.println(date.format(DISPLAY_DATE_FORMAT)
                    + " appears to make no demands upon you.");
        }
    }

    /**
     * Displays the current number of tasks after a task has been added.
     *
     * @param numberOfTasks the total number of tasks now in the list
     */
    private void showTaskCount(int numberOfTasks) {
        String taskNoun = numberOfTasks == 1 ? "task" : "tasks";
        outputStream.println("That makes " + numberOfTasks + " " + taskNoun + " awaiting your attention.");
    }
}
