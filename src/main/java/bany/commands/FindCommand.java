package bany.commands;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import bany.TaskFileRepository;
import bany.TaskStorage;
import bany.gui.Responder;
import bany.tasks.Task;

/** Finds tasks whose descriptions contain the search text, preserving their current list numbers. */
public class FindCommand extends Command {
    /** Parsed command values containing the search description. */
    private final Map<String, String> values;

    /**
     * Creates a find command from parsed command values.
     *
     * @param values values extracted from the user's input.
     */
    public FindCommand(Map<String, String> values) {
        this.values = values;
    }

    /**
     * Searches task descriptions and returns either matches or an error response.
     * Matching is case-sensitive. Each match keeps its one-based position in the
     * complete task list so that task commands act on the displayed task number.
     *
     * @return command outcome containing matching tasks or an error message.
     */
    @Override
    public CommandResult execute(TaskStorage tasks, Responder responder,
                        TaskFileRepository repository) {
        String query = values.get("description");

        if (query == null || query.isBlank()) {
            return CommandResult.error(
                    ResponseMessage.error(
                            responder.respondInvalidCommand()));
        }

        Map<Integer, Task> matches = new LinkedHashMap<>();
        List<Task> allTasks = tasks.getTasks();
        for (int index = 0; index < allTasks.size(); index++) {
            Task task = allTasks.get(index);
            if (task.getDescription().contains(query)) {
                matches.put(index + 1, task);
            }
        }

        if (matches.isEmpty()) {
            return CommandResult.error(
                    ResponseMessage.error(
                            responder.respondNoSuchTask()));
        } else {
            return CommandResult.success(
                    ResponseMessage.info(responder.respondMatchedTasks(matches)));
        }
    }
}
