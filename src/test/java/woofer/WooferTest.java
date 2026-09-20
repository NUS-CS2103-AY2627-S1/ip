package woofer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Runs the real CLI in a separate JVM and temporary working directory to protect user data. */
public class WooferTest {
    @TempDir
    private Path directory;

    /**
     * Checks the entry point handles errors, saves tasks, and stops immediately at bye.
     *
     * @throws Exception when launching the child JVM or accessing fixtures fails.
     */
    @Test
    public void commandSessionRecoversFromErrorsAndExits() throws Exception {
        String output = runSession("todo\ntodo walk dog\nmark 1\nlist\nbye\ntodo ignored\n");
        assertTrue(output.contains("Required format: todo <description>."));
        assertTrue(output.contains("1.[T][X] walk dog"));
        assertTrue(output.contains("Time for a nap"));
        assertEquals("T | 1 | walk dog", Files.readString(directory.resolve("data/woofer.txt")).strip());
        assertFalse(output.contains("ignored"));
    }

    /**
     * Checks end-of-input exits cleanly without writing a data file.
     *
     * @throws Exception when launching the child JVM fails.
     */
    @Test
    public void endOfInputExitsCleanly() throws Exception {
        String output = runSession("");
        assertTrue(output.contains("Woofer"));
        assertFalse(output.contains("Time for a nap"));
        assertFalse(Files.exists(directory.resolve("data/woofer.txt")));
    }

    /**
     * Checks a corrupt file's recovery warning is visible through the real CLI.
     *
     * @throws Exception when launching the child JVM or creating fixtures fails.
     */
    @Test
    public void corruptFileWarningIsVisible() throws Exception {
        Path file = directory.resolve("data/woofer.txt");
        Files.createDirectories(file.getParent());
        Files.writeString(file, "corrupted");
        String output = runSession("todo temporary\nbye\n");
        assertTrue(output.contains("saving is disabled"));
        assertTrue(output.contains("only in memory"));
        assertEquals("corrupted", Files.readString(file));
    }

    /**
     * Runs commands with a bounded wait and captures output in a file to avoid pipe blocking.
     *
     * @param commands newline-separated input.
     * @return combined standard output and error.
     * @throws Exception when the subprocess or fixture setup fails.
     */
    private String runSession(String commands) throws Exception {
        Path input = directory.resolve("input.txt");
        Path output = directory.resolve("output.txt");
        Files.writeString(input, commands, StandardCharsets.UTF_8);
        String executable = System.getProperty("os.name").startsWith("Windows") ? "java.exe" : "java";
        Path java = Path.of(System.getProperty("java.home"), "bin", executable);
        Path classes = Path.of(Woofer.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        Process process = new ProcessBuilder(java.toString(), "-ea", "-cp", classes.toString(), "woofer.Woofer")
                .directory(directory.toFile())
                .redirectInput(input.toFile())
                .redirectErrorStream(true)
                .redirectOutput(output.toFile())
                .start();
        try {
            assertTrue(process.waitFor(15, TimeUnit.SECONDS), "CLI did not exit within 15 seconds");
            String result = Files.readString(output, StandardCharsets.UTF_8);
            assertEquals(0, process.exitValue(), result);
            return result;
        } finally {
            process.destroyForcibly();
        }
    }
}
