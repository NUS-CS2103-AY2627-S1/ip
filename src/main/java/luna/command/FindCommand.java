package luna.command;

import java.util.ArrayList;

import luna.exception.LunaException;
import luna.parser.Parser;
import luna.task.Task;
import luna.task.TaskList;

/**
 * Finds tasks whose descriptions contain a keyword.
 */
public class FindCommand extends Command {
    private final String commandArgs;
    private final TaskList tasks;

    /**
     * Creates a command that finds matching tasks.
     *
     * @param commandArgs User input after the command word.
     * @param tasks Task list to search.
     */
    public FindCommand(String commandArgs, TaskList tasks) {
        this.commandArgs = commandArgs;
        this.tasks = tasks;
    }

    @Override
    public String execute() throws LunaException {
        String keyword = Parser.parseKeyword(commandArgs).toLowerCase();

        ArrayList<Task> matches = new ArrayList<>();
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            String desc = task.getDescription().toLowerCase();
            if (desc.contains(keyword)) {
                matches.add(task);
            }
        }

        if (matches.isEmpty()) {
            return "(No matching tasks found.)";
        }

        StringBuilder response = new StringBuilder("Here are the matching tasks in your list:");
        for (int i = 0; i < matches.size(); i++) {
            response.append("\n")
                    .append(i + 1)
                    .append(". ")
                    .append(matches.get(i));
        }
        return response.toString();
    }
}
