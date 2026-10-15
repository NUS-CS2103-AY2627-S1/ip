package ubis;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests console startup and termination in child processes so System.exit cannot stop JUnit.
 */
public class ConsoleTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    public void main_endOfInput_printsWelcomeAndExits() throws Exception {
        String output = runConsole("");
        assertTrue(output.contains(Ui.getWelcomeMessage()));
        assertEquals(1, output.split("Goodbye. See you soon!", -1).length - 1);
        assertTrue(Files.isRegularFile(temporaryDirectory.resolve("data/data.txt")));
    }

    @Test
    public void main_bye_printsOneGoodbyeAndStopsReading() throws Exception {
        String output = runConsole("todo read book\nbye\ntodo should not run\n");
        assertEquals(1, output.split("Goodbye. See you soon!", -1).length - 1);
        assertFalse(output.contains("should not run"));
        assertEquals("{T}{0}{read book}\n", Files.readString(temporaryDirectory.resolve("data/data.txt")));
    }

    @Test
    public void main_uppercaseBye_reportsUnknownCommandAndContinues() throws Exception {
        String output = runConsole("BYE\ntodo still running\nbye\n");
        assertTrue(output.contains("Unknown command \"BYE\""));
        assertTrue(output.contains("added: [T][ ] still running"));
        assertEquals("{T}{0}{still running}\n", Files.readString(temporaryDirectory.resolve("data/data.txt")));
    }

    @Test
    public void main_invalidByeAndBlankLine_continuesUntilValidBye() throws Exception {
        String output = runConsole("bye now\n\nhelp\nbye\n");
        assertTrue(output.contains("The \"bye\" command does not accept any arguments."));
        assertTrue(output.contains(Ui.Message.EMPTY_INPUT.getMessage()));
        assertTrue(output.contains(Ui.Message.HELP.getMessage()));
    }

    @Test
    public void main_corruptStorage_displaysWarningAndValidTasks() throws Exception {
        Path savePath = temporaryDirectory.resolve("data/data.txt");
        Files.createDirectories(savePath.getParent());
        Files.writeString(savePath, "broken\n{T}{1}{read book}\n");
        String output = runConsole("list\nbye\n");
        assertTrue(output.contains("Ubis skipped 1 invalid saved task record."));
        assertTrue(output.contains("1: [T][X] read book"));
    }

    @Test
    public void storage_defaultAndBarePaths_roundTripsInIsolatedDirectory() throws Exception {
        String output = runProcess("", "ubis.StorageProcess");
        assertEquals("true\n{T}{0}{default task}\nnull\n1: [T][ ] default task\n"
                + "true\n{T}{0}{default task}\n", output);
        assertEquals("{T}{0}{default task}\n", Files.readString(temporaryDirectory.resolve("data/data.txt")));
        assertEquals("{T}{0}{default task}\n", Files.readString(temporaryDirectory.resolve("bare.txt")));
    }

    /**
     * Runs the console entry point without exposing JUnit to System.exit.
     */
    private String runConsole(String input) throws IOException, InterruptedException {
        return runProcess(input, "ubis.Ubis");
    }

    /**
     * Runs the real console entry point with bounded execution and isolated data files.
     * Output is redirected to disk to avoid filling a pipe while waiting for exit.
     */
    private String runProcess(String input, String mainClass) throws IOException, InterruptedException {
        Path inputPath = temporaryDirectory.resolve("input.txt");
        Path outputPath = temporaryDirectory.resolve("output.txt");
        Files.writeString(inputPath, input);
        Process process = new ProcessBuilder(createCommand(mainClass))
                .directory(temporaryDirectory.toFile())
                .redirectInput(inputPath.toFile())
                .redirectErrorStream(true)
                .redirectOutput(outputPath.toFile())
                .start();
        try {
            assertTrue(process.waitFor(10, TimeUnit.SECONDS), "Console did not exit within ten seconds");
            String output = Files.readString(outputPath);
            assertEquals(0, process.exitValue(), output);
            return output.replace("\r\n", "\n");
        } finally {
            process.destroyForcibly();
            process.waitFor(5, TimeUnit.SECONDS);
        }
    }

    /**
     * Builds the child JVM command with a fixed locale and the test classpath.
     */
    private List<String> createCommand(String mainClass) {
        List<String> command = new ArrayList<>();
        command.add(Path.of(System.getProperty("java.home"), "bin", "java").toString());
        command.add("-Duser.language=en");
        command.add("-Duser.country=US");
        addCoverageArguments(command);
        command.add("-cp");
        command.add(System.getProperty("ubis.test.classpath"));
        command.add(mainClass);
        return command;
    }

    /**
     * Forwards coverage instrumentation with a separate destination for child process data.
     */
    private void addCoverageArguments(List<String> command) {
        for (String argument : ManagementFactory.getRuntimeMXBean().getInputArguments()) {
            if (argument.startsWith("-javaagent:") && argument.contains("jacoco")) {
                String destination = "destfile=" + System.getProperty("ubis.test.coverage.path");
                command.add(argument.replaceAll("destfile=[^,]*",
                        java.util.regex.Matcher.quoteReplacement(destination)));
            }
        }
    }

}
