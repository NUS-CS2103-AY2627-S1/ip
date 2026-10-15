package edith.parser;

import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edith.command.AddCommand;
import edith.command.Command;
import edith.command.DeleteCommand;
import edith.command.ExitCommand;
import edith.command.FindCommand;
import edith.command.HelpCommand;
import edith.command.ListCommand;
import edith.command.MarkCommand;
import edith.command.TagCommand;
import edith.command.UnmarkCommand;
import edith.command.UntagCommand;
import edith.exception.EdithException;
import edith.task.Deadline;
import edith.task.Event;
import edith.task.Task;
import edith.task.Todo;
import edith.util.DateFormatter;

/** Interprets user commands and creates the tasks described by them. */
public class Parser {
    private static final String DEADLINE_USAGE = "deadline DESCRIPTION /by yyyy-MM-dd [HHmm]";
    private static final String EVENT_USAGE = "event DESCRIPTION /from yyyy-MM-dd [HHmm] "
            + "/to yyyy-MM-dd [HHmm]";
    private static final String DEADLINE_DATE_MARKER = "/by";
    private static final String EVENT_START_MARKER = "/from";
    private static final String EVENT_END_MARKER = "/to";
    private static final Pattern TAG_MARKER_PATTERN = Pattern.compile("(?<!\\S)/tags(?=\\s|$)");
    private static final String INVALID_TAG_MESSAGE = "Tags must start with # and contain only letters, "
            + "digits, underscores, or hyphens.";

    /** Separates a task's existing command details from optional trailing tags. */
    private record TaskDetails(String text, List<String> tags) {
    }

    /** Holds a full-list task number and validated tags for a tag-changing command. */
    private record TagArguments(int taskNumber, List<String> tags) {
    }

    /**
     * Parses complete user input into an executable command.
     *
     * @param command the complete user input
     * @return the command represented by the input
     * @throws EdithException if the command or its arguments are invalid
     */
    public static Command parse(String command) throws EdithException {
        if (command == null) {
            throw new EdithException("Please enter a command.");
        }
        command = command.trim();
        CommandType commandType = CommandType.fromInput(command);
        if (commandType == null) {
            throw new EdithException("I don't know what that means. Type 'help' to list all commands.");
        }

        return switch (commandType) {
            case HELP -> new HelpCommand();
            case BYE -> new ExitCommand();
            case LIST -> new ListCommand();
            case FIND -> new FindCommand(parseFindKeyword(command));
            case MARK -> new MarkCommand(parseTaskNumber(command, commandType));
            case UNMARK -> new UnmarkCommand(parseTaskNumber(command, commandType));
            case DELETE -> new DeleteCommand(parseTaskNumber(command, commandType));
            case TAG -> parseTagCommand(command);
            case UNTAG -> parseUntagCommand(command);
            case TODO -> new AddCommand(parseTodo(command));
            case DEADLINE -> new AddCommand(parseDeadline(command));
            case EVENT -> new AddCommand(parseEvent(command));
        };
    }

    /** Parses and validates the search text supplied to a find command. */
    private static String parseFindKeyword(String command) throws EdithException {
        String keyword = command.substring(CommandType.FIND.getKeyword().length()).trim();
        if (keyword.isEmpty()) {
            throw new EdithException("Please provide a word or phrase to find. Use: find KEYWORD");
        }
        return keyword;
    }

    /** Parses a todo command into a task. */
    private static Task parseTodo(String command) throws EdithException {
        TaskDetails details = parseTaskDetails(command.substring(CommandType.TODO.getKeyword().length()).trim());
        String description = details.text();
        if (description.isEmpty()) {
            throw new EdithException("The description of a todo cannot be empty.");
        }
        return addTags(new Todo(description), details.tags());
    }

