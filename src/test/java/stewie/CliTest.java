package stewie;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Verifies the real CLI entry point in child JVMs with isolated working directories and exact output.
 */
public class CliTest {
    private static final String GREETING = """
            ███████╗ ████████╗ ███████╗ ██╗    ██╗ ██╗ ███████╗
            ██╔════╝ ╚══██╔══╝ ██╔════╝ ██║    ██║ ██║ ██╔════╝
            ███████╗    ██║    █████╗   ██║ █╗ ██║ ██║ █████╗
            ╚════██║    ██║    ██╔══╝   ██║███╗██║ ██║ ██╔══╝
            ███████║    ██║    ███████╗ ╚███╔███╔╝ ██║ ███████╗
            ╚══════╝    ╚═╝    ╚══════╝  ╚══╝╚══╝  ╚═╝ ╚══════╝

            Ah, there you are. I'm Stewie.
            Tell me your tasks. Clearly, this operation requires supervision.
            """;
    private static final String GOODBYE =
            "Very well. Do come back. I mean, someone must supervise your progress.\n";
    private static final String LIST = "Behold, your agenda. Let us examine the scale of this undertaking.\n";
    private static final String EMPTY =
            "No tasks to show. How suspiciously serene. Try adding a task or checking your search.\n";

    @TempDir
    private Path directory;

    /**
     * Verifies EOF terminates cleanly without creating storage or printing an unsolicited farewell.
     */
    @Test
    public void run_handlesEndOfInput() throws Exception {
        assertEquals(GREETING, runCli(""));
        assertFalse(Files.exists(directory.resolve("data")));
    }

    /**
     * Verifies bye exits immediately and ignores subsequent input.
     */
    @Test
    public void run_stopsAtBye() throws Exception {
        assertEquals(GREETING + GOODBYE, runCli("  BYE  \ntodo ignored\n"));
        assertFalse(Files.exists(directory.resolve("data")));
    }

    /**
     * Verifies all successful command routes, normalization, persistence, and restart.
     */
    @Test
    public void run_handlesCompleteTaskLifecycle() throws Exception {
        String input = """
                TODO Read Book
                deadline Report /by 2026-01-02
                event Trip /from 2026-01-01 /to 2026-01-03
                mark 1
                unmark 1
                update 1 Read More
                update 2 d/2026-01-04
                update 3 from/2026-01-02 to/2026-01-04
                find trip
                delete 2
                list
                bye
                """;
        String output = """
                Consider it recorded. A small triumph for competent administration.
                [T] [ ] read book
                Your agenda now contains 1 task. Do try to keep up.
                Consider it recorded. A small triumph for competent administration.
                [D] [ ] report (by: 02 Jan 2026)
                Your agenda now contains 2 tasks. Do try to keep up.
                Consider it recorded. A small triumph for competent administration.
                [E] [ ] trip (from: 01 Jan 2026 to: 03 Jan 2026)
                Your agenda now contains 3 tasks. Do try to keep up.
                Revised to your specifications. Yes, even that detail.
                [T] [ ] read more
                Revised to your specifications. Yes, even that detail.
                [D] [ ] report (by: 04 Jan 2026)
                Revised to your specifications. Yes, even that detail.
                [E] [ ] trip (from: 02 Jan 2026 to: 04 Jan 2026)
                Behold, your agenda. Let us examine the scale of this undertaking.
                1. [E] [ ] trip (from: 02 Jan 2026 to: 04 Jan 2026)
                Behold, your agenda. Let us examine the scale of this undertaking.
                1. [T] [ ] read more
                2. [E] [ ] trip (from: 02 Jan 2026 to: 04 Jan 2026)
                """;
        assertEquals(GREETING + output + GOODBYE, runCli(input));
        assertEquals("T | 0 | read more\nE | 0 | trip | 02 Jan 2026 | 04 Jan 2026",
                Files.readString(directory.resolve("data/stewie.txt")));
        assertEquals(GREETING + LIST + "1. [T] [ ] read more\n"
                + "2. [E] [ ] trip (from: 02 Jan 2026 to: 04 Jan 2026)\n" + GOODBYE, runCli("list\nbye\n"));
    }

    /**
     * Verifies malformed numbers, missing update fields, invalid commands, and dates allow recovery.
     */
    @Test
    public void run_recoversAfterInvalidCommands() throws Exception {
        String input = """
                nonsense
                mark
                unmark zero
                delete 0
                update
                update 1
                update 1 from/
                todo
                deadline report /by 2026-02-30
                mark 1
                unmark 1
                delete 1
                update 1 revised
                find absent
                list
                bye
                """;
        String output = """
                What precisely is the plan? Use a command: todo, event, deadline, mark, unmark, delete, find, \
                update, list, bye + description!
                Numbers, please. Use: mark <number>.
                Numbers, please. Use: unmark <number>.
                Numbers, please. Use: delete <number>.
                A revision needs details. Use: update <number> [description] [d/<deadline>] [from/<from>] [to/<to>]
                A revision needs details. Use: update <number> [description] [d/<deadline>] [from/<from>] [to/<to>]
                A slight flaw in your plan: Update fields cannot be empty.
                A slight flaw in your plan: Use: todo <description>.
                A slight flaw in your plan: Use a real calendar date, such as 2026-08-28 or 28/8/2026 (dates only).
                That task exists only in your imagination. Use a listed number: mark <number>
                That task exists only in your imagination. Use a listed number: unmark <number>
                That task exists only in your imagination. Use a listed number: delete <number>
                That task exists only in your imagination. Use a listed number: update <number>
                """;
        assertEquals(GREETING + output + LIST + EMPTY + LIST + EMPTY + GOODBYE, runCli(input));
        assertFalse(Files.exists(directory.resolve("data/stewie.txt")));
    }

