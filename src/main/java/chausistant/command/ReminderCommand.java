package chausistant.command;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

import chausistant.storage.Storage;
import chausistant.task.DeadlineTask;
import chausistant.task.TaskList;
import chausistant.ui.Ui;

/** Displays incomplete deadlines due within the next seven days. */
public class ReminderCommand extends Command {
    private static final long REMINDER_HORIZON_DAYS = 7;

    private final Clock clock;

    /** Creates a reminder command that uses the computer's local clock. */
    public ReminderCommand() {
        this(Clock.systemDefaultZone());
    }

    /** Creates a reminder command using the supplied clock. */
    ReminderCommand(Clock clock) {
        assert clock != null : "A reminder command must have a clock.";
        this.clock = clock;
    }

    /** Displays incomplete deadlines due from now through seven days from now. */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        LocalDateTime now = LocalDateTime.now(clock);
        LocalDateTime end = now.plusDays(REMINDER_HORIZON_DAYS);
        List<String> deadlines = tasks.getIncompleteDeadlinesDueBetween(now, end).stream()
                .map(DeadlineTask::printTask)
                .toList();
        ui.showUpcomingDeadlines(deadlines);
    }
}
