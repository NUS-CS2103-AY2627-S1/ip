package wiz;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Represents a list of clients and provides operations to manipulate them.
 */
public class ClientList {
    private final ArrayList<Client> clients;

    /**
     * Constructs an empty ClientList.
     */
    public ClientList() {
        this.clients = new ArrayList<>();
    }

    /**
     * Constructs a ClientList initialized with the given list of clients.
     *
     * @param clients The ArrayList of clients.
     */
    public ClientList(ArrayList<Client> clients) {
        assert clients != null : "Initial client list cannot be null";
        this.clients = clients;
    }

    /**
     * Constructs a ClientList initialized with varargs clients.
     *
     * @param clients The array/varargs of clients.
     */
    public ClientList(Client... clients) {
        assert clients != null : "Clients varargs cannot be null";
        this.clients = new ArrayList<>(Arrays.asList(clients));
    }

    /**
     * Adds one or more clients to the client list.
     *
     * @param clients The clients to add.
     */
    public void add(Client... clients) {
        assert clients != null : "Clients to add cannot be null";
        for (Client client : clients) {
            assert client != null : "Cannot add a null client to ClientList";
            this.clients.add(client);
        }
    }

    /**
     * Deletes and returns the client at the specified index.
     *
     * @param index The zero-based index of the client to delete.
     * @return The removed client.
     */
    public Client delete(int index) {
        assert index >= 0 && index < clients.size() : "Index out of bounds for deletion";
        return clients.remove(index);
    }

    /**
     * Returns the client at the specified index.
     *
     * @param index The zero-based index of the client.
     * @return The client at the given index.
     */
    public Client get(int index) {
        assert index >= 0 && index < clients.size() : "Index out of bounds for retrieval";
        return clients.get(index);
    }

    /**
     * Returns the number of clients in the list.
     *
     * @return The size of the client list.
     */
    public int size() {
        return clients.size();
    }

    /**
     * Returns the underlying list of clients.
     *
     * @return The ArrayList of clients.
     */
    public ArrayList<Client> getClients() {
        return clients;
    }

    /**
     * Checks if a duplicate client already exists in the list.
     *
     * @param client The client to check.
     * @return True if a client with matching name, phone, or email exists, false otherwise.
     */
    public boolean hasDuplicate(Client client) {
        assert client != null : "Client to check cannot be null";
        return clients.stream().anyMatch(c -> c.getName().equalsIgnoreCase(client.getName())
                || c.getPhone().equalsIgnoreCase(client.getPhone())
                || c.getEmail().equalsIgnoreCase(client.getEmail()));
    }

    /**
     * Finds clients that contain the specified keyword in their string representation.
     *
     * @param keyword The keyword to search for.
     * @return A list of clients matching the keyword.
     */
    public ArrayList<Client> find(String keyword) {
        assert keyword != null : "Search keyword cannot be null";
        String lowerKeyword = keyword.toLowerCase();
        return clients.stream()
                .filter(client -> client.toString().toLowerCase().contains(lowerKeyword))
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
