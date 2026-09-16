package will.command;

import java.util.List;
import java.util.stream.Collectors;

import will.Storage;
import will.TaskList;
import will.Ui;
import will.WillException;
import will.task.Task;

/**
 * The "todo"/"deadline"/"event" commands: adds an already-constructed
 * Task to the list. One AddCommand class serves all three task types,
 * since the only thing that differs between them is which Task
 * subclass Parser builds before wrapping it in an AddCommand.
 */
public class AddCommand extends Command {
    private final Task task;

    /**
     * Wraps an already-constructed task so execute() can add it to the list.
     *
     * @param task The already-constructed task to add.
     */
    public AddCommand(Task task) {
        this.task = task;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws WillException {
        int sizeBeforeAdd = tasks.size();
        tasks.add(task);
        // Adding a task should always grow the list by exactly one;
        // this documents that assumption right where the next line
        // relies on it (tasks.size() - 1 being the just-added task).
        assert tasks.size() == sizeBeforeAdd + 1 : "adding a task should grow the list by exactly one";
        storage.save(tasks.getTasks());
        ui.showMessage("Got it. I've added this task:");
        ui.showMessage("  " + tasks.get(tasks.size() - 1).toString());
        ui.showMessage("Now you have " + tasks.size() + " tasks in the list.");
        warnAboutClashes(tasks, ui);
    }

    /**
     * Warns the user if the just-added task's schedule clashes with any
     * other task already in the list (e.g. two events with overlapping
     * date ranges). This is a heads-up rather than a block: a clash
     * might be intentional (e.g. two optional events on the same day),
     * so the task is still added either way.
     */
    private void warnAboutClashes(TaskList tasks, Ui ui) {
        List<Task> clashes = tasks.getTasks().stream()
                .filter(other -> other != task)
                .filter(task::clashesWith)
                .collect(Collectors.toList());
        if (clashes.isEmpty()) {
            return;
        }
        String taskWord = clashes.size() == 1 ? "task" : "tasks";
        ui.showMessage("Note: this clashes with " + clashes.size() + " other " + taskWord + " already in your list:");
        for (Task clash : clashes) {
            ui.showMessage("  " + clash.toString());
        }
    }
}
