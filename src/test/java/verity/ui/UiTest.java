package verity.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import verity.client.Client;
import verity.client.PreferredContactMethod;
import verity.exception.VerityException;
import verity.task.Deadline;
import verity.task.TaskList;
import verity.task.Todo;

/**
 * Tests user input and responses formatted by the UI.
 */
class UiTest {
    private static final String HORIZONTAL_LINE =
            "____________________________________________________________\n";

    @Test
    void readCommand_inputLine_returnsLine() {
        InputStream originalInput = System.in;
        try {
            System.setIn(new ByteArrayInputStream("list\n".getBytes(
                    StandardCharsets.UTF_8)));
            Ui ui = new Ui();

            assertEquals("list", ui.readCommand());
        } finally {
            System.setIn(originalInput);
        }
    }

    @Test
    void getGreeting_returnsWelcomeMessage() {
        String response = new Ui().getGreeting();

        assertTrue(response.contains("Hello! I'm Verity."));
        assertTrue(response.contains("What can I do for you?"));
    }

    @Test
    void getExitMessage_returnsGoodbyeMessage() {
        String response = new Ui().getExitMessage();

        assertTrue(response.contains("Bye. Hope to see you again soon!"));
    }

    @Test
    void getHelpMessage_returnsAllCommandSyntaxes() {
        String response = new Ui().getHelpMessage();

        List<String> expectedCommandSyntaxes = List.of(
                "todo DESCRIPTION [/client CLIENT_ID ...]",
                "deadline DESCRIPTION /by YYYY-MM-DD [/client CLIENT_ID ...]",
                "event DESCRIPTION /from YYYY-MM-DD /to YYYY-MM-DD"
                        + " [/client CLIENT_ID ...]",
                "list",
                "mark TASK_NUMBER",
                "unmark TASK_NUMBER",
                "delete TASK_NUMBER",
                "find KEYWORD",
                "finddate YYYY-MM-DD",
                "client add /name NAME [FIELDS]",
                "client list",
                "client view CLIENT_ID",
                "client find NAME",
                "client edit CLIENT_ID /FIELD VALUE ...",
                "client edit CLIENT_ID /clear FIELD ...",
                "client associate CLIENT_ID /task TASK_NUMBER ...",
                "client dissociate CLIENT_ID /task TASK_NUMBER ...",
                "client delete CLIENT_ID [confirm]",
                "help",
                "bye");

        for (String commandSyntax : expectedCommandSyntaxes) {
            assertTrue(
                    response.contains(commandSyntax),
                    () -> "Missing help entry: " + commandSyntax);
        }
    }

    @Test
    void getTaskAddedMessage_returnsTaskAndCount() {
        String response = new Ui().getTaskAddedMessage(
                new Todo("read book"), 1);

        assertTrue(response.contains("[T][ ] read book"));
        assertTrue(response.contains("Now you have 1 tasks"));
    }

    @Test
    void getTaskMarkedMessage_returnsCompletedTask() {
        Todo todo = new Todo("read book");
        todo.markAsDone();

        String response = new Ui().getTaskMarkedMessage(todo);

        assertEquals(
                HORIZONTAL_LINE + "\n"
                        + "Nice! I've marked this task as done:\n\n"
                        + "[T][X] read book\n"
                        + HORIZONTAL_LINE,
                response);
    }

    @Test
    void getTaskUnmarkedMessage_returnsIncompleteTask() {
        Todo todo = new Todo("read book");

        String response = new Ui().getTaskUnmarkedMessage(todo);

        assertEquals(
                HORIZONTAL_LINE + "\n"
                        + "Ok, I've marked this task as not done yet:\n\n"
                        + "[T][ ] read book\n"
                        + HORIZONTAL_LINE,
                response);
    }

