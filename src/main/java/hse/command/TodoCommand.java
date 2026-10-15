package hse.command;

import java.io.IOException;

import hse.storage.Storage;
import hse.task.Task;
import hse.task.TaskList;
import hse.task.ToDos;
import hse.ui.Ui;

/** Adds a todo task described by the user. */
public class TodoCommand extends Command {
    private final String description;

    /** Creates a todo command from the text after the command keyword. */
    public TodoCommand(String description) {
        this.description = description;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws IOException {
        if (description.isEmpty()) {
            throw new IllegalArgumentException("Please give the description of a todo.");
        }
        Task task = new ToDos(description);
        tasks.add(task);
        storage.save(tasks);
        ui.showAddedTask(task, tasks.size());
    }
}
