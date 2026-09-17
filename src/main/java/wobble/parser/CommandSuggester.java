package wobble.parser;

import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;

/** Suggests the closest supported command when a user enters an invalid one. */
public final class CommandSuggester {
    private static final int MAX_COMMAND_DISTANCE = 2;

    private static final List<CommandSpec> COMMANDS = List.of(
            new CommandSpec("todo", "todo <description>"),
            new CommandSpec("deadline", "deadline <description> /by <date/time>"),
            new CommandSpec("event", "event <description> /from <start> /to <end>"),
            new CommandSpec("list", "list"),
            new CommandSpec("find", "find <keyword>"),
            new CommandSpec("mark", "mark <number>"),
            new CommandSpec("unmark", "unmark <number>"),
            new CommandSpec("delete", "delete <number>"),
            new CommandSpec("remove", "remove <number>"),
            new CommandSpec("due on", "due on <date>"),
            new CommandSpec("reminders", "reminders [number of days]"),
            new CommandSpec("help", "help"),
            new CommandSpec("bye", "bye"));

    private CommandSuggester() {
        // Prevent instantiation of this utility class.
    }

    /**
     * Finds a supported command that is close to the command entered by the user.
     *
     * @param command the complete command entered by the user
     * @return a quoted corrected command or command format, using {@code "help"} when no close match exists
     */
    public static String suggest(String command) {
        String input = command == null ? "" : command.trim();
        String normalizedCommand = input.toLowerCase(Locale.ROOT);

        if (normalizedCommand.isEmpty()) {
            return quote("help");
        }

        for (CommandSpec commandSpec : COMMANDS) {
            if (matchesCommand(normalizedCommand, commandSpec.name())) {
                String correctedCommand = correctDateCommand(input, commandSpec.name());
                if (correctedCommand != null && !correctedCommand.equals(input)) {
                    return quote(correctedCommand);
                }
                return formatSuggestion(commandSpec.format());
            }
        }

        String typedCommandRoot = extractCommandRoot(normalizedCommand);
        CommandSpec closestCommand = findClosestCommand(typedCommandRoot);
        if (closestCommand == null) {
            return quote("help");
        }

        String correctedRootCommand = replaceCommandRoot(input, typedCommandRoot, closestCommand.name());
        String correctedCommand = correctDateCommand(correctedRootCommand, closestCommand.name());
        if (correctedCommand != null && !correctedCommand.equals(input)) {
            return quote(correctedCommand);
        }
        if (hasArguments(correctedRootCommand, closestCommand.name())) {
            return quote(correctedRootCommand);
        }
        return formatSuggestion(closestCommand.format());
    }

    /** Checks whether the entered text uses a supported command name. */
    private static boolean matchesCommand(String command, String commandName) {
        return command.equals(commandName) || command.startsWith(commandName + " ");
    }

    /** Extracts the command name, including the two-word {@code due on} command. */
    private static String extractCommandRoot(String command) {
        String[] words = command.split("\\s+");
        if (words.length >= 2 && words[0].equals("due")) {
            return words[0] + words[1];
        }
        return words[0];
    }

    /** Replaces a misspelled command name while preserving the user's command arguments. */
    private static String replaceCommandRoot(String command, String typedCommandRoot, String correctedCommandRoot) {
        return correctedCommandRoot + command.substring(typedCommandRoot.length());
    }

    /** Checks whether the corrected command contains arguments that can be retained in a suggestion. */
    private static boolean hasArguments(String command, String commandName) {
        return command.length() > commandName.length()
                && command.substring(commandName.length()).trim().length() > 0;
    }

    /** Attempts to correct an obvious date/time typo in a command. */
    private static String correctDateCommand(String command, String commandName) {
        if (commandName.equals("deadline")) {
            return correctSingleDateSegment(command, " /by ");
        }
        if (commandName.equals("event")) {
            return correctEventDates(command);
        }
        if (commandName.equals("due on")) {
            String dateText = command.substring(commandName.length()).trim();
            String correctedDate = correctDateTimeText(dateText);
            return correctedDate == null ? null : commandName + " " + correctedDate;
        }
        return null;
    }

