package gooble.command;

import gooble.task.Task;

/** Handles deleting a task. */
public class DeleteCommand extends Command {
    /** Creates a delete command from complete user input. */
    public DeleteCommand(String input) {
        super(input);
    }

    /** Deletes the task identified by the command input. */
    public void execute(CommandContext context) {
        String number = context.parser().argumentAfter(input, "delete");
        try {
            int index = Integer.parseInt(number) - 1;
            if (!context.tasks().isValidIndex(index)) {
                context.ui().showMessage("That task number does not exist. Gooble searched everywhere, "
                        + "even under the digital couch.");
            } else {
                Task removed = context.tasks().remove(index);
                context.ui().showDeleted(removed, context.tasks().size());
            }
        } catch (NumberFormatException e) {
            context.ui().showMessage("Please provide a valid task number. Gooble cannot delete task-shaped air.");
        }
    }
}
