package jarvis;

import java.util.List;
import java.util.Scanner;
import java.util.stream.IntStream;

/** Handles all console input and output for Jarvis. */
public class Ui {
    /** Text printed between user interactions. */
    private static final String SEPARATOR = "____________________________________________________________";
    /** Reads commands from standard input. */
    private final Scanner scanner;

    /** Creates a UI that reads commands from standard input. */
    public Ui() {
        scanner = new Scanner(System.in);
    }

    /** Displays Jarvis's introductory greeting. */
    public void showWelcome() {
        showSeparator();
        System.out.println("Jarvis");
        System.out.println("Hello! I'm Jarvis.");
        System.out.println("What can I do for you?");
        showSeparator();
    }

    /** Returns whether another command is available from the user. */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /** Reads and returns the next command from the user. */
    public String readCommand() {
        return scanner.nextLine();
    }

    /** Displays the standard separator between interactions. */
    public void showSeparator() {
        System.out.println(SEPARATOR);
    }

    /** Displays Jarvis's goodbye message. */
    public void showGoodbye() {
        System.out.println("Bye. Hope to see you again soon!");
        showSeparator();
    }

    /** Displays the supplied tasks in their given order. */
    public void showTasks(Task... tasks) {
        System.out.println("     Here are the tasks in your list:");
        for (int i = 0; i < tasks.length; i++) {
            System.out.println("     " + (i + 1) + "." + tasks[i]);
        }
    }

    /** Displays the confirmation for adding a task. */
    void showTaskAdded(Task task, int taskCount) {
        System.out.println("     Got it. I've added this task:");
        System.out.println("       " + task);
        System.out.println("     Now you have " + taskCount + " tasks in the list.");
    }

    /** Displays the confirmation for removing a task. */
    void showTaskRemoved(Task task, int taskCount) {
        System.out.println("     Noted. I've removed this task:");
        System.out.println("       " + task);
        System.out.println("     Now you have " + taskCount + " tasks in the list.");
    }

    /** Displays the confirmation for marking a task as done. */
    void showTaskMarked(Task task) {
        System.out.println("     Nice! I've marked this task as done:");
        System.out.println("       [X] " + task.getDescription());
    }

    /** Displays the confirmation for marking a task as not done. */
    void showTaskUnmarked(Task task) {
        System.out.println("     OK, I've marked this task as not done yet:");
        System.out.println("       [ ] " + task.getDescription());
    }

    /** Displays the confirmation for postponing a deadline. */
    void showTaskSnoozed(int taskNumber, Deadline deadline) {
        System.out.println("     Snoozed task " + taskNumber + " until " + deadline.getFormattedBy() + ":");
        System.out.println("       " + deadline);
    }

    /** Displays tasks whose descriptions match the supplied search. */
    public void showMatchingTasks(List<Task> matchingTasks) {
        System.out.println("     Here are the matching tasks in your list:");
        IntStream.range(0, matchingTasks.size())
                .forEach(index -> System.out.println("     " + (index + 1) + "." + matchingTasks.get(index)));
    }

    /** Displays an error message caused by invalid user input. */
    public void showError(JarvisException exception) {
        System.out.println("     Oops: " + exception.getMessage());
    }
}
