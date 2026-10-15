package gunna;

import gunna.command.Command;
import gunna.command.DeadlineCommand;
import gunna.command.DeleteCommand;
import gunna.command.EventCommand;
import gunna.command.ExitCommand;
import gunna.command.FindCommand;
import gunna.command.InvalidCommand;
import gunna.command.ListCommand;
import gunna.command.MarkCommand;
import gunna.command.SearchCommand;
import gunna.command.SortCommand;
import gunna.command.TodoCommand;
import gunna.command.UnmarkCommand;

/**
 * Handles parsing of user commands.
 * Converts command strings into Command objects.
 */
public class Parser {
    private static final String DELIMITER_BY_WITH_SPACE = " /by ";
    private static final String DELIMITER_BY_NO_SPACE = " /by";
    private static final String DELIMITER_FROM = " /from ";
    private static final String DELIMITER_TO_WITH_SPACE = " /to ";
    private static final String DELIMITER_TO_NO_SPACE = " /to";

    /**
     * Parses a full command string and returns the appropriate Command object.
     *
     * @param fullCommand The complete command string from the user.
     * @return A Command object representing the user's intent.
     * @throws DukeException If the command format is invalid.
     */
    public static Command parse(String fullCommand) throws DukeException {
        assert fullCommand != null : "Command string cannot be null";
        if (fullCommand.equals("bye")) {
            return new ExitCommand();
        } else if (fullCommand.equals("list")) {
            return new ListCommand();
        } else if (fullCommand.equals("mark") || fullCommand.startsWith("mark ")) {
            return parseMarkCommand(fullCommand);
        } else if (fullCommand.equals("unmark") || fullCommand.startsWith("unmark ")) {
            return parseUnmarkCommand(fullCommand);
        } else if (fullCommand.equals("delete") || fullCommand.startsWith("delete ")) {
            return parseDeleteCommand(fullCommand);
        } else if (fullCommand.equals("todo") || fullCommand.startsWith("todo ")) {
            return parseTodoCommand(fullCommand);
        } else if (fullCommand.equals("deadline") || fullCommand.startsWith("deadline ")) {
            return parseDeadlineCommand(fullCommand);
        } else if (fullCommand.equals("event") || fullCommand.startsWith("event ")) {
            return parseEventCommand(fullCommand);
        } else if (fullCommand.equals("find") || fullCommand.startsWith("find ")) {
            return parseSearchCommand(fullCommand);
        } else if (fullCommand.equals("on") || fullCommand.startsWith("on ")) {
            return parseFindCommand(fullCommand);
        } else if (fullCommand.equals("sort") || fullCommand.startsWith("sort ")) {
            return parseSortCommand(fullCommand);
        } else {
            return new InvalidCommand();
        }
    }

    /**
     * Extracts the argument after a command word.
     * For example, "mark 5" with commandWord "mark" returns " 5".
     * Note: Leading/trailing spaces are preserved for parsing purposes.
     *
     * @param command The full command string.
     * @param commandWord The command word (e.g., "mark", "todo").
     * @return The argument string after the command word (may have leading spaces).
     */
    private static String getArgument(String command, String commandWord) {
        int prefixLength = commandWord.length();
        if (command.length() > prefixLength) {
            return command.substring(prefixLength);
        }
        return "";
    }

    /**
     * Parses a task number from a command argument.
     * Converts from 1-based user input to 0-based array index.
     * Note: Does not validate bounds - that's done by the Command.
     *
     * @param argument The argument containing the task number.
     * @return The task index (0-based, may be negative if user entered < 1).
     * @throws NumberFormatException if the argument is not a valid number.
     */
    private static int parseTaskNumber(String argument) throws NumberFormatException {
        int taskNumber = Integer.parseInt(argument.trim());
        return taskNumber - 1; // Convert to 0-based index
    }

