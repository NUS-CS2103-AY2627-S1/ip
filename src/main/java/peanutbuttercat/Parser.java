package peanutbuttercat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.time.temporal.TemporalAccessor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Interprets raw user input and validates command arguments.
 */
public class Parser {
    private static final DateTimeFormatter[] INPUT_FORMATS = {
        DateTimeFormatter.ofPattern("d/M/uuuu HHmm").withResolverStyle(ResolverStyle.STRICT),
        DateTimeFormatter.ofPattern("d/M/uuuu HH:mm").withResolverStyle(ResolverStyle.STRICT),
        DateTimeFormatter.ofPattern("uuuu-MM-dd HHmm").withResolverStyle(ResolverStyle.STRICT),
        DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm").withResolverStyle(ResolverStyle.STRICT),
        DateTimeFormatter.ofPattern("uuuu-MM-dd").withResolverStyle(ResolverStyle.STRICT)
    };

    /** Identifies the command represented by the input. */
    public CommandType parseCommandType(String input) {
        return CommandType.fromInput(input);
    }

    /** Extracts a non-empty description after a command word. */
    public String getDescription(String command, String commandWord) throws PeanutButterCatException {
        assert command != null : "Command to parse must not be null";
        assert commandWord != null && !commandWord.isBlank() : "Command word must not be blank";
        assert command.trim().startsWith(commandWord) : "Command must start with its command word";

        String description = normalizeWhitespace(command.substring(commandWord.length()));
        if (description.isEmpty()) {
            throw new PeanutButterCatException("Oops, this kitty needs a description for your "
                    + commandWord + "! Please add one after '" + commandWord + "'.");
        }
        return description;
    }

    /**
     * Verifies that a command does not contain arguments after its command word.
     *
     * @param command Full command entered by the user.
     * @param commandWord Command word that starts the input.
     * @throws PeanutButterCatException If an argument follows the command word.
     */
    public void validateNoArguments(String command, String commandWord) throws PeanutButterCatException {
        assert command != null : "Command to validate must not be null";
        assert commandWord != null && !commandWord.isBlank() : "Command word must not be blank";
        assert command.trim().startsWith(commandWord) : "Command must start with its command word";

        String arguments = command.substring(commandWord.length()).trim();
        if (!arguments.isEmpty()) {
            throw new PeanutButterCatException(
                    "No extra toppings needed for statistics! Use: stats");
        }
    }

    /**
     * Parses a deadline command into a deadline task.
     *
     * @param command Full deadline command entered by the user.
     * @return The parsed deadline task.
     * @throws PeanutButterCatException If the description or due time is missing or invalid.
     */
    public Deadline parseDeadline(String command) throws PeanutButterCatException {
        assert parseCommandType(command) == CommandType.DEADLINE
                : "Deadline parser must receive a deadline command";

        String details = command.substring(CommandType.DEADLINE.getCommandWord().length()).trim();
        int byIndex = findSingleParameter(details, "/by",
                "My whiskers can't find the deadline! Use: deadline DESCRIPTION /by TIME");
        String description = normalizeWhitespace(details.substring(0, byIndex));
        String by = normalizeWhitespace(details.substring(byIndex + "/by".length()));
        if (description.isEmpty()) {
            throw new PeanutButterCatException("Oops, this kitty needs a description for your deadline!");
        }
        if (by.isEmpty()) {
            throw new PeanutButterCatException("When is it due? Add a time after '/by', purr-lease!");
        }
        LocalDateTime dueTime = parseDateTime(by);
        return new Deadline(description, dueTime);
    }

    /**
     * Parses an event command into an event task.
     *
     * @param command Full event command entered by the user.
     * @return The parsed event task.
     * @throws PeanutButterCatException If the description or either event time is missing or invalid.
     */
    public Event parseEvent(String command) throws PeanutButterCatException {
        assert parseCommandType(command) == CommandType.EVENT
                : "Event parser must receive an event command";

        String details = command.substring(CommandType.EVENT.getCommandWord().length()).trim();
        String usageMessage = "My whiskers need the whole time trail! Use: "
                + "event DESCRIPTION /from START /to END";
        int fromIndex = findSingleParameter(details, "/from", usageMessage);
        int toIndex = findSingleParameter(details, "/to", usageMessage);
        if (fromIndex > toIndex) {
            throw new PeanutButterCatException(usageMessage);
        }
        String description = normalizeWhitespace(details.substring(0, fromIndex));
        String from = normalizeWhitespace(details.substring(fromIndex + "/from".length(), toIndex));
        String to = normalizeWhitespace(details.substring(toIndex + "/to".length()));
        if (description.isEmpty()) {
            throw new PeanutButterCatException("Oops, this kitty needs a description for your event!");
        }
        if (from.isEmpty() || to.isEmpty()) {
            throw new PeanutButterCatException("An event needs both start and end times - no missing paws!");
        }
        LocalDateTime startTime = parseDateTime(from);
        LocalDateTime endTime = parseDateTime(to);
        if (!endTime.isAfter(startTime)) {
            throw new PeanutButterCatException(
                    "An event must end after it starts. Please check the '/from' and '/to' times!");
        }
        return new Event(description, startTime, endTime);
    }

