package miku.command;

/** Ends the current Miku session after showing a farewell message. */
public class ExitCommand extends Command {
    /** Displays the farewell message. */
    @Override
    public String execute(CommandContext commandContext) {
        return commandContext.responseFormatter().formatGoodbye();
    }

    /** Indicates that this command ends the application session. */
    @Override
    public boolean isExit() {
        return true;
    }
}