    /**
     * Parses a deadline command to extract description and deadline time.
     * Format: deadline DESCRIPTION /by TIME
     *
     * @param command The full deadline command.
     * @return A String array [description, by], or null if format is invalid.
     */
    private static String[] parseDeadline(String command) {
        String remaining = getArgument(command, "deadline");

        // Find the /by delimiter
        int byIndex = remaining.indexOf(DELIMITER_BY_WITH_SPACE);
        boolean hasTrailingSpace = true;

        // Also check for /by at the end without trailing space
        if (byIndex == -1 && remaining.endsWith(DELIMITER_BY_NO_SPACE)) {
            byIndex = remaining.lastIndexOf(DELIMITER_BY_NO_SPACE);
            hasTrailingSpace = false;
        }

        // If no /by found, return null to indicate invalid format
        if (byIndex == -1) {
            return null;
        }

        String description = remaining.substring(0, byIndex).trim();
        String by;
        if (hasTrailingSpace) {
            by = remaining.substring(byIndex + DELIMITER_BY_WITH_SPACE.length()).trim();
        } else {
            by = remaining.substring(byIndex + DELIMITER_BY_NO_SPACE.length()).trim();
        }

        return new String[]{description, by};
    }

    /**
     * Parses an event command to extract description, from time, and to time.
     * Format: event DESCRIPTION /from TIME /to TIME
     *
     * @param command The full event command.
     * @return A String array [description, from, to], or null if format is invalid.
     */
    private static String[] parseEvent(String command) {
        String remaining = getArgument(command, "event");

        int fromIndex = remaining.indexOf(DELIMITER_FROM);
        int toIndex = remaining.indexOf(DELIMITER_TO_WITH_SPACE);

        // Check for /to without trailing space at the end
        if (toIndex == -1 && remaining.contains(DELIMITER_TO_NO_SPACE)) {
            toIndex = remaining.lastIndexOf(DELIMITER_TO_NO_SPACE);
        }

        // Validate that both delimiters exist and are in the correct order
        if (fromIndex == -1 || toIndex == -1 || fromIndex + DELIMITER_FROM.length() > toIndex) {
            return null;
        }

        String description = remaining.substring(0, fromIndex).trim();
        String from = remaining.substring(fromIndex + DELIMITER_FROM.length(), toIndex).trim();
        String to;

        // Handle both " /to " and " /to" (at end without trailing space)
        if (remaining.indexOf(DELIMITER_TO_WITH_SPACE) != -1) {
            to = remaining.substring(toIndex + DELIMITER_TO_WITH_SPACE.length()).trim();
        } else {
            to = remaining.substring(toIndex + DELIMITER_TO_NO_SPACE.length()).trim();
        }

        return new String[]{description, from, to};
    }

    /**
     * Extracts the date string from an "on" command.
     * Format: on DATE
     *
     * @param command The full on command.
     * @return The date string (trimmed).
     */
    private static String parseDate(String command) {
        return getArgument(command, "on").trim();
    }

    /**
     * Parses a mark command.
     *
     * @param fullCommand The full mark command string.
     * @return A MarkCommand object.
     * @throws DukeException If the argument is missing or invalid.
     */
    private static Command parseMarkCommand(String fullCommand) throws DukeException {
        String argument = getArgument(fullCommand, "mark");
        if (argument.trim().isEmpty()) {
            throw new DukeException("Task number required.\n     Usage: mark <task number>");
        }
        try {
            int taskIndex = parseTaskNumber(argument);
            return new MarkCommand(taskIndex);
        } catch (NumberFormatException e) {
            throw new DukeException("Task number must be a whole number.");
        }
    }

    /**
     * Parses an unmark command.
     *
     * @param fullCommand The full unmark command string.
     * @return An UnmarkCommand object.
     * @throws DukeException If the argument is missing or invalid.
     */
    private static Command parseUnmarkCommand(String fullCommand) throws DukeException {
        String argument = getArgument(fullCommand, "unmark");
        if (argument.trim().isEmpty()) {
            throw new DukeException("Task number required.\n     Usage: unmark <task number>");
        }
        try {
            int taskIndex = parseTaskNumber(argument);
            return new UnmarkCommand(taskIndex);
        } catch (NumberFormatException e) {
            throw new DukeException("Task number must be a whole number.");
        }
    }

