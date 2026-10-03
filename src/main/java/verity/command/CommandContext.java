package verity.command;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import verity.client.ClientList;
import verity.storage.ClientStorage;
import verity.storage.Storage;
import verity.task.TaskList;
import verity.ui.Ui;

/**
 * Provides the dependencies needed to execute commands.
 */
public class CommandContext {
    private final TaskList tasks;
    private final ClientList clients;
    private final Ui ui;
    private final Storage storage;
    private final ClientStorage clientStorage;

    /**
     * Creates a command execution context.
     *
     * @param tasks Task list on which commands operate.
     * @param ui UI used to format command results.
     * @param storage Storage used to save task changes.
     */
    public CommandContext(
            TaskList tasks, Ui ui, Storage storage) {
        this(tasks, new ClientList(), ui, storage,
                new ClientStorage(storage.getDataFilePath()
                        .resolveSibling("clients.txt")));
    }

    /**
     * Creates a command execution context with task and client storage.
     *
     * @param tasks Task list on which commands operate.
     * @param clients Client list on which commands operate.
     * @param ui UI used to format command results.
     * @param storage Storage used to save task changes.
     * @param clientStorage Storage used to save client changes.
     */
    public CommandContext(TaskList tasks, ClientList clients, Ui ui,
            Storage storage, ClientStorage clientStorage) {
        this.tasks = tasks;
        this.clients = clients;
        this.ui = ui;
        this.storage = storage;
        this.clientStorage = clientStorage;
    }

    public TaskList getTasks() {
        return tasks;
    }

    public Ui getUi() {
        return ui;
    }

    public ClientList getClients() {
        return clients;
    }

    public Storage getStorage() {
        return storage;
    }

    public ClientStorage getClientStorage() {
        return clientStorage;
    }

    /**
     * Saves tasks and clients while restoring the original files if either save fails.
     *
     * @throws IOException If either file cannot be saved or restored.
     */
    public void saveAll() throws IOException {
        FileSnapshot taskSnapshot = FileSnapshot.capture(storage.getDataFilePath());
        FileSnapshot clientSnapshot = FileSnapshot.capture(clientStorage.getDataFilePath());
        try {
            storage.saveTasks(tasks);
            clientStorage.saveClients(clients);
        } catch (IOException exception) {
            try {
                taskSnapshot.restore();
                clientSnapshot.restore();
            } catch (IOException restoreException) {
                exception.addSuppressed(restoreException);
            }
            throw exception;
        }
    }

    /**
     * Captures one file so a failed multi-file save can restore it.
     */
    private static class FileSnapshot {
        private final Path path;
        private final boolean hasSnapshot;
        private final byte[] contents;

        private FileSnapshot(Path path, boolean hasSnapshot, byte[] contents) {
            this.path = path;
            this.hasSnapshot = hasSnapshot;
            this.contents = contents;
        }

        private static FileSnapshot capture(Path path) throws IOException {
            boolean hasSnapshot = Files.exists(path);
            byte[] contents = hasSnapshot ? Files.readAllBytes(path) : new byte[0];
            return new FileSnapshot(path, hasSnapshot, contents);
        }

        private void restore() throws IOException {
            if (!hasSnapshot) {
                Files.deleteIfExists(path);
                return;
            }
            Path parentDirectory = path.getParent();
            if (parentDirectory != null) {
                Files.createDirectories(parentDirectory);
            }
            Files.write(path, contents);
        }
    }
}
