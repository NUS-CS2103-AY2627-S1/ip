package topaz.parser;

import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import topaz.TopazException;
import topaz.command.AddCommand;
import topaz.command.Command;
import topaz.command.DeleteCommand;
import topaz.command.ExitCommand;
import topaz.command.FindCommand;
import topaz.command.ListCommand;
import topaz.command.MarkCommand;
import topaz.command.UnmarkCommand;
import topaz.task.Deadline;
import topaz.task.Event;
import topaz.task.FixedDurationTask;
import topaz.task.Todo;
import topaz.util.DateTimeParser;

/**
 * Parses user commands and creates tasks from valid command arguments.
 */
public class Parser {
    private static final String DURATION_COMMAND = "duration";
    private static final Pattern PARAMETER_PATTERN = Pattern.compile("(?<!\\S)/\\S+");
    private static final Pattern DURATION_PATTERN = Pattern.compile("([1-9]\\d*)([hm])");
    private static final long MINUTES_PER_HOUR = 60;

    /**
     * Parses one complete user command into the command object that performs it.
     *
     * @param command the complete user command
     * @param taskCount the number of tasks currently in the list
     * @return the parsed command
     * @throws TopazException if the command or its arguments are invalid
     */
    public Command parse(String command, int taskCount) throws TopazException {
        command = normalizeCommand(command);
        if (isCommandWithArguments(command, "bye") && !command.equals("bye")) {
            throw new TopazException("Use: bye (without extra arguments).");
        }
        if (isCommandWithArguments(command, "list") && !command.equals("list")) {
            throw new TopazException("Use: list (without extra arguments).");
        }
        if (command.equals("bye")) {
            return new ExitCommand();
        } else if (command.equals("list")) {
            return new ListCommand();
        } else if (isCommandWithArguments(command, "find")) {
            return parseFind(command);
        } else if (isCommandWithArguments(command, "mark")) {
            return parseMarkCommand(command, taskCount);
        } else if (isCommandWithArguments(command, "unmark")) {
            return parseUnmarkCommand(command, taskCount);
        } else if (isCommandWithArguments(command, "delete")) {
            return parseDeleteCommand(command, taskCount);
        } else if (isCommandWithArguments(command, "todo")) {
            return parseTodo(command);
        } else if (isCommandWithArguments(command, "deadline")) {
            return parseDeadline(command);
        } else if (isCommandWithArguments(command, "event")) {
            return parseEvent(command);
        } else if (isCommandWithArguments(command, DURATION_COMMAND)) {
            return parseDuration(command);
        }
        throw new TopazException("I'm sorry, but I don't know what that means.");
    }

    /**
     * Accepts extra horizontal whitespace while rejecting pasted control characters.
     */
    private String normalizeCommand(String command) throws TopazException {
        if (command == null || command.isBlank()) {
            throw new TopazException("Please enter a command, such as list or todo <description>.");
        }
        if (command.codePoints().anyMatch(character -> (Character.isISOControl(character) && character != '\t')
                || character == '\u2028' || character == '\u2029')) {
            throw new TopazException("Enter one command on a single line without control characters.");
        }
        return command.replaceAll("\\h+", " ").strip();
    }

    /**
     * Returns whether the input is a command word, optionally followed by arguments.
     */
    private boolean isCommandWithArguments(String input, String commandWord) {
        return input.equals(commandWord) || input.startsWith(commandWord + " ");
    }

    /**
     * Parses a numbered command and returns its zero-based task index.
     */
    private int parseTaskNumber(String command, String action, int taskCount)
            throws TopazException {
        String numberText = command.substring(action.length()).trim();
        if (numberText.isEmpty()) {
            throw new TopazException("Please provide a task number after " + action + ".");
        }

        int taskNumber;
        try {
            if (!numberText.matches("-?[0-9]+")) {
                throw new NumberFormatException();
            }
            taskNumber = Integer.parseInt(numberText);
        } catch (NumberFormatException exception) {
            throw new TopazException("The task number must be an integer.");
        }

        if (taskNumber < 1 || taskNumber > taskCount) {
            throw new TopazException("That task number is not in your list.");
        }
        return taskNumber - 1;
    }

    /**
     * Parses a mark command into a mark command object.
     */
    private Command parseMarkCommand(String command, int taskCount) throws TopazException {
        return new MarkCommand(parseTaskNumber(command, "mark", taskCount));
    }

    /**
     * Parses an unmark command into an unmark command object.
     */
    private Command parseUnmarkCommand(String command, int taskCount) throws TopazException {
        return new UnmarkCommand(parseTaskNumber(command, "unmark", taskCount));
    }

    /**
     * Parses a delete command into a delete command object.
     */
    private Command parseDeleteCommand(String command, int taskCount) throws TopazException {
        return new DeleteCommand(parseTaskNumber(command, "delete", taskCount));
    }

