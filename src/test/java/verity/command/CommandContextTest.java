package verity.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import verity.client.ClientList;
import verity.storage.ClientStorage;
import verity.storage.Storage;
import verity.task.TaskList;
import verity.task.Todo;
import verity.ui.Ui;

/**
 * Tests command dependency access and atomic task-and-client persistence.
 */
class CommandContextTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void constructor_storesAllDependencies() {
        TaskList tasks = new TaskList();
        ClientList clients = new ClientList();
        Ui ui = new Ui();
        Storage storage = new Storage(temporaryDirectory.resolve("tasks.txt"));
        ClientStorage clientStorage = new ClientStorage(
                temporaryDirectory.resolve("clients.txt"));

        CommandContext context = new CommandContext(
                tasks, clients, ui, storage, clientStorage);

        assertSame(tasks, context.getTasks());
        assertSame(clients, context.getClients());
        assertSame(ui, context.getUi());
        assertSame(storage, context.getStorage());
        assertSame(clientStorage, context.getClientStorage());
    }

    @Test
    void defaultConstructor_usesSiblingClientFile() {
        Path taskPath = temporaryDirectory.resolve("nested/tasks.txt");
        CommandContext context = new CommandContext(
                new TaskList(), new Ui(), new Storage(taskPath));

        assertEquals(
                taskPath.resolveSibling("clients.txt"),
                context.getClientStorage().getDataFilePath());
    }

    @Test
    void saveAll_success_savesBothFiles() throws IOException {
        TaskList tasks = new TaskList(new Todo("read book"));
        CommandContext context = createContext(
                tasks,
                temporaryDirectory.resolve("tasks.txt"),
                temporaryDirectory.resolve("clients.txt"));

        context.saveAll();

        assertTrue(Files.readString(
                context.getStorage().getDataFilePath()).contains("read book"));
        assertTrue(Files.readString(
                context.getClientStorage().getDataFilePath()).contains("NEXT_ID"));
    }

    @Test
    void saveAll_clientSaveFails_restoresExistingTaskFile() throws IOException {
        Path taskPath = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(taskPath, "original task data");
        Path blockingParent = temporaryDirectory.resolve("not-a-directory");
        Files.writeString(blockingParent, "blocking file");
        CommandContext context = createContext(
                new TaskList(new Todo("new task")),
                taskPath,
                blockingParent.resolve("clients.txt"));

        assertThrows(IOException.class, context::saveAll);

        assertEquals("original task data", Files.readString(taskPath));
    }

    @Test
    void saveAll_clientSaveFails_removesNewTaskFile() throws IOException {
        Path taskPath = temporaryDirectory.resolve("new-tasks.txt");
        Path blockingParent = temporaryDirectory.resolve("blocked-parent");
        Files.writeString(blockingParent, "blocking file");
        CommandContext context = createContext(
                new TaskList(new Todo("new task")),
                taskPath,
                blockingParent.resolve("clients.txt"));

        assertThrows(IOException.class, context::saveAll);

        assertFalse(Files.exists(taskPath));
    }

    private CommandContext createContext(
            TaskList tasks, Path taskPath, Path clientPath) {
        return new CommandContext(
                tasks,
                new ClientList(),
                new Ui(),
                new Storage(taskPath),
                new ClientStorage(clientPath));
    }
}
