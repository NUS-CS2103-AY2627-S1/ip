package anders.parser;

import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import anders.AndersException;
import anders.command.AddCommand;
import anders.command.Command;
import anders.command.DeleteCommand;
import anders.command.ExitCommand;
import anders.command.FindCommand;
import anders.command.ListCommand;
import anders.command.MarkCommand;
import anders.command.TagCommand;
import anders.task.Deadline;
import anders.task.Event;
import anders.task.Task;
import anders.task.Todo;

/** Validates the command formats accepted by Anders. */
public class Parser {
    private static final Pattern DEADLINE_PATTERN = Pattern.compile("(.+?)\\s+/by\\s+(.+)");
    private static final Pattern EVENT_PATTERN = Pattern.compile("(.+?)\\s+/from\\s+(.+?)\\s+/to\\s+(.+)");
    private static final Pattern TAG_CLAUSE_PATTERN = Pattern.compile("^(?:(.*?)\\s+)?/tags(?:\\s+(.*))?$");
    private static final String TAG_ERROR_MESSAGE =
            "Each tag must start with # and contain only letters, digits, hyphens, or underscores.";

    /** Converts a validated command string into an executable command object. */
    public static Command parse(String command) throws AndersException {
        Parser parser = new Parser();
        parser.validate(command);
        String word = parser.commandWord(command);
        String args = parser.arguments(command);
        switch (word) {
            case "bye":
                return new ExitCommand();
            case "list":
                return new ListCommand();
            case "mark":
                return new MarkCommand(args, true);
            case "unmark":
                return new MarkCommand(args, false);
            case "delete":
                return new DeleteCommand(args);
            case "find":
                return new FindCommand(args);
            case "tag":
                return new TagCommand(args, true);
            case "untag":
                return new TagCommand(args, false);
            case "todo":
            case "deadline":
            case "event":
                return new AddCommand(parser.parseTask(command));
            default:
                assert false : "Validation must reject unsupported command words";
                throw new AndersException("I don't know what that means. Please try a supported command.");
        }
    }
    /** Returns the command keyword, separated from its arguments. */
    public String commandWord(String command) {
        String trimmedCommand = command.trim();
        int separator = firstWhitespaceIndex(trimmedCommand);
        return separator < 0 ? trimmedCommand : trimmedCommand.substring(0, separator);
    }

    /** Returns the text following the command keyword. */
    public String arguments(String command) {
        String trimmedCommand = command.trim();
        int separator = firstWhitespaceIndex(trimmedCommand);
        return separator < 0 ? "" : trimmedCommand.substring(separator).trim();
    }

    /** Builds a task from a validated task-creation command. */
    public Task parseTask(String command) {
        String details = arguments(command);
        CreationParts parts = parseCreationParts(details);
        switch (commandWord(command)) {
            case "todo":
                return createTaggedTask(new Todo(parts.taskDetails()), parts.tags());
            case "deadline": {
                Matcher deadlineMatcher = DEADLINE_PATTERN.matcher(parts.taskDetails());
                if (!deadlineMatcher.matches()) {
                    throw new IllegalArgumentException("Invalid deadline format");
                }
                return createTaggedTask(new Deadline(deadlineMatcher.group(1).trim(), deadlineMatcher.group(2).trim()),
                        parts.tags());
            }
            case "event": {
                Matcher eventMatcher = EVENT_PATTERN.matcher(parts.taskDetails());
                if (!eventMatcher.matches()) {
                    throw new IllegalArgumentException("Invalid event format");
                }
                return createTaggedTask(new Event(eventMatcher.group(1).trim(), eventMatcher.group(2).trim(),
                        eventMatcher.group(3).trim()), parts.tags());
            }
            default:
                assert false : "parseTask must only receive a task-creation command";
                throw new IllegalArgumentException("Command does not create a task");
        }
    }

    /** Validates that a command is non-empty, well-formed, and supported. */
    public void validate(String command) throws AndersException {
        String trimmedCommand = command.trim();
        if (trimmedCommand.isEmpty()) {
            throw new AndersException("I don't know what that means. Please enter a command.");
        }
        String word = commandWord(trimmedCommand);
        String args = arguments(trimmedCommand);
        switch (word) {
            case "todo":
                validateTodo(args);
                break;
            case "deadline":
                validateDeadline(trimmedCommand, args);
                break;
            case "event":
                validateEvent(trimmedCommand, args);
                break;
            case "mark":
                validateRequiredArgument(args, "Mark", "a task number");
                break;
            case "unmark":
                validateRequiredArgument(args, "Unmark", "a task number");
                break;
            case "delete":
                validateRequiredArgument(args, "Delete", "a task number");
                break;
            case "find":
                validateFind(args);
                break;
            case "tag":
                validateTagCommand(args, "Tag");
                break;
            case "untag":
                validateTagCommand(args, "Untag");
                break;
            case "bye":
            case "list":
                validateNoArguments(args);
                break;
            default:
                throw new AndersException("I don't know what that means. Please try a supported command.");
        }
    }

