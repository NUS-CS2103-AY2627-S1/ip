package gunna;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests the response status used by the graphical user interface. */
public class GunnaTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    public void getResponseWithStatus_validCommand_reportsSuccessfulResponse() {
        Gunna gunna = new Gunna(temporaryDirectory.resolve("tasks.txt").toString());

        Gunna.Response response = gunna.getResponseWithStatus("todo write a test");

        assertFalse(response.isError());
        assertTrue(response.message().contains("Task secured"));
    }

    @Test
    public void getResponseWithStatus_invalidCommand_reportsErrorResponse() {
        Gunna gunna = new Gunna(temporaryDirectory.resolve("tasks.txt").toString());

        Gunna.Response response = gunna.getResponseWithStatus("unknown-command");

        assertTrue(response.isError());
        assertTrue(response.message().contains("Command not recognized"));
    }
}
