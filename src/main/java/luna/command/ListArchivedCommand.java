package luna.command;

import java.io.IOException;
import java.util.List;

import luna.LunaException;
import luna.storage.Storage;
import luna.task.Task;
import luna.task.TaskList;
import luna.ui.Ui;

/**
 * Represents the command that displays all archived tasks.
 */
public class ListArchivedCommand extends Command {
    /**
     * Loads and displays the archived tasks in their storage order.
     *
     * @param tasks Current active tasks in memory.
     * @param ui User interface for showing results.
     * @param storage Storage used to load archived tasks.
     * @throws LunaException If the archive cannot be loaded.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws LunaException {
        try {
            List<Task> archivedTasks = storage.loadArchivedTasks();
            ui.showArchivedTasks(archivedTasks);
        } catch (IOException | LunaException | SecurityException e) {
            throw new LunaException("I could not load your archived tasks.");
        }
    }
}