    /** Corrects the date/time segment after a deadline separator when the fix is unambiguous. */
    private static String correctSingleDateSegment(String command, String separator) {
        int separatorIndex = command.toLowerCase(Locale.ROOT).indexOf(separator);
        if (separatorIndex < 0) {
            return null;
        }

        String dateText = command.substring(separatorIndex + separator.length()).trim();
        String correctedDate = correctDateTimeText(dateText);
        return correctedDate == null
                ? null
                : command.substring(0, separatorIndex + separator.length()) + correctedDate;
    }

    /** Corrects both date/time segments of an event when each fix is unambiguous. */
    private static String correctEventDates(String command) {
        String lowerCaseCommand = command.toLowerCase(Locale.ROOT);
        int fromSeparator = lowerCaseCommand.indexOf(" /from ");
        int toSeparator = lowerCaseCommand.indexOf(" /to ");
        if (fromSeparator < 0 || toSeparator < 0 || fromSeparator >= toSeparator) {
            return null;
        }

        String fromText = command.substring(fromSeparator + 7, toSeparator).trim();
        String toText = command.substring(toSeparator + 5).trim();
        String correctedFrom = correctDateTimeText(fromText);
        String correctedTo = correctDateTimeText(toText);
        if (correctedFrom == null || correctedTo == null) {
            return null;
        }

        return command.substring(0, fromSeparator + 7) + correctedFrom
                + command.substring(toSeparator, toSeparator + 5) + correctedTo;
    }

    /** Returns the original text or an unambiguous one-character correction if it parses as a date/time. */
    private static String correctDateTimeText(String dateText) {
        if (dateText.isEmpty()) {
            return null;
        }
        if (isDateTime(dateText)) {
            return dateText;
        }

        for (int index = dateText.length() - 1; index >= 0; index--) {
            String candidate = dateText.substring(0, index) + dateText.substring(index + 1);
            if (isDateTime(candidate)) {
                return candidate;
            }
        }
        return null;
    }

    /** Checks whether Wobble can parse the complete date/time text. */
    private static boolean isDateTime(String dateText) {
        try {
            DateTimeParser.parse(dateText);
            return true;
        } catch (DateTimeParseException exception) {
            return false;
        }
    }

    /** Finds the closest command when the command name itself contains a typo. */
    private static CommandSpec findClosestCommand(String commandRoot) {
        CommandSpec closestCommand = null;
        int closestDistance = Integer.MAX_VALUE;
        String normalizedRoot = commandRoot.replace(" ", "");

        for (CommandSpec commandSpec : COMMANDS) {
            String normalizedName = commandSpec.name().replace(" ", "");
            int distance = editDistance(normalizedRoot, normalizedName);
            if (distance < closestDistance) {
                closestDistance = distance;
                closestCommand = commandSpec;
            }
        }

        return closestDistance <= MAX_COMMAND_DISTANCE ? closestCommand : null;
    }

    /** Computes the number of single-character edits needed to transform one string into another. */
    private static int editDistance(String first, String second) {
        int[] previousRow = new int[second.length() + 1];
        for (int index = 0; index <= second.length(); index++) {
            previousRow[index] = index;
        }

        for (int firstIndex = 1; firstIndex <= first.length(); firstIndex++) {
            int[] currentRow = new int[second.length() + 1];
            currentRow[0] = firstIndex;
            for (int secondIndex = 1; secondIndex <= second.length(); secondIndex++) {
                int substitutionCost = first.charAt(firstIndex - 1) == second.charAt(secondIndex - 1)
                        ? 0
                        : 1;
                currentRow[secondIndex] = Math.min(
                        Math.min(currentRow[secondIndex - 1] + 1, previousRow[secondIndex] + 1),
                        previousRow[secondIndex - 1] + substitutionCost);
            }
            previousRow = currentRow;
        }

        return previousRow[second.length()];
    }

    /** Formats a command syntax for display in a conversational error message. */
    private static String formatSuggestion(String commandFormat) {
        return quote(commandFormat);
    }

    /** Wraps a command so that it stands out as a complete suggestion in the conversation. */
    private static String quote(String command) {
        return "\"" + command + "\"";
    }

    /** Stores a command's name and the syntax shown to users. */
    private record CommandSpec(String name, String format) {
    }
}
