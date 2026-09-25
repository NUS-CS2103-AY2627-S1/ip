package parser;

import java.util.List;

import exception.FriedbergException;
import task.Task;

/**
 * Handles the 'find' command for Friedberg.
 * Filters the tasks by name and lists them.
 */
public class FindCommand implements Command {
    @Override
    public boolean isCommand(String userInput) {
        return ParserUtil.hasCommandWord(userInput, "find");
    }

    @Override
    public String execute(String userInput, CommandContext commandContext) throws FriedbergException {
        String tasksNameFilter = ParserUtil.parseDescriptionCommand(userInput, "find", "search query");
        List<Task> filteredTasks = commandContext.filterTasks(tasksNameFilter);
        if (filteredTasks.isEmpty()) {
            return "There are no tasks matching your search.";
        }
        StringBuilder response = new StringBuilder("Here are the matching tasks in your list:");
        for (Task task : filteredTasks) {
            response.append("\n").append(task.renderTask());
        }
        return response.toString();
    }

    @Override
    public boolean isBye() {
        return false;
    }
}
