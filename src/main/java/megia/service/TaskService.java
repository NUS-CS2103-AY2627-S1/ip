package megia.service;

import java.time.LocalDate;
import java.util.Optional;

import megia.exception.MegiaException;
import megia.exception.StorageException;
import megia.model.CommandResult;
import megia.model.ParsedCommand;
import megia.model.Task;
import megia.model.TaskEntry;
import megia.model.TaskStorage;

/**
 * Applies parsed commands to task data and persists successful mutations.
 */
public final class TaskService {
    private final TaskStorage taskStorage;
    private final LocalStorageService localStorageService;
    private final TaskParser taskParser;

    /**
     * Creates a task service using the supplied task collection and storage.
     *
     * @param taskStorage In-memory task collection to manage.
     * @param localStorageService Storage used to persist mutations.
     */
    public TaskService(TaskStorage taskStorage, LocalStorageService localStorageService) {
        this.taskStorage = taskStorage;
        this.localStorageService = localStorageService;
        this.taskParser = new TaskParser();
    }

    /**
     * Executes one parsed command and returns a structured semantic result.
     *
     * @param command Parsed command to execute.
     * @return Structured command result.
     * @throws MegiaException If the command contains invalid input or storage fails.
     */
    public CommandResult execute(ParsedCommand command) throws MegiaException {
        return switch (command.commandName()) {
            case "list" -> executeList(command);
            case "find" -> executeFind(command);
            case "help" -> new CommandResult.Help();
            case "bye" -> new CommandResult.Exit();
            case "todo", "deadline", "event" -> executeAdd(command);
            case "mark", "unmark", "delete" -> executeTaskMutation(command);
            case "" -> new CommandResult.Empty();
            default -> throw new megia.exception.UserInputException(
                    megia.exception.ErrorCode.UNKNOWN_COMMAND, command.commandName());
        };
    }

    private CommandResult executeList(ParsedCommand command) throws MegiaException {
        Optional<LocalDate> date = taskParser.parseListDate(command);
        if (date.isPresent()) {
            LocalDate requestedDate = date.get();
            return new CommandResult.TaskList(
                    taskStorage.getTaskEntriesOn(requestedDate),
                    new CommandResult.Query(
                            CommandResult.QueryType.DATE, requestedDate.toString()));
        }
        return new CommandResult.TaskList(
                taskStorage.getTaskEntries(),
                new CommandResult.Query(CommandResult.QueryType.ALL, ""));
    }

    private CommandResult executeFind(ParsedCommand command) throws MegiaException {
        String query = taskParser.parseFindQuery(command);
        return new CommandResult.TaskList(
                taskStorage.findTaskEntries(query),
                new CommandResult.Query(CommandResult.QueryType.FIND, query));
    }

    private CommandResult executeAdd(ParsedCommand command) throws MegiaException {
        int originalTaskCount = taskStorage.getTaskCount();
        Task newTask = taskParser.parseNewTask(command);
        taskStorage.addTask(newTask);
        assert taskStorage.getTaskCount() == originalTaskCount + 1
                : "Adding a task must increase the task count by one";
        try {
            localStorageService.saveTaskData(taskStorage);
        } catch (StorageException exception) {
            taskStorage.deleteTask(taskStorage.getTaskCount());
            assert taskStorage.getTaskCount() == originalTaskCount
                    : "Rolling back an added task must restore the task count";
            throw exception;
        }
        return new CommandResult.TaskMutation(
                CommandResult.MutationType.ADD,
                new TaskEntry(taskStorage.getTaskCount(), newTask),
                taskStorage.getTaskCount());
    }

    private CommandResult executeTaskMutation(ParsedCommand command) throws MegiaException {
        int taskId = taskParser.parseTaskId(command);
        int originalTaskCount = taskStorage.getTaskCount();
        Task taskBeforeMutation = taskStorage.getTaskById(taskId, command.commandName());
        boolean originalCompletionState = taskBeforeMutation.isDone();
        Task task = switch (command.commandName()) {
            case "mark" -> taskStorage.markTask(taskId);
            case "unmark" -> taskStorage.unmarkTask(taskId);
            default -> taskStorage.deleteTask(taskId);
        };
        int expectedTaskCount = command.commandName().equals("delete")
                ? originalTaskCount - 1
                : originalTaskCount;
        assert taskStorage.getTaskCount() == expectedTaskCount
                : "A task mutation must have the expected effect on the task count";
        try {
            localStorageService.saveTaskData(taskStorage);
        } catch (StorageException exception) {
            rollbackMutation(command.commandName(), taskId, task, originalCompletionState);
            assert taskStorage.getTaskCount() == originalTaskCount
                    : "Rolling back a task mutation must restore the task count";
            throw exception;
        }
        return new CommandResult.TaskMutation(
                CommandResult.MutationType.valueOf(command.commandName().toUpperCase()),
                new TaskEntry(taskId, task),
                taskStorage.getTaskCount());
    }

    private void rollbackMutation(String commandName, int taskId, Task task,
            boolean originalCompletionState) {
        switch (commandName) {
            case "mark", "unmark" -> {
                if (originalCompletionState) {
                    task.markAsDone();
                } else {
                    task.markAsNotDone();
                }
            }
            case "delete" -> taskStorage.restoreTask(taskId, task);
            default -> throw new IllegalArgumentException("Unsupported mutation: " + commandName);
        }
    }
}