    /**
     * Verifies startup warnings and storage exceptions are visible without false success replies.
     */
    @Test
    public void run_protectsDamagedStorage() throws Exception {
        Path file = directory.resolve("data/stewie.txt");
        Files.createDirectories(file.getParent());
        String content = "bad record\nT | 0 | preserved";
        Files.writeString(file, content);
        String warning = "Storage warning: Invalid or duplicate records at lines 1. "
                + "Valid tasks are available read-only. Back up and repair the task file, then restart.\n";
        String error = "Tasks are read-only. Back up and repair the task file, then restart.\n";
        assertEquals(GREETING + warning + error + error + error + error + error
                + LIST + "1. [T] [ ] preserved\n" + GOODBYE,
                runCli("todo new\nmark 1\nunmark 1\nupdate 1 changed\ndelete 1\nlist\nbye\n"));
        assertEquals(content, Files.readString(file));
    }

    /**
     * Verifies Burmese input survives a command session and an application restart in a Burmese locale.
     */
    @Test
    public void run_preservesBurmeseInput() throws Exception {
        assertEquals(GREETING + "Consider it recorded. A small triumph for competent administration.\n"
                + "[T] [ ] စာအုပ် ဖတ်ရန်\nYour agenda now contains ၁ task. Do try to keep up.\n",
                runCli("todo စာအုပ် ဖတ်ရန်\n", "my", "MM"));
        assertEquals(GREETING + LIST + "1. [T] [ ] စာအုပ် ဖတ်ရန်\n" + GOODBYE,
                runCli("find စာအုပ်\nbye\n", "my", "MM"));
    }

    /**
     * Runs the default English session independently of the host operating-system language.
     */
    private String runCli(String input) throws IOException, URISyntaxException, InterruptedException {
        return runCli(input, "en", "US");
    }

    /**
     * Runs a bounded child process with the current Java 25 runtime and optional coverage instrumentation.
     */
    private String runCli(String input, String language, String country)
            throws IOException, URISyntaxException, InterruptedException {
        List<String> command = createCliCommand(language, country);
        Path stdout = directory.resolve("stdout.txt");
        Path stderr = directory.resolve("stderr.txt");
        Process process = new ProcessBuilder(command).directory(directory.toFile())
                .redirectOutput(stdout.toFile()).redirectError(stderr.toFile()).start();
        try {
            try (var stream = process.getOutputStream()) {
                stream.write(input.getBytes(StandardCharsets.UTF_8));
            }
            assertTrue(process.waitFor(15, TimeUnit.SECONDS), "CLI did not terminate within 15 seconds");
            assertEquals(0, process.exitValue(), () -> "CLI failed; see " + stderr);
            assertEquals("", Files.readString(stderr), "Unexpected CLI stderr");
            return Files.readString(stdout).replace("\r\n", "\n");
        } finally {
            process.destroyForcibly();
            process.waitFor(5, TimeUnit.SECONDS);
        }
    }

    /**
     * Builds the CLI launch command using the current runtime, requested locale, and test classpath.
     */
    private List<String> createCliCommand(String language, String country) throws URISyntaxException {
        boolean isWindows = System.getProperty("os.name").startsWith("Windows");
        Path java = Path.of(System.getProperty("java.home"), "bin", isWindows ? "java.exe" : "java");
        Path classes = Path.of(Stewie.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        List<String> command = new ArrayList<>(List.of(java.toString(), "-ea", "-Dfile.encoding=UTF-8",
                "-Duser.language=" + language, "-Duser.country=" + country));
        addCoverageAgent(command);
        command.addAll(List.of("-cp", classes.toString(), "stewie.Stewie"));
        return command;
    }

    /**
     * Adds optional instrumentation with a separate output file that the Gradle worker cannot overwrite.
     */
    private void addCoverageAgent(List<String> command) {
        for (String argument : ManagementFactory.getRuntimeMXBean().getInputArguments()) {
            if (argument.startsWith("-javaagent:") && argument.contains("jacocoagent.jar")) {
                String coverage = Path.of(System.getProperty("stewie.cliCoverageFile")).toAbsolutePath().toString();
                String agent = argument.substring(0, argument.indexOf('='));
                command.add(agent + "=destfile=" + coverage + ",append=true");
            }
        }
    }
}
