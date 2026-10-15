package charliek.command;

import java.util.Objects;
import java.util.Optional;

import charliek.ui.Ui;

/**
 * Handles requests for command usage information.
 */
public class HelpCommand extends Command {
    /**
     * The UI used to display help information.
     */
    private final Ui ui;

    /**
     * The requested command keyword, or an empty string for general help.
     */
    private final String requestedCommand;

    /**
     * Creates a help command.
     *
     * @param ui the UI used to display help information.
     * @param requestedCommand the command to describe, or an empty string for general help.
     */
    public HelpCommand(Ui ui, String requestedCommand) {
        this.ui = Objects.requireNonNull(ui);
        this.requestedCommand = Objects.requireNonNull(requestedCommand);
    }

    /**
     * Displays general help or detailed help for the requested command.
     */
    @Override
    public void execute() {
        String commandKeyword = requestedCommand.trim();
        if (commandKeyword.isEmpty()) {
            ui.showAvailableCommands();
            return;
        }

        Optional<CommandType> command = CommandType.getCommandFromKeyword(commandKeyword);
        if (command.isPresent()) {
            ui.showCommandHelp(command.get());
        } else {
            ui.showUnknownHelpCommand(commandKeyword);
        }
    }
}
