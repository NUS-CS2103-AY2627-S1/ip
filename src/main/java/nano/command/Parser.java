package nano.command;

import nano.NanoException;
import nano.task.Priority;
import nano.task.Task;

/**
 * Parses user commands and identifies their command types.
 */
public class Parser {

    /**
     * Identifies the type of the given user command.
     *
     * @param command user command.
     * @return the type of the command.
     */
    public CommandType parseCommand(String command) {
        return CommandType.from(command);
    }

    /**
     * Parses a command into the appropriate task.
     *
     * @param command user command describing a task.
     * @return the task represented by the command.
     * @throws NanoException if the command is invalid.
     */
    public Task parseTask(String command) throws NanoException {
        TaskType type = TaskType.from(command);
        String input = command.substring(type.keyword().length()).trim();
        return type.create(input);
    }

    /**
     * Extracts the task number from a command.
     *
     * @param command user command containing a task number.
     * @return the task number.
     * @throws NanoException if the task number is not a valid number.
     */
    public int parseTaskNumber(String command) throws NanoException {
        String[] parts = command.trim().split("\\s+");

        if (parts.length != 2) {
            throw new NanoException(
                    "Please provide exactly one task number."
            );
        }

        try {
            return Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            throw new NanoException(
                    "The task number must be a number."
            );
        }
    }

    /**
     * Extracts the keyword from a find command.
     *
     * @param command user command containing a keyword.
     * @return the keyword to find.
     * @throws NanoException if no keyword provided.
     */
    public String parseFindKeyword(String command) throws NanoException {
        String keyword = command.substring(4).trim();

        if (keyword.isEmpty()) {
            throw new NanoException("Please provide a keyword to search for.");
        }

        return keyword;
    }

    /**
     * Parses a priority command.
     *
     * @param command user command containing a task number and priority level.
     * @return the priority command.
     * @throws NanoException if the command is invalid.
     */
    public Command parsePriorityCommand(String command)
            throws NanoException {
        String[] parts = command.split("\\s+");

        if (parts.length != 3) {
            throw new NanoException(
                    "Usage: priority <task number> <high|normal|low>"
            );
        }

        int taskNumber;

        try {
            taskNumber = Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            throw new NanoException("The task number must be a number.");
        }

        try {
            Priority priority = Priority.valueOf(parts[2].toUpperCase());
            return new PriorityCommand(taskNumber, priority);
        } catch (IllegalArgumentException e) {
            throw new NanoException(
                    "Priority must be high, normal, or low."
            );
        }
    }

    /**
     * Parses the user's input into a command.
     *
     * @param input user input
     * @return the command represented by the input
     * @throws NanoException if the input is invalid
     */
    public Command parse(String input) throws NanoException {
        CommandType commandType = parseCommand(input);

        switch (commandType) {
            case BYE:
                return new ByeCommand();

            case LIST:
                return new ListCommand();

            case MARK:
                return new MarkCommand(parseTaskNumber(input));

            case UNMARK:
                return new UnmarkCommand(parseTaskNumber(input));

            case DELETE:
                return new DeleteCommand(parseTaskNumber(input));

            case FIND:
                return new FindCommand(parseFindKeyword(input));

            case PRIORITY:
                return parsePriorityCommand(input);

            case TODO:
            case DEADLINE:
            case EVENT:
                return new AddCommand(parseTask(input));

            default:
                throw new NanoException("What do you mean?");
        }
    }
}
