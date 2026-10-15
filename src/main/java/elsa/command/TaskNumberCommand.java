package elsa.command;

import elsa.ElsaException;
import elsa.task.TaskList;

/**
 * A command that names one task by the number shown in the list, such as
 * "mark 2" or "delete 3".
 *
 * <p>Three commands work this way and all need the same check, so the check is
 * written here once and inherited. The number is kept exactly as the user typed
 * it, counting from 1, and turned into a list position only when the command
 * runs.
 *
 * <p>That split is deliberate. Whether "2" is a whole number can be settled while
 * reading the line, but whether a task 2 exists depends on how many tasks there
 * are, which the parser has no way of knowing. So the parser checks the writing
 * and this class checks the meaning, at the moment the list is in front of it.
 */
public abstract class TaskNumberCommand extends Command {
    /** The task number as the user typed it, counting from 1. */
    private final int number;

    /** Which command this is, so that the errors can name it. */
    private final CommandType type;

    /**
     * Creates a command naming one task by number.
     *
     * @param number the number the user typed, counting from 1
     * @param type   the command being run, used to word the errors
     */
    protected TaskNumberCommand(int number, CommandType type) {
        this.number = number;
        this.type = type;
    }

    /**
     * Returns the position in the list that the user's number refers to,
     * having checked that a task with that number exists.
     *
     * @param tasks the list the number has to make sense in
     * @return the corresponding position, counting from 0
     * @throws ElsaException if the list is empty or has no task with that number
     */
    protected int indexIn(TaskList tasks) throws ElsaException {
        String keyword = type.getKeyword();
        if (tasks.isEmpty()) {
            throw new ElsaException("There are no tasks yet, so there is nothing to "
                    + keyword + ". Add one with \"" + CommandType.TODO.getUsage()
                    + "\" first.");
        }
        if (number < 1 || number > tasks.size()) {
            String plural = (tasks.size() == 1) ? "task" : "tasks";
            throw new ElsaException("There is no task " + number + ". You have "
                    + tasks.size() + " " + plural + ", so use a number from 1 to "
                    + tasks.size() + ".");
        }
        // The user counts from 1, so subtract 1 to get the list position.
        int index = number - 1;
        assert index >= 0 && index < tasks.size()
                : "task " + number + " gave position " + index + " in " + tasks.size();
        return index;
    }
}
