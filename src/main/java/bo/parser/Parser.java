package bo.parser;

import bo.BoException;
import bo.model.Deadline;
import bo.model.Event;
import bo.model.Task;
import bo.model.Todo;
import bo.time.DateTimeParser;

/**
 * Interprets user input and turns it into validated commands for Bo.
 */
public final class Parser {
    /** Creates a stateless command parser. */
    public Parser() {
        // No state is needed to parse independent commands.
    }

    /**
     * Parses one user command.
     *
     * @param command the complete command entered by the user.
     * @return the structured command.
     * @throws BoException if the command or its arguments are invalid.
     */
    public Command parse(String command) throws BoException {
        if (command == null || command.isBlank()) {
            throw new BoException("Please enter a command instead of an empty line.");
        }

        command = command.strip().replaceAll("\\s+", " ");
        String[] commandParts = command.split("\\s+", 2);
        String commandName = commandParts[0];
        if (command.equals("list")) {
            return new Command(Type.LIST, -1, null, null);
        }
        if (commandName.equals("list")) {
            throw new BoException("Please use list without additional arguments.");
        }
        if (commandName.equals("delete")) {
            return new Command(Type.DELETE, parseTaskIndex(command, Type.DELETE), null, null);
        }
        if (commandName.equals("mark")) {
            return new Command(Type.MARK, parseTaskIndex(command, Type.MARK), null, null);
        }
        if (commandName.equals("unmark")) {
            return new Command(Type.UNMARK, parseTaskIndex(command, Type.UNMARK), null, null);
        }
        if (commandName.equals("find")) {
            return new Command(Type.FIND, -1, null, parseKeyword(command));
        }
        if (commandName.equals("sort")) {
            if (!command.equals("sort")) {
                throw new BoException("Please use sort without additional arguments.");
            }
            return new Command(Type.SORT, -1, null, null);
        }
        if (commandName.equals("todo") || commandName.equals("deadline")
                || commandName.equals("event")) {
            return new Command(Type.ADD, -1, createTask(command), null);
        }

        throw new BoException("I'm sorry, but I don't know what that means :-(");
    }

    /**
     * Parses the one-based task number used by a task mutation command.
     *
     * @param command the complete command entered by the user.
     * @param type the mutation whose argument is being parsed.
     * @return the zero-based task index.
     * @throws BoException if the command does not contain a whole number.
     */
    private static int parseTaskIndex(String command, Type type) throws BoException {
        String[] commandParts = command.split("\\s+");
        String commandName = type.name().toLowerCase();
        if (commandParts.length != 2) {
            if (type == Type.DELETE) {
                throw new BoException("Please use delete followed by one task number, e.g. delete 1.");
            }
            throw new BoException("Please use " + commandName
                    + " followed by one task number, e.g. " + commandName + " 1.");
        }

        try {
            int taskNumber = Integer.parseInt(commandParts[1]);
            if (taskNumber <= 0) {
                throw new BoException("The task number must be a positive whole number.");
            }
            return taskNumber - 1;
        } catch (NumberFormatException exception) {
            throw new BoException("The task number must be a whole number.");
        }
    }

    /** Parses the keyword used by a find command. */
    private static String parseKeyword(String command) throws BoException {
        String[] commandParts = command.split("\\s+", 2);
        if (commandParts.length != 2 || commandParts[1].isBlank()) {
            throw new BoException("Please use find followed by a keyword, e.g. find book.");
        }
        return commandParts[1].strip();
    }

