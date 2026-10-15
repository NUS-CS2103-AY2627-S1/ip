package hse.command;

import java.io.IOException;
import java.time.LocalDate;

import hse.parser.Parser;
import hse.storage.Storage;
import hse.task.TaskList;
import hse.ui.Ui;

/** Displays deadlines and events that occur on a requested date. */
public class FindDateCommand extends Command {
    private final String dateText;

    /** Creates a date-search command for the supplied date text. */
    public FindDateCommand(String dateText) {
        this.dateText = dateText;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws IOException {
        if (dateText.isEmpty()) {
            throw new IllegalArgumentException("Please provide a date to find.");
        }
        LocalDate date = Parser.parseDate(dateText);
        ui.showTasksOnDate(tasks, date);
    }
}
