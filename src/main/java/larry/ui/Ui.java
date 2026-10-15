package larry.ui;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;
import java.util.function.Consumer;

import larry.task.Task;
import larry.task.TaskDateTime;
import larry.task.TaskList;

/**
 * Handles console input and presents Larry's responses to the user.
 */
public class Ui implements AutoCloseable {
    private static final String SEPARATOR = "    __________________________________________________________";
    private static final String INIT_SPACE = "     ";
    private static final String PRE_TASK_SPACE = "       ";
    private static final String BANNER = """
                             ███████╗██╗   ██╗██╗██╗
                             ██╔════╝██║   ██║██║██║
                             █████╗  ██║   ██║██║██║
                             ██╔══╝  ╚██╗ ██╔╝██║██║
                             ███████╗ ╚████╔╝ ██║███████╗
                             ╚══════╝  ╚═══╝  ╚═╝╚══════╝

                     ██╗      █████╗ ██████╗ ██████╗ ██╗   ██╗
                     ██║     ██╔══██╗██╔══██╗██╔══██╗╚██╗ ██╔╝
                     ██║     ███████║██████╔╝██████╔╝ ╚████╔╝
                     ██║     ██╔══██║██╔══██╗██╔══██╗  ╚██╔╝
                     ███████╗██║  ██║██║  ██║██║  ██║   ██║
                     ╚══════╝╚═╝  ╚═╝╚═╝  ╚═╝╚═╝  ╚═╝   ╚═╝

            ██████████╗                                             ╔██████████
              ╚██████████╗                                       ╔██████████╝
                 ╚██████████╗                                 ╔██████████╝
                    ███████████████████████████████████████████████
                     █████████████████████████████████████████████
                      ███████████████████████████████████████████
                      ████████    ░▓██▓░       ░▓██▓░    ████████
                       ███████     ▀██▀         ▀██▀     ███████
                       █████████████████████████████████████████
                        ███████████████████▄█▄█████████████████
                         ██████████████████▀█▀████████████████
                          ███████████████   ▄   █████████████
                           ██████████████  ╲_╱  ████████████
                            ████████████████████████████████
                              ████████████████████████████
                                ████████████████████████
                                 ██████████████████████
                                ████████████████████████
                             ██████████████████████████████
                         ██████████████████████████████████████
                     ██████████████████████████████████████████████
                    """;

    private final Scanner scanner;
    private final Consumer<String> output;
    private final Consumer<String> errorOutput;

    /**
     * Creates a console UI that reads from standard input.
     */
    public Ui() {
        this.scanner = new Scanner(System.in);
        this.output = System.out::println;
        this.errorOutput = System.err::println;
    }

    /**
     * Creates a UI that sends output to the supplied destinations without reading console input.
     *
     * @param output Destination for normal output lines.
     * @param errorOutput Destination for warning and error output lines.
     */
    public Ui(Consumer<String> output, Consumer<String> errorOutput) {
        this.scanner = null;
        this.output = output;
        this.errorOutput = errorOutput;
    }

    /**
     * Checks whether another command is available from the user.
     *
     * @return True when another command can be read.
     */
    public boolean hasNextCommand() {
        if (scanner == null) {
            return false;
        }
        return scanner.hasNextLine();
    }

    /**
     * Reads the next command from the user.
     *
     * @return Next command entered by the user.
     */
    public String readCommand() {
        if (scanner == null) {
            throw new IllegalStateException("This UI does not read console input.");
        }
        return scanner.nextLine();
    }

    /**
     * Displays Larry's banner and greeting.
     */
    public void showWelcome() {
        output.accept(SEPARATOR + "\n");
        output.accept(BANNER);
        output.accept("I'm EVIL LARRY.\nWhat do you want to do?");
        showSeparator();
    }

    /**
     * Displays a separator between interactions.
     */
    public void showSeparator() {
        output.accept(SEPARATOR);
    }

    /**
     * Displays Larry's farewell message.
     */
    public void showGoodbye() {
        output.accept(INIT_SPACE + "EVIL LARRY has decided to let you go\n     FOR NOW...");
    }

    /**
     * Displays every task with its one-based task number.
     *
     * @param tasks Tasks to display.
     */
    public void showTasks(TaskList tasks) {
        if (tasks.size() == 0) {
            output.accept(INIT_SPACE + "EVIL LARRY's task vault is empty. "
                    + "Enjoy your freedom while it lasts.");
            return;
        }
        output.accept(INIT_SPACE + "Here are the tasks EVIL LARRY says are in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            output.accept("     " + (i + 1) + "." + tasks.get(i));
        }
    }

