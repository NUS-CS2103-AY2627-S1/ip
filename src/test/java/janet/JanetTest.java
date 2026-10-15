package janet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class JanetTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    public void malformedStorage_requiresResetBeforeCommands_recoversAfterReset() throws IOException, JanetException {
        Path storageFile = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(storageFile, "not a saved task");
        final Janet janet = new Janet(new Storage(storageFile.toFile()));

        assertTrue(janet.isStorageRecoveryRequired());
        assertTrue(janet.getStorageRecoveryPrompt()
                .contains("All tasks take at least 3 positional arguments"));
        assertThrows(JanetException.class, () -> Janet.getResponse(janet, "list"));

        assertFalse(janet.isResolveStorageRecoveryRequest("keep"));
        assertTrue(janet.isStorageRecoveryRequired());
        assertTrue(janet.isResolveStorageRecoveryRequest(" reset "));

        Janet newJanet = janet.resolveStorageRecovery();
        assertFalse(newJanet.isStorageRecoveryRequired());
        assertEquals("", Files.readString(storageFile));
        assertEquals("No tasks listed!", Janet.getResponse(newJanet, "list").message());
    }
}
