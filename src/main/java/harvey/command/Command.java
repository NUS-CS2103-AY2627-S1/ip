package harvey.command;

import harvey.HarveyException;
import harvey.storage.Storage;
import harvey.task.TaskList;
import harvey.ui.Ui;
/**
 * One instruction from the user, in a form that can carry itself out.
 * <p>
 * Previously {@link Harvey} held a switch that named every instruction and spelled out
 * what each one did. Each branch of that switch is now its own subclass, so the code for
 * an instruction sits in one place with a name on it, and adding another instruction means
 * adding a class rather than editing a growing switch.
 * <p>
 * This is an abstract class rather than an interface because {@link #isExit()} has a
 * sensible default that almost every subclass wants to inherit unchanged.
 */
public abstract class Command {
    /**
     * Carries out this instruction.
     * <p>
     * The three collaborators are passed in rather than stored, so a command holds only
     * what the user typed and can be created without knowing which task list or file it
     * will eventually act on.
     *
     * @param tasks   the task list to read or change.
     * @param ui      used to word replies that list tasks.
     * @param storage used to save the list if this command changed it.
     * @return what Harvey should say in reply, ready to be printed or shown in a window.
     * @throws HarveyException if the instruction cannot be carried out as asked.
     */
    public abstract String execute(TaskList tasks, Ui ui, Storage storage) throws HarveyException;

    /**
     * Returns whether Harvey should stop after this command.
     * Only {@link ExitCommand} overrides this, so the default is to keep going.
     *
     * @return true if this command ends the session.
     */
    public boolean isExit() {
        return false;
    }

    /**
     * Converts the argument of a {@code mark} or {@code unmark} command into a task number.
     * Whether that number names an existing task is checked by {@link TaskList}, which is
     * the class that knows how long the list is.
     *
     * @param argument  the text typed after the command word.
     * @param tasks     the current task list, used to reject a number when it is empty.
     * @param command   the command being run, used to make the error messages specific.
     * @return the task number the user typed, counting from 1.
     * @throws HarveyException if the argument is missing or is not a number.
     */
    protected static int parseTaskNumber(String argument, TaskList tasks, CommandType command)
            throws HarveyException {
        if (argument.isEmpty()) {
            throw new HarveyException("Tell me which task to " + command.getKeyword()
                    + ". For example: " + command.getExample());
        }
        if (tasks.isEmpty()) {
            throw new HarveyException("You have no tasks yet, so there is nothing to "
                    + command.getKeyword() + ".");
        }

        try {
            return Integer.parseInt(argument);
        } catch (NumberFormatException e) {
            // The user typed something that is not a number, e.g. "mark book".
            // The original exception is not shown to the user; the advice below is more useful.
            throw new HarveyException("\"" + argument + "\" is not a task number. Use the number "
                    + "shown by list, for example: " + command.getExample());
        }
    }
}
