package jaylen;

/** Converts raw Jaylen input into structured commands. */
public class Parser {
    private static final String DEADLINE_MARKER = " /by ";
    private static final String EVENT_FROM_MARKER = " /from ";
    private static final String EVENT_TO_MARKER = " /to ";

    /** Creates a parser for Jaylen commands. */
    public Parser() {
    }

    /**
     * Parses one complete line entered by the user.
     *
     * @param command the raw command entered by the user
     * @return the structured command
     * @throws JaylenException if the command is not recognized or is malformed
     */
    public ParsedCommand parse(String command) {
        if (command == null || command.isBlank()) {
            throw new JaylenException("Please enter a command.");
        }

        String normalizedCommand = command.trim().replaceAll("\\s+", " ");
        if (normalizedCommand.equals("bye")) {
            return ParsedCommand.simple(ParsedCommand.Type.BYE);
        } else if (normalizedCommand.equals("list")) {
            return ParsedCommand.simple(ParsedCommand.Type.LIST);
        } else if (isCommand(normalizedCommand, "find")) {
            String query = getArgument(normalizedCommand, "find");
            if (query.isEmpty()) {
                throw new JaylenException("A find command must include a keyword.");
            }
            return ParsedCommand.find(query);
        } else if (isCommand(normalizedCommand, "mark")) {
            return ParsedCommand.forTask(ParsedCommand.Type.MARK,
                    parseTaskNumber(getArgument(normalizedCommand, "mark"), "mark"));
        } else if (isCommand(normalizedCommand, "unmark")) {
            return ParsedCommand.forTask(ParsedCommand.Type.UNMARK,
                    parseTaskNumber(getArgument(normalizedCommand, "unmark"), "unmark"));
        } else if (isCommand(normalizedCommand, "delete")) {
            return ParsedCommand.forTask(ParsedCommand.Type.DELETE,
                    parseTaskNumber(getArgument(normalizedCommand, "delete"), "delete"));
        } else if (isCommand(normalizedCommand, "todo")) {
            return ParsedCommand.todo(requireDescription(
                    getArgument(normalizedCommand, "todo"), "todo"));
        } else if (isCommand(normalizedCommand, "deadline")) {
            return parseDeadline(normalizedCommand);
        } else if (isCommand(normalizedCommand, "event")) {
            return parseEvent(normalizedCommand);
        }
        throw new JaylenException("I'm sorry, but I don't know what that means.");
    }

    private ParsedCommand parseDeadline(String command) {
        int marker = command.indexOf(DEADLINE_MARKER);
        if (marker < 0) {
            throw new JaylenException("A deadline must include /by followed by a date or time.");
        }
        if (marker != command.lastIndexOf(DEADLINE_MARKER)) {
            throw new JaylenException("A deadline can contain only one /by marker.");
        }
        String description = requireDescription(
                safeSubstring(command, "deadline".length() + 1, marker), "deadline");
        String by = requireText(command.substring(marker + DEADLINE_MARKER.length()),
                "deadline date or time");
        return ParsedCommand.deadline(description, by);
    }

    private ParsedCommand parseEvent(String command) {
        int fromMarker = command.indexOf(EVENT_FROM_MARKER);
        int toMarker = command.indexOf(EVENT_TO_MARKER);
        if (fromMarker < 0 || toMarker < 0) {
            throw new JaylenException("An event must include /from and /to times.");
        }
        if (fromMarker != command.lastIndexOf(EVENT_FROM_MARKER)
                || toMarker != command.lastIndexOf(EVENT_TO_MARKER)) {
            throw new JaylenException("An event can contain only one /from and one /to marker.");
        }
        if (toMarker < fromMarker) {
            throw new JaylenException("Put /from before /to in an event command.");
        }
        String description = requireDescription(
                safeSubstring(command, "event".length() + 1, fromMarker), "event");
        String from = requireText(
                safeSubstring(command, fromMarker + EVENT_FROM_MARKER.length(), toMarker),
                "event start time");
        String to = requireText(command.substring(toMarker + EVENT_TO_MARKER.length()),
                "event end time");
        return ParsedCommand.event(description, from, to);
    }

    private int parseTaskNumber(String text, String commandName) {
        if (text.isBlank()) {
            throw new JaylenException("A " + commandName + " command must include a task number.");
        }
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException exception) {
            throw new JaylenException("The task number must be a whole number.");
        }
    }

    private String requireDescription(String text, String commandName) {
        String trimmedText = text.trim();
        if (trimmedText.isEmpty()) {
            if (commandName.equals("todo")) {
                throw new JaylenException("The description of a todo cannot be empty.");
            }
            throw new JaylenException("The description of a " + commandName + " cannot be empty.");
        }
        if (trimmedText.contains("|")) {
            throw new JaylenException("Task descriptions cannot contain the | character.");
        }
        return trimmedText;
    }

    private String requireText(String text, String fieldName) {
        String trimmedText = text.trim();
        if (trimmedText.isEmpty()) {
            throw new JaylenException("The " + fieldName + " cannot be empty.");
        }
        return trimmedText;
    }

    private boolean isCommand(String input, String commandWord) {
        return input.equals(commandWord) || input.startsWith(commandWord + " ");
    }

    private String getArgument(String input, String commandWord) {
        if (input.length() == commandWord.length()) {
            return "";
        }
        return input.substring(commandWord.length() + 1);
    }

    private String safeSubstring(String text, int startIndex, int endIndex) {
        return endIndex < startIndex ? "" : text.substring(startIndex, endIndex);
    }
}
