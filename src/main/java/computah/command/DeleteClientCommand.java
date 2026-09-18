package computah.command;

import computah.client.Client;
import computah.exception.ComputahException;
import computah.model.Model;
import computah.storage.Storage;
import computah.ui.Ui;

/**
 * Deletes a client from the client list.
 */
public class DeleteClientCommand extends Command {
    private final int clientIndex;

    /**
     * Creates a command that deletes the client at the given zero-based index.
     *
     * @param clientIndex zero-based index of the client to delete.
     */
    public DeleteClientCommand(int clientIndex) {
        this.clientIndex = clientIndex;
    }

    /**
     * Deletes the client, persists the updated list, and shows the result.
     *
     * @param model current application data.
     * @param ui user interface used to display the deleted client.
     * @param storage storage used to persist the updated client list.
     * @throws ComputahException if the updated client list cannot be saved.
     */
    @Override
    public void execute(Model model, Ui ui, Storage storage) throws ComputahException {
        assert clientIndex >= 0 && clientIndex < model.getClientCount()
                : "Parser should supply an index of an existing client";
        Client removedClient = model.getClients().remove(clientIndex);
        storage.saveClients(model.getClients());
        ui.showClientDeleted(removedClient, model.getClientCount());
    }
}