    @Test
    void getTaskDeletedMessage_returnsTaskAndRemainingCount() {
        String response = new Ui().getTaskDeletedMessage(
                new Todo("read book"), 0);

        assertEquals(
                HORIZONTAL_LINE + "\n"
                        + "Noted. I've removed this task:\n\n"
                        + "[T][ ] read book\n"
                        + HORIZONTAL_LINE + "\n"
                        + "Now you have 0 tasks in the list.\n\n"
                        + HORIZONTAL_LINE,
                response);
    }

    @Test
    void getTaskListMessage_returnsNumberedTasks() {
        TaskList tasks = new TaskList(
                new Todo("read book"),
                new Deadline("submit report", LocalDate.of(2026, 8, 10))
        );

        String response = new Ui().getTaskListMessage(tasks);

        assertEquals(
                HORIZONTAL_LINE + "\n"
                        + "    Here are the tasks in your list:\n\n"
                        + "    1.[T][ ] read book\n"
                        + "    2.[D][ ] submit report "
                        + "(by: Aug 10 2026)\n"
                        + HORIZONTAL_LINE,
                response);
    }

    @Test
    void getTasksOnMessage_matchingTasks_returnsDateAndTasks() {
        LocalDate date = LocalDate.of(2026, 8, 10);
        String response = new Ui().getTasksOnMessage(
                date,
                List.of(new Deadline("submit report", date)));

        assertTrue(response.contains("Tasks on 2026-08-10:"));
        assertTrue(response.contains("[D][ ] submit report"));
    }

    @Test
    void getTasksOnMessage_noMatchingTasks_returnsEmptyMessage() {
        String response = new Ui().getTasksOnMessage(
                LocalDate.of(2026, 8, 10), List.of());

        assertTrue(response.contains("There are no tasks on this date."));
    }

    @Test
    void getMatchingTasksMessage_matchingTasks_returnsNumberedTasks() {
        String response = new Ui().getMatchingTasksMessage(List.of(
                new Todo("read book"),
                new Todo("return book")));

        assertTrue(response.contains(
                "Here are the matching tasks in your list:"));
        assertTrue(response.contains("1.[T][ ] read book"));
        assertTrue(response.contains("2.[T][ ] return book"));
    }

    @Test
    void getMatchingTasksMessage_noMatchingTasks_returnsEmptyMessage() {
        String response = new Ui().getMatchingTasksMessage(List.of());

        assertTrue(response.contains("There are no matching tasks."));
    }

    @Test
    void errorMessageMethods_returnExpectedMessages() {
        Ui ui = new Ui();

        String commandError =
                ui.getCommandErrorMessage("invalid command");
        String loadingError = ui.getLoadingErrorMessage();
        String clientLoadingError = ui.getClientLoadingErrorMessage();
        String corruptedDataError =
                ui.getCorruptedDataErrorMessage("Line 1 is invalid.");
        String savingError = ui.getSavingErrorMessage();

        assertTrue(commandError.contains(
                "No... no, that is not right. invalid command"));
        assertTrue(commandError.contains("Stay with me and try again."));
        assertTrue(loadingError.contains(
                "I could not load your saved tasks."));
        assertTrue(clientLoadingError.contains(
                "I could not load your saved clients."));
        assertTrue(corruptedDataError.contains(
                "The saved task data is corrupted."));
        assertTrue(corruptedDataError.contains("Line 1 is invalid."));
        assertTrue(savingError.contains(
                "I could not save your tasks."));
    }

    @Test
    void getClientListMessage_clients_returnsAllFields()
            throws VerityException {
        Client client = new Client(
                1,
                "Alice Tan",
                "+65 9123 4567",
                "alice@example.com",
                "",
                "Acme",
                "Called\nRequested quote",
                PreferredContactMethod.EMAIL);

        String response = new Ui().getClientListMessage(List.of(client));

        assertTrue(response.contains("C001"));
        assertTrue(response.contains("Name: Alice Tan"));
        assertTrue(response.contains("Address: -"));
        assertTrue(response.contains("Notes: Called\n             Requested quote"));
        assertTrue(response.contains("Total: 1 client."));
    }

