package xian;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class StorageTest {

    @TempDir
    Path temporaryDirectory;

    @Test
    public void load_invalidDateInSaveFile_throwsXianException() throws IOException {
        Path saveFile = temporaryDirectory.resolve("xian.txt");
        Files.writeString(saveFile, "D | 0 | submit report | 31/2/2026 1800");

        assertThrows(XianException.class, () -> new Storage(saveFile.toString()).load());
    }

    @Test
    public void load_eventWithInvalidTimeRange_throwsXianException() throws IOException {
        Path saveFile = temporaryDirectory.resolve("xian.txt");
        Files.writeString(saveFile,
                "E | 0 | project meeting | 10/9/2026 1700 | 10/9/2026 1600");

        assertThrows(XianException.class, () -> new Storage(saveFile.toString()).load());
    }

    @Test
    public void load_invalidStatusInSaveFile_throwsXianException() throws IOException {
        Path saveFile = temporaryDirectory.resolve("xian.txt");
        Files.writeString(saveFile, "T | 2 | read book");

        assertThrows(XianException.class, () -> new Storage(saveFile.toString()).load());
    }

    @Test
    public void load_missingFieldInSaveFile_throwsXianException() throws IOException {
        Path saveFile = temporaryDirectory.resolve("xian.txt");
        Files.writeString(saveFile, "T | 0");

        assertThrows(XianException.class, () -> new Storage(saveFile.toString()).load());
    }
}
