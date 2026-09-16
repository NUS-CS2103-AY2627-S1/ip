package brucli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import brucli.ui.CommandResponse;
import brucli.ui.ResponseType;

public class BruCliAppTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    public void dummyTest() {
        assertEquals(2, 2);
    }

    @Test
    public void anotherDummyTest() {
        assertEquals(4, 4);
    }

    @Test
    public void getResponse_help_preservesTasksAndReturnsGuide() {
        Path taskFile = temporaryDirectory.resolve("tasks.txt");
        BruCliApp app = new BruCliApp(taskFile.toString());
        app.getResponse("todo Read a chapter");

        CommandResponse response = app.getResponse("  HeLp  ");

        assertEquals(ResponseType.STANDARD, response.type());
        assertTrue(response.text().contains("todo DESCRIPTION"));
        assertTrue(response.text().contains("deadline Submit assignment /by 2026-09-30 1800"));
        BruCliApp reloadedApp = new BruCliApp(taskFile.toString());
        assertTrue(reloadedApp.getResponse("list").text().contains("Read a chapter"));
    }

    @Test
    public void getResponse_invalidCommandSyntax_returnsErrorResponse() {
        Path taskFile = temporaryDirectory.resolve("tasks.txt");
        BruCliApp app = new BruCliApp(taskFile.toString());

        CommandResponse response = app.getResponse("todo");

        assertEquals(ResponseType.ERROR, response.type());
        assertFalse(response.text().isBlank());
    }
}
