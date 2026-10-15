package piplupbot.command;

import piplupbot.Parser;
import piplupbot.PiplupBotException;
import piplupbot.Storage;
import piplupbot.Ui;
import piplupbot.task.Task;
import piplupbot.task.TaskList;

// ACKNOWLEDGEMENTS: This Java file was written with the help of Claude.

/**
 * Sets the done status of a task and confirms it to the user.
 *
 * <p>One class covers both {@code mark} and {@code unmark} because they differ
 * only in the value stored and the wording of the confirmation. Which of the two
 * was typed is settled once, by {@link Parser}, and remembered here as
 * {@link #isTaskDone}; two subclasses would repeat the whole method to change
 * one boolean and one sentence.</p>
 */
public class MarkCommand extends Command {
    /** The task's position as shown by {@code list}, counting from 1. */
    private final int taskNumber;

    /** {@code true} to mark the task done, {@code false} to reverse it. */
    private final boolean isTaskDone;

    /**
     * Creates a command that will set a task's done status.
     *
     * @param taskNumber The task's position as shown by {@code list}, counting from 1.
     * @param isTaskDone {@code true} to mark the task done, {@code false} to reverse it.
     */
    public MarkCommand(int taskNumber, boolean isTaskDone) {
        this.taskNumber = taskNumber;
        this.isTaskDone = isTaskDone;
    }

    /**
     * {@inheritDoc}
     *
     * <p>Serves both {@code mark} and {@code unmark}: {@link #isTaskDone}
     * decides the status stored and the wording of the confirmation alike, which
     * is what lets one method answer for the two commands.</p>
     *
     * <p>A task that already has the status asked for is refused rather than
     * confirmed. Nothing would change, so "I've marked this task as done" would
     * claim work that was never done -- and seeing which task it was may show
     * that the wrong number was typed.</p>
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws PiplupBotException {
        // get() refuses a number that names no task, so the lines below can assume
        // there is one; the refusal reaches the user as a reply, not a crash.
        Task task = tasks.get(taskNumber);
        if (task.isDone() == isTaskDone) {
            String refusal = isTaskDone
                    ? "Pip... This task is already marked as done:"
                    : "Pip... This task is not marked as done yet:";
            throw new PiplupBotException(refusal, "  " + task);
        }

        if (isTaskDone) {
            task.markAsDone();
        } else {
            task.markAsNotDone();
        }

        String confirmation = isTaskDone
                ? "Piplup! One more fish in the bucket. I've marked this task as done:"
                : "Pip-pip, no rush! I've marked this task as not done yet:";
        ui.show(confirmation, "  " + task);
        save(tasks, ui, storage);
    }
}
