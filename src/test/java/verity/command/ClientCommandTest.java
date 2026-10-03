package verity.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import verity.client.Client;
import verity.client.ClientList;
import verity.client.PreferredContactMethod;
import verity.exception.VerityException;
import verity.storage.ClientStorage;
import verity.storage.Storage;
import verity.task.TaskList;
import verity.task.Todo;
import verity.ui.Ui;

/**
 * Tests execution of client-management commands.
 */
class ClientCommandTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void clientAddCommand_execute_addsAndSavesClient()
            throws IOException, VerityException {
        CommandContext context = createContext(new TaskList(), new ClientList());
        ClientAddCommand command = new ClientAddCommand(
                "Alice Tan", "+65 9123 4567", "alice@example.com",
                "", "Acme", "", PreferredContactMethod.EMAIL);

        String response = command.execute(context);

        assertEquals(1, context.getClients().size());
        assertTrue(response.contains("Client added:"));
        assertTrue(Files.readString(
                context.getClientStorage().getDataFilePath()).contains("C001"));
    }

    @Test
    void clientEditCommand_clearPhone_preservesOtherFields()
            throws IOException, VerityException {
        ClientList clients = new ClientList();
        Client client = clients.addNewClient(
                "Alice Tan", "123 456", "alice@example.com",
                "", "Acme", "", PreferredContactMethod.EMAIL);
        CommandContext context = createContext(new TaskList(), clients);
        ClientEditCommand command = new ClientEditCommand(
                client.getId(), Map.of(), Set.of("phone"));

        command.execute(context);

        Client updated = clients.getById(client.getId());
        assertEquals("", updated.getPhone());
        assertEquals("alice@example.com", updated.getEmail());
        assertEquals("Acme", updated.getCompany());
    }

    @Test
    void clientEditCommand_duplicateEmail_rejectsWithoutMutation()
            throws VerityException {
        ClientList clients = new ClientList();
        Client first = clients.addNewClient(
                "Alice Tan", "", "alice@example.com", "", "", "", null);
        Client second = clients.addNewClient(
                "Bob Lee", "", "bob@example.com", "", "", "", null);
        CommandContext context = createContext(new TaskList(), clients);
        ClientEditCommand command = new ClientEditCommand(
                second.getId(), Map.of("email", first.getEmail()), Set.of());

        assertThrows(VerityException.class, () -> command.execute(context));
        assertEquals("bob@example.com", clients.getById(second.getId()).getEmail());
    }

    @Test
    void clientAssociateCommand_execute_addsClientToAllTasks()
            throws IOException, VerityException {
        ClientList clients = new ClientList();
        Client client = addClient(clients, "Alice Tan");
        TaskList tasks = new TaskList(
                new Todo("first task"), new Todo("second task"));
        CommandContext context = createContext(tasks, clients);

        new ClientAssociateCommand(client.getId(), List.of(0, 1)).execute(context);

        assertTrue(tasks.get(0).hasClientId(client.getId()));
        assertTrue(tasks.get(1).hasClientId(client.getId()));
    }

    @Test
    void clientAssociateCommand_alreadyAssociated_throwsWithoutMutation()
            throws VerityException {
        ClientList clients = new ClientList();
        Client client = addClient(clients, "Alice Tan");
        Todo todo = new Todo("task");
        todo.addClientId(client.getId());
        CommandContext context = createContext(new TaskList(todo), clients);

        VerityException exception = assertThrows(
                VerityException.class,
                () -> new ClientAssociateCommand(
                        client.getId(), List.of(0)).execute(context));

        assertEquals(
                "Client C001 is already associated with task 1.",
                exception.getMessage());
        assertEquals(List.of("C001"), todo.getClientIds());
    }

    @Test
    void clientDissociateCommand_lastClient_throwsWithoutMutation()
            throws VerityException {
        ClientList clients = new ClientList();
        Client client = addClient(clients, "Alice Tan");
        Todo todo = new Todo("task");
        todo.addClientId(client.getId());
        CommandContext context = createContext(new TaskList(todo), clients);

        assertThrows(
                VerityException.class,
                () -> new ClientDissociateCommand(
                        client.getId(), List.of(0)).execute(context));
        assertTrue(todo.hasClientId(client.getId()));
    }

    @Test
    void clientDissociateCommand_notAssociated_throwsWithoutMutation()
            throws VerityException {
        ClientList clients = new ClientList();
        Client client = addClient(clients, "Alice Tan");
        Client otherClient = addClient(clients, "Bob Lee");
        Todo todo = new Todo("task");
        todo.addClientId(otherClient.getId());
        CommandContext context = createContext(new TaskList(todo), clients);

        VerityException exception = assertThrows(
                VerityException.class,
                () -> new ClientDissociateCommand(
                        client.getId(), List.of(0)).execute(context));

        assertEquals(
                "Client C001 is not associated with task 1.",
                exception.getMessage());
        assertEquals(List.of("C002"), todo.getClientIds());
    }

    @Test
    void clientDissociateCommand_multipleClients_removesRequestedClient()
            throws IOException, VerityException {
        ClientList clients = new ClientList();
        Client client = addClient(clients, "Alice Tan");
        Client otherClient = addClient(clients, "Bob Lee");
        Todo todo = new Todo("task");
        todo.addClientId(client.getId());
        todo.addClientId(otherClient.getId());
        CommandContext context = createContext(new TaskList(todo), clients);

        String response = new ClientDissociateCommand(
                client.getId(), List.of(0)).execute(context);

        assertEquals(List.of("C002"), todo.getClientIds());
        assertTrue(response.contains("Client C001 was dissociated from task 1"));
    }

    @Test
    void clientEditCommand_changeAndClearPreferredContact_updatesClient()
            throws IOException, VerityException {
        ClientList clients = new ClientList();
        Client client = clients.addNewClient(
                "Alice Tan", "", "", "", "", "",
                PreferredContactMethod.EMAIL);
        CommandContext context = createContext(new TaskList(), clients);

        new ClientEditCommand(
                client.getId(), Map.of("preferred", "phone"), Set.of())
                .execute(context);
        assertEquals(
                PreferredContactMethod.PHONE,
                clients.getById(client.getId()).getPreferredContactMethod());

        new ClientEditCommand(
                client.getId(), Map.of(), Set.of("preferred")).execute(context);
        assertNull(clients.getById(client.getId()).getPreferredContactMethod());
    }

    @Test
    void clientListAndFindCommands_execute_formatExpectedClients()
            throws VerityException {
        ClientList clients = new ClientList();
        addClient(clients, "Alice Tan");
        addClient(clients, "Bob Lee");
        CommandContext context = createContext(new TaskList(), clients);

        String listResponse = new ClientListCommand().execute(context);
        String findResponse = new ClientFindCommand("alice").execute(context);

        assertTrue(listResponse.contains("Alice Tan"));
        assertTrue(listResponse.contains("Bob Lee"));
        assertTrue(findResponse.contains("Alice Tan"));
        assertFalse(findResponse.contains("Bob Lee"));
    }

    @Test
    void clientDeleteCommand_withoutConfirmation_warnsWithoutMutation()
            throws IOException, VerityException {
        ClientList clients = new ClientList();
        Client client = addClient(clients, "Alice Tan");
        CommandContext context = createContext(new TaskList(), clients);

        String response = new ClientDeleteCommand(
                client.getId(), false).execute(context);

        assertTrue(response.contains("Warning: Deleting client C001"));
        assertEquals(1, clients.size());
    }

    @Test
    void clientDeleteCommand_confirmed_removesAssociationAndAddsNote()
            throws IOException, VerityException {
        ClientList clients = new ClientList();
        Client client = addClient(clients, "Alice Tan");
        Todo todo = new Todo("Prepare invoice");
        todo.addClientId(client.getId());
        TaskList tasks = new TaskList(todo);
        CommandContext context = createContext(tasks, clients);

        new ClientDeleteCommand(client.getId(), true).execute(context);

        assertEquals(0, clients.size());
        assertFalse(todo.hasClientId(client.getId()));
        assertEquals(
                List.of("Former client Alice Tan (C001) was deleted."),
                todo.getFormerClientNotes());
        assertTrue(Files.readString(
                context.getStorage().getDataFilePath()).contains("Former client Alice Tan"));
    }

    private CommandContext createContext(TaskList tasks, ClientList clients) {
        Storage storage = new Storage(temporaryDirectory.resolve("tasks.txt"));
        ClientStorage clientStorage = new ClientStorage(
                temporaryDirectory.resolve("clients.txt"));
        return new CommandContext(tasks, clients, new Ui(), storage, clientStorage);
    }

    private Client addClient(ClientList clients, String name)
            throws VerityException {
        return clients.addNewClient(name, "", "", "", "", "", null);
    }
}
