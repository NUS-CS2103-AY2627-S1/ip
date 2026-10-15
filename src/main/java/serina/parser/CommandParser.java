package serina.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import serina.exception.SerinaError;
import serina.exception.SerinaException;

/**
 * Parses and validates commands entered by a user.
 */
public final class CommandParser {
    private static final String DEADLINE_PARAMETER = "/by";
    private static final String EVENT_START_PARAMETER = "/from";
    private static final String EVENT_END_PARAMETER = "/to";

    private CommandParser() {
    }

    /**
     * Parses a command and returns its semantic arguments.
     *
     * @param input Raw user input.
     * @return Validated command.
     * @throws SerinaException If the input does not follow a supported command format.
     */
    public static ParsedCommand parse(String input) throws SerinaException {
        if (input == null || input.isBlank()) {
            throw new SerinaException(SerinaError.EMPTY_COMMAND);
        }

        String commandText = input.strip();
        int firstWhitespace = findFirstWhitespace(commandText);
        String commandWord = firstWhitespace == -1 ? commandText : commandText.substring(0, firstWhitespace);
        String arguments = firstWhitespace == -1 ? "" : commandText.substring(firstWhitespace).strip();

        CommandType type;
        try {
            type = CommandType.valueOf(commandWord.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new SerinaException(SerinaError.UNKNOWN_COMMAND);
        }

        return switch (type) {
            case HELP, LIST, BYE -> parseWithoutArguments(type, arguments);
            case MARK, UNMARK, DELETE -> parseTaskNumber(type, arguments);
            case FIND -> parseRequiredText(type, arguments, SerinaError.EMPTY_FIND_KEYWORD);
            case TODO -> parseRequiredText(type, arguments, SerinaError.EMPTY_TODO);
            case DEADLINE -> parseDeadline(arguments);
            case EVENT -> parseEvent(arguments);
        };
    }

    private static ParsedCommand parseWithoutArguments(CommandType type, String arguments) throws SerinaException {
        if (!arguments.isEmpty()) {
            throw new SerinaException(SerinaError.UNEXPECTED_ARGUMENTS);
        }
        return new ParsedCommand(type, List.of());
    }

    private static ParsedCommand parseTaskNumber(CommandType type, String arguments) throws SerinaException {
        if (!arguments.matches("[1-9]\\d*")) {
            throw new SerinaException(SerinaError.INVALID_TASK_NUMBER);
        }
        return new ParsedCommand(type, List.of(arguments));
    }

    private static ParsedCommand parseRequiredText(CommandType type, String arguments, SerinaError emptyError)
            throws SerinaException {
        if (arguments.isEmpty()) {
            throw new SerinaException(emptyError);
        }
        validateText(arguments);
        return new ParsedCommand(type, List.of(arguments));
    }

    private static ParsedCommand parseDeadline(String arguments) throws SerinaException {
        List<String> tokens = splitTokens(arguments);
        int byIndex = tokens.indexOf(DEADLINE_PARAMETER);
        if (count(tokens, DEADLINE_PARAMETER) > 1) {
            throw new SerinaException(SerinaError.DUPLICATE_PARAMETER, "Use: deadline <task> /by <yyyy-MM-dd>");
        }
        if (tokens.contains(EVENT_START_PARAMETER) || tokens.contains(EVENT_END_PARAMETER)) {
            throw new SerinaException(SerinaError.INVALID_DEADLINE_FORMAT);
        }
        if (byIndex == -1) {
            throw new SerinaException(SerinaError.INVALID_DEADLINE_FORMAT);
        }

        String description = join(tokens, 0, byIndex);
        String date = join(tokens, byIndex + 1, tokens.size());
        if (description.isEmpty()) {
            throw new SerinaException(SerinaError.EMPTY_DEADLINE_DESCRIPTION);
        }
        if (date.isEmpty()) {
            throw new SerinaException(SerinaError.EMPTY_DEADLINE_BY);
        }
        if (date.contains(" ")) {
            throw new SerinaException(SerinaError.INVALID_DEADLINE_FORMAT);
        }
        validateText(description);
        return new ParsedCommand(CommandType.DEADLINE, List.of(description, date));
    }

    private static ParsedCommand parseEvent(String arguments) throws SerinaException {
        List<String> tokens = splitTokens(arguments);
        if (count(tokens, EVENT_START_PARAMETER) > 1 || count(tokens, EVENT_END_PARAMETER) > 1) {
            throw new SerinaException(SerinaError.DUPLICATE_PARAMETER,
                    "Use: event <task> /from <yyyy-MM-dd> /to <yyyy-MM-dd>");
        }
        if (tokens.contains(DEADLINE_PARAMETER)) {
            throw new SerinaException(SerinaError.INVALID_EVENT_FORMAT);
        }

        int fromIndex = tokens.indexOf(EVENT_START_PARAMETER);
        int toIndex = tokens.indexOf(EVENT_END_PARAMETER);
        if (fromIndex == -1 || toIndex == -1 || fromIndex > toIndex) {
            throw new SerinaException(SerinaError.INVALID_EVENT_FORMAT);
        }

        String description = join(tokens, 0, fromIndex);
        String startDate = join(tokens, fromIndex + 1, toIndex);
        String endDate = join(tokens, toIndex + 1, tokens.size());
        if (description.isEmpty()) {
            throw new SerinaException(SerinaError.EMPTY_EVENT_DESCRIPTION);
        }
        if (startDate.isEmpty()) {
            throw new SerinaException(SerinaError.EMPTY_EVENT_FROM);
        }
        if (endDate.isEmpty()) {
            throw new SerinaException(SerinaError.EMPTY_EVENT_TO);
        }
        if (startDate.contains(" ") || endDate.contains(" ")) {
            throw new SerinaException(SerinaError.INVALID_EVENT_FORMAT);
        }
        validateText(description);
        return new ParsedCommand(CommandType.EVENT, List.of(description, startDate, endDate));
    }

    private static void validateText(String text) throws SerinaException {
        if (text.codePoints().anyMatch(character -> Character.isISOControl(character) && character != '\t')) {
            throw new SerinaException(SerinaError.INVALID_CHARACTERS);
        }
    }

    private static List<String> splitTokens(String text) {
        if (text.isBlank()) {
            return List.of();
        }
        return new ArrayList<>(List.of(text.strip().split("\\s+")));
    }

    private static long count(List<String> tokens, String parameter) {
        return tokens.stream().filter(parameter::equals).count();
    }

    private static String join(List<String> tokens, int start, int end) {
        return String.join(" ", tokens.subList(start, end));
    }

    private static int findFirstWhitespace(String text) {
        for (int i = 0; i < text.length(); i++) {
            if (Character.isWhitespace(text.charAt(i))) {
                return i;
            }
        }
        return -1;
    }
}
