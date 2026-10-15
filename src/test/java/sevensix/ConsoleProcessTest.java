package sevensix;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.management.ManagementFactory;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/** Tests real console startup in a temporary working directory, never touching the user's default data file. */
class ConsoleProcessTest {
    /** Working directory for each child JVM and its default data folder. */
    @TempDir
    private Path temporaryDirectory;

    /** An absent configuration must create data/sevensix.txt relative to the process working directory. */
    @Test
    void mainWithoutConfiguredPathCreatesDefaultFile() throws Exception {
        String output = runConsole(null, "todo 读书\nbye\n");

        assertTrue(output.contains("Your pipeline now holds 1 deliverable."));
        assertEquals("T | 0 | 读书" + System.lineSeparator(),
                Files.readString(temporaryDirectory.resolve("data/sevensix.txt")));
        String reloadedOutput = runConsole(null, "list\nbye\n");
        assertTrue(reloadedOutput.contains("1.[T][ ] 读书"), reloadedOutput);
    }

    /** Blank configuration uses the same default path as absent configuration.
     *
     * @param configuredPath the blank system-property value.
     */
    @ParameterizedTest
    @ValueSource(strings = {"", "   "})
    void mainWithBlankConfiguredPathUsesDefaultFile(String configuredPath) throws Exception {
        runConsole(configuredPath, "todo first\nbye\n");

        assertEquals("T | 0 | first" + System.lineSeparator(),
                Files.readString(temporaryDirectory.resolve("data/sevensix.txt")));
    }

    /** Unicode working directories and filenames containing spaces survive a fresh JVM restart. */
    @Test
    void mainReloadsTasksFromUnicodeWorkingDirectoryWithSpaces() throws Exception {
        Path workingDirectory = Files.createDirectory(temporaryDirectory.resolve("资料 workspace"));
        // Keep launcher arguments ASCII: Windows can replace unmappable characters before Java starts.
        String configuredPath = "task list.txt";
        Path dataFile = workingDirectory.resolve(configuredPath);
        String creationOutput = runConsole(workingDirectory, configuredPath, "todo 借书\nbye\n");

        assertTrue(creationOutput.contains("[T][ ] 借书"), creationOutput);
        assertTrue(Files.exists(dataFile), creationOutput);
        assertEquals("T | 0 | 借书" + System.lineSeparator(),
                Files.readString(dataFile));
        String output = runConsole(workingDirectory, configuredPath, "list\nbye\n");

        assertTrue(output.contains("1.[T][ ] 借书"), output);
    }

    /** A bad default path must produce a visible warning and remain intact. */
    @Test
    void mainReportsBlockedDefaultDataFolder() throws Exception {
        Files.writeString(temporaryDirectory.resolve("data"), "keep this file");
        String output = runConsole(null, "todo rejected\nlist\nbye\n");

        assertTrue(output.contains("Flagging a blocker: " + Ui.LOAD_FAILURE));
        assertTrue(output.contains("Flagging a blocker: " + Ui.SAVE_FAILURE));
        assertTrue(output.contains("Your pipeline is empty."));
        assertEquals("keep this file", Files.readString(temporaryDirectory.resolve("data")));
    }

    /** Runs the console with a bounded timeout and an isolated working directory.
     *
     * @param configuredPath the optional data-file property.
     * @param input the complete UTF-8 console input.
     * @return the combined output of a successful child JVM.
     * @throws Exception if launch, input, or process completion fails.
     */
    private String runConsole(String configuredPath, String input) throws Exception {
        return runConsole(temporaryDirectory, configuredPath, input);
    }

    /** Runs the production entry point in the supplied directory without putting that path in JVM arguments.
     *
     * @param workingDirectory the existing directory used for relative task paths.
     * @param configuredPath the optional data-file property.
     * @param input the complete UTF-8 console input.
     * @return the combined output of a successful child JVM.
     * @throws Exception if launch, input, or process completion fails.
     */
    private String runConsole(Path workingDirectory, String configuredPath, String input) throws Exception {
        Process process = new ProcessBuilder(launchCommand(configuredPath))
                .directory(workingDirectory.toFile()).redirectErrorStream(true).start();
        try {
            try (var commands = process.getOutputStream()) {
                commands.write(input.getBytes(StandardCharsets.UTF_8));
            }
            assertTrue(process.waitFor(15, TimeUnit.SECONDS), "Console process did not terminate");
            String output = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            assertEquals(0, process.exitValue(), output);
            return output;
        } finally {
            if (process.isAlive()) {
                process.destroyForcibly();
            }
        }
    }

    /** Uses the same JDK and, when available, coverage agent as the Gradle test worker.
     *
     * @param configuredPath the optional data-file property.
     * @return arguments for launching the production console entry point.
     * @throws Exception if the production class directory cannot be resolved.
     */
    private List<String> launchCommand(String configuredPath) throws Exception {
        String executable = System.getProperty("os.name").startsWith("Windows") ? "java.exe" : "java";
        List<String> command = new ArrayList<>(List.of(
                Path.of(System.getProperty("java.home"), "bin", executable).toString(), "-ea"));
        command.addAll(ManagementFactory.getRuntimeMXBean().getInputArguments().stream()
                .filter(argument -> argument.startsWith("-javaagent:") && argument.contains("jacoco"))
                .map(ConsoleProcessTest::withAbsoluteCoverageDestination).toList());
        if (configuredPath != null) {
            command.add("-Dsevensix.data.file=" + configuredPath);
        }
        // Standard output and error have separate encodings from the default file charset on Windows.
        command.addAll(List.of("-Dfile.encoding=UTF-8", "-Dstdout.encoding=UTF-8", "-Dstderr.encoding=UTF-8",
                "-Duser.language=" + System.getProperty("user.language"),
                "-Duser.country=" + System.getProperty("user.country", ""), "-cp",
                Path.of(SevenSix.class.getProtectionDomain().getCodeSource().getLocation().toURI()).toString(),
                "sevensix.SevenSix"));
        return command;
    }

    /** Keeps child coverage in the parent's report despite the child's different working directory.
     *
     * @param argument the coverage agent argument inherited from Gradle.
     * @return the same argument with its execution-data destination made absolute.
     */
    private static String withAbsoluteCoverageDestination(String argument) {
        Matcher destination = Pattern.compile("destfile=([^,]+)").matcher(argument);
        if (!destination.find()) {
            return argument;
        }
        String absoluteDestination = "destfile=" + Path.of(destination.group(1)).toAbsolutePath();
        return destination.replaceFirst(Matcher.quoteReplacement(absoluteDestination));
    }
}
