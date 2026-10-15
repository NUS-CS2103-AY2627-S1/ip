package ff15.command;

import java.io.IOException;

import ff15.FF15Exception;
import ff15.Storage;
import ff15.Ui;
import ff15.contact.ContactList;
import ff15.task.TaskList;

/**
 * One thing the user asked the chatbot to do.
 *
 * <p>{@link ff15.Parser} turns an input line into the matching subclass, carrying
 * whatever that command needs (a task to add, a number to delete). The subclass
 * then carries it out in {@link #execute}, so adding a new command means adding
 * a class rather than another branch to a switch.
 */
public abstract class Command {
    /**
     * Carries out this command, reporting the result through {@code ui}. Every
     * command is handed both lists, and uses whichever one it works on.
     *
     * @throws FF15Exception if the command cannot be carried out as asked.
     * @throws IOException if the tasks or contacts could not be saved afterwards.
     */
    public abstract void execute(TaskList tasks, ContactList contacts, Ui ui, Storage storage)
            throws FF15Exception, IOException;

    /** Returns whether the chatbot should stop after this command. Only exit says yes. */
    public boolean isExit() {
        return false;
    }
}
