package gooble.command;

import gooble.GoobleException;

/** Handles unrecognized commands. */
public class UnknownCommand extends Command {
    /** Creates an unknown command from complete user input. */
    public UnknownCommand(String input) {
        super(input);
    }

    @Override
    public void execute(CommandContext context) throws GoobleException {
        throw new GoobleException("Invalid command. Gooble is confused, but not offended. "
                + "Type help to see what I understand.");
    }
}
