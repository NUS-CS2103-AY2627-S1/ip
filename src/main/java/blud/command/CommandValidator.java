package blud.command;

import blud.exception.CommandFormatException;

/** Validates command-level syntax before command-specific objects are created. */
public final class CommandValidator {
    private CommandValidator() {
    }

    /** Validates whitespace and command arity rules shared by all commands. */
    public static void validate(String userInput) {
        if (userInput == null || userInput.isBlank()) {
            throw new CommandFormatException("Please enter a command.");
        }
        if (!userInput.equals(userInput.trim())) {
            throw new CommandFormatException("Command must not have leading or trailing spaces.");
        }
        if (userInput.indexOf('\t') >= 0 || userInput.contains("  ")) {
            throw new CommandFormatException("Use exactly one space between command parameters.");
        }
        if (userInput.indexOf('|') >= 0) {
            throw new CommandFormatException("Task descriptions must not contain '|'.");
        }
    }

    /** Validates the number and basic shape of parameters for a parsed command. */
    public static void validateCommand(Command command, String userInput) {
        String[] words = userInput.split(" ");
        String[] taskParts = userInput.split(" /", -1);
        switch (command) {
            case LIST, BYE -> requireWordCount(words, 1, command.name().toLowerCase());
            case FIND -> requireAtLeastWords(words, 2, "find <keyword>");
            case MARK, UNMARK, DELETE -> requireWordCount(words, 2,
                    command.name().toLowerCase() + " <task number>");
            case SORT -> requireWordCountRange(words, 1, 3,
                    "sort <deadline|event|status> [asc|desc]");
            case TODO -> requireAtMostTaskParts(taskParts, 1, "todo <description>");
            case DEADLINE -> requireAtMostTaskParts(taskParts, 2,
                    "deadline <description> /by <date time>");
            case EVENT -> requireAtMostTaskParts(taskParts, 3,
                    "event <description> /from <start> /to <end>");
            default -> {
                // The command conversion reports unsupported command names.
            }
        }
    }

    /** Returns task command parts split only at parameter markers. */
    public static String[] splitTaskParts(String userInput) {
        return userInput.split(" /", -1);
    }

    /** Returns a one-based task number as a zero-based list index. */
    public static int parseTaskIndex(String value) {
        try {
            int taskNumber = Integer.parseInt(value);
            if (taskNumber < 1) {
                throw new NumberFormatException();
            }
            return taskNumber - 1;
        } catch (NumberFormatException exception) {
            throw new CommandFormatException("Task number must be a positive integer.");
        }
    }

    /** Requires exactly the specified number of words. */
    private static void requireWordCount(String[] words, int expected, String usage) {
        if (words.length != expected) {
            throw new CommandFormatException("Invalid command format. Usage: " + usage);
        }
    }

    /** Requires at least the specified number of words. */
    private static void requireAtLeastWords(String[] words, int minimum, String usage) {
        if (words.length < minimum) {
            throw new CommandFormatException("Invalid command format. Usage: " + usage);
        }
    }

    /** Requires a word count within an inclusive range. */
    private static void requireWordCountRange(String[] words, int minimum, int maximum, String usage) {
        if (words.length < minimum || words.length > maximum) {
            throw new CommandFormatException("Invalid command format. Usage: " + usage);
        }
    }

    /** Rejects extra slash-separated task parameters while allowing task-specific missing-field errors. */
    private static void requireAtMostTaskParts(String[] parts, int maximum, String usage) {
        if (parts.length > maximum) {
            throw new CommandFormatException("Invalid command format. Usage: " + usage);
        }
    }
}
