package kelore;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests command processing performed by {@link Kelore}. */
public class KeloreTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    public void constructor_nullDataPath_assertionError() {
        assertThrows(AssertionError.class, () -> new Kelore(null));
    }

    @Test
    public void getWelcomeMessage_newDataFile_returnsGreeting() {
        Kelore kelore = new Kelore(temporaryDirectory.resolve("tasks.txt"));

        assertEquals("Hi, I'm Kelore - ready when you are.\n"
                + "Add a task or type list to get started.", kelore.getWelcomeMessage());
    }

    @Test
    public void getWelcomeMessage_corruptedDataFile_reportsLoadError() throws Exception {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(dataFile, "corrupted record");

        Kelore kelore = new Kelore(dataFile);

        assertEquals("Hi, I'm Kelore - ready when you are.\n"
                + "Add a task or type list to get started."
                + "\nI couldn't load your saved tasks, so saving is disabled for this session.\n"
                + "The data file is corrupted at line 1.", kelore.getWelcomeMessage());
        assertEquals("You don't have any tasks yet.", kelore.getResponse("list"));
        assertEquals("I couldn't save that change. Your task list was left unchanged.\n"
                        + "Saving is disabled because the existing data file could not be loaded.",
                kelore.getResponse("todo replacement"));
        assertEquals("corrupted record", Files.readString(dataFile));
    }

    @Test
    public void getResponse_addThenReload_persistsAddedTask() {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Kelore kelore = new Kelore(dataFile);

        kelore.getResponse("todo read a book");

        assertTrue(new Kelore(dataFile).getResponse("list").contains("read a book"));
    }

    @Test
    public void getResponse_invalidCommand_returnsFriendlyError() {
        Kelore kelore = new Kelore(temporaryDirectory.resolve("tasks.txt"));

        assertEquals("I don't recognise that command. Check the command and try again.",
                kelore.getResponse("dance"));
    }

    @Test
    public void getResponse_surroundingAndRepeatedWhitespace_acceptsCommand() {
        Kelore kelore = new Kelore(temporaryDirectory.resolve("tasks.txt"));

        assertTrue(kelore.getResponse("  todo   read a book  ").contains("read a book"));
        assertTrue(kelore.getResponse("  list  ").contains("read a book"));
    }

    @Test
    public void getResponse_nullOrBlankCommand_returnsFriendlyError() {
        Kelore kelore = new Kelore(temporaryDirectory.resolve("tasks.txt"));

        assertEquals("Please enter a command.", kelore.getResponse(null));
        assertEquals("Please enter a command.", kelore.getResponse("   "));
    }

    @Test
    public void getResponse_allSupportedCommands_dispatchesAndUpdatesTasks() {
        Kelore kelore = new Kelore(temporaryDirectory.resolve("tasks.txt"));

        assertEquals("See you next time - your tasks are saved.", kelore.getResponse("bye"));
        assertTrue(kelore.getResponse("todo read a book").contains("[T][ ] read a book"));
        assertTrue(kelore.getResponse("deadline submit report /by 2/9/2026 1800")
                .contains("[D][ ] submit report"));
        assertTrue(kelore.getResponse(
                "event conference /from 2/9/2026 0900 /to 3/9/2026 1700")
                .contains("[E][ ] conference"));

        assertTrue(kelore.getResponse("list").contains("3.[E][ ] conference"));
        assertTrue(kelore.getResponse("mark 1").contains("[T][X] read a book"));
        assertTrue(kelore.getResponse("unmark 1").contains("[T][ ] read a book"));
        assertTrue(kelore.getResponse("find report").contains("submit report"));
        assertTrue(kelore.getResponse("on 2/9/2026").contains("conference"));
        assertTrue(kelore.getResponse("delete 2").contains("submit report"));
        assertTrue(kelore.getResponse("list").contains("2.[E][ ] conference"));
    }

    @Test
    public void getResponse_invalidArguments_returnsSpecificFriendlyErrors() {
        Kelore kelore = new Kelore(temporaryDirectory.resolve("tasks.txt"));

        assertEquals("Please provide a valid task number after the command.",
                kelore.getResponse("mark"));
        assertEquals("There is no task with that number.", kelore.getResponse("delete 1"));
        assertEquals("The todo description cannot be empty.", kelore.getResponse("todo"));
        assertEquals("The search keyword cannot be empty.", kelore.getResponse("find"));
    }

    @Test
    public void getResponse_storageCannotCreateParent_returnsSaveError() throws Exception {
        Path parentFile = temporaryDirectory.resolve("not-a-directory");
        Files.writeString(parentFile, "content");
        Kelore kelore = new Kelore(parentFile.resolve("tasks.txt"));

        String response = kelore.getResponse("todo read a book");

        assertTrue(response.startsWith(
                "I couldn't save that change. Your task list was left unchanged.\n"));
        assertFalse(kelore.getResponse("list").contains("read a book"));
    }

    @Test
    public void getResponseDetails_invalidCommand_marksResponseAsError() {
        Kelore kelore = new Kelore(temporaryDirectory.resolve("tasks.txt"));

        assertTrue(kelore.getResponseDetails("dance").isError());
    }

    @Test
    public void getResponseDetails_validCommand_marksResponseAsSuccessful() {
        Kelore kelore = new Kelore(temporaryDirectory.resolve("tasks.txt"));

        assertFalse(kelore.getResponseDetails("list").isError());
    }

    @Test
    public void getResponse_freeCommand_returnsFreeTimeUsingInjectedClock() {
        Clock clock = Clock.fixed(
                Instant.parse("2026-09-21T01:00:00Z"), ZoneId.of("Asia/Singapore"));
        Kelore kelore = new Kelore(temporaryDirectory.resolve("tasks.txt"), clock);

        String response = kelore.getResponse("free 4");

        assertTrue(response.contains("Sep 21 2026, 9:00 AM to 1:00 PM"));
    }
}
