package verity.client;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import verity.exception.VerityException;

/**
 * Stores clients and enforces client identity and uniqueness rules.
 */
public class ClientList {
    private final ArrayList<Client> clients;
    private int nextId;

    /**
     * Creates an empty client list whose first generated ID is {@code C001}.
     */
    public ClientList() {
        this.clients = new ArrayList<>();
        this.nextId = 1;
    }

    /**
     * Creates a client list reconstructed from storage.
     *
     * @param nextId Next numeric ID to generate.
     * @param initialClients Stored clients.
     * @throws VerityException If IDs or unique fields conflict.
     */
    public ClientList(int nextId, List<Client> initialClients) throws VerityException {
        this.clients = new ArrayList<>();
        this.nextId = nextId;
        for (Client client : initialClients) {
            addLoadedClient(client);
        }
        validateNextId();
    }

    /**
     * Creates, stores, and returns a client with the next generated ID.
     *
     * @return Newly created client.
     * @throws VerityException If a unique field conflicts.
     */
    public Client addNewClient(String fullName, String phone, String email,
            String address, String company, String notes,
            PreferredContactMethod preferredContactMethod) throws VerityException {
        Client client = new Client(nextId, fullName, phone, email, address,
                company, notes, preferredContactMethod);
        validateUniqueFields(client, -1);
        clients.add(client);
        nextId++;
        return client;
    }

    /**
     * Replaces an existing client while preserving its immutable ID.
     *
     * @param replacement Updated client.
     * @throws VerityException If the client is missing or a unique field conflicts.
     */
    public void replace(Client replacement) throws VerityException {
        int index = indexOf(replacement.getNumericId());
        if (index < 0) {
            throw new VerityException("That client ID does not exist.");
        }
        validateUniqueFields(replacement, replacement.getNumericId());
        clients.set(index, replacement);
    }

    /**
     * Returns a client by ID.
     *
     * @param clientId Client ID.
     * @return Matching client.
     * @throws VerityException If the client does not exist.
     */
    public Client getById(String clientId) throws VerityException {
        int numericId = Client.parseId(clientId);
        int index = indexOf(numericId);
        if (index < 0) {
            throw new VerityException("That client ID does not exist.");
        }
        return clients.get(index);
    }

    /**
     * Deletes and returns a client by ID.
     *
     * @param clientId Client ID.
     * @return Deleted client.
     * @throws VerityException If the client does not exist.
     */
    public Client delete(String clientId) throws VerityException {
        Client client = getById(clientId);
        clients.remove(client);
        return client;
    }

    /**
     * Returns whether a client ID exists.
     *
     * @param clientId Client ID.
     * @return True if the client exists.
     */
    public boolean containsId(String clientId) {
        try {
            return indexOf(Client.parseId(clientId)) >= 0;
        } catch (VerityException exception) {
            return false;
        }
    }

    /**
     * Returns clients sorted by name and then numeric ID.
     *
     * @return Sorted read-only client list.
     */
    public List<Client> getSortedClients() {
        return clients.stream()
                .sorted(Comparator
                        .comparing((Client client) -> client.getFullName()
                                .toLowerCase(Locale.ROOT))
                        .thenComparingInt(Client::getNumericId))
                .toList();
    }

    /**
     * Returns clients whose names contain a keyword.
     *
     * @param keyword Search keyword.
     * @return Matching clients sorted by name and ID.
     */
    public List<Client> findByName(String keyword) {
        return getSortedClients().stream()
                .filter(client -> client.matchesName(keyword))
                .toList();
    }

    public List<Client> getClients() {
        return List.copyOf(clients);
    }

    public int getNextId() {
        return nextId;
    }

    /**
     * Returns the number of stored clients.
     *
     * @return Number of clients.
     */
    public int size() {
        return clients.size();
    }

    private void addLoadedClient(Client client) throws VerityException {
        if (indexOf(client.getNumericId()) >= 0) {
            throw new VerityException("duplicate client ID '" + client.getId() + "'.");
        }
        validateUniqueFields(client, -1);
        clients.add(client);
    }

    private void validateNextId() throws VerityException {
        int largestId = clients.stream()
                .mapToInt(Client::getNumericId)
                .max()
                .orElse(0);
        if (nextId <= largestId || nextId <= 0) {
            throw new VerityException(
                    "NEXT_ID must be greater than every stored client ID.");
        }
    }

    private void validateUniqueFields(Client candidate, int excludedId)
            throws VerityException {
        for (Client existing : clients) {
            if (existing.getNumericId() == excludedId) {
                continue;
            }
            if (!candidate.getEmail().isEmpty()
                    && Client.normalizeEmail(existing.getEmail())
                            .equals(Client.normalizeEmail(candidate.getEmail()))) {
                throw new VerityException(
                        "A client with that email address already exists.");
            }
            if (!candidate.getPhone().isEmpty()
                    && Client.normalizePhone(existing.getPhone())
                            .equals(Client.normalizePhone(candidate.getPhone()))) {
                throw new VerityException(
                        "A client with that phone number already exists.");
            }
        }
    }

    private int indexOf(int numericId) {
        for (int i = 0; i < clients.size(); i++) {
            if (clients.get(i).getNumericId() == numericId) {
                return i;
            }
        }
        return -1;
    }
}
