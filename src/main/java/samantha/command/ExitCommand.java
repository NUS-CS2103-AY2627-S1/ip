package samantha.command;


/**
 * Represents the command that ends Samantha's command loop.
 */
public class ExitCommand extends Command {

    /**
     * Creates a command that ends the application loop.
     */
    public ExitCommand() {
    }

    /**
     * Performs no task operation because the application loop handles shutdown.
     *
     * @param context current application state and persistence handlers
     * @return an empty response because the application handles farewell output
     */
    @Override
    public String execute(CommandContext context) {
        // The application loop ends after checking isExit().
        return "";
    }

    /**
     * Indicates that the application should exit.
     *
     * @return {@code true}
     */
    @Override
    public boolean isExit() {
        return true;
    }
}
