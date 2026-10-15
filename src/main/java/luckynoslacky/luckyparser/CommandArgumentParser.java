package luckynoslacky.luckyparser;

import java.util.Locale;

import luckynoslacky.luckyexception.LuckyNoInputException;
import luckynoslacky.luckyresponse.LuckyNoFormatMessages;
import luckynoslacky.luckyresponse.LuckyNoQuips;

/**
 * Parses syntax shared by multiple chatbot commands.
 *
 * <p>This class owns tokenization, task-number validation, and slash-marker
 * handling so command-specific parsers can focus on their own input shapes.</p>
 */
final class CommandArgumentParser {
    private CommandArgumentParser() {
        // Prevent instantiation of this utility class.
    }

    /**
     * Splits raw input into a normalized command token and its arguments.
     *
     * @param userInput raw user input
     * @return parsed command token and arguments
     * @throws LuckyNoInputException if the input is blank
     */
    static ParsedInput parseInput(String userInput) throws LuckyNoInputException {
        if (userInput == null || userInput.trim().isEmpty()) {
            throw new LuckyNoInputException(LuckyNoQuips.missingCommandMessage());
        }

        String[] commandParts = userInput.trim().split("\\s+", 2);
        String commandToken = commandParts[0].toLowerCase(Locale.ROOT);
        String arguments = commandParts.length == 2
                ? commandParts[1].trim()
                : "";
        return new ParsedInput(commandToken, arguments);
    }

    /**
     * Converts a command token into a supported command name.
     *
     * @param commandToken normalized command token
     * @return matching command name
     * @throws LuckyNoInputException if the token is not recognized
     */
    static LuckyNoParser.CommandName parseCommandName(String commandToken)
            throws LuckyNoInputException {
        return LuckyNoParser.CommandName.fromCommandToken(commandToken)
                .orElseThrow(() -> new LuckyNoInputException(
                        LuckyNoQuips.unknownCommandMessage()));
    }

    /**
     * Rejects arguments for commands that must stand alone.
     *
     * @param commandArguments command arguments
     * @param commandName command name used in the error message
     * @throws LuckyNoInputException if arguments are present
     */
    static void checkNoArguments(String commandArguments, String commandName)
            throws LuckyNoInputException {
        if (!commandArguments.isEmpty()) {
            throw new LuckyNoInputException(
                    LuckyNoQuips.extraArgumentsMessage(commandName));
        }
    }

    /**
     * Validates and converts a one-based task number.
     *
     * @param taskNumberText task-number text
     * @param taskCount number of tasks currently available
     * @return validated one-based task number
     * @throws LuckyNoInputException if the number is missing or out of range
     */
    static int parseTaskNumber(String taskNumberText, int taskCount)
            throws LuckyNoInputException {
        if (taskNumberText.isEmpty() || taskNumberText.matches(".*\\s+.*")) {
            throw new LuckyNoInputException(
                    LuckyNoQuips.missingTaskNumberMessage());
        }

        try {
            int taskNumber = Integer.parseInt(taskNumberText);
            if (taskNumber < 1 || taskNumber > taskCount) {
                throw new NumberFormatException();
            }
            return taskNumber;
        } catch (NumberFormatException exception) {
            throw new LuckyNoInputException(
                    LuckyNoQuips.invalidTaskNumberMessage());
        }
    }

    /**
     * Splits a command's first task number from its remaining arguments.
     *
     * @param commandArguments command arguments
     * @return task-number text and remaining arguments
     */
    static String[] splitTaskNumber(String commandArguments) {
        if (commandArguments == null || commandArguments.isBlank()) {
            return new String[]{"", ""};
        }
        String[] parts = commandArguments.trim().split("\\s+", 2);
        return parts.length == 1
                ? new String[]{parts[0], ""}
                : new String[]{parts[0], parts[1].trim()};
    }

