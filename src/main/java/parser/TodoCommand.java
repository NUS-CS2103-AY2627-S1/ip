package parser;

import exception.FriedbergException;
import task.Task;
import task.ToDo;

/**
 * Handles the 'todo' command for Friedberg.
 * Adds a todo to the list of tasks.
 */
public class TodoCommand implements Command {
    @Override
    public boolean isCommand(String userInput) {
        return ParserUtil.hasCommandWord(userInput, "todo");
    }

    @Override
    public String execute(String userInput, CommandContext commandContext) throws FriedbergException {
        String taskName = ParserUtil.parseDescriptionCommand(userInput, "todo", "task description");
        Task task = new ToDo(taskName);
        commandContext.addTask(task);
        return TaskResponseFormatter.formatAddition(task, commandContext.getTasksSize());
    }

    @Override
    public boolean isBye() {
        return false;
    }
}
