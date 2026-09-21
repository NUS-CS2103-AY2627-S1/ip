package auto.command;

import java.time.LocalDate;

import auto.storage.Storage;
import auto.task.TaskList;
import auto.util.DateUtil;

/** Displays deadlines and events occurring on a requested date. */
public class OccurCommand extends Command {
    private final LocalDate date;

    /** Creates a command that displays tasks occurring on the specified date. */
    public OccurCommand(LocalDate date) {
        this.date = date;
    }

    /** {@inheritDoc} */
    @Override
    public CommandResult execute(TaskList tasks, Storage storage) {
        return new CommandResult(formatTasks(
                "Here's what you have on " + DateUtil.format(date) + ":",
                tasks.asList(),
                task -> task.occursOn(date)));
    }
}
