package parser;

import exception.FriedbergException;
import task.Deadline;
import task.Task;

/**
 * Handles the 'deadline' command for Friedberg.
 * Adds a deadline to the list of tasks.
 */
public class DeadlineCommand implements Command {
    @Override
    public boolean isCommand(String userInput) {
        return ParserUtil.hasCommandWord(userInput, "deadline");
    }

    @Override
    public String execute(String userInput, CommandContext commandContext) throws FriedbergException {
        ParserUtil.ParsedDeadline deadline = ParserUtil.parseDeadlineCommand(userInput);
        Task task = new Deadline(deadline.description(), deadline.byDate());
        commandContext.addTask(task);
        return TaskResponseFormatter.formatAddition(task, commandContext.getTasksSize());
    }

    @Override
    public boolean isBye() {
        return false;
    }
}
