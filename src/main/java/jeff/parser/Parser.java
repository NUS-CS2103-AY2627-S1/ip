package jeff.parser;

import java.util.Arrays;

/**
 * Makes sense of raw user input: figuring out which command was typed, and
 * pulling the pieces (description, dates, task number) out of its arguments.
 */
public class Parser {
    /** The fixed set of commands Jeff understands, each tied to the exact word that triggers it. */
    public enum Command {
        BYE("bye"), LIST("list"), MARK("mark"), UNMARK("unmark"), DELETE("delete"),
        TODO("todo"), DEADLINE("deadline"), EVENT("event"), FIND("find"), SORT("sort"), UNKNOWN("");

        private final String word;

        Command(String word) {
            this.word = word;
        }

        /** Looks up the command matching the given (case-sensitive) first word of user input. */
        private static Command fromWord(String word) {
            return Arrays.stream(values())
                    .filter(command -> command.word.equals(word))
                    .findFirst()
                    .orElse(UNKNOWN);
        }
    }

    /** Returns the command type named by the first whitespace-separated word of the given input. */
    public static Command parseCommandType(String input) {
        return Command.fromWord(input.split(" ", 2)[0]);
    }

    /** Returns everything after the first whitespace-separated word of the given input, trimmed. */
    public static String getArguments(String input) {
        String[] parts = input.split(" ", 2);
        return parts.length > 1 ? parts[1].trim() : "";
    }

    /** Strips a leading label word (e.g. "by "/"from ") off a "/"-delimited segment, returning what's left. */
    private static String afterLabel(String segment) {
        String trimmed = segment.trim();
        int spaceIndex = trimmed.indexOf(' ');
        return spaceIndex == -1 ? "" : trimmed.substring(spaceIndex + 1).trim();
    }

    /**
     * Splits a "deadline" command's arguments into {description, by}.
     * Throws {@link ArrayIndexOutOfBoundsException} if the /by segment is missing.
     */
    public static String[] splitDeadlineArgs(String arguments) {
        String[] parts = arguments.split("/", 2);
        return new String[] { parts[0].trim(), afterLabel(parts[1]) };
    }

    /**
     * Splits an "event" command's arguments into {description, from, to}.
     * Throws {@link ArrayIndexOutOfBoundsException} if the /from or /to segment is missing.
     */
    public static String[] splitEventArgs(String arguments) {
        String[] parts = arguments.split("/", 3);
        return new String[] { parts[0].trim(), afterLabel(parts[1]), afterLabel(parts[2]) };
    }

    /**
     * Parses a "mark"/"unmark"/"delete" command's task-number argument into a zero-based
     * index into a list of the given size.
     * Throws {@link NumberFormatException} if the number is missing/malformed, or
     * {@link IndexOutOfBoundsException} if it doesn't refer to an existing task.
     */
    public static int parseTaskIndex(String numberPart, int listSize) {
        int index = Integer.parseInt(numberPart) - 1;
        if (index < 0 || index >= listSize) {
            throw new IndexOutOfBoundsException("Task " + numberPart + " doesn't exist.");
        }
        // Postcondition: every path that could leave index out of [0, listSize) has
        // already returned via the exception above, so this documents (and would catch,
        // if a future edit breaks it) the guarantee this method makes to its callers.
        assert index >= 0 && index < listSize : "index must be in range here";
        return index;
    }
}
