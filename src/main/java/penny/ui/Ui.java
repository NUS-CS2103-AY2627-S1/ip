package penny.ui;

import java.util.List;
import java.util.Random;
import java.util.Scanner;

import penny.task.Task;

/**
 * Handles user interface interactions for the Penny chatbot with a Peni Parker &amp; SP//dr personality.
 * Manages standard input from the user, randomized themed feedback, and formatted output.
 */
public class Ui {

    private static final String DIVIDER = "____________________________________________________________";
    private static final String BANNER = " ___  ___  _  _  _  _  _  _ \n"
            + "| . \\| __>| \\| || \\| || | |\n"
            + "|  _/| _> | \\  || \\  |\\   /\n"
            + "|_|  |___>|_|\\_||_|\\_| |_| \n";
    private static final String INDENTATION = "     ";

    private static final String[] WELCOME_MESSAGES = {
        "Kon'nichiwa! Peni Parker online with SP//dr! "
                + "Neural link synchronized. What's the mission today?",
        "SP//dr systems online and diagnostic complete! "
                + "Ready to log some tasks, pilot?",
        "Yo! Peni here! All dimensional channels clear. "
                + "Let's get through today's objectives!"
    };

    private static final String[] GOODBYE_MESSAGES = {
        "SP//dr powering down to standby mode... "
                + "Disconnecting neural link. See you in the next dimension!",
        "Mission debrief complete! Powering off core thrusters. "
                + "Catch ya later, pilot!",
        "Disengaging interface! Don't forget to take a break and grab some snacks! Ja ne!"
    };

    private static final String[] TASK_ADDED_TEMPLATES = {
        "Mission directive logged into SP//dr!\n"
                + "  %s\n"
                + "Mission queue updated: %d active operation(s) ready for deployment!",
        "Target locked and registered in the database!\n"
                + "  %s\n"
                + "Total directives in queue: %d.",
        "Added to the tactical HUD!\n"
                + "  %s\n"
                + "SP//dr workload currently sitting at %d task(s)."
    };

    private static final String[] TASK_MARKED_TEMPLATES = {
        "Target neutralized! Objective completed:\n"
                + "  %s\n"
                + "Great work, pilot! Keep that momentum going!",
        "Direct hit! Objective cleared from the radar:\n"
                + "  %s\n"
                + "SP//dr combat efficiency +100!",
        "Boom! Mission accomplished!\n"
                + "  %s\n"
                + "One step closer to saving the day!"
    };

    private static final String[] TASK_UNMARKED_TEMPLATES = {
        "Wait, mission objective reactivated? Back to the queue:\n"
                + "  %s\n"
                + "No worries, we'll get 'em next time!",
        "Re-flagging operation as ongoing!\n"
                + "  %s\n"
                + "SP//dr sensors back on target.",
        "Status reset: objective still active!\n"
                + "  %s\n"
                + "Don't give up, pilot!"
    };

    private static final String[] TASK_DELETED_TEMPLATES = {
        "Purged from databanks! Operation eliminated:\n"
                + "  %s\n"
                + "Remaining operations: %d.",
        "Scrapped directive! Erased from SP//dr memory:\n"
                + "  %s\n"
                + "Active tasks left in tactical queue: %d.",
        "Directive aborted and discarded!\n"
                + "  %s\n"
                + "Tactical workload decreased to %d task(s)."
    };

    private static final String[] TASK_LIST_HEADERS = {
        "SP//dr Tactical HUD: Active Protocols",
        "Mission Scanner Readout: Here's what's queued up",
        "Tactical Briefing: Current task status"
    };

    private static final String[] TASK_LIST_EMPTY_MESSAGES = {
        "Radar is clear! No active missions on the scanner right now. Time for some ramen?",
        "Queue is totally empty! SP//dr is in resting idle mode.",
        "All objectives clear! Zero tasks pending in the matrix!"
    };

    private static final String[] DATE_SEARCH_HEADERS = {
        "Tactical schedule for %s",
        "Mission radar sweep for %s",
        "Protocols operating on %s"
    };

