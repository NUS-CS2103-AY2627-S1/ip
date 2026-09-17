import java.io.BufferedReader;
import java.io.File;
import java.io.FileDescriptor;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

/**
 * Runs the text-UI test cases recorded in test/ui-test-plan.md against the chatbot.
 *
 * <p>For each test case the program is started fresh, each command is typed in order, and the
 * output that follows each command is compared with the expected output from the plan. The whole
 * console session is echoed as it happens. On the first failing test case the run stops and
 * reports the expected output, the actual output, and the first line where they differ.
 *
 * <p>Run it directly from the project root, no build step needed:
 *
 * <pre>
 *   java test/TestRunner.java
 *   java test/TestRunner.java --only TC-05
 *   java test/TestRunner.java --no-compile
 * </pre>
 *
 * <p>Exit status: 0 if every case passed, 1 on the first failure, 2 on a setup error.
 */
public class TestRunner {

    /** Wait this long for further output after a command before deciding the program is done. */
    private static final long IDLE_TIMEOUT_MS = 600;
    private static final long STARTUP_TIMEOUT_MS = 3000;

    private static final String WILDCARD = "...";

    private static final Pattern DIVIDER = Pattern.compile("^[_-]{10,}$");
    private static final Pattern HEADING = Pattern.compile("^##\\s+(TC-\\S+)\\s*—\\s*(.*)$");
    private static final Pattern AIM = Pattern.compile("^\\*\\*Aim:\\*\\*\\s*(.*)$");

    private static final boolean COLOUR = System.console() != null;
    private static final String RED = COLOUR ? "\033[31m" : "";
    private static final String GREEN = COLOUR ? "\033[32m" : "";
    private static final String YELLOW = COLOUR ? "\033[33m" : "";
    private static final String DIM = COLOUR ? "\033[2m" : "";
    private static final String BOLD = COLOUR ? "\033[1m" : "";
    private static final String OFF = COLOUR ? "\033[0m" : "";

    // ---------------------------------------------------------------- model

    /** One command and the output expected to follow it. */
    private record Step(String command, List<String> expected) {
        /** A step whose command is null holds the expected startup output. */
        boolean isStartup() {
            return command == null;
        }
    }

    private record TestCase(String id, String title, String aim, List<Step> steps) {
    }

    // ---------------------------------------------------------------- entry

