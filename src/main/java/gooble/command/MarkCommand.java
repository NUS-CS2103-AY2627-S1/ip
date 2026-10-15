package gooble.command;

/** Handles marking a task complete or incomplete. */
public class MarkCommand extends Command {
    /** Creates a mark command from complete user input. */
    public MarkCommand(String input) {
        super(input);
    }

    /** Marks the selected task as complete. */
    public void execute(CommandContext context) {
        execute(input, context, true);
    }

    static void execute(String input, CommandContext context, boolean done) {
        String number = context.parser().argumentAfter(input, done ? "mark" : "unmark");
        try {
            int index = Integer.parseInt(number) - 1;
            if (!context.tasks().isValidIndex(index)) {
                context.ui().showMessage("That task number does not exist. Gooble searched everywhere, "
                        + "even under the digital couch.");
            } else if (done) {
                context.tasks().markAsDone(index);
                context.ui().showMarkedDone(context.tasks().get(index));
            } else {
                context.tasks().markAsNotDone(index);
                context.ui().showMarkedNotDone(context.tasks().get(index));
            }
        } catch (NumberFormatException e) {
            context.ui().showMessage("Please provide a valid task number. Gooble cannot mark task-shaped air.");
        }
    }
}
