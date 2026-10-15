package windy.command;

/**
 * Exits the application.
 */
public class ByeCommand extends Command {

    /**
     * Requests application exit through the command context.
     *
     * @param context the current application context.
     */
    @Override
    public void execute(CommandContext context) {
        context.requestExit();
    }
}
