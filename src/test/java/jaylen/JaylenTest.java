package jaylen;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests Jaylen's safe behavior when its save file cannot be loaded. */
public class JaylenTest {
    private static final String STORAGE_PROPERTY = "jaylen.storage";

    @TempDir
    private Path temporaryDirectory;

    private String previousStoragePath;
    private Path storageFile;

    @BeforeEach
    public void useMalformedStorageFile() throws IOException {
        previousStoragePath = System.getProperty(STORAGE_PROPERTY);
        storageFile = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(storageFile, "not a valid task");
        System.setProperty(STORAGE_PROPERTY, storageFile.toString());
    }

    @AfterEach
    public void restoreStorageProperty() {
        if (previousStoragePath == null) {
            System.clearProperty(STORAGE_PROPERTY);
        } else {
            System.setProperty(STORAGE_PROPERTY, previousStoragePath);
        }
    }

    @Test
    public void constructor_malformedStorage_surfacesWarningAndPreservesFile() throws IOException {
        Jaylen jaylen = new Jaylen();

        assertNotNull(jaylen.getStartupErrorMessage());
        assertTrue(jaylen.getStartupErrorMessage().contains("save file"));
        assertTrue(jaylen.isErrorResponse(jaylen.getResponse("todo keep data safe")));
        assertEquals("not a valid task", Files.readString(storageFile));
    }
}
