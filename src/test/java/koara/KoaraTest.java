package koara;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests command execution across Koara's parser, task list, and storage.
 */
public class KoaraTest {

    @TempDir
    Path tempDirectory;

    @Test
    public void getResponse_taskWorkflow_returnsExpectedResponses() {
        Koara koara = new Koara(tempDirectory.resolve("koara.txt"));

        assertEquals("Ayo, locked in! Added this task:\n  [T][ ] read book"
                + "\nYou now have 1 task. We move!", koara.getResponse("todo read book"));
        assertEquals("Shiok! That's a W—task done:\n  [T][X] read book",
                koara.getResponse("mark 1"));
        assertEquals("No stress lah—this task is back on the radar:\n  [T][ ] read book",
                koara.getResponse("unmark 1"));
        assertEquals("Found them—here's the matching lineup:\n1.[T][ ] read book",
                koara.getResponse("find book"));
        assertEquals("Your game plan—steady lah:\n1.[T][ ] read book",
                koara.getResponse("list"));
        assertEquals("Clean slate energy—removed this task:\n  [T][ ] read book"
                + "\nYou have 0 tasks left. Keep cooking!", koara.getResponse("delete 1"));
        assertEquals("See ya later! Koara is always here for you—go get that W.",
                koara.getResponse("bye"));
    }

    @Test
    public void getResponse_clientWorkflow_managesAndPersistsClients() {
        Path dataFile = tempDirectory.resolve("koara.txt");
        Koara koara = new Koara(dataFile);

        assertEquals("Ayo, client locked in:\n  Alex Tan | Phone: 91234567"
                + " | Goal: Run 5 km | Notes: Knee injury\nYou now have 1 client. Steady!",
                koara.getResponse("client add Alex Tan /phone 91234567 /goal Run 5 km /notes Knee injury"));
        assertEquals("Found them—here's the matching client lineup:\n1. Alex Tan | Phone: 91234567"
                + " | Goal: Run 5 km | Notes: Knee injury", koara.getResponse("client find KNEE"));
        assertEquals("Glow-up complete—updated this client:\n  Alex Tan | Phone: 91230000"
                + " | Goal: Run 10 km | Notes: Recovered", koara.getResponse(
                        "client edit 1 /name Alex Tan /phone 91230000 /goal Run 10 km /notes Recovered"));

        Koara reloadedKoara = new Koara(dataFile);
        assertEquals("Your client lineup—steady lah:\n1. Alex Tan | Phone: 91230000"
                + " | Goal: Run 10 km | Notes: Recovered", reloadedKoara.getResponse("client list"));
        assertEquals("Clean slate energy—removed this client:\n  Alex Tan | Phone: 91230000"
                + " | Goal: Run 10 km | Notes: Recovered\nYou now have 0 clients.",
                reloadedKoara.getResponse("client delete 1"));
    }

    @Test
    public void getCommandResult_validAndInvalidCommands_reportsOutcome() {
        Koara koara = new Koara(tempDirectory.resolve("koara.txt"));

        Koara.CommandResult successfulResult = koara.getCommandResult("todo read book");
        Koara.CommandResult errorResult = koara.getCommandResult("mark 99");
        Koara.CommandResult unknownResult = koara.getCommandResult("vibe check");

        assertEquals(false, successfulResult.isError());
        assertEquals(true, errorResult.isError());
        assertEquals("That task number is not on the board yet. Check list and try again.",
                errorResult.message());
        assertEquals(true, unknownResult.isError());
        assertEquals("Walao, Koara catch no ball. Try todo, deadline, event, list, find, "
                + "or a client command.", unknownResult.message());
    }

    @Test
    public void getCommandResult_whitespaceAndDuplicateTask_handlesWithoutCorruptingList() {
        Koara koara = new Koara(tempDirectory.resolve("koara.txt"));

        Koara.CommandResult spacedResult = koara.getCommandResult("  todo   read   book  ");
        Koara.CommandResult duplicateResult = koara.getCommandResult("todo read book");

        assertFalse(spacedResult.isError());
        assertTrue(duplicateResult.isError());
        assertEquals("Your game plan—steady lah:\n1.[T][ ] read book", koara.getResponse(" list "));
    }

