package duke.parser;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import duke.command.ByeCommand;
import duke.command.Command;
import duke.command.DeadlineCommand;
import duke.command.DeleteCommand;
import duke.command.EventCommand;
import duke.command.FindCommand;
import duke.command.ListCommand;
import duke.command.MarkCommand;
import duke.command.TodoCommand;
import duke.command.UndoCommand;
import duke.command.UnknownCommand;
import duke.exception.GaryException;

/**
 * Parses user input lines into {@link duke.command.Command} objects.
 *
 * <p>This class is responsible for:
 * <ul>
 *   <li>Identifying the command keyword (the first token).</li>
 *   <li>Extracting the argument string (the remainder of the line).</li>
 *   <li>Constructing the corresponding {@code Command} object.</li>
 * </ul>
 *
 * <p>This class validates command structure and reserved parameter markers. Semantic validation, such as checking
 * whether a date exists, remains with the individual {@code Command} implementations.</p>
 */
public class Parser {
    private static final int MAX_COMMAND_LENGTH = 500;
    private static final Pattern BY_MARKER_PATTERN = Pattern.compile("(?i)(?<!\\S)/by(?!\\S)");
    private static final Pattern DEADLINE_PATTERN = Pattern.compile(
            "^(.+?)\\s+/by\\s+(\\S+)\\s*$", Pattern.CASE_INSENSITIVE);
    private static final Pattern FROM_MARKER_PATTERN = Pattern.compile("(?i)(?<!\\S)/from(?!\\S)");
    private static final Pattern TO_MARKER_PATTERN = Pattern.compile("(?i)(?<!\\S)/to(?!\\S)");
    private static final Pattern EVENT_PATTERN = Pattern.compile(
            "^(.+?)\\s+/from\\s+(\\S+)\\s+/to\\s+(\\S+)\\s*$", Pattern.CASE_INSENSITIVE);

    /**
     * Parses a full user input line and returns a {@link Command} representing that input.
     *
     * @param userInput Full user input line.
     * @return A {@code Command} instance; returns {@link UnknownCommand} if the command keyword is not recognised.
     * @throws GaryException If the input is blank or a known command has malformed syntax.
     */
    public Command parse(String userInput) {
        if (userInput == null || userInput.isBlank()) {
            throw new GaryException("Please enter a command.");
        }
        if (userInput.strip().length() > MAX_COMMAND_LENGTH) {
            throw new GaryException("Commands cannot exceed " + MAX_COMMAND_LENGTH + " characters.");
        }

        String[] userSplit = userInput.strip().split("\\s+", 2);
        String commandWord = userSplit[0].toUpperCase(Locale.ROOT);
        String arguments = userSplit.length > 1 ? userSplit[1].strip() : "";

        return switch (commandWord) {
            case "BYE" -> {
                requireNoArguments("bye", arguments);
                yield new ByeCommand();
            }
            case "LIST" -> {
                requireNoArguments("list", arguments);
                yield new ListCommand();
            }
            case "MARK" -> new MarkCommand(arguments, true);
            case "UNMARK" -> new MarkCommand(arguments, false);
            case "TODO" -> new TodoCommand(arguments);
            case "DEADLINE" -> parseDeadline(arguments);
            case "EVENT" -> parseEvent(arguments);
            case "DELETE" -> new DeleteCommand(arguments);
            case "FIND" -> new FindCommand(arguments);
            case "UNDO" -> new UndoCommand(arguments);
            default -> new UnknownCommand();
        };
    }

    /**
     * Parses the arguments for a {@code deadline} command.
     *
     * <p>Expected format: {@code <description> /by <YYYY-MM-DD>}</p>
     *
     * @param arguments The raw argument string after the command keyword.
     * @return A {@link DeadlineCommand} carrying the extracted description and due date strings.
     */
    private Command parseDeadline(String arguments) {
        Matcher matcher = DEADLINE_PATTERN.matcher(arguments);
        if (countMatches(BY_MARKER_PATTERN, arguments) != 1 || !matcher.matches()) {
            throw new GaryException("Use: deadline <description> /by <YYYY-MM-DD>.");
        }
        return new DeadlineCommand(matcher.group(1), matcher.group(2));
    }

    /**
     * Parses the arguments for an {@code event} command.
     *
     * <p>Expected format: {@code <description> /from <YYYY-MM-DD> /to <YYYY-MM-DD>}</p>
     *
     * @param arguments The raw argument string after the command keyword.
     * @return An {@link EventCommand} carrying the extracted description, start date string, and end date string.
     */
    private Command parseEvent(String arguments) {
        Matcher matcher = EVENT_PATTERN.matcher(arguments);
        if (countMatches(FROM_MARKER_PATTERN, arguments) != 1
                || countMatches(TO_MARKER_PATTERN, arguments) != 1
                || !matcher.matches()) {
            throw new GaryException("Use: event <description> /from <YYYY-MM-DD> /to <YYYY-MM-DD>.");
        }
        return new EventCommand(matcher.group(1), matcher.group(2), matcher.group(3));
    }

    private int countMatches(Pattern pattern, String input) {
        int count = 0;
        Matcher matcher = pattern.matcher(input);
        while (matcher.find()) {
            count++;
        }
        return count;
    }

    private void requireNoArguments(String commandWord, String arguments) {
        if (!arguments.isBlank()) {
            throw new GaryException("The " + commandWord + " command does not take any arguments.");
        }
    }
}
