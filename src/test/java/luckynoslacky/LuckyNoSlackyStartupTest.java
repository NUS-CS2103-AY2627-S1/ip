package luckynoslacky;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import luckynoslacky.luckyresponse.LuckyNoQuips;
import luckynoslacky.testutil.TestOutputNormalizer;

/** Tests startup, termination, and isolated facade error paths. */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LuckyNoSlackyStartupTest {
    private static final String FIXED_NOW_PROPERTY =
            "luckynoslacky.fixedNow";
    private static final String DIVIDER =
            "  ____________________________________________________________\n";

    @TempDir
    Path temporaryDirectory;

    /** Verifies invalid fixed-clock values fail before greeting output. */
    @ParameterizedTest
    @ValueSource(strings = {"not-an-instant", "2026-99-99T99:99:99Z"})
    void main_invalidFixedNowValue_failsBeforeGreeting(String fixedNow)
            throws IOException, InterruptedException {
        ProcessResult result = runApplication(
                "", "-D" + FIXED_NOW_PROPERTY + "=" + fixedNow);

        assertEquals(0, result.exitCode());
        assertEquals(
                DIVIDER + LuckyNoQuips.banner() + "\n"
                        + expectedReply(LuckyNoQuips.greeting())
                        + expectedReply(
                        LuckyNoQuips.configurationErrorMessage()),
                result.standardOutput());
        assertEquals("", result.standardError());
    }

    /** Verifies a blank fixed-clock value falls back to the system clock. */
    @Test
    void main_blankFixedNowValue_startsNormally() throws Exception {
        ProcessResult result = runApplication(
                "", "-D" + FIXED_NOW_PROPERTY + "=");

        assertSuccessfulNormalExit(result);
    }

    /** Verifies an omitted fixed-clock value falls back to the system clock. */
    @Test
    void main_missingFixedNowValue_startsNormally() throws Exception {
        ProcessResult result = runApplication("");

        assertSuccessfulNormalExit(result);
    }

    /** Verifies an invalid CSV header produces the loading-error response. */
    @Test
    void main_invalidCsvHeader_showsLoadingError() throws Exception {
        Path dataFile = createDataDirectory()
                .resolve("luckyNoSlacky.csv");
        Files.writeString(dataFile, "wrong,header\n", StandardCharsets.UTF_8);

        ProcessResult result = runApplication("bye\n");

        assertEquals(0, result.exitCode());
        assertEquals(expectedStartupOutput(true), result.standardOutput());
        assertEquals("", result.standardError());
        assertTrue(result.standardOutput().contains(LuckyNoQuips.goodbye()));
    }

    /** Verifies a non-file data path produces the loading-error response. */
    @Test
    void main_dataDirectory_showsLoadingError() throws Exception {
        Files.createDirectories(createDataDirectory()
                .resolve("luckyNoSlacky.csv"));

        ProcessResult result = runApplication("bye\n");

        assertEquals(0, result.exitCode());
        assertEquals(expectedStartupOutput(true), result.standardOutput());
        assertEquals("", result.standardError());
        assertTrue(result.standardOutput().contains(LuckyNoQuips.goodbye()));
    }

    /** Verifies the facade converts a save failure into its GUI response. */
    @Test
    void getResponse_saveFailure_returnsSaveErrorResponse() throws Exception {
        Files.createDirectories(createDataDirectory()
                .resolve("luckyNoSlacky.csv"));

        ProcessResult result = runResponseProbe("todo new task");

        assertEquals(0, result.exitCode());
        assertEquals(LuckyNoQuips.saveErrorMessage()
                        + "\nshouldExit=false\n",
                TestOutputNormalizer.normalizeLineEndings(
                        result.standardOutput()));
        assertEquals("", result.standardError());
    }

    /** Verifies normal startup output and clean EOF termination. */
    private void assertSuccessfulNormalExit(ProcessResult result) {
        assertEquals(0, result.exitCode());
        assertEquals(expectedStartupOutput(false), result.standardOutput());
        assertEquals("", result.standardError());
    }

    /** Creates the isolated application data directory. */
    private Path createDataDirectory() throws IOException {
        return Files.createDirectories(temporaryDirectory.resolve("data"));
    }

    /** Builds the exact greeting, optional load error, and goodbye output. */
    private String expectedStartupOutput(boolean hasLoadError) {
        String output = DIVIDER + LuckyNoQuips.banner() + "\n"
                + expectedReply(LuckyNoQuips.greeting());
        if (hasLoadError) {
            output += expectedReply(LuckyNoQuips.loadErrorMessage());
        }
        return output + expectedReply(LuckyNoQuips.goodbye());
    }

    /** Builds one CLI reply using the production divider and indentation. */
    private String expectedReply(String message) {
        String indentedMessage = message.replace("\n", "\n  ");
        return DIVIDER + "  " + indentedMessage + "\n" + DIVIDER;
    }

    /** Runs the application main class in the supplied temporary directory. */
    private ProcessResult runApplication(
            String input,
            String... jvmArguments)
            throws IOException, InterruptedException {
        return runProcess(
                LuckyNoSlacky.class.getName(),
                input,
                List.of(jvmArguments),
                List.of());
    }

    /** Runs the response probe in the supplied temporary directory. */
    private ProcessResult runResponseProbe(String command)
            throws IOException, InterruptedException {
        return runProcess(
                LuckyNoSlackyResponseProbe.class.getName(),
                "",
                List.of(),
                List.of(command));
    }

    /** Runs one Java subprocess and captures both output streams. */
    private ProcessResult runProcess(
            String mainClass,
            String input,
            List<String> jvmArguments,
            List<String> programArguments)
            throws IOException, InterruptedException {
        String javaExecutable = Path.of(
                System.getProperty("java.home"), "bin", "java").toString();
        List<String> command = new ArrayList<>();
        command.add(javaExecutable);
        command.addAll(jvmArguments);
        command.add("-cp");
        command.add(System.getProperty("java.class.path"));
        command.add(mainClass);
        command.addAll(programArguments);

        Process process = new ProcessBuilder(command)
                .directory(temporaryDirectory.toFile())
                .start();
        process.getOutputStream().write(input.getBytes(StandardCharsets.UTF_8));
        process.getOutputStream().close();

        String standardOutput = new String(
                process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        String standardError = new String(
                process.getErrorStream().readAllBytes(), StandardCharsets.UTF_8);
        int exitCode = process.waitFor();
        return new ProcessResult(exitCode, standardOutput, standardError);
    }

    /** Captures the result of an isolated Java subprocess. */
    private record ProcessResult(
            int exitCode,
            String standardOutput,
            String standardError) {
    }
}
