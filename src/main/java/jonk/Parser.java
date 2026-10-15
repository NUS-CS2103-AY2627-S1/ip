package jonk;

/**
 * Interprets and validates commands entered by the user.
 */
public final class Parser {

    private Parser() {
        // Prevent instantiation because command parsing does not require stored state.
    }

    /**
     * Extracts the first word that identifies a command.
     *
     * @param input Complete command entered by the user.
     * @return Command word, or an empty string for blank input.
     */
    public static String parseCommandWord(String input) {
        return splitCommand(input)[0];
    }

    /**
     * Extracts a one-based task number from a task-management command.
     *
     * @param input Complete command entered by the user.
     * @return Parsed one-based task number.
     * @throws JonkException If the command does not contain exactly one whole number.
     */
    public static int parseTaskNumber(String input) throws JonkException {
        String[] parts = splitCommand(input);
        if (parts.length != 2) {
            throw new JonkException("Mission control needs exactly one task number.");
        }

        try {
            return Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            throw new JonkException("Task coordinates must be a whole number.");
        }
    }

    /**
     * Extracts the keyword from a find command.
     *
     * @param input Complete find command entered by the user.
     * @return Keyword to search for in task descriptions.
     * @throws JonkException If the command does not contain a keyword.
     */
    public static String parseKeyword(String input) throws JonkException {
        String[] parts = splitCommand(input);
        if (parts.length != 2 || parts[1].isBlank()) {
            throw new JonkException("Send a keyword for Jonk to scan.");
        }
        return parts[1].trim();
    }

    /**
     * Creates a task from a todo, deadline, or event command.
     *
     * @param input Complete add-task command entered by the user.
     * @return Task described by the command.
     * @throws JonkException If required task details are missing or malformed.
     */
    public static Task parseTask(String input) throws JonkException {
        String[] parts = splitCommand(input);
        String taskType = parts[0];

        if (parts.length < 2 || parts[1].isBlank()) {
            throw missingTaskDetailsException(taskType);
        }

        String[] details = parts[1].trim().split("\\s+(?=/)");
        return switch (taskType) {
            case "todo" -> parseTodo(details);
            case "deadline" -> parseDeadline(details);
            case "event" -> parseEvent(details);
            default -> throw unclearSignalException();
        };
    }

    /**
     * Splits a command into its operation and remaining arguments.
     *
     * @param input Complete command entered by the user.
     * @return One or two command parts.
     */
    private static String[] splitCommand(String input) {
        return input.trim().split("\\s+", 2);
    }

    /**
     * Creates a todo from its parsed command details.
     *
     * @param details Todo command details.
     * @return Parsed todo.
     * @throws JonkException If the description is empty.
     */
    private static Task parseTodo(String[] details) throws JonkException {
        if (details[0].isBlank()) {
            throw new JonkException("A todo mission needs a description.");
        }
        return new Todo(details[0]);
    }

    /**
     * Creates a deadline from its parsed command details.
     *
     * @param details Deadline command details.
     * @return Parsed deadline.
     * @throws JonkException If its description or due date is missing or malformed.
     */
    private static Task parseDeadline(String[] details) throws JonkException {
        if (details.length != 2) {
            throw new JonkException("A deadline mission needs a non-empty /by date.");
        }

        String[] dueDateDetails = details[1].trim().split("\\s+", 2);
        if (dueDateDetails.length != 2 || !dueDateDetails[0].equals("/by")
                || dueDateDetails[1].isBlank()) {
            throw new JonkException("A deadline mission needs a non-empty /by date.");
        }

        return new Deadline(details[0], dueDateDetails[1].trim());
    }

    /**
     * Creates an event from its parsed command details.
     *
     * @param details Event command details.
     * @return Parsed event.
     * @throws JonkException If its description or dates are missing or malformed.
     */
    private static Task parseEvent(String[] details) throws JonkException {
        if (details.length != 3) {
            throw new JonkException("An event mission needs non-empty /from and /to dates.");
        }

        String[] fromDetails = details[1].trim().split("\\s+", 2);
        String[] toDetails = details[2].trim().split("\\s+", 2);
        boolean hasValidFrom = fromDetails.length == 2
                && fromDetails[0].equals("/from")
                && !fromDetails[1].isBlank();
        boolean hasValidTo = toDetails.length == 2
                && toDetails[0].equals("/to")
                && !toDetails[1].isBlank();

        if (!hasValidFrom || !hasValidTo) {
            throw new JonkException("An event mission needs non-empty /from and /to dates.");
        }

        return new Event(details[0], fromDetails[1].trim(), toDetails[1].trim());
    }

    /**
     * Creates the appropriate missing-details error for an add-task command.
     *
     * @param taskType Task type named by the command.
     * @return User-friendly validation error.
     */
    private static JonkException missingTaskDetailsException(String taskType) {
        return switch (taskType) {
            case "todo" -> new JonkException("A todo mission needs a description.");
            case "deadline" -> new JonkException("A deadline mission needs a non-empty /by date.");
            case "event" -> new JonkException(
                    "An event mission needs non-empty /from and /to dates.");
            default -> unclearSignalException();
        };
    }

    /**
     * Creates the shared response for an unrecognized transmission.
     *
     * @return Error that directs the user to the mission guide.
     */
    private static JonkException unclearSignalException() {
        return new JonkException("Signal unclear. Type help to open the mission guide.");
    }
}
