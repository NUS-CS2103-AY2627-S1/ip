package billy.command;

import java.io.IOException;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.IntStream;

import billy.BillyException;
import billy.storage.Storage;
import billy.task.Task;
import billy.task.TaskList;
import billy.ui.Ui;

/**
 * Something the user has asked Billy to do, ready to be carried out.
 *
 * <p>A command is built by {@link billy.parser.Parser Parser} from what was typed, and holds
 * whatever it needs: the task to add, the number to delete, the day to look at.
 * By the time it exists it is known to be valid, so carrying it out is simply a
 * matter of asking it to.
 *
 * <p>Each kind of command is its own subclass, which is what replaced a switch
 * over every command Billy knows. Adding a command is now a new class rather
 * than another branch in a method that grows forever, and no existing command
 * has to be touched for it.
 *
 * <p>The three helpers a command may need are handed to {@link #execute} rather
 * than held as fields, so a command carries only what makes it that command, and
 * can be built without knowing which Billy will run it.
 */
public abstract class Command {

    /**
     * Carries out this command.
     *
     * @param tasks the list to work on
     * @param ui how to tell the user what happened
     * @param storage where the list is kept between runs
     * @throws BillyException if the command cannot be carried out after all,
     *                        such as a task number that names nothing
     */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage) throws BillyException;

    /**
     * Returns whether the conversation should end after this command.
     *
     * <p>Only one command says yes, so that is the exception rather than
     * something every subclass has to answer.
     *
     * @return whether the conversation should end after this command
     */
    public boolean isExit() {
        return false;
    }

    /**
     * Returns the tasks a test picks out, each numbered as the user refers to it.
     *
     * <p>The number is the task's place in the whole list rather than in the
     * result, so a task found by {@code find} or {@code on} can be marked or
     * deleted straight away without running {@code list} first to look its
     * number up. Every command that shows part of the list makes that promise,
     * and keeping the numbering here is what stops one of them quietly breaking
     * it.
     *
     * @param tasks the list to look through
     * @param isMatch what makes a task worth showing
     * @return one line per matching task, e.g. {@code 3.[T][ ] read book}
     */
    protected static List<String> numberMatching(TaskList tasks, Predicate<Task> isMatch) {
        List<Task> all = tasks.asList();
        // Streaming the positions rather than the tasks keeps each task's number
        // available, since a stream of tasks alone could not say where each came from.
        return IntStream.range(0, all.size())
                .filter(i -> isMatch.test(all.get(i)))
                // List positions start at 0, but people count from 1.
                .mapToObj(i -> (i + 1) + "." + all.get(i))
                .toList();
    }

    /**
     * Writes the task list out, so the next run starts where this one left off.
     *
     * <p>Shared by the commands that change the list. A failure is reported
     * rather than thrown, because the change itself did work: the user should
     * still see the confirmation, alongside a warning that it will not outlive
     * this session.
     *
     * @param tasks the list to write
     * @param ui how to warn the user if writing fails
     * @param storage where to write it
     */
    protected void save(TaskList tasks, Ui ui, Storage storage) {
        try {
            storage.save(tasks.asList());
        } catch (IOException e) {
            ui.showError("I couldn't save your list to " + storage.getPath() + " ("
                    + Storage.describeFailure(e) + ").",
                    "The change is still here, but it will be lost when Billy closes.");
        }
    }
}
