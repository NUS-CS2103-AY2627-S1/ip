package zeus.command;

import zeus.exception.ZeusException;

/** Represents an operation that Zeus can execute. */
public abstract class Command {
    /** Creates a command. */
    public Command() {
    }

    /**
     * Executes this command using the supplied application context.
     * @param context application components available to the command
     * @throws ZeusException if the command cannot be completed
     */
    public abstract void execute(CommandContext context) throws ZeusException;

    /**
     * Reports whether this command should end the command loop.
     * @return true only for an exit command
     */
    public boolean isExit() {
        return false;
    }
}
