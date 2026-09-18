package plana.command;

import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import plana.client.Client;
import plana.client.ClientList;
import plana.exception.PlanaException;
import plana.storage.ClientStorage;
import plana.storage.Storage;
import plana.task.TaskList;
import plana.ui.Ui;

/**
 * Executes one action on Plana's separate client list.
 */
public class ClientCommand extends Command {
    /**
     * Identifies a client field accepted by add and edit commands.
     */
    public enum Field {
        /** Client name. */
        NAME,

        /** Client email. */
        EMAIL,

        /** Client phone number. */
        PHONE,

        /** Client address. */
        ADDRESS,

        /** Client baking-product preferences. */
        PREFERENCES,

        /** Additional client notes. */
        NOTES
    }

    private final ClientAction action;
    private final String reference;
    private final String keyword;
    private final Client client;
    private final Map<Field, String> changes;

    /**
     * Creates a client command that does not require client data.
     *
     * @param action the client action.
     */
    public ClientCommand(ClientAction action) {
        this(action, null, null, null, Collections.emptyMap());
    }

    /**
     * Creates a client command that operates on a client reference.
     *
     * @param action the client action.
     * @param reference the client reference.
     */
    public ClientCommand(ClientAction action, String reference) {
        this(action, reference, null, null, Collections.emptyMap());
    }

    /**
     * Creates a client command that searches for a keyword.
     *
     * @param action the find action.
     * @param keyword the search keyword.
     * @param isKeyword marker distinguishing this constructor from reference commands.
     */
    public ClientCommand(ClientAction action, String keyword, boolean isKeyword) {
        this(action, null, keyword, null, Collections.emptyMap());
    }

    /**
     * Creates an add-client command.
     *
     * @param client the client to add.
     */
    public ClientCommand(Client client) {
        this(ClientAction.ADD, null, null, client, Collections.emptyMap());
    }

    /**
     * Creates an edit-client command.
     *
     * @param reference the client reference.
     * @param changes the fields to replace.
     */
    public ClientCommand(String reference, Map<Field, String> changes) {
        this(ClientAction.EDIT, reference, null, null, new EnumMap<>(changes));
    }

    private ClientCommand(ClientAction action, String reference, String keyword,
                          Client client, Map<Field, String> changes) {
        this.action = action;
        this.reference = reference;
        this.keyword = keyword;
        this.client = client;
        this.changes = changes;
    }

    /**
     * Rejects the legacy task-only execution path because client commands need client collaborators.
     *
     * @param tasks unused by this command.
     * @param ui unused by this execution path.
     * @param storage unused by this execution path.
     * @throws UnsupportedOperationException always.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        throw new UnsupportedOperationException("Client commands require client storage.");
    }

    /**
     * Executes this client action and persists successful mutations.
     *
     * @param tasks unused by client actions.
     * @param clients the client list to update or query.
     * @param ui the user interface used for the response.
     * @param storage unused task storage.
     * @param clientStorage the client storage used for persistence.
     * @throws PlanaException if the client command is invalid during execution.
     */
    @Override
    public void execute(TaskList tasks, ClientList clients, Ui ui, Storage storage,
        ClientStorage clientStorage) throws PlanaException {
        switch (action) {
            case ADD:
                List<Client> clientsBeforeAddition = clients.copyClients();
                clients.add(client);
                saveClientsOrRestore(clients, clientsBeforeAddition, clientStorage);
                ui.showClientAdded(client, clients.size());
                break;
            case LIST:
                ui.showClientList(clients);
                break;
            case VIEW:
                Client viewedClient = clients.get(reference, "view");
                ui.showClientDetails(reference, viewedClient);
                break;
            case FIND:
                ui.showMatchingClientsHeader();
                List<Integer> matchingIndexes = clients.matchingIndexes(keyword);
                for (int index : matchingIndexes) {
                    ui.showClient(index, clients.get(index));
                }
                if (matchingIndexes.isEmpty()) {
                    ui.showNoMatchingClients();
                }
                ui.showLine();
                break;
            case EDIT:
                editClient(clients, clientStorage, ui);
                break;
            case DELETE:
                List<Client> clientsBeforeDeletion = clients.copyClients();
                Client deletedClient = clients.get(reference, "delete");
                clients.delete(reference);
                saveClientsOrRestore(clients, clientsBeforeDeletion, clientStorage);
                ui.showClientDeleted(reference, deletedClient, clients.size());
                break;
            default:
                throw new IllegalStateException("Unsupported client action.");
        }
    }

    private void editClient(ClientList clients, ClientStorage clientStorage, Ui ui)
            throws PlanaException {
        Client currentClient = clients.get(reference, "edit");
        Client updatedClient = new Client(
                changes.getOrDefault(Field.NAME, currentClient.getName()),
                changes.getOrDefault(Field.EMAIL, currentClient.getEmail()),
                changes.getOrDefault(Field.PHONE, currentClient.getPhone()),
                changes.getOrDefault(Field.ADDRESS, currentClient.getAddress()),
                changes.getOrDefault(Field.PREFERENCES, currentClient.getPreferences()),
                changes.getOrDefault(Field.NOTES, currentClient.getNotes()));
        List<Client> savedClients = clients.copyClients();
        clients.replace(reference, updatedClient);
        saveClientsOrRestore(clients, savedClients, clientStorage);
        ui.showClientUpdated(reference, updatedClient);
    }

    /**
     * Saves changed clients or restores the in-memory list after a save failure.
     *
     * @param clients the client list that was changed.
     * @param savedClients the client list before the change.
     * @param clientStorage the client storage used for persistence.
     * @throws PlanaException if the changed list could not be saved.
     */
    private void saveClientsOrRestore(ClientList clients, List<Client> savedClients,
                                      ClientStorage clientStorage) throws PlanaException {
        if (!clientStorage.saveClients(clients)) {
            clients.restoreClients(savedClients);
            throw new PlanaException("Oops, I couldn't save that client change."
                    + " Please check that the data folder is writable.");
        }
    }
}
