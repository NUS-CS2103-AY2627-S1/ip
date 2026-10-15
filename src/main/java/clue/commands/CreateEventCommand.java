package clue.commands;

import static clue.Clue.parseDate;

import java.time.LocalDateTime;
import java.util.Map;

import clue.commands.base.CreateTaskCommand;
import clue.components.TaskList;
import clue.tasks.Event;
import clue.ui.Ui;

/** Command that creates new Events */
public class CreateEventCommand extends CreateTaskCommand<Event> {
    public static final String NAME = "event";
    /**
     * Constructs a CreateEventCommand that adds the created Events to the given TaskList
     *
     * @param tasks the TaskList to operate on
     */
    public CreateEventCommand(TaskList tasks) {
        super(tasks, NAME);
    }

    @Override
    protected Event makeTask(Ui ui, Map<String, String> args, String name) {
        String unparsedFrom = args.get("from");
        String unparsedTo = args.get("to");

        if (unparsedFrom == null || unparsedFrom.trim().isEmpty()) {
            ui.println("Please provide a start date using the /from argument!");
            return null;
        }

        if (unparsedTo == null || unparsedTo.trim().isEmpty()) {
            ui.println("Please provide an end date using the /to argument!");
            return null;
        }

        LocalDateTime from = parseDate(ui, unparsedFrom);
        LocalDateTime to = parseDate(ui, unparsedTo);
        if (from == null || to == null) {
            return null;
        }
        return new Event(name, from, to);
    }
}
