package koara.client;

import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Manages the clients stored by Koara.
 */
public class ClientList {
    private final ArrayList<Client> clients;

    /**
     * Creates an empty client list.
     */
    public ClientList() {
        this(new ArrayList<>());
    }

    /**
     * Creates a client list containing the specified clients.
     *
     * @param clients Initial clients.
     */
    public ClientList(ArrayList<Client> clients) {
        assert clients != null : "Initial client list must not be null";
        assert !clients.contains(null) : "Initial client list must not contain null";
        this.clients = new ArrayList<>(clients);
    }

    /**
     * Returns the number of clients.
     *
     * @return Number of clients.
     */
    public int getSize() {
        return clients.size();
    }

    /**
     * Returns the client at the specified index.
     *
     * @param index Zero-based client index.
     * @return Client at the index.
     */
    public Client get(int index) {
        assert isValidIndex(index) : "Client index must be within the list";
        return clients.get(index);
    }

    /**
     * Adds a client to the end of the list.
     *
     * @param client Client to add.
     */
    public void add(Client client) {
        assert client != null : "Client to add must not be null";
        clients.add(client);
    }

    /**
     * Inserts a client at a specified zero-based index.
     *
     * @param index Position at which to insert the client.
     * @param client Client to insert.
     */
    public void insert(int index, Client client) {
        assert index >= 0 && index <= clients.size() : "Client insertion index must be within the list";
        assert client != null : "Client to insert must not be null";
        clients.add(index, client);
    }

    /**
     * Returns whether a client with the same name or phone already exists.
     *
     * @param client Client whose identity should be checked.
     * @param excludedIndex Existing client index to ignore, or {@code -1} when adding.
     * @return True when another client has matching identifying information.
     */
    public boolean containsDuplicate(Client client, int excludedIndex) {
        assert client != null : "Client to compare must not be null";
        assert excludedIndex >= -1 && excludedIndex < clients.size()
                : "Excluded client index must be -1 or within the list";
        return java.util.stream.IntStream.range(0, clients.size())
                .filter(index -> index != excludedIndex)
                .mapToObj(clients::get)
                .anyMatch(existingClient -> existingClient.hasSameIdentity(client));
    }

    /**
     * Replaces the client at the specified index.
     *
     * @param index Zero-based client index.
     * @param client Updated client details.
     */
    public void update(int index, Client client) {
        assert isValidIndex(index) : "Client index must be within the list";
        assert client != null : "Updated client must not be null";
        clients.set(index, client);
    }

    /**
     * Removes and returns the client at the specified index.
     *
     * @param index Zero-based client index.
     * @return Removed client.
     */
    public Client delete(int index) {
        assert isValidIndex(index) : "Client index must be within the list";
        return clients.remove(index);
    }

    /**
     * Returns clients whose details contain the keyword.
     *
     * @param keyword Keyword to search for.
     * @return Matching clients in their original order.
     */
    public ClientList find(String keyword) {
        assert keyword != null && !keyword.isBlank() : "Client search keyword must not be blank";
        ArrayList<Client> matchingClients = clients.stream()
                .filter(client -> client.containsKeyword(keyword))
                .collect(Collectors.toCollection(ArrayList::new));
        return new ClientList(matchingClients);
    }

    /**
     * Returns every client in the data-file format.
     *
     * @return Serialized client lines.
     */
    public ArrayList<String> toDataLines() {
        return clients.stream()
                .map(Client::toDataString)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private boolean isValidIndex(int index) {
        return index >= 0 && index < clients.size();
    }
}
