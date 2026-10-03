package verity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests the chatbot's top-level startup and command loop behavior.
 */
class VerityTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void run_missingDataFile_startsAndExitsNormally() {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");

        String output = runWithInput(dataFile, "bye\n");

        assertTrue(output.contains("Hello! I'm Verity."));
        assertTrue(output.contains("Bye. Hope to see you again soon!"));
        assertFalse(Files.exists(dataFile));
    }

    @Test
    void run_existingDataFile_loadsAndListsSavedTask() throws IOException {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(
                dataFile,
                "T\t0\tread book\t\t" + System.lineSeparator(),
                StandardCharsets.UTF_8);

        String output = runWithInput(dataFile, "list\nbye\n");

        assertTrue(output.contains("1.[T][ ] read book"));
        assertTrue(output.contains("Bye. Hope to see you again soon!"));
    }

    @Test
    void run_invalidCommand_showsErrorAndContinues() {
        String output = runWithInput(
                temporaryDirectory.resolve("tasks.txt"),
                "unknown\nbye\n");

        assertTrue(output.contains("I don't know that command."));
        assertTrue(output.contains("Bye. Hope to see you again soon!"));
    }

    @Test
    void run_closedInput_exitsWithoutFailure() {
        String output = runWithInput(
                temporaryDirectory.resolve("tasks.txt"), "");

        assertTrue(output.contains("Hello! I'm Verity."));
    }

    @Test
    void run_savingFailure_showsErrorAndContinues() throws IOException {
        Path dataDirectory = temporaryDirectory.resolve("blocked-data");
        Files.writeString(dataDirectory, "not a directory");

        String output = runWithInput(
                dataDirectory.resolve("tasks.txt"),
                "todo phantom\nlist\nbye\n");

        assertTrue(output.contains("I could not save your tasks."));
        assertFalse(output.contains("1.[T][ ] phantom"));
        assertTrue(output.contains("Bye. Hope to see you again soon!"));
    }

    @Test
    void run_findCommands_displayKeywordAndDateMatches() throws IOException {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(
                dataFile,
                "T\t0\tread book" + System.lineSeparator()
                        + "D\t1\treturn BOOK\t2026-08-10"
                        + System.lineSeparator()
                        + "D\t0\tsubmit report\t2026-08-11"
                        + System.lineSeparator(),
                StandardCharsets.UTF_8);

        String output = runWithInput(
                dataFile,
                "find book\nfinddate 2026-08-11\nbye\n");

        assertTrue(output.contains("1.[T][ ] read book"));
        assertTrue(output.contains("2.[D][X] return BOOK"));
        assertTrue(output.contains("Tasks on 2026-08-11:"));
        assertTrue(output.contains("[D][ ] submit report"));
    }

    @Test
    void run_corruptedData_showsCorruptionErrorAndStops() throws IOException {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(
                dataFile,
                "X\t0\tunknown task" + System.lineSeparator(),
                StandardCharsets.UTF_8);

        String output = runWithInput(dataFile, "bye\n");

        assertTrue(output.contains("The saved task data is corrupted."));
        assertFalse(output.contains("Bye. Hope to see you again soon!"));
    }

    @Test
    void getResponse_addCommand_returnsResponseAndPersistsTask()
            throws IOException {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Verity verity = new Verity(dataFile);

        String response = verity.getResponse("todo read book");

        assertTrue(response.contains("I've added this task"));
        assertTrue(response.contains("[T][ ] read book"));
        assertEquals(
                "T\t0\tread book\t\t" + System.lineSeparator(),
                Files.readString(dataFile));
    }

    @Test
    void getResponse_existingData_listsSavedTask() throws IOException {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(
                dataFile,
                "T\t0\tread book" + System.lineSeparator(),
                StandardCharsets.UTF_8);
        Verity verity = new Verity(dataFile);

        String response = verity.getResponse("list");

        assertTrue(response.contains("1.[T][ ] read book"));
    }

    @Test
    void getResponse_invalidCommand_returnsErrorMessage() {
        Verity verity = new Verity(
                temporaryDirectory.resolve("tasks.txt"));

        String response = verity.getResponse("unknown");

        assertTrue(response.contains("I don't know that command."));
    }

    @Test
    void getResponse_invalidCommandAfterSuccess_clearsCommandType() {
        Verity verity = new Verity(
                temporaryDirectory.resolve("tasks.txt"));
        verity.getResponse("list");

        verity.getResponse("unknown");

        assertNull(verity.getCommandType());
    }

    @Test
    void getResponse_byeCommand_requestsExit() {
        Verity verity = new Verity(
                temporaryDirectory.resolve("tasks.txt"));

        verity.getResponse("bye");

        assertTrue(verity.isExitRequested());
    }

    @Test
    void getResponse_nonExitCommandAfterBye_clearsExitRequest() {
        Verity verity = new Verity(
                temporaryDirectory.resolve("tasks.txt"));
        verity.getResponse("bye");

        verity.getResponse("list");

        assertFalse(verity.isExitRequested());
    }

    @Test
    void getResponse_savingFailure_doesNotRetainUnsavedTask()
            throws IOException {
        Path dataDirectory = temporaryDirectory.resolve("data");
        Files.writeString(dataDirectory, "not a directory");
        Verity verity = new Verity(dataDirectory.resolve("tasks.txt"));

        String errorResponse = verity.getResponse("todo phantom");
        String listResponse = verity.getResponse("list");

        assertTrue(errorResponse.contains("I could not save your tasks."));
        assertFalse(listResponse.contains("phantom"));
    }

    @Test
    void getResponse_clientWorkflow_persistsAssociatesAndDeletesClient()
            throws IOException {
        Path taskFile = temporaryDirectory.resolve("tasks.txt");
        Path clientFile = temporaryDirectory.resolve("clients.txt");
        Verity verity = new Verity(taskFile, clientFile);

        String addClientResponse = verity.getResponse(
                "client add /name Alice Tan /email alice@example.com");
        String addTaskResponse = verity.getResponse(
                "todo Prepare invoice /client C001");
        String viewResponse = verity.getResponse("client view C001");
        String warningResponse = verity.getResponse("client delete C001");
        String deleteResponse = verity.getResponse(
                "client delete C001 confirm");
        String taskListResponse = verity.getResponse("list");

        assertTrue(addClientResponse.contains("Client added:"));
        assertTrue(addTaskResponse.contains("Prepare invoice"));
        assertTrue(viewResponse.contains("Prepare invoice"));
        assertTrue(warningResponse.contains("is permanent"));
        assertTrue(deleteResponse.contains("Client deleted:"));
        assertTrue(taskListResponse.contains(
                "Former client Alice Tan (C001) was deleted."));
        assertTrue(Files.readString(clientFile).startsWith("NEXT_ID\t2"));
    }

    @Test
    void getResponse_existingClientFile_loadsClient() throws IOException {
        Path taskFile = temporaryDirectory.resolve("tasks.txt");
        Path clientFile = temporaryDirectory.resolve("clients.txt");
        Files.writeString(
                clientFile,
                "NEXT_ID\t2" + System.lineSeparator()
                        + "C\tC001\tAlice Tan\t\talice@example.com\t\t\t\temail"
                        + System.lineSeparator(),
                StandardCharsets.UTF_8);

        String response = new Verity(taskFile, clientFile)
                .getResponse("client view C001");

        assertTrue(response.contains("Name: Alice Tan"));
        assertTrue(response.contains("Assigned tasks:\n      None"));
    }

    @Test
    void getResponse_corruptedClientFile_returnsCorruptionMessage()
            throws IOException {
        Path taskFile = temporaryDirectory.resolve("tasks.txt");
        Path clientFile = temporaryDirectory.resolve("clients.txt");
        Files.writeString(
                clientFile,
                "invalid header" + System.lineSeparator(),
                StandardCharsets.UTF_8);

        String response = new Verity(taskFile, clientFile).getResponse("list");

        assertTrue(response.contains("The saved client data is corrupted."));
    }

    @Test
    void getResponse_unreadableClientPath_returnsClientLoadingMessage()
            throws IOException {
        Path taskFile = temporaryDirectory.resolve("tasks.txt");
        Path clientPath = temporaryDirectory.resolve("client-directory");
        Files.createDirectory(clientPath);

        String response = new Verity(taskFile, clientPath).getResponse("list");

        assertTrue(response.contains("I could not load your saved clients."));
    }

    @Test
    void getResponse_unreadableTaskPath_returnsTaskLoadingMessage()
            throws IOException {
        Path taskPath = temporaryDirectory.resolve("task-directory");
        Files.createDirectory(taskPath);

        String response = new Verity(taskPath).getResponse("list");

        assertTrue(response.contains("I could not load your saved tasks."));
    }

    @Test
    void getResponse_taskReferencesMissingClient_returnsCorruptionMessage()
            throws IOException {
        Path taskFile = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(
                taskFile,
                "T\t0\tinvoice\tC001\t" + System.lineSeparator(),
                StandardCharsets.UTF_8);

        String response = new Verity(taskFile).getResponse("list");

        assertTrue(response.contains("The saved task data is corrupted."));
        assertTrue(response.contains("unknown client ID 'C001'."));
    }

    @Test
    void getResponse_clientSavingFailure_restoresClientAndShowsChangesError()
            throws IOException {
        Path taskFile = temporaryDirectory.resolve("tasks.txt");
        Path blockingParent = temporaryDirectory.resolve("blocked-client-parent");
        Files.writeString(blockingParent, "not a directory");
        Verity verity = new Verity(
                taskFile, blockingParent.resolve("clients.txt"));

        String errorResponse = verity.getResponse(
                "client add /name Alice Tan");
        String listResponse = verity.getResponse("client list");

        assertTrue(errorResponse.contains("I could not save your changes."));
        assertTrue(listResponse.contains("There are no clients."));
    }

    @Test
    void getResponse_repairedTaskFile_retriesInitialization()
            throws IOException {
        Path taskFile = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(taskFile, "X\t0\tinvalid" + System.lineSeparator());
        Verity verity = new Verity(taskFile);

        String errorResponse = verity.getResponse("list");
        Files.writeString(
                taskFile,
                "T\t0\tread book" + System.lineSeparator(),
                StandardCharsets.UTF_8);
        String recoveredResponse = verity.getResponse("list");

        assertTrue(errorResponse.contains("The saved task data is corrupted."));
        assertTrue(recoveredResponse.contains("1.[T][ ] read book"));
    }

    @Test
    void getResponse_clientFieldWithTrailingWhitespace_rejectsUntrimmedValue() {
        Verity verity = new Verity(
                temporaryDirectory.resolve("tasks.txt"));

        String response = verity.getResponse("client add /name Alice Tan ");

        assertTrue(response.contains(
                "Client name cannot start or end with whitespace."));
    }

    @Test
    void getResponse_missingClientOnNewTask_rejectsTaskWithoutSaving()
            throws IOException {
        Path taskFile = temporaryDirectory.resolve("tasks.txt");
        Verity verity = new Verity(taskFile);

        String response = verity.getResponse(
                "todo Prepare invoice /client C001");

        assertTrue(response.contains("That client ID does not exist: C001."));
        assertFalse(Files.exists(taskFile));
    }

    private static String runWithInput(Path dataFile, String input) {
        InputStream originalInput = System.in;
        PrintStream originalOutput = System.out;
        ByteArrayOutputStream capturedOutput = new ByteArrayOutputStream();
        try (PrintStream replacementOutput = new PrintStream(
                capturedOutput, true, StandardCharsets.UTF_8)) {
            System.setIn(new ByteArrayInputStream(
                    input.getBytes(StandardCharsets.UTF_8)));
            System.setOut(replacementOutput);
            new Verity(dataFile).run();
        } finally {
            System.setIn(originalInput);
            System.setOut(originalOutput);
        }
        return capturedOutput.toString(StandardCharsets.UTF_8);
    }
}
