package atlas.storage;

import java.util.ArrayList;
import java.util.List;

import atlas.client.Client;
import atlas.task.Task;

/**
 * One reading of the storage file: the tasks and the clients it holds, together
 * with a description of any record that could not be read.
 */
public class AtlasData {

    /** Tasks loaded from the file. */
    private final ArrayList<Task> tasks;
    /** Clients loaded from the file. */
    private final ArrayList<Client> clients;
    /** Records that could not be read, described for the user. */
    private final List<String> warnings;

    /**
     * Creates a snapshot of the loaded data.
     *
     * @param tasks tasks loaded from the file.
     * @param clients clients loaded from the file.
     */
    public AtlasData(ArrayList<Task> tasks, ArrayList<Client> clients) {
        this(tasks, clients, new ArrayList<>());
    }

    /**
     * Creates a snapshot of the loaded data together with the problems found
     * while reading the file.
     *
     * @param tasks tasks loaded from the file.
     * @param clients clients loaded from the file.
     * @param warnings descriptions of the records that could not be read, or an
     *     empty list when every record was read.
     */
    public AtlasData(ArrayList<Task> tasks, ArrayList<Client> clients, List<String> warnings) {
        assert tasks != null : "loaded tasks must not be null";
        assert clients != null : "loaded clients must not be null";
        assert warnings != null : "loaded warnings must not be null";
        this.tasks = tasks;
        this.clients = clients;
        this.warnings = warnings;
    }

    /**
     * Returns the tasks held in this snapshot.
     *
     * @return loaded tasks.
     */
    public ArrayList<Task> getTasks() {
        return tasks;
    }

    /**
     * Returns the clients held in this snapshot.
     *
     * @return loaded clients.
     */
    public ArrayList<Client> getClients() {
        return clients;
    }

    /**
     * Returns the problems found while reading the file. An empty list means
     * every record was read, so a front-end can tell the user only when there
     * is something to tell.
     *
     * @return descriptions of the records that could not be read.
     */
    public List<String> getWarnings() {
        return warnings;
    }
}