    /**
     * Parses a todo command into an add command.
     */
    private Command parseTodo(String command) throws TopazException {
        String description = requireText(command.substring(4),
                "The description of a todo cannot be empty.");
        return new AddCommand(new Todo(description));
    }

    /**
     * Parses a find command into a search command.
     */
    private Command parseFind(String command) throws TopazException {
        String keyword = requireText(command.substring(4), "Please provide a keyword after find.");
        return new FindCommand(keyword);
    }

    /**
     * Parses a deadline command into an add command.
     */
    private Command parseDeadline(String command) throws TopazException {
        String[] fields = parseFields(command.substring(8).trim(),
                new String[] {"/by"}, "Use: deadline <description> /by <time>.");
        String description = requireText(fields[0], "The description of a deadline cannot be empty.");
        String by = requireText(fields[1], "The deadline time cannot be empty.");
        LocalDateTime byDateTime = DateTimeParser.parse(by,
                "Use a date as yyyy-MM-dd or d/M/yyyy HHmm.");
        return new AddCommand(new Deadline(description, byDateTime, DateTimeParser.hasTimeComponent(by)));
    }

    /**
     * Parses an event command into an add command.
     */
    private Command parseEvent(String command) throws TopazException {
        String[] fields = parseFields(command.substring(5).trim(),
                new String[] {"/from", "/to"}, "Use: event <description> /from <time> /to <time>.");
        String description = requireText(fields[0], "The description of an event cannot be empty.");
        String from = requireText(fields[1], "The event start time cannot be empty.");
        String to = requireText(fields[2], "The event end time cannot be empty.");
        LocalDateTime fromDateTime = DateTimeParser.parse(from,
                "Use a date as yyyy-MM-dd or d/M/yyyy HHmm.");
        LocalDateTime toDateTime = DateTimeParser.parse(to,
                "Use a date as yyyy-MM-dd or d/M/yyyy HHmm.");
        DateTimeParser.validateEventPeriod(fromDateTime, toDateTime);
        return new AddCommand(new Event(description, fromDateTime, toDateTime,
                DateTimeParser.hasTimeComponent(from), DateTimeParser.hasTimeComponent(to)));
    }

    /**
     * Parses a fixed-duration task command into an add command.
     */
    private Command parseDuration(String command) throws TopazException {
        String[] fields = parseFields(command.substring(DURATION_COMMAND.length()).trim(),
                new String[] {"/for"}, "Use: duration <description> /for <duration>.");
        String description = requireText(fields[0], "The description of a duration task cannot be empty.");
        String durationText = requireText(fields[1], "The duration cannot be empty.");
        return new AddCommand(new FixedDurationTask(description, parseDurationMinutes(durationText)));
    }

    /**
     * Splits named parameters, requiring exactly one of each in the documented order.
     * Slash-prefixed words are reserved as parameter markers in structured commands.
     */
    private String[] parseFields(String content, String[] markers, String usage) throws TopazException {
        String[] fields = new String[markers.length + 1];
        Matcher matcher = PARAMETER_PATTERN.matcher(content);
        int fieldIndex = 0;
        int fieldStart = 0;
        while (matcher.find()) {
            if (fieldIndex >= markers.length || !matcher.group().equals(markers[fieldIndex])) {
                throw new TopazException(usage);
            }
            fields[fieldIndex] = content.substring(fieldStart, matcher.start());
            fieldStart = matcher.end();
            fieldIndex++;
        }
        if (fieldIndex != markers.length) {
            throw new TopazException(usage);
        }
        fields[fieldIndex] = content.substring(fieldStart);
        return fields;
    }

    /**
     * Parses a positive whole-minute or whole-hour duration into minutes.
     */
    private long parseDurationMinutes(String durationText) throws TopazException {
        Matcher matcher = DURATION_PATTERN.matcher(durationText);
        if (!matcher.matches()) {
            throw invalidDurationException();
        }

        try {
            long amount = Long.parseLong(matcher.group(1));
            return matcher.group(2).equals("h") ? Math.multiplyExact(amount, MINUTES_PER_HOUR) : amount;
        } catch (NumberFormatException | ArithmeticException exception) {
            throw invalidDurationException();
        }
    }

    /**
     * Returns the error used when a duration does not use a supported format.
     */
    private TopazException invalidDurationException() {
        return new TopazException("Use a duration as a positive whole number followed by h or m.");
    }

    /**
     * Requires a non-empty text value that can be represented in the save file.
     */
    private String requireText(String text, String message) throws TopazException {
        String trimmedText = text.trim();
        if (trimmedText.isEmpty()) {
            throw new TopazException(message);
        }
        if (trimmedText.contains("|")) {
            throw new TopazException("Task details cannot contain the | character.");
        }
        return trimmedText;
    }

}
