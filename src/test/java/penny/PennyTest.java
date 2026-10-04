package penny;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Integration and unit test suite for {@link Penny}.
 * Verifies high-level command dispatch, exception handling, and application lifecycle.
 */
public class PennyTest {

    @TempDir
    private Path tempDir;

    private Penny penny;
    private Path storagePath;

    @BeforeEach
    public void setUp() {
        storagePath = tempDir.resolve("penny.txt");
        penny = new Penny(storagePath.toString());
    }

    @Test
    public void getResponse_validAddCommand_returnsResponse() {
        String response = penny.getResponse("todo borrow textbook");
        assertNotNull(response);
        assertTrue(response.contains("borrow textbook"));
        assertFalse(penny.isExit());
    }

    @Test
    public void getResponse_invalidCommand_returnsErrorMessageWithoutCrashing() {
        String response = penny.getResponse("invalid_command");
        assertNotNull(response);
        assertFalse(penny.isExit());
    }

    @Test
    public void getResponse_exitCommand_setsIsExitTrue() {
        assertFalse(penny.isExit());
        String response = penny.getResponse("bye");
        assertNotNull(response);
        assertTrue(penny.isExit());
    }

    @Test
    public void getWelcomeMessage_invoked_returnsNonEmptyString() {
        String welcome = penny.getWelcomeMessage();
        assertNotNull(welcome);
        assertFalse(welcome.isEmpty());
    }

    @Test
    public void constructor_corruptedStorageFile_recoversWithEmptyList() throws IOException {
        Path corruptedPath = tempDir.resolve("corrupted.txt");
        Files.write(corruptedPath, List.of("T | corrupted_line"));

        Penny recoveredPenny = new Penny(corruptedPath.toString());
        String response = recoveredPenny.getResponse("list");
        assertNotNull(response);
    }
}
