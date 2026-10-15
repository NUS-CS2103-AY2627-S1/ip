package clover.parser;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import clover.command.AddTutoreeCommand;
import clover.command.Command;
import clover.command.DeadlineCommand;
import clover.command.DeleteCommand;
import clover.command.EventCommand;
import clover.command.ExitCommand;
import clover.command.FindCommand;
import clover.command.FindTutoreeCommand;
import clover.command.HelpCommand;
import clover.command.ListCommand;
import clover.command.ListTutoreesCommand;
import clover.command.MarkCommand;
import clover.command.ToDoCommand;
import clover.command.UnmarkCommand;
import clover.exception.CloverException;

/**
 * Converts raw user input into executable Clover commands.
 */
public class Parser {
    private static final String UNKNOWN_COMMAND_MESSAGE = "That command is not a forest path I know. "
            + "Type \"help\" to view the supported commands.";
    private static final Pattern FOR_MARKER_PATTERN = Pattern.compile("(?<!\\S)/for(?:\\s|$)");
    private static final Pattern PARAMETER_PATTERN = Pattern.compile("(?<!\\S)/(\\p{Alpha}+)(?=\\s|$)");

    /**
     * Creates the command represented by one complete line of user input.
     */
    public static Command parse(String input) throws CloverException {
        if (input.isBlank()) {
            throw new CloverException("The grove needs a command or a quest description.");
        }

        String[] parts = input.trim().split("\\s+", 2);
        String commandWord = parts[0].toLowerCase();
        String arguments = parts.length == 2 ? parts[1] : "";

        return switch (commandWord) {
            case "list" -> {
                requireNoArguments(commandWord, arguments);
                yield new ListCommand();
            }
            case "mark" -> new MarkCommand(arguments);
            case "unmark" -> new UnmarkCommand(arguments);
            case "todo" -> new ToDoCommand(arguments);
            case "deadline" -> new DeadlineCommand(arguments);
            case "event" -> new EventCommand(arguments);
            case "delete" -> new DeleteCommand(arguments);
            case "find" -> new FindCommand(arguments);
            case "add-tutoree" -> new AddTutoreeCommand(arguments);
            case "list-tutorees" -> {
                requireNoArguments(commandWord, arguments);
                yield new ListTutoreesCommand();
            }
            case "find-tutoree" -> new FindTutoreeCommand(arguments);
            case "help" -> {
                requireNoArguments(commandWord, arguments);
                yield new HelpCommand();
            }
            case "bye" -> {
                requireNoArguments(commandWord, arguments);
                yield new ExitCommand();
            }
            default -> throw new CloverException(UNKNOWN_COMMAND_MESSAGE);
        };
    }

    /**
     * Returns whether the input is a valid one-based task number in the current list.
     */
    public static boolean isValidTaskNumber(String input, int taskCount) {
        try {
            int taskNumber = Integer.parseInt(input.trim());
            return taskNumber >= 1 && taskNumber <= taskCount;
        } catch (NumberFormatException exception) {
            return false;
        }
    }

    /**
     * Converts a valid one-based task number to its zero-based list index.
     */
    public static int parseTaskIndex(String input) {
        return Integer.parseInt(input.trim()) - 1;
    }

    /**
     * Finds one whitespace-delimited marker, or returns {@code -1} when it is absent or repeated.
     */
    public static int findSingleMarker(String arguments, String marker) {
        Pattern markerPattern = Pattern.compile("(?<!\\S)" + Pattern.quote(marker) + "(?=\\s|$)");
        Matcher matcher = markerPattern.matcher(arguments);
        if (!matcher.find()) {
            return -1;
        }
        int markerIndex = matcher.start();
        return matcher.find() ? -1 : markerIndex;
    }

    /** Rejects slash-prefixed parameters that are not valid for the current command. */
    public static void rejectUnknownParameters(String arguments, String... allowedParameters) throws CloverException {
        Matcher matcher = PARAMETER_PATTERN.matcher(arguments);
        while (matcher.find()) {
            String parameter = "/" + matcher.group(1);
            for (String allowedParameter : allowedParameters) {
                if (parameter.equals(allowedParameter)) {
                    parameter = null;
                    break;
                }
            }
            if (parameter != null) {
                throw new CloverException("Unknown parameter \"" + parameter + "\" for this command.");
            }
        }
    }

    /**
     * Parses a date entered by the user in ISO yyyy-MM-dd format.
     */
    public static LocalDate parseDate(String text) throws CloverException {
        if (!text.matches("\\d{4}-\\d{2}-\\d{2}")) {
            throw new CloverException("Enter a date in yyyy-MM-dd format, for example 2026-02-28.");
        }
        try {
            return LocalDate.parse(text);
        } catch (DateTimeParseException exception) {
            throw new CloverException("\"" + text + "\" is not a real calendar date. Please check the month and day.");
        }
    }

    /**
     * Separates the optional final {@code /for TUTOREE NAME} marker from task arguments.
     */
    public static TaskArguments parseTaskArguments(String arguments) throws CloverException {
        Matcher matcher = FOR_MARKER_PATTERN.matcher(arguments);
        if (!matcher.find()) {
            return new TaskArguments(arguments, null);
        }
        int markerIndex = matcher.start();
        String taskArguments = arguments.substring(0, markerIndex).trim();
        String tutoreeName = arguments.substring(matcher.end()).trim();
        if (tutoreeName.isEmpty()) {
            throw new CloverException("Please add a learning companion name after /for.");
        }
        if (FOR_MARKER_PATTERN.matcher(tutoreeName).find()) {
            throw new CloverException("Use /for only once, at the end of the task command.");
        }
        return new TaskArguments(taskArguments, tutoreeName);
    }

    /** Rejects unexpected text supplied to a command that takes no arguments. */
    private static void requireNoArguments(String commandWord, String arguments) throws CloverException {
        if (!arguments.isBlank()) {
            throw new CloverException("The " + commandWord + " command does not take any additional text.");
        }
    }
}
