package nico;

import java.util.List;

/**
 * Provides common validation for commands that identify a task by its list number.
 */
public abstract class TaskNumberCommand extends ParameterCommand {
    /**
     * Creates a command that operates on a task number.
     *
     * @param commandWord word that identifies this command in error messages.
     * @param taskNumberText user-entered one-based task number.
     */
    protected TaskNumberCommand(String commandWord, String taskNumberText) {
        super(commandWord, taskNumberText);
    }

    /**
     * Returns the requested task after validating its one-based list number.
     *
     * @param tasks tasks currently managed by the chatbot.
     * @return task identified by the supplied one-based task number
     * @throws NicoException if the task number is missing, non-numeric, or outside the list
     */
    protected Task getTask(List<Task> tasks) throws NicoException {
        String taskNumberText = getParameter();
        if (taskNumberText == null || taskNumberText.trim().isEmpty()) {
            throw NicoException.invalidInput("Choose a task number.", getCommandWord() + " TASK_NUMBER");
        }
        try {
            int taskNumber = Integer.parseInt(taskNumberText.trim());
            if (taskNumber < 1 || taskNumber > tasks.size()) {
                throw NicoException.invalidInput("That task number is not in the list.",
                        getCommandWord() + " TASK_NUMBER");
            }
            assert taskNumber >= 1 && taskNumber <= tasks.size()
                    : "a validated task number must identify an element in the task list";
            return tasks.get(taskNumber - 1);
        } catch (NumberFormatException exception) {
            throw NicoException.invalidInput("A task number must be a whole number.",
                    getCommandWord() + " TASK_NUMBER");
        }
    }
}
