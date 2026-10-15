package lenzabot.parser;

import java.util.Locale;
import java.util.Map;

/**
 * Breaks raw user input into the command word and its argument.
 */
public class Parser {
    private static final Map<String, String> COMMAND_ALIASES = Map.of(
            "t", "todo",
            "d", "deadline",
            "e", "event",
            "ls", "list"
    );

    private Parser() {
    }

    /**
     * Represents user input split into its command word and argument.
     */
    public static class ParsedInput {
        private final String command;
        private final String argument;

        /**
         * Creates parsed input with a command word and argument.
         *
         * @param command First word identifying the command to execute.
         * @param argument Remaining text supplied to the command.
         */
        public ParsedInput(String command, String argument) {
            this.command = command;
            this.argument = argument;
        }

        /**
         * Returns the parsed command word.
         *
         * @return Command word identifying the action to execute.
         */
        public String getCommand() {
            return command;
        }

        /**
         * Returns the argument supplied after the command word.
         *
         * @return Remaining command text, or an empty string if absent.
         */
        public String getArgument() {
            return argument;
        }
    }

    /**
     * Splits the given input at its first whitespace into a command word and
     * an argument. Surrounding and repeated whitespace is normalized.
     *
     * @param input Raw command text entered by the user.
     * @return Input separated into its command and argument.
     */
    public static ParsedInput parse(String input) {
        String trimmedInput = input.trim();
        int firstWhitespaceIndex = findFirstWhitespace(trimmedInput);
        if (firstWhitespaceIndex == -1) {
            return new ParsedInput(canonicalizeCommand(trimmedInput), "");
        }
        return new ParsedInput(
                canonicalizeCommand(trimmedInput.substring(0, firstWhitespaceIndex)),
                trimmedInput.substring(firstWhitespaceIndex).trim().replaceAll("\\s+", " ")
        );
    }

    // Converts a supported alias while leaving full and unknown commands unchanged.
    private static String canonicalizeCommand(String command) {
        String normalizedCommand = command.toLowerCase(Locale.ROOT);
        return COMMAND_ALIASES.getOrDefault(normalizedCommand, normalizedCommand);
    }

    private static int findFirstWhitespace(String input) {
        for (int index = 0; index < input.length(); index++) {
            if (Character.isWhitespace(input.charAt(index))) {
                return index;
            }
        }
        return -1;
    }
}
