package verity.command;

import java.time.LocalDate;
import java.util.List;

import verity.storage.Storage;
import verity.task.Task;
import verity.task.TaskList;

/**
 * Lists every deadline due, or event spanning, a given date.
 */
public class OnCommand extends Command {
    private final LocalDate date;

    /**
     * Creates a command that lists tasks occurring on the given date.
     *
     * @param date Date to filter tasks by.
     */
    public OnCommand(LocalDate date) {
        this.date = date;
    }

    /**
     * Lists every deadline due, or event spanning, this command's date.
     *
     * @param tasks {@inheritDoc}
     * @param storage {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public String execute(TaskList tasks, Storage storage) {
        List<Task> matches = tasks.getTasksOnDate(date);
        String formattedDate = date.format(Task.DATE_DISPLAY_FORMAT);
        if (matches.isEmpty()) {
            return "Nothing on " + formattedDate + ". Shocking, I know.";
        }
        return "Here's what's happening on " + formattedDate + ":\n" + formatNumberedList(matches);
    }
}
