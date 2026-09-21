package thaisbot.command.commands;

import java.util.List;

import thaisbot.Storage;
import thaisbot.ThaisBotException;
import thaisbot.Ui;
import thaisbot.command.Command;
import thaisbot.task.Task;
import thaisbot.task.TaskList;
import thaisbot.task.Todo;

/**
 * Command to add a todo task.
 */
public class AddTodoCommand extends Command {
    private final String description;
    private final List<String> tags;

    /**
     * Construct an AddTodoCommand.
     * @param description description of the todo
     * @param tags tags to attach to the todo
     */
    public AddTodoCommand(String description, List<String> tags) {
        this.description = description;
        this.tags = List.copyOf(tags);
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws ThaisBotException {
        Task task = new Todo(description);
        task.addTags(tags);
        tasks.add(task);
        storage.save(tasks);
        ui.showTaskAdded(task, tasks.size());
    }
}
