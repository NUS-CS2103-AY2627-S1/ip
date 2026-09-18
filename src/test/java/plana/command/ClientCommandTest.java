package plana.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import plana.client.Client;
import plana.client.ClientList;
import plana.exception.PlanaException;
import plana.storage.ClientStorage;
import plana.storage.Storage;
import plana.task.TaskList;
import plana.ui.Ui;

/**
 * Tests client command execution, persistence, and rollback behavior.
 */
class ClientCommandTest {
    @Test
    void addCommand_execute_clientIsPersistedAndDisplayed(@TempDir Path temporaryDirectory)
            throws PlanaException {
        Client alice = new Client("Alice", "alice@example.com", "91234567", "", "", "");
        ClientList clients = new ClientList();
        ClientStorage storage = clientStorageAt(temporaryDirectory);

        String response = execute(new ClientCommand(alice), clients, storage);

        assertEquals(1, clients.size());
        assertEquals(alice.toStorageString(), storage.loadClients().get(0).toStorageString());
        assertTrue(response.contains("I've added this client"));
        assertTrue(response.contains("Now you have 1 client in your list."));
    }

    @Test
    void listFindAndViewCommands_execute_expectedClientsDisplayed(@TempDir Path temporaryDirectory)
            throws PlanaException {
        Client alice = new Client("Alice", "alice@example.com", "", "", "", "");
        Client bob = new Client("Bob", "bob@example.com", "", "", "", "vegan");
        ClientList clients = new ClientList(List.of(alice, bob));
        ClientStorage storage = clientStorageAt(temporaryDirectory);

        String listResponse = execute(new ClientCommand(ClientAction.LIST), clients, storage);
        String viewResponse = execute(new ClientCommand(ClientAction.VIEW, "C2"), clients, storage);
        String findResponse = execute(new ClientCommand(ClientAction.FIND, "vegan", true), clients, storage);

        assertTrue(listResponse.contains("C1. Alice <alice@example.com>"));
        assertTrue(listResponse.contains("C2. Bob <bob@example.com>"));
        assertTrue(viewResponse.contains("Name: Bob"));
        assertTrue(viewResponse.contains("Notes: vegan"));
        assertTrue(findResponse.contains("C2. Bob <bob@example.com>"));
        assertFalse(findResponse.contains("C1. Alice <alice@example.com>"));
    }

    @Test
    void findCommand_noMatches_displaysEmptyResult(@TempDir Path temporaryDirectory)
            throws PlanaException {
        ClientList clients = new ClientList(List.of(
                new Client("Alice", "alice@example.com", "", "", "", "")));

        String response = execute(new ClientCommand(ClientAction.FIND, "missing", true), clients,
                clientStorageAt(temporaryDirectory));

        assertTrue(response.contains("No matching clients found."));
    }

    @Test
    void editCommand_execute_changedFieldsArePersistedAndDisplayed(@TempDir Path temporaryDirectory)
            throws PlanaException {
        Client original = new Client("Alice", "alice@example.com", "", "", "", "");
        ClientList clients = new ClientList(List.of(original));
        ClientStorage storage = clientStorageAt(temporaryDirectory);
        storage.saveClients(clients);
        Map<ClientCommand.Field, String> changes = new EnumMap<>(ClientCommand.Field.class);
        changes.put(ClientCommand.Field.PHONE, "91234567");
        changes.put(ClientCommand.Field.NOTES, "birthday cake");

        String response = execute(new ClientCommand("C1", changes), clients, storage);

        Client updated = clients.get(0);
        assertEquals("91234567", updated.getPhone());
        assertEquals("birthday cake", updated.getNotes());
        assertEquals(updated.toStorageString(), storage.loadClients().get(0).toStorageString());
        assertTrue(response.contains("I've updated this client"));
    }

    @Test
    void deleteCommand_execute_clientIsRemovedAndPersisted(@TempDir Path temporaryDirectory)
            throws PlanaException {
        ClientList clients = new ClientList(List.of(
                new Client("Alice", "alice@example.com", "", "", "", ""),
                new Client("Bob", "bob@example.com", "", "", "", "")));
        ClientStorage storage = clientStorageAt(temporaryDirectory);
        storage.saveClients(clients);

        String response = execute(new ClientCommand(ClientAction.DELETE, "C1"), clients, storage);

        assertEquals(1, clients.size());
        assertEquals("Bob", clients.get(0).getName());
        assertEquals("bob@example.com", storage.loadClients().get(0).getEmail());
        assertTrue(response.contains("I've removed this client"));
        assertTrue(response.contains("Now you have 1 client in the list."));
    }

    @Test
    void clientMutations_saveFailure_restoreOriginalList(@TempDir Path temporaryDirectory) throws IOException {
        Path blockedDirectory = temporaryDirectory.resolve("blocked-directory");
        Files.writeString(blockedDirectory, "This path is deliberately a file.");
        ClientStorage storage = new ClientStorage(blockedDirectory.resolve("clients.txt").toString());
        Client alice = new Client("Alice", "alice@example.com", "", "", "", "");
        Client bob = new Client("Bob", "bob@example.com", "", "", "", "");

        ClientList emptyClients = new ClientList();
        assertThrows(PlanaException.class, () -> execute(new ClientCommand(alice), emptyClients, storage));
        assertEquals(0, emptyClients.size());

        ClientList clientsForEdit = new ClientList(List.of(alice));
        Map<ClientCommand.Field, String> changes = new EnumMap<>(ClientCommand.Field.class);
        changes.put(ClientCommand.Field.NAME, "Alicia");
        assertThrows(PlanaException.class, () ->
                execute(new ClientCommand("C1", changes), clientsForEdit, storage));
        assertEquals("Alice", clientsForEdit.get(0).getName());

        ClientList clientsForDelete = new ClientList(List.of(alice, bob));
        assertThrows(PlanaException.class, () ->
                execute(new ClientCommand(ClientAction.DELETE, "C1"), clientsForDelete, storage));
        assertEquals(List.of("Alice", "Bob"), List.of(
                clientsForDelete.get(0).getName(), clientsForDelete.get(1).getName()));
    }

    @Test
    void execute_legacyTaskOnlyPath_rejected() {
        ClientCommand command = new ClientCommand(ClientAction.LIST);

        assertThrows(UnsupportedOperationException.class, () ->
                command.execute(new TaskList(), new Ui(), new Storage("unused-test-file.txt")));
    }

    private ClientStorage clientStorageAt(Path temporaryDirectory) {
        return new ClientStorage(temporaryDirectory.resolve("clients.txt").toString());
    }

    private String execute(ClientCommand command, ClientList clients, ClientStorage clientStorage)
            throws PlanaException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (PrintStream printStream = new PrintStream(output); Ui ui = new Ui(printStream)) {
            command.execute(new TaskList(), clients, ui, new Storage("unused-test-file.txt"), clientStorage);
        }
        return output.toString();
    }
}
