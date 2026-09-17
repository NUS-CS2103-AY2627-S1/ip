package potatobot.ui.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.management.ManagementFactory;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Runs console sessions in separate JVMs with isolated save files and bounded
 * execution times.
 */
public class CliLauncherTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    public void main_bye_savesTasksAndStopsBeforeNextCommand() throws Exception {
        String output = runSession("todo read book\nbye\ntodo ignored\n", false);
        assertTrue(output.contains("PotatoBot:\n  Added: read book (Todo)\n\n"));
        assertTrue(output.contains("Hope to see you again soon!"));
        assertFalse(output.contains("Added: ignored"));
        assertEquals("[ ] read book (Todo)", Files.readString(temporaryDirectory.resolve("tasks.txt")));
    }

    @Test
    public void main_endOfInput_exitsWithoutFarewell() throws Exception {
        String output = runSession("list\n", false);
        assertTrue(output.contains("PotatoBot:\n  Nothing to see here...\n\n"));
        assertFalse(output.contains("Hope to see you again soon!"));
    }

    @Test
    public void main_corruptSaveFile_displaysErrorAndContinues() throws Exception {
        Files.writeString(temporaryDirectory.resolve("tasks.txt"), "corrupt");
        String output = runSession("list\nbye\n", false);
        assertTrue(output.contains("I couldn't load your saved tasks: Invalid completion status in line: corrupt"));
        assertTrue(output.contains("PotatoBot:\n  Nothing to see here...\n\n"));
    }

    @Test
    public void main_defaultSavePath_usesIsolatedWorkingDirectory() throws Exception {
        runSession("add default path\nbye\n", true);
        assertEquals("[ ] default path", Files.readString(temporaryDirectory.resolve("data/potatabot.txt")));
    }

    /**
     * Runs a fresh console process without accessing the user's task data.
     *
     * @param input          Commands to send before closing standard input.
     * @param useDefaultPath Whether to exercise the default relative save path.
     * @return Combined console output with normalized line endings.
     * @throws Exception If the process cannot start, finish, or exchange data.
     */
    private String runSession(String input, boolean useDefaultPath) throws Exception {
        List<String> command = new ArrayList<>();
        command.add(Path.of(System.getProperty("java.home"), "bin", "java").toString());
        // Separate files prevent the parent test JVM from overwriting child-process
        // coverage at shutdown.
        String coverageDirectory = System.getProperty("cli.coverageDirectory");
        if (coverageDirectory != null) {
            Path coverageFile = Path.of(coverageDirectory, temporaryDirectory.getFileName() + ".exec");
            Files.createDirectories(coverageFile.getParent());
            ManagementFactory.getRuntimeMXBean().getInputArguments().stream()
                    .filter(argument -> argument.startsWith("-javaagent:") && argument.contains("jacoco"))
                    .map(argument -> argument.replaceFirst("destfile=[^,]+",
                            java.util.regex.Matcher.quoteReplacement("destfile=" + coverageFile)))
                    .forEach(command::add);
        }
        command.addAll(List.of("-cp",
                Path.of(CliLauncher.class.getProtectionDomain().getCodeSource().getLocation().toURI()).toString(),
                CliLauncher.class.getName()));
        Path outputFile = temporaryDirectory.resolve("console.txt");
        ProcessBuilder builder = new ProcessBuilder(command)
                .directory(temporaryDirectory.toFile())
                .redirectErrorStream(true)
                .redirectOutput(outputFile.toFile());
        if (useDefaultPath) {
            builder.environment().remove("POTATOBOT_SAVE_FILE");
        } else {
            builder.environment().put("POTATOBOT_SAVE_FILE", "tasks.txt");
        }
        Process process = builder.start();
        try {
            try (var inputStream = process.getOutputStream()) {
                inputStream.write(input.getBytes(StandardCharsets.UTF_8));
            }
            assertTrue(process.waitFor(15, TimeUnit.SECONDS), "Console process timed out");
            String output = Files.readString(outputFile).replace("\r\n", "\n");
            assertEquals(0, process.exitValue(), output);
            return output;
        } finally {
            if (process.isAlive()) {
                process.destroyForcibly();
                process.waitFor(5, TimeUnit.SECONDS);
            }
        }
    }
}
