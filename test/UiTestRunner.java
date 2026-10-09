package test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Runs the console UI test cases documented in {@code test/ui-test-plan.md}.
 */
public class UiTestRunner {
    private static final String SEPARATOR = "____________________________________________________________";
    private static final Pattern CASE_PATTERN = Pattern.compile(
            "(?ms)^### (?<id>UI-\\d+) \\S+ (?<name>[^\\r\\n]+)\\r?\\n(?<body>.*?)(?=^### UI-|\\z)");
    private static final Pattern COMMAND_PATTERN = Pattern.compile(
            "(?m)^\\s+\\d+\\. Command/input: `(?<command>[^`]+)`\\s*$");
    private static final Pattern OUTPUT_PATTERN = Pattern.compile(
            "(?ms)Expected output:\\s*\\r?\\n\\s*```text\\r?\\n(?<content>.*?)^\\s*```\\s*$");
    private static final Pattern FILE_PATTERN = Pattern.compile(
            "(?ms)Expected `(?<path>[^`]+)` content immediately after the command:"
                    + "\\s*\\r?\\n\\s*```text\\r?\\n(?<content>.*?)^\\s*```\\s*$");

    private final Path repositoryRoot;
    private final boolean shouldShowOutput;

    /**
     * Creates a runner rooted at the current repository.
     *
     * @param repositoryRoot root directory containing {@code test/ui-test-plan.md}.
     * @param shouldShowOutput whether successful application output should be printed.
     */
    public UiTestRunner(Path repositoryRoot, boolean shouldShowOutput) {
        this.repositoryRoot = repositoryRoot;
        this.shouldShowOutput = shouldShowOutput;
    }

    /**
     * Runs every documented UI test case.
     *
     * @param args accepts the optional {@code --show-output} flag.
     * @throws Exception if the plan is invalid, a command fails, or an assertion fails.
     */
    public static void main(String[] args) throws Exception {
        boolean shouldShowOutput = false;
        for (String argument : args) {
            if (argument.equals("--show-output")) {
                shouldShowOutput = true;
            } else {
                throw new IllegalArgumentException("Unknown argument: " + argument);
            }
        }

        Path repositoryRoot = Paths.get("").toAbsolutePath().normalize();
        new UiTestRunner(repositoryRoot, shouldShowOutput).run();
    }

    /** Compiles the application and executes the test cases in plan order. */
    private void run() throws Exception {
        String plan = normalizeLineEndings(Files.readString(resolvePath("test/ui-test-plan.md")));
        int recordsHeading = plan.indexOf("## Test-session records");
        if (recordsHeading >= 0) {
            plan = plan.substring(0, recordsHeading);
        }

        String requiredJavaVersion = getRequiredJavaVersion(plan);
        String compileCommand = getInlineCommand(plan, "Setup/compile command");
        String launchCommand = getInlineCommand(plan, "Program launch command");
        List<UiTestCase> testCases = getTestCases(plan);
        if (testCases.isEmpty()) {
            throw new IllegalArgumentException("No UI test cases were parsed from the plan.");
        }
        System.out.printf("Parsed %d UI test cases from the plan.%n", testCases.size());

        confirmJavaVersion(requiredJavaVersion);
        long compilationStart = System.nanoTime();
        runDocumentedCommand(compileCommand);
        System.out.println("$ " + compileCommand);
        System.out.printf("Compilation passed in %.2f s.%n", elapsedSeconds(compilationStart));

        long suiteStart = System.nanoTime();
        for (UiTestCase testCase : testCases) {
            runTestCase(testCase, launchCommand);
        }
        System.out.printf("All %d UI test cases passed in %.2f s, excluding compilation.%n",
                testCases.size(), elapsedSeconds(suiteStart));
    }

    /** Checks that the selected Java runtime matches the version required by the plan. */
    private void confirmJavaVersion(String requiredVersion) throws Exception {
        Process process = new ProcessBuilder("java", "-version")
                .redirectErrorStream(true)
                .start();
        String versionOutput = readAllOutput(process);
        int exitCode = process.waitFor();
        if (exitCode != 0 || !versionOutput.contains("version \"" + requiredVersion + ".")) {
            throw new IllegalStateException(
                    "Java " + requiredVersion + " is required.\nSelected Java:\n" + versionOutput.strip());
        }
        System.out.printf("$ java -version -- Java %s confirmed%n", requiredVersion);
    }

