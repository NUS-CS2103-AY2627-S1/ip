package avon;

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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import avon.storage.Storage;
import avon.ui.Ui;

class AvonTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void run_corruptedDataFile_doesNotProcessCommandsOrOverwriteFile() throws IOException {
        Path dataFile = temporaryDirectory.resolve("avon.txt");
        String corruptedData = "D\tfalse\tmissing date";
        Files.writeString(dataFile, corruptedData);
        ByteArrayInputStream input = new ByteArrayInputStream(
                "todo overwrite data\nbye\n".getBytes(StandardCharsets.UTF_8));
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Avon avon = new Avon(new Ui(input, new PrintStream(output)), new Storage(dataFile));

        avon.run();

        assertEquals(corruptedData, Files.readString(dataFile));
        assertFalse(output.toString(StandardCharsets.UTF_8).contains("I've added this task"));
        assertTrue(output.toString(StandardCharsets.UTF_8)
                .contains("Thy save file may be corrupted. Repair it or move it away before trying again."));
    }

    @Test
    void getResponse_commandsAcrossCalls_sharePersistedTasks() {
        Path dataFile = temporaryDirectory.resolve("avon.txt");
        Avon avon = new Avon(new Ui(), new Storage(dataFile));

        String addResponse = avon.getResponse("todo rehearse Hamlet");
        String listResponse = avon.getResponse("list");

        assertEquals("""
                Avon:\tBy thy command, I've added this task:
                        [T][ ] rehearse Hamlet
                Avon:\tNow thou hast 1 task in thy list.""", addResponse);
        assertEquals("""
                Avon:\tHere are the tasks in thy list:
                        1.[T][ ] rehearse Hamlet""", listResponse);
    }

    @Test
    void getResponse_invalidCommand_returnsGuidanceWithoutChangingStorage() {
        Path dataFile = temporaryDirectory.resolve("avon.txt");
        Avon avon = new Avon(new Ui(), new Storage(dataFile));

        String response = avon.getResponse("unknown");

        assertTrue(response.contains("Pardon, I beseech thee! I know not that command."));
        assertFalse(Files.exists(dataFile));
    }

    @Test
    void getResponse_corruptedDataFile_returnsRecoveryGuidance() throws IOException {
        Path dataFile = temporaryDirectory.resolve("avon.txt");
        Files.writeString(dataFile, "D\tfalse\tmissing date");
        Avon avon = new Avon(new Ui(), new Storage(dataFile));

        String response = avon.getResponse("list");

        assertTrue(response.contains("Thy save file may be corrupted."));
        assertTrue(response.contains("Repair it or move it away before trying again."));
    }

    @Test
    void run_inputEndsWithoutBye_closesCleanlyAfterProcessingCommands() {
        Path dataFile = temporaryDirectory.resolve("avon.txt");
        ByteArrayInputStream input = new ByteArrayInputStream(
                "todo rehearse Hamlet\n".getBytes(StandardCharsets.UTF_8));
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Avon avon = new Avon(new Ui(input, new PrintStream(output)), new Storage(dataFile));

        avon.run();

        assertTrue(output.toString(StandardCharsets.UTF_8).contains("I've added this task"));
        assertFalse(output.toString(StandardCharsets.UTF_8).contains("Fare thee well!"));
    }
}
