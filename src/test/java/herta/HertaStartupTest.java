package herta;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests startup validation and the disabled state after storage failures.
 */
class HertaStartupTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void startup_invalidConfiguredPathDisablesCommandsWithoutThrowing() {
        Herta herta = new Herta("bad\u0000path");

        assertFalse(herta.isReady());
        assertTrue(herta.getLoadingError().contains("configured data path"));
        assertEquals(ResponseCategory.ERROR, herta.getResponse("todo should not save")
                .getResponseCategory());
    }

    @Test
    void startup_missingArchiveFile_isValid() throws Exception {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");

        Herta herta = new Herta(dataFile.toString());

        assertTrue(herta.isReady());
        assertEquals("The archive has nothing to show. Complete a task before archiving it.",
                herta.getResponse("archived").getMessage());
    }

    @Test
    void startup_invalidArchiveFile_exposesErrorAndRejectsResponses() throws Exception {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Path archiveFile = dataFile.resolveSibling("archive.txt");
        Files.writeString(archiveFile, "X | 0 | invalid\n");

        Herta herta = new Herta(dataFile.toString());

        assertFalse(herta.isReady());
        assertTrue(herta.getLoadingError().startsWith("Failed to load archived tasks at line "));
        HertaResponse response = herta.getResponse("list");
        assertEquals(ResponseCategory.ERROR, response.getResponseCategory());
        assertEquals(herta.getLoadingError(), response.getMessage());
    }

    @Test
    void startupConflictingArchivePath_failsBeforeProcessingCommands() throws Exception {
        Path archiveNamedActiveFile = temporaryDirectory.resolve("archive.txt");
        Files.writeString(archiveNamedActiveFile, "T | 0 | task\n");

        Herta herta = new Herta(archiveNamedActiveFile.toString());

        assertFalse(herta.isReady());
        assertTrue(herta.getLoadingError().startsWith("Failed to load archived tasks: "));
        assertEquals(ResponseCategory.ERROR, herta.getResponse("archived").getResponseCategory());
    }

    @Test
    void startup_malformedActiveData_disablesInteraction() throws Exception {
        Path dataFile = temporaryDirectory.resolve("malformed-active.txt");
        Files.write(dataFile, List.of("X | 0 | invalid type"), StandardCharsets.UTF_8);

        Herta herta = new Herta(dataFile.toString());

        assertFalse(herta.isReady());
        assertTrue(herta.getLoadingError().startsWith("Failed to load tasks at line "));
        assertEquals(ResponseCategory.ERROR, herta.getResponse("todo no save")
                .getResponseCategory());
    }
}
