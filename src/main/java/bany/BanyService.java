package bany;

import bany.commands.Command;
import bany.commands.CommandParser;
import bany.commands.CommandResult;
import bany.commands.ResponseMessage;
import bany.gui.Responder;

/**
 * Coordinates command parsing and execution for the JavaFX interface.
 *
 * <p>The service owns the shared application dependencies used for each
 * command and translates malformed input into a {@link CommandResult}.</p>
 */
public class BanyService {

    /** Repository used to persist changes to the task list. */
    private final TaskFileRepository taskFileRepository;
    /** In-memory list on which commands operate. */
    private final TaskStorage taskStorage;
    /** Parser that turns entered text into command objects. */
    private final CommandParser commandParser;
    /** Builder for messages displayed in the GUI. */
    private final Responder responder;

    /**
     * Creates the service with the dependencies shared by all commands.
     *
     * @param taskFileRepository repository used to persist task changes.
     * @param taskStorage in-memory task storage.
     * @param commandParser parser for user-entered commands.
     * @param responder builder for GUI response messages.
     */
    public BanyService(
            TaskFileRepository taskFileRepository,
            TaskStorage taskStorage,
            CommandParser commandParser,
            Responder responder
    ) {
        this.taskFileRepository = taskFileRepository;
        this.taskStorage = taskStorage;
        this.commandParser = commandParser;
        this.responder = responder;
    }

    /**
     * Parses and executes one command entered by the user.
     *
     * @param userInput complete command text entered by the user.
     * @return execution outcome, including an invalid-command response for malformed input.
     */
    public CommandResult executeCommand(String userInput) {
        Command command;

        try {
            command = commandParser.parse(userInput);
        } catch (IllegalArgumentException e) {
            // CommandParser uses IllegalArgumentException for blank or
            // malformed input. The UI turns it into friendly output.
            return CommandResult.error(
                    ResponseMessage.error(responder.respondInvalidCommand()));
        }

        return command.execute(taskStorage, responder, taskFileRepository);
    }

    /**
     * Returns Bany's initial greeting as an informational response.
     *
     * @return the greeting message for a new conversation.
     */
    public ResponseMessage getWelcomeMessage() {
        return ResponseMessage.info(
                responder.respondWelcome());
    }
}
