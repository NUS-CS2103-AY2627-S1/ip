package tangent.ui;

import java.util.List;
import java.util.Scanner;
import java.util.function.Consumer;

import tangent.task.Task;
import tangent.task.TaskList;

/** Handles console input and output messages. */
public class Ui {
    /** A line used to separate responses in the console. */
    private static final String DIVIDER = "____________________________________________________________";
    /** The banner shown when loading up the program. */
    private static final String BANNER = """
            ████████╗ █████╗ ███╗   ██╗ ██████╗ ███████╗███╗   ██╗████████╗
            ╚══██╔══╝██╔══██╗████╗  ██║██╔════╝ ██╔════╝████╗  ██║╚══██╔══╝
               ██║   ███████║██╔██╗ ██║██║  ███╗█████╗  ██╔██╗ ██║   ██║
               ██║   ██╔══██║██║╚██╗██║██║   ██║██╔══╝  ██║╚██╗██║   ██║
               ██║   ██║  ██║██║ ╚████║╚██████╔╝███████╗██║ ╚████║   ██║
               ╚═╝   ╚═╝  ╚═╝╚═╝  ╚═══╝ ╚═════╝ ╚══════╝╚═╝  ╚═══╝   ╚═╝""";
    /** The greeting shown in the graphical interface without the console banner. */
    private static final String GUI_WELCOME_MESSAGE = "good morning/afternoon/evening ^-^\n"
            + "what do you want me to do?";

    /** Receives formatted messages for display. */
    private final Consumer<String> output;
    /** Receives error messages separately when a visual UI wants to highlight them. */
    private final Consumer<String> errorOutput;

    /** Creates a UI that writes messages to standard output. */
    public Ui() {
        this(System.out::println, System.out::println);
    }

    /**
     * Creates a UI that sends messages to the supplied output handler.
     *
     * @param output handler that receives each formatted message
     */
    public Ui(Consumer<String> output) {
        this(output, output);
    }

    /**
     * Creates a UI with separate normal and error output handlers.
     *
     * @param output handler for normal messages
     * @param errorOutput handler for error messages
     */
    public Ui(Consumer<String> output, Consumer<String> errorOutput) {
        this.output = output;
        this.errorOutput = errorOutput;
    }

    /** Displays the greeting shown when the program starts. */
    public void showWelcome() {
        showDivider();
        display(BANNER);
        display("good morning/afternoon/evening ^-^ I'm TANGENT.\nwhat do you want me to do?");
        showDivider();
    }

    /** Displays the compact greeting used by the graphical interface. */
    public void showGuiWelcome() {
        display(GUI_WELCOME_MESSAGE);
    }

    /** Displays the divider used to separate responses in the console. */
    public void showDivider() {
        display(DIVIDER);
    }

    /** Reads and trims one command entered by the user. */
    public String readCommand(Scanner scanner) {
        return scanner.nextLine().trim();
    }

    /** Displays an error message from the application. */
    public void showError(String message) {
        errorOutput.accept(message);
    }

    /** Displays the tasks currently stored in the task list with 1-based indexing. */
    public void showTaskList(TaskList tasks) {
        if (tasks.isEmpty()) {
            display("no tasks yet!");
        }
        for (int i = 0; i < tasks.size(); i++) {
            display((i + 1) + ". " + tasks.get(i));
        }
    }

    /** Displays the current-task heading followed by every task in the task list. */
    public void showCurrentTasks(TaskList tasks) {
        if (tasks.isEmpty()) {
            showTaskList(tasks);
            return;
        }
        display("hello! here are your current tasks:");
        showTaskList(tasks);
    }

    /** Displays the farewell message shown when the program exits. */
    public void showGoodbye() {
        display("bye o/ hope to see you again soon");
    }

    /** Displays a status-change summary followed by every changed task. */
    public void showTaskStatusChanged(boolean isDone, List<Task> changedTasks) {
        showTaskStatusChanged(isDone, changedTasks, List.of());
    }

    /** Displays changed tasks followed by tasks that already had the requested status. */
    public void showTaskStatusChanged(boolean isDone, List<Task> changedTasks, List<Task> unchangedTasks) {
        String status = isDone ? "done" : "undone";
        String noun = changedTasks.size() == 1 ? "task" : "tasks";
        display("i've marked " + changedTasks.size() + " " + noun + " as " + status + "!");
        for (Task task : changedTasks) {
            display(task.toString());
        }
        if (!unchangedTasks.isEmpty()) {
            display("the following task(s) were already marked as " + status + ":");
            for (Task task : unchangedTasks) {
                display(task.toString());
            }
        }
    }

    /** Displays a message when every selected task already has the requested status. */
    public void showStatusAlreadyApplied(boolean isDone) {
        String status = isDone ? "done" : "undone";
        display("all selected tasks are already marked as " + status + "!");
    }

    /** Displays a message when one selected task already has the requested status. */
    public void showSingleStatusAlreadyApplied(boolean isDone) {
        String status = isDone ? "done" : "undone";
        display("the selected task is already marked as " + status + "!");
    }

    /** Displays removed tasks in original order followed by the remaining task count. */
    public void showTasksDeleted(List<Task> removedTasks, TaskList tasks) {
        if (removedTasks.size() == 1) {
            display("got it! i've removed this task:");
            display(removedTasks.get(0).toString());
            display(formatTaskCount(tasks));
            return;
        }
        display("got it! i've removed these tasks:");
        for (Task task : removedTasks) {
            display(task.toString());
        }
        display("removed " + removedTasks.size() + " tasks, " + tasks.size() + " task(s) remaining!");
    }

    /** Displays confirmation that a task was added and displays the new task count. */
    public void showTaskAdded(Task task, TaskList tasks) {
        display("got it! you have a new task: ");
        display(task.toString());
        display(formatTaskCount(tasks));
    }

    /**
     * Displays all tasks matching the specified {@code keyword}, or a no-match message.
     *
     * @param keyword the keyword used for the search
     * @param matches the matching tasks to display
     */
    public void showMatchingTasks(String keyword, TaskList matches) {
        if (matches.isEmpty()) {
            display("there were no matching tasks in the task list!");
        } else {
            display("here are the tasks matching the keyword " + keyword + ":");
            showTaskList(matches);
        }
    }

    /** Formats the task-count confirmation after adding or deleting a task. */
    private String formatTaskCount(TaskList tasks) {
        if (tasks.isEmpty()) {
            return "you now have no tasks in the list!";
        }
        if (tasks.size() == 1) {
            return "you now have 1 task in the list!";
        }
        return "you now have " + tasks.size() + " tasks in the list!";
    }

    /** Sends one formatted message to configured output handler. */
    protected void display(String message) {
        output.accept(message);
    }
}
