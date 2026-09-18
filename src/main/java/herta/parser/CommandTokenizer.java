package herta.parser;

import herta.exception.HertaException;

/**
 * Normalizes the command boundary while preserving spaces inside task text.
 */
public final class CommandTokenizer {
    /** Maximum command length accepted from either the console or GUI. */
    private static final String NULL_COMMAND_ERROR =
            "Nothing usable came through. Enter a command, such as list or todo <description>.";
    private static final String LONG_COMMAND_ERROR =
            "That is excessive. Keep the command under %d characters.";
    private static final String UNSUPPORTED_CHARACTER_ERROR =
            "That command contains unsupported spacing or control characters. "
                    + "Use ordinary spaces and one line.";

    private CommandTokenizer() {
        // Utility class; do not instantiate.
    }

    /**
     * Normalizes one raw command before recognition and argument extraction.
     * Only the outer boundary and the separator after the command keyword are changed;
     * meaningful spaces in the remaining argument are preserved.
     *
     * @param input the raw command.
     * @return the normalized command.
     * @throws HertaException if the input is null, too long, or contains unsupported characters.
     */
    public static String normalize(String input) throws HertaException {
        validateInput(input);
        String trimmedInput = trimHorizontalWhitespace(input);
        if (trimmedInput.isEmpty()) {
            return trimmedInput;
        }

        int keywordEnd = findFirstWhitespace(trimmedInput);
        if (keywordEnd == trimmedInput.length()) {
            return trimmedInput;
        }
        String keyword = trimmedInput.substring(0, keywordEnd);
        String arguments = trimHorizontalWhitespace(trimmedInput.substring(keywordEnd));
        return arguments.isEmpty() ? keyword : keyword + " " + arguments;
    }

    /** Indicates whether a character is one of the two supported horizontal separators. */
    static boolean isHorizontalWhitespace(char character) {
        return character == ' ' || character == '\t';
    }

    /** Counts complete marker tokens in command arguments. */
    static int countMarker(String input, String marker) {
        int markerCount = 0;
        int searchStart = 0;
        while (searchStart < input.length()) {
            int markerIndex = findMarker(input, marker, searchStart);
            if (markerIndex < 0) {
                return markerCount;
            }
            markerCount++;
            searchStart = markerIndex + marker.length();
        }
        return markerCount;
    }

    /** Finds the first complete marker token after the supplied character offset. */
    static int findMarker(String input, String marker) {
        return findMarker(input, marker, 0);
    }

    /** Finds a complete marker token after the supplied character offset. */
    private static int findMarker(String input, String marker, int searchStart) {
        int markerIndex = input.indexOf(marker, searchStart);
        while (markerIndex >= 0) {
            int markerEnd = markerIndex + marker.length();
            boolean hasValidLeftBoundary = markerIndex == 0
                    || isHorizontalWhitespace(input.charAt(markerIndex - 1));
            boolean hasValidRightBoundary = markerEnd == input.length()
                    || isHorizontalWhitespace(input.charAt(markerEnd));
            if (hasValidLeftBoundary && hasValidRightBoundary) {
                return markerIndex;
            }
            markerIndex = input.indexOf(marker, markerIndex + 1);
        }
        return -1;
    }

    /** Validates raw command size and characters before any parser work occurs. */
    private static void validateInput(String input) throws HertaException {
        if (input == null) {
            throw new HertaException(NULL_COMMAND_ERROR);
        }
        if (input.length() > ParserLimits.MAX_COMMAND_LENGTH) {
            throw new HertaException(LONG_COMMAND_ERROR.formatted(ParserLimits.MAX_COMMAND_LENGTH));
        }
        for (int i = 0; i < input.length(); i++) {
            char character = input.charAt(i);
            if (isUnsupportedCharacter(character)) {
                throw new HertaException(UNSUPPORTED_CHARACTER_ERROR);
            }
        }
    }

    /** Identifies line breaks, controls, lookalike separators, and formatting overrides. */
    private static boolean isUnsupportedCharacter(char character) {
        boolean isLineBreak = character == '\n' || character == '\r';
        boolean isUnsupportedControl = Character.isISOControl(character)
                && !isHorizontalWhitespace(character);
        boolean isLookalikeSpace = Character.isSpaceChar(character)
                && !isHorizontalWhitespace(character);
        boolean isFormatCharacter = Character.getType(character) == Character.FORMAT;
        return isLineBreak || isUnsupportedControl || isLookalikeSpace || isFormatCharacter;
    }

    /** Trims only the horizontal whitespace supported by the command grammar. */
    private static String trimHorizontalWhitespace(String input) {
        int start = 0;
        int end = input.length();
        while (start < end && isHorizontalWhitespace(input.charAt(start))) {
            start++;
        }
        while (end > start && isHorizontalWhitespace(input.charAt(end - 1))) {
            end--;
        }
        return input.substring(start, end);
    }

    /** Returns the first separator after the command keyword. */
    private static int findFirstWhitespace(String input) {
        for (int i = 0; i < input.length(); i++) {
            if (isHorizontalWhitespace(input.charAt(i))) {
                return i;
            }
        }
        return input.length();
    }
}
