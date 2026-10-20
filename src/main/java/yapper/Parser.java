package yapper;

/**
 * Interprets command words and validates their arguments before execution.
 */
public class Parser {
    /**
     * Parses a nonempty keyword or phrase from a find command.
     *
     * @param input Find command supplied by the user.
     * @return Search text with surrounding whitespace removed.
     * @throws YapperException If the search text is empty.
     */
    public String parseKeyword(String input) throws YapperException {
        String keyword = input.substring("find".length()).trim();
        if (keyword.isEmpty()) {
            throw new YapperException("Please specify a keyword. Try: find <keyword>");
        }
        return keyword;
    }

    /**
     * Identifies the command represented by trimmed user input.
     *
     * @param input Trimmed command line.
     * @return Matching command type, or UNKNOWN when no command matches.
     */
    public CommandType parseCommandType(String input) {
        return CommandType.from(input);
    }

    /**
     * Creates a todo from its command, rejecting an empty description.
     *
     * @param input Command line beginning with {@code todo}.
     * @return New incomplete todo.
     * @throws YapperException If the description is empty.
     */
    public Todo parseTodo(String input) throws YapperException {
        return new Todo(getTaskDescription(input, "todo"));
    }

    /**
     * Extracts and validates a task description following a command word.
     *
     * @param input Command line beginning with the supplied command word.
     * @param command Command word to remove.
     * @return Trimmed, nonempty description.
     * @throws YapperException If no description follows the command.
     */
    private String getTaskDescription(String input, String command) throws YapperException {
        String description = input.substring(command.length()).trim();
        if (description.isEmpty()) {
            throw new YapperException("The " + command + " description cannot be empty. "
                    + "Try: " + command + " <task description>");
        }
        if (description.contains("\n") || description.contains("\r")) {
            throw new YapperException("Task descriptions must stay on one line.");
        }
        if (description.contains("|")) {
            throw new YapperException("Task descriptions cannot contain the pipe character (|).");
        }
        return description;
    }

    /**
     * Parses and validates the task number supplied to a task-index command.
     *
     * @param input Command line beginning with the supplied command word.
     * @param command Command word to remove.
     * @param taskCount Number of tasks available for selection.
     * @return Zero-based index corresponding to the one-based task number.
     * @throws YapperException If the number is missing, nonnumeric, or outside the list.
     */
    public int parseTaskIndex(String input, String command, int taskCount)
            throws YapperException {
        String indexText = input.substring(command.length()).trim();
        if (indexText.isEmpty()) {
            throw new YapperException("Please specify a task number. Try: " + command + " <task number>");
        }

        try {
            int taskIndex = Integer.parseInt(indexText) - 1;
            if (taskIndex < 0 || taskIndex >= taskCount) {
                throw new YapperException("Task number " + indexText + " is not in the list. "
                        + "Type 'list' to see the available task numbers.");
            }
            return taskIndex;
        } catch (NumberFormatException exception) {
            throw new YapperException("'" + indexText + "' is not a valid task number. "
                    + "Try: " + command + " <task number>");
        }
    }

    /**
     * Parses a deadline command, including its required /by value.
     *
     * @param input Command line beginning with {@code deadline}.
     * @return New incomplete deadline.
     * @throws YapperException If the description, separator, or supported due date is missing or invalid.
     * @see TaskDateTime#parse(String)
     */
    public Deadline parseDeadline(String input) throws YapperException {
        String details = getTaskDescription(input, "deadline");
        int bySeparator = details.indexOf(" /by ");
        if (bySeparator <= 0 || bySeparator + 5 >= details.length()) {
            throw new YapperException("A deadline needs a description and a due date. "
                    + "Try: deadline <task description> /by <date or time>");
        }
        String description = details.substring(0, bySeparator).trim();
        String by = details.substring(bySeparator + 5).trim();
        try {
            return new Deadline(description, TaskDateTime.parse(by));
        } catch (IllegalArgumentException exception) {
            throw new YapperException("Please use yyyy-MM-dd HHmm or d/M/yyyy HHmm for the deadline date."
                    + " For example: deadline return book /by 2019-12-02 1800");
        }
    }

    /**
     * Parses an event command, including its required /from and /to values.
     *
     * @param input Command line beginning with {@code event}.
     * @return New incomplete event; equal start and end times are allowed.
     * @throws YapperException If required fields are missing, dates are invalid, or the end precedes the start.
     * @see TaskDateTime#parse(String)
     */
    public Event parseEvent(String input) throws YapperException {
        String details = getTaskDescription(input, "event");
        int fromSeparator = details.indexOf(" /from ");
        int toSeparator = fromSeparator < 0 ? -1 : details.indexOf(" /to ", fromSeparator + 7);
        if (fromSeparator <= 0 || toSeparator <= fromSeparator + 7 || toSeparator + 5 >= details.length()) {
            throw new YapperException("An event needs a description, start, and end. "
                    + "Try: event <task description> /from <start> /to <end>");
        }
        String description = details.substring(0, fromSeparator).trim();
        String from = details.substring(fromSeparator + 7, toSeparator).trim();
        String to = details.substring(toSeparator + 5).trim();
        try {
            java.time.LocalDateTime start = TaskDateTime.parse(from);
            java.time.LocalDateTime end = TaskDateTime.parse(to);
            if (end.isBefore(start)) {
                throw new YapperException("The event end cannot be before its start.");
            }
            return new Event(description, start, end);
        } catch (IllegalArgumentException exception) {
            throw new YapperException("Please use yyyy-MM-dd HHmm or d/M/yyyy HHmm for event dates."
                    + " For example: event workshop /from 2019-12-02 1400 /to 2019-12-02 1600");
        }
    }

    /**
     * Parses the date argument of an on command.
     *
     * @param input Command line beginning with {@code on}.
     * @return Calendar date to search.
     * @throws YapperException If the date is missing or is not in a supported date-only format.
     */
    public java.time.LocalDate parseDate(String input) throws YapperException {
        String dateText = input.substring("on".length()).trim();
        if (dateText.isEmpty()) {
            throw new YapperException("Please specify a date. Try: on <yyyy-MM-dd>");
        }
        java.time.LocalDate date;
        try {
            date = TaskDateTime.parseDate(dateText);
        } catch (IllegalArgumentException exception) {
            throw new YapperException("Please use yyyy-MM-dd or d/M/yyyy after 'on'. "
                    + "For example: on 2019-12-02");
        }

        return date;
    }
}
