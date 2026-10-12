package echo.ui;

import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import echo.task.Task;

/**
 * Handles all user interface interaction for E.C.H.O.
 * (Everyday Conversational and Helpful Operator).
 */
public class Ui implements AutoCloseable {
    /** Horizontal line used to frame E.C.H.O. responses. */

    private static final String SEPARATOR = "----------------------------------------"
            + "------------------------------------------------------------------";

    /** Prompt displayed while waiting for user input. */
    private static final String PROMPT = "[E.C.H.O. // SYS] >> ";

    /** Indentation applied to each response line. */
    private static final String RESPONSE_INDENT = "  ";

    /** Maximum response content width before a line is wrapped. */
    private static final int RESPONSE_CONTENT_WIDTH = SEPARATOR.length() - RESPONSE_INDENT.length();

    /** Help text describing the commands supported by E.C.H.O. */
    private static final String HELP_MESSAGE = """
        === TACTICAL PROTOCOL DIRECTIVES ===
          help                                                                      Show tactical manual
          list                                                                      Display active directives
          todo <description>                                                        Log standard directive
          deadline <description> /by <dd-mm-yyyy> [HH:MM]                           Log deadline objective
          event <description> /from <dd-mm-yyyy> [HH:MM] /to <dd-mm-yyyy> [HH:MM]   Log scheduled mission
          mark <number>                                                             Mark objective as complete
          unmark <number>                                                           Reopen active directive
          delete <number>                                                           Purge directive from log
          find <keyword>                                                            Scan telemetry by keyword
          sort [date|name]                                                          Sort by date or name
          bye                                                                       Disconnect from E.C.H.O.

        Directive numbers are indexed in the 'list' manifest.""";

    /** Startup banner displayed when an E.C.H.O. session begins. */
    private static final String BANNER = """
       ______ _____ _   _  ____  \s
      |  ____/ ____| | | |/ __ \\ \s
      | |__ | |    | |_| | |  | |
      |  __|| |    |  _  | |  | |
      | |___| |____| | | | |__| |
      |______\\_____|_| |_|\\____/ \s
      [ Everyday Conversational & Helpful Operator // v2.1 ]
        """;

    /** Farewell messages selected randomly when a session ends. */
    private static final List<String> FAREWELL_FLAVOURS = List.of(
            "[SIGNAL TERMINATED] Cycling down telemetry arrays. E.C.H.O. signing off.",
            "[COMM OFFLINE] Encrypting directive memory banks. Stay sharp out there, Commander.",
            "[STANDBY MODE] Powering down transmitter. Telemetry preserved. Catch you soon!"
    );

    /** Random generator used to select a farewell message. */
    private static final Random RANDOM = new Random();

    /** Input source used by this interface. */
    private final Scanner scanner;

    /** Creates an interface that reads from standard input. */
    public Ui() {
        this(new Scanner(System.in));
    }

    /**
     * Creates an interface using the supplied input source.
     *
     * @param scanner input source used to read commands.
     */
    public Ui(Scanner scanner) {
        this.scanner = scanner;
    }

    /**
     * Displays the startup banner and connection message.
     *
     * @return the welcome message.
     */
    public String showWelcome() {
        System.out.println(BANNER);
        return showMessage(getWelcomeMessage());
    }

    /**
     * Returns the startup welcome message.
     *
     * @return the welcome message.
     */
    public String getWelcomeMessage() {
        return "[SECURE UPLINK ESTABLISHED]\n"
                + "E.C.H.O. (Everyday Conversational & Helpful Operator) Online and listening!\n"
                + "Sensor telemetry active. Awaiting directive. Type 'help' for tactical protocols.";
    }

    /**
     * Reads one trimmed command from the user.
     *
     * @return the command, or {@code null} when input has ended.
     */
    public String readCommand() {
        System.out.print(PROMPT);
        if (!scanner.hasNextLine()) {
            return null;
        }
        return scanner.nextLine().trim();
    }

    /**
     * Displays the available commands.
     *
     * @return the help message.
     */
    public String showHelp() {
        return showMessage(HELP_MESSAGE);
    }

    /**
     * Displays an error message.
     *
     * @param message error message to be displayed.
     * @return the error message.
     */
    public String showError(String message) {
        return showMessage(message);
    }

    /**
     * Displays a recoverable error that occurred while loading saved tasks.
     *
     * @return the loading error message.
     */
    public String showLoadingError() {
        return showError("[STORAGE FAULT DETECTED] Failed to load directive archive. "
                + "Initializing empty register.");
    }

    /**
     * Displays all tasks, or an empty-list message when there are no tasks.
     *
     * @param tasks list of tasks.
     * @return the task list message.
     */
    public String showTaskList(List<Task> tasks) {
        if (tasks.isEmpty()) {
            return showMessage("Tactical radar is clear. No active directives on record.");
        }

        String taskList = IntStream.range(0, tasks.size())
                .mapToObj(i -> (i + 1) + ": " + tasks.get(i))
                .collect(Collectors.joining("\n"));
        return showMessage("[ACTIVE DIRECTIVE MANIFEST]\n" + taskList);
    }