    /** Creates a task from a validated todo, deadline, or event command. */
    private static Task createTask(String command) throws BoException {
        String[] commandParts = command.split("\\s+", 2);
        String commandName = commandParts[0];
        String taskDetails = commandParts.length == 2 ? commandParts[1].strip() : "";

        if (commandName.equals("todo")) {
            if (taskDetails.isEmpty()) {
                throw new BoException("The description of a todo cannot be empty.");
            }
            return new Todo(taskDetails);
        }

        if (commandName.equals("deadline")) {
            if (taskDetails.isEmpty()) {
                throw new BoException("A deadline needs a description and a /by date.");
            }

            String[] byParts = taskDetails.split(" /by ", -1);
            if (byParts.length < 2) {
                throw new BoException("A deadline must include a /by date, e.g. deadline return book /by Friday.");
            }
            if (byParts.length > 2) {
                throw new BoException("A deadline can include only one /by date.");
            }
            String description = byParts[0].strip();
            String by = byParts[1].strip();
            if (description.isEmpty()) {
                throw new BoException("A deadline must include a /by date, e.g. deadline return book /by Friday.");
            }
            if (by.isEmpty()) {
                throw new BoException("The /by date of a deadline cannot be empty.");
            }
            validateDateTime(by, "The /by date of a deadline");
            return new Deadline(description, by);
        }

        if (taskDetails.isEmpty()) {
            throw new BoException("An event needs a description, a /from time, and a /to time.");
        }

        int fromMarker = taskDetails.indexOf(" /from ");
        int toMarker = taskDetails.indexOf(" /to ");
        if (fromMarker < 0) {
            throw new BoException("An event must include a /from time.");
        }
        if (toMarker < 0) {
            throw new BoException("An event must include a /to time.");
        }
        if (toMarker < fromMarker) {
            throw new BoException("Please put the /from time before the /to time.");
        }
        if (taskDetails.indexOf(" /from ", fromMarker + 1) >= 0) {
            throw new BoException("An event can include only one /from time.");
        }
        if (taskDetails.indexOf(" /to ", toMarker + 1) >= 0) {
            throw new BoException("An event can include only one /to time.");
        }

        String description = taskDetails.substring(0, fromMarker).strip();
        String from = taskDetails.substring(fromMarker + " /from ".length(), toMarker).strip();
        String to = taskDetails.substring(toMarker + " /to ".length()).strip();
        if (description.isEmpty()) {
            throw new BoException("The description of an event cannot be empty.");
        }
        if (from.isEmpty()) {
            throw new BoException("The /from time of an event cannot be empty.");
        }
        if (to.isEmpty()) {
            throw new BoException("The /to time of an event cannot be empty.");
        }
        DateTimeParser.ParsedDateTime parsedFrom = validateDateTime(from, "The /from time of an event");
        DateTimeParser.ParsedDateTime parsedTo = validateDateTime(to, "The /to time of an event");
        if (parsedFrom != null && parsedTo != null && !parsedFrom.value().isBefore(parsedTo.value())) {
            throw new BoException("The /from time of an event must be before its /to time.");
        }
        return new Event(description, from, to);
    }

    /**
     * Validates a value when it clearly uses a structured date/time format.
     * Legacy free-form values remain valid for backwards compatibility.
     */
    private static DateTimeParser.ParsedDateTime validateDateTime(String value, String fieldName)
            throws BoException {
        if (!DateTimeParser.looksLikeStructuredDateTime(value)) {
            return null;
        }
        try {
            return DateTimeParser.parse(value);
        } catch (IllegalArgumentException exception) {
            throw new BoException(fieldName + " is invalid. Please use a valid date or time.");
        }
    }

    /** The categories of command that Bo can execute. */
    public enum Type {
        /** Display all tasks. */
        LIST,
        /** Delete one task. */
        DELETE,
        /** Mark one task as done. */
        MARK,
        /** Mark one task as not done. */
        UNMARK,
        /** Find tasks whose descriptions contain a keyword. */
        FIND,
        /** Sort tasks by their relevant date. */
        SORT,
        /** Add a newly created task. */
        ADD
    }

    /**
     * A parsed command and the data needed to execute it.
     *
     * @param type the kind of command to execute.
     * @param taskIndex the zero-based task index, or {@code -1} when unused.
     * @param task the task to add, or {@code null} when the command does not add one.
     * @param keyword the search keyword, or {@code null} for non-search commands.
     */
    public record Command(Type type, int taskIndex, Task task, String keyword) {
    }
}
