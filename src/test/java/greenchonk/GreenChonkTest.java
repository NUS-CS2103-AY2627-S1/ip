package greenchonk;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import greenchonk.storage.Storage;
import greenchonk.ui.Ui;

class GreenChonkTest {
    @TempDir
    private Path tempDirectory;

    @Test
    void getResponse_consecutiveCommands_shareTaskState() {
        GreenChonk greenChonk = new GreenChonk(tempDirectory.resolve("data/tasks.txt").toString());

        String addResponse = greenChonk.getResponse("todo read book");
        String listResponse = greenChonk.getResponse("list");

        assertTrue(addResponse.contains("[T][ ] read book"));
        assertTrue(listResponse.contains("1.[T][ ] read book"));
    }

    @Test
    void getResponse_invalidCommand_returnsUserFacingError() {
        GreenChonk greenChonk = new GreenChonk(tempDirectory.resolve("data/tasks.txt").toString());

        String response = greenChonk.getResponse("   ");

        assertTrue(response.contains("Please enter a command. Try: todo buy milk"));
    }

    @Test
    void getResponse_editCommand_updatesSharedTaskState() {
        GreenChonk greenChonk = new GreenChonk(tempDirectory.resolve("data/tasks.txt").toString());
        greenChonk.getResponse("deadline submit draft /by 2026-08-28");
        greenChonk.getResponse("mark 1");

        String editResponse = greenChonk.getResponse(
                "edit 1 /description submit final report");
        String listResponse = greenChonk.getResponse("list");

        assertTrue(editResponse.contains("Before: [D][X] submit draft (by: Aug 28 2026)"));
        assertTrue(editResponse.contains("After:  [D][X] submit final report (by: Aug 28 2026)"));
        assertTrue(listResponse.contains("1.[D][X] submit final report (by: Aug 28 2026)"));
    }

    @Test
    void getResponse_exitCommand_returnsFarewellWithoutAnimationFrames() {
        GreenChonk greenChonk = new GreenChonk(tempDirectory.resolve("data/tasks.txt").toString());

        String response = greenChonk.getResponse("bye");

        assertTrue(response.contains("Task backpack zipped! I'm rolling off for now. See you soon!"));
        assertFalse(response.contains("\r"));
        assertFalse(response.contains("___"));
    }

    @Test
    void run_commandsUntilBye_tasksPersistedAndFarewellShown() throws IOException {
        Path dataFile = tempDirectory.resolve("data/tasks.txt");
        ByteArrayInputStream input = new ByteArrayInputStream(
                "todo test the command loop\nbye\nlist\n"
                        .getBytes(StandardCharsets.UTF_8));
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Ui ui = new Ui(input, new PrintStream(output, true, StandardCharsets.UTF_8));
        GreenChonk greenChonk = new GreenChonk(new Storage(dataFile.toString()), ui);

        greenChonk.run();

        String text = output.toString(StandardCharsets.UTF_8);
        assertTrue(text.contains("Chomped and packed this task"));
        assertTrue(text.contains("Task backpack zipped"));
        assertFalse(text.contains("Here are the tasks Green Chonk is carrying"));
        assertEquals(List.of("T | 0 | test the command loop"),
                Files.readAllLines(dataFile, StandardCharsets.UTF_8));
    }

    @Test
    void run_inputEndsWithoutBye_loopStopsAfterReportingLoadingFailure() throws IOException {
        Path dataFile = tempDirectory.resolve("data/tasks.txt");
        Files.createDirectories(dataFile.getParent());
        Files.writeString(dataFile, "invalid data", StandardCharsets.UTF_8);
        ByteArrayInputStream input = new ByteArrayInputStream(
                "list\n".getBytes(StandardCharsets.UTF_8));
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Ui ui = new Ui(input, new PrintStream(output, true, StandardCharsets.UTF_8));
        GreenChonk greenChonk = new GreenChonk(new Storage(dataFile.toString()), ui);

        greenChonk.run();

        String text = output.toString(StandardCharsets.UTF_8);
        assertTrue(text.contains("couldn't load saved tasks"));
        assertTrue(text.contains("not carrying any tasks yet"));
        assertFalse(text.contains("Task backpack zipped"));
    }
}
