package parser;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.regex.Pattern;

import exception.FriedbergUserInputException;

/**
 * Provides shared, strict validation and parsing for Friedberg commands.
 */
public final class ParserUtil {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("uuuu-MM-dd")
            .withResolverStyle(ResolverStyle.STRICT);
    private static final Pattern DATE_PATTERN = Pattern.compile("[0-9]{4}-[0-9]{2}-[0-9]{2}");
    private static final Pattern DESCRIPTION_PATTERN = Pattern.compile(
            "[\\p{L}\\p{N}][\\p{L}\\p{N} .'!?()&+\\-]*");

    private ParserUtil() {
        // Prevent instantiation.
    }

    /**
     * Parsed fields of a deadline command.
     *
     * @param description task description
     * @param byDate deadline date text
     */
    public record ParsedDeadline(String description, String byDate) {
    }

    /**
     * Parsed fields of an event command.
     *
     * @param description task description
     * @param fromDate start date text
     * @param toDate end date text
     */
    public record ParsedEvent(String description, String fromDate, String toDate) {
    }

    /**
     * Checks whether the first command token exactly matches a command word.
     *
     * @param userInput raw command input
     * @param commandWord expected command word
     * @return true if the first token exactly matches the command word
     */
    public static boolean hasCommandWord(String userInput, String commandWord) {
        if (userInput == null) {
            return false;
        }
        int firstSpaceIndex = userInput.indexOf(' ');
        String firstToken = firstSpaceIndex < 0 ? userInput : userInput.substring(0, firstSpaceIndex);
        return firstToken.equals(commandWord);
    }

    /**
     * Validates whitespace rules shared by all commands.
     *
     * @param userInput raw command input
     * @throws FriedbergUserInputException if the input is null, blank, or has invalid whitespace
     */
    public static void validateRawInput(String userInput) throws FriedbergUserInputException {
        if (userInput == null) {
            throw new FriedbergUserInputException("Command must not be null");
        }
        if (userInput.isEmpty()) {
            throw new FriedbergUserInputException("Command must not be empty");
        }
        if (!userInput.equals(userInput.strip())) {
            throw new FriedbergUserInputException("Command must not have leading or trailing whitespace");
        }
        if (userInput.chars().anyMatch(character -> Character.isWhitespace(character) && character != ' ')) {
            throw new FriedbergUserInputException("Tabs and line breaks are not allowed in commands");
        }
        if (userInput.contains("  ")) {
            throw new FriedbergUserInputException("Use exactly one space between command parts");
        }
    }

    /**
     * Validates a command that takes no arguments.
     *
     * @param userInput raw command input
     * @param commandWord expected command word
     * @throws FriedbergUserInputException if arguments are present or the command differs
     */
    public static void parseNoArgumentCommand(String userInput, String commandWord)
            throws FriedbergUserInputException {
        validateRawInput(userInput);
        if (!userInput.equals(commandWord)) {
            throw new FriedbergUserInputException(String.format(
                    "The %s command does not take parameters. Expected: %s", commandWord, commandWord));
        }
    }

    /**
     * Parses and validates a command followed by a required text value.
     *
     * @param userInput raw command input
     * @param commandWord expected command word
     * @param fieldName name used in validation errors
     * @return validated text following the command word
     * @throws FriedbergUserInputException if the text is missing or invalid
     */
    public static String parseDescriptionCommand(String userInput, String commandWord, String fieldName)
            throws FriedbergUserInputException {
        validateRawInput(userInput);
        String prefix = commandWord + " ";
        if (!userInput.startsWith(prefix)) {
            throw new FriedbergUserInputException(String.format(
                    "Missing %s. Expected: %s %s", fieldName, commandWord, fieldName.toUpperCase()));
        }
        String description = userInput.substring(prefix.length());
        validateDescription(description, fieldName);
        return description;
    }

    /**
     * Parses the one-based index of a command and converts it to zero-based form.
     *
     * @param userInput raw command input
     * @param commandWord expected command word
     * @return zero-based task index
     * @throws FriedbergUserInputException if the index is missing or malformed
     */
    public static int parseIndexCommand(String userInput, String commandWord) throws FriedbergUserInputException {
        validateRawInput(userInput);
        String prefix = commandWord + " ";
        if (!userInput.startsWith(prefix)) {
            throw new FriedbergUserInputException(String.format(
                    "Missing task index. Expected: %s INDEX", commandWord));
        }
        String indexText = userInput.substring(prefix.length());
        if (!indexText.matches("[0-9]+")) {
            throw new FriedbergUserInputException("Task index must contain digits only and start from 1");
        }
        int oneBasedIndex = parseInt(indexText);
        if (oneBasedIndex < 1) {
            throw new FriedbergUserInputException("Task index must start from 1");
        }
        return oneBasedIndex - 1;
    }

