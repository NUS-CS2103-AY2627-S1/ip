package spoon.ui;

import java.time.LocalDate;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import spoon.command.Command;
import spoon.task.Task;
import spoon.task.TaskList;
import spoon.util.DateFormat;

/**
 * Handles the user interface and display of Spoon.
 */
public class UserInterface {
    // String definitions
    private static final String DIVIDER = "-".repeat(80);
    private static final String BANNER = "~~~ Welcome to Spoon ~~~";
    private static final String INTRODUCTION = "Hello, I'm Spoon, your friendly neighbourhood chatbot!"
            + System.lineSeparator() + "What do you wanna talk about?";
    private static final String LOAD_SUCCESSFUL = "Tasks loaded! Time to get to work!";
    private static final String LOADING_ERROR = "Error reading storage file: %s";
    private static final String WRITING_ERROR = "Error writing to storage file: %s";
    private static final String LIST_EMPTY = "Nothing here yet...";
    private static final String TASK_ADDED = "I've added this task to the list! :)";
    private static final String LIST_LENGTH = "Now, you have %d task(s)! \uD83D\uDC4D";
    private static final String LIST_INTRODUCTION = "Here's your list!";
    private static final String TASK_FILTER = "Filter criteria: %s %s";
    private static final String MARK_COMPLETE = "YAYYYY, task complete!";
    private static final String MARK_INCOMPLETE = "Oops, there's more work to be done!";
    private static final String TASK_DELETED = "Okay, task deleted!";
    private static final String SAVE_SUCCESS = "Tasks saved! Ready for next time!";
    private static final String EXIT = "Goodbye! Let's speak again soon!";

    private static final String HELP = "Here are the available commands:" + System.lineSeparator()
            + "  • todo <desc>              - Add a todo task" + System.lineSeparator()
            + "  • deadline <desc> /by <dd/MM/yyyy [HHmm]>    - Add a deadline" + System.lineSeparator()
            + "  • event <desc> /from <..> /to <..>           - Add an event" + System.lineSeparator()
            + "  • list                     - List all tasks" + System.lineSeparator()
            + "  • mark <index>             - Mark task as done" + System.lineSeparator()
            + "  • unmark <index>           - Mark task as not done" + System.lineSeparator()
            + "  • delete <index>           - Delete task" + System.lineSeparator()
            + "  • find <keyword[, ...]>    - Search tasks by keywords" + System.lineSeparator()
            + "  • on <dd/MM/yyyy>          - List tasks due/occurring on date" + System.lineSeparator()
            + "  • by <dd/MM/yyyy>          - List tasks due before date" + System.lineSeparator()
            + "  • help                     - Show this help guide" + System.lineSeparator()
            + "  • bye                      - Exit Spoon";

    private final Scanner scanner;

    // Constructor
    public UserInterface() {
        this.scanner = new Scanner(System.in);
    }

    // Methods for interacting with user input
    public String readCommand() {
        return scanner.nextLine();
    }

    // Methods for printing pre-defined texts
    public String showDivider() {
        return DIVIDER + System.lineSeparator();
    }

    public String showIntroduction() {
        return INTRODUCTION + System.lineSeparator();
    }

    public String showStart() {
        return BANNER + System.lineSeparator()
                + showDivider()
                + INTRODUCTION + System.lineSeparator()
                + showDivider();
    }

    public String showError(String message) {
        return message + System.lineSeparator();
    }

    public String showHelp() {
        return HELP;
    }

    // Methods for printing interactions with storage (external file)
    public String showLoadSuccess() {
        return LOAD_SUCCESSFUL + System.lineSeparator();
    }

    public String showLoadingError(String message) {
        return String.format(LOADING_ERROR, message) + System.lineSeparator();
    }

    public String showWritingError(String message) {
        return String.format(WRITING_ERROR, message) + System.lineSeparator();
    }

    // Methods for printing interactions with the task list
    public String showTaskList(TaskList tasks) {
        if (tasks.size() == 0) {
            return LIST_EMPTY + System.lineSeparator();
        } else {
            String message = LIST_INTRODUCTION + System.lineSeparator();
            for (int i = 0; i < tasks.size(); i++) {
                message += (i + 1) + ". " + tasks.get(i) + System.lineSeparator();
            }
            return message;
        }
    }

    public String showAdded(Task task, int totalTasks) {
        return TASK_ADDED + System.lineSeparator()
                + task + System.lineSeparator()
                + String.format(LIST_LENGTH, totalTasks) + System.lineSeparator();
    }

    public String showMarked(Task task) {
        return MARK_COMPLETE + System.lineSeparator()
                + task + System.lineSeparator();
    }

    public String showUnmarked(Task task) {
        return MARK_INCOMPLETE + System.lineSeparator()
                + task + System.lineSeparator();
    }

    public String showDeleted(Task task, int totalTasks) {
        return TASK_DELETED + System.lineSeparator()
                + task + System.lineSeparator()
                + String.format(LIST_LENGTH, totalTasks) + System.lineSeparator();
    }

    /**
     * Prints the tasks occurring on / by the date.
     *
     * @param tasks list of tasks to be printed.
     * @param date date where the tasks occur on / by.
     * @param connective string to be inserted into output message.
     * @return formatted string of all filtered tasks.
     */
    public String showFilteredTasks(TaskList tasks, LocalDate date, Command connective) {
        String header = String.format(TASK_FILTER, connective.toString().toLowerCase(),
                DateFormat.toDisplay(date)) + System.lineSeparator();
        return formatTaskList(tasks, header);
    }

    /**
     * Displays the list of tasks matching a search keyword.
     *
     * @param tasks list of matching tasks.
     * @return formatted string of all filtered tasks.
     */
    public String showFilteredTasks(TaskList tasks, String word) {
        String header = String.format(TASK_FILTER, "contains", word)
                + System.lineSeparator();
        return formatTaskList(tasks, header);
    }

    /**
     * Formats the tasks into an indexed list.
     *
     * @param tasks list of matching tasks.
     * @param header start of the task list (after it has been converted to string).
     * @return formatted string of all filtered tasks.
     */
    private String formatTaskList(TaskList tasks, String header) {
        if (tasks.size() == 0) {
            return LIST_EMPTY + System.lineSeparator();
        } else {
            String formattedTasks = IntStream.range(0, tasks.size())
                    .mapToObj(i -> (i + 1) + ". " + tasks.get(i))
                    .collect(Collectors.joining(System.lineSeparator()));
            return header + formattedTasks + System.lineSeparator();
        }
    }

    // Methods for closing Spoon
    public String showSave() {
        return SAVE_SUCCESS + System.lineSeparator();
    }

    public String showExit() {
        return EXIT + System.lineSeparator();
    }

    public void close() {
        scanner.close();
    }

    // Method for printing to terminal (CLI)
    /**
     * Prints messages to the terminal.
     *
     * @param messages an arbitrary number of messages to be printed.
     *                 can accept zero or more arguments.
     */
    public void print(String... messages) {
        for (String message : messages) {
            System.out.print(message);
        }
    }
}
