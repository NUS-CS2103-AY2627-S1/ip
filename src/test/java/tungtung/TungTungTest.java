package tungtung;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Exercises console startup and search commands without touching the user's saved tasks. */
class TungTungTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    void main_corruptFile_preservesOriginalBytes() throws Exception {
        Path dataFile = temporaryDirectory.resolve("data/tungtung.txt");
        Files.createDirectories(dataFile.getParent());
        String original = "T | 0 | precious task\nbroken line\n";
        Files.writeString(dataFile, original);

        String output = runConsole("todo replacement\nbye\n");

        assertTrue(output.contains("Exiting without changing the saved file."));
        assertFalse(output.contains("I've added this task"));
        assertEquals(original, Files.readString(dataFile));
    }

    @Test
    void main_missingFile_allowsSaving() throws Exception {
        String output = runConsole("todo first task\nbye\n");

        assertTrue(output.contains("I've added this task"));
        assertEquals("[T][ ] first task", new Storage(
                temporaryDirectory.resolve("data/tungtung.txt").toString()).load().get(0).toString());
    }

    @Test
    void main_findThenDelete_usesFullListNumber() throws Exception {
        String output = runConsole("todo groceries\ntodo book\nfind book\nmark 2\ndelete 2\nbye\n");

        assertTrue(output.contains("Here are the matching tasks in your list:\n2.[T][ ] book"));
        assertTrue(output.contains("[T][X] book"));
        assertEquals("[T][ ] groceries", new Storage(
                temporaryDirectory.resolve("data/tungtung.txt").toString()).load().get(0).toString());
    }

    /** Runs the console in a temporary working directory with this test's Java runtime. */
    private String runConsole(String input) throws Exception {
        String java = Path.of(System.getProperty("java.home"), "bin", "java").toString();
        String classes = Path.of(TungTung.class.getProtectionDomain().getCodeSource()
                .getLocation().toURI()).toString();
        Process process = new ProcessBuilder(java, "-cp", classes, "tungtung.TungTung")
                .directory(temporaryDirectory.toFile()).redirectErrorStream(true).start();
        try {
            try (var commands = process.getOutputStream()) {
                commands.write(input.getBytes(StandardCharsets.UTF_8));
            }
            assertTrue(process.waitFor(15, TimeUnit.SECONDS), "Console should exit promptly.");
            String output = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            assertEquals(0, process.exitValue(), output);
            return output.replace("\r\n", "\n");
        } finally {
            process.destroyForcibly();
        }
    }
}
