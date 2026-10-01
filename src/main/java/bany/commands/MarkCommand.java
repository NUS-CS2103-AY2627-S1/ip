package bany.commands;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import bany.TaskFileRepository;
import bany.TaskStorage;
import bany.gui.Responder;
import bany.tasks.Task;

/** Marks a numbered task as completed. */
public class MarkCommand extends TaskIndexCommand {

    /**
     * Creates a mark command from parsed command values.
     *
     * @param values values extracted from the user's input.
     */
    public MarkCommand(Map<String, String> values) {
        super(values);
    }

    /**
     * Marks the selected task as done, persists the list, and returns the outcome.
     *
     * @return command outcome describing the change or an error.
     */
    @Override
    public CommandResult execute(TaskStorage tasks, Responder responder,
                        TaskFileRepository repository) {
        Integer taskIndex = getTaskIndex();

        if (taskIndex == null || taskIndex < 0) {
            return CommandResult.error(
                    ResponseMessage.error(responder.respondInvalidCommand()));
        }

        Optional<Task> optionalTask = tasks.getTask(taskIndex);

        if (optionalTask.isEmpty()) {
            return CommandResult.error(
                    ResponseMessage.error(
                            responder.respondOutOfBoundIndex("mark", tasks.getSize())));
        }

        Task task = optionalTask.get();

        boolean wasMarked = task.isDone();

        task.mark();


        try {
            saveTasks(tasks, responder, repository);
            return CommandResult.success(
                    ResponseMessage.info(
                            responder.respondMarkTask(task)));
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
