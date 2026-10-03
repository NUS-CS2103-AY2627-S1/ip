package verity.command;

import java.io.IOException;
import java.util.List;

import verity.client.Client;
import verity.exception.VerityException;
import verity.task.Task;

/**
 * Represents a command that dissociates a client from tasks.
 */
public class ClientDissociateCommand extends Command {
    private final String clientId;
    private final List<Integer> taskIndexes;

    /**
     * Creates a client-dissociation command.
     *
     * @param clientId Client ID to dissociate.
     * @param taskIndexes Zero-based task indexes.
     */
    public ClientDissociateCommand(String clientId, List<Integer> taskIndexes) {
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
            if (!task.hasClientId(client.getId())) {
                throw new VerityException("Client " + client.getId()
                        + " is not associated with task " + (taskIndex + 1) + ".");
            }
            if (task.getClientIds().size() == 1) {
                throw new VerityException("Client " + client.getId()
                        + " is the last client associated with task "
                        + (taskIndex + 1) + ".");
            }
        }
        for (int taskIndex : taskIndexes) {
            context.getTasks().get(taskIndex).removeClientId(client.getId());
        }
        context.getStorage().saveTasks(context.getTasks());
        return context.getUi().getClientDissociatedMessage(
                client, taskIndexes);
    }
}
