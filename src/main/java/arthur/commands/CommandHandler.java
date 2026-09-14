package arthur.commands;

import arthur.exceptions.ArthurRuntimeException;
import arthur.exceptions.UnknownCommandException;
import arthur.messages.ErrorMessage;
import arthur.messages.Message;
import arthur.state.CommandContext;
import arthur.state.Storage;

/**
 * Handles commands sent by the user.
 */
public class CommandHandler {
    // AI-assisted: Named command parsing and application shutdown literals.
    private static final String COMMAND_SEPARATOR = " ";
    private static final int COMMAND_NAME_INDEX = 0;
    private static final long EXIT_DELAY_MILLIS = 1000;
    /** The command context used to store and retrieve data */
    private final CommandContext context;
    /** The storage instance used to save and load the command context */
    private final Storage storage;

    /**
     * Constructs a CommandHandler with the given command context and storage instance.
     *
     * @param context the command context used to store and retrieve data.
     * @param storage the storage instance used to save and load the command context.
     */
    public CommandHandler(CommandContext context, Storage storage) {
        this.context = context;
        this.storage = storage;
    }

    /**
     * Handles a command.
     *
     * @param command the command to handle.
     * @return the response to the command.
     */
    public Message handle(String command) {
        /*
            AI-assisted:
            * Replaced echo handling with registered add and list command handling.
            * Extracted command execution and final-message exit scheduling.
         */
        Message message = executeCommand(command.trim());
        storage.save(context);
        exitAfterDelayIfFinal(message);
        return message;
    }

    // AI-assisted: Extracted command lookup, argument parsing, and exception handling from handle.
    /**
     * Executes a command and converts command failures into displayable error messages.
     *
     * @param command the user-entered command.
     * @return the command response or an error response.
     */
    private Message executeCommand(String command) {
        String commandName = command.split(COMMAND_SEPARATOR)[COMMAND_NAME_INDEX];
        Command commandInstance = CommandRegistry.getCommand(context, commandName);
        try {
            if (commandInstance == null) {
                throw new UnknownCommandException(command);
            }
            if (command.length() == commandName.length()) {
                return commandInstance.handle(context, Command.UNNAMED_ARGUMENT);
            }
            String otherArg = command.substring(commandName.length() + COMMAND_SEPARATOR.length());
            return commandInstance.handle(context, otherArg);
        } catch (ArthurRuntimeException e) {
            return new ErrorMessage(e);
        }
    }

    // AI-assisted: Extracted delayed exit handling and started its background thread.
    /**
     * Starts a delayed application exit after a final message has been displayed.
     *
     * @param message the response returned by the command.
     */
    private void exitAfterDelayIfFinal(Message message) {
        if (!message.isFinal()) {
            return;
        }
        new Thread(() -> {
            try {
                Thread.sleep(EXIT_DELAY_MILLIS);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.exit(0);
        }).start();
    }
}
