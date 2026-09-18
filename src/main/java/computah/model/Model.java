package computah.model;

import java.util.ArrayList;

import computah.client.Client;
import computah.task.Task;

/**
 * Holds the task and client data used by Computah commands.
 */
public class Model {
    private final ArrayList<Task> tasks = new ArrayList<>();
    private final ArrayList<Client> clients = new ArrayList<>();

    /**
     * Creates an application model with empty task and client collections.
     */
    public Model() {
    }

    /**
     * Returns the mutable task collection managed by the application.
     *
     * @return current tasks.
     */
    public ArrayList<Task> getTasks() {
        return tasks;
    }

    /**
     * Returns the mutable client collection managed by the application.
     *
     * @return current clients.
     */
    public ArrayList<Client> getClients() {
        return clients;
    }

    /**
     * Returns the number of tasks in the model.
     *
     * @return task count.
     */
    public int getTaskCount() {
        return tasks.size();
    }

    /**
     * Returns the number of clients in the model.
     *
     * @return client count.
     */
    public int getClientCount() {
        return clients.size();
    }
}
