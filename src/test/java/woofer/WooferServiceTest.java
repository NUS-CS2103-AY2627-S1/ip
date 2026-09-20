package woofer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import woofer.exception.WooferException;
import woofer.storage.Storage;

/** Tests command rejection and storage warnings through the shared application service. */
public class WooferServiceTest {
    @TempDir
    private Path directory;

    /**
     * Checks that a rejected task does not change the list or replace the previous undo action.
     *
     * @throws WooferException when valid commands fail.
     */
    @Test
    public void rejectedCommandPreservesTasksAndUndo() throws WooferException {
        WooferService service = new WooferService(new Storage(directory.resolve("tasks.txt")));
        service.execute("  todo   walk dog  ");
        assertThrows(WooferException.class, () -> service.execute("todo broken|record"));
        assertThrows(WooferException.class, () -> service.execute("delete 99"));
        assertTrue(service.execute(" list ").message().contains("walk dog"));
        service.execute("undo");
        assertTrue(service.execute("list").message().contains("list (0)"));
    }

    /**
     * Checks that startup failure prevents later task changes from overwriting the original.
     *
     * @throws IOException when creating the fixture fails.
     * @throws WooferException when valid commands fail.
     */
    @Test
    public void failedLoadDisablesSaving() throws IOException, WooferException {
        Path file = directory.resolve("tasks.txt");
        Files.writeString(file, "corrupted");
        WooferService service = new WooferService(new Storage(file));
        assertTrue(service.hasLoadingError());
        assertTrue(service.getLoadingWarning().contains("saving is disabled"));
        assertTrue(service.execute("todo temporary").warning());
        assertTrue(Files.readString(file).equals("corrupted"));
    }

    /**
     * Checks warning metadata and recovery when the data directory becomes unavailable.
     *
     * @throws IOException when changing the temporary directory fails.
     * @throws WooferException when valid commands fail.
     */
    @Test
    public void failedSaveIsVisibleAndCanRecover() throws IOException, WooferException {
        Path parent = directory.resolve("data");
        WooferService service = new WooferService(new Storage(parent.resolve("tasks.txt")));
        Files.writeString(parent, "blocks directory creation");
        WooferService.Response response = service.execute("todo walk dog");
        assertTrue(response.warning());
        assertTrue(response.message().contains("only in memory"));
        assertFalse(service.execute("list").warning());
        Files.delete(parent);
        assertFalse(service.execute("todo buy food").warning());
        assertTrue(Files.readString(parent.resolve("tasks.txt")).contains("walk dog"));
    }

    /**
     * Verifies all task types survive a new service instance with their status and order.
     *
     * @throws Exception when commands or temporary storage fail.
     */
    @Test
    public void allTaskTypesPersistAcrossRestart() throws Exception {
        Storage storage = new Storage(directory.resolve("tasks.txt"));
        WooferService service = new WooferService(storage);
        assertFalse(service.hasLoadingError());
        assertFalse(service.execute("todo walk dog").warning());
        service.execute("deadline food /by 2028-02-29");
        service.execute("event holiday /from 2026-12-31 /to 2027-01-02");
        service.execute("mark 2");
        String expected = "Woof! I fetched your task list (3):\n"
                + "1.[T][ ] walk dog\n2.[D][X] food (by: Feb 29 2028)\n"
                + "3.[E][ ] holiday (from: Dec 31 2026 to: Jan 02 2027)";
        assertEquals(expected,
                new WooferService(storage).execute("list").message());
    }

    /**
     * Verifies search filtering, result numbering, and absence of matches.
     *
     * @throws Exception when commands fail.
     */
    @Test
    public void searchDoesNotChangeTasksOrUndo() throws Exception {
        WooferService service = new WooferService(new Storage(directory.resolve("tasks.txt")));
        service.execute("todo unrelated");
        service.execute("todo Walk Dog");
        service.execute("todo feed dog");
        String before = service.execute("list").message();
        assertEquals("Sniff sniff! Here are the matching tasks:\n"
                + "1.[T][ ] Walk Dog\n2.[T][ ] feed dog", service.execute("find DOG").message());
        assertTrue(service.execute("find cat").message().contains("No matching tasks"));
        assertEquals(before, service.execute("list").message());
        service.execute("undo");
        assertFalse(service.execute("list").message().contains("feed dog"));
    }

    /**
     * Verifies undo restores deletion at the original position, including its done state.
     *
     * @throws Exception when commands or storage fail.
     */
    @Test
    public void undoDeletionRestoresOrderAndStatusOnDisk() throws Exception {
        Storage storage = new Storage(directory.resolve("tasks.txt"));
        WooferService service = new WooferService(storage);
        service.execute("todo first");
        service.execute("todo middle");
        service.execute("todo last");
        service.execute("mark 2");
        String before = service.execute("list").message();
        service.execute("delete 2");
        assertFalse(service.execute("list").message().contains("middle"));
        service.execute("undo");
        assertEquals(before,
                new WooferService(storage).execute("list").message());
        assertThrows(WooferException.class, () -> service.execute("undo"));
    }

    /**
     * Verifies undo restores both completion states, including repeated mark and unmark commands.
     *
     * @throws Exception when commands fail.
     */
    @Test
    public void undoStatusChangesRestoresPreviousState() throws Exception {
        WooferService service = new WooferService(new Storage(directory.resolve("tasks.txt")));
        service.execute("todo walk");
        service.execute("mark 1");
        service.execute("undo");
        assertTrue(service.execute("list").message().contains("[T][ ] walk"));
        service.execute("mark 1");
        service.execute("mark 1");
        service.execute("undo");
        assertTrue(service.execute("list").message().contains("[T][X] walk"));
        service.execute("unmark 1");
        service.execute("undo");
        assertTrue(service.execute("list").message().contains("[T][X] walk"));
        service.execute("unmark 1");
        service.execute("unmark 1");
        service.execute("undo");
        assertTrue(service.execute("list").message().contains("[T][ ] walk"));
    }

    /**
     * Verifies a full list rejects additions without replacing the previous undo action.
     *
     * @throws Exception when commands or storage fail.
     */
    @Test
    public void capacityFailurePreservesUndo() throws Exception {
        Path file = directory.resolve("tasks.txt");
        Files.writeString(file, "T | 0 | chore\n".repeat(100));
        WooferService service = new WooferService(new Storage(file));
        service.execute("mark 100");
        assertThrows(WooferException.class, () -> service.execute("todo excess"));
        service.execute("undo");
        assertFalse(service.execute("list").message().contains("[X]"));
        assertTrue(service.execute("list").message().contains("list (100)"));
    }

    /**
     * Verifies read-only commands do not create a save file and exit has explicit metadata.
     *
     * @throws Exception when commands fail.
     */
    @Test
    public void emptySessionAndExitDoNotWrite() throws Exception {
        Path file = directory.resolve("tasks.txt");
        WooferService service = new WooferService(new Storage(file));
        assertThrows(WooferException.class, () -> service.execute("undo"));
        assertFalse(service.execute("list").exits());
        assertFalse(service.execute("find dog").warning());
        WooferService.Response exit = service.execute("bye");
        assertTrue(exit.exits());
        assertFalse(exit.warning());
        assertFalse(Files.exists(file));
    }

}
