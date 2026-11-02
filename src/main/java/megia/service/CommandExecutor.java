package megia.service;

import java.util.Objects;

import megia.exception.MegiaException;
import megia.model.CommandResult;

/**
 * Parses raw user input and delegates execution to the shared task service.
 */
public final class CommandExecutor {
    private final TaskParser taskParser;
    private final TaskService taskService;

    /**
     * Creates a command executor with the supplied task service.
     *
     * @param taskService Service that applies parsed commands.
     */
    public CommandExecutor(TaskService taskService) {
        this.taskParser = new TaskParser();
        this.taskService = Objects.requireNonNull(taskService);
    }

    /**
     * Executes one raw command through the shared parser and task service.
     *
     * @param rawCommand Complete command entered by a user.
     * @return Structured semantic command result.
     * @throws MegiaException If the command contains invalid input or storage fails.
     */
    public CommandResult execute(String rawCommand) throws MegiaException {
        return taskService.execute(taskParser.parseCommand(rawCommand));
    }
}