    /** Runs one isolated test case and stops at the first output or file mismatch. */
    private void runTestCase(UiTestCase testCase, String launchCommand) throws Exception {
        System.out.printf("Starting %s -- %s%n", testCase.id(), testCase.name());
        prepareTestData(testCase);

        long caseStart = System.nanoTime();
        int currentSession = 0;
        RunningProgram program = null;
        try {
            for (TestStep step : testCase.steps()) {
                if (step.session() != currentSession) {
                    stopProgram(program);
                    program = startProgram(launchCommand);
                    showOutputIfRequested(readOutputBlock(program));
                    currentSession = step.session();
                }

                runCommandStep(testCase, step, program);
                assertFileIfRequired(testCase, step);
            }

            if (testCase.shouldRequireDataDirectory() && !Files.isDirectory(resolvePath("data"))) {
                throw new AssertionError(testCase.id() + " expected the data directory to exist after startup.");
            }
        } finally {
            stopProgram(program);
        }
        System.out.printf("%s passed in %.2f s.%n", testCase.id(), elapsedSeconds(caseStart));
    }

    /** Clears saved test data according to the case's isolation requirements. */
    private void prepareTestData(UiTestCase testCase) throws IOException {
        if (testCase.shouldRemoveDataDirectory()) {
            deleteRecursively(resolvePath("data"));
        } else {
            Files.deleteIfExists(resolvePath("data/sumo.txt"));
        }
    }

    /** Sends one command and compares its complete response before the next step. */
    private void runCommandStep(UiTestCase testCase, TestStep step, RunningProgram program) throws IOException {
        if (step.command().isEmpty()) {
            throw new IllegalArgumentException(testCase.id() + " contains an empty command.");
        }
        program.input().write(step.command());
        program.input().newLine();
        program.input().flush();
        String actualOutput = readOutputBlock(program);
        if (!actualOutput.equals(step.expectedOutput())) {
            throw new AssertionError(testCase.id() + " failed at > " + step.command()
                    + "\nActual output:\n" + actualOutput
                    + "\nExpected output:\n" + step.expectedOutput());
        }
        System.out.printf("%s > %s -- PASS%n", testCase.id(), step.command());
        showOutputIfRequested(actualOutput);
    }

    /** Prints captured application output when a full transcript is requested. */
    private void showOutputIfRequested(String output) {
        if (shouldShowOutput) {
            System.out.println(output);
        }
    }

    /** Compares persisted file contents with the optional assertion for a step. */
    private void assertFileIfRequired(UiTestCase testCase, TestStep step) throws IOException {
        if (step.filePath() == null) {
            return;
        }

        Path assertedPath = resolvePath(step.filePath());
        if (!Files.isRegularFile(assertedPath)) {
            throw new AssertionError(testCase.id() + " expected '" + step.filePath()
                    + "' after > " + step.command() + ", but the file does not exist.");
        }
        String actualFile = trimTrailingNewlines(normalizeLineEndings(Files.readString(assertedPath)));
        if (!actualFile.equals(step.expectedFile())) {
            throw new AssertionError(testCase.id() + " file assertion failed after > " + step.command()
                    + "\nActual file content:\n" + actualFile
                    + "\nExpected file content:\n" + step.expectedFile());
        }
        System.out.printf("%s $ read %s -- PASS%n", testCase.id(), step.filePath());
    }

    /** Starts an application session with UTF-8 input and output streams. */
    private RunningProgram startProgram(String launchCommand) throws IOException {
        Process process = new ProcessBuilder(splitCommand(launchCommand))
                .directory(repositoryRoot.toFile())
                .start();
        return new RunningProgram(
                process,
                process.inputReader(StandardCharsets.UTF_8),
                process.errorReader(StandardCharsets.UTF_8),
                process.outputWriter(StandardCharsets.UTF_8));
    }

