package verity.command;

import java.io.IOException;
import java.util.List;

import verity.client.Client;
import verity.exception.VerityException;
import verity.task.Task;

/**
 * Represents a warning or confirmed client-deletion command.
 */
public class ClientDeleteCommand extends Command {
    private final String clientId;
    private final boolean isConfirmed;

    /**
     * Creates a client-deletion command.
     *
     * @param clientId Client ID to delete.
     * @param isConfirmed Whether permanent deletion was confirmed.
     */
    public ClientDeleteCommand(String clientId, boolean isConfirmed) {
        super(false);
        this.clientId = clientId;
        this.isConfirmed = isConfirmed;
    }

    @Override
    public String execute(CommandContext context)
            throws IOException, VerityException {
        Client client = context.getClients().getById(clientId);
        List<Task> assignedTasks = context.getTasks().findByClientId(client.getId());
        if (!isConfirmed) {
            return context.getUi().getClientDeleteWarningMessage(
                    client, assignedTasks.size());
        }

        String formerClientNote = "Former client " + client.getFullName()
                + " (" + client.getId() + ") was deleted.";
        for (Task task : assignedTasks) {
            task.removeClientId(client.getId());
            task.addFormerClientNote(formerClientNote);
        }
        context.getClients().delete(client.getId());
        context.saveAll();
        return context.getUi().getClientDeletedMessage(client, assignedTasks.size());
    }
}