    private static final String[] DATE_SEARCH_EMPTY_MESSAGES = {
        "No missions detected on radar for %s.",
        "Tactical calendar clear: zero operations on %s.",
        "Scanner returned no tasks scheduled for %s."
    };

    private static final String[] KEYWORD_SEARCH_HEADERS = {
        "Matching tactical signatures for '%s'",
        "Scanner query results for '%s'",
        "Located protocols matching '%s'"
    };

    private static final String[] KEYWORD_SEARCH_EMPTY_MESSAGES = {
        "Zero signals matching '%s' found in SP//dr databanks.",
        "Radar search complete: no tasks match keyword '%s'.",
        "Negative contact! No directives contain '%s'."
    };

    private static final String[] ERROR_TEMPLATES = {
        "Bzzzt! System glitch! %s\n"
                + "Check the console syntax and try again, pilot!",
        "Error: Command not recognized by SP//dr!\n"
                + "%s",
        "Whoa, neural feedback error! %s\n"
                + "Let's recalibrate and try that again!"
    };

    private final Scanner scanner;
    private final Random random;

    /**
     * Constructs a Ui object initializing the input scanner and a default random generator.
     */
    public Ui() {
        this(new Random());
    }

    /**
     * Constructs a Ui object with a specified random generator for deterministic testing.
     *
     * @param random The random generator instance used for selecting phrases.
     */
    public Ui(Random random) {
        assert random != null : "Random instance must not be null";
        this.scanner = new Scanner(System.in);
        this.random = random;
    }

    /**
     * Reads a line of command input from the user.
     *
     * @return The trimmed command string entered by the user.
     */
    public String readCommand() {
        if (scanner.hasNextLine()) {
            return scanner.nextLine().trim();
        }
        return "bye";
    }

    /**
     * Prints the standard horizontal divider line.
     */
    public void showLine() {
        System.out.println(DIVIDER);
    }

    /**
     * Prints a message to the console prefixed with standard indentation.
     * Multi-line messages are formatted so each line aligns with the margin.
     *
     * @param message The message to print.
     */
    private void printIndented(String message) {
        System.out.println(INDENTATION + message.replace("\n", "\n" + INDENTATION));
    }

    /**
     * Selects a random phrase from the given array of candidate phrase strings.
     *
     * @param phrases The array of candidate phrases.
     * @return A randomly selected phrase.
     */
    private String getRandomPhrase(String[] phrases) {
        assert phrases != null && phrases.length > 0 : "Phrases array cannot be null or empty";
        int index = random.nextInt(phrases.length);
        return phrases[index];
    }

    /**
     * Selects a random template and formats it with the provided arguments.
     *
     * @param templates The array of candidate format templates.
     * @param args The format arguments.
     * @return The formatted string result.
     */
    private String getRandomFormattedPhrase(String[] templates, Object... args) {
        String template = getRandomPhrase(templates);
        return String.format(template, args);
    }

    /**
     * Formats a collection of tasks into a 1-based numbered list preceded by a header,
     * or returns the fallback message if the collection is empty.
     *
     * @param header The header text preceding the numbered items.
     * @param tasks The tasks to format into a list.
     * @param emptyMessage The fallback message to display when no tasks exist.
     * @return The formatted response string.
     */
    private String formatNumberedList(String header, List<Task> tasks, String emptyMessage) {
        if (tasks.isEmpty()) {
            printIndented(emptyMessage);
            return emptyMessage;
        }

        StringBuilder sb = new StringBuilder(header).append(":\n");
        for (int i = 0; i < tasks.size(); i++) {
            sb.append(i + 1).append(".").append(tasks.get(i).toString());
            if (i < tasks.size() - 1) {
                sb.append("\n");
            }
        }
        String response = sb.toString();
        printIndented(response);
        return response;
    }

    /**
     * Displays the welcome banner and returns a randomized greeting message.
     *
     * @return The welcome greeting string.
     */
    public String showWelcome() {
        showLine();
        System.out.println(BANNER);
        String welcome = getRandomPhrase(WELCOME_MESSAGES);
        printIndented(welcome);
        showLine();
        return welcome;
    }

