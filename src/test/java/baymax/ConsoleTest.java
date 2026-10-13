package baymax;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Exercises the real console entry point in isolated working directories. */
public class ConsoleTest {
    @TempDir
    public Path temporaryFolder;

    @Test
    public void main_bye_savesTasksAndStopsReading() throws Exception {
        String output = runConsole("todo 阅读\nbye\ntodo ignored\n", false);
        assertTrue(output.contains("I am satisfied with my care."));
        assertFalse(output.contains("ignored"));
        assertEquals("T | 0 | 阅读" + System.lineSeparator(),
                Files.readString(temporaryFolder.resolve("data/Baymax.txt")));
    }

    @Test
    public void main_endOfInput_savesWithoutBye() throws Exception {
        String output = runConsole("todo work\nmark 1\n", false);
        assertTrue(output.contains("[T][X] work"));
        assertEquals("T | 1 | work" + System.lineSeparator(),
                Files.readString(temporaryFolder.resolve("data/Baymax.txt")));
    }

    @Test
    public void main_corruptStorage_warnsAndPreservesOriginal() throws Exception {
        Path file = temporaryFolder.resolve("data/Baymax.txt");
        Files.createDirectories(file.getParent());
        Files.writeString(file, "invalid\n");
        String output = runConsole("todo blocked\nbye\n", false);
        assertTrue(output.contains("Skipped 1 invalid"));
        assertTrue(output.contains("read-only"));
        assertEquals("invalid\n", Files.readString(file));
    }

    @Test
    public void main_externalChange_reportsSaveErrorsAndContinuesAfterBye() throws Exception {
        String output = runConsole("todo work\nbye\nlist\n", true);
        assertTrue(output.contains("I cannot save your care plan right now."));
        assertTrue(output.contains("data file changed since it was loaded"));
        assertTrue(output.contains("Here is your current care plan:"));
        assertEquals(2, output.split("I cannot save your care plan right now\\.", -1).length - 1);
        assertEquals("T | 0 | external\n", Files.readString(temporaryFolder.resolve("data/Baymax.txt")));
    }

    /** Runs the console with a bounded lifetime and optionally changes storage after loading. */
    private String runConsole(String input, boolean changesStorage) throws Exception {
        List<String> command = new ArrayList<>();
        command.add(Path.of(System.getProperty("java.home"), "bin", "java").toString());
        command.add("-ea");
        // Give each child its own coverage file so the test worker cannot overwrite its results.
        String coverageAgent = System.getProperty("baymax.coverage.agent");
        if (coverageAgent != null) {
            command.add(coverageAgent.replaceFirst("destfile=[^,]+",
                    Matcher.quoteReplacement("destfile=" + Path.of(
                            System.getProperty("baymax.coverage.directory"),
                            "test-console-" + UUID.randomUUID() + ".exec"))));
        }
        command.add("-cp");
        command.add(Path.of(Baymax.class.getProtectionDomain().getCodeSource().getLocation().toURI()).toString());
        command.add("baymax.Baymax");
        Process process = new ProcessBuilder(command).directory(temporaryFolder.toFile())
                .redirectErrorStream(true).start();
        try {
            // A watchdog also bounds blocking reads if startup or shutdown regresses.
            process.onExit().orTimeout(15, TimeUnit.SECONDS).exceptionally(exception -> {
                process.destroyForcibly();
                return process;
            });
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append('\n');
                if (line.equals("How may I assist you?")) {
                    break;
                }
            }
            if (changesStorage) {
                Path file = temporaryFolder.resolve("data/Baymax.txt");
                Files.createDirectories(file.getParent());
                Files.writeString(file, "T | 0 | external\n");
            }
            process.getOutputStream().write(input.getBytes(StandardCharsets.UTF_8));
            process.getOutputStream().close();
            while ((line = reader.readLine()) != null) {
                output.append(line).append('\n');
            }
            assertTrue(process.waitFor(15, TimeUnit.SECONDS), "Console did not terminate");
            assertEquals(0, process.exitValue(), output.toString());
            return output.toString();
        } finally {
            process.destroyForcibly();
        }
    }
}
