package kopi;

import java.io.PrintStream;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/** Handles all console input and output. */
public class Ui {
    private static final String LINE = "____________________________________________________________";
    private static final String BANNER = " _  __           _\n"
            + "| |/ /___  _ __ (_)\n"
            + "| ' // _ \\| '_ \\| |\n"
            + "| . \\ (_) | |_) | |\n"
            + "|_|\\_\\___/| .__/|_|\n"
            + "          |_|";

    private final PrintStream output;
    private final Scanner scanner;

    /** Creates a UI connected to standard input and output. */
    public Ui() {
        scanner = new Scanner(System.in);
        output = System.out;
    }

    /** Returns whether another command is available. */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /** Reads the next command. */
    public String readCommand() {
        return scanner.nextLine();
    }

    /** Shows Kopi's greeting. */
    public void showWelcome() {
        showLines(LINE, BANNER, "Hello! I'm Kopi.", "What can I do for you?", LINE);
    }

    /** Returns Kopi's farewell. */
    public String getGoodbye() {
        return "Bye. Hope to see you again soon!";
    }

    /** Returns all tasks with one-based numbering. */
    public String getTaskList(TaskList tasks) {
        return getNumberedTasks("Here are the tasks in your list:", tasks.getAll());
    }

    /** Returns tasks that match a search keyword. */
    public String getMatches(List<Task> matches) {
        return getNumberedTasks("Here are the matching tasks in your list:", matches);
    }

    /** Returns a message confirming that a task was marked complete. */
    public String getMarked(Task task) {
        return "Nice! I've marked this task as done:" + System.lineSeparator() + "  " + task;
    }

    /** Returns a message confirming that a task was marked incomplete. */
    public String getUnmarked(Task task) {
        return "OK, I've marked this task as not done yet:" + System.lineSeparator() + "  " + task;
    }

    /** Returns a message confirming that a task was deleted. */
    public String getDeleted(Task task, int remainingCount) {
        return "Noted. I've removed this task:" + System.lineSeparator()
                + "  " + task + System.lineSeparator()
                + "Now you have " + remainingCount + " tasks in the list.";
    }

    /** Returns a message confirming that a task was added. */
    public String getAdded(Task task) {
        return "added: " + task;
    }

    /** Returns a user-facing error message. */
    public String getError(String message) {
        return "OOPS!!! " + message;
    }

    /** Prints one complete response. */
    public void showResponse(String response) {
        showLines(response);
    }

    /** Shows a divider line. */
    public void showLine() {
        showLines(LINE);
    }

    private String getNumberedTasks(String heading, List<Task> tasks) {
        String numberedTasks = IntStream.range(0, tasks.size())
                .mapToObj(index -> (index + 1) + ". " + tasks.get(index))
                .collect(Collectors.joining(System.lineSeparator()));
        if (numberedTasks.isEmpty()) {
            return heading;
        }
        return heading + System.lineSeparator() + numberedTasks;
    }

    private void showLines(String... lines) {
        for (String line : lines) {
            output.println(line);
        }
    }
}
