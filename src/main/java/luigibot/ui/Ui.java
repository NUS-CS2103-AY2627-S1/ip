package luigibot.ui;

import java.io.PrintWriter;
import java.io.Writer;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

import luigibot.task.Task;
import luigibot.task.TaskList;

/**
 * Handles console input and output for LuigiBot.
 */
public class Ui {
    private static final String LINE = "____________________________________________________________";
    private static final DateTimeFormatter DATE_DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd uuuu", Locale.ENGLISH);

    private final Scanner scanner;
    private final PrintWriter output;

    /**
     * Creates a user interface that reads from standard input.
     */
    public Ui() {
        this.scanner = new Scanner(System.in);
        this.output = new PrintWriter(System.out, true);
    }

    /**
     * Creates a user interface that writes to the specified destination.
     *
     * @param output destination for LuigiBot's responses.
     */
    public Ui(Writer output) {
        this.scanner = null;
        this.output = new PrintWriter(output, true);
    }

    /**
     * Reads the next command entered by the user.
     *
     * @return full command entered by the user.
     */
    public String readCommand() {
        if (this.scanner == null) {
            throw new IllegalStateException("This user interface does not accept input");
        }
        return this.scanner.nextLine();
    }

    /**
     * Closes the input scanner.
     */
    public void close() {
        if (this.scanner != null) {
            this.scanner.close();
        }
    }

    /**
     * Shows LuigiBot's welcome banner and greeting.
     */
    public void showGreeting() {
        this.output.println(LINE);
        String banner = ".____          .__       .____________        __   \n"
                + "|    |    __ __|__| ____ |__\\______   \\ _____/  |_\n"
                + "|    |   |  |  \\  |/ ___\\|  ||    |  _//  _ \\   __\\\n"
                + "|    |___|  |  /  / /_/  >  ||    |   (  <_> )  | \n"
                + "|_______ \\____/|__\\___  /|__||______  /\\____/|__|\n"
                + "        \\/       /_____/            \\/             \n";
        this.output.print(banner);
        this.output.println(LINE);
        this.output.println("Its a-me,LuigiBot!");
        this.output.println("What can I do for you?");
        this.output.println(LINE);
    }

    /**
     * Shows LuigiBot's goodbye message.
     */
    public void showGoodbye() {
        this.output.println("Mama mia! Leaving already? Cya soon!");
        this.output.println(LINE);
    }

    /**
     * Shows an error message between separator lines.
     *
     * @param message error message to show.
     */
    public void showError(String message) {
        this.showMessage(message);
    }

    /**
     * Shows confirmation that a task was added.
     *
     * @param task task that was added.
     * @param taskCount number of stored tasks after adding the task.
     */
    public void showTaskAdded(Task task, int taskCount) {
        this.showMessage(
                "Okie-dokie! Luigi added this task:",
                "  " + task,
                "You've-a got " + taskCount + " tasks now!");
    }

    /**
     * Shows an error for events that overlap with an event about to be added.
     *
     * @param tasks stored tasks.
     * @param clashingIndexes zero-based indexes of clashing events.
     */
    public void showEventClashes(TaskList tasks, List<Integer> clashingIndexes) {
        this.output.println(LINE);
        this.output.println("Mamma mia! Luigi can't-a add that Event because it clashes with:");
        for (int index : clashingIndexes) {
            this.output.println((index + 1) + "." + tasks.getTasks().get(index));
        }
        this.output.println(LINE);
    }

    /**
     * Shows confirmation that a task was marked as done.
     *
     * @param task task that was marked.
     */
    public void showTaskMarked(Task task) {
        this.showMessage(
                "Nice-a! Luigi marked this task as done:",
                "  " + task);
    }

    /**
     * Shows confirmation that a task was marked as not done.
     *
     * @param task task that was unmarked.
     */
    public void showTaskUnmarked(Task task) {
        this.showMessage(
                "No problem! Luigi marked this task as not done:",
                "  " + task);
    }

    /**
     * Shows confirmation that a task was deleted.
     *
     * @param task task that was deleted.
     * @param taskCount number of stored tasks after deleting the task.
     */
    public void showTaskDeleted(Task task, int taskCount) {
        this.showMessage(
                "Okie-dokie! Luigi removed this task:",
                "  " + task,
                "You've-a got " + taskCount + " tasks now!");
    }

    /**
     * Shows all stored tasks using their original task numbers.
     *
     * @param tasks stored tasks.
     */
    public void showTaskList(TaskList tasks) {
        this.output.println(LINE);
        this.output.println("Let's-a see what Luigi has on the list:");
        for (int i = 0; i < tasks.size(); i++) {
            this.output.println((i + 1) + "." + tasks.getTasks().get(i));
        }
        this.output.println(LINE);
    }

    /**
     * Shows stored tasks that occur on a specified date.
     *
     * @param date date being searched.
     * @param tasks stored tasks.
     * @param matchingIndexes indexes of tasks occurring on the date.
     */
    public void showTasksOnDate(LocalDate date, TaskList tasks,
                                List<Integer> matchingIndexes) {
        this.output.println(LINE);
        if (matchingIndexes.isEmpty()) {
            this.output.println("Mamma mia! Luigi found-a no tasks on "
                    + date.format(DATE_DISPLAY_FORMAT) + ".");
        } else {
            this.output.println("Luigi found-a these tasks on "
                    + date.format(DATE_DISPLAY_FORMAT) + ":");
            for (int index : matchingIndexes) {
                this.output.println((index + 1) + "." + tasks.getTasks().get(index));
            }
        }
        this.output.println(LINE);
    }

    /**
     * Shows tasks whose descriptions contain a keyword.
     *
     * @param keyword keyword being searched.
     * @param tasks stored tasks.
     * @param matchingIndexes indexes of matching tasks.
     */
    public void showTasksMatchingKeyword(String keyword, TaskList tasks,
                                         List<Integer> matchingIndexes) {
        this.output.println(LINE);
        if (matchingIndexes.isEmpty()) {
            this.output.println("Mamma mia! Luigi found-a no tasks matching \""
                    + keyword + "\".");
        } else {
            this.output.println("Here are the matching tasks in Luigi's list:");
            for (int index : matchingIndexes) {
                this.output.println((index + 1) + "." + tasks.getTasks().get(index));
            }
        }
        this.output.println(LINE);
    }

    /**
     * Shows a message between separator lines.
     *
     * @param lines lines that make up the message.
     */
    private void showMessage(String... lines) {
        this.output.println(LINE);
        for (String line : lines) {
            this.output.println(line);
        }
        this.output.println(LINE);
    }
}
