package morgan.command;

import java.time.LocalDate;

import morgan.storage.Storage;
import morgan.task.TaskList;
import morgan.ui.Ui;

/**
 * Represents a command in the application.
 */
public class FindDateCommand extends Command {
    private final LocalDate targetDate;

    /**
     * Constructs find command with the given date.
     *
     * @param targetDate The date entered by the user.
     */
    public FindDateCommand(LocalDate targetDate) {
        this.targetDate = targetDate;
    }

    /**
     * Executes the find command using the given tasks, ui, and storage.
     *
     * @param tasks The list of tasks.
     * @param ui The ui.
     * @param storage The storage file.
     * @return False.
     */
    @Override
    public boolean execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showToUser(" Meow~ Here are the fish swimming on " + targetDate + ":");
        var matchingTasks = tasks.findTasksOnDate(targetDate);
        for (int i = 0; i < matchingTasks.size(); i++) {
            ui.showToUser(String.format("   %d.%s", i + 1, matchingTasks.get(i)));
        }
        if (matchingTasks.isEmpty()) {
            ui.showToUser("   No fish caught on this day, Meow! (=T w T=)");
        }
        return false;
    }
}
