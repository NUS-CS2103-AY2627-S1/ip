package gongrilla.command;

import java.io.IOException;

import gongrilla.exception.GongrillaException;
import gongrilla.storage.Storage;
import gongrilla.task.Deadline;
import gongrilla.task.Event;
import gongrilla.task.Task;
import gongrilla.task.TaskList;
import gongrilla.ui.Ui;

/**
 * Adds a task to the task list and records the addition in storage.
 */
public class AddCommand extends Command {
    private final Task task;

    /**
     * Creates a command that adds the supplied task.
     *
     * @param task task that should be added.
     */
    public AddCommand(Task task) {
        this.task = task;
    }

    /**
     * Saves and adds the task, then displays the result.
     *
     * @param tasks task list to modify.
     * @param ui user interface used to display the added task.
     * @param storage storage used to persist the addition.
     * @throws GongrillaException if the command cannot be completed.
     * @throws IOException if the addition cannot be saved.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage)
            throws GongrillaException, IOException {
        String normalizedName = task.getName().strip().replaceAll("(?U)\\s+", " ");
        for (Task existing : tasks.asList()) {
            if (hasSameDetails(existing, normalizedName)) {
                throw new GongrillaException("Gongrilla already have that task. Use list to find it.");
            }
        }
        String taskTypeName = switch (task.getType()) {
            case TODO -> "todo";
            case DEADLINE -> "deadline";
            case EVENT -> "event";
        };

        storage.appendAdd(task);
        tasks.add(task);
        ui.showAddedTask(taskTypeName, task, tasks.size());
    }

    /** Compares type, normalized description and dates, ignoring completion state. */
    private boolean hasSameDetails(Task existing, String normalizedName) {
        String existingName = existing.getName().strip().replaceAll("(?U)\\s+", " ");
        if (existing.getType() != task.getType() || !existingName.equalsIgnoreCase(normalizedName)) {
            return false;
        }
        if (task instanceof Deadline deadline && existing instanceof Deadline other) {
            return deadline.getBy().equals(other.getBy());
        }
        if (task instanceof Event event && existing instanceof Event other) {
            return event.getFrom().equals(other.getFrom()) && event.getTo().equals(other.getTo());
        }
        return true;
    }
}
