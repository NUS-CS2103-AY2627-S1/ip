package computah.command;

import computah.model.Model;
import computah.storage.Storage;
import computah.ui.Ui;

/**
 * Shows all clients in the client list.
 */
public class ListClientsCommand extends Command {
    /**
     * Creates a command that lists clients.
     */
    public ListClientsCommand() {
    }

    /**
     * Displays the current client list.
     *
     * @param model current application data.
     * @param ui user interface used to display the client list.
     * @param storage storage component; unused for this command.
     */
    @Override
    public void execute(Model model, Ui ui, Storage storage) {
        ui.showClientList(model.getClients());
    }
}
