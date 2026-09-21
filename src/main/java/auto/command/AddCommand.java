package auto.command;

import auto.storage.Storage;
import auto.task.Task;
import auto.task.TaskList;

/** Adds a parsed task and persists the updated list. */
public class AddCommand extends Command {
    private final Task task;

    /** Creates a command that adds the specified task. */
    public AddCommand(Task task) {
        this.task = task;
    }

    /** {@inheritDoc} */
    @Override
    public CommandResult execute(TaskList tasks, Storage storage) {
        tasks.add(task);
        if (!saveTasks(tasks, storage)) {
            tasks.removeLast();
            return storageFailure();
        }
        return new CommandResult(String.format(
                "Can! Added this task for you:%n  %s%nYou now have %d tasks on your list. Slowly clear, can one.",
                task, tasks.size()));
    }
}
