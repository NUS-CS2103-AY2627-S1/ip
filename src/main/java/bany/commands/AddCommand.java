package bany.commands;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import bany.TaskFileRepository;
import bany.TaskStorage;
import bany.exceptions.InvalidTaskType;
import bany.gui.Responder;
import bany.parsers.DateTimeParser;
import bany.tasks.Deadline;
import bany.tasks.Event;
import bany.tasks.Task;
import bany.tasks.ToDo;
import bany.utilities.CommandValidator;

/** Handles the creation and persistence of TODO, DEADLINE, and EVENT tasks. */
public class AddCommand extends Command {
    /**
     * Parsed command values such as the type, description, and date tags.
     */
    private final Map<String, String> values;
    /**
     * Tag names in the order in which they appeared in the input.
     */
    private final List<String> tagNames;
    /**
     * Rules used to validate required, duplicate, and extra tags.
     */
    private final CommandValidator validator;

    /**
     * Creates an add command from parsed values and tag names.
     *
     * @param values    values extracted from the user's input.
     * @param tagNames  tags in their original input order.
     * @param validator validates required and extra tags.
     */
    public AddCommand(Map<String, String> values, List<String> tagNames,
                      CommandValidator validator) {
        this.values = values;
        this.tagNames = tagNames;
        this.validator = validator;
    }

    /**
     * Creates, stores, and persists a task before returning a response.
     *
     * @return command outcome describing success, validation failure, or a save failure.
     */
    @Override
    public CommandResult execute(TaskStorage tasks, Responder responder,
                                 TaskFileRepository repository) {
        String type = values.get("command");

        String duplicatedCriticalTags = validator.findDuplicateCriticalTag(type, tagNames);

        if (duplicatedCriticalTags != null) {
            return CommandResult.error(
                    ResponseMessage.error(
                            responder.respondDuplicateTag(duplicatedCriticalTags)));
        }

        return switch (createTask(responder)) {
            case TaskCreationFailure failure -> CommandResult.error(
                    ResponseMessage.error(failure.validationMessage()));
            case TaskCreationSuccess success -> executeSuccessfulCreation(
                    type, success.task(), tasks, responder, repository);
        };
    }

    /**
     * Stores and persists a validated task, rolling back the in-memory change if saving fails.
     *
     * @param type command type used for tag-warning validation.
     * @param task validated task to add.
     * @param tasks current task storage.
     * @param responder response builder used for user-facing messages.
     * @param repository persistence component.
     * @return command outcome describing the save or rollback result.
     */
    private CommandResult executeSuccessfulCreation(String type, Task task,
                                                    TaskStorage tasks, Responder responder,
                                                    TaskFileRepository repository) {

        boolean hasTagWarning = validator.hasTagWarning(type, tagNames);
        tasks.addTask(task);

        try {
            saveTasks(tasks, responder, repository);
            ResponseMessage taskMessage = ResponseMessage.info(
                    responder.respondAddTask(task, tasks.getSize()));
            if (hasTagWarning) {
                return CommandResult.success(
                        ResponseMessage.warning(
                                responder.respondTagWarning()), taskMessage);
            }
            return CommandResult.success(taskMessage);
        } catch (IOException e) {
            tasks.deleteTask(tasks.getSize() - 1);
            return CommandResult.error(
                    ResponseMessage.error(
                            Responder.ErrorResponder.respondFileUpdateError()));
        }
    }

    /**
     * Creates the task type requested by the parsed command.
     *
     * @param responder response builder used while validating task details.
     * @return a complete task or the user-facing validation message that prevents creation.
     */
    private TaskCreationResult createTask(Responder responder) {
        String type = values.get("command");
        String description = values.get("description");

        if (description == null || description.isBlank()) {
            return new TaskCreationFailure(responder.respondInvalidTaskDescription());
        }

        return switch (type) {
            case "TODO" -> new TaskCreationSuccess(new ToDo(description, Task.allocateId()));
            case "DEADLINE" -> createDeadline(description, responder);
            case "EVENT" -> createEvent(description, responder);
            default -> throw new InvalidTaskType("Unknown task type: " + type);
        };
    }

    /**
     * Creates a deadline after validating its date-time value.
     *
     * @param description deadline description.
     * @param responder response builder used while validating the deadline.
     * @return a complete deadline or the validation message that prevents creation.
     */
    private TaskCreationResult createDeadline(String description, Responder responder) {
        String by = values.get("by");
        if (by == null || by.isBlank()) {
            return new TaskCreationFailure(responder.respondInvalidTaskInitiation());
        }

        try {
            LocalDateTime dateTime = DateTimeParser.createLocalDateTime(by);
            return new TaskCreationSuccess(
                    new Deadline(description, dateTime, Task.allocateId()));
        } catch (IllegalArgumentException e) {
            return new TaskCreationFailure(responder.respondInvalidDateTime());
        }
    }

    /**
     * Creates an event after validating both date-times and their order.
     *
     * @param description event description.
     * @param responder response builder used while validating the event.
     * @return a complete event or the validation message that prevents creation.
     */
    private TaskCreationResult createEvent(String description, Responder responder) {
        String from = values.get("from");
        String to = values.get("to");
        if (from == null || to == null || from.isBlank() || to.isBlank()) {
            return new TaskCreationFailure(responder.respondInvalidTaskInitiation());
        }

        try {
            LocalDateTime fromDateTime = DateTimeParser.createLocalDateTime(from);
            LocalDateTime toDateTime = DateTimeParser.createLocalDateTime(to);

            if (toDateTime.isBefore(fromDateTime)) {
                return new TaskCreationFailure(responder.respondInvalidEventRange());
            }

            return new TaskCreationSuccess(
                    new Event(description, fromDateTime, toDateTime, Task.allocateId()));

        } catch (IllegalArgumentException e) {
            return new TaskCreationFailure(responder.respondInvalidDateTime());
        }
    }

    /** Represents either a validated task or a message explaining why creation failed. */
    private sealed interface TaskCreationResult
            permits TaskCreationSuccess, TaskCreationFailure {
    }

    /** Holds the validated task ready to be added to storage. */
    private record TaskCreationSuccess(Task task) implements TaskCreationResult {
        private TaskCreationSuccess {
            Objects.requireNonNull(task);
        }
    }

    /** Holds the validation message to show when no task can be created. */
    private record TaskCreationFailure(String validationMessage)
            implements TaskCreationResult {
        private TaskCreationFailure {
            Objects.requireNonNull(validationMessage);
        }
    }

}