    private void validateTodo(String args) throws AndersException {
        CreationParts parts = getCreationParts(args);
        if (parts.taskDetails().isEmpty()) {
            throw new AndersException("The description of a todo cannot be empty. Please include a description!");
        }
    }

    private void validateDeadline(String command, String args) throws AndersException {
        CreationParts parts = getCreationParts(args);
        if (!isValidDeadline(parts.taskDetails())) {
            throw new AndersException("A deadline needs a description and a /by value.");
        }
        try {
            parseTask(command);
        } catch (DateTimeParseException e) {
            throw new AndersException("A deadline needs a real date in d/M/yyyy or yyyy-MM-dd format,"
                    + " optionally followed by HHmm. yyyy-MM-dd also accepts HH:mm.");
        }
    }

    private void validateEvent(String command, String args) throws AndersException {
        CreationParts parts = getCreationParts(args);
        if (!isValidEvent(parts.taskDetails())) {
            throw new AndersException("An event needs a description, /from value, and /to value.");
        }
        try {
            parseTask(command);
        } catch (DateTimeParseException e) {
            throw new AndersException("Event dates must be real dates in yyyy-MM-dd or d/M/yyyy,"
                    + " optionally followed by HHmm.");
        } catch (IllegalArgumentException e) {
            throw new AndersException(e.getMessage());
        }
    }

    private void validateRequiredArgument(String args, String commandName, String argumentDescription)
            throws AndersException {
        if (args.isEmpty()) {
            throw new AndersException(commandName + " needs " + argumentDescription + ".");
        }
    }

    private void validateFind(String args) throws AndersException {
        validateRequiredArgument(args, "Find", "a keyword");
        if (args.startsWith("#") && !Task.isValidTag(args)) {
            throw new AndersException(TAG_ERROR_MESSAGE);
        }
    }

    private void validateTagCommand(String args, String commandName) throws AndersException {
        if (args.isEmpty()) {
            throw new AndersException(commandName + " needs a task number and at least one tag.");
        }
        String[] fields = args.split("\\s+");
        if (fields.length < 2) {
            throw new AndersException(commandName + " needs a task number and at least one tag.");
        }
        for (int i = 1; i < fields.length; i++) {
            if (!Task.isValidTag(fields[i])) {
                throw new AndersException(TAG_ERROR_MESSAGE);
            }
        }
    }

    private void validateNoArguments(String args) throws AndersException {
        if (!args.isEmpty()) {
            throw new AndersException("I don't know what that means. Please try a supported command.");
        }
    }

    private boolean isValidDeadline(String details) {
        Matcher deadlineMatcher = DEADLINE_PATTERN.matcher(details);
        return deadlineMatcher.matches()
                && !deadlineMatcher.group(1).trim().isEmpty()
                && !deadlineMatcher.group(2).trim().isEmpty();
    }

    /** Checks that an event contains non-empty {@code /from} and {@code /to} values. */
    private boolean isValidEvent(String details) {
        Matcher eventMatcher = EVENT_PATTERN.matcher(details);
        return eventMatcher.matches()
                && !eventMatcher.group(1).trim().isEmpty()
                && !eventMatcher.group(2).trim().isEmpty()
                && !eventMatcher.group(3).trim().isEmpty();
    }

    private static int firstWhitespaceIndex(String command) {
        for (int i = 0; i < command.length(); i++) {
            if (Character.isWhitespace(command.charAt(i))) {
                return i;
            }
        }
        return -1;
    }

    private static CreationParts parseCreationParts(String details) {
        Matcher tagMatcher = TAG_CLAUSE_PATTERN.matcher(details);
        if (!tagMatcher.matches()) {
            return new CreationParts(details, List.of());
        }

        String taskDetails = tagMatcher.group(1) == null ? "" : tagMatcher.group(1).trim();
        String tagText = tagMatcher.group(2) == null ? "" : tagMatcher.group(2).trim();
        if (tagText.isEmpty()) {
            throw new IllegalArgumentException("The /tags clause must contain at least one tag.");
        }
        return new CreationParts(taskDetails, parseTags(tagText));
    }

    private static List<String> parseTags(String tagText) {
        List<String> tags = new ArrayList<>();
        for (String tag : tagText.split("\\s+")) {
            if (!Task.isValidTag(tag)) {
                throw new IllegalArgumentException(TAG_ERROR_MESSAGE);
            }
            tags.add(Task.normalizeTag(tag));
        }
        return List.copyOf(tags);
    }

    private static CreationParts getCreationParts(String details) throws AndersException {
        try {
            return parseCreationParts(details);
        } catch (IllegalArgumentException exception) {
            throw new AndersException(exception.getMessage());
        }
    }

    private static Task createTaggedTask(Task task, List<String> tags) {
        task.addTags(tags);
        return task;
    }

    private record CreationParts(String taskDetails, List<String> tags) {
    }
}
