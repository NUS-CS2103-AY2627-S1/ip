package gongrilla;

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

/** Runs the real console in isolated JVMs so default paths and locales cannot affect user data. */
class ConsoleIntegrationTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    void main_commandsThenBye_persistsTasksAndStopsBeforeNextCommand() throws Exception {
        String output = runConsole("todo read book\nmark 1\nlist\nbye\ntodo ignored\n", "en", "US");
        assertTrue(output.contains("Human back"));
        assertTrue(output.contains("[T][X] read book"));
        assertTrue(output.contains("Bring banana next time"));
        assertFalse(output.contains("ignored"));
        String data = Files.readString(temporaryDirectory.resolve("data/gongrilla.txt"));
        assertTrue(data.contains("read+book"));
        assertTrue(data.contains("M | 0"));
        assertFalse(data.contains("ignored"));
    }

    @Test
    void main_endOfInput_exitsWithoutGoodbyeOrDataFile() throws Exception {
        String output = runConsole("help\n", "en", "US");
        assertTrue(output.contains("help - show this guide"));
        assertFalse(output.contains("Bring banana next time"));
        assertFalse(Files.exists(temporaryDirectory.resolve("data/gongrilla.txt")));
    }

    @Test
    void main_corruptData_reportsLoadErrorAndDoesNotAcceptCommands() throws Exception {
        Path path = temporaryDirectory.resolve("data/gongrilla.txt");
        Files.createDirectories(path.getParent());
        Files.writeString(path, "corrupt data");
        String output = runConsole("todo ignored\n", "en", "US");
        assertTrue(output.contains("cannot read saved tasks"));
        assertTrue(output.contains("Invalid data on line 1"));
        assertFalse(output.contains("New todo"));
        assertEquals("corrupt data", Files.readString(path));
    }

    @Test
    void main_chineseLocale_preservesUnicodeAndEnglishDateFormatting() throws Exception {
        String output = runConsole("deadline 买香蕉 /by 2/12/2026 1800\nfind 香蕉\nbye\n", "zh", "CN");
        assertTrue(output.contains("买香蕉"));
        assertTrue(output.contains("2 Dec 2026, 6:00PM"));
        assertTrue(output.contains("matching tasks"));
        Gongrilla restarted = new Gongrilla(temporaryDirectory.resolve("data/gongrilla.txt"));
        assertTrue(restarted.getResponse("list").contains("买香蕉"));
    }

    /** Launches the console with a private working directory and a bounded execution time. */
    private String runConsole(String input, String language, String country) throws Exception {
        String executable = System.getProperty("os.name").startsWith("Windows") ? "java.exe" : "java";
        List<String> arguments = new ArrayList<>();
        arguments.add(Path.of(System.getProperty("java.home"), "bin", executable).toString());
        arguments.add("-ea");
        // Include child-process execution in Gradle's JaCoCo report when its agent is available.
        ManagementFactory.getRuntimeMXBean().getInputArguments().stream()
                .filter(argument -> argument.startsWith("-javaagent:") && argument.contains("jacoco"))
                .map(this::configureCoverage)
                .forEach(arguments::add);
        arguments.addAll(List.of("-Dfile.encoding=UTF-8", "-Dstdout.encoding=UTF-8",
                "-Dstderr.encoding=UTF-8", "-Duser.language=" + language, "-Duser.country=" + country,
                "-cp", Path.of(Gongrilla.class.getProtectionDomain().getCodeSource().getLocation().toURI()).toString(),
                "gongrilla.Gongrilla"));
        Path outputFile = temporaryDirectory.resolve("console-output.txt");
        Process process = new ProcessBuilder(arguments).directory(temporaryDirectory.toFile())
                .redirectErrorStream(true).redirectOutput(outputFile.toFile()).start();
        try {
            try (var inputStream = process.getOutputStream()) {
                inputStream.write(input.getBytes(StandardCharsets.UTF_8));
            }
            assertTrue(process.waitFor(20, TimeUnit.SECONDS), "Console did not exit within 20 seconds");
            String output = Files.readString(outputFile);
            assertEquals(0, process.exitValue(), output);
            return output;
        } finally {
            if (process.isAlive()) {
                process.destroyForcibly();
                process.waitFor(5, TimeUnit.SECONDS);
            }
        }
    }

    /** Gives each child its own coverage file so the parent JVM cannot overwrite its results. */
    private String configureCoverage(String agentArgument) {
        String directory = System.getProperty("gongrilla.console.coverageDirectory");
        if (directory == null) {
            return agentArgument;
        }
        Path destination = Path.of(directory, temporaryDirectory.getFileName() + ".exec");
        return agentArgument.replaceAll("destfile=[^,]+",
                java.util.regex.Matcher.quoteReplacement("destfile=" + destination));
    }
}
