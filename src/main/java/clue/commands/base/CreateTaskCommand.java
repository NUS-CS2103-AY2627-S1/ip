package clue.commands.base;

import java.util.Map;

import clue.components.Parser;
import clue.components.TaskList;
import clue.tasks.Task;
import clue.ui.Ui;

/** A command which creates a new task */
public abstract class CreateTaskCommand<T extends Task> implements Parser.Command {
    private final TaskList tasks;
    private final String taskType;

    /** Construct a CreateTaskCommand that adds the created tasks to the given TaskList */
    public CreateTaskCommand(TaskList tasks, String taskType) {
        this.tasks = tasks;
        this.taskType = taskType;
    }

    protected abstract T makeTask(Ui ui, Map<String, String> args, String name);

    @Override
    public boolean run(Ui ui, Map<String, String> args) {
        String name = args.get(Parser.DEFAULT_ARGUMENT);
        if (name != null) {
            name = name.trim();
        }
        if (name == null || name.isEmpty()) {
            ui.printf("Please tell us what this %s is called!\n", taskType);
            return true;
        }

        T task;
        try {
            task = makeTask(ui, args, name);
        } catch (Task.InvalidTaskException e) {
            ui.println("Error creating todo: " + e.getMessage());
            return true;
        }
        if (task != null) {
            tasks.addTask(ui, task);
        }
        return true;
    }
}
