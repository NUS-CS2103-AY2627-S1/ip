package am.command;

import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import am.task.DeadlineTask;
import am.task.EventTask;
import am.task.TaskEdit;
import am.task.TodoTask;

/** Converts textual user commands into typed command objects. */
public class CommandParser {
    private static final Pattern MARKER = Pattern.compile("(?<!\\S)/\\S*");

    /** Creates a command parser. */
    public CommandParser() {
    }

    /**
     * Parses a complete user command.
     *
     * @param input command text entered by the user
     * @return the corresponding command object, or an empty command for blank input
     * @throws InvalidCommandException if the command arguments are malformed
     * @throws UnknownCommandException if the command name is not supported
     */
    public static Command parse(String input) {
        if (input.isBlank()) {
            return new Command.EmptyCommand();
        }
        String[] parts = input.strip().split("\\s+", 2);
        String commandType = parts[0];
        String argument = parts.length > 1 ? parts[1] : "";
        return switch (commandType) {
            case "bye" -> requireNoArgument(argument, commandType, new Command.ByeCommand());
            case "list" -> requireNoArgument(argument, commandType, new Command.ListCommand());
            case "past" -> requireNoArgument(argument, commandType, new Command.PastCommand());
            case "find" -> parseFindCommand(argument);
            case "edit" -> parseEditCommand(argument);
            case "mark" -> new Command.MarkCommand(parseTaskNumber(argument, commandType) - 1);
            case "unmark" -> new Command.UnmarkCommand(parseTaskNumber(argument, commandType) - 1);
            case "todo" -> parseTodoCommand(argument);
            case "deadline" -> parseDeadlineCommand(argument);
            case "event" -> parseEventCommand(argument);
            case "delete" -> new Command.DeleteTaskCommand(parseTaskNumber(argument, commandType) - 1);
            default -> throw new UnknownCommandException(String.format(
                    "AAAAHHHHHHHHHHHHHHH\nYou can't tell me to '%s'", input));
        };
    }

    /** Parses each edit marker once, preserving whitespace inside descriptions. */
    private static Command parseEditCommand(String argument) {
        String[] parts = argument.strip().split("\\s+", 2);
        if (parts.length != 2 || argument.contains("\n") || argument.contains("\r")) {
            throw InvalidCommandException.forSyntax("edit");
        }
        int taskNumber = parseTaskNumber(parts[0], "edit");
        Map<String, String> fields = parseFields(parts[1], Set.of("/name", "/by", "/from", "/to"), "edit");
        if (fields.containsKey("/name")) {
            validateDescription(fields.get("/name"), "edit");
        }
        return new Command.EditCommand(taskNumber, new TaskEdit(fields));
    }

    /** Returns a command after confirming that it has no arguments. */
    private static Command requireNoArgument(String argument, String commandType, Command command) {
        if (!argument.isBlank()) {
            throw InvalidCommandException.forSyntax(commandType);
        }
        return command;
    }

    /** Parses a find command and validates its keyword. */
    private static Command parseFindCommand(String argument) {
        String keyword = argument.strip();
        if (keyword.isEmpty()) {
            throw InvalidCommandException.forSyntax("find");
        }
        return new Command.FindCommand(keyword);
    }

    /** Parses a todo command and validates its task name. */
    private static Command parseTodoCommand(String argument) {
        String name = argument.strip();
        validateDescription(name, "todo");
        return new Command.AddTaskCommand(new TodoTask(name));
    }

    /** Parses a deadline command and converts its date into a task. */
    private static Command parseDeadlineCommand(String argument) {
        String[] taskParts = splitTaskArguments(argument, "deadline");
        Map<String, String> fields = parseFields(taskParts[1], Set.of("/by"), "deadline");
        String by = fields.get("/by").replaceAll("\\s+", " ");
        try {
            return new Command.AddTaskCommand(new DeadlineTask(taskParts[0], by));
        } catch (DateTimeParseException exception) {
            throw InvalidCommandException.forSyntax("deadline");
        }
    }

    /** Parses an event command and converts its dates into a task. */
    private static Command parseEventCommand(String argument) {
        String[] taskParts = splitTaskArguments(argument, "event");
        Set<String> required = Set.of("/from", "/to");
        Map<String, String> fields = parseFields(taskParts[1], required, "event");
        if (!fields.keySet().containsAll(required)) {
            throw InvalidCommandException.forSyntax("event");
        }
        String from = fields.get("/from").replaceAll("\\s+", " ");
        String to = fields.get("/to").replaceAll("\\s+", " ");
        try {
            return new Command.AddTaskCommand(new EventTask(taskParts[0], from, to));
        } catch (DateTimeParseException exception) {
            throw InvalidCommandException.forSyntax("event");
        }
    }

    /** Splits a task name from the parameter section without changing its internal spacing. */
    private static String[] splitTaskArguments(String argument, String commandType) {
        Matcher markers = MARKER.matcher(argument);
        int parameterStart = markers.find() ? markers.start() : argument.length();
        String name = argument.substring(0, parameterStart).strip();
        validateDescription(name, commandType);
        return new String[]{name, argument.substring(parameterStart)};
    }

    /** Validates a positive user task number before any zero-based conversion. */
    private static int parseTaskNumber(String argument, String commandType) {
        try {
            int taskNumber = Integer.parseInt(argument.strip());
            if (taskNumber > 0) {
                return taskNumber;
            }
        } catch (NumberFormatException exception) {
            // Missing, non-integer, and overflowing numbers share the same syntax response.
        }
        throw InvalidCommandException.forSyntax(commandType);
    }

    /** Extracts unique supported markers and nonblank values for creation and editing. */
    private static Map<String, String> parseFields(String parameters, Set<String> allowed, String commandType) {
        Matcher markers = MARKER.matcher(parameters);
        Map<String, String> fields = new HashMap<>();
        String previous = null;
        int valueStart = 0;
        while (markers.find()) {
            if (previous == null && !parameters.substring(0, markers.start()).isBlank()) {
                throw InvalidCommandException.forSyntax(commandType);
            }
            if (previous != null) {
                fields.put(previous, parameters.substring(valueStart, markers.start()).strip());
            }
            String marker = markers.group();
            if (!allowed.contains(marker) || fields.containsKey(marker)) {
                throw InvalidCommandException.forSyntax(commandType);
            }
            previous = marker;
            valueStart = markers.end();
        }
        if (previous == null) {
            throw InvalidCommandException.forSyntax(commandType);
        }
        fields.put(previous, parameters.substring(valueStart).strip());
        if (fields.values().stream().anyMatch(String::isBlank)) {
            throw InvalidCommandException.forSyntax(commandType);
        }
        return fields;
    }

    /** Rejects descriptions that cannot be represented safely in the saved-file format. */
    private static void validateDescription(String name, String commandType) {
        if (name.isBlank() || name.contains("|") || name.contains("\r") || name.contains("\n")) {
            throw InvalidCommandException.forSyntax(commandType);
        }
    }
}
