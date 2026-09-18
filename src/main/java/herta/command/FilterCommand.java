package herta.command;

import java.time.LocalDate;

import herta.parser.DateTimeParser;
import herta.storage.Storage;
import herta.task.TaskList;
import herta.ui.UiOutput;

/**
 * Represents the command that displays deadlines and events occurring on a date.
 */
public class FilterCommand extends QueryCommand {
    private static final String FILTER_HEADING =
            "Here's what's scheduled for %s. Try not to miss it:";
    private static final String FILTER_EMPTY_MESSAGE =
            "No tasks on %s. A remarkably empty date.";
    private final LocalDate date;

    /**
     * Creates a command that filters tasks for a specific date.
     *
     * @param date the date on which matching tasks must occur.
     */
    public FilterCommand(LocalDate date) {
        this.date = date;
    }

    /**
     * Displays tasks occurring on the command's date.
     *
     * @param tasks the task list to search.
     * @param ui the output interface used to display responses.
     * @param storage unused because filtering does not change stored data.
     */
    @Override
    public void execute(TaskList tasks, UiOutput ui, Storage storage) {
        String displayDate = DateTimeParser.formatDateForDisplay(date);
        showMatchingTasks(tasks, task -> task.occursOn(date),
                FILTER_HEADING.formatted(displayDate),
                FILTER_EMPTY_MESSAGE.formatted(displayDate), ui);
    }
}
