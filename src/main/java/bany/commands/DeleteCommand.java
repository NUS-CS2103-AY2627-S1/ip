package bany.commands;

import java.io.IOException;
import java.util.Map;

import bany.TaskFileRepository;
import bany.TaskStorage;
import bany.gui.Responder;
import bany.tasks.Task;

/** Deletes a numbered task from Bany's task list. */
public class DeleteCommand extends TaskIndexCommand {

    /**
     * Creates a delete command from parsed command values.
     *
     * @param values values extracted from the user's input.
     */
    public DeleteCommand(Map<String, String> values) {
        super(values);
    }

    /**
     * Deletes the selected task, persists the updated list, and returns the outcome.
     *
     * @return command outcome describing the deletion or an error.
     */
    @Override
    public CommandResult execute(TaskStorage tasks, Responder responder,
                        TaskFileRepository repository) {
        Integer taskIndex = getTaskIndex();
        if (taskIndex == null) {
            return CommandResult.error(
                    ResponseMessage.error(responder.respondInvalidCommand())
            );
        }

        Task deletedTask = tasks.deleteTask(taskIndex);

        if (deletedTask == null) {
            return CommandResult.error(
                    ResponseMessage.error(
                            responder.respondOutOfBoundIndex("delete", tasks.getSize())));
        }

        try {
            saveTasks(tasks, responder, repository);
            return CommandResult.success(
                    ResponseMessage.info(responder.respondDeleteTask(deletedTask, tasks.getSize())));
        } catch (IOException e) {
            tasks.insertTask(taskIndex, deletedTask);
            return CommandResult.error(
                    ResponseMessage.error(Responder.ErrorResponder.respondFileUpdateError()));
        }
    }
}
