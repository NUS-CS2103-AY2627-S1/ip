package cheecken;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Checks command output and persistence through application processes.
     * @return command inputs and their expected output fragments
 */
class CheeckenUiTest {
    /**
     * Provides command inputs and expected output fragments.
     * @return command inputs and their expected output fragments
     */
    static Stream<Arguments> cases() {
        return Stream.of(
            Arguments.of("Exit with bye", "bye\n", "Bye. Hope to see you again soon!"),
            Arguments.of("Reject empty todo", "todo\n", "There's no task that is empty."),
            Arguments.of("Add a todo task", "todo buy her flowers\n", "[T][ ] buy her flowers"),
            Arguments.of("Reject empty deadline", "deadline\n", "There's no task that is empty."),
            Arguments.of("Reject deadline without task", "deadline /by\n", "There's no task that is empty."),
            Arguments.of("Reject deadline without date", "deadline buy her flowers\n", "No time how I set the task..."),
            Arguments.of("Reject deadline with empty date", "deadline buy her flowers /by\n",
                "No time how I set the task..."),
            Arguments.of("Add deadline with 12-hour time", "deadline buy her flowers /by 15/10/2025 1800\n",
                "Oct 15 2025 6:00 PM"),
            Arguments.of("Reject empty event", "event\n", "There's no task that is empty."),
            Arguments.of("Reject event without task", "event /from /to\n", "There's no task that is empty."),
            Arguments.of("Reject event without datetimes", "event love me\n", "No time how I set the task..."),
            Arguments.of("Add event with 12-hour times", "event love me /from 15/10/2025 0900 /to 15/10/3000 1100\n",
                "9:00 AM"),
            Arguments.of("Find tasks whose descriptions contain a keyword",
                "todo read book\ndeadline return book /by 15/10/2025\nfind book\nbye\n",
                "Here are the matching tasks in your list:\n1.[T][ ] read book\n2.[D][ ] return book"),
            Arguments.of("Find is case-insensitive",
                "todo Read Book\nfind book\nbye\n",
                "1.[T][ ] Read Book"),
            Arguments.of("Find with no matches reports no matching tasks",
                "todo buy flowers\nfind book\nbye\n",
                "There are no matching tasks in your list."),
            Arguments.of("Find without a keyword is rejected",
                "todo read book\nfind\nbye\n",
                "Please provide a keyword to search for."),
            Arguments.of("Natural deadline resolves on a fixed Monday",
                "deadline report /by tomorrow\nbye\n", "[D][ ] report (by: Sep 08 2026)"),
            Arguments.of("Same weekday advances seven days",
                "event meeting /from Mon 0900 /to MONDAY 1000\nbye\n",
                "from: Sep 14 2026 9:00 AM to: Sep 14 2026 10:00 AM"),
            Arguments.of("Past time today is accepted",
                "deadline earlier /by today 0900\nbye\n", "Sep 07 2026 9:00 AM"),
            Arguments.of("Now uses the command time",
                "deadline instant /by NOW\nbye\n", "Sep 07 2026 3:30 PM"));
    }

    /**
     * Verifies command produces expected output.
     * @param name descriptive test-case name
     * @param input input supplied by the test case
     * @param expected expected result for this test case
     * @throws Exception if test setup, execution, or file access fails
     */
    @ParameterizedTest(name = "{0}")
    @MethodSource("cases")
    void commandProducesExpectedOutput(String name, String input, String expected) throws Exception {
        String output = run(input, null);
        assertTrue(output.contains(expected), () -> "Expected: " + expected + "\nActual:\n" + output);
    }

    /**
     * Verifies loads persisted task.
     * @throws Exception if test setup, execution, or file access fails
     */
    @org.junit.jupiter.api.Test
    void loadsPersistedTask() throws Exception {
        String output = run("list\nbye\n", "T | 1 | read book\n");
        assertTrue(output.contains("1.[T][X] read book"));
    }

    /**
     * Verifies persists task changes.
     * @throws Exception if test setup, execution, or file access fails
     */
    @org.junit.jupiter.api.Test
    void persistsTaskChanges() throws Exception {
        Path dir = Files.createTempDirectory("cheecken-ui-");
        Files.createDirectories(dir.resolve("data"));
        ProcessBuilder pb = process("todo read book\nbye\n", dir);
        Process process = pb.start();
        process.getOutputStream().write("todo read book\nbye\n".getBytes());
        process.getOutputStream().close();
        process.waitFor();
        assertTrue(Files.readString(dir.resolve("data/cheecken.txt")).contains("T | 0 | read book"));
    }

    /**
     * Runs commands in an isolated directory with optional persisted tasks.
     * @param input input supplied by the test case
     * @param persisted initial storage contents, or null for no task file
     * @return captured console output
     * @throws Exception if test setup, execution, or file access fails
     */
    private static String run(String input, String persisted) throws Exception {
        Path dir = Files.createTempDirectory("cheecken-ui-");
        if (persisted != null) {
            Files.createDirectories(dir.resolve("data"));
            Files.writeString(dir.resolve("data/cheecken.txt"), persisted);
        }
        Process process = process(input, dir).start();
        process.getOutputStream().write(input.getBytes());
        process.getOutputStream().close();
        return new String(process.getInputStream().readAllBytes());
    }

    /**
     * Builds an application process isolated from real user data.
     * @param input input supplied by the test case
     * @param dir isolated working directory for the application process
     * @return configured application process builder
     */
    private static ProcessBuilder process(String input, Path dir) {
        String java = Path.of(System.getProperty("java.home"), "bin", "java").toString();
        return new ProcessBuilder(java, "-ea", "-cp", System.getProperty("java.class.path"), "cheecken.FixedClockCli")
                .directory(dir.toFile()).redirectErrorStream(true).redirectInput(ProcessBuilder.Redirect.PIPE);
    }
}