    /**
     * Parses a delete command.
     *
     * @param fullCommand The full delete command string.
     * @return A DeleteCommand object.
     * @throws DukeException If the argument is missing or invalid.
     */
    private static Command parseDeleteCommand(String fullCommand) throws DukeException {
        String argument = getArgument(fullCommand, "delete");
        if (argument.trim().isEmpty()) {
            throw new DukeException("Task number required.\n     Usage: delete <task number>");
        }
        try {
            int taskIndex = parseTaskNumber(argument);
            return new DeleteCommand(taskIndex);
        } catch (NumberFormatException e) {
            throw new DukeException("Task number must be a whole number.");
        }
    }

    /**
     * Parses a todo command.
     *
     * @param fullCommand The full todo command string.
     * @return A TodoCommand object.
     */
    private static Command parseTodoCommand(String fullCommand) {
        String description = parseTodo(fullCommand);
        return new TodoCommand(description);
    }

    /**
     * Parses a deadline command.
     *
     * @param fullCommand The full deadline command string.
     * @return A DeadlineCommand object.
     * @throws DukeException If the command format is invalid.
     */
    private static Command parseDeadlineCommand(String fullCommand) throws DukeException {
        String[] parts = parseDeadline(fullCommand);
        if (parts == null) {
            throw new DukeException("Format check.\n     Usage: deadline <description> /by <time>");
        }
        return new DeadlineCommand(parts[0], parts[1]);
    }

    /**
     * Parses an event command.
     *
     * @param fullCommand The full event command string.
     * @return An EventCommand object.
     * @throws DukeException If the command format is invalid.
     */
    private static Command parseEventCommand(String fullCommand) throws DukeException {
        String[] parts = parseEvent(fullCommand);
        if (parts == null) {
            throw new DukeException("Format check.\n     Usage: event <description> /from <time> /to <time>");
        }
        return new EventCommand(parts[0], parts[1], parts[2]);
    }

    /**
     * Parses a search command (find command).
     *
     * @param fullCommand The full find command string.
     * @return A SearchCommand object.
     */
    private static Command parseSearchCommand(String fullCommand) {
        String keyword = getArgument(fullCommand, "find").trim();
        return new SearchCommand(keyword);
    }

    /**
     * Parses a find command (on command).
     *
     * @param fullCommand The full on command string.
     * @return A FindCommand object.
     */
    private static Command parseFindCommand(String fullCommand) {
        String dateStr = parseDate(fullCommand);
        return new FindCommand(dateStr);
    }

    /**
     * Parses a sort command.
     *
     * @param fullCommand The full sort command string.
     * @return A SortCommand object.
     * @throws DukeException If the criterion is missing or invalid.
     */
    private static Command parseSortCommand(String fullCommand) throws DukeException {
        String criterion = getArgument(fullCommand, "sort").trim();
        if (criterion.isEmpty()) {
            throw new DukeException("Sort criterion required.\n"
                    + "     Usage: sort status / sort description / sort date");
        }
        String normalizedCriterion = criterion.toLowerCase();
        if (!normalizedCriterion.equals("status")
                && !normalizedCriterion.equals("description")
                && !normalizedCriterion.equals("date")) {
            throw new DukeException("Invalid sort criterion.\n"
                    + "     Usage: sort status / sort description / sort date");
        }
        return new SortCommand(normalizedCriterion);
    }

    /**
     * Extracts the description from a todo command.
     * Format: todo DESCRIPTION
     *
     * @param command The full todo command.
     * @return The todo description (trimmed).
     */
    private static String parseTodo(String command) {
        return getArgument(command, "todo").trim();
    }
}
