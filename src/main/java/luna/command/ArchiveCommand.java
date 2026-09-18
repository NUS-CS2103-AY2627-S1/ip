package luna.command;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import luna.LunaException;
import luna.storage.Storage;
import luna.task.Task;
import luna.task.TaskList;
import luna.ui.Ui;

/**
 * Represents a command that moves one or all active tasks into the archive.
 */
public class ArchiveCommand extends Command {
    private static final String ARCHIVE_ALL_ARGUMENT = "all";
    private static final String USAGE_MESSAGE = "Please use archive <index> or archive all.";

    private final String input;

    /**
     * Creates an archive command from the full user input.
     *
     * @param input Full command entered by the user.
     */
    public ArchiveCommand(String input) {
        this.input = input;
    }

    /**
     * Archives the requested tasks and removes them from the active task list.
     *
     * @param tasks Current tasks in memory.
     * @param ui User interface for showing results.
     * @param storage Storage used to persist active and archived tasks.
     * @throws LunaException If the syntax is invalid or persistence fails.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws LunaException {
        String argument = input.substring("archive".length()).trim();
        if (argument.isEmpty()) {
            throw new LunaException(USAGE_MESSAGE);
        }

        if (argument.equalsIgnoreCase(ARCHIVE_ALL_ARGUMENT)) {
            archiveAll(tasks, ui, storage);
            return;
        }

        archiveOne(argument, tasks, ui, storage);
    }

    /**
     * Archives every active task while preserving their current order.
     */
    private void archiveAll(TaskList tasks, Ui ui, Storage storage) throws LunaException {
        if (tasks.size() == 0) {
            ui.showNoTasksToArchive();
            return;
        }

        List<Task> tasksToArchive = tasks.asList();
        persistArchive(tasksToArchive, new TaskList(), storage);
        tasks.clear();
        ui.showArchiveSuccess(tasksToArchive.size(), tasks.size());
    }

    /**
     * Archives the active task identified by a one-based index.
     */
    private void archiveOne(String argument, TaskList tasks, Ui ui, Storage storage) throws LunaException {
        if (!argument.matches("[1-9][0-9]*")) {
            throw new LunaException(USAGE_MESSAGE);
        }

        int index = parseTaskIndex("archive " + argument, "archive", tasks.size(), "archive");

        Task task = tasks.get(index);
        List<Task> remainingTasks = new ArrayList<>(tasks.asList());
        remainingTasks.remove(index);

        persistArchive(List.of(task), new TaskList(remainingTasks), storage);
        tasks.remove(index);
        ui.showArchiveSuccess(1, tasks.size());
    }

    /**
     * Writes archive entries before saving the corresponding active-task list.
     */
    private void persistArchive(List<Task> tasksToArchive, TaskList remainingTasks, Storage storage)
            throws LunaException {
        try {
            storage.archiveTasks(tasksToArchive);
        } catch (IOException | LunaException | SecurityException e) {
            throw new LunaException("I could not archive your tasks to disk.");
        }

        try {
            storage.saveTasks(remainingTasks);
        } catch (IOException | SecurityException e) {
            throw new LunaException("Archiving was saved, but I could not update your active task file. "
                    + "Your active list was left unchanged.");
        }
    }

    /**
     * Returns whether this command changes persisted task data.
     *
     * @return Always {@code true} for an archive command.
     */
    @Override
    public boolean isMutating() {
        return true;
    }
}
