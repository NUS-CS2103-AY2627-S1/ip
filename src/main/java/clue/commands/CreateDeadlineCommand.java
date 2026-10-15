package clue.commands;

import static clue.Clue.parseDate;

import java.time.LocalDateTime;
import java.util.Map;

import clue.commands.base.CreateTaskCommand;
import clue.components.TaskList;
import clue.tasks.Deadline;
import clue.ui.Ui;

/** Command that creates new Deadlines */
public class CreateDeadlineCommand extends CreateTaskCommand<Deadline> {
    public static final String NAME = "deadline";
    /**
     * Constructs a CreateTodoCommand that adds the created ToDos to the given TaskList
     *
     * @param tasks the TaskList to operate on
     */
    public CreateDeadlineCommand(TaskList tasks) {
        super(tasks, NAME);
    }

    @Override
    protected Deadline makeTask(Ui ui, Map<String, String> args, String name) {
        String unparsedBy = args.get("by");
        if (unparsedBy == null || unparsedBy.trim().isEmpty()) {
            ui.println("Please provide a deadline date using the /by argument!");
            return null;
        }

        LocalDateTime by = parseDate(ui, unparsedBy);
        return new Deadline(name, by);
    }
}
