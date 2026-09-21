package echo.command;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import echo.storage.Storage;
import echo.task.TaskList;
import echo.ui.Ui;

/**
 * Shows global and current-week task completion statistics.
 */
public class StatsCommand extends Command {
    /**
     * Shows statistics without changing or saving the task list.
     * See: {@link Command#execute(TaskList, Ui, Storage)}
     */
    @Override
    public String execute(TaskList taskList, Ui ui, Storage storage) {
        LocalDateTime currentTime = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        return ui.joinLines(taskList.getStatistics(currentTime));
    }
}
