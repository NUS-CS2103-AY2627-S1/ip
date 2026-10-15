package gooble.command;

import java.time.LocalDateTime;
import java.util.Locale;

import gooble.GoobleException;
import gooble.task.DeadlineDateParser;
import gooble.task.Task;

/**
 * Interprets the command word and arguments entered by the user.
 */
public class Parser {
    private static final String DEADLINE_COMMAND = "deadline";
    private static final String EVENT_COMMAND = "event";
    private static final String LIST_FROM_PREFIX = "list from ";
    private static final String DEADLINE_MARKER = " /by ";
    private static final String EVENT_START_MARKER = " /from ";
    private static final String EVENT_END_MARKER = " /to ";
    private static final String DATE_RANGE_SEPARATOR = " to ";

    /** Creates the command object corresponding to complete user input. */
    public Command parse(String command) {
        if (command == null || command.isBlank()) {
            return new UnknownCommand("");
        }
        CommandType type = parseType(command);
        if (type == CommandType.LIST && command.startsWith(LIST_FROM_PREFIX)) {
            return new ListFromCommand(command);
        }
        Command parsed = type.handler(command);
        return parsed == null ? new UnknownCommand(command) : parsed;
    }

    /**
     * Determines the command type from a complete input line.
     *
     * @param command complete user input
     * @return matching command type, or {@link CommandType#UNKNOWN}
     */
    public CommandType parseType(String command) {
        if (command == null || command.isBlank()) {
            return CommandType.UNKNOWN;
        }
        String commandWord = command.split("\\s+", 2)[0];
        return CommandType.fromString(commandWord.toLowerCase(Locale.ROOT));
    }

    /**
     * Extracts the trimmed text after a command word.
     *
     * @param command complete user input
     * @param commandWord command whose argument should be extracted
     * @return command argument, or an empty string when omitted
     */
    public String argumentAfter(String command, String commandWord) {
        if (command.length() <= commandWord.length()) {
            return "";
        }
        return command.substring(commandWord.length()).trim();
    }

    /**
     * Ensures that a command contains a non-empty description.
     *
     * @param description description extracted from a command
     * @throws GoobleException when the description is empty
     */
    public void validateDescription(String description) throws GoobleException {
        if (description.isEmpty()) {
            throw new GoobleException("Missing a description. Gooble needs something to put on the task list.");
        }
    }

    /** Parses a tag command into a one-based task number and one tag. */
    public String[] parseTag(String command) throws GoobleException {
        String details = argumentAfter(command, "tag");
        String[] parts = details.split("\\s+");
        if (parts.length < 2) {
            throw new GoobleException("Please provide a tag in the format #tag. Gooble cannot tag vibes alone.");
        }
        if (parts.length > 2) {
            throw new GoobleException("Please provide exactly one tag. Gooble's tag drawer is already wobbly.");
        }
        validateTag(parts[1]);
        return new String[] { parts[0], parts[1].toLowerCase(java.util.Locale.ROOT) };
    }

    /** Validates a user-provided tag. */
    public void validateTag(String tag) throws GoobleException {
        if (!tag.startsWith("#")) {
            throw new GoobleException("Please provide a tag in the format #tag. Gooble cannot tag vibes alone.");
        }
        if (!Task.isValidTag(tag)) {
            throw new GoobleException("Tags may contain only letters, numbers, hyphens, and underscores. "
                    + "Gooble rejects tag confetti.");
        }
    }

    /** Parses a deadline command into description and deadline text. */
    public String[] parseDeadline(String command) throws GoobleException {
        ensureCommandHasEnoughText(command, DEADLINE_COMMAND);
        String details = command.substring(DEADLINE_COMMAND.length()).trim();
        validateDescription(details);
        int markerIndex = details.indexOf(DEADLINE_MARKER);
        if (markerIndex == -1) {
            throw new GoobleException("Please specify a deadline using /by. Gooble needs a date to do calendar magic.");
        }
        if (details.indexOf(DEADLINE_MARKER, markerIndex + DEADLINE_MARKER.length()) >= 0) {
            throw new GoobleException("Please specify only one deadline using /by.");
        }
        String description = details.substring(0, markerIndex).trim();
        String deadline = details.substring(markerIndex + DEADLINE_MARKER.length()).trim();
        validateDescription(description);
        if (deadline.isEmpty()) {
            throw new GoobleException("Please specify a deadline using /by. Gooble needs a date to do calendar magic.");
        }
        return new String[] { description, deadline };
    }

    /** Parses an event command into description, start, and end text. */
    public String[] parseEvent(String command) throws GoobleException {
        ensureCommandHasEnoughText(command, EVENT_COMMAND);
        String details = command.substring(EVENT_COMMAND.length()).trim();
        validateDescription(details);
        validateEventMarkers(details);
        String[] eventDetails = extractEventDetails(details);
        validateEventDetails(eventDetails);
        validateEventOrder(eventDetails[1], eventDetails[2]);
        return eventDetails;
    }

