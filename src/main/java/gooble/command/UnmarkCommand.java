package gooble.command;

/** Handles marking a task incomplete. */
public class UnmarkCommand extends Command {
    /** Creates an unmark command from complete user input. */
    public UnmarkCommand(String input) {
        super(input);
    }

    /** Marks the selected task as incomplete. */
    public void execute(CommandContext context) {
        MarkCommand.execute(input, context, false);
    }
}
