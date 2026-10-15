package jeremy.ui;

import java.util.List;
import java.util.Scanner;

import jeremy.task.Task;

/** Deals with all interactions with the user: reading input and printing output. */
public class Ui {
    private static final String LINE =
            "____________________________________________________________";

    private final Scanner scanner;

    public Ui() {
        this.scanner = new Scanner(System.in);
    }

    public void showWelcome() {
        showLine();
        System.out.println(" Hey, I'm Jeremy — your low-key task companion.");
        System.out.println(" Let's get your setlist sorted.");
        showLine();
    }

    public void showBye() {
        showLine();
        System.out.println(" Session over. Keep your deadlines loud and your stress low.");
        showLine();
    }

    public void showLine() {
        System.out.println(LINE);
    }

    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    public String readCommand() {
        return scanner.nextLine();
    }

    public void showEmptyInputMessage() {
        showLine();
        System.out.println(" No signal came through — type a command, or 'bye' to exit.");
        showLine();
    }

    public void showError(String message) {
        showLine();
        System.out.println(" " + message);
        showLine();
    }

    public void showLoadingError() {
        System.out.println(" Warning: I couldn't load your saved tasks.");
    }

    public void showTaskList(List<Task> tasks) {
        showLine();
        if (tasks.isEmpty()) {
            System.out.println(" No tasks on the setlist yet.");
        } else {
            System.out.println(" Setlist of tasks:");
            for (int i = 0; i < tasks.size(); i++) {
                System.out.println(" " + (i + 1) + "." + tasks.get(i));
            }
        }
        showLine();
    }

    public void showMatchingTasks(List<Task> tasks) {
        showLine();
        if (tasks.isEmpty()) {
            System.out.println(" No matches in the setlist.");
        } else {
            System.out.println(" Matching tracks:");
            for (int i = 0; i < tasks.size(); i++) {
                System.out.println(" " + (i + 1) + "." + tasks.get(i));
            }
        }
        showLine();
    }

    public void showTaskMarked(Task task) {
        showLine();
        System.out.println(" Status updated — task marked as done:");
        System.out.println("   " + task);
        showLine();
    }

    public void showTaskUnmarked(Task task) {
        showLine();
        System.out.println(" Status updated — task marked as not done:");
        System.out.println("   " + task);
        showLine();
    }

    public void showTaskAdded(Task task, int taskCount) {
        showLine();
        System.out.println(" Locked in. Added to the setlist:");
        System.out.println("   " + task);
        System.out.println(" Now you have " + taskCount + " task(s) in the list.");
        showLine();
    }

    public void showScheduleConflict(List<Task> conflicts) {
        showLine();
        System.out.println(" Heads up: this task may clash with:");
        for (Task conflict : conflicts) {
            System.out.println("   - " + conflict);
        }
        showLine();
    }

    public void showTaskDeleted(Task task, int taskCount) {
        showLine();
        System.out.println(" Cleared from the setlist:");
        System.out.println("   " + task);
        System.out.println(" Now you have " + taskCount + " task(s) in the list.");
        showLine();
    }

    public void close() {
        scanner.close();
    }
}
