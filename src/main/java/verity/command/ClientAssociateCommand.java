package verity.command;

import java.io.IOException;
import java.util.List;

import verity.client.Client;
import verity.exception.VerityException;
import verity.task.Task;

/**
 * Represents a command that associates a client with tasks.
 */
public class ClientAssociateCommand extends Command {
    private final String clientId;
    private final List<Integer> taskIndexes;

    /**
     * Creates a client-association command.
     *
     * @param clientId Client ID to associate.
     * @param taskIndexes Zero-based task indexes.
     */
    public ClientAssociateCommand(String clientId, List<Integer> taskIndexes) {
        super(false);
        this.clientId = clientId;
        this.taskIndexes = List.copyOf(taskIndexes);
    }

    @Override
    public String execute(CommandContext context)
            throws IOException, VerityException {
        Client client = context.getClients().getById(clientId);
        for (int taskIndex : taskIndexes) {
            Task task = context.getTasks().get(taskIndex);
            if (task.hasClientId(client.getId())) {
                throw new VerityException("Client " + client.getId()
                        + " is already associated with task " + (taskIndex + 1) + ".");
            }
        }
        for (int taskIndex : taskIndexes) {
            context.getTasks().get(taskIndex).addClientId(client.getId());
        }
        context.getStorage().saveTasks(context.getTasks());
        return context.getUi().getClientAssociatedMessage(
                client, context.getTasks(), taskIndexes);
    }
}
