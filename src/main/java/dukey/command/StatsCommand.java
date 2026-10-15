package dukey.command;

import java.time.Clock;
import java.time.LocalDate;

import dukey.exception.DukeyException;
import dukey.storage.Storage;
import dukey.task.TaskList;
import dukey.ui.Ui;

/**
 * Shows statistics about tasks in the task list.
 */
public class StatsCommand extends Command {
    private final Clock clock;

    /**
     * Creates a command that reports task statistics using the system clock.
     */
    public StatsCommand() {
        this(Clock.systemDefaultZone());
    }

    /**
     * Creates a command that reports task statistics using the given clock.
     *
     * @param clock Clock used to decide the current calendar week and month.
     */
    public StatsCommand(Clock clock) {
        this.clock = clock;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws DukeyException {
        ui.showStatistics(tasks, LocalDate.now(this.clock));
    }

    @Override
    public String getResponseStyleClass() {
        return STYLE_STATS;
    }
}
