package bany.commands;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import bany.TaskFileRepository;
import bany.TaskStorage;
import bany.gui.Responder;
import bany.tasks.Task;

/** Marks a numbered task as not completed. */
public class UnmarkCommand extends TaskIndexCommand {

    /**
     * Creates an unmark command from parsed command values.
     *
     * @param values values extracted from the user's input.
     */
    public UnmarkCommand(Map<String, String> values) {
        super(values);
    }

    /**
     * Marks the selected task as not done, persists the list, and returns the outcome.
     *
     * @return command outcome describing the change or an error.
     */
    @Override
    public CommandResult execute(TaskStorage tasks, Responder responder,
                                 TaskFileRepository repository) {
        Integer taskIndex = getTaskIndex();
        if (taskIndex == null) {
            return CommandResult.error(
                    ResponseMessage.error(responder.respondInvalidCommand()));
        }

        Optional<Task> optionalTask = tasks.getTask(taskIndex);

        if (optionalTask.isEmpty()) {
            return CommandResult.error(
                    ResponseMessage.error(
                            responder.respondOutOfBoundIndex("unmark", tasks.getSize())));
        }

        Task task = optionalTask.get();

        boolean wasMarked = task.isDone();

        task.unmark();


        try {
            saveTasks(tasks, responder, repository);
            return CommandResult.success(
                    ResponseMessage.info(
                            responder.respondUnmarkTask(task)));
        } catch (IOException e) {
            if (wasMarked) {
                task.mark();
            } else {
                task.unmark();
            }
            return CommandResult.error(
                    ResponseMessage.error(
                            Responder.ErrorResponder.respondFileUpdateError()));
        }
    }
}
