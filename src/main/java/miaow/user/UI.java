package miaow.user;

import java.util.ArrayList;
import java.util.Scanner;

import miaow.task.Deadline;
import miaow.task.Event;
import miaow.task.Task;


/**
 * UI
 */
public class UI {
    private Scanner scanner;

    public UI() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * Shows welcome message.
     */
    public void showWelcome() {
        String banner = " _._     _,-'\"\"`-._\n"
                + "(,-.`._,'(       |\\`-/|\n"
                + "    `-.-' \\ )-`( , o o)    MIAOW\n"
                + "          `-    \\`_`\"'-\n"
                + "Hello! I'm Miaow.\n"
                + "What can I do for you?\n"
                + "____________________________________________________________";
        System.out.println(banner);
    }

    /**
     * Shows goodbye message.
     */
    public void showGoodbye() {
        System.out.println("Miaow: Bye. Hope to see you again soon!\n"
                + "____________________________________________________________");
    }

    /**
     * Shows a line.
     */
    public void showLine() {
        System.out.println("____________________________________________________________");
    }

    /**
     * Shows a task has been added.
     * @param task Task to add.
     * @param taskCount Updated number of tasks.
     */
    public void showTaskAdded(Task task, int taskCount) {
        System.out.println("Miaow: Got it. I've added this task:");
        System.out.println(" " + task);
        showTaskCount(taskCount);
    }

    /**
     * Shows a task has been deleted.
     * @param task Task to delete
     * @param taskCount Updated number of tasks.
     */
    public void showTaskDeleted(Task task, int taskCount) {
        System.out.println("Miaow: Deleted task:");
        System.out.println(" " + task);
        showTaskCount(taskCount);
    }

    /**
     * Shows list of tasks
     * @param tasks Arraylist of tasks
     */
    public void showTaskList(ArrayList<Task> tasks) {
        if (tasks.isEmpty()) {
            showEmptyTaskList();
        } else {
            for (int i = 0; i < tasks.size(); i++) {
                System.out.println(" " + (i + 1) + ". " + tasks.get(i).toString());
            }
        }

    }

    /**
     * Shows that a task has been marked
     * @param task Task
     * @param taskNumber Index of task
     */
    public void showMarked(Task task, int taskNumber) {
        System.out.println("Miaow: Marked task " + taskNumber);
        System.out.println(" " + task);
    }

    /**
     * Shows that a task has been unmarked
     * @param task Task
     * @param taskNumber Index of task
     */
    public void showUnmarked(Task task, int taskNumber) {
        System.out.println("Miaow: Unmarked task " + taskNumber);
        System.out.println(" " + task);
    }

    /**
     * Shows an error has occurred with a message.
     * @param message Error message
     */
    public void showError(String message) {
        System.out.println("Miaow: Error -- " + message);
    }
    public void showLoadingError() {
        System.out.println("Error loading tasks from file. Starting with empty task list.");
    }

    /**
     * Shows empty task list
     */
    public void showEmptyTaskList() {
        System.out.println("No tasks in your list.");
    }

    /**
     * Scans for the next input line
     * @return The next line as a String
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /**
     * Shows number of tasks in the list
     * @param count The number of tasks
     */
    public void showTaskCount(int count) {
        System.out.println("Now you have " + count + " tasks in the list.");
    }
    /**
     * Shows message for invalid command
     */
    public void showInvalidCommand() {
        System.out.println("Miaow: That's not a command, try again!");
    }

    /**
     * Shows message for invalid task number
     */
    public void showInvalidTaskNumber() {
        System.out.println("Miaow: Invalid task number.");
    }

    /**
     * Shows message for invalid date format
     */
    public void showInvalidDateFormat() {
        System.out.println("Miaow: Please provide a valid date (yyyy-MM-dd or dd/MM/yyyy)");
    }

    /**
     * Shows message when there are no tasks on a specific date
     */
    public void showNoTasksOnDate() {
        System.out.println(" No tasks found on this date.");
    }

    /**
     * Shows deadlines on a specific date
     * @param date The date to display deadlines for
     * @param deadlines The list of deadlines on that date
     */
    public void showDeadlinesOnDate(String date, ArrayList<Deadline> deadlines) {
        System.out.println("Miaow: Deadlines on " + date + ":");
        if (deadlines.isEmpty()) {
            showNoTasksOnDate();
        } else {
            for (int i = 0; i < deadlines.size(); i++) {
                System.out.println(" " + (i + 1) + ". " + deadlines.get(i));
            }
        }
    }

    /**
     * Shows events on a specific date
     * @param date The date to display events for
     * @param events The list of events on that date
     */
    public void showEventsOnDate(String date, ArrayList<Event> events) {
        System.out.println("Miaow: Events on " + date + ":");
        if (events.isEmpty()) {
            showNoTasksOnDate();
        } else {
            for (int i = 0; i < events.size(); i++) {
                System.out.println(" " + (i + 1) + ". " + events.get(i));
            }
        }
    }

    /**
     * Shows help/usage message for todo command
     */
    public void showTodoHelp() {
        System.out.println("Miaow: Add task name.");
        System.out.println("Example: todo read book");
    }

    /**
     * Shows help/usage message for deadline command
     */
    public void showDeadlineHelp() {
        System.out.println("Miaow: Add task name and deadline.");
        System.out.println("Example: deadline return book /by 2019-12-02");
        System.out.println("Example: deadline return book /by 2/12/2019");
        System.out.println("Example: deadline return book /by 2/12/2019 1800");
    }

    /**
     * Shows help/usage message for event command
     */
    public void showEventHelp() {
        System.out.println("Miaow: Add task name, start time, and end time.");
        System.out.println("Example: event meeting /from 2019-08-06 14:00 /to 16:00");
        System.out.println("Example: event meeting /from 6/8/2019 1400 /to 1600");
    }

    /**
     * Shows the loading message when tasks are loaded from file
     * @param count The number of tasks loaded
     */
    public void showLoadingSuccess(int count) {
        if (count > 0) {
            System.out.println("Loaded " + count + " existing tasks.");
        } else {
            System.out.println("No existing data file found. Starting with empty task list.");
        }
        showLine();
    }

    /**
     * Shows a generic message with Miaow prefix
     * @param message The message to display
     */
    public void showMessage(String message) {
        System.out.println("Miaow: " + message);
    }

    /**
     * Closes the scanner when the application exits
     */
    public void close() {
        scanner.close();
    }

    /**
     * shows results of search
     * @param keyword Keyword used for searches
     * @param matchingTasks Returned list of tasks
     */
    public void showFindResults(String keyword, ArrayList<Task> matchingTasks) {
        System.out.println("Miaow: Here are the matching tasks in your list:");

        if (matchingTasks.isEmpty()) {
            System.out.println(" No matching tasks found.");
        } else {
            for (int i = 0; i < matchingTasks.size(); i++) {
                System.out.println(" " + (i + 1) + ". " + matchingTasks.get(i));
            }
        }

        showLine();
    }
}




