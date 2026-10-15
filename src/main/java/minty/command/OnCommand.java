package minty.command;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import minty.exception.MintyException;
import minty.storage.Storage;
import minty.task.Task;
import minty.task.TaskList;
import minty.ui.Ui;

/**
 * Displays dated tasks occurring on a requested date.
 */
public class OnCommand extends Command {
    private static final DateTimeFormatter DISPLAY_DATE_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);

    private final String fullCommand;

    /**
     * Creates a date-query command from the user's complete input.
     *
     * @param fullCommand complete {@code on} command.
     */
    public OnCommand(String fullCommand) {
        this.fullCommand = fullCommand;
    }

    /**
     * Parses the requested date and displays every matching deadline and event.
     *
     * @param tasks task list to search.
     * @param ui command-line interface used for the response.
     * @param storage task storage, which is not used.
     * @throws MintyException if the date is missing or invalid.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws MintyException {
        LocalDate date = Parser.parseOnDate(fullCommand);
        ui.showMessage("Here's your lineup for "
                + date.format(DISPLAY_DATE_FORMAT) + ":");

        int matchCount = 0;
        for (Task task : tasks.findOn(date)) {
            matchCount++;
            ui.showNumberedTask(matchCount, task);
        }
        if (matchCount == 0) {
            ui.showMessage("No deadlines or events on this date. A little breathing room!");
        }
    }
}
