package es;

/** Command action that terminates the chatbot loop. */
public class ExitCommand extends CommandAction {
    @Override
    public void execute() {
        // The exit command has no work beyond signalling termination.
    }

    @Override
    public boolean isExit() {
        return true;
    }
}
