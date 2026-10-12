package bogos;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Interprets user command text and converts it into application data.
 */
public class Parser {
    private static final String TODO_COMMAND_PREFIX = "todo ";
    private static final String DEADLINE_COMMAND_PREFIX = "deadline ";
    private static final String EVENT_COMMAND_PREFIX = "event ";
    private static final String TODO_BODY_ERROR = "Bwhere body? Be: todo DESCRIPTION [#TAG]...";
    private static final String DEADLINE_BODY_ERROR =
            "Bwhere body? Be: deadline DESCRIPTION /by YYYY-MM-DD [#TAG]...";
    private static final String EVENT_BODY_ERROR =
            "Bwhere body? Be: event DESCRIPTION /from YYYY-MM-DD /to YYYY-MM-DD [#TAG]...";
    private static final String DEADLINE_BY_ERROR =
            "Bwhere /by? Be: deadline DESCRIPTION /by YYYY-MM-DD [#TAG]...";
    private static final String EVENT_FROM_ERROR =
            "Bwhere /from? Be: event DESCRIPTION /from YYYY-MM-DD /to YYYY-MM-DD [#TAG]...";
    private static final String EVENT_TO_ERROR =
            "Bwhere /to? Be: event DESCRIPTION /from YYYY-MM-DD /to YYYY-MM-DD [#TAG]...";
    private static final String FOREIGN_OBJECT_ERROR_PREFIX = "Bah! Unidentified Foreign Object: ";

    /**
     * Returns whether a command creates a task.
     *
     * @param command Command to classify.
     * @return Whether the command is a to-do, deadline, or event command.
     */
    public boolean isTaskCommand(String command) {
        return command.equals("todo")
                || command.startsWith(TODO_COMMAND_PREFIX)
                || command.equals("deadline")
                || command.startsWith(DEADLINE_COMMAND_PREFIX)
                || command.equals("event")
                || command.startsWith(EVENT_COMMAND_PREFIX);
    }

    /**
     * Creates the task described by a to-do, deadline, or event command.
     *
     * @param command Command describing the task.
     * @return Task described by the command.
     * @throws BogosException If the command is not a valid task command.
     */
    public Task parseTask(String command) throws BogosException {
        if (command.equals("todo")) {
            throw new BogosException(TODO_BODY_ERROR);
        }
        if (command.equals("deadline")) {
            throw new BogosException(DEADLINE_BODY_ERROR);
        }
        if (command.equals("event")) {
            throw new BogosException(EVENT_BODY_ERROR);
        }

        ParsedTaskInput parsedInput = extractTags(command);
        String commandWithoutTags = parsedInput.commandWithoutTags();
        List<String> tags = parsedInput.tags();

        if (commandWithoutTags.equals("todo") || commandWithoutTags.startsWith(TODO_COMMAND_PREFIX)) {
            return new Todo(getRequiredTodoDescription(commandWithoutTags.substring("todo".length())), tags);
        }
        if (commandWithoutTags.equals("deadline")) {
            throw new BogosException(DEADLINE_BODY_ERROR);
        }
        if (commandWithoutTags.startsWith(DEADLINE_COMMAND_PREFIX)) {
            return parseDeadline(commandWithoutTags, tags);
        }
        if (commandWithoutTags.equals("event")) {
            throw new BogosException(EVENT_BODY_ERROR);
        }
        if (commandWithoutTags.startsWith(EVENT_COMMAND_PREFIX)) {
            return parseEvent(commandWithoutTags, tags);
        }
        throw new BogosException("Bwhat? Best browse: help");
    }

    /**
     * Parses a one-based task number from user input.
     *
     * @param taskNumberText Text containing the task number.
     * @return Parsed task number.
     * @throws BogosException If the text is not a whole number.
     */
    public int parseTaskNumber(String taskNumberText) throws BogosException {
        try {
            return Integer.parseInt(taskNumberText);
        } catch (NumberFormatException e) {
            throw new BogosException("Bogus. Bring Bogos base-ten. :[");
        }
    }

