package plana.client;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import plana.exception.PlanaException;

/**
 * Owns Plana's ordered collection of client records.
 */
public class ClientList implements Iterable<Client> {
    private final ArrayList<Client> clients;

    /**
     * Creates an empty client list.
     */
    public ClientList() {
        clients = new ArrayList<>();
    }

    /**
     * Creates a client list containing a copy of the supplied clients.
     *
     * @param clients the initial clients.
     */
    public ClientList(List<Client> clients) {
        assert clients != null && clients.stream().allMatch(client -> client != null)
                : "A client list must contain only non-null clients.";
        this.clients = new ArrayList<>(clients);
    }

    /**
     * Adds a client to the end of the list.
     *
     * @param client the client to add.
     * @throws PlanaException if another client already uses the email address.
     */
    public void add(Client client) throws PlanaException {
        assert client != null : "A client list must not contain null clients.";
        ensureEmailAvailable(client.getEmail(), null);
        clients.add(client);
    }

    /**
     * Returns the client at the specified zero-based index.
     *
     * @param index the zero-based index.
     * @return the client at that index.
     */
    public Client get(int index) {
        return clients.get(index);
    }

    /**
     * Returns the client selected by a user-facing reference.
     *
     * @param reference the one-based reference such as {@code C1}.
     * @param action the action being performed, used in validation messages.
     * @return the selected client.
     * @throws PlanaException if the reference is invalid or out of range.
     */
    public Client get(String reference, String action) throws PlanaException {
        return get(clientIndex(reference, action));
    }

    /**
     * Replaces a client selected by a user-facing reference.
     *
     * @param reference the one-based reference such as {@code C1}.
     * @param replacement the updated client.
     * @throws PlanaException if the reference is invalid or the email is already used.
     */
    public void replace(String reference, Client replacement) throws PlanaException {
        int index = clientIndex(reference, "edit");
        ensureEmailAvailable(replacement.getEmail(), clients.get(index).getEmail());
        clients.set(index, replacement);
    }

    /**
     * Deletes and returns a client selected by a user-facing reference.
     *
     * @param reference the one-based reference such as {@code C1}.
     * @return the deleted client.
     * @throws PlanaException if the reference is invalid or out of range.
     */
    public Client delete(String reference) throws PlanaException {
        return clients.remove(clientIndex(reference, "delete"));
    }

    /**
     * Returns the zero-based indexes of clients matching a keyword.
     *
     * @param keyword the keyword to search for.
     * @return matching indexes in client-list order.
     */
    public List<Integer> matchingIndexes(String keyword) {
        ArrayList<Integer> matchingIndexes = new ArrayList<>();
        for (int i = 0; i < clients.size(); i++) {
            if (clients.get(i).matchesKeyword(keyword)) {
                matchingIndexes.add(i);
            }
        }
        return matchingIndexes;
    }

    /**
     * Returns the number of clients in the list.
     *
     * @return the client count.
     */
    public int size() {
        return clients.size();
    }

    /**
     * Returns a snapshot of the clients in their current order.
     *
     * @return an unmodifiable copy of the current clients.
     */
    public List<Client> copyClients() {
        return List.copyOf(clients);
    }

    /**
     * Replaces all clients with a previously captured snapshot.
     *
     * @param savedClients the clients to restore in display order.
     */
    public void restoreClients(List<Client> savedClients) {
        assert savedClients != null && savedClients.stream().allMatch(client -> client != null)
                : "A client list must contain only non-null clients.";
        clients.clear();
        clients.addAll(savedClients);
    }

    /**
     * Returns an iterator over clients in creation order.
     *
     * @return an iterator over this client list.
     */
    @Override
    public Iterator<Client> iterator() {
        return clients.iterator();
    }

    private int clientIndex(String reference, String action) throws PlanaException {
        if (reference == null || reference.isBlank()) {
            throw new PlanaException("Oops, client " + action + " needs a client position."
                    + " Try: client " + action + " C1.");
        }
        if (!reference.matches("C[1-9][0-9]*")) {
            throw new PlanaException("Oops, '" + reference + "' isn't a valid client position."
                    + " Use a reference like C1.");
        }

        final int index;
        try {
            index = Integer.parseInt(reference.substring(1)) - 1;
        } catch (NumberFormatException exception) {
            throw new PlanaException("Oops, '" + reference + "' isn't a valid client position."
                    + " Use a reference like C1.");
        }
        if (index >= size()) {
            throw new PlanaException("Oops, client " + reference + " doesn't exist yet."
                    + " Type client list to check the client positions you have.");
        }
        return index;
    }

    private void ensureEmailAvailable(String email, String emailToIgnore) throws PlanaException {
        for (Client client : clients) {
            if (client.getEmail().equalsIgnoreCase(email)
                    && (emailToIgnore == null || !client.getEmail().equalsIgnoreCase(emailToIgnore))) {
                throw new PlanaException("Oops, a client with email '" + email.toLowerCase(Locale.ROOT)
                        + "' already exists.");
            }
        }
    }
}
