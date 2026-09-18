package hermes.command;

import hermes.HermesException;
import hermes.task.LogBook;

/**
 * One instruction from the user, parsed and ready to carry out.
 *
 * <p>A Command holds whatever the user supplied — a task, an index, a date —
 * already checked and converted, so carrying it out cannot fail for want of
 * valid input. Parser decides which Command to build; Hermes
 * runs it without knowing which one it has.
 */
public abstract class Command {

    /**
     * Carries out this command and shows the result.
     *
     * @param logBook the tasks to act on.
     * @throws HermesException if the command could not be completed.
     */
    public abstract String execute(LogBook logBook) throws HermesException;

    /**
     * Reports whether Hermes should terminate after this command.
     *
     * @return false for every command except {@link ByeCommand}.
     */
    public boolean isExit() {
        return false;
    }
}