    public static void main(String[] args) throws Exception {
        // The plan and the sample output contain non-ASCII characters; make sure they survive
        // whatever the platform's default console encoding happens to be.
        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out), true, StandardCharsets.UTF_8));
        System.setErr(new PrintStream(new FileOutputStream(FileDescriptor.err), true, StandardCharsets.UTF_8));

        String planPath = "test/ui-test-plan.md";
        String srcDir = "src/main/java";
        String mainClass = "quu.Quu";
        String only = null;
        String savePath = "data/Quu.txt";
        boolean noCompile = false;

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
            case "--plan" -> planPath = requireValue(args, ++i, "--plan");
            case "--src" -> srcDir = requireValue(args, ++i, "--src");
            case "--main" -> mainClass = requireValue(args, ++i, "--main");
            case "--only" -> only = requireValue(args, ++i, "--only");
            case "--save" -> savePath = requireValue(args, ++i, "--save");
            case "--no-compile" -> noCompile = true;
            case "--help", "-h" -> {
                printUsage();
                return;
            }
            default -> failSetup("unknown option: " + args[i] + "\nRun with --help for usage.");
            }
        }

        List<TestCase> cases = parsePlan(planPath);
        if (only != null) {
            String wanted = only;
            cases = cases.stream().filter(c -> c.id().equals(wanted)).collect(Collectors.toList());
            if (cases.isEmpty()) {
                failSetup("no test case with id " + wanted);
            }
        }

        String classpath = noCompile ? intellijOutput(srcDir) : compile(srcDir);
        System.out.println(DIM + "running " + cases.size() + " test case(s) from " + planPath + OFF);

        int passed = 0;
        for (TestCase testCase : cases) {
            resetSave(savePath);
            if (!runCase(testCase, classpath, mainClass)) {
                System.out.println();
                System.out.println(RED + passed + "/" + cases.size()
                        + " passed before the failure." + OFF);
                System.exit(1);
            }
            passed++;
        }

        System.out.println();
        System.out.println(GREEN + BOLD + "All " + cases.size() + " test cases passed." + OFF);
    }

    private static String requireValue(String[] args, int index, String flag) {
        if (index >= args.length) {
            failSetup(flag + " needs a value");
        }
        return args[index];
    }

    private static void printUsage() {
        System.out.println("""
                Usage: java test/TestRunner.java [options]

                  --plan PATH    test plan to read     (default: test/ui-test-plan.md)
                  --src PATH     sources to compile    (default: src/main/java)
                  --main CLASS   main class to run     (default: quu.Quu)
                  --only TC-ID   run a single test case, e.g. --only TC-05
                  --save PATH    save file cleared before each case (default: data/Quu.txt,
                                 "none" to leave the save file alone)
                  --no-compile   run the classes IntelliJ last built
                  --help         show this message

                Exit status: 0 all passed, 1 a test failed, 2 setup problem.""");
    }

    /**
     * Deletes the chatbot's save file so each test case starts with an empty task list.
     *
     * <p>Each case runs in a fresh JVM, but the save file outlives the process, so without this
     * one case's tasks would be loaded by the next and every task count would drift.
     */
    private static void resetSave(String savePath) {
        if (savePath == null || savePath.equals("none")) {
            return;
        }
        try {
            Files.deleteIfExists(Paths.get(savePath));
        } catch (IOException e) {
            failSetup("could not clear the save file " + savePath + ": " + e.getMessage());
        }
    }

    /** Reports a setup problem and exits; never returns. */
    private static void failSetup(String message) {
        System.err.println("error: " + message);
        System.exit(2);
    }

    // ---------------------------------------------------------------- plan

    private static List<TestCase> parsePlan(String path) throws IOException {
        Path file = Paths.get(path);
        if (!Files.isRegularFile(file)) {
            failSetup("test plan not found at " + path);
        }

        List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
        List<TestCase> cases = new ArrayList<>();
        String id = null;
        String title = null;
        String aim = null;

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);

            Matcher heading = HEADING.matcher(line);
            if (heading.matches()) {
                id = heading.group(1);
                title = heading.group(2).trim();
                aim = null;
                continue;
            }

            Matcher aimMatch = AIM.matcher(line);
            if (aimMatch.matches() && id != null) {
                aim = aimMatch.group(1).trim();
                continue;
            }

            if (line.equals("```session") && id != null) {
                List<String> body = new ArrayList<>();
                i++;
                while (i < lines.size() && !lines.get(i).strip().equals("```")) {
                    body.add(lines.get(i));
                    i++;
                }
                cases.add(new TestCase(id, title, aim, parseSession(body)));
                id = null;
                title = null;
                aim = null;
            }
        }

        if (cases.isEmpty()) {
            failSetup("no test cases found in " + path);
        }
        return cases;
    }

    /**
     * Splits a session block into steps. Lines starting with '&gt;' are input; everything after one,
     * until the next input line, is that command's expected output. Lines before the first input
     * line form the startup step.
     */
    private static List<Step> parseSession(List<String> body) {
        List<Step> steps = new ArrayList<>();
        String command = null;
        List<String> buffer = new ArrayList<>();
        boolean seenInput = false;

        for (String line : body) {
            if (line.startsWith(">")) {
                if (seenInput || !buffer.isEmpty()) {
                    steps.add(new Step(command, normalise(buffer)));
                }
                command = line.substring(1).trim();
                buffer = new ArrayList<>();
                seenInput = true;
            } else {
                buffer.add(line);
            }
        }
        if (seenInput || !buffer.isEmpty()) {
            steps.add(new Step(command, normalise(buffer)));
        }

        // A leading startup step with nothing expected means "don't check startup".
        if (!steps.isEmpty() && steps.get(0).isStartup() && steps.get(0).expected().isEmpty()) {
            steps.remove(0);
        }
        return steps;
    }

    // ---------------------------------------------------------- comparison

    /**
     * Strips indentation, drops blank lines and divider rules. Whitespace <em>inside</em> a line is
     * preserved, so "[T][ ] x" and "[T] [ ] x" stay different.
     */
    private static List<String> normalise(List<String> lines) {
        List<String> out = new ArrayList<>();
        for (String line : lines) {
            String stripped = line.strip();
            if (stripped.isEmpty() || DIVIDER.matcher(stripped).matches()) {
                continue;
            }
            out.add(stripped);
        }
        return out;
    }

    /**
     * Compares expected against actual, honouring "..." wildcard lines. A line of exactly "..."
     * matches zero or more consecutive output lines; without any wildcard the two must be equal.
     */
    private static boolean matches(List<String> expected, List<String> actual) {
        if (!expected.contains(WILDCARD)) {
            return expected.equals(actual);
        }

        int ei = 0;
        int ai = 0;
        while (ei < expected.size()) {
            if (expected.get(ei).equals(WILDCARD)) {
                ei++;
                List<String> segment = new ArrayList<>();
                while (ei < expected.size() && !expected.get(ei).equals(WILDCARD)) {
                    segment.add(expected.get(ei));
                    ei++;
                }
                if (segment.isEmpty()) {
                    return true;                    // trailing wildcard absorbs the rest
                }
                int found = -1;
                for (int start = ai; start + segment.size() <= actual.size(); start++) {
                    if (actual.subList(start, start + segment.size()).equals(segment)) {
                        found = start;
                        break;
                    }
                }
                if (found < 0) {
                    return false;
                }
                ai = found + segment.size();
            } else {
                if (ai >= actual.size() || !actual.get(ai).equals(expected.get(ei))) {
                    return false;
                }
                ei++;
                ai++;
            }
        }
        return ai == actual.size();
    }

    // ------------------------------------------------------------- compile

    private static List<File> javaSources(String srcDir) throws IOException {
        Path root = Paths.get(srcDir);
        if (!Files.isDirectory(root)) {
            failSetup("source directory not found: " + srcDir);
        }
        List<File> sources;
        try (Stream<Path> walk = Files.walk(root)) {
            sources = walk.filter(p -> p.toString().endsWith(".java"))
                    .map(Path::toFile)
                    .collect(Collectors.toList());
        }
        if (sources.isEmpty()) {
            failSetup("no .java files found under " + srcDir);
        }
        return sources;
    }

    /**
     * Compiles the sources into a fresh temporary directory, so the run always tests what is on
     * disk and never disturbs IntelliJ's out/ directory.
     */
    private static String compile(String srcDir) throws IOException {
        List<File> sources = javaSources(srcDir);
        Path outDir = Files.createTempDirectory("test-ui-");
        outDir.toFile().deleteOnExit();

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            failSetup("no Java compiler available - run this with a JDK, not a JRE");
        }

        List<String> options = new ArrayList<>(List.of("-d", outDir.toString(), "-nowarn"));
        sources.forEach(f -> options.add(f.getPath()));

        ByteArrayOutputStream errors = new ByteArrayOutputStream();
        int status = compiler.run(null, null, errors, options.toArray(new String[0]));
        if (status != 0) {
            System.out.println(RED + "COMPILE FAILED" + OFF);
            System.out.println(errors.toString(StandardCharsets.UTF_8).strip());
            System.exit(2);
        }

        System.out.println(DIM + "compiled " + sources.size() + " source file(s) -> " + outDir + OFF);
        return outDir.toString();
    }

    /** The module name, taken from the .iml file. Falls back to the project directory name. */
    private static String moduleName() {
        File[] imls = new File(".").listFiles((dir, name) -> name.endsWith(".iml"));
        if (imls != null && imls.length > 0) {
            String name = imls[0].getName();
            return name.substring(0, name.length() - ".iml".length());
        }
        return Paths.get("").toAbsolutePath().getFileName().toString();
    }

    /**
     * Runs against the classes IntelliJ last built, warning if they look stale. IntelliJ rebuilds
     * on Build or Run, not on save, so these can lag behind the editor.
     */
    private static String intellijOutput(String srcDir) throws IOException {
        Path outDir = Paths.get("out", "production", moduleName());
        if (!Files.isDirectory(outDir)) {
            failSetup("no IntelliJ output at " + outDir + "\n"
                    + "  Build the project once (Build > Build Project, or Cmd+F9), "
                    + "or drop --no-compile.");
        }

        List<Path> classes;
        try (Stream<Path> walk = Files.walk(outDir)) {
            classes = walk.filter(p -> p.toString().endsWith(".class")).collect(Collectors.toList());
        }
        if (classes.isEmpty()) {
            failSetup(outDir + " contains no .class files - build the project in IntelliJ first");
        }

        List<File> sources = javaSources(srcDir);
        long newestSource = sources.stream().mapToLong(File::lastModified).max().orElse(0);
        long oldestClass = classes.stream().mapToLong(p -> p.toFile().lastModified()).min().orElse(0);
        if (newestSource > oldestClass || classes.size() < sources.size()) {
            System.out.println(YELLOW + "warning: " + outDir + " looks stale ("
                    + classes.size() + " class file(s) for " + sources.size()
                    + " source file(s))." + OFF);
            System.out.println(YELLOW + "         Rebuild in IntelliJ (Cmd+F9) "
                    + "or drop --no-compile." + OFF);
        }

        System.out.println(DIM + "using IntelliJ output -> " + outDir + OFF);
        return outDir.toString();
    }

    // ------------------------------------------------------------- session

    /** A running instance of the program under test, read line by line. */
    private static final class Session implements AutoCloseable {
        private final Process process;
        private final BlockingQueue<String> lines = new ArrayBlockingQueue<>(1024);
        private final PrintWriter input;

        Session(String classpath, String mainClass) throws IOException {
            // Reuse the JDK that is running this file, so there is nothing to configure and no
            // dependence on what happens to be on the shell PATH.
            String java = Paths.get(System.getProperty("java.home"), "bin", "java").toString();
            process = new ProcessBuilder(java, "-cp", classpath, mainClass)
                    .redirectErrorStream(true)
                    .start();
            input = new PrintWriter(process.outputWriter(), true);

            Thread reader = new Thread(() -> {
                try (BufferedReader out = new BufferedReader(
                        new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = out.readLine()) != null) {
                        lines.put(line);
                    }
                } catch (IOException | InterruptedException ignored) {
                    // Program exited or the run was cut short; drain() reports what arrived.
                }
            });
            reader.setDaemon(true);
            reader.start();
        }

        /** Collects output until the program has been quiet for the given timeout. */
        List<String> drain(long timeoutMs) throws InterruptedException {
            List<String> collected = new ArrayList<>();
            while (true) {
                String line = lines.poll(timeoutMs, TimeUnit.MILLISECONDS);
                if (line == null) {
                    return collected;
                }
                collected.add(line);
            }
        }

        void send(String command) {
            input.println(command);
        }

        @Override
        public void close() {
            input.close();
            try {
                if (!process.waitFor(5, TimeUnit.SECONDS)) {
                    process.destroyForcibly();
                }
            } catch (InterruptedException e) {
                process.destroyForcibly();
                Thread.currentThread().interrupt();
            }
        }
    }

    // -------------------------------------------------------------- runner

    private static boolean runCase(TestCase testCase, String classpath, String mainClass)
            throws IOException, InterruptedException {
        System.out.println();
        System.out.println(BOLD + testCase.id() + " — " + testCase.title() + OFF);
        if (testCase.aim() != null) {
            System.out.println(DIM + "aim: " + testCase.aim() + OFF);
        }
        System.out.println(DIM + "-".repeat(60) + OFF);

        try (Session session = new Session(classpath, mainClass)) {
            List<String> startup = session.drain(STARTUP_TIMEOUT_MS);
            startup.forEach(line -> System.out.println("  " + line));

            for (Step step : testCase.steps()) {
                List<String> actual;
                if (step.isStartup()) {
                    actual = normalise(startup);
                } else {
                    System.out.println(BOLD + "> " + step.command() + OFF);
                    session.send(step.command());
                    actual = normalise(session.drain(IDLE_TIMEOUT_MS));
                    actual.forEach(line -> System.out.println("  " + line));
                }

                if (!matches(step.expected(), actual)) {
                    reportFailure(testCase, step, step.expected(), actual);
                    return false;
                }
            }
        }

        System.out.println(GREEN + "PASS" + OFF);
        return true;
    }

    private static void reportFailure(
            TestCase testCase, Step step, List<String> expected, List<String> actual) {
        System.out.println();
        System.out.println(RED + BOLD + "FAILED: " + testCase.id() + " — " + testCase.title() + OFF);
        if (testCase.aim() != null) {
            System.out.println(DIM + "aim: " + testCase.aim() + OFF);
        }
        System.out.println(DIM + (step.isStartup() ? "startup output" : "command: " + step.command()) + OFF);

        System.out.println();
        System.out.println(GREEN + "expected:" + OFF);
        printBlock(expected);
        System.out.println();
        System.out.println(RED + "actual:" + OFF);
        printBlock(actual);

        if (!expected.contains(WILDCARD)) {
            System.out.println();
            System.out.println(YELLOW + "first difference:" + OFF);
            int width = Math.max(expected.size(), actual.size());
            for (int n = 0; n < width; n++) {
                String want = n < expected.size() ? expected.get(n) : "(missing)";
                String got = n < actual.size() ? actual.get(n) : "(missing)";
                if (!want.equals(got)) {
                    System.out.println("  line " + (n + 1) + ":");
                    System.out.println("    expected: \"" + want + "\"");
                    System.out.println("    actual:   \"" + got + "\"");
                    break;
                }
            }
        }

        System.out.println();
        System.out.println(RED + "Test session terminated at the first failure." + OFF);
    }

    private static void printBlock(List<String> lines) {
        if (lines.isEmpty()) {
            System.out.println("  (no output)");
            return;
        }
        lines.forEach(line -> System.out.println("  " + line));
    }
}
