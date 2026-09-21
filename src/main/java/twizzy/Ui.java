package twizzy;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

/** Handles console input and output for Twizzy. */
public class Ui {
    private static final String DIVIDER = "____________________________________________________________";

    private static final DateTimeFormatter DISPLAY_DATE_FORMAT =
            DateTimeFormatter.ofPattern("d MMM uuuu", Locale.ENGLISH);

    private static final String BANNER = " _______        _                     \n"
            + "|__   __|      (_)                    \n"
            + "   | |_      ___ __________   _       \n"
            + "   | \\ \\ /\\ / / |_  /_  / | | |      \n"
            + "   | |\\ V  V /| |/ / / /| |_| |      \n"
            + "   |_| \\_/\\_/ |_/___/___| \\__, |      \n"
            + "                           __/ |      \n"
            + "                          |___/";

    private final Scanner scanner = new Scanner(System.in);

    /** Displays Twizzy's banner and greeting. */
    public void showWelcome() {
        System.out.println(DIVIDER);
        System.out.println(BANNER);
        System.out.println("Wsg, I'm Twizzy — your task-list twin.");
        System.out.println("Type help to see what I can do.");
        System.out.println(DIVIDER);
    }

    /** Returns whether another command is available from standard input. */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /** Returns the next complete command from standard input. */
    public String readCommand() {
        return scanner.nextLine();
    }

    /** Displays the standard divider between user interactions. */
    public void showDivider() {
        System.out.println(DIVIDER);
    }

    /** Displays Twizzy's farewell message. */
    public void showGoodbye() {
        System.out.println("Catch you later, broski. Don't ghost your tasks.");
    }

    /** Displays a compact reference for all supported commands. */
    public void showHelp() {
        System.out.println("Commands:");
        System.out.println("  todo <description>");
        System.out.println("  deadline <description> /by <yyyy-MM-dd>");
        System.out.println("  event <description> /from <yyyy-MM-dd> /to <yyyy-MM-dd>");
        System.out.println("  list | list snoozed | find <keyword>");
        System.out.println("  mark <number> | unmark <number> | delete <number>");
        System.out.println("  snooze <number> /until <yyyy-MM-dd> | unsnooze <number>");
        System.out.println("  bye");
    }

    /** Displays a user-facing error. */
    public void showError(String message) {
        System.out.println("Nah, you gotta lock in, gang. " + message);
    }

    /** Displays a complete multi-line response supplied by the application. */
    public void showResponse(String response) {
        System.out.println(response);
    }

    /** Displays all tasks in their current order. */
    public void showTaskList(TaskList tasks, LocalDate date) {
        System.out.println("Your active tasks, gang:");
        showGroupedTasks(tasks.getActiveTasks(date), false);
    }

    /** Displays tasks that remain deferred on the current date. */
    public void showSnoozedTasks(List<Task> snoozedTasks) {
        System.out.println("Here are the snoozed tasks:");
        showGroupedTasks(snoozedTasks, true);
    }

    /** Displays the tasks that match a user's search keyword. */
    public void showMatchingTasks(List<Task> matchingTasks) {
        System.out.println("Here are the matching tasks in your list:");
        showGroupedTasks(matchingTasks, false);
    }

    /** Displays confirmation that a task was added. */
    public void showTaskAdded(Task task, int taskCount) {
        System.out.println("Locked in, gang. I added:");
        System.out.println("  " + task);
        showTaskCount(taskCount);
    }

    /** Displays confirmation that a task was marked as completed. */
    public void showTaskMarked(Task task) {
        System.out.println("Marked done, twin:");
        System.out.println("  " + task);
    }

    /** Displays confirmation that a task was marked as incomplete. */
    public void showTaskUnmarked(Task task) {
        System.out.println("Marked pending, gang:");
        System.out.println("  " + task);
    }

    /** Displays confirmation that a task has been deferred. */
    public void showTaskSnoozed(Task task) {
        System.out.println("Snoozed, gang:");
        System.out.println("  " + task + formatSnoozeDate(task));
    }

    /** Displays confirmation that a snoozed task has returned to the active list. */
    public void showTaskUnsnoozed(Task task) {
        System.out.println("Unsnoozed, twin:");
        System.out.println("  " + task);
    }

    /** Displays confirmation that a task was removed. */
    public void showTaskDeleted(Task task, int taskCount) {
        System.out.println("Deleted, broski:");
        System.out.println("  " + task);
        showTaskCount(taskCount);
    }

    /** Closes the standard-input scanner when Twizzy exits. */
    public void close() {
        scanner.close();
    }

    private void showTaskCount(int taskCount) {
        String taskWord = taskCount == 1 ? "task" : "tasks";
        System.out.println("You're juggling " + taskCount + " " + taskWord + " now, twin.");
    }

    /** Displays a numbered task list grouped by the task's concrete type. */
    private void showGroupedTasks(List<Task> displayedTasks, boolean includeSnoozeDate) {
        String previousGroup = "";
        for (int i = 0; i < displayedTasks.size(); i++) {
            Task task = displayedTasks.get(i);
            String group = getTaskGroupName(task);
            if (!group.equals(previousGroup)) {
                if (!previousGroup.isEmpty()) {
                    System.out.println();
                }
                System.out.println(group);
                previousGroup = group;
            }
            String snoozeDate = includeSnoozeDate ? formatSnoozeDate(task) : "";
            System.out.println((i + 1) + ". " + task + snoozeDate);
        }
    }

    /** Returns the user-facing heading for a task type. */
    private String getTaskGroupName(Task task) {
        if (task instanceof Todo) {
            return "Todos";
        }
        if (task instanceof Deadline) {
            return "Deadlines";
        }
        return "Events";
    }

    private String formatSnoozeDate(Task task) {
        return System.lineSeparator() + "  ↳ Returns: " + task.getSnoozedUntil().format(DISPLAY_DATE_FORMAT);
    }
}
