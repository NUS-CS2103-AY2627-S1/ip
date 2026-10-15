package atlas.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Contains the client list and the operations that change it.
 * Client numbering is independent of task numbering.
 */
public class ClientList {

    /** Clients in their user-visible order. */
    private final ArrayList<Client> clients;

    /**
     * Creates a client list backed by the supplied clients.
     *
     * @param clients clients to place in the list.
     */
    public ClientList(ArrayList<Client> clients) {
        assert clients != null : "client list must not be null";
        this.clients = clients;
    }

    /** Creates an empty client list. */
    public ClientList() {
        this.clients = new ArrayList<>();
    }

    /**
     * Returns whether this list contains no clients.
     *
     * @return {@code true} when the list is empty.
     */
    public boolean isEmpty() {
        return clients.isEmpty();
    }

    /**
     * Returns the number of clients in this list.
     *
     * @return number of clients.
     */
    public int size() {
        return clients.size();
    }

    /**
     * Returns the client at the specified zero-based position.
     *
     * @param index zero-based position of the client.
     * @return client at the specified position.
     */
    public Client get(int index) {
        assert index >= 0 && index < clients.size() : "index out of range: " + index;
        return clients.get(index);
    }

    /**
     * Adds a client to the end of this list.
     *
     * @param client client to add.
     */
    public void add(Client client) {
        assert client != null : "client must not be null";
        clients.add(client);
    }

    /**
     * Removes and returns the client at the specified zero-based position.
     *
     * @param index zero-based position of the client to remove.
     * @return the removed client.
     */
    public Client remove(int index) {
        assert index >= 0 && index < clients.size() : "index out of range: " + index;
        return clients.remove(index);
    }

    /**
     * Returns clients matching any of the supplied keywords. A client matches
     * when its name, phone or email contains a keyword. The keywords are ORed
     * together and the original client order is preserved.
     *
     * @param keywords one or more text fragments to search for.
     * @return matching clients, or an empty list when no client matches.
     */
    public ArrayList<Client> find(String... keywords) {
        assert keywords.length > 0 : "at least one keyword is required";
        return clients.stream()
                .filter(client -> Arrays.stream(keywords).anyMatch(client::matches))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Returns all clients in their current order for persistence.
     *
     * @return the underlying client list.
     */
    public ArrayList<Client> all() {
        return clients;
    }
}
