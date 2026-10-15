package zabud.commands;

import zabud.session.Session;

/**
 * Defines an executable action built from validated command input.
 */
public abstract class Command {
    /**
     * The session this command may read or update.
     */
    protected final Session session;

    /**
     * Creates a command with the session it will operate on.
     *
     * @param session the current session.
     */
    protected Command(Session session) {
        this.session = session;
    }

    /**
     * Executes this command's effect.
     */
    public abstract void execute();

    /**
     * Returns whether this command ends the application session.
     *
     * @return whether Zabud should stop processing input after this command.
     */
    protected boolean exitsApplication() {
        return false;
    }
}
