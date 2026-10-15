package gooble.command;

/** Handles exiting the application. */
public class ByeCommand extends Command {
    /** Creates an exit command from complete user input. */
    public ByeCommand(String input) {
        super(input);
    }

    /** Displays the exit message. */
    public void execute(CommandContext context) {
        context.ui().showBye();
    }

    @Override
    public boolean isExit() {
        return true;
    }
}
