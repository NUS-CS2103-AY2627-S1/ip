package gongrilla;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests error recovery and persistence through the public chatbot interface. */
class GongrillaFailureTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    void getResponse_corruptSavedData_reportsFailureWithoutOverwriting() throws IOException {
        Path path = temporaryDirectory.resolve("tasks.txt");
        String corruptData = "not a task\n";
        Files.writeString(path, corruptData);
        Gongrilla gongrilla = new Gongrilla(path);
        for (String input : List.of("list", "todo new task")) {
            assertTrue(gongrilla.getResponse(input).contains("cannot read saved tasks"));
            assertEquals("Error", gongrilla.getCommandType());
            assertEquals(corruptData, Files.readString(path));
        }
    }

    @Test
    void getResponse_saveFails_leavesTaskListUnchangedAndCanRecover() throws IOException {
        Path parent = temporaryDirectory.resolve("data");
        Gongrilla gongrilla = new Gongrilla(parent.resolve("tasks.txt"));
        Files.writeString(parent, "blocking file");
        String response = gongrilla.getResponse("todo buy bananas");
        assertTrue(response.contains("cannot save"));
        assertTrue(response.contains("Task list was not changed"));
        assertEquals("Error", gongrilla.getCommandType());
        assertFalse(gongrilla.getResponse("list").contains("buy bananas"));
        Files.delete(parent);
        assertTrue(gongrilla.getResponse("todo buy bananas").contains("buy bananas"));
        assertEquals("AddCommand", gongrilla.getCommandType());
        assertTrue(new Gongrilla(parent.resolve("tasks.txt")).getResponse("list").contains("buy bananas"));
    }

    @Test
    void getResponse_invalidDatesAndRanges_reportsHelpfulErrorsWithoutSaving() {
        Path path = temporaryDirectory.resolve("tasks.txt");
        Gongrilla gongrilla = new Gongrilla(path);
        String invalidDate = gongrilla.getResponse("deadline report /by 31/2/2026");
        assertTrue(invalidDate.contains("D/M/YYYY"));
        assertTrue(invalidDate.contains("Type help"));
        assertEquals("Error", gongrilla.getCommandType());
        assertTrue(gongrilla.getResponse("event lunch /from 2/9/2026 /to 1/9/2026")
                .contains("cannot be after"));
        assertEquals("Error", gongrilla.getCommandType());
        assertFalse(Files.exists(path));
    }

    @Test
    void getResponse_successErrorSuccess_resetsCommandClassification() {
        Gongrilla gongrilla = new Gongrilla(temporaryDirectory.resolve("tasks.txt"));
        gongrilla.getResponse("todo read");
        assertEquals("AddCommand", gongrilla.getCommandType());
        gongrilla.getResponse("mark 99");
        assertEquals("Error", gongrilla.getCommandType());
        assertTrue(gongrilla.getResponse("mark 1").contains("[X] read"));
        assertEquals("MarkCommand", gongrilla.getCommandType());
    }

    @Test
    void getResponse_bye_returnsFarewellWithoutWriting() {
        Path path = temporaryDirectory.resolve("tasks.txt");
        Gongrilla gongrilla = new Gongrilla(path);
        assertTrue(gongrilla.getResponse("bye").contains("Bring banana next time"));
        assertEquals("ExitCommand", gongrilla.getCommandType());
        assertFalse(Files.exists(path));
    }
}
