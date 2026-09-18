package oreo.ui;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

import oreo.model.Deadline;
import oreo.model.Event;
import oreo.model.Task;
import oreo.model.TaskList;

/** Handles messages that greet the user and close the application. */
public class Ui {
    /** The product name shown in user-facing messages and the GUI. */
    public static final String PRODUCT_NAME = "Oreo";
    private static final String LINE = "____________________________________________";
    private final Scanner scanner;

    /** Creates a UI that reads commands from standard input. */
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

    /** Displays the standard response divider. */
    public void showLine() {
        System.out.println(LINE);
    }

    /** Displays the application's greeting banner. */
    public void showGreeting() {
        String banner = "  OOO   RRRR   EEEEE  OOO  \n"
                + " O   O  R   R  E     O   O \n"
                + " O   O  RRRR   EEEE  O   O \n"
                + " O   O  R R    E     O   O \n"
                + "  OOO   R  RR  EEEEE  OOO  \n";
        System.out.println("____________________________________________ \n"
                + banner + "\n"
                + "Hello! I'm " + PRODUCT_NAME + ". \n"
                + "Let's get started shall we? \n"
                + "____________________________________________");
    }

    /** Displays the application's goodbye message. */
    public void showGoodbye() {
        System.out.println("____________________________________________ \n"
                + "Good work. See you next time! \n"
                + "____________________________________________ \n");
    }

    /** Displays every supported command and its expected usage. */
    public void showHelp() {
        showLine();
        System.out.println("Here are the commands you can use:");
        System.out.println("- help: Shows this help message.");
        System.out.println("- list: Lists all tasks.");
        System.out.println("- todo DESCRIPTION: Adds a to-do task.");
        System.out.println("- deadline DESCRIPTION /by DATE: Adds a task with a deadline.");
        System.out.println("- event DESCRIPTION /from START /to END: Adds an event.");
        System.out.println("- find KEYWORD: Finds tasks by description.");
        System.out.println("- on DATE: Lists tasks occurring on a date.");
        System.out.println("- listall: Lists all tasks together with their merchandise.");
        System.out.println("- merchandise TASK_NUMBER DETAILS: Adds merchandise to a task.");
        System.out.println("- edit-merchandise TASK_NUMBER DETAILS: Edits merchandise for a task.");
        System.out.println("- delete-merchandise TASK_NUMBER: Removes merchandise from a task.");
        System.out.println("- find-merchandise KEYWORD: Finds merchandise by its details.");
        System.out.println("- mark TASK_NUMBER: Marks a task as done.");
        System.out.println("- unmark TASK_NUMBER: Marks a task as not done.");
        System.out.println("- delete TASK_NUMBER: Deletes a task.");
        System.out.println("- bye: Exits Oreo.");
        showLine();
    }

    /** Displays a confirmation for a task status change. */
    public void showSuccess(String message, Task task) {
        System.out.println("____________________________________________\n" + message + "\n"
                + "  " + task + "\n____________________________________________");
    }

    /** Displays a consistently formatted command error. */
    public void showError(String message) {
        System.out.println("____________________________________________\n"
                + "  Oh My God! " + message + "\n____________________________________________");
    }

    /** Displays all tasks, or an empty-list message. */
    public void showTaskList(TaskList tasks) {
        showLine();
        if (tasks.isEmpty()) {
            System.out.println("No tasks in the list.");
        } else {
            System.out.println("Here are the tasks in your list:");
            for (int i = 0; i < tasks.size(); i++) {
                System.out.println((i + 1) + ". " + tasks.get(i));
            }
        }
        showLine();
    }

    /** Displays tasks whose descriptions match a search keyword. */
    public void showMatchingTasks(TaskList tasks) {
        showLine();
        if (tasks.isEmpty()) {
            System.out.println("No matching tasks found.");
        } else {
            System.out.println("Here are the matching tasks in your list:");
            for (int i = 0; i < tasks.size(); i++) {
                System.out.println((i + 1) + ". " + tasks.get(i));
            }
        }
        showLine();
    }

    /** Displays confirmation after adding a task. */
    public void showAdded(Task task, int taskCount) {
        System.out.println("____________________________________________\n"
                + "Got it. I've added this task:\n" + task + "\n"
                + "Now you have " + taskCount + " tasks in the list.\n"
                + "____________________________________________");
    }

    /** Displays confirmation after deleting a task. */
    public void showDeleted(Task task, int taskCount) {
        System.out.println("____________________________________________\n"
                + "Noted. I've removed this task:\n  " + task + "\n"
                + "Now you have " + taskCount + " tasks in the list.\n"
                + "____________________________________________");
    }

    /** Displays deadlines and events occurring on the supplied date. */
    public void showTasksOnDate(TaskList tasks, LocalDate date) {
        showLine();
        System.out.println("Tasks occurring on "
                + date.format(DateTimeFormatter.ofPattern("MMM dd yyyy")) + ":");
        int count = 0;
        for (Task task : tasks) {
            boolean occurs = task instanceof Deadline deadline && deadline.occursOn(date)
                    || task instanceof Event event && event.occursOn(date);
            if (occurs) {
                System.out.println((++count) + ". " + task);
            }
        }
        if (count == 0) {
            System.out.println("No deadlines or events on this date.");
        }
        showLine();
    }

    /** Displays all tasks and their non-empty merchandise details. */
    public void showAllTasks(TaskList tasks) {
        showLine();
        if (tasks.isEmpty()) {
            System.out.println("No tasks in the list.");
        } else {
            System.out.println("Here are all tasks and their merchandise:");
            for (int i = 0; i < tasks.size(); i++) {
                Task task = tasks.get(i);
                System.out.println((i + 1) + ". " + task);
                if (task.hasMerchandise()) {
                    System.out.println("   Merchandise: " + task.getMerchandise());
                }
            }
        }
        showLine();
    }

    /** Displays tasks with matching merchandise details. */
    public void showMatchingMerchandise(TaskList tasks) {
        showLine();
        if (tasks.isEmpty()) {
            System.out.println("No matching merchandise found.");
        } else {
            System.out.println("Here are the matching merchandise in your list:");
            for (int i = 0; i < tasks.size(); i++) {
                Task task = tasks.get(i);
                System.out.println((i + 1) + ". " + task);
                System.out.println("   Merchandise: " + task.getMerchandise());
            }
        }
        showLine();
    }

    /** Displays a merchandise operation confirmation. */
    public void showMerchandise(String message, Task task) {
        System.out.println("____________________________________________\n" + message + "\n"
                + "" + task + "\n   Merchandise: " + task.getMerchandise()
                + "\n____________________________________________");
    }
}
