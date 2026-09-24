package sage.command;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import sage.exception.SageException;
import sage.model.Deadline;
import sage.model.Event;
import sage.model.Task;
import sage.model.TaskList;
import sage.storage.Storage;
import sage.ui.Ui;

/** Command that reports groups of duplicate tasks. */
public class DuplicatesCommand extends Command {
    private final String arguments;

    /**
     * Creates a duplicates command with the supplied arguments.
     *
     * @param arguments arguments supplied after the command name
     */
    public DuplicatesCommand(String arguments) {
        this.arguments = arguments;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws IOException, SageException {
        if (!arguments.isBlank()) {
            throw new SageException("The duplicates command does not accept arguments. "
                    + "Please use `duplicates`.");
        }

        Map<String, List<Integer>> duplicateGroups = findDuplicateGroups(tasks);
        if (duplicateGroups.isEmpty()) {
            ui.show("There are no duplicate tasks.");
            return;
        }

        ui.show("Here are the duplicate task groups in your list:");
        for (List<Integer> group : duplicateGroups.values()) {
            ui.show("Group:");
            for (int taskNumber : group) {
                ui.show(taskNumber + "." + tasks.get(taskNumber - 1));
            }
        }
    }

    /** Finds task groups containing at least two tasks with the same duplicate key. */
    private Map<String, List<Integer>> findDuplicateGroups(TaskList tasks) {
        Map<String, List<Integer>> groups = new LinkedHashMap<>();
        for (int index = 0; index < tasks.size(); index++) {
            String key = getDuplicateKey(tasks.get(index));
            groups.computeIfAbsent(key, unused -> new ArrayList<>()).add(index + 1);
        }
        groups.values().removeIf(group -> group.size() < 2);
        return groups;
    }

    /** Builds a key from the normalized description and task-specific time range. */
    private String getDuplicateKey(Task task) {
        String timeRange = "";
        if (task instanceof Deadline) {
            timeRange = ((Deadline) task).getBy().toString();
        } else if (task instanceof Event) {
            Event event = (Event) task;
            timeRange = event.getFrom() + "|" + event.getTo();
        }
        return task.getDescription().trim().replaceAll("\\s+", " ").toLowerCase()
                + "|" + timeRange;
    }
}
