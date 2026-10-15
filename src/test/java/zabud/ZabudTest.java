package zabud;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import zabud.session.Session;

/**
 * Tests the command-response adapter used by the JavaFX interface.
 */
class ZabudTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void getResponse_executesCommandsAndReportsExit() {
        Zabud zabud = new Zabud(new Session(temporaryDirectory.resolve("session.txt")));

        Zabud.Response added = zabud.getResponse("todo read book");
        assertTrue(added.message().contains("read book"));
        assertTrue(added.shouldContinue());
        assertFalse(added.isError());

        Zabud.Response invalid = zabud.getResponse("todo");
        assertTrue(invalid.isError());

        Zabud.Response listed = zabud.getResponse("list");
        assertTrue(listed.message().contains("1.[T][ ] read book"));

        Zabud.Response exit = zabud.getResponse("bye");
        assertTrue(exit.message().contains("Hope to see you again soon"));
        assertFalse(exit.shouldContinue());
        assertFalse(exit.isError());
    }

    @Test
    void getResponse_reportsStorageFailureWithoutDiscardingCommandOutput() {
        Zabud zabud = new Zabud(new Session(temporaryDirectory));

        Zabud.Response response = zabud.getResponse("todo hold court");

        assertTrue(response.message().contains("could not save your session"));
        assertTrue(response.message().contains("hold court"));
        assertTrue(response.shouldContinue());
        assertFalse(response.isError());
    }
}
