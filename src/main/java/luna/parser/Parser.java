package luna.parser;

import java.util.Map;

import luna.exception.LunaException;

/**
 * Parses raw user input into command words and arguments.
 * Validates input format before Luna tries to execute anything.
 */
public class Parser {
    /** Maps command words to their shorter aliases. */
    private static final Map<String, String> COMMAND_ALIASES = Map.ofEntries(
            Map.entry("ls", "list"),
            Map.entry("t", "todo"),
            Map.entry("d", "deadline"),
            Map.entry("e", "event"),
            Map.entry("m", "mark"),
            Map.entry("u", "unmark"),
            Map.entry("del", "delete"),
            Map.entry("f", "find"),
            Map.entry("q", "bye")
    );

    private static final String FORMAT_MESSAGE_DEADLINE = "A deadline must include a description and end date in "
            + "this format: deadline <desc> /by <end>\n"
            + "Example: deadline return book /by 2019-10-15";

    private static final String FORMAT_MESSAGE_EVENT = "An event must include a description, start date, and "
            + "end date in "
            + "this format: event <desc> /from <start> /to <end>\n"
            + "Example: event project meeting /from 2019-10-15 /to 2019-10-16";

    /**
     * Splits raw input into [commandWord, rest of arguments] and resolves command aliases.
     * Example: [todo, description].
     *
     * @param input Raw user input.
     * @return String array of size 2: [commandWord, rest of arguments].
     * @throws LunaException If user input is empty.
     */
    public static String[] parse(String input) throws LunaException {
        String trimmedInput = input.trim();
        if (trimmedInput.isEmpty()) {
            throw new LunaException("Please enter a command");
        }

        // Splits input by one or more whitespaces
        String[] parts = trimmedInput.split("\\s+", 2);
        String commandWord = parts[0].toLowerCase();
        // If commandWord is an alias we get its corresponding value (the full command)
        // Otherwise just return the commandWord as is
        commandWord = COMMAND_ALIASES.getOrDefault(commandWord, commandWord);
        String commandArgs = (parts.length == 2) ? parts[1].trim() : "";

        return new String[] {commandWord, commandArgs};
    }

    /**
     * Extracts and parses the task index from the arguments of a task-number command.
     * Converts the user-provided task number (1-based) into a 0-based index for internal use.
     *
     * @param commandArgs User input after the command word.
     * @param commandWord Command word, such as {@code mark} or {@code delete}.
     * @param listSize Current number of tasks.
     * @return 0-based index.
     * @throws LunaException If the task number is missing/not a number/out of range.
     */
    public static int parseIndex(String commandArgs, String commandWord, int listSize) throws LunaException {
        String trimmedArgs = normalizeArgs(commandArgs);
        if (trimmedArgs.isEmpty()) {
            throw new LunaException("Please provide a task number. Example: " + commandWord + " 2");
        }

        int taskNumber;
        try {
            taskNumber = Integer.parseInt(trimmedArgs);
        } catch (NumberFormatException e) {
            throw new LunaException("Task number must be an integer. Example: " + commandWord + " 2");
        }

        if (taskNumber < 1 || taskNumber > listSize) {
            throw new LunaException("Task number is out of range. Use 1 to " + listSize + ".");
        }

        return taskNumber - 1;
    }

    /**
     * Parses the keyword for the user command {@code find}.
     *
     * @param commandArgs User input after {@code find}.
     * @return Keyword to search for.
     * @throws LunaException If the keyword is missing.
     */
    public static String parseKeyword(String commandArgs) throws LunaException {
        String keyword = normalizeArgs(commandArgs);
        if (keyword.isEmpty()) {
            throw new LunaException("Please provide a keyword to search for. Example: find book");
        }
        return keyword;
    }

    /**
     * Parses the description of a todo.
     *
     * @param commandArgs User input after {@code todo}.
     * @return Description.
     * @throws LunaException If the command arguments are empty.
     */
    public static String parseTodo(String commandArgs) throws LunaException {
        String desc = normalizeArgs(commandArgs);
        if (desc.isEmpty()) {
            throw new LunaException("The description of a todo cannot be empty. Example: todo read book");
        }

        return validateDescription(desc);
    }

    /**
     * Parses the description and date of a deadline task.
     *
     * @param commandArgs User input after {@code deadline}.
     * @return String array of size 2: [description, by].
     * @throws LunaException If input format is invalid.
     */
    public static String[] parseDeadline(String commandArgs) throws LunaException {
        String trimmedArgs = normalizeArgs(commandArgs);
        if (trimmedArgs.isEmpty() || !trimmedArgs.contains(" /by ")) {
            throw new LunaException(FORMAT_MESSAGE_DEADLINE);
        }

        int byPos = trimmedArgs.indexOf(" /by ");
        String desc = trimmedArgs.substring(0, byPos).trim();
        String by = trimmedArgs.substring(byPos + " /by ".length()).trim();
        if (desc.isEmpty() || by.isEmpty()) {
            throw new LunaException(FORMAT_MESSAGE_DEADLINE);
        }

        return new String[] {validateDescription(desc), by};
    }

    /**
     * Parses the description, start date, and end date of an event.
     *
     * @param commandArgs User input after {@code event}.
     * @return String array of size 3: [description, startDate, endDate].
     * @throws LunaException If input format is invalid.
     */
    public static String[] parseEvent(String commandArgs) throws LunaException {
        String trimmedArgs = normalizeArgs(commandArgs);
        if (trimmedArgs.isEmpty() || !trimmedArgs.contains(" /from ") || !trimmedArgs.contains(" /to ")) {
            throw new LunaException(FORMAT_MESSAGE_EVENT);
        }

        int fromPos = trimmedArgs.indexOf(" /from ");
        int toPos = trimmedArgs.indexOf(" /to ");
        if (fromPos >= toPos) {
            throw new LunaException(FORMAT_MESSAGE_EVENT);
        }

        String desc = trimmedArgs.substring(0, fromPos).trim();
        String from = trimmedArgs.substring(fromPos + " /from ".length(), toPos).trim();
        String to = trimmedArgs.substring(toPos + " /to ".length()).trim();
        if (desc.isEmpty() || from.isEmpty() || to.isEmpty()) {
            throw new LunaException(FORMAT_MESSAGE_EVENT);
        }

        return new String[] {validateDescription(desc), from, to};
    }

    /**
     * Returns trimmed command arguments, or an empty string if they are absent.
     *
     * @param commandArgs Command arguments to normalize.
     * @return Normalized command arguments.
     */
    private static String normalizeArgs(String commandArgs) {
        return commandArgs == null ? "" : commandArgs.trim();
    }

    /**
     * Validates that a task description can be stored safely.
     *
     * @param description Description to validate.
     * @return Validated description.
     * @throws LunaException If the description contains a reserved character.
     */
    private static String validateDescription(String description) throws LunaException {
        if (description.contains("|")) {
            throw new LunaException("Task descriptions cannot contain the character '|'.");
        }
        return description;
    }
}
