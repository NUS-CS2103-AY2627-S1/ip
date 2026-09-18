package computah.command;

import computah.client.Client;
import computah.exception.ComputahException;
import computah.model.Model;
import computah.storage.Storage;
import computah.ui.Ui;

/**
 * Edits selected fields of an existing client.
 */
public class EditClientCommand extends Command {
    private final int clientIndex;
    private final String name;
    private final String phone;
    private final String email;

    /**
     * Creates a command that updates the supplied client fields.
     *
     * @param clientIndex zero-based index of the client to edit.
     * @param name new name, or null if unchanged.
     * @param phone new phone, an empty string to clear it, or null if unchanged.
     * @param email new email, an empty string to clear it, or null if unchanged.
     */
    public EditClientCommand(int clientIndex, String name, String phone, String email) {
        this.clientIndex = clientIndex;
        this.name = name;
        this.phone = phone;
        this.email = email;
    }

    /**
     * Updates the selected client fields, saves them, and shows the result.
     *
     * @param model current application data.
     * @param ui user interface used to display the updated client.
     * @param storage storage used to persist the updated client list.
     * @throws ComputahException if a field is invalid or the client list cannot be saved.
     */
    @Override
    public void execute(Model model, Ui ui, Storage storage) throws ComputahException {
        assert clientIndex >= 0 && clientIndex < model.getClientCount()
                : "Parser should supply an index of an existing client";
        Client client = model.getClients().get(clientIndex);
        client.update(name, phone, email);
        storage.saveClients(model.getClients());
        ui.showClientEdited(client);
    }
}
