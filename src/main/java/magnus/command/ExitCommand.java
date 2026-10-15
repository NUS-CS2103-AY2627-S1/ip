package magnus.command;

import magnus.exception.CommandSyntaxException;

/**
 * Displays Magnus's farewell message.
 */
public class ExitCommand implements Command {
    private static final String EXIT_TEXT =
            "\tThe board is set aside for now. Goodbye, and see you next game!";

    /**
     * Creates a command that displays Magnus's farewell message.
     */
    public ExitCommand() {
    }

    /**
     * Creates the farewell message.
     *
     * @param args The command arguments.
     * @return The farewell message.
     */
    @Override
    public String execute(String... args) throws CommandSyntaxException {
        if (args.length > 0) {
            throw new CommandSyntaxException("\tThat move has extra pieces - the bye command does not accept "
                    + "arguments.\n"
                    + "\tUsage: bye");
        }
        return EXIT_TEXT;
    }
}