    private void validateEventMarkers(String details) throws GoobleException {
        int startIndex = details.indexOf(EVENT_START_MARKER);
        int endIndex = details.indexOf(EVENT_END_MARKER);
        if (startIndex == -1 || endIndex == -1 || endIndex < startIndex) {
            throw new GoobleException("Please specify an event time using /from and /to. "
                    + "Gooble needs both ends of the event.");
        }
        if (details.indexOf(EVENT_START_MARKER, startIndex + EVENT_START_MARKER.length()) >= 0
                || details.indexOf(EVENT_END_MARKER, endIndex + EVENT_END_MARKER.length()) >= 0) {
            throw new GoobleException("Please specify each event time marker only once.");
        }
    }

    private String[] extractEventDetails(String details) {
        int startIndex = details.indexOf(EVENT_START_MARKER);
        int endIndex = details.indexOf(EVENT_END_MARKER);
        String description = details.substring(0, startIndex).trim();
        String start = details.substring(startIndex + EVENT_START_MARKER.length(), endIndex).trim();
        String end = details.substring(endIndex + EVENT_END_MARKER.length()).trim();
        return new String[] { description, start, end };
    }

    private void validateEventDetails(String[] eventDetails) throws GoobleException {
        String description = eventDetails[0];
        String start = eventDetails[1];
        String end = eventDetails[2];
        validateDescription(description);
        if (start.isEmpty() || end.isEmpty()) {
            throw new GoobleException("Please specify an event time using /from and /to. "
                    + "Gooble needs both ends of the event.");
        }
    }

    /** Rejects an event whose two supported date-time values are not chronological. */
    private void validateEventOrder(String start, String end) throws GoobleException {
        DeadlineDateParser.DeadlineDate startDate = DeadlineDateParser.parse(start);
        DeadlineDateParser.DeadlineDate endDate = DeadlineDateParser.parse(end);
        LocalDateTime startDateTime = LocalDateTime.of(startDate.date(),
                startDate.time() == null ? java.time.LocalTime.MIDNIGHT : startDate.time());
        LocalDateTime endDateTime = LocalDateTime.of(endDate.date(),
                endDate.time() == null ? java.time.LocalTime.MIDNIGHT : endDate.time());
        if (!endDateTime.isAfter(startDateTime)) {
            throw new GoobleException("Please ensure the event ends after it starts.");
        }
    }

    /** Ensures a command parser is not called with a truncated command. */
    private void ensureCommandHasEnoughText(String command, String commandWord) throws GoobleException {
        if (command == null || command.length() < commandWord.length()) {
            throw new GoobleException("Invalid command format. Please provide the required command details.");
        }
    }

    /** Parses and validates a list command's inclusive date-time range. */
    public DeadlineDateParser.DeadlineDate[] parseDateRange(String command) throws GoobleException {
        String range = extractDateRange(command);
        int separator = range.indexOf(DATE_RANGE_SEPARATOR);
        DeadlineDateParser.DeadlineDate from = DeadlineDateParser.parse(range.substring(0, separator).trim());
        DeadlineDateParser.DeadlineDate to = DeadlineDateParser.parse(
                range.substring(separator + DATE_RANGE_SEPARATOR.length()).trim());
        validateDateRange(from, to);
        return new DeadlineDateParser.DeadlineDate[] { from, to };
    }

    private String extractDateRange(String command) throws GoobleException {
        if (command == null || !command.startsWith(LIST_FROM_PREFIX)) {
            throw new GoobleException("Please use: list from yyyy-MM-dd HHmm to yyyy-MM-dd HHmm");
        }
        String range = command.substring(LIST_FROM_PREFIX.length()).trim();
        int separator = range.indexOf(DATE_RANGE_SEPARATOR);
        if (separator <= 0 || separator + DATE_RANGE_SEPARATOR.length() >= range.length()) {
            throw new GoobleException("Please use: list from yyyy-MM-dd HHmm to yyyy-MM-dd HHmm");
        }
        return range;
    }

    private void validateDateRange(DeadlineDateParser.DeadlineDate from,
                                   DeadlineDateParser.DeadlineDate to) throws GoobleException {
        if (from.time() == null || to.time() == null) {
            throw new GoobleException("Please include both dates and times, e.g. 2026-02-01 0900");
        }
        LocalDateTime fromDateTime = LocalDateTime.of(from.date(), from.time());
        LocalDateTime toDateTime = LocalDateTime.of(to.date(), to.time());
        if (toDateTime.isBefore(fromDateTime)) {
            throw new GoobleException("Please ensure the 'to' date and time is not before the 'from' date and time.");
        }
        assert !toDateTime.isBefore(fromDateTime);
    }
}
