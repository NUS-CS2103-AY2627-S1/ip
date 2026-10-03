package verity.command;

/**
 * Represents a command that lists all clients.
 */
public class ClientListCommand extends Command {
    /**
     * Creates a client-list command.
     */
    public ClientListCommand() {
        super(false);
    }

    @Override
    public String execute(CommandContext context) {
        return context.getUi().getClientListMessage(
                context.getClients().getSortedClients());
    }
}