    /**
     * Parses a deadline command into a deadline task.
     *
     * @param command Deadline command to parse.
     * @param tags Tags assigned to the deadline.
     * @return Deadline task described by the command.
     * @throws BogosException If the command is invalid.
     */
    private Task parseDeadline(String command, List<String> tags) throws BogosException {
        assert command.startsWith("deadline ")
                : "Deadline parsing is only reached for deadline commands.";
        DateParameters parameters = extractDateParameters(command, DEADLINE_COMMAND_PREFIX, true);
        if (parameters.byDate() == null) {
            throw new BogosException(DEADLINE_BY_ERROR);
        }

        return new Deadline(parameters.description(), parameters.byDate(), tags);
    }

    /**
     * Parses an event command into an event task.
     *
     * @param command Event command to parse.
     * @param tags Tags assigned to the event.
     * @return Event task described by the command.
     * @throws BogosException If the command is invalid or its dates are reversed.
     */
    private Task parseEvent(String command, List<String> tags) throws BogosException {
        assert command.startsWith("event ")
                : "Event parsing is only reached for event commands.";
        DateParameters parameters = extractDateParameters(command, EVENT_COMMAND_PREFIX, false);
        if (parameters.fromDate() == null) {
            throw new BogosException(EVENT_FROM_ERROR);
        }
        if (parameters.toDate() == null) {
            throw new BogosException(EVENT_TO_ERROR);
        }

        try {
            return new Event(parameters.description(), parameters.fromDate(), parameters.toDate(), tags);
        } catch (IllegalArgumentException e) {
            throw new BogosException("Bro be breathing backwards??");
        }
    }

    /**
     * Separates a task description from its date parameters and validates their marker names.
     *
     * @param command Task command containing a description and optional date parameters.
     * @param commandPrefix Prefix identifying the task type.
     * @param isDeadline Whether the command is a deadline rather than an event.
     * @return Description and date values indexed by their markers.
     * @throws BogosException If the description or parameter syntax is invalid.
     */
    private DateParameters extractDateParameters(String command, String commandPrefix, boolean isDeadline)
            throws BogosException {
        String[] tokens = command.substring(commandPrefix.length()).trim().split("\\s+");
        List<String> descriptionTokens = new ArrayList<>();
        boolean hasDateParameter = false;
        LocalDate byDate = null;
        LocalDate fromDate = null;
        LocalDate toDate = null;

        for (int index = 0; index < tokens.length; index++) {
            String token = tokens[index];
            if (!token.startsWith("/")) {
                if (hasDateParameter) {
                    throw new BogosException(FOREIGN_OBJECT_ERROR_PREFIX + token);
                }
                descriptionTokens.add(token);
                continue;
            }

            if (descriptionTokens.isEmpty()) {
                throw new BogosException(isDeadline ? DEADLINE_BODY_ERROR : EVENT_BODY_ERROR);
            }
            hasDateParameter = true;
            if (isDeadline && !token.equals("/by")) {
                throw new BogosException(getDateParameterUsageError(true));
            }
            if (!isDeadline && !token.equals("/from") && !token.equals("/to")) {
                throw new BogosException(getDateParameterUsageError(isDeadline));
            }
            if (index + 1 == tokens.length || tokens[index + 1].startsWith("/")) {
                throw new BogosException(getMissingDateParameterError(token));
            }

            LocalDate date = parseDate(tokens[++index]);
            switch (token) {
            case "/by" -> {
                if (byDate != null) {
                    throw new BogosException("Bummer, buplicate /by. :[");
                }
                byDate = date;
            }
            case "/from" -> {
                if (fromDate != null) {
                    throw new BogosException("Bummer, buplicate /from. :[");
                }
                fromDate = date;
            }
            case "/to" -> {
                if (toDate != null) {
                    throw new BogosException("Bummer, buplicate /to. :[");
                }
                toDate = date;
            }
            default -> throw new IllegalStateException("Validated date parameter was not handled.");
            }
        }

        String description = String.join(" ", descriptionTokens);
        if (description.isBlank()) {
            throw new BogosException(isDeadline ? DEADLINE_BODY_ERROR : EVENT_BODY_ERROR);
        }
        return new DateParameters(description, byDate, fromDate, toDate);
    }

