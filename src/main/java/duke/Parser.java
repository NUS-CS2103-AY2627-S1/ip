package duke;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Interprets user commands and converts their arguments into domain values.
 */
public class Parser {
    private static final int MAX_AI_QUESTION_LENGTH = 1000;

    private static final Pattern DETAIL_MARKER = Pattern.compile("(?<!\\S)/(by|from|to)(?=\\s|$)");

    /**
     * Creates a parser for Wangsa command lines.
     */
    public Parser() {
    }

    /**
     * The supported actions that Wangsa can perform.
     */
    public enum CommandType {
        /** Exit the application. */ BYE,
        /** Display all tasks. */ LIST,
        /** Display the built-in command reference. */ HELP,
        /** Ask AI about supported commands. */ AI,
        /** Mark a task complete. */ MARK,
        /** Mark a task incomplete. */ UNMARK,
        /** Delete a task. */ DELETE,
        /** Search task descriptions. */ FIND,
        /** Sort tasks by their deadlines. */ SORT,
        /** Add a task. */ ADD_TASK
    }

    /**
     * Parses and validates a complete command into a typed command object.
     *
     * @param command Complete command entered by the user.
     * @return Validated command ready for the application service.
     * @throws WangsaException If the command or one of its arguments is invalid.
     */
    public Command parse(String command) throws WangsaException {
        command = command.trim();
        CommandType commandType = parseCommandType(command);
        return switch (commandType) {
            case BYE, LIST, SORT, HELP -> new Command.Simple(commandType);
            case MARK, UNMARK, DELETE -> new Command.TaskNumber(commandType, parseTaskNumber(command));
            case FIND -> new Command.Search(parseSearchKeyword(command));
            case ADD_TASK -> new Command.AddTask(parseTask(command));
            case AI -> new Command.AiQuestion(parseAiQuestion(command));
        };
    }

    /**
     * Identifies the action requested by a full command line.
     *
     * @param command Full command entered by the user.
     * @return The recognized command type.
     * @throws WangsaException If the command is empty or unknown.
     */
    public CommandType parseCommandType(String command) throws WangsaException {
        command = command.trim();
        if (command.isEmpty()) {
            throw new WangsaException("Please enter a command.");
        }

        String keyword = getFirstWord(command);
        return switch (keyword) {
            case "bye" -> requireExactCommand(command, keyword, CommandType.BYE);
            case "list" -> requireExactCommand(command, keyword, CommandType.LIST);
            case "help" -> requireExactCommand(command, keyword, CommandType.HELP);
            case "@ai" -> CommandType.AI;
            case "mark" -> CommandType.MARK;
            case "unmark" -> CommandType.UNMARK;
            case "delete" -> CommandType.DELETE;
            case "find" -> CommandType.FIND;
            case "sort" -> requireExactCommand(command, keyword, CommandType.SORT);
            case "todo", "deadline", "event" -> CommandType.ADD_TASK;
            default -> throw createUnknownCommandException();
        };
    }

    /**
     * Creates a task from a supported task-creation command.
     *
     * @param command Full todo, deadline, or event command.
     * @return The task described by the command.
     * @throws WangsaException If required task details are missing or invalid.
     */
    public Task parseTask(String command) throws WangsaException {
        command = command.trim();
        return switch (getFirstWord(command)) {
            case "todo" -> parseTodo(command);
            case "deadline" -> parseDeadline(command);
            case "event" -> parseEvent(command);
            default -> throw createUnknownCommandException();
        };
    }

    /**
     * Extracts the one task number supplied to a status or delete command.
     *
     * @param command Full mark, unmark, or delete command.
     * @return The parsed one-based task number.
     * @throws WangsaException If the argument count or number is invalid.
     */
    public int parseTaskNumber(String command) throws WangsaException {
        command = command.trim();
        String[] parts = command.split("\\s+");
        if (parts.length != 2) {
            throw new WangsaException(parts[0] + " expects one task number.");
        }

        try {
            return Integer.parseInt(parts[1]);
        } catch (NumberFormatException exception) {
            throw new WangsaException("Task number must be a whole number.");
        }
    }

    /**
     * Extracts the keyword used to search task descriptions.
     *
     * @param command Full find command.
     * @return Non-empty search keyword.
     * @throws WangsaException If the keyword is missing.
     */
    public String parseSearchKeyword(String command) throws WangsaException {
        command = command.trim();
        String keyword = getTextAfterKeyword(command, "find");
        if (keyword.isEmpty()) {
            throw new WangsaException("Find needs a keyword to search for.");
        }
        return keyword;
    }

