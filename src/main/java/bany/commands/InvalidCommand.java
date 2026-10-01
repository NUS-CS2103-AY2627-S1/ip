package bany.commands;

import bany.TaskFileRepository;
import bany.TaskStorage;
import bany.gui.Responder;

/** Represents an unrecognized command and its optional suggestion. */
public class InvalidCommand extends Command {
    /** Closest recognized command to suggest, if one was found. */
    private final String suggestion;

    /**
     * Creates an invalid command with an optional suggested command.
     *
     * @param suggestion the closest valid command, or null if unavailable.
     */
    public InvalidCommand(String suggestion) {
        this.suggestion = suggestion;
    }

    /**
     * Returns the invalid-command response and any available suggestion.
     *
     * @return invalid-command outcome.
     */
    @Override
    public CommandResult execute(TaskStorage tasks, Responder responder,
                        TaskFileRepository repository) {
        return CommandResult.error(
                ResponseMessage.error(
                        responder.respondInvalidCommand(suggestion)));
    }
}
