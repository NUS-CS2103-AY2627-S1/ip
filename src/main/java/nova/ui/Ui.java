package nova.ui;

import java.io.PrintStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;

import nova.exception.NovaException;
import nova.task.Task;
import nova.task.TaskList;

/** Handles Nova's console input and output. */
public class Ui {
    /** The divider used between console interactions. */
    private static final String DIVIDER = "____________________________________________________________";

    /** Reads commands from the console. */
    private final Scanner scanner;

    /** Receives user-facing output. */
    private final PrintStream output;

    /** Creates a console UI using standard input. */
    public Ui() {
        this(System.out);
    }

    /** Creates a UI that reads standard input and writes to the given output. */
    public Ui(PrintStream output) {
        scanner = new Scanner(System.in);
        this.output = output;
    }

    /** Prints Nova's greeting. */
    public void showGreeting() {
        output.println(DIVIDER);
        output.print(" _   _    ___    _   _    _\n"
                + "| \\ | |  / _ \\  | | | |  / \\\n"
                + "|  \\| | | | | | | | | | / _ \\\n"
                + "| |\\  | | |_| |  \\ V / / ___ \\\n"
                + "|_| \\_|  \\___/    \\_/ /_/   \\_\\\n");
        output.println("Hello! I'm Nova.");
        output.println("What can I do for you?");
        output.println(DIVIDER);
    }

    /** Returns whether another command is available. */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /** Reads the next complete command. */
    public String readCommand() {
        return scanner.nextLine();
    }

    /** Prints the interaction divider. */
    public void showDivider() {
        output.println(DIVIDER);
    }

    /** Prints the farewell and its closing divider. */
    public void showFarewell() {
        output.println("Bye. Hope to see you again soon!");
        showDivider();
    }

    /** Prints all tasks in their numbered list format. */
    public void showTaskList(TaskList tasks) {
        output.println(" Here are the tasks in your list:");
        showNumberedTasks(tasks);
    }

    /** Prints matching tasks in a newly numbered list or a no-results message. */
    public void showMatchingTasks(ArrayList<Task> tasks) {
        if (tasks.isEmpty()) {
            output.println(" No matching tasks found.");
            return;
        }
        output.println(" Here are the matching tasks in your list:");
        showNumberedTasks(tasks);
    }

    /** Prints tasks with one-based display numbers. */
    private void showNumberedTasks(Iterable<Task> tasks) {
        int taskNumber = 1;
        for (Task task : tasks) {
            output.println(" " + taskNumber + "." + task);
            taskNumber++;
        }
    }

    /** Prints tasks matching a requested date. */
    public void showTasksOnDate(LocalDate date, ArrayList<Task> tasks) {
        output.println(" Tasks on " + date + ":");
        for (Task task : tasks) {
            output.println(" " + task);
        }
    }

    /** Prints a successful task-addition confirmation. */
    public void showAdded(Task task, int taskCount) {
        output.println(" Got it. I've added this task:");
        output.println("  " + task);
        output.println(" Now you have " + taskCount + " tasks in the list.");
    }

    /** Prints a completion confirmation. */
    public void showMarkedDone(Task task) {
        output.println(" Nice! I've marked this task as done:");
        output.println("   " + task);
    }

    /** Prints an uncompletion confirmation. */
    public void showMarkedNotDone(Task task) {
        output.println(" OK, I've marked this task as not done yet:");
        output.println("   " + task);
    }

    /** Prints a deletion confirmation. */
    public void showDeleted(Task task, int taskCount) {
        output.println(" Noted. I've removed this task:");
        output.println("   " + task);
        output.println(" Now you have " + taskCount + " tasks in the list.");
    }

    /** Prints a tag confirmation for the updated task. */
    public void showTagged(Task task) {
        output.println(" Noted. I've added a tag to this task:");
        output.println("   " + task);
    }

    /** Prints an untag confirmation for the updated task. */
    public void showUntagged(Task task) {
        output.println(" Noted. I've removed the tag from this task:");
        output.println("   " + task);
    }

    /** Prints a user-facing error. */
    public void showError(NovaException exception) {
        output.println(" " + exception.getMessage());
    }
}
