package maggigorengayam.command;

import java.util.List;
import java.util.stream.Collectors;

import maggigorengayam.storage.Storage;
import maggigorengayam.task.Task;
import maggigorengayam.tasklist.TaskList;
import maggigorengayam.ui.Ui;

/** The {@code find <keyword>} command: shows tasks whose description contains the given keyword. */
public class FindCommand extends Command {
    private final String keyword;

    /** Shows tasks whose description contains the given keyword once {@link #execute} runs. */
    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Filters {@code tasks} down to those whose description contains
     * {@code keyword} (case-insensitive) and returns them as a message.
     */
    @Override
    public String execute(TaskList tasks, Ui ui, Storage storage) {
        List<Task> matches = tasks.getAll().stream()
                .filter(task -> task.getDescription().toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toList());
        return ui.showMatchingTasks(matches);
    }
}
