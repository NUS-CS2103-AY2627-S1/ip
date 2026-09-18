package fein;

import java.util.List;
import java.util.Scanner;

import fein.task.Task;
import fein.task.TaskList;

/** Handles all interaction between Fein and the command-line user. */
public class Ui {
    /** The line used to separate console messages. */
    private static final String SEPARATOR = "_".repeat(100);

    /** The greeting shown when Fein starts. */
    private static final String BANNER = "oooooooooooo           o8o                     \n"
            + "`888'     `8           `\"'                     \n"
            + " 888          .ooooo.  oooo  ooo. .oo.         \n"
            + " 888oooo8    d88' `88b `888  `888P\"Y88b        \n"
            + " 888    \"    888ooo888  888   888   888        \n"
            + " 888         888    .o  888   888   888        \n"
            + "o888o        `Y8bod8P' o888o o888o o888o       \n";

    /** Reads commands from standard input. */
    private final Scanner scanner;

    /** Creates a UI connected to standard input. */
    public Ui() {
        scanner = new Scanner(System.in);
    }

    /** Shows Fein's greeting. */
    public void showWelcome() {
        System.out.println(SEPARATOR);
        System.out.println(BANNER);
        System.out.println("Hello! I'm Fein.");
        System.out.println("What can I do for you?");
        System.out.println(SEPARATOR);
    }

    /** Returns the next command, or null when input has ended. */
    public String readCommand() {
        return scanner.hasNextLine() ? scanner.nextLine() : null;
    }

    /** Shows a separator before or after a command response. */
    public void showSeparator() {
        System.out.println(SEPARATOR);
    }

    /** Shows a task-list response. */
    public void showTasks(TaskList tasks) {
        showTaskList(tasks.asList(), "Here are the tasks in your list:",
                "Your list is empty! Try adding a task with `todo <description>`.\nExample: todo buy milk");
    }

    /** Shows tasks whose descriptions match a search keyword. */
    public void showMatchingTasks(List<Task> matchingTasks) {
        showTaskList(matchingTasks, "Here are the matching tasks in your list:",
                "No matching tasks found");
    }

    /** Shows numbered tasks, or an empty-list message when no tasks are supplied. */
    private void showTaskList(List<Task> tasks, String heading, String emptyMessage) {
        if (tasks.isEmpty()) {
            System.out.println(" " + emptyMessage);
            return;
        }
        System.out.println(" " + heading);
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println(" " + (i + 1) + "." + tasks.get(i));
        }
    }

    /** Shows a successful task-creation response. */
    public void showTaskAdded(Task task, int taskCount) {
        System.out.println(" Got it. I've added this task:");
        System.out.println("   " + task);
        System.out.println(" Now you have " + taskCount + " tasks in your list.");
    }

    /** Shows a successful task-deletion response. */
    public void showTaskDeleted(Task task, int taskCount) {
        System.out.println(" Noted. I've removed this task:");
        System.out.println("   " + task);
        System.out.println(" Now you have " + taskCount + " tasks in your list.");
    }

    /** Shows a successful mark response. */
    public void showTaskMarked(Task task) {
        System.out.println(" Nice! I've marked this task as done:");
        System.out.println("   " + task);
    }

    /** Shows a successful unmark response. */
    public void showTaskUnmarked(Task task) {
        System.out.println(" OK, I've marked this task as not done yet:");
        System.out.println("   " + task);
    }

    /** Shows an error response. */
    public void showError(String message) {
        System.out.println(" " + message);
    }

    /** Shows a general response produced by a non-task command. */
    public void showMessage(String message) {
        System.out.println(" " + message);
    }

    /** Shows Fein's farewell. */
    public void showGoodbye() {
        System.out.println("Bye! Keeping feining and stay LIT!");
        System.out.println(SEPARATOR);
    }
}
