package computah.command;

import computah.client.Client;
import computah.exception.ComputahException;
import computah.model.Model;
import computah.storage.Storage;
import computah.ui.Ui;

/**
 * Adds a client to the client list.
 */
public class AddClientCommand extends Command {
    private final Client client;

    /**
     * Creates a command that adds the given client.
     *
     * @param client client to add.
     */
    public AddClientCommand(Client client) {
        this.client = client;
    }

    /**
     * Adds the client, persists the updated list, and shows the confirmation.
     *
     * @param model current application data.
     * @param ui user interface used to display the confirmation.
     * @param storage storage used to persist the updated client list.
     * @throws ComputahException if the updated client list cannot be saved.
     */
    @Override
    public void execute(Model model, Ui ui, Storage storage) throws ComputahException {
        model.getClients().add(client);
        storage.saveClients(model.getClients());
        ui.showClientAdded(client, model.getClientCount());
    }
}
