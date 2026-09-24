package dobby.parser;

import dobby.command.Command;
import dobby.command.DeadlineCommand;
import dobby.command.DeleteCommand;
import dobby.command.EventCommand;
import dobby.command.ExitCommand;
import dobby.command.FindCommand;
import dobby.command.HelpCommand;
import dobby.command.InvalidCommand;
import dobby.command.ListCommand;
import dobby.command.MarkCommand;
import dobby.command.TodoCommand;
import dobby.command.UnmarkCommand;
import dobby.exception.DobbyException;
import dobby.util.DateTimeUtil;

/** Converts every user input into an executable command object. */
public final class Parser {
    private Parser() {
    }

    /** Parses one complete user input line. */
    public static Command parse(String input) {
        assert input != null : "Parser input must come from a successful UI read";

        String trimmedInput = input.trim();
        if (trimmedInput.isEmpty()) {
            return invalid("> Dobby couldn't hear you. Dobby want you to speak louder!");
        }
        if (trimmedInput.equalsIgnoreCase("list")) {
            return new ListCommand();
        }
        if (trimmedInput.equalsIgnoreCase("help")) {
            return new HelpCommand();
        }
        if (input.equalsIgnoreCase("bye")) {
            return new ExitCommand();
        }
        String[] tokens = trimmedInput.split("\\s+"); // '\s+' matches 1 or more whitespace(s)
        return switch (tokens[0].toLowerCase()) {
            case "list" -> invalid("> Dobby is confused. Dobby think you meant 'list'");
            case "help" -> invalid("> Dobby is confused. Dobby think you meant 'help'");
            case "todo" -> tokens.length > 1 ? new TodoCommand(join(tokens, 1, tokens.length))
                    : invalid("> Dobby is confused. Dobby think you meant 'todo <description>'");
            case "deadline" -> deadline(tokens);
            case "event" -> event(tokens);
            case "find" -> tokens.length > 1 ? new FindCommand(join(tokens, 1, tokens.length))
                    : invalid("> Dobby is confused. Dobby think you meant 'find <keyword>'");
            case "mark", "unmark", "delete" -> numbered(tokens);
            default -> invalid("> Dobby asks is this a Todo, Deadline, or Event?\n"
                    + "> Dobby recommend whisper 'help' so that Dobby can help you!");
        };
    }

    /**
     * Creates a command that operates on one numbered task.
     *
     * @param tokens command input split into tokens
     * @return the requested command, or an invalid command when the number is invalid
     */
    private static Command numbered(String[] tokens) {
        if (tokens.length != 2) {
            return invalid("> Dobby is confused. Dobby think you meant '" + tokens[0] + " <Task number>'");
        }
        try {
            int taskNumber = parseTaskNumber(tokens[1]);
            return switch (tokens[0].toLowerCase()) {
                case "mark" -> new MarkCommand(taskNumber);
                case "unmark" -> new UnmarkCommand(taskNumber);
                case "delete" -> new DeleteCommand(taskNumber);
                default -> throw new IllegalArgumentException("Unsupported numbered command: " + tokens[0]);
            };
        } catch (DobbyException e) {
            return invalid(e.getMessage());
        }
    }

    /**
     * Creates a deadline command from tokenized user input.
     *
     * @param tokens command input split into tokens
     * @return a deadline command, or an invalid command when the input is malformed
     */
    private static Command deadline(String[] tokens) {
        int byIndex = marker(tokens, "/by", 1);
        if (byIndex == -1 || byIndex == 1 || byIndex == tokens.length - 1) {
            return invalid("> Dobby is confused. Dobby think you meant 'deadline <description> /by <date/time>'");
        }
        try {
            return new DeadlineCommand(join(tokens, 1, byIndex),
                    DateTimeUtil.parse(join(tokens, byIndex + 1, tokens.length)));
        } catch (DobbyException e) {
            return invalid("> Dobby needs a valid date: yyyy-MM-dd, optionally followed by HHmm.");
        }
    }

    /**
     * Creates an event command from tokenized user input.
     *
     * @param tokens command input split into tokens
     * @return an event command, or an invalid command when the input is malformed
     */
    private static Command event(String[] tokens) {
        int fromIndex = marker(tokens, "/from", 1);
        int toIndex = marker(tokens, "/to", fromIndex + 1);
        if (fromIndex == -1 || toIndex == -1 || fromIndex == 1
                || fromIndex + 1 == toIndex || toIndex == tokens.length - 1) {
            return invalid("> Dobby is confused. Dobby think you meant "
                    + "'event <description> /from <date/time> /to <date/time>'");
        }
        try {
            DateTimeUtil.ParsedDateTime from = DateTimeUtil.parse(join(tokens, fromIndex + 1, toIndex));
            DateTimeUtil.ParsedDateTime to = DateTimeUtil.parse(join(tokens, toIndex + 1, tokens.length));
            if (to.getValue().isBefore(from.getValue())) {
                return invalid("> Dobby needs the event end to be at or after its start.");
            }
            return new EventCommand(join(tokens, 1, fromIndex), from, to);
        } catch (DobbyException e) {
            return invalid("> Dobby needs valid dates: yyyy-MM-dd, optionally followed by HHmm.");
        }
    }

    private static InvalidCommand invalid(String message) {
        return new InvalidCommand(message);
    }

    /** Converts task-number text into an integer used by a Dobby command. */
    private static int parseTaskNumber(String text) throws DobbyException {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            throw new DobbyException("Dobby is confused. Dobby expected a task number", e);
        }
    }

    /**
     * Finds the first case-insensitive marker token at or after a given index.
     *
     * @param tokens tokens to search
     * @param marker marker text to find
     * @param start first index to search
     * @return the marker index, or {@code -1} when it is absent
     */
    private static int marker(String[] tokens, String marker, int start) {
        assert tokens != null : "Tokens must be created before searching for a marker";
        assert marker != null : "Marker text must be specified";
        assert start >= 0 && start <= tokens.length : "Marker search start must be within the token array";

        for (int index = start; index < tokens.length; index++) {
            if (marker.equalsIgnoreCase(tokens[index])) {
                return index;
            }
        }
        return -1;
    }

    /**
     * Joins a range of tokens with single spaces.
     *
     * @param tokens tokens to join
     * @param start inclusive index of the first token
     * @param end exclusive index after the last token
     * @return the joined tokens
     */
    private static String join(String[] tokens, int start, int end) {
        assert tokens != null : "Tokens must be created before joining them";
        assert start >= 0 && start <= end && end <= tokens.length
                : "Joined token range must be within the token array";

        String[] selectedTokens = new String[end - start];
        System.arraycopy(tokens, start, selectedTokens, 0, selectedTokens.length);
        return String.join(" ", selectedTokens);
    }
}