    /** Parses a deadline command into a task. */
    private static Task parseDeadline(String command) throws EdithException {
        TaskDetails taggedDetails = parseTaskDetails(
                command.substring(CommandType.DEADLINE.getKeyword().length()).trim());
        String details = taggedDetails.text();
        int byMarker = details.indexOf(DEADLINE_DATE_MARKER);
        if (byMarker < 0) {
            throw new EdithException("A deadline needs a description and due date. Use: " + DEADLINE_USAGE);
        }

        String description = details.substring(0, byMarker).trim();
        String by = details.substring(byMarker + DEADLINE_DATE_MARKER.length()).trim();
        if (!isStandaloneMarker(details, byMarker, DEADLINE_DATE_MARKER)
                || details.indexOf(DEADLINE_DATE_MARKER, byMarker + DEADLINE_DATE_MARKER.length()) >= 0) {
            throw new EdithException("A deadline needs exactly one /by marker. Use: " + DEADLINE_USAGE);
        }
        if (description.isEmpty()) {
            throw new EdithException("The description of a deadline cannot be empty. Use: " + DEADLINE_USAGE);
        }
        if (by.isEmpty()) {
            throw new EdithException("A deadline needs a due date after /by. Use: " + DEADLINE_USAGE);
        }
        DateFormatter.ParsedDateTime dueDateTime = parseDateTime(by, "deadline");
        return addTags(new Deadline(description, dueDateTime.value(), dueDateTime.hasTime()), taggedDetails.tags());
    }

    /** Parses an event command into a task. */
    private static Task parseEvent(String command) throws EdithException {
        TaskDetails taggedDetails = parseTaskDetails(
                command.substring(CommandType.EVENT.getKeyword().length()).trim());
        String details = taggedDetails.text();
        int fromMarker = details.indexOf(EVENT_START_MARKER);
        int toMarker = details.indexOf(EVENT_END_MARKER);
        if (fromMarker < 0 || toMarker < 0 || toMarker < fromMarker) {
            throw new EdithException("An event needs a description, start date, and end date. Use: " + EVENT_USAGE);
        }
        if (!isStandaloneMarker(details, fromMarker, EVENT_START_MARKER)
                || !isStandaloneMarker(details, toMarker, EVENT_END_MARKER)
                || details.indexOf(EVENT_START_MARKER, fromMarker + EVENT_START_MARKER.length()) >= 0
                || details.indexOf(EVENT_END_MARKER, toMarker + EVENT_END_MARKER.length()) >= 0) {
            throw new EdithException("An event needs exactly one /from and one /to marker. Use: " + EVENT_USAGE);
        }

        String description = details.substring(0, fromMarker).trim();
        String from = details.substring(fromMarker + EVENT_START_MARKER.length(), toMarker).trim();
        String to = details.substring(toMarker + EVENT_END_MARKER.length()).trim();
        if (description.isEmpty()) {
            throw new EdithException("The description of an event cannot be empty. Use: " + EVENT_USAGE);
        }
        if (from.isEmpty()) {
            throw new EdithException("An event needs a start date after /from. Use: " + EVENT_USAGE);
        }
        if (to.isEmpty()) {
            throw new EdithException("An event needs an end date after /to. Use: " + EVENT_USAGE);
        }
        DateFormatter.ParsedDateTime startDateTime = parseDateTime(from, "event start");
        DateFormatter.ParsedDateTime endDateTime = parseDateTime(to, "event end");
        if (!startDateTime.value().isBefore(endDateTime.value())) {
            throw new EdithException("The event start must be before its end.");
        }
        return addTags(new Event(description, startDateTime.value(), startDateTime.hasTime(),
                endDateTime.value(), endDateTime.hasTime()), taggedDetails.tags());
    }