    /**
     * Returns a command-specific usage error for an unrecognised date marker.
     *
     * @param isDeadline Whether the command is a deadline rather than an event.
     * @return Usage error for the task command.
     */
    private String getDateParameterUsageError(boolean isDeadline) {
        return isDeadline ? DEADLINE_BY_ERROR : EVENT_FROM_ERROR;
    }

    /**
     * Returns the appropriate error for a date marker without a following date.
     *
     * @param parameter Date marker missing its value.
     * @return Error describing the missing marker value.
     */
    private String getMissingDateParameterError(String parameter) {
        return switch (parameter) {
        case "/by" -> "Bogus blank /by. :[";
        case "/from" -> "Bogus blank /from. :[";
        case "/to" -> "Bogus blank /to. :[";
        default -> throw new IllegalArgumentException("Unknown date parameter: " + parameter);
        };
    }

    /**
     * Returns a non-blank to-do description or its command-specific usage error.
     *
     * @param descriptionText Text to validate and trim.
     * @return Trimmed non-blank to-do description.
     * @throws BogosException If the description is blank.
     */
    private String getRequiredTodoDescription(String descriptionText) throws BogosException {
        String trimmedDescription = descriptionText.trim();
        if (trimmedDescription.isBlank()) {
            throw new BogosException(TODO_BODY_ERROR);
        }
        return trimmedDescription;
    }

    /**
     * Parses an ISO-8601 date and converts failures to a user-facing error.
     *
     * @param dateText Date text to parse.
     * @return Parsed date.
     * @throws BogosException If the date text is invalid.
     */
    private LocalDate parseDate(String dateText) throws BogosException {
        try {
            return LocalDate.parse(dateText);
        } catch (DateTimeParseException e) {
            throw new BogosException("Bogus. Bring Bogos bona-fide YYYY-MM-DD. :[");
        }
    }

    /**
     * Separates inline tag tokens from a task command.
     *
     * @param command Raw task command.
     * @return Task command without tags and tags in their original order.
     * @throws BogosException If a tag is blank, duplicated, or followed by non-tag text.
     */
    private ParsedTaskInput extractTags(String command) throws BogosException {
        String[] tokens = command.split("\\s+");
        List<String> commandTokens = new ArrayList<>();
        List<String> tags = new ArrayList<>();
        Set<String> uniqueTags = new HashSet<>();
        boolean hasTags = false;

        for (String token : tokens) {
            if (!token.startsWith("#")) {
                if (hasTags) {
                    throw new BogosException(FOREIGN_OBJECT_ERROR_PREFIX + token);
                }
                commandTokens.add(token);
                continue;
            }

            hasTags = true;
            String tag = token.substring(1);
            if (tag.isEmpty()) {
                throw new BogosException("Bogus blank #badge. :[");
            }
            if (!uniqueTags.add(tag)) {
                throw new BogosException("Bummer, buplicate #badge. :[");
            }
            tags.add(tag);
        }
        String commandWithoutTags = hasTags ? String.join(" ", commandTokens) : command;
        return new ParsedTaskInput(commandWithoutTags, tags);
    }

    /**
     * Holds a task command after tag extraction.
     *
     * @param commandWithoutTags Task command with inline tags removed.
     * @param tags Tags extracted from the command.
     */
    private record ParsedTaskInput(String commandWithoutTags, List<String> tags) {
    }

    /**
     * Holds a task description and parsed date values extracted from its parameter markers.
     *
     * @param description Task description before the first date marker.
     * @param byDate Parsed due date supplied by {@code /by}, if present.
     * @param fromDate Parsed start date supplied by {@code /from}, if present.
     * @param toDate Parsed end date supplied by {@code /to}, if present.
     */
    private record DateParameters(String description, LocalDate byDate, LocalDate fromDate, LocalDate toDate) {
    }
}