    @Test
    public void getCommandResult_invalidDataAndDuplicateClient_reportsErrors() {
        Koara koara = new Koara(tempDirectory.resolve("koara.txt"));

        assertTrue(koara.getCommandResult(
                "event trip /from 2026-09-14 /to 2026-09-13").isError());
        assertTrue(koara.getCommandResult("todo review | notes").isError());
        assertTrue(koara.getCommandResult(
                "client add Alex /phone abc /goal Run /notes Healthy").isError());
        assertFalse(koara.getCommandResult(
                "client add Alex /phone 91234567 /goal Run /notes Healthy").isError());
        assertTrue(koara.getCommandResult(
                "client add Beth /phone 91234567 /goal Swim /notes Healthy").isError());
    }

    @Test
    public void getResponse_deadlineEventAndEmptyLists_routesAllCommandTypes() {
        Koara koara = new Koara(tempDirectory.resolve("koara.txt"));

        assertEquals("Your game plan—steady lah:", koara.getResponse("list"));
        assertEquals("Your client lineup—steady lah:", koara.getResponse("client list"));
        assertFalse(koara.getCommandResult("deadline submit /by 2026-09-13").isError());
        assertFalse(koara.getCommandResult(
                "event workshop /from 2026-09-13 /to 2026-09-14").isError());
        assertTrue(koara.getCommandResult("find missing").message().endsWith("lineup:"));
    }

    @Test
    public void constructor_invalidSavedFiles_combinesStartupErrors() throws IOException {
        Path taskFile = tempDirectory.resolve("koara.txt");
        Path clientFile = tempDirectory.resolve("clients.txt");
        Files.writeString(taskFile, "invalid");
        Files.writeString(clientFile, "invalid");

        Koara koara = new Koara(taskFile);
        String startupError = koara.getStartupError().orElseThrow();

        assertTrue(startupError.contains("saved task data"));
        assertTrue(startupError.contains("saved client data"));
        assertEquals("Your game plan—steady lah:", koara.getResponse("list"));
        assertEquals("Your client lineup—steady lah:", koara.getResponse("client list"));
    }

    @Test
    public void failedTaskSave_rollsBackInMemoryAddition() throws IOException {
        Path taskPath = Files.createDirectory(tempDirectory.resolve("koara.txt"));
        Koara koara = new Koara(taskPath);

        Koara.CommandResult result = koara.getCommandResult("todo read book");

        assertTrue(result.isError());
        assertEquals("Your game plan—steady lah:", koara.getResponse("list"));
    }

    @Test
    public void failedClientSave_rollsBackInMemoryAddition() throws IOException {
        Files.createDirectory(tempDirectory.resolve("clients.txt"));
        Koara koara = new Koara(tempDirectory.resolve("koara.txt"));

        Koara.CommandResult result = koara.getCommandResult(
                "client add Alex /phone 91234567 /goal Run /notes Healthy");

        assertTrue(result.isError());
        assertEquals("Your client lineup—steady lah:", koara.getResponse("client list"));
    }

    @Test
    public void failedTaskUpdates_restorePreviousStatusAndOrder() throws IOException {
        Path taskPath = tempDirectory.resolve("koara.txt");
        Koara koara = new Koara(taskPath);
        koara.getResponse("todo first");
        koara.getResponse("todo second");
        Files.delete(taskPath);
        Files.createDirectory(taskPath);

        assertTrue(koara.getCommandResult("mark 1").isError());
        assertTrue(koara.getCommandResult("delete 1").isError());
        assertEquals("Your game plan—steady lah:\n1.[T][ ] first\n2.[T][ ] second",
                koara.getResponse("list"));
    }

    @Test
    public void failedClientUpdates_restorePreviousDetailsAndOrder() throws IOException {
        Path taskPath = tempDirectory.resolve("koara.txt");
        Path clientPath = tempDirectory.resolve("clients.txt");
        Koara koara = new Koara(taskPath);
        koara.getResponse("client add Alex /phone 91234567 /goal Run /notes Healthy");
        koara.getResponse("client add Beth /phone 92345678 /goal Swim /notes Healthy");
        Files.delete(clientPath);
        Files.createDirectory(clientPath);

        assertTrue(koara.getCommandResult(
                "client edit 1 /name Chris /phone 93456789 /goal Cycle /notes Healthy").isError());
        assertTrue(koara.getCommandResult("client delete 1").isError());
        assertEquals("Your client lineup—steady lah:\n"
                + "1. Alex | Phone: 91234567 | Goal: Run | Notes: Healthy\n"
                + "2. Beth | Phone: 92345678 | Goal: Swim | Notes: Healthy",
                koara.getResponse("client list"));
    }

    @Test
    public void constructor_nullPath_throwsAssertionError() {
        org.junit.jupiter.api.Assertions.assertThrows(AssertionError.class, () -> new Koara(null));
    }
}
