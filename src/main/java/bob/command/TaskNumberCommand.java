package bob.command;

import bob.BobException;
import bob.task.TaskList;

/**
 * Represents a command that acts on the one task the user named by its number.
 *
 * <p>{@link MarkCommand}, {@link DeleteCommand} and {@link EditCommand} all take a
 * number, and all have to answer the same question before they can do anything: is
 * there a task with that number? {@link bob.parser.Parser Parser} cannot answer it, because it
 * reads text and has no list to count; so the check belongs to the commands, and
 * being the same check in each it belongs to a parent they share.
 *
 * <p>Only the checking is shared. What to do with the task once it has been
 * found is what tells the commands apart, and that stays in each of them.
 */
public abstract class TaskNumberCommand extends Command {

    /** The task number as the user typed it, counting from 1. */
    private final int taskNumber;

    /** The command to name in any complaint about that number. */
    private final CommandWord word;

    /**
     * Creates a command acting on one numbered task.
     *
     * @param taskNumber the number the user typed, counting from 1, which may
     *                   still name no task at all.
     * @param word       the command to name in any error message. It is passed as
     *                   a {@link CommandWord} rather than as its keyword, so a
     *                   subclass cannot name a command that does not exist.
     */
    protected TaskNumberCommand(int taskNumber, CommandWord word) {
        this.taskNumber = taskNumber;
        this.word = word;
    }

    /**
     * Returns the position of the task this command names, having first checked
     * that the list holds one with that number.
     *
     * <p>An empty list is complained about separately from a number that is
     * merely too big, because the two are different mistakes: one user has
     * nothing to act on yet, the other has miscounted.
     *
     * @param tasks the list to look in.
     * @return the position of that task, counting from 0.
     * @throws BobException if the list is empty, or holds no task with that number.
     */
    protected int requireTaskIndex(TaskList tasks) throws BobException {
        String keyword = word.getKeyword();
        String listCommand = CommandWord.LIST.getKeyword();
        if (tasks.isEmpty()) {
            throw new BobException("There is nothing to " + keyword
                    + " yet — your list is empty.");
        }
        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new BobException("I don't have a task numbered " + taskNumber + "."
                    + "\nYour list runs from 1 to " + tasks.size() + "; type " + listCommand
                    + " to see it.");
        }
        // The user counts from 1, the list counts from 0.
        int index = taskNumber - 1;
        // The whole point of this method is to hand back a position the list holds,
        // so the two checks above have to have left one.
        assert index >= 0 && index < tasks.size()
                : "Position " + index + " is outside a list of " + tasks.size() + " tasks";
        return index;
    }
}
