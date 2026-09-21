package friday;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.io.TempDir;

/** Runs every console regression case recorded in the Markdown UI test plan. */
class ConsoleUiRegressionTest {
    private static final Path TEST_PLAN = Path.of("test", "ui-test-plan.md");
    private static final Pattern TEST_CASE_PATTERN = Pattern.compile(
            "(?ms)^## Test Case: (?<name>.+?)\\R- Aim: (?<aim>.+?)\\R- Inputs:\\R```text\\R"
                    + "(?<inputs>.*?)\\R```\\R- Expected output:\\R```text\\R(?<expected>.*?)\\R```"
    );
    private static final String SEPARATOR = "_".repeat(60);

    @TempDir
    Path temporaryDirectory;

    /** Creates one executable JUnit test for each case in the source-of-truth test plan. */
    @TestFactory
    List<DynamicTest> consoleCases_matchExpectedOutput() throws IOException {
        List<UiTestCase> testCases = readTestCases();
        return testCases.stream()
                .map(testCase -> DynamicTest.dynamicTest(testCase.name(),
                        () -> assertEquals(normalize(testCase.expectedOutput()),
                                normalize(runTestCase(testCase)), testCase.aim())))
                .toList();
    }

    /** Reads the named input/output blocks from the Markdown plan. */
    private List<UiTestCase> readTestCases() throws IOException {
        String plan = Files.readString(TEST_PLAN, StandardCharsets.UTF_8);
        Matcher matcher = TEST_CASE_PATTERN.matcher(plan);
        List<UiTestCase> testCases = new ArrayList<>();
        while (matcher.find()) {
            testCases.add(new UiTestCase(matcher.group("name"), matcher.group("aim"),
                    matcher.group("inputs"), matcher.group("expected")));
        }
        if (testCases.isEmpty()) {
            throw new IOException("No console UI test cases found in " + TEST_PLAN + ".");
        }
        return testCases;
    }

    /** Runs test sessions separated by restart directives in one isolated data directory. */
    private String runTestCase(UiTestCase testCase) throws IOException, InterruptedException {
        Path workingDirectory = Files.createDirectory(temporaryDirectory.resolve("case-" + System.nanoTime()));
        StringBuilder output = new StringBuilder();
        List<String> sessionLines = new ArrayList<>();
        for (String line : testCase.inputs().split("\\R")) {
            if (line.equals("@restart")) {
                output.append(runSession(workingDirectory, sessionLines));
                sessionLines.clear();
            } else {
                sessionLines.add(line);
            }
        }
        output.append(runSession(workingDirectory, sessionLines));
        return output.toString();
    }

    /** Sets up directives, runs Friday once, and returns all console output from that session. */
    private String runSession(Path workingDirectory, List<String> lines) throws IOException, InterruptedException {
        SessionFixture fixture = prepareSession(workingDirectory, lines);
        Process process = startFriday(workingDirectory);
        String output = exchangeCommands(process, fixture);
        verifyExit(process);
        return output;
    }

    /** Applies startup fixtures and separates test directives from actual commands. */
    private SessionFixture prepareSession(Path workingDirectory, List<String> lines) throws IOException {
        Path dataFile = workingDirectory.resolve("data/friday.txt");
        Path contactFile = workingDirectory.resolve("data/contacts.txt");
        List<String> taskRecords = new ArrayList<>();
        List<String> contactRecords = new ArrayList<>();
        List<String> commands = new ArrayList<>();
        boolean shouldBlockTaskSave = false;
        boolean shouldBlockContactSave = false;

        for (String line : lines) {
            if (line.startsWith("@file ")) {
                taskRecords.add(line.substring("@file ".length()));
            } else if (line.startsWith("@contact-file ")) {
                contactRecords.add(line.substring("@contact-file ".length()));
            } else if (line.equals("@directory")) {
                Files.createDirectories(dataFile);
            } else if (line.equals("@contact-directory")) {
                Files.createDirectories(contactFile);
            } else if (line.equals("@block-save")) {
                shouldBlockTaskSave = true;
            } else if (line.equals("@block-contact-save")) {
                shouldBlockContactSave = true;
            } else if (line.startsWith("@")) {
                throw new IllegalArgumentException("Unknown UI test directive: " + line);
            } else {
                commands.add(line);
            }
        }
        writeRecords(dataFile, taskRecords);
        writeRecords(contactFile, contactRecords);

        return new SessionFixture(dataFile, contactFile, List.copyOf(commands),
                shouldBlockTaskSave, shouldBlockContactSave);
    }