    /** Reads one complete response, including its opening and closing separators. */
    private String readOutputBlock(RunningProgram program) throws IOException {
        List<String> lines = new ArrayList<>();
        int separatorCount = 0;
        while (separatorCount < 2) {
            String line = program.output().readLine();
            if (line == null) {
                String errorOutput = program.error().lines().reduce("", (left, right) -> left + right + "\n");
                throw new IllegalStateException(
                        "The application ended before a complete output block was received.\n" + errorOutput);
            }
            lines.add(line);
            if (line.equals(SEPARATOR)) {
                separatorCount++;
            }
        }
        return String.join("\n", lines);
    }

    /** Closes input and terminates a session, waiting for its process to exit. */
    private void stopProgram(RunningProgram program) throws InterruptedException, IOException {
        if (program == null) {
            return;
        }
        program.input().close();
        if (program.process().isAlive()) {
            program.process().destroy();
            if (!program.process().waitFor(1, TimeUnit.SECONDS)) {
                program.process().destroyForcibly();
            }
        }
        program.process().waitFor();
    }

    /** Runs a plan command and rejects a nonzero exit status. */
    private void runDocumentedCommand(String command) throws Exception {
        Process process = new ProcessBuilder(splitCommand(command))
                .directory(repositoryRoot.toFile())
                .inheritIO()
                .start();
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new IllegalStateException(
                    "Documented command failed with exit code " + exitCode + ": " + command);
        }
    }

    /** Parses test cases, session boundaries, expected responses, and file assertions. */
    private List<UiTestCase> getTestCases(String markdown) {
        List<UiTestCase> testCases = new ArrayList<>();
        Matcher caseMatcher = CASE_PATTERN.matcher(markdown);
        while (caseMatcher.find()) {
            String body = caseMatcher.group("body");
            testCases.add(new UiTestCase(
                    caseMatcher.group("id"),
                    caseMatcher.group("name"),
                    getTestSteps(caseMatcher.group("id"), body),
                    body.contains("delete the `data` directory recursively if it exists"),
                    body.contains("Confirm that `data` exists after startup")));
        }
        return testCases;
    }

    /** Parses a case's commands and assigns each step to its documented session. */
    private List<TestStep> getTestSteps(String caseId, String body) {
        int secondSessionPosition = body.indexOf("- Second session inputs");
        List<MatcherResult> commands = findCommands(body);
        if (commands.isEmpty()) {
            throw new IllegalArgumentException(caseId + " does not contain any command inputs.");
        }
        List<TestStep> steps = new ArrayList<>();
        for (int index = 0; index < commands.size(); index++) {
            MatcherResult command = commands.get(index);
            int sectionEnd = index + 1 < commands.size() ? commands.get(index + 1).start() : body.length();
            String section = body.substring(command.end(), sectionEnd);
            int session = secondSessionPosition >= 0 && command.start() > secondSessionPosition ? 2 : 1;
            steps.add(parseTestStep(caseId, command.command(), section, session));
        }
        return List.copyOf(steps);
    }

    /** Parses the required response and optional file assertion for one command. */
    private TestStep parseTestStep(String caseId, String command, String section, int session) {
        Matcher outputMatcher = OUTPUT_PATTERN.matcher(section);
        if (!outputMatcher.find()) {
            throw new IllegalArgumentException(caseId + " has no expected output for '" + command + "'.");
        }
        Matcher fileMatcher = FILE_PATTERN.matcher(section);
        boolean hasFileAssertion = fileMatcher.find();
        return new TestStep(
                command,
                removeMarkdownIndent(outputMatcher.group("content")),
                session,
                hasFileAssertion ? fileMatcher.group("path") : null,
                hasFileAssertion ? removeMarkdownIndent(fileMatcher.group("content")) : null);
    }

    /** Finds command inputs and their positions within a test case. */
    private List<MatcherResult> findCommands(String body) {
        List<MatcherResult> commands = new ArrayList<>();
        Matcher matcher = COMMAND_PATTERN.matcher(body);
        while (matcher.find()) {
            commands.add(new MatcherResult(matcher.start(), matcher.end(), matcher.group("command")));
        }
        return commands;
    }

    /** Reads the required Java version from the plan. */
    private String getRequiredJavaVersion(String markdown) {
        Matcher matcher = Pattern.compile("(?m)^- Java version: (?<version>\\d+)\\s*$").matcher(markdown);
        if (!matcher.find()) {
            throw new IllegalArgumentException("The plan does not define 'Java version'.");
        }
        return matcher.group("version");
    }

    /** Reads the command associated with a required plan label. */
    private String getInlineCommand(String markdown, String label) {
        Pattern pattern = Pattern.compile("(?m)^- " + Pattern.quote(label) + ": `(?<command>[^`]+)`$");
        Matcher matcher = pattern.matcher(markdown);
        if (!matcher.find()) {
            throw new IllegalArgumentException("The plan does not define '" + label + "'.");
        }
        return matcher.group("command");
    }

    /** Splits an unquoted command into arguments and rejects unsupported quoting. */
    private List<String> splitCommand(String command) {
        if (command.contains("\"") || command.contains("'")) {
            throw new IllegalArgumentException(
                    "Quoted arguments are not supported in documented commands: " + command);
        }
        return List.of(command.trim().split("\\s+"));
    }

    /** Resolves a plan path relative to the repository using either path separator. */
    private Path resolvePath(String relativePath) {
        Path resolvedPath = repositoryRoot;
        for (String segment : relativePath.split("[/\\\\]")) {
            if (!segment.isEmpty()) {
                resolvedPath = resolvedPath.resolve(segment);
            }
        }
        return resolvedPath.normalize();
    }

    /** Deletes a test directory and its contents, visiting children before parents. */
    private void deleteRecursively(Path path) throws IOException {
        if (!Files.exists(path)) {
            return;
        }
        try (var paths = Files.walk(path)) {
            for (Path currentPath : paths.sorted(Comparator.reverseOrder()).toList()) {
                Files.delete(currentPath);
            }
        }
    }

    /** Reads and closes the standard output stream of a process. */
    private String readAllOutput(Process process) throws IOException {
        try (BufferedReader reader = process.inputReader(StandardCharsets.UTF_8)) {
            return reader.lines().reduce("", (left, right) -> left + right + "\n");
        }
    }

    /** Removes shared Markdown indentation while preserving relative output spacing. */
    private String removeMarkdownIndent(String text) {
        String[] lines = normalizeLineEndings(text).split("\n", -1);
        int minimumIndent = Integer.MAX_VALUE;
        for (String line : lines) {
            if (!line.isEmpty()) {
                minimumIndent = Math.min(minimumIndent, line.length() - line.stripLeading().length());
            }
        }
        if (minimumIndent == Integer.MAX_VALUE) {
            return "";
        }

        List<String> adjustedLines = new ArrayList<>();
        for (String line : lines) {
            adjustedLines.add(line.length() >= minimumIndent ? line.substring(minimumIndent) : line);
        }
        return trimTrailingNewlines(String.join("\n", adjustedLines));
    }

    /** Converts Windows line endings to the line endings used for comparison. */
    private String normalizeLineEndings(String text) {
        return text.replace("\r\n", "\n");
    }

    /** Removes trailing newline characters from a comparison value. */
    private String trimTrailingNewlines(String text) {
        return text.replaceFirst("\\n+$", "");
    }

    /** Returns the elapsed seconds since a monotonic start timestamp. */
    private double elapsedSeconds(long startTime) {
        return (System.nanoTime() - startTime) / 1_000_000_000.0;
    }

    /** Holds a test case and its required data-directory setup. */
    private record UiTestCase(String id, String name, List<TestStep> steps,
            boolean shouldRemoveDataDirectory, boolean shouldRequireDataDirectory) {
    }

    /** Holds one command, expected response, session number, and optional file assertion. */
    private record TestStep(String command, String expectedOutput, int session,
            String filePath, String expectedFile) {
    }

    /** Retains a command and its source positions in the Markdown plan. */
    private record MatcherResult(int start, int end, String command) {
    }

    /** Groups an application process with its input and output streams. */
    private record RunningProgram(Process process, BufferedReader output, BufferedReader error,
            BufferedWriter input) {
    }
}