    /**
     * Parses a deadline's description and single required /by parameter.
     *
     * @param userInput raw deadline command
     * @return validated deadline fields
     * @throws FriedbergUserInputException if syntax, description, or date is invalid
     */
    public static ParsedDeadline parseDeadlineCommand(String userInput) throws FriedbergUserInputException {
        validateRawInput(userInput);
        String prefix = "deadline ";
        if (!userInput.startsWith(prefix)) {
            throw new FriedbergUserInputException(
                    "Missing task description. Expected: deadline DESCRIPTION /by yyyy-MM-dd");
        }
        String body = userInput.substring(prefix.length());
        String marker = " /by ";
        int markerCount = countOccurrences(body, marker);
        if (markerCount == 0) {
            throw new FriedbergUserInputException("deadline task expected to have /by");
        }
        if (markerCount > 1) {
            throw new FriedbergUserInputException("Parameter /by must be specified exactly once");
        }
        int markerIndex = body.indexOf(marker);
        String description = body.substring(0, markerIndex);
        String byDate = body.substring(markerIndex + marker.length());
        validateDescription(description, "task description");
        parseStrictDate(byDate, "/by");
        return new ParsedDeadline(description, byDate);
    }

    /**
     * Parses an event's description and required ordered /from and /to parameters.
     *
     * @param userInput raw event command
     * @return validated event fields
     * @throws FriedbergUserInputException if syntax, description, dates, or ordering is invalid
     */
    public static ParsedEvent parseEventCommand(String userInput) throws FriedbergUserInputException {
        validateRawInput(userInput);
        String prefix = "event ";
        if (!userInput.startsWith(prefix)) {
            throw new FriedbergUserInputException(
                    "Missing task description. Expected: event DESCRIPTION /from yyyy-MM-dd /to yyyy-MM-dd");
        }
        String body = userInput.substring(prefix.length());
        String fromMarker = " /from ";
        String toMarker = " /to ";
        int fromCount = countOccurrences(body, fromMarker);
        int toCount = countOccurrences(body, toMarker);
        if (fromCount == 0) {
            throw new FriedbergUserInputException("event task expected to have /from");
        }
        if (fromCount > 1) {
            throw new FriedbergUserInputException("Parameter /from must be specified exactly once");
        }
        if (toCount == 0) {
            throw new FriedbergUserInputException("event task expected to have /to");
        }
        if (toCount > 1) {
            throw new FriedbergUserInputException("Parameter /to must be specified exactly once");
        }
        int fromIndex = body.indexOf(fromMarker);
        int toIndex = body.indexOf(toMarker);
        if (fromIndex > toIndex) {
            throw new FriedbergUserInputException("Parameter /from must appear before /to");
        }
        String description = body.substring(0, fromIndex);
        String fromDate = body.substring(fromIndex + fromMarker.length(), toIndex);
        String toDate = body.substring(toIndex + toMarker.length());
        validateDescription(description, "task description");
        LocalDate parsedFromDate = parseStrictDate(fromDate, "/from");
        LocalDate parsedToDate = parseStrictDate(toDate, "/to");
        if (!parsedFromDate.isBefore(parsedToDate)) {
            throw new FriedbergUserInputException("Event start date must be before its end date");
        }
        return new ParsedEvent(description, fromDate, toDate);
    }

    /**
     * Validates user-entered descriptive text against storage and command constraints.
     *
     * @param description text to validate
     * @param fieldName name used in validation errors
     * @throws FriedbergUserInputException if the text is empty or contains unsupported characters
     */
    public static void validateDescription(String description, String fieldName)
            throws FriedbergUserInputException {
        if (description.isEmpty()) {
            throw new FriedbergUserInputException(String.format("Missing %s", fieldName));
        }
        if (!DESCRIPTION_PATTERN.matcher(description).matches()) {
            throw new FriedbergUserInputException(String.format(
                    "%s contains an unexpected character", capitalize(fieldName)));
        }
    }

    /**
     * Parses an exact ISO-style date and rejects nonexistent calendar dates.
     *
     * @param dateText date text to parse
     * @param parameterName parameter associated with the date
     * @return parsed local date
     * @throws FriedbergUserInputException if format or calendar value is invalid
     */
    public static LocalDate parseStrictDate(String dateText, String parameterName)
            throws FriedbergUserInputException {
        if (!DATE_PATTERN.matcher(dateText).matches()) {
            throw new FriedbergUserInputException(String.format(
                    "Date for %s must use yyyy-MM-dd, e.g. 2026-09-10", parameterName));
        }
        try {
            return LocalDate.parse(dateText, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new FriedbergUserInputException(String.format(
                    "Date for %s does not exist: %s", parameterName, dateText));
        }
    }

    /**
     * Parses a string as an integer and wraps invalid input in a Friedberg user input exception.
     *
     * @param str string to parse
     * @return parsed integer value
     * @throws FriedbergUserInputException if the string is not a valid integer
     */
    public static int parseInt(String str) throws FriedbergUserInputException {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            throw new FriedbergUserInputException(String.format("Expected a valid integer input instead of %s", str));
        }
    }

    private static int countOccurrences(String input, String target) {
        int count = 0;
        int searchIndex = 0;
        while ((searchIndex = input.indexOf(target, searchIndex)) >= 0) {
            count++;
            searchIndex += target.length();
        }
        return count;
    }

    private static String capitalize(String text) {
        return Character.toUpperCase(text.charAt(0)) + text.substring(1);
    }
}