    /**
     * Displays search results matching a keyword, or a not-found message if empty.
     *
     * @param keyword search keyword.
     * @param tasks matching tasks.
     * @return the search results message.
     */
    public String showFindResults(String keyword, List<Task> tasks) {
        if (tasks.isEmpty()) {
            return showMessage("No directives found matching keyword: '" + keyword + "'.");
        }

        String taskList = IntStream.range(0, tasks.size())
                .mapToObj(i -> (i + 1) + ": " + tasks.get(i))
                .collect(Collectors.joining("\n"));
        return showMessage("[MATCHING DIRECTIVES]\n" + taskList);
    }

    /**
     * Displays all tasks after being sorted, or an empty-list message when there are no tasks.
     *
     * @param tasks sorted list of tasks.
     * @return the sorted task list message.
     */
    public String showSorted(List<Task> tasks) {
        if (tasks.isEmpty()) {
            return showMessage("Directive manifest is empty! Nothing to sort.");
        }

        String taskList = IntStream.range(0, tasks.size())
                .mapToObj(i -> (i + 1) + ": " + tasks.get(i))
                .collect(Collectors.joining("\n"));
        return showMessage("[TELEMETRY REORDERED]\nDirectives organized successfully:\n" + taskList);
    }

    /**
     * Displays a randomly selected farewell message.
     *
     * @return the farewell message.
     */
    public String showFarewell() {
        assert !FAREWELL_FLAVOURS.isEmpty() : "At least one farewell message must be configured";
        String farewell = FAREWELL_FLAVOURS.get(RANDOM.nextInt(FAREWELL_FLAVOURS.size()));
        return showMessage(farewell);
    }

    /**
     * Displays the task created by a successful add command.
     *
     * @param task the new task created.
     * @param totalTasks total number of tasks.
     * @return the task added message.
     */
    public String showAdded(Task task, int totalTasks) {
        return showMessage("[DIRECTIVE LOGGED]\nCommitted to tactical memory:\n  " + task
                + "\nActive directives in backlog: " + totalTasks);
    }

    /**
     * Displays the result of marking a task as done.
     *
     * @param task task that was marked as done.
     * @return the status change message.
     */
    public String showMarked(Task task) {
        return showMessage("[OBJECTIVE NEUTRALIZED]\nTarget marked as completed:\n  " + task);
    }

    /**
     * Displays the result of marking a task as not done.
     *
     * @param task task that was marked as not done.
     * @return the status change message.
     */
    public String showUnmarked(Task task) {
        return showMessage("[DIRECTIVE REOPENED]\nObjective marked as in-progress:\n  " + task);
    }

    /**
     * Displays the removed task and the number of tasks remaining.
     *
     * @param taskNumber task number of the removed task.
     * @param task the task that has been removed.
     * @param remainingTasks remaining number of tasks.
     * @return the task deleted message.
     */
    public String showDeleted(int taskNumber, Task task, int remainingTasks) {
        return showMessage("[PURGE COMPLETE]\nPurged directive #" + taskNumber + " from telemetry:\n  " + task
                + "\nRemaining active directives: " + remainingTasks);
    }

    /** Releases the input source owned by this interface. */
    @Override
    public void close() {
        scanner.close();
    }

    /** Prints a message using the standard E.C.H.O. response layout. */
    private String showMessage(String message) {
        System.out.println(SEPARATOR);
        System.out.println(wrapResponse(message));
        System.out.println(SEPARATOR + "\n");
        return message;
    }

    /** Wraps each response line so it fits within the separator width. */
    private String wrapResponse(String message) {
        String[] lines = message.split("\\R", -1);
        StringBuilder wrapped = new StringBuilder();

        for (int i = 0; i < lines.length; i++) {
            if (i > 0) {
                wrapped.append("\n");
            }
            appendWrappedLine(wrapped, lines[i]);
        }

        return wrapped.toString();
    }

    /** Appends one response line, breaking it at a word boundary when possible. */
    private void appendWrappedLine(StringBuilder output, String line) {
        if (line.isEmpty()) {
            output.append(RESPONSE_INDENT);
            return;
        }

        String remaining = line;
        boolean isFirstSegment = true;

        while (!remaining.isEmpty()) {
            if (!isFirstSegment) {
                output.append("\n");
            }

            int end = Math.min(RESPONSE_CONTENT_WIDTH, remaining.length());
            int breakAt = end < remaining.length() ? findWordBreak(remaining, end) : end;
            assert breakAt > 0 && breakAt <= remaining.length()
                    : "Response wrapping must consume at least one character";

            output.append(RESPONSE_INDENT).append(remaining, 0, breakAt);
            remaining = remaining.substring(breakAt).stripLeading();
            isFirstSegment = false;
        }
    }

    /** Finds the last whitespace before the width limit, or uses a hard break. */
    private int findWordBreak(String line, int end) {
        int firstContent = 0;
        while (firstContent < line.length() && Character.isWhitespace(line.charAt(firstContent))) {
            firstContent++;
        }

        for (int i = end - 1; i > firstContent; i--) {
            if (Character.isWhitespace(line.charAt(i))) {
                return i;
            }
        }

        return end;
    }
}
