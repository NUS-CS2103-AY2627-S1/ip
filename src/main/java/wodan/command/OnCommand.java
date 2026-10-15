package wodan.command;

import java.time.LocalDate;
import java.util.ArrayList;

import wodan.WodanException;
import wodan.parser.Parser;
import wodan.storage.Storage;
import wodan.task.TaskDateTime;
import wodan.task.TaskList;
import wodan.ui.Ui;

/**
 * Lists deadlines and events that occur on a given calendar date.
 */
public class OnCommand extends Command {
    private final String arguments;

    /**
     * Creates an {@code on} command from the text after {@code on}.
     *
     * @param arguments Expected to be a date.
     */
    public OnCommand(String arguments) {
        this.arguments = arguments;
    }

    /**
     * Shows deadlines and events that occur on the parsed date, with original list numbers.
     *
     * @param tasks The task list to search.
     * @param ui The user interface for the reply.
     * @param storage Unused.
     * @throws WodanException If the date is missing or cannot be parsed.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws WodanException {
        LocalDate date = Parser.parseOnDate(arguments);
        String displayDate = TaskDateTime.formatDate(date);
        ArrayList<Integer> numbers = tasks.taskNumbersOn(date);
        if (numbers.isEmpty()) {
            ui.show("    The ravens found no quests on " + displayDate + ".");
        } else {
            ui.show("    The ravens found these quests on " + displayDate + ".\n");
            for (int number : numbers) {
                ui.showNumberedTask(number, tasks.get(number - 1));
            }
        }
    }
}