    /** Parses a supported date or date-time value. */
    public LocalDateTime parseDateTime(String value) throws PeanutButterCatException {
        String normalizedValue = normalizeWhitespace(value);
        for (DateTimeFormatter format : INPUT_FORMATS) {
            try {
                TemporalAccessor parsed = format.parse(normalizedValue);
                LocalDate date = LocalDate.from(parsed);
                return parsed.isSupported(java.time.temporal.ChronoField.HOUR_OF_DAY)
                        ? LocalDateTime.from(parsed) : date.atStartOfDay();
            } catch (DateTimeParseException ignored) {
                // Try the next accepted input format.
            }
        }
        throw new PeanutButterCatException("I couldn't understand that date. Use yyyy-MM-dd "
                + "or d/M/yyyy HHmm, purr-lease!");
    }

    /** Parses the date argument of an {@code on} command. */
    public LocalDate parseDate(String command) throws PeanutButterCatException {
        assert parseCommandType(command) == CommandType.ON : "Date parser must receive an on command";

        String value = command.substring(CommandType.ON.getCommandWord().length()).trim();
        if (value.isEmpty()) {
            throw new PeanutButterCatException("Which date should I search? Use: on yyyy-MM-dd");
        }
        try {
            return LocalDate.parse(normalizeWhitespace(value),
                    DateTimeFormatter.ISO_LOCAL_DATE.withResolverStyle(ResolverStyle.STRICT));
        } catch (DateTimeParseException exception) {
            throw new PeanutButterCatException("I couldn't understand that date. Use yyyy-MM-dd, purr-lease!");
        }
    }

    /** Converts a one-based task number into a zero-based list index. */
    public int parseTaskIndex(String command, String commandWord, int numberOfTasks)
            throws PeanutButterCatException {
        assert command != null : "Command to parse must not be null";
        assert commandWord != null && !commandWord.isBlank() : "Command word must not be blank";
        assert command.trim().startsWith(commandWord) : "Command must start with its command word";
        assert numberOfTasks >= 0 : "Task count must not be negative";

        String numberText = command.substring(commandWord.length()).trim();
        if (numberText.isEmpty()) {
            throw new PeanutButterCatException("Which task should I " + commandWord
                    + "? Give me its number, purr-lease!");
        }
        if (!numberText.matches("\\d+")) {
            throw new PeanutButterCatException("My paws can only count whole task numbers. "
                    + "Try '" + commandWord + " 1', for example!");
        }
        int taskNumber;
        try {
            taskNumber = Integer.parseInt(numberText);
        } catch (NumberFormatException exception) {
            throw new PeanutButterCatException("My paws can only count whole task numbers. "
                    + "Try '" + commandWord + " 1', for example!");
        }
        if (taskNumber < 1 || taskNumber > numberOfTasks) {
            throw new PeanutButterCatException("I can't find task " + taskNumber
                    + " in the task jar. Check 'list' and try again!");
        }
        int taskIndex = taskNumber - 1;
        assert taskIndex >= 0 && taskIndex < numberOfTasks : "Validated task index must be in range";
        return taskIndex;
    }

    private static int findSingleParameter(String details, String parameter, String missingMessage)
            throws PeanutButterCatException {
        Pattern parameterPattern = Pattern.compile("(?<!\\S)" + Pattern.quote(parameter) + "(?!\\S)");
        Matcher matcher = parameterPattern.matcher(details);
        if (!matcher.find()) {
            throw new PeanutButterCatException(missingMessage);
        }
        int parameterIndex = matcher.start();
        if (matcher.find()) {
            throw new PeanutButterCatException("Use '" + parameter + "' only once in each command, purr-lease!");
        }
        return parameterIndex;
    }

    private static String normalizeWhitespace(String value) {
        return value == null ? "" : value.trim().replaceAll("\\s+", " ");
    }
}