    /** Extracts tags only when the standalone /tags marker appears in a creation command. */
    private static TaskDetails parseTaskDetails(String details) throws EdithException {
        Matcher marker = TAG_MARKER_PATTERN.matcher(details);
        if (!marker.find()) {
            return new TaskDetails(details, List.of());
        }
        if (marker.find()) {
            throw new EdithException("Please specify /tags only once.");
        }
        marker.reset();
        marker.find();

        String taskText = details.substring(0, marker.start()).trim();
        String tagText = details.substring(marker.end()).trim();
        if (tagText.isEmpty()) {
            throw new EdithException("Please provide at least one tag after /tags.");
        }
        List<String> tags = Arrays.asList(tagText.split("\\s+"));
        for (String tag : tags) {
            validateTag(tag);
        }
        return new TaskDetails(taskText, tags);
    }

    /** Returns whether a marker is separated from its description and value by whitespace. */
    private static boolean isStandaloneMarker(String text, int index, String marker) {
        return (index == 0 || Character.isWhitespace(text.charAt(index - 1)))
                && (index + marker.length() == text.length()
                        || Character.isWhitespace(text.charAt(index + marker.length())));
    }

    /** Parses a command that adds tags after validating its complete argument list. */
    private static Command parseTagCommand(String command) throws EdithException {
        TagArguments arguments = parseTagArguments(command, CommandType.TAG);
        return new TagCommand(arguments.taskNumber(), arguments.tags());
    }

    /** Parses a command that removes tags after validating its complete argument list. */
    private static Command parseUntagCommand(String command) throws EdithException {
        TagArguments arguments = parseTagArguments(command, CommandType.UNTAG);
        return new UntagCommand(arguments.taskNumber(), arguments.tags());
    }

    /** Parses a full-list task number followed by one or more valid tags. */
    private static TagArguments parseTagArguments(String command, CommandType commandType) throws EdithException {
        String keyword = commandType.getKeyword();
        String arguments = command.substring(keyword.length()).trim();
        String[] parts = arguments.isEmpty() ? new String[0] : arguments.split("\\s+");
        String usage = keyword + " NUMBER #tag [#tag ...]";
        if (parts.length == 0) {
            throw new EdithException("Please provide a whole-number task number. Use: " + usage);
        }
        int taskNumber;
        try {
            taskNumber = Integer.parseInt(parts[0]);
        } catch (NumberFormatException e) {
            throw new EdithException("Please provide a whole-number task number. Use: " + usage);
        }
        if (parts.length == 1) {
            throw new EdithException("Please provide at least one tag. Use: " + usage);
        }

        List<String> tags = Arrays.asList(parts).subList(1, parts.length);
        for (String tag : tags) {
            validateTag(tag);
        }
        return new TagArguments(taskNumber, List.copyOf(tags));
    }

    /** Gives the shared user-facing error for an invalid tag token. */
    private static void validateTag(String tag) throws EdithException {
        if (!Task.isValidTag(tag)) {
            throw new EdithException(INVALID_TAG_MESSAGE);
        }
    }

    /** Adds already-validated tags to a fully parsed task. */
    private static Task addTags(Task task, List<String> tags) {
        for (String tag : tags) {
            task.addTag(tag);
        }
        return task;
    }

    /** Parses the one-based task number supplied to a mark, unmark, or delete command. */
    private static int parseTaskNumber(String command, CommandType commandType) throws EdithException {
        String commandWord = commandType.getKeyword();
        String taskNumberText = command.substring(commandWord.length()).trim();
        try {
            return Integer.parseInt(taskNumberText);
        } catch (NumberFormatException e) {
            throw new EdithException("Please provide a whole-number task number. Use: " + commandWord + " NUMBER");
        }
    }

    /** Parses a command date-time and gives the user a corrective message when it is invalid. */
    private static DateFormatter.ParsedDateTime parseDateTime(String dateText, String dateRole) throws EdithException {
        try {
            return DateFormatter.parseInput(dateText);
        } catch (DateTimeParseException e) {
            throw new EdithException("The " + dateRole
                    + " date must use yyyy-MM-dd, optionally followed by a time in the format HHmm.");
        }
    }
}
