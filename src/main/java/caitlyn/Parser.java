package caitlyn;

/**
 * Converts complete user commands into executable command objects.
 */
public final class Parser {
    /**
     * Prevents construction of this utility class.
     */
    private Parser() {
    }

    /**
     * Parses one complete command before dispatching it to a command object.
     *
     * @param fullCommand the command entered by the user.
     * @return the command object representing the input.
     */
    public static Command parse(String fullCommand) {
        return parse(fullCommand, new TaskStorage());
    }

    /**
     * Parses a command whose changes will be saved through the supplied storage.
     *
     * @param fullCommand the command entered by the user.
     * @param storage the destination for task changes.
     * @return the command object representing the input.
     */
    static Command parse(String fullCommand, TaskStorage storage) {
        String command = fullCommand.trim();
        if ("bye".equals(command)) {
            return new ExitCommand();
        } else if ("list".equals(command)) {
            return new ListCommand();
        } else if (hasCommandName(command, "find")) {
            return new FindCommand(command.substring("find".length()).trim());
        } else if (hasCommandName(command, "mark")) {
            return new MarkCommand(command, true, storage);
        } else if (hasCommandName(command, "unmark")) {
            return new MarkCommand(command, false, storage);
        } else if (hasCommandName(command, "delete")) {
            return new DeleteCommand(command, storage);
        } else if (hasCommandName(command, "todo")) {
            return new TodoCommand(command.substring("todo".length()).trim(), storage);
        } else if (hasCommandName(command, "deadline")) {
            return new DeadlineCommand(command.substring("deadline".length()).trim(), storage);
        } else if (hasCommandName(command, "event")) {
            return new EventCommand(command.substring("event".length()).trim(), storage);
        } else if (hasCommandName(command, "within")) {
            return new WithinCommand(command.substring("within".length()).trim(), storage);
        }
        return new UnknownCommand();
    }

    /** Matches a complete command name followed by a space or tab, or with no arguments. */
    private static boolean hasCommandName(String command, String name) {
        return command.equals(name) || command.startsWith(name + " ") || command.startsWith(name + "\t");
    }
}
