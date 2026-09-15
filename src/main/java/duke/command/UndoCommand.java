package duke.command;

import duke.exception.GaryException;
import duke.task.TaskList;
import duke.ui.Ui;

/**
 * Reverses the most recent change to the task list.
 */
public class UndoCommand extends Command {
    /** Raw text supplied after the undo command word. */
    private final String arguments;

    /**
     * Creates an undo command.
     *
     * @param arguments Raw text supplied after the command word.
     */
    public UndoCommand(String arguments) {
        this.arguments = arguments;
    }

    /**
     * Reverses one task-list change if the command has no arguments and history is available.
     *
     * @param tasks Task list whose most recent change should be reversed.
     * @param ui UI used to show the result.
     * @throws GaryException If arguments are supplied or there is no change to undo.
     */
    @Override
    public void execute(TaskList tasks, Ui ui) {
        if (!arguments.isBlank()) {
            throw new GaryException("The undo command does not take any arguments.");
        }
        if (!tasks.undo()) {
            throw new GaryException("There is no change to undo.");
        }

        ui.showMessage("Undid the last change.");
    }
}
