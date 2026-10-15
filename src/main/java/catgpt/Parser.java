package catgpt;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Locale;

/**
 * Interprets user input and validates command arguments.
 */
public class Parser {
    /**
     * Creates a parser for CatGPT commands.
     */
    public Parser() {
    }

    /** Supported CatGPT commands. */
    public enum CommandType {
        /** Exits CatGPT. */
        BYE,
        /** Displays all tasks. */
        LIST,
        /** Marks a task as completed. */
        MARK,
        /** Marks a task as incomplete. */
        UNMARK,
        /** Removes a task. */
        DELETE,
        /** Finds tasks containing a keyword. */
        FIND,
        /** Sorts tasks alphabetically by description. */
        SORT,
        /** Adds a todo task. */
        TODO,
        /** Adds a deadline task. */
        DEADLINE,
        /** Adds an event task. */
        EVENT
    }

    /** Contains a recognized command and its unprocessed arguments. */
    public static class ParsedCommand {
        private final CommandType type;
        private final String arguments;

        private ParsedCommand(CommandType type, String arguments) {
            this.type = type;
            this.arguments = arguments;
        }

        /**
         * Returns the recognized command type.
         *
         * @return Command type.
         */
        public CommandType getType() {
            return type;
        }

        /**
         * Returns the text following the command word.
         *
         * @return Command arguments.
         */
        public String getArguments() {
            return arguments;
        }
    }

    /**
     * Recognizes the command word and separates it from its arguments.
     *
     * @param input Full user input.
     * @return Parsed command.
     * @throws CatGPTException If the command is empty or unknown.
     */
    public ParsedCommand parse(String input) throws CatGPTException {
        String trimmedInput = input.trim();
        if (trimmedInput.isEmpty()) {
            throw new CatGPTException("Please enter a command.");
        }

        String[] parts = trimmedInput.split("\\s+", 2);
        String commandWord = parts[0].toUpperCase(Locale.ENGLISH);
        String arguments = parts.length == 2 ? parts[1].trim() : "";
        try {
            CommandType type = CommandType.valueOf(commandWord);
            if ((type == CommandType.BYE
                    || type == CommandType.LIST
                    || type == CommandType.SORT)
                    && !arguments.isEmpty()) {
                throw new CatGPTException("I don't know what that means.");
            }
            return new ParsedCommand(type, arguments);
        } catch (IllegalArgumentException error) {
            throw new CatGPTException("I don't know what that means.");
        }
    }

    /**
     * Parses and validates the task number in a task-selection command.
     *
     * @param command Parsed command containing a task number.
     * @param taskCount Current number of tasks.
     * @return Zero-based index of the selected task.
     * @throws CatGPTException If the task number is missing, invalid, or out of range.
     */
    public int parseTaskIndex(ParsedCommand command, int taskCount) throws CatGPTException {
        String arguments = command.getArguments();
        String commandWord = command.getType().name().toLowerCase(Locale.ENGLISH);
        if (arguments.isEmpty()) {
            throw new CatGPTException("Please provide a task number after '" + commandWord + "'.");
        }

        int taskNumber;
        try {
            taskNumber = Integer.parseInt(arguments);
        } catch (NumberFormatException error) {
            throw new CatGPTException("The task number must be a whole number.");
        }
        if (taskNumber < 1 || taskNumber > taskCount) {
            throw new CatGPTException("That task number is not in your list.");
        }
        assert taskNumber >= 1 && taskNumber <= taskCount
                : "Validated task number must be within the task list";
        return taskNumber - 1;
    }

    /**
     * Returns the keyword supplied to a find command.
     *
     * @param command Parsed find command.
     * @return Keyword to search for.
     * @throws CatGPTException If the keyword is missing.
     */
    public String parseKeyword(ParsedCommand command) throws CatGPTException {
        String keyword = command.getArguments();
        if (keyword.isEmpty()) {
            throw new CatGPTException("Please provide a keyword after 'find'.");
        }
        return keyword;
    }

    /**
     * Creates a task from an add command after validating its arguments.
     *
     * @param command Parsed todo, deadline, or event command.
     * @return Task represented by the command.
     * @throws CatGPTException If required task details are missing or invalid.
     */
    public Task parseTask(ParsedCommand command) throws CatGPTException {
        return switch (command.getType()) {
            case TODO -> parseTodo(command.getArguments());
            case DEADLINE -> parseDeadline(command.getArguments());
            case EVENT -> parseEvent(command.getArguments());
            default -> throw new IllegalArgumentException("Not an add command");
        };
    }

    private Task parseTodo(String description) throws CatGPTException {
        if (description.isEmpty()) {
            throw new CatGPTException("The description of a todo cannot be empty.");
        }
        return new Todo(description);
    }

    private Task parseDeadline(String arguments) throws CatGPTException {
        int byIndex = arguments.indexOf(" /by ");
        if (byIndex < 0) {
            throw new CatGPTException("Use: deadline DESCRIPTION /by yyyy-MM-dd");
        }
        String description = arguments.substring(0, byIndex).trim();
        String dateText = arguments.substring(byIndex + 5).trim();
        if (description.isEmpty()) {
            throw new CatGPTException("The description of a deadline cannot be empty.");
        }
        if (dateText.isEmpty()) {
            throw new CatGPTException("The deadline date cannot be empty.");
        }
        try {
            return new Deadline(description, LocalDate.parse(dateText));
        } catch (DateTimeParseException error) {
            throw new CatGPTException("The deadline date must use yyyy-MM-dd and be a valid date.");
        }
    }

    private Task parseEvent(String arguments) throws CatGPTException {
        int fromIndex = arguments.indexOf(" /from ");
        int toIndex = fromIndex < 0 ? -1 : arguments.indexOf(" /to ", fromIndex + 7);
        if (fromIndex < 0 || toIndex < 0) {
            throw new CatGPTException("Use: event DESCRIPTION /from START /to END");
        }
        String description = arguments.substring(0, fromIndex).trim();
        String from = arguments.substring(fromIndex + 7, toIndex).trim();
        String to = arguments.substring(toIndex + 5).trim();
        if (description.isEmpty()) {
            throw new CatGPTException("The description of an event cannot be empty.");
        }
        if (from.isEmpty() || to.isEmpty()) {
            throw new CatGPTException("The event start and end times cannot be empty.");
        }
        return new Event(description, from, to);
    }
}
