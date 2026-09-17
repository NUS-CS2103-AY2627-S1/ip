package notmarth.command;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.IntStream;

import notmarth.model.ContactList;
import notmarth.model.Deadline;
import notmarth.model.Event;
import notmarth.model.Task;
import notmarth.model.TaskList;
import notmarth.parser.DateTimeParser;
import notmarth.storage.Storage;
import notmarth.ui.Ui;

/** Executes a command that displays deadlines and events for a date. */
public final class OnCommand extends Command {
    private final LocalDate date;

    /**
     * Creates a date-query command.
     *
     * @param date the date to inspect.
     */
    public OnCommand(LocalDate date) {
        this.date = date;
    }

    /**
     * Displays deadlines and events that match the command's requested date.
     *
     * @param tasks the task list to search.
     * @param ui the console interaction handler.
     * @param storage the task archive handler, which is unused.
     */
    @Override
    public void execute(TaskList tasks, ContactList contacts, Ui ui, Storage storage) {
        String displayDate = DateTimeParser.format(date.atStartOfDay(), false);
        List<Integer> matchingIndexes = IntStream.range(0, tasks.size())
                .filter(index -> isMatch(tasks.get(index)))
                .boxed()
                .toList();
        if (matchingIndexes.isEmpty()) {
            ui.showNoDateTasks(displayDate);
            return;
        }
        ui.showDateTasksHeader(displayDate);
        matchingIndexes.forEach(index -> ui.showNumberedTask(index + 1, tasks.get(index)));
    }

    private boolean isMatch(Task task) {
        return task instanceof Deadline deadline && deadline.isDueOn(date)
                || task instanceof Event event && event.isOccurringOn(date);
    }
}
