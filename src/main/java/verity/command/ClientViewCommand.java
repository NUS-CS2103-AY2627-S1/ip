package verity.command;

import verity.client.Client;
import verity.exception.VerityException;

/**
 * Represents a command that displays one client.
 */
public class ClientViewCommand extends Command {
    private final String clientId;

    /**
     * Creates a client-view command.
     *
     * @param clientId Client ID to display.
     */
    public ClientViewCommand(String clientId) {
        super(false);
        this.clientId = clientId;
    }

    @Override
    public String execute(CommandContext context) throws VerityException {
        Client client = context.getClients().getById(clientId);
        return context.getUi().getClientDetailsMessage(
                client, context.getTasks());
    }
}
