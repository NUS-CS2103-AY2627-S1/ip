package dobby.command;

import dobby.logic.DobbyLogic;

/**
 * Ends the Dobby application loop.
 */
public final class ExitCommand extends Command {
    /** This command has no task-list work to perform before exit. */
    @Override
    public String execute(DobbyLogic logic) {
        return "";
    }

    /** Returns that Dobby should exit after this command. */
    @Override
    public boolean isExit() {
        return true;
    }
}