    @Test
    void getTaskListMessage_associatedTask_returnsClientMetadata()
            throws VerityException {
        Todo todo = new Todo("Prepare invoice");
        todo.addClientId("C001");
        todo.addFormerClientNote(
                "Former client Bob Lee (C002) was deleted.");

        String response = new Ui().getTaskListMessage(new TaskList(todo));

        assertTrue(response.contains("Clients: C001"));
        assertTrue(response.contains(
                "Note: Former client Bob Lee (C002) was deleted."));
    }

    @Test
    void getClientCollectionMessages_emptyCollections_returnEmptyMessages() {
        Ui ui = new Ui();

        assertTrue(ui.getClientListMessage(List.of()).contains(
                "There are no clients."));
        assertTrue(ui.getMatchingClientsMessage(List.of()).contains(
                "There are no matching clients."));
    }

    @Test
    void getMatchingClientsMessage_multipleClients_returnsPluralTotal()
            throws VerityException {
        Client first = createClient(1, "Alice Tan");
        Client second = createClient(2, "Bob Lee");

        String response = new Ui().getMatchingClientsMessage(
                List.of(first, second));

        assertTrue(response.contains("Matching clients:"));
        assertTrue(response.contains("Total: 2 clients."));
    }

    @Test
    void getClientDetailsMessage_withAndWithoutAssignment_listsExpectedTasks()
            throws VerityException {
        Client client = createClient(1, "Alice Tan");
        Todo unrelatedTask = new Todo("unrelated");
        Todo assignedTask = new Todo("assigned");
        assignedTask.addClientId(client.getId());
        Ui ui = new Ui();

        String assignedResponse = ui.getClientDetailsMessage(
                client, new TaskList(unrelatedTask, assignedTask));
        String emptyResponse = ui.getClientDetailsMessage(
                client, new TaskList(unrelatedTask));

        assertTrue(assignedResponse.contains("2.[T][ ] assigned"));
        assertTrue(emptyResponse.contains("Assigned tasks:\n      None"));
    }

    @Test
    void getClientChangeMessages_returnExpectedSingularAndPluralWording()
            throws VerityException {
        Client client = createClient(1, "Alice Tan");
        Ui ui = new Ui();

        String added = ui.getClientAddedMessage(client);
        String updated = ui.getClientUpdatedMessage(client);
        String singularDissociation = ui.getClientDissociatedMessage(
                client, List.of(0));
        String pluralDissociation = ui.getClientDissociatedMessage(
                client, List.of(0, 2));
        String singularWarning = ui.getClientDeleteWarningMessage(client, 1);
        String pluralWarning = ui.getClientDeleteWarningMessage(client, 2);
        String singularDeletion = ui.getClientDeletedMessage(client, 1);
        String pluralDeletion = ui.getClientDeletedMessage(client, 2);

        assertTrue(added.contains("Client added:"));
        assertTrue(updated.contains("Client updated:"));
        assertTrue(singularDissociation.contains("from task 1."));
        assertTrue(pluralDissociation.contains("from tasks 1, 3."));
        assertTrue(singularWarning.contains("assigned to 1 task."));
        assertTrue(pluralWarning.contains("assigned to 2 tasks."));
        assertTrue(singularDeletion.contains("from 1 task."));
        assertTrue(pluralDeletion.contains("from 2 tasks."));
    }

    @Test
    void clientPersistenceErrorMessages_returnExpectedDetails() {
        Ui ui = new Ui();

        assertTrue(ui.getSavingChangesErrorMessage().contains(
                "I could not save your changes."));
        assertTrue(ui.getCorruptedClientDataErrorMessage("Line 2 invalid.").contains(
                "The saved client data is corrupted.\n     Line 2 invalid."));
    }

    private Client createClient(int id, String name) throws VerityException {
        return new Client(id, name, "", "", "", "", "", null);
    }
}
