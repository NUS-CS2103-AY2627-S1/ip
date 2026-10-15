package anders.ui;

import java.util.Locale;
import java.util.Scanner;

import anders.collection.TaskList;
import anders.task.Deadline;
import anders.task.Event;
import anders.task.Task;

/** Handles input and the lantern keeper personality shared by the console and GUI. */
public class Ui {
    public static final String NAME = "Anders";
    public static final String TAGLINE = "Your lantern keeper";
    public static final String WELCOME_MESSAGE = "Hello, wanderer. I'm " + NAME + ", your lantern keeper.\n"
            + "One task at a time; we'll find the way.";

    private final Scanner scanner;

    /** Creates a console user interface reading from standard input. */
    public Ui() {
        scanner = new Scanner(System.in);
    }

    /** Returns whether another command is available. */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /** Reads and trims the next user command. */
    public String readCommand() {
        return scanner.nextLine().trim();
    }

    /** Prints the startup greeting. */
    public void showWelcome() {
        showSeparator();
        System.out.println(NAME + " | " + TAGLINE);
        System.out.println(WELCOME_MESSAGE);
        System.out.println("Start your trail with: todo read chapter 1");
        showSeparator();
    }

    /** Prints the command separator. */
    public void showSeparator() {
        System.out.println("____________________________________________________________");
    }

    /** Prints an error message. */
    public void showError(String message) {
        System.out.println("     A little fog on the path. " + message);
    }

    /** Prints a goodbye message. */
    public void showGoodbye() {
        System.out.println("     Rest well, wanderer. I'll keep the lantern lit.");
    }

    /** Displays all tasks in the list. */
    public void showTaskList(TaskList tasks) {
        if (tasks.size() == 0) {
            System.out.println("     A clear path! No tasks yet. Try: todo read chapter 1");
            return;
        }
        System.out.println("     Lantern lit. Here are the tasks on your trail:");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println("     " + (i + 1) + "." + formatTask(tasks.get(i)));
        }
    }

    /** Displays tasks whose descriptions contain the supplied keyword, or a no-match message. */
    public void showMatchingTasks(TaskList tasks, String keyword) {
        System.out.println("     I've held the lantern up to these matches:");
        String searchText = keyword.toLowerCase(Locale.ROOT);
        boolean hasMatch = false;
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            boolean isMatch = keyword.startsWith("#")
                    ? task.hasTag(keyword)
                    : task.getDescription().toLowerCase(Locale.ROOT).contains(searchText);
            if (isMatch) {
                hasMatch = true;
                System.out.println("     " + (i + 1) + "." + formatTask(task));
            }
        }
        if (!hasMatch) {
            System.out.println("     No matching tasks found. Try another word or #tag.");
        }
    }

    /** Displays a task-added confirmation. */
    public void showTaskAdded(Task task, int taskCount) {
        System.out.println("     A new trail marker. I've added this task:");
        System.out.println("       " + task);
        showTaskCount(taskCount);
    }

    /**
     * Displays a task status change confirmation.
     *
     * @param task the task whose status changed
     * @param isDone whether the task is now marked as done
     */
    public void showMarked(Task task, boolean isDone) {
        System.out.println(isDone ? "     One more light along the path. Task marked as done:"
                : "     Back on the trail. Task marked as not done:");
        System.out.println("       [" + (isDone ? "X" : " ") + "] " + task.getDescription()
                + task.getTagsDisplayText());
    }

    /**
     * Displays the result of adding or removing tags from a task.
     *
     * @param task the task whose tags were selected for an update
     * @param isAdding whether the command adds tags rather than removes them
     * @param hasChanged whether at least one tag was added or removed
     */
    public void showTagUpdate(Task task, boolean isAdding, boolean hasChanged) {
        if (isAdding) {
            System.out.println(hasChanged ? "     Trail labels attached. I've tagged this task:"
                    : "     Already signposted. This task already has these tags:");
        } else {
            System.out.println(hasChanged ? "     A little less baggage. I've removed these tags from this task:"
                    : "     No change needed. This task does not have these tags:");
        }
        System.out.println("       " + formatTask(task));
    }

    /** Displays a deletion confirmation. */
    public void showDeleted(Task task, int remaining) {
        System.out.println("     Path cleared. I've removed this task:");
        System.out.println("       " + formatTask(task));
        showTaskCount(remaining);
    }

    /** Displays an invalid task-number message. */
    public void showInvalidTaskNumber(int count) {
        if (count == 0) {
            showError("There are no tasks yet. Add one with: todo read chapter 1");
        } else {
            showError("Task number must be between 1 and " + count + ". Use list to see your trail.");
        }
    }

    /** Displays a non-numeric task-number message. */
    public void showInvalidTaskNumberFormat() {
        showError("Please provide a valid task number. Use list to see your trail.");
    }

    /** Prints the total number of stored tasks with the appropriate singular or plural noun. */
    private void showTaskCount(int count) {
        System.out.println("     Your trail holds " + count + (count == 1 ? " task." : " tasks."));
    }

    /** Formats a task with its type and completion icons for console display. */
    private String formatTask(Task task) {
        assert task != null : "The UI can only format an existing task";
        String typeIcon = task instanceof Deadline ? "D" : task instanceof Event ? "E" : "T";
        String taskText = task.toString();

        String typePrefix = "[" + typeIcon + "] ";
        assert taskText.startsWith(typePrefix)
                : "Every displayable task must include its type prefix";
        return "[" + typeIcon + "][" + task.getStatusIcon() + "] "
                + taskText.substring(typePrefix.length());
    }
}
