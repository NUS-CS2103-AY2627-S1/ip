package oreo.core;

import oreo.command.Command;

/** Converts raw user input into executable commands. */
public class Parser {
    private final CommandFactory commandFactory;

    /** Creates a parser using the supplied command factory. */
    public Parser(CommandFactory commandFactory) {
        assert commandFactory != null : "A parser must have a command factory.";
        this.commandFactory = commandFactory;
    }

    /** Parses complete input into an executable command object.
     *
     * @param input the complete user input
     * @return the executable command represented by the input
     * @throws OreoException if the input cannot be converted into a command
     */
    public Command parse(String input) throws OreoException {
        if (input == null) {
            throw new OreoException("Please enter a command.");
        }
        CommandType type = CommandType.fromInput(input);
        Command command = commandFactory.create(type, input, this);
        assert command != null : "Every command type must produce a command.";
        return command;
    }

    /** Returns the argument following a command keyword, or an empty string.
     *
     * @param input the complete user input
     * @param command the command keyword
     * @return the trimmed argument, or an empty string when none is supplied
     */
    public String argument(String input, String command) {
        if (input == null || command == null) {
            throw new IllegalArgumentException("Command parsing requires non-null text.");
        }
        assert input.equals(command) || input.startsWith(command + " ")
                : "The input must begin with the command keyword.";
        if (input.length() == command.length()) {
            return "";
        }
        return input.substring(command.length() + 1).trim();
    }

    /** Splits a deadline argument into description and due date.
     *
     * @param command the deadline command argument
     * @return the description and due date
     * @throws OreoException if the argument is malformed
     */
    public String[] deadlineParts(String command) throws OreoException {
        int marker = command.indexOf(" /by ");
        if (marker <= 0 || marker + 5 >= command.length()) {
            throw new OreoException("Use: deadline DESCRIPTION /by DATE");
        }
        String description = command.substring(0, marker).trim();
        String by = command.substring(marker + 5).trim();
        if (description.isEmpty() || by.isEmpty()) {
            throw new OreoException("Use: deadline DESCRIPTION /by DATE");
        }
        return new String[] {description, by};
    }

    /** Splits an event argument into description, start, and end values.
     *
     * @param command the event command argument
     * @return the description, start, and end values
     * @throws OreoException if the argument is malformed
     */
    public String[] eventParts(String command) throws OreoException {
        int fromMarker = command.indexOf(" /from ");
        int toMarker = command.indexOf(" /to ");
        if (fromMarker <= 0 || toMarker <= fromMarker + 7 || toMarker + 5 >= command.length()) {
            throw new OreoException("Use: event DESCRIPTION /from START /to END");
        }
        String description = command.substring(0, fromMarker).trim();
        String from = command.substring(fromMarker + 7, toMarker).trim();
        String to = command.substring(toMarker + 5).trim();
        if (description.isEmpty() || from.isEmpty() || to.isEmpty()) {
            throw new OreoException("Use: event DESCRIPTION /from START /to END");
        }
        return new String[] {description, from, to};
    }

    /** Validates and returns a to-do description.
     *
     * @param command the to-do description
     * @return the validated description
     * @throws OreoException if the description is empty
     */
    public String todoDescription(String command) throws OreoException {
        if (command.isEmpty()) {
            throw new OreoException("To do what task exactly?.");
        }
        return command;
    }

    /** Splits merchandise input into a task number and non-empty detail.
     *
     * @param command the merchandise command argument
     * @param usage the usage message to report for invalid input
     * @return the task number and merchandise detail
     * @throws OreoException if the argument is malformed
     */
    public String[] merchandiseParts(String command, String usage) throws OreoException {
        int separator = command.indexOf(' ');
        if (separator <= 0 || separator + 1 >= command.length()
                || command.substring(separator + 1).trim().isEmpty()) {
            throw new OreoException(usage);
        }
        return new String[] {command.substring(0, separator), command.substring(separator + 1).trim()};
    }

    /** Converts a one-based task number into a zero-based index.
     *
     * @param taskNumberText the one-based task number
     * @param taskCount the number of tasks available
     * @return the corresponding zero-based index
     * @throws OreoException if the task number is invalid or out of range
     */
    public int taskIndex(String taskNumberText, int taskCount) throws OreoException {
        assert taskCount >= 0 : "A task list cannot have a negative size.";
        if (taskNumberText.isEmpty()) {
            throw new OreoException("Sooo which task is it?");
        }
        try {
            int taskIndex = Integer.parseInt(taskNumberText) - 1;
            if (taskIndex < 0 || taskIndex >= taskCount) {
                throw new OreoException("I can't find that task number.");
            }
            assert taskIndex >= 0 && taskIndex < taskCount
                    : "A successfully validated task number must be within the list.";
            return taskIndex;
        } catch (NumberFormatException e) {
            throw new OreoException("That is not a valid task number.");
        }
    }
}
