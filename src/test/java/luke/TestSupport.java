package luke;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Shared helpers for Luke's JUnit tests.
 */
final class TestSupport {
    private TestSupport() {
    }

    static void assertContains(String output, String expected) {
        assertTrue(output.contains(expected), "Expected output to contain: " + expected);
    }

    static void assertEquals(Object expected, Object actual) {
        org.junit.jupiter.api.Assertions.assertEquals(expected, actual);
    }

    static String runLuke(String input) {
        Path storageFile = createTempStorageFile();
        try {
            return runLuke(input, storageFile);
        } finally {
            deleteTempStorageFile(storageFile);
        }
    }

    static String runLuke(String input, Path storageFile) {
        PrintStream originalOut = System.out;
        java.io.InputStream originalIn = System.in;
        String originalStoragePath = System.getProperty("luke.storage.path");
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        try {
            System.setProperty("luke.storage.path", storageFile.toString());
            System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
            System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));
            Luke.main(new String[0]);
            return output.toString(StandardCharsets.UTF_8);
        } finally {
            if (originalStoragePath == null) {
                System.clearProperty("luke.storage.path");
            } else {
                System.setProperty("luke.storage.path", originalStoragePath);
            }
            System.setIn(originalIn);
            System.setOut(originalOut);
        }
    }

    static Path createTempStorageFile() {
        try {
            return Files.createTempDirectory("luke-test").resolve("itemlist.txt");
        } catch (IOException e) {
            throw new AssertionError("Could not create temp storage path", e);
        }
    }

    static Path createTempStorageDirectory() {
        try {
            return Files.createTempDirectory("luke-test-storage-dir");
        } catch (IOException e) {
            throw new AssertionError("Could not create temp storage directory", e);
        }
    }

    static void writeString(Path path, String content) {
        try {
            Files.createDirectories(path.getParent());
            Files.writeString(path, content, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new AssertionError("Could not write temp storage data", e);
        }
    }

    static void deleteTempStorageFile(Path storageFile) {
        try {
            Files.deleteIfExists(storageFile);
            Files.deleteIfExists(storageFile.getParent());
        } catch (IOException e) {
            throw new AssertionError("Could not delete temp storage path", e);
        }
    }

    static void deleteTempStorageDirectory(Path storageDirectory) {
        try {
            Files.deleteIfExists(storageDirectory);
        } catch (IOException e) {
            throw new AssertionError("Could not delete temp storage directory", e);
        }
    }
}
