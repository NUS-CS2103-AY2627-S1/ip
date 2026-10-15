package zen.ui;

import java.time.LocalDate;
import java.util.Scanner;

import zen.task.Task;
import zen.task.TaskList;

/** Records command responses for the graphical interface and reads legacy console input. */
public class Ui {
    private static final String DIVIDER = "____________________________________________________________";
    private static final String INDENTATION = " ".repeat(5);
    private static final String GREETING_INDENTATION = " ".repeat(4);
    private static final String INDENTED_DIVIDER = GREETING_INDENTATION + DIVIDER;
    private static final String BANNER = """
            ______             \s
           |__  /___  _ __     \s
             / // _ \\| '_ \\  \s
            / /|  __/| | | |   \s
           /____\\___||_| |_|\
           """;
    private final Scanner scanner;
    private final StringBuilder response;
    private boolean isTerminalOutputEnabled;

    /** Creates a user interface that reads commands from standard input. */
    public Ui() {
        this.scanner = new Scanner(System.in);
        this.response = new StringBuilder();
        this.isTerminalOutputEnabled = false;
    }

    /** Returns the next trimmed command read from standard input. */
    public String readCommand() {
        return scanner.nextLine().trim();
    }

    /**
     * Enables or disables formatted terminal output for debugging.
     *
     * @param isTerminalOutputEnabled whether terminal output is enabled
     */
    public void setTerminalOutputEnabled(boolean isTerminalOutputEnabled) {
        this.isTerminalOutputEnabled = isTerminalOutputEnabled;
    }

    /**
     * Prints each supplied line with the standard console indentation when debugging is enabled.
     *
     * @param lines lines to print; embedded line breaks are also indented
     */
    private void printIndented(String... lines) {
        if (!isTerminalOutputEnabled) {
            return;
        }

        for (String line : lines) {
            // Ensure each line after a line break begins with indentation.
            for (String individualLine : line.split("\\R")) {
                System.out.println(INDENTATION + individualLine);
            }
        }
    }

    /**
     * Records lines that the graphical user interface displays after a command.
     *
     * @param lines response lines to record
     */
    private void recordResponse(String... lines) {
        for (String line : lines) {
            if (!response.isEmpty()) {
                response.append('\n');
            }
            response.append(line);
        }
    }

    /** Clears the response accumulated for the previous command. */
    public void clearResponse() {
        response.setLength(0);
    }

    /**
     * Returns the response accumulated while executing the current command.
     *
     * @return the command response without console indentation
     */
    public String getResponse() {
        return response.toString();
    }

    /** Prints a divider between terminal messages when debugging is enabled. */
    public void printDivider() {
        if (isTerminalOutputEnabled) {
            System.out.println(INDENTED_DIVIDER);
        }
    }

    /**
     * Prints confirmation that a task was added.
     *
     * @param task the task that was added.
     * @param size the resulting number of tasks.
     */
    public void printAddTask(Task task, int size) {
        String[] lines = {
            "Got it. I've added this task:",
            String.format("  %s", task),
            getTaskCountMessage(size)
        };
        echo(lines);
    }

    /**
     * Records a message for the graphical user interface.
     *
     * @param lines message lines to record
     */
    public void echo(String... lines) {
        printIndented(lines);
        recordResponse(lines);
    }

    /**
     * Prints a task list, or a message when it is empty.
     *
     * @param taskList the task list to print.
     */
    public void printTaskList(TaskList taskList) {
        if (taskList.isEmpty()) {
            echo("Your task list is currently empty.");
        } else {
            echo("Here are the tasks in your list:", taskList.toString());
        }
    }

    /**
     * Prints a task list which contains tasks occurring on the date, or a message when it is empty.
     *
     * @param taskList the task list to print.
     */
    public void printTaskListOccurring(TaskList taskList, LocalDate date) {
        if (taskList.isEmpty()) {
            echo("No tasks occurring on " + date.toString() + ".");
        } else {
            echo("Task occurring on " + date.toString() + ":", taskList.toString());
        }
    }

    /**
     * Displays the matching tasks, or a message when no tasks match.
     *
     * @param taskList the tasks that match a find command
     */
    public void printMatchingTasks(TaskList taskList) {
        if (taskList.isEmpty()) {
            echo("No matching tasks in your list.");
        } else {
            echo("Here are the matching tasks in your list:", taskList.toString());
        }
    }

    /**
     * Prints confirmation that a task was completed.
     *
     * @param task the completed task.
     */
    public void printMarkTaskDone(Task task) {
        String[] lines = {
            "I've marked this task as done:",
            String.format("  %s", task)
        };
        echo(lines);
    }

    /**
     * Prints confirmation that a task was marked incomplete.
     *
     * @param task the incomplete task.
     */
    public void printMarkTaskNotDone(Task task) {
        String[] lines = {
            "Okay, I've marked this task as not done:",
            String.format("  %s", task)
        };
        echo(lines);
    }

    /**
     * Prints confirmation that a task was deleted.
     *
     * @param task the deleted task.
     * @param size the resulting number of tasks.
     */
    public void printDeleteTask(Task task, int size) {
        String[] lines = {
            "Noted. I've removed this task:",
            String.format("  %s", task),
            getTaskCountMessage(size)
        };
        echo(lines);
    }

    /**
     * Returns a grammatically correct message describing the current number of tasks.
     *
     * @param size number of tasks in the list
     * @return task-count message
     */
    private static String getTaskCountMessage(int size) {
        String taskWord = size == 1 ? "task" : "tasks";
        return String.format("You now have %d %s in the list.", size, taskWord);
    }

    /**
     * Prints the application greeting when terminal output is enabled for debugging.
     *
     * @param greeting greeting shared with the graphical interface
     */
    public void printGreeting(String greeting) {
        if (!isTerminalOutputEnabled) {
            return;
        }

        printDivider();
        printGreetingContent(BANNER, greeting);
        printDivider();
        System.out.println();
    }

    /**
     * Prints greeting lines using the divider's indentation.
     *
     * @param lines greeting lines to print
     */
    private static void printGreetingContent(String... lines) {
        for (String line : lines) {
            for (String individualLine : line.split("\\R")) {
                System.out.println(GREETING_INDENTATION + individualLine);
            }
        }
    }

    /** Records the farewell message and closes standard input. */
    public void printExitMessage() {
        echo("Bye. See you again soon!");
        scanner.close();
    }

    /**
     * Prints an initialization error message when terminal output is enabled for debugging.
     *
     * @param errorMessage the error explanation
     */
    public void printErrorMessage(String errorMessage) {
        if (!isTerminalOutputEnabled) {
            return;
        }

        String exitMessage = """
                %s
                    Error occurred: %s
                %s
                """.formatted(INDENTED_DIVIDER, errorMessage, INDENTED_DIVIDER);
        System.out.println(exitMessage);
    }
}
