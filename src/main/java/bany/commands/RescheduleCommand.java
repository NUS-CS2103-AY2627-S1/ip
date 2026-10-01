package bany.commands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import bany.TaskFileRepository;
import bany.TaskStorage;
import bany.gui.Responder;
import bany.tags.Tag;
import bany.tasks.Task;
import bany.utilities.CommandValidator;

/** Updates the tags of a numbered task and persists the changed task. */
public class RescheduleCommand extends TaskIndexCommand {
    /** Tag names in the order in which they appeared in the input. */
    private final List<String> tagNames;
    /** Rules used to validate critical tags and command warnings. */
    private final CommandValidator validator;

    /**
     * Creates a reschedule command from parsed input.
     *
     * @param values parsed command values, including the task number.
     * @param tagNames tag names in their original input order.
     * @param validator tag validation rules.
     */
    public RescheduleCommand(Map<String, String> values, List<String> tagNames,
                             CommandValidator validator) {
        super(values);
        this.tagNames = List.copyOf(tagNames);
        this.validator = validator;
    }

    /**
     * Updates the selected task's tags, saves the task list, and returns a response.
     *
     * @return command outcome describing the update or validation failure.
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
                            responder.respondOutOfBoundIndex("reschedule", tasks.getSize())));
        }

        Task task = optionalTask.get();

        if (tagNames.isEmpty()) {
            return CommandResult.error(
                    ResponseMessage.error(responder.respondInvalidCommand()));
        }

        String duplicateCriticalTag = validator.findDuplicateCriticalTag(
                task.getCriticalTags(), tagNames);
        if (duplicateCriticalTag != null) {
            return CommandResult.error(
                    ResponseMessage.error(
                            responder.respondDuplicateTag(duplicateCriticalTag)));
        }

        List<Tag> updates = createUpdates();
        boolean hasTagWarning = validator.hasRescheduleTagWarning(
                task.getCriticalTags(), tagNames);
        List<Tag> originalTags = task.getTags();

        try {
            task.updateTags(updates);
        } catch (IllegalArgumentException e) {
            return CommandResult.error(
                    ResponseMessage.error(getValidationMessage(e, responder)));
        }

        try {
            saveTasks(tasks, responder, repository);
        } catch (IOException e) {
            task.replaceTags(originalTags);
            return CommandResult.error(
                    ResponseMessage.error(
                            Responder.ErrorResponder.respondFileUpdateError()));
        }

        ResponseMessage successMessage = ResponseMessage.info(
                responder.respondRescheduleTask(task));
        if (hasTagWarning) {
            return CommandResult.success(
                    ResponseMessage.warning(responder.respondRescheduleTagWarning()),
                    successMessage);
        }
        return CommandResult.success(successMessage);
    }

    /**
     * Creates one update per distinct tag name. Non-critical duplicate tags
     * are reduced to their final value after the validator has marked the
     * command for a warning; critical duplicates are rejected before this
     * method is called.
     */
    private List<Tag> createUpdates() {
        Map<String, Tag> updatesByName = new LinkedHashMap<>();
        for (String tagName : tagNames) {
            updatesByName.put(tagName,
                    new Tag(tagName, values.get(tagName)));
        }
        return new ArrayList<>(updatesByName.values());
    }

    /** Converts task-tag validation failures into existing GUI messages. */
    private String getValidationMessage(IllegalArgumentException exception,
                                        Responder responder) {
        String message = exception.getMessage();
        if (message != null && message.contains("before its start")) {
            return responder.respondInvalidEventRange();
        }
        if (message != null && message.contains("date-time")) {
            return responder.respondInvalidDateTime();
        }
        return responder.respondInvalidTaskInitiation();
    }
}
