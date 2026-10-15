package hse.ui;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import hse.task.Task;
import hse.task.TaskList;

/** Handles all interaction with the user, either through the console or a graphical interface. */
public class Ui {
    private static final String DIVIDER = "____________________________________________________________";
    private final Scanner scanner;
    private boolean isGuiMode;
    private final List<String> responseBuffer;

    /** Creates a user interface that reads from the standard input stream. */
    public Ui() {
        scanner = new Scanner(System.in);
        isGuiMode = false;
        responseBuffer = new ArrayList<>();
    }

    /** Switches this interface to capture responses for a graphical interface instead of the console. */
    public void setGuiMode() {
        isGuiMode = true;
        responseBuffer.clear();
    }

    /** Returns the captured response and clears the buffer for the next interaction. */
    public String getResponse() {
        String response = String.join(System.lineSeparator(), responseBuffer);
        responseBuffer.clear();
        return response;
    }

    /** Prints the greeting displayed when the application starts. */
    public void showWelcome() {
        if (isGuiMode) {
            responseBuffer.add("Hello! I'm HSE. What can I do for you?");
            return;
        }
        System.out.println(" _   _  ____  _____ \n"
                + "| | | |/ ___|| ____|\n"
                + "| |_| |\\___ \\|  _|  \n"
                + "|  _  | ___) | |___ \n"
                + "|_| |_| |____/|_____|\n");
        output("Hello! I'm HSE.", "What can I do for you?");
    }

    /** Returns whether another command is available from the console. */
    public boolean hasNextCommand() {
        return !isGuiMode && scanner.hasNextLine();
    }

    /** Reads and trims one command from the console. */
    public String readCommand() {
        return scanner.nextLine().trim();
    }

    /** Prints the divider used to separate command interactions. */
    public void showLine() {
        if (!isGuiMode) {
            System.out.println(DIVIDER);
        }
    }

    /** Prints the saved tasks with one-based task numbers. */
    public void showTaskList(TaskList tasks) {
        showNumberedTasks(tasks.getTasks());
    }

    /** Prints tasks whose descriptions match a requested keyword. */
    public void showMatchingTasks(List<Task> matchingTasks) {
        output("Here are the matching tasks in your list:");
        showNumberedTasks(matchingTasks);
    }

    /** Prints tasks that occur on a date, or a message when there are no matches. */
    public void showTasksOnDate(TaskList tasks, LocalDate date) {
        List<Integer> positions = tasks.getTaskPositionsOn(date);
        if (positions.isEmpty()) {
            output("No deadlines or events occur on that date.");
            return;
        }
        for (int position : positions) {
            output(position + "." + tasks.get(position - 1));
        }
    }

    /** Prints confirmation for an added task. */
    public void showAddedTask(Task task, int taskCount) {
        output("Got it. I've added this task:", "  " + task,
                "Now you have " + taskCount + " tasks in the list.");
    }

    /** Prints confirmation for a marked task. */
    public void showMarkedTask(Task task) {
        output("Nice! I've marked this task as done:", task.toString());
    }

    /** Prints confirmation for an unmarked task. */
    public void showUnmarkedTask(Task task) {
        output("OK, I've marked this task as not done yet:", task.toString());
    }

    /** Prints confirmation for a deleted task. */
    public void showDeletedTask(Task task, int taskCount) {
        output("Noted. I've removed this task:", "  " + task,
                "Now you have " + taskCount + " tasks in the list.");
    }

    /** Prints confirmation that tasks have been sorted chronologically. */
    public void showTasksSorted() {
        output("Tasks sorted by date.");
    }

    /** Prints an application error in the established error format. */
    public void showError(String message) {
        output("OOPS!!! " + message);
    }

    /** Prints the error shown when saved tasks cannot be loaded. */
    public void showLoadingError() {
        showError("Could not load saved tasks.");
    }

    /** Prints the farewell displayed when the application exits. */
    public void showGoodbye() {
        output("Bye. Hope to see you again soon!");
    }

    /** Sends the supplied messages to the console or graphical response buffer in order. */
    private void output(String... messages) {
        for (String message : messages) {
            if (isGuiMode) {
                responseBuffer.add(message);
            } else {
                System.out.println(message);
            }
        }
    }

    /** Prints a list of tasks with one-based numbering. */
    private void showNumberedTasks(List<Task> tasks) {
        for (int i = 0; i < tasks.size(); i++) {
            output((i + 1) + "." + tasks.get(i));
        }
    }
}
