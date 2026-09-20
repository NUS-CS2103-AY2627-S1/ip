package loki.ui;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;
import java.util.Scanner;

import loki.exception.LokiExceptions;
import loki.model.Task;
import loki.model.TaskList;

/**
 * Handles interaction with the user, including input and console output.
 */
public class Ui implements AutoCloseable {
    private static final String DIALOGUE_INDENT = "        ";
    private static final String SPLASH = "|        ____   |   / |____|\n"
            + "|       /    \\  |  /     |  \n"
            + "|       |    |  |<       |  \n"
            + "|       \\    /  |  \\ |  \n"
            + "|_______ \\__/   |   \\ |____|\n";
    private static final Random RANDOM_GENERATOR = new Random();
    private static final String[] FAREWELLS = loadDialogue("farewells.txt");
    private static final String[] INVALID_TASK_RESPONSES = loadDialogue("invalid-task-responses.txt");
    private static final String[] VALID_TASK_RESPONSES = loadDialogue("valid-task-responses.txt");
    private static final String[] LOKI_EXCEPTION_MESSAGES = loadDialogue("loki-exceptions.txt");
    private static final String[] HELP_LINES = {
        "Commands:",
        "  todo <task description>",
        "    Example: todo Buy groceries",
        "  deadline <task description> /by <date/time>",
        "    Example: deadline Return book /by 2019-06-06",
        "  event <title> /from <date/time> /to <date/time>",
        "    Example: event Project meeting /from 2019-08-06 1400 /to 2019-08-06 1600",
        "  list",
        "    Example: list",
        "  mark <task number>",
        "    Example: mark 1",
        "  unmark <task number>",
        "    Example: unmark 1",
        "  delete <task number>",
        "    Example: delete 1",
        "  update <task number> <field> [<field>...]",
        "    Updates one or more fields while keeping the task type unchanged.",
        "    To-do fields: /title <new title>",
        "    Deadline fields: /title <new title>, /by <date/time>",
        "    Event fields: /title <new title>, /from <date/time>, /to <date/time>",
        "    Date formats: yyyy-MM-dd, yyyy-MM-dd HHmm, or d/M/yyyy HHmm",
        "    Examples:",
        "      update 1 /title Buy groceries and toiletries",
        "      update 2 /by 20/9/2026 1800",
        "      update 3 /title Final consultation /from 20/9/2026 1400 /to 20/9/2026 1600",
        "  help",
        "    Example: help",
        "  exit (alias: faretheewell)",
        "    Example: exit"
    };

    private final Scanner scanner;

    /** Creates a UI that reads commands from standard input. */
    public Ui() {
        scanner = new Scanner(System.in);
    }

    /**
     * Returns whether another command is available.
     *
     * @return true if another input line is available.
     */
    public boolean hasNextLine() {
        return scanner.hasNextLine();
    }

    /**
     * Reads the next command line.
     *
     * @return the next input line.
     */
    public String readLine() {
        return scanner.nextLine();
    }

    /**
     * Prints Loki's splash screen, logo, and greeting.
     *
     * @throws IOException if the logo asset cannot be read
     */
    public void showWelcome() throws IOException {
        String logo = Files.readString(resolveAsset("loki.txt"));
        showBanner();
        System.out.println(SPLASH);
        System.out.println(logo);
        showGreeting();
    }

    /** Prints a heavy divider to standard output. */
    public void showHeavyBanner() {
        System.out.println("======================================================");
    }

    /** Prints a light divider to standard output. */
    public void showBanner() {
        System.out.println("------------------------------------------------------");
    }

    /** Prints Loki's greeting. */
    public void showGreeting() {
        printHeavyDialogue(
            "Greetings, mortal",
            "Loki the Trickster God at your service",
            "Type 'help' to see commands and examples."
        );
    }

    /** Prints the supported commands, their usage, and examples. */
    public void showHelp() {
        printDialogue(HELP_LINES);
    }

    /** Prints a randomly selected farewell message. */
    public void showFarewell() {
        printHeavyDialogue(pickRandom(FAREWELLS));
    }

    /**
     * Prints a response to a user's input.
     *
     * @param text the response text
     */
    public void echo(String text) {
        printDialogue(text);
    }

    /** Prints a randomly selected response for an invalid task index. */
    public void showInvalidTaskResponse() {
        printDialogue(pickRandom(INVALID_TASK_RESPONSES));
    }

