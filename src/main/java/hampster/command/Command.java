package hampster.command;


import hampster.exception.HampsterException;
import hampster.task.TaskList;
import hampster.ui.Ui;

/** Represents an instruction that can be executed by Hampster. */
public abstract class Command {

    public Command() {
    }

    /** Parses and validates the one-based task number shared by task commands. */
    protected static int parseTaskNumber(String[] parts, String commandName) throws HampsterException {
        if (parts.length != 2) {
            throw new HampsterException(
                    commandName + " requires exactly one task number, minion. Try: " + commandName.toLowerCase()
                            + " <task number>");
        }

        try {
            int taskNumber = Integer.parseInt(parts[1]);
            if (taskNumber < 1) {
                throw new HampsterException(commandName + " cannot conquer a negative task number.");
            }
            return taskNumber;
        } catch (NumberFormatException exception) {
            throw new HampsterException("'" + parts[1] + "' is not a task number. My minions must use digits.");
        }
    }

    /** Executes this command against the task list and user interface. */
    public abstract void execute(TaskList tasks, Ui ui) throws HampsterException;
}
