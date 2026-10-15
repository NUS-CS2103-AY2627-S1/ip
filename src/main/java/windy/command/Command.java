package windy.command;

/**
 * Represents a command that Windy can execute.
 */
public abstract class Command {

    /**
     * Executes this command using the current application context.
     *
     * @param context the current application context.
     */
    public abstract void execute(CommandContext context);
}
