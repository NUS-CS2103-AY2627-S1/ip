package piplupbot.command;

import piplupbot.Parser;
import piplupbot.PiplupBotException;
import piplupbot.Storage;
import piplupbot.Ui;
import piplupbot.task.Deadline;
import piplupbot.task.Event;
import piplupbot.task.Task;
import piplupbot.task.TaskList;
import piplupbot.task.Todo;

// ACKNOWLEDGEMENTS: This Java file was written with the help of Claude.

/**
 * Stores a task and confirms it to the user.
 *
 * <p>One class covers {@code todo}, {@code deadline} and {@code event} because
 * by the time a command exists the differences between them are spent: the
 * parser has already worked out which kind was typed and built the matching
 * {@link Todo}, {@link Deadline} or {@link Event}. All that is left is to add it
 * and say so, which is identical for all three -- even the confirmation, since
 * the task prints itself through its own {@code toString()}.</p>
 */
public class AddCommand extends Command {
    /** The task to store, already built and checked by {@link Parser}. */
    private final Task task;

    /**
     * Creates a command that will store the given task.
     *
     * @param task The task to remember.
     */
    public AddCommand(Task task) {
        this.task = task;
    }

    /**
     * {@inheritDoc}
     *
     * <p>The confirmation shows the task through its own {@code toString()}, so
     * the same three lines serve a todo, a deadline and an event alike -- by the
     * time this runs, which kind was typed has already been settled by
     * {@link Parser}.</p>
     *
     * <p>A task the list already holds is refused by {@link TaskList#add}
     * before anything is said, so no confirmation is shown and nothing is
     * saved.</p>
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws PiplupBotException {
        tasks.add(task);
        ui.show("Piplup! I've tucked this task under my wing:",
                "  " + task,
                describeTaskCount(tasks.size()));
        save(tasks, ui, storage);
    }
}
