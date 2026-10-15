package gooble.command;

import gooble.GoobleException;
import gooble.task.Deadline;
import gooble.task.DeadlineDateParser;

/** Handles adding a deadline task. */
public class DeadlineCommand extends Command {
    /** Creates a deadline command from complete user input. */
    public DeadlineCommand(String input) {
        super(input);
    }

    /** Adds a deadline task described by the command input. */
    public void execute(CommandContext context) throws GoobleException {
        String[] parts = context.parser().parseDeadline(input);
        DeadlineDateParser.DeadlineDate date = DeadlineDateParser.parse(parts[1]);
        try {
            context.tasks().add(new Deadline(parts[0], date));
        } catch (IllegalArgumentException e) {
            throw new GoobleException(e.getMessage());
        }
        context.ui().showAdded(context.tasks().get(context.tasks().size() - 1));
        if (DeadlineDateParser.isValentinesDay(date)) {
            context.ui().showMessage("Love is in the air~");
        }
        if (DeadlineDateParser.isChineseNewYear(date)) {
            context.ui().showMessage("\u606d\u559c\u53d1\u8d22\uff01\uff01");
        }
        context.ui().showTaskCount(context.tasks().size());
    }
}
