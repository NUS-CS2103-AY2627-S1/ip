package ekud.command;

import java.time.LocalDate;
import java.util.List;

import ekud.EkudException;
import ekud.storage.Storage;
import ekud.task.Task;
import ekud.task.TaskDateTime;
import ekud.task.TaskList;
import ekud.ui.Ui;

/**
 * Lists deadlines and events that occur on a given date.
 * Todos are never included. This command does not change the save file.
 */
public class OnCommand implements Command {
    private final LocalDate date;

    /**
     * Creates a command that will list dated tasks on {@code date}.
     *
     * @param date the calendar date to match
     */
    private OnCommand(LocalDate date) {
        this.date = date;
    }

    /**
     * Parses {@code on DATE}, where {@code DATE} uses the same formats as
     * deadline and event dates. Only the calendar date is used for matching.
     *
     * @param arguments text after the command word
     * @return an on command for that date
     * @throws EkudException if the date is missing or not a supported format
     */
    public static OnCommand parse(String arguments) throws EkudException {
        if (arguments.isBlank()) {
            throw new EkudException("Please provide a date, e.g. on 2019-12-02.");
        }
        TaskDateTime when = TaskDateTime.parse(arguments);
        return new OnCommand(when.toLocalDate());
    }

    /**
     * Shows deadlines due on this date and events whose range includes it.
     *
     * @param tasks   the list to search
     * @param ui      used to show the matches
     * @param storage unused; this command does not change the list
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        List<Task> matches = tasks.findOccurringOn(date);
        ui.showTasksOn(date, matches);
    }
}