    /** Prints a temporary response for an unrecognized command. */
    public void showUnknownCommandResponse() {
        printDialogue(pickRandom(LOKI_EXCEPTION_MESSAGES));
    }

    /**
     * Prints an input or storage error.
     *
     * @param message the error message
     */
    public void showError(String message) {
        printDialogue(message);
    }

    /**
     * Prints a randomly selected response to a successful task operation.
     *
     * @param task the task involved in the operation
     */
    public void showSuccess(String task) {
        printDialogue(pickRandom(VALID_TASK_RESPONSES) + "\n          " + task);
    }

    /**
     * Prints all tasks in their one-based list order.
     *
     * @param tasks the tasks to print
     * @throws LokiExceptions if a task cannot be retrieved by its index
     */
    public void showTaskList(TaskList tasks) throws LokiExceptions {
        showBanner();
        for (int i = 1; i <= tasks.size(); i++) {
            System.out.println(String.format("      %s. %s", i, tasks.get(i)));
        }
        System.out.println();
    }

    /**
     * Prints the response after a task is added.
     *
     * @param task the task that was added
     * @param taskCount the number of tasks after the addition
     */
    public void showTaskAdded(Task task, int taskCount) {
        showSuccess(task.toString());
        showTaskCount(taskCount);
    }

    /**
     * Prints the response after a task is deleted.
     *
     * @param task the task that was deleted
     * @param taskCount the number of tasks after the deletion
     */
    public void showTaskDeleted(Task task, int taskCount) {
        showSuccess(task.toString());
        showTaskCount(taskCount);
    }

    /**
     * Prints the number of tasks remaining.
     *
     * @param taskCount the number of tasks remaining
     */
    public void showTaskCount(int taskCount) {
        echo(String.format("You have %s tasks left to conquer.", taskCount));
    }

    /**
     * Returns a randomly selected Loki exception message.
     *
     * @return a formatted Loki exception message
     */
    public static String getRandomExceptionMessage() {
        return "Loki error: " + pickRandom(LOKI_EXCEPTION_MESSAGES);
    }

    /** Closes the scanner used for command input. */
    @Override
    public void close() {
        scanner.close();
    }

    /**
     * Prints one or more lines between light dividers.
     *
     * @param lines the dialogue lines to print
     */
    private void printDialogue(String... lines) {
        showBanner();
        for (String line : lines) {
            System.out.println(DIALOGUE_INDENT + line);
        }
        showBanner();
    }

    /**
     * Prints one or more lines between heavy dividers.
     *
     * @param lines the dialogue lines to print
     */
    private void printHeavyDialogue(String... lines) {
        showHeavyBanner();
        for (String line : lines) {
            System.out.println(DIALOGUE_INDENT + line);
        }
        showHeavyBanner();
    }

    /**
     * Selects one response from an array of choices.
     *
     * @param choices the available responses
     * @return one randomly selected response
     */
    private static String pickRandom(String[] choices) {
        // Internal callers supply dialogue arrays initialized by loadDialogue before selection.
        assert choices != null && choices.length > 0 : "Dialogue choices must already be loaded";
        return choices[RANDOM_GENERATOR.nextInt(choices.length)];
    }

    /**
     * Loads dialogue entries separated by a line containing {@code +}.
     *
     * @param filename the dialogue asset filename
     * @return the dialogue entries in the asset
     * @throws ExceptionInInitializerError if the asset cannot be read or is empty
     */
    private static String[] loadDialogue(String filename) {
        try {
            String content = Files.readString(resolveAsset(filename));
            content = content.replaceFirst("\\r?\\n$", "");
            String[] entries = content.split("\\r?\\n\\+\\r?\\n");
            if (entries.length == 0) {
                throw new IOException("file contains no dialogue entries");
            }
            return entries;
        } catch (IOException exception) {
            throw new ExceptionInInitializerError(
                    "Unable to read dialogue file " + filename + ": " + exception.getMessage());
        }
    }

    /**
     * Resolves an asset from the working directory or the bundled assets directory.
     *
     * @param filename the asset filename
     * @return the path to use for the asset
     */
    private static Path resolveAsset(String filename) {
        Path workingDirectoryAsset = Path.of(filename);
        if (Files.exists(workingDirectoryAsset)) {
            return workingDirectoryAsset;
        }
        return Path.of("src", "assets", filename);
    }
}
