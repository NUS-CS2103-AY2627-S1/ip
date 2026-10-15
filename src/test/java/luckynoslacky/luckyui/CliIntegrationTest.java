package luckynoslacky.luckyui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import luckynoslacky.testutil.TestOutputNormalizer;

/** Runs the documented command-line test plan against the real application. */
class CliIntegrationTest {
    private static final String FIXED_NOW = "2026-08-25T10:00:00Z";

    /** Executes one documented CLI session in an isolated working directory. */
    @ParameterizedTest(name = "{0}")
    @MethodSource("documentedCliCases")
    void cliTest_documentedCase_matchesExpectedOutput(
            String caseName,
            CliTestCase testCase)
            throws IOException, InterruptedException {
        Path temporaryDirectory = Files.createTempDirectory("lucky-cli-test-");
        try {
            Files.createDirectory(temporaryDirectory.resolve("data"));
            Process process = createProcess(temporaryDirectory);
            process.getOutputStream().write(
                    testCase.input().getBytes(StandardCharsets.UTF_8));
            process.getOutputStream().close();

            String actualOutput = new String(
                    process.getInputStream().readAllBytes(),
                    StandardCharsets.UTF_8);
            String errorOutput = new String(
                    process.getErrorStream().readAllBytes(),
                    StandardCharsets.UTF_8);
            int exitCode = process.waitFor();

            System.out.println("CLI test case: " + caseName);
            System.out.println("Input:\n" + testCase.input());
            System.out.println("Output:\n" + actualOutput);

            assertEquals(0, exitCode, testCase.markdownReference());
            assertEquals("", errorOutput, testCase.markdownReference());
            String failureMessage = testCase.markdownReference()
                    + "\nstandard error:\n" + errorOutput;
            assertEquals(
                    TestOutputNormalizer.normalize(testCase.expectedOutput()),
                    TestOutputNormalizer.normalize(actualOutput),
                    failureMessage);
        } finally {
            deleteTemporaryDirectory(temporaryDirectory);
        }
    }

    /** Supplies CLI fixtures in Markdown test-plan order. */
    static Stream<Arguments> documentedCliCases() {
        return CliTestCaseFixtures.load().stream()
                .map(testCase -> Arguments.of(testCase.name(), testCase));
    }

    /** Creates the application process with deterministic settings. */
    private static Process createProcess(Path workingDirectory)
            throws IOException {
        String javaExecutable = Path.of(
                System.getProperty("java.home"), "bin", "java").toString();
        return new ProcessBuilder(
                javaExecutable,
                "-Dluckynoslacky.fixedNow=" + FIXED_NOW,
                "-Duser.language=en",
                "-Duser.country=US",
                "-cp",
                System.getProperty("java.class.path"),
                "luckynoslacky.LuckyNoSlacky")
                .directory(workingDirectory.toFile())
                .start();
    }

    /** Deletes a temporary CLI test directory. */
    private static void deleteTemporaryDirectory(Path directory)
            throws IOException {
        try (Stream<Path> paths = Files.walk(directory)) {
            paths.sorted((first, second) -> second.compareTo(first))
                    .forEach(CliIntegrationTest::deletePath);
        }
    }

    /** Deletes one temporary path. */
    private static void deletePath(Path path) {
        try {
            Files.deleteIfExists(path);
        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Unable to clean up CLI test path: " + path,
                    exception);
        }
    }
}
