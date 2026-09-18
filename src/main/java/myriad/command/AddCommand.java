package myriad.command;

import java.util.Optional;

import myriad.MyriadException;
import myriad.storage.Storage;
import myriad.task.Task;
import myriad.task.TaskList;
import myriad.ui.Ui;

/**
 * Adds a task to the list. One class serves {@code todo}, {@code deadline}
 * and {@code event} alike: {@code ToDo}, {@code Deadline} and {@code Event}
 * already differ from each other as {@link Task} subclasses, so what to add
 * is decided by the {@code Parser} when it builds the task, and three
 * near-identical add commands would only duplicate that distinction here.
 */
public class AddCommand extends Command {

    private final Task task;

    /**
     * Creates a command that adds the already-built task. The {@code Parser}
     * has decided by now which {@link Task} subclass it is, so this only has
     * to carry it.
     *
     * @param task the task to add when this command is executed.
     */
    public AddCommand(Task task) {
        assert task != null : "the Parser always builds the task before the command";
        this.task = task;
    }

    /**
     * Adds the task, shows the standard added-task acknowledgement, then
     * saves. The add and acknowledgement happen before the save is
     * attempted, so a save failure never undoes the in-memory add: the
     * task still shows up in {@code list} for the rest of the session even
     * if it couldn't be written to disk.
     *
     * A task with the same details as one already in the list is refused
     * instead, since a second copy is almost always a slip and would make
     * the list harder to read and the task numbers harder to keep track of.
     * The duplicate check happens here rather than in the {@code Parser},
     * because it depends on what the list holds when the command runs.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws MyriadException {
        Optional<Task> existing = tasks.findTaskWithSameDetails(task);
        if (existing.isPresent()) {
            throw new MyriadException("That task is already in your list: " + existing.get());
        }

        tasks.add(task);
        ui.showAddedTask(task, tasks.size());
        save(tasks, storage);
    }
}