    /**
     * Displays and returns a randomized goodbye exit message.
     *
     * @return The exit greeting string.
     */
    public String showGoodbye() {
        String message = getRandomPhrase(GOODBYE_MESSAGES);
        printIndented(message);
        return message;
    }

    /**
     * Displays and returns a randomized error message with standard indentation.
     *
     * @param message The message to display.
     * @return The error message string.
     */
    public String showError(String message) {
        String response = getRandomFormattedPhrase(ERROR_TEMPLATES, message);
        printIndented(response);
        return response;
    }

    /**
     * Displays and returns a warning message when initial storage loading fails.
     *
     * @param message The underlying error message.
     * @return The loading error warning string.
     */
    public String showLoadingError(String message) {
        String warning = "Warning: SP//dr could not read storage file (" + message
                + "). Starting with an empty mission queue.";
        printIndented(warning);
        return warning;
    }

    /**
     * Displays and returns feedback when a task is successfully added.
     *
     * @param task The task that was added.
     * @param totalTasks The new total count of tasks.
     * @return The task added confirmation string.
     */
    public String showTaskAdded(Task task, int totalTasks) {
        String response = getRandomFormattedPhrase(TASK_ADDED_TEMPLATES, task.toString(), totalTasks);
        printIndented(response);
        return response;
    }

    /**
     * Displays and returns feedback when a task is marked as done.
     *
     * @param task The task that was marked done.
     * @return The marked task confirmation string.
     */
    public String showTaskMarked(Task task) {
        String response = getRandomFormattedPhrase(TASK_MARKED_TEMPLATES, task.toString());
        printIndented(response);
        return response;
    }

    /**
     * Displays and returns feedback when a task is marked as not yet completed.
     *
     * @param task The task that was unmarked.
     * @return The unmarked task confirmation string.
     */
    public String showTaskUnmarked(Task task) {
        String response = getRandomFormattedPhrase(TASK_UNMARKED_TEMPLATES, task.toString());
        printIndented(response);
        return response;
    }

    /**
     * Displays and returns feedback when a task is deleted.
     *
     * @param task The task that was removed.
     * @param remainingTasks The remaining count of tasks.
     * @return The task deleted confirmation string.
     */
    public String showTaskDeleted(Task task, int remainingTasks) {
        String response = getRandomFormattedPhrase(TASK_DELETED_TEMPLATES, task.toString(), remainingTasks);
        printIndented(response);
        return response;
    }

    /**
     * Displays and returns all tasks currently in the list.
     *
     * @param tasks The list of tasks to display.
     * @return The task list string.
     */
    public String showTaskList(List<Task> tasks) {
        String header = getRandomPhrase(TASK_LIST_HEADERS);
        String emptyMessage = getRandomPhrase(TASK_LIST_EMPTY_MESSAGES);
        return formatNumberedList(header, tasks, emptyMessage);
    }

    /**
     * Displays and returns tasks matching a specific date query.
     *
     * @param matchingTasks The tasks occurring on that date.
     * @param formattedDate The formatted date string.
     * @return The matching tasks string.
     */
    public String showTasksOnDate(List<Task> matchingTasks, String formattedDate) {
        String header = getRandomFormattedPhrase(DATE_SEARCH_HEADERS, formattedDate);
        String emptyMessage = getRandomFormattedPhrase(DATE_SEARCH_EMPTY_MESSAGES, formattedDate);
        return formatNumberedList(header, matchingTasks, emptyMessage);
    }

    /**
     * Displays and returns tasks matching a keyword search query.
     *
     * @param matchingTasks The tasks whose description matches the keyword.
     * @param keyword The search keyword.
     * @return The matching tasks string.
     */
    public String showMatchingTasks(List<Task> matchingTasks, String keyword) {
        String header = getRandomFormattedPhrase(KEYWORD_SEARCH_HEADERS, keyword);
        String emptyMessage = getRandomFormattedPhrase(KEYWORD_SEARCH_EMPTY_MESSAGES, keyword);
        return formatNumberedList(header, matchingTasks, emptyMessage);
    }

    /**
     * Closes the scanner resource.
     */
    public void close() {
        this.scanner.close();
    }
}
