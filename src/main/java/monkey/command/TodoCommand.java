package monkey.command;

import java.util.List;

import monkey.model.Task;
import monkey.model.TaskList;
import monkey.model.ToDos;
import monkey.storage.Storage;
import monkey.ui.Ui;

/** Command object for adding a todo task. */
public class TodoCommand extends CommandAction {
    private final String description;

    public TodoCommand(String description) {
        this.description = description;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        if (description.isEmpty()) {
            ui.showMessage("OOPS! Monkey says: A todo needs a description. "
                    + "This monkey cannot fetch an invisible banana!");
            return;
        }
        ToDos todo = new ToDos(description);
        if (tasks.containsEquivalent(todo)) {
            ui.showMessage("OOPS! Monkey says: That task is already in your list.");
            return;
        }
        List<Task> proposedTasks = tasks.asList();
        proposedTasks.add(todo);
        if (!saveTasks(proposedTasks, ui, storage)) {
            return;
        }
        tasks.add(todo);
        ui.showMessage("Got it. I've added this task:");
        ui.showMessage("  " + tasks.get(tasks.size() - 1));
        ui.showMessage("Now you have " + tasks.size() + " tasks in the list.");
    }
}
