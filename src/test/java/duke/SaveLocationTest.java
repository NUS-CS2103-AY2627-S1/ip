package duke;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests that save paths stay inside the supplied launch folder. */
class SaveLocationTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void resolve_launchFolderWithSpacesAndUnicode_keepsDataInsideThatFolder() {
        Path workingFolder = temporaryDirectory.resolve("My tasks 王");

        assertEquals(workingFolder.resolve("data/wangsa.txt"), SaveLocation.resolve(workingFolder));
    }

    @Test
    void resolve_sourceRun_usesProjectWorkingFolder() {
        assertEquals(temporaryDirectory.resolve("data/wangsa.txt"),
                SaveLocation.resolve(temporaryDirectory));
    }
}