    /** Sends commands after any startup synchronization and collects the session output. */
    private String exchangeCommands(Process process, SessionFixture fixture) throws IOException {
        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = process.inputReader(StandardCharsets.UTF_8);
                Writer writer = new OutputStreamWriter(process.getOutputStream(), StandardCharsets.UTF_8)) {
            prepareSaveFailures(reader, output, fixture);
            sendCommands(writer, fixture.commands());
            output.append(reader.lines().collect(java.util.stream.Collectors.joining("\n")));
            if (!output.isEmpty()) {
                output.append("\n");
            }
        }
        return output.toString();
    }

    /** Waits for startup before blocking save paths, leaving loading behavior unchanged. */
    private void prepareSaveFailures(BufferedReader reader, StringBuilder output, SessionFixture fixture)
            throws IOException {
        if (fixture.shouldBlockTaskSave() || fixture.shouldBlockContactSave()) {
            readStartupOutput(reader, output);
            blockSaveDestination(fixture.dataFile(), fixture.shouldBlockTaskSave());
            blockSaveDestination(fixture.contactFile(), fixture.shouldBlockContactSave());
        }
    }

    /** Sends the session input and closes stdin so EOF-only sessions can finish. */
    private void sendCommands(Writer writer, List<String> commands) throws IOException {
        writer.write(String.join("\n", commands));
        writer.write("\n");
        writer.flush();
        writer.close();
    }

    /** Checks process completion and reports abnormal exits. */
    private void verifyExit(Process process) throws IOException, InterruptedException {
        if (!process.waitFor(15, TimeUnit.SECONDS)) {
            process.destroyForcibly();
            throw new IOException("Friday did not finish within 15 seconds.");
        }
        if (process.exitValue() != 0) {
            throw new IOException("Friday exited with status " + process.exitValue() + ".");
        }
    }

    /** Writes fixture records only when the test case supplies at least one record. */
    private void writeRecords(Path file, List<String> records) throws IOException {
        if (!records.isEmpty()) {
            Files.createDirectories(file.getParent());
            Files.writeString(file, String.join("\n", records) + "\n", StandardCharsets.UTF_8);
        }
    }

    /** Starts Friday using the test runtime classpath and the selected Java runtime. */
    private Process startFriday(Path workingDirectory) throws IOException {
        Path javaCommand = Path.of(System.getProperty("java.home"), "bin", executableName());
        // Match the UTF-8 pipe reader even when Windows uses a different native output encoding.
        return new ProcessBuilder(javaCommand.toString(), "-Dstdout.encoding=UTF-8", "-Dstderr.encoding=UTF-8",
                "-cp", System.getProperty("java.class.path"), "friday.Friday")
                .directory(workingDirectory.toFile())
                .redirectErrorStream(true)
                .start();
    }

    /** Returns the platform-specific Java command name. */
    private String executableName() {
        return System.getProperty("os.name").startsWith("Windows") ? "java.exe" : "java";
    }

    /** Reads Friday's welcome response before a test changes its save destination. */
    private void readStartupOutput(BufferedReader reader, StringBuilder output) throws IOException {
        int separatorCount = 0;
        while (separatorCount < 2) {
            String line = reader.readLine();
            if (line == null) {
                throw new IOException("Friday exited before finishing its welcome message.");
            }
            output.append(line).append("\n");
            if (line.equals(SEPARATOR)) {
                separatorCount++;
            }
        }
    }

    /** Replaces a fixture file with a nonempty directory so the following save reliably fails. */
    private void blockSaveDestination(Path destination, boolean shouldBlock) throws IOException {
        if (!shouldBlock) {
            return;
        }
        if (Files.isRegularFile(destination)) {
            Files.delete(destination);
        }
        Files.createDirectories(destination);
        Files.writeString(destination.resolve("blocker"), "Keep this directory nonempty.", StandardCharsets.UTF_8);
    }

    /** Normalizes platform line endings and ignores one trailing newline. */
    private String normalize(String text) {
        return text.replace("\r\n", "\n").replaceFirst("\\n$", "");
    }

    /** Carries prepared fixture paths, commands, and deferred save-failure instructions. */
    private record SessionFixture(Path dataFile, Path contactFile, List<String> commands,
            boolean shouldBlockTaskSave, boolean shouldBlockContactSave) {
    }

    /** Stores one named console test case parsed from the Markdown plan. */
    private record UiTestCase(String name, String aim, String inputs, String expectedOutput) {
    }
}