    /**
     * Displays tasks whose descriptions match a search keyword.
     *
     * @param matchingTasks Tasks to display.
     */
    public void showMatchingTasks(List<Task> matchingTasks) {
        if (matchingTasks.isEmpty()) {
            output.accept(INIT_SPACE + "EVIL LARRY found nothing. Try a less pathetic keyword.");
            return;
        }
        output.accept(INIT_SPACE + "EVIL LARRY uncovered these matching tasks:");
        for (int i = 0; i < matchingTasks.size(); i++) {
            output.accept("     " + (i + 1) + "." + matchingTasks.get(i));
        }
    }

    /**
     * Displays deadlines and events occurring on a requested date.
     *
     * @param date Date whose tasks should be displayed.
     * @param tasks Tasks to search and display.
     */
    public void showTasksOnDate(LocalDate date, TaskList tasks) {
        boolean hasTasksOnDate = tasks.stream().anyMatch(task -> task.occursOn(date));
        if (!hasTasksOnDate) {
            output.accept(INIT_SPACE + "EVIL LARRY found no tasks on "
                    + TaskDateTime.formatDate(date) + ". Your reprieve is temporary.");
            return;
        }
        output.accept(INIT_SPACE + "EVIL LARRY says these tasks occur on "
                + TaskDateTime.formatDate(date) + ":");
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            if (task.occursOn(date)) {
                output.accept("     " + (i + 1) + "." + task);
            }
        }
    }

    /**
     * Displays confirmation that a task was marked as done.
     *
     * @param task Task that was marked.
     */
    public void showTaskMarked(Task task) {
        output.accept(INIT_SPACE + "EVIL LARRY has marked this task as done:");
        output.accept(PRE_TASK_SPACE + task);
    }

    /**
     * Displays confirmation that a task was marked as not done.
     *
     * @param task Task that was unmarked.
     */
    public void showTaskUnmarked(Task task) {
        output.accept(INIT_SPACE + "EVIL LARRY has marked this task as not done yet:");
        output.accept(PRE_TASK_SPACE + task);
    }

    /**
     * Displays confirmation that a task was deleted and the new task count.
     *
     * @param task Task that was deleted.
     * @param taskCount Number of remaining tasks.
     */
    public void showTaskDeleted(Task task, int taskCount) {
        output.accept(INIT_SPACE + "EVIL LARRY removed this task:");
        output.accept(PRE_TASK_SPACE + task);
        showTaskCount(taskCount);
    }

    /**
     * Displays confirmation that a task was added and the new task count.
     *
     * @param task Task that was added.
     * @param taskCount Number of stored tasks.
     */
    public void showTaskAdded(Task task, int taskCount) {
        output.accept(INIT_SPACE + "EVIL LARRY has added this task for you:");
        output.accept(PRE_TASK_SPACE + task);
        showTaskCount(taskCount);
    }

    /**
     * Displays the task before and after a successful single-field edit.
     *
     * @param taskNumber One-based number of the edited task.
     * @param previousTaskText Task display text before the edit.
     * @param task Edited task.
     */
    public void showTaskEdited(int taskNumber, String previousTaskText, Task task) {
        output.accept(INIT_SPACE + "EVIL LARRY updated task " + taskNumber + ":");
        output.accept(INIT_SPACE + "Before: " + previousTaskText);
        output.accept(INIT_SPACE + "After:  " + task);
    }

    /**
     * Displays an invalid-command error.
     *
     * @param message Error message to display.
     */
    public void showError(String message) {
        output.accept(INIT_SPACE + message);
    }

    /**
     * Displays a non-fatal warning.
     *
     * @param message Warning message to display.
     */
    public void showWarning(String message) {
        errorOutput.accept("WARNING: EVIL LARRY rejected corrupted task data. " + message);
    }

    /**
     * Warns that Larry could not load the task data file.
     *
     * @param errorMessage Cause of the loading failure.
     */
    public void showLoadingError(String errorMessage) {
        errorOutput.accept("WARNING: EVIL LARRY could not seize the task data file. "
                + "He will begin with an empty vault. Details: " + errorMessage);
    }

    /**
     * Warns that Larry could not save the latest task change.
     *
     * @param errorMessage Cause of the saving failure.
     */
    public void showSavingError(String errorMessage) {
        errorOutput.accept("WARNING: EVIL LARRY could not preserve the latest task change. "
                + "It survives only for this session. Details: " + errorMessage);
    }

    /**
     * Closes the console input reader.
     */
    @Override
    public void close() {
        if (scanner != null) {
            scanner.close();
        }
    }

    /**
     * Displays the number of tasks currently stored.
     *
     * @param taskCount Number of stored tasks.
     */
    private void showTaskCount(int taskCount) {
        String taskWord = taskCount == 1 ? "task" : "tasks";
        output.accept(INIT_SPACE + "EVIL LARRY says you have " + taskCount + " "
                + taskWord + " in the list.");
    }
}