    /**
     * Checks whether an argument starts with a complete syntax marker.
     *
     * @param text argument text
     * @param marker marker to find
     * @return true if the marker is at the beginning of the text
     */
    static boolean startsWithMarker(String text, String marker) {
        return text.length() >= marker.length()
                && text.regionMatches(true, 0, marker, 0, marker.length())
                && (text.length() == marker.length()
                || Character.isWhitespace(text.charAt(marker.length())));
    }

    /**
     * Extracts the value following a syntax marker and rejects extra markers.
     *
     * @param text marker and value text
     * @param marker marker to remove
     * @param commandName command being parsed
     * @param validFormats valid formats for the command
     * @return marker value
     * @throws LuckyNoInputException if the value is empty or contains a slash
     */
    static String markerValue(
            String text,
            String marker,
            LuckyNoParser.CommandName commandName,
            String... validFormats)
            throws LuckyNoInputException {
        if (!startsWithMarker(text, marker)) {
            throw invalidFormat(commandName, validFormats);
        }
        String value = text.substring(marker.length()).trim();
        if (value.isEmpty()) {
            throw invalidFormat(commandName, validFormats);
        }
        if (hasMarkerLikeSlash(value)) {
            throw invalidFormat(commandName, validFormats);
        }
        return value;
    }

    /**
     * Finds a marker after a specified character offset.
     *
     * @param text text to search
     * @param marker marker to find
     * @param fromIndex first index to inspect
     * @return marker index, or -1 when absent
     */
    static int markerIndex(String text, String marker, int fromIndex) {
        String normalizedText = text.toLowerCase(Locale.ROOT);
        String normalizedMarker = marker.toLowerCase(Locale.ROOT);
        int index = normalizedText.indexOf(normalizedMarker, fromIndex);
        while (index >= 0) {
            boolean atTokenStart = index == 0
                    || Character.isWhitespace(text.charAt(index - 1));
            int markerEnd = index + marker.length();
            boolean isCompleteMarker = markerEnd == text.length()
                    || Character.isWhitespace(text.charAt(markerEnd));
            if (atTokenStart && isCompleteMarker) {
                return index;
            }
            index = normalizedText.indexOf(normalizedMarker, index + 1);
        }
        return -1;
    }

    /**
     * Rejects a slash that looks like an unsupported command marker.
     *
     * @param text text to inspect
     * @param commandName command being parsed
     * @param validFormats valid command formats
     * @throws LuckyNoInputException if a marker-like slash is found
     */
    static void rejectMarkerLikeSlash(
            String text,
            LuckyNoParser.CommandName commandName,
            String... validFormats)
            throws LuckyNoInputException {
        if (hasMarkerLikeSlash(text)) {
            throw invalidFormat(commandName, validFormats);
        }
    }

    /**
     * Creates an invalid-format exception with the supplied alternatives.
     *
     * @param commandName command being parsed
     * @param formats valid command formats
     * @return invalid-format exception
     */
    static LuckyNoInputException invalidFormat(
            LuckyNoParser.CommandName commandName, String... formats) {
        return new LuckyNoInputException(
                LuckyNoFormatMessages.invalidFormatMessage(commandName, formats));
    }

    /**
     * Checks whether text contains a slash followed by an ASCII letter at the
     * beginning of an argument or immediately after Java whitespace.
     *
     * @param text text to inspect
     * @return true if an unsupported marker-like slash is present
     */
    private static boolean hasMarkerLikeSlash(String text) {
        for (int index = 0; index < text.length(); index++) {
            if (text.charAt(index) != '/') {
                continue;
            }
            boolean atArgumentStart = index == 0
                    || Character.isWhitespace(text.charAt(index - 1));
            boolean followedByAsciiLetter = index + 1 < text.length()
                    && isAsciiLetter(text.charAt(index + 1));
            if (atArgumentStart && followedByAsciiLetter) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks whether a character is an ASCII letter.
     *
     * @param character character to inspect
     * @return true if the character is an ASCII letter
     */
    private static boolean isAsciiLetter(char character) {
        return character >= 'A' && character <= 'Z'
                || character >= 'a' && character <= 'z';
    }

    /** Stores a normalized command token and its remaining arguments. */
    record ParsedInput(String commandToken, String arguments) {
    }
}