    /**
     * Extracts a bounded question so accidental large pastes do not become expensive requests.
     */
    private String parseAiQuestion(String command) throws WangsaException {
        String question = getTextAfterKeyword(command, "@ai");
        if (question.isEmpty()) {
            throw new WangsaException("Ask a question after @ai, for example: @ai how do I add a deadline?");
        }
        if (question.length() > MAX_AI_QUESTION_LENGTH) {
            throw new WangsaException("Please keep your AI question to " + MAX_AI_QUESTION_LENGTH
                    + " characters or fewer.");
        }
        return question;
    }

    /**
     * Returns the first whitespace-delimited word in a command.
     */
    private String getFirstWord(String command) {
        int keywordEnd = 0;
        while (keywordEnd < command.length() && !Character.isWhitespace(command.charAt(keywordEnd))) {
            keywordEnd++;
        }
        return command.substring(0, keywordEnd);
    }

    /**
     * Returns a command type only when a command that takes no arguments is exact.
     */
    private CommandType requireExactCommand(String command, String keyword, CommandType commandType)
            throws WangsaException {
        if (!command.equals(keyword)) {
            throw createUnknownCommandException();
        }
        return commandType;
    }

    /**
     * Creates the standard error for an unrecognized command.
     */
    private WangsaException createUnknownCommandException() {
        return new WangsaException("I don't recognize that command. Type `help` to see what you can do.");
    }

    /**
     * Creates a todo or reports its missing description.
     */
    private Task parseTodo(String command) throws WangsaException {
        String description = getTextAfterKeyword(command, "todo");
        if (description.isEmpty()) {
            throw new WangsaException("What would you like to do? Try `todo read a book`.");
        }
        return new Todo(description);
    }

    /**
     * Creates a deadline with a validated ISO date.
     */
    private Task parseDeadline(String command) throws WangsaException {
        String content = getTextAfterKeyword(command, "deadline");
        List<String> fields = splitDetails(content,
                "A deadline must include a description and a /by date.", "by");
        String description = fields.get(0);
        String by = fields.get(1);
        if (description.isEmpty()) {
            throw new WangsaException("The description of a deadline cannot be empty.");
        }
        if (by.isEmpty()) {
            throw new WangsaException("A deadline needs a value after /by.");
        }

        try {
            if (!by.matches("[0-9]{4}-[0-9]{2}-[0-9]{2}")) {
                throw new DateTimeParseException("Expected a four-digit year", by, 0);
            }
            return new Deadline(description, LocalDate.parse(by));
        } catch (DateTimeParseException exception) {
            throw new WangsaException("Deadline date must be valid and use yyyy-MM-dd format "
                    + "(e.g., 2019-10-15).");
        }
    }

    /**
     * Creates an event or reports its missing description, start, or end.
     */
    private Task parseEvent(String command) throws WangsaException {
        String content = getTextAfterKeyword(command, "event");
        List<String> fields = splitDetails(content,
                "An event must include a description, /from start, and /to end.", "from", "to");
        String description = fields.get(0);
        String from = fields.get(1);
        String to = fields.get(2);
        if (description.isEmpty()) {
            throw new WangsaException("The description of an event cannot be empty.");
        }
        if (from.isEmpty() || to.isEmpty()) {
            throw new WangsaException("An event needs values after /from and /to.");
        }
        return new Event(description, from, to);
    }

    /**
     * Splits standalone detail markers, requiring each marker once in the expected order.
     */
    private List<String> splitDetails(String content, String formatHint, String... expectedMarkers)
            throws WangsaException {
        Matcher matcher = DETAIL_MARKER.matcher(content);
        List<String> fields = new ArrayList<>();
        int fieldStart = 0;
        while (matcher.find()) {
            int markerIndex = fields.size();
            if (markerIndex >= expectedMarkers.length
                    || !matcher.group(1).equals(expectedMarkers[markerIndex])) {
                throw new WangsaException("Use each detail marker once and in order. " + formatHint);
            }
            fields.add(content.substring(fieldStart, matcher.start()).trim());
            fieldStart = matcher.end();
        }
        if (fields.size() != expectedMarkers.length) {
            throw new WangsaException(formatHint);
        }
        fields.add(content.substring(fieldStart).trim());
        return fields;
    }

    /**
     * Returns the trimmed text after a command keyword.
     */
    private String getTextAfterKeyword(String command, String keyword) {
        return command.length() == keyword.length()
                ? ""
                : command.substring(keyword.length()).trim();
    }
}
