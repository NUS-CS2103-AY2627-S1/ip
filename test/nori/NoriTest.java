package nori;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Runs end-to-end regression tests for Nori's commands, date queries, and persisted task state.
 */
public class NoriTest {
    /**
     * Runs every end-to-end regression test.
     *
     * @param args ignored command-line arguments.
     * @throws Exception if a test cannot create its isolated environment or Nori cannot run.
     */
    public static void main(String[] args) throws Exception {
        runTest("Completes a mixed task workflow", NoriTest::mixedTaskWorkflow_updatesListAndStatuses);
        runTest("Rejects malformed input without adding tasks",
                NoriTest::malformedCommands_leaveTaskListEmpty);
        runTest("Shows command help", NoriTest::helpCommand_listsSupportedCommands);
        runTest("Finds deadlines and events on a date", NoriTest::onCommand_matchesDeadlineAndEventDates);
        runTest("Lists deadlines and events in a date range", NoriTest::listCommand_matchesDateRange);
        runTest("Finds tasks by description keyword", NoriTest::findCommand_matchesTaskDescriptions);
        runTest("Persists every task type and completion state", NoriTest::savedTasks_restoreAcrossLaunches);
        runTest("Lays a day out as an ordered schedule",
                NoriTest::scheduleCommand_ordersTheDayAndGroupsTheRest);
        System.out.println("All Nori regression tests passed.");
    }

    private static void mixedTaskWorkflow_updatesListAndStatuses() throws Exception {
        Path testDirectory = Files.createTempDirectory("nori-regression-test-");
        try {
            String output = runNori(testDirectory, "todo read book\n"
                    + "deadline return book /by 2024-02-29\n"
                    + "event exam /from 2024-02-29 0900 /to 2024-02-29 1200\n"
                    + "mark 2\n"
                    + "unmark 2\n"
                    + "delete 1\n"
                    + "list\n"
                    + "bye\n");

            String finalList = getFinalList(output);
            assertContains(finalList, "1.[D][ ] return book (by: Feb 29 2024)");
            assertContains(finalList, "2.[E][ ] exam (from: 2024-02-29 0900 to: 2024-02-29 1200)");
            assertNotContains(finalList, "read book");
            assertContains(output, "Noot noot! This task is now ice-cold complete:");
            assertContains(output, "Brrr... thawing this task back out:");
        } finally {
            deleteDirectory(testDirectory);
        }
    }

    private static void malformedCommands_leaveTaskListEmpty() throws Exception {
        Path testDirectory = Files.createTempDirectory("nori-regression-test-");
        try {
            String output = runNori(testDirectory, "todo\n"
                    + "deadline impossible /by 2024-02-30\n"
                    + "event invalid /from 2024-2-29 0900 /to 2024-02-29 1000\n"
                    + "on\n"
                    + "on 2024-02-30\n"
                    + "list\n"
                    + "bye\n");

            assertContains(output, "NOOT?! A todo needs a description.");
            assertContains(output, "NOOT?! I cannot understand \"2024-02-30\" as a deadline.");
            assertContains(output, "NOOT?! I cannot understand \"2024-2-29\" as an event date.");
            assertContains(output, "NOOT?! \"on\" needs a date.");
            assertContains(output, "NOOT?! I cannot understand \"2024-02-30\" as a date.");
            assertContains(output, "The iceberg is empty.");
        } finally {
            deleteDirectory(testDirectory);
        }
    }

    private static void helpCommand_listsSupportedCommands() throws Exception {
        Path testDirectory = Files.createTempDirectory("nori-regression-test-");
        try {
            String output = runNori(testDirectory, "help\nbye\n");

            assertContains(output, "Noot noot! Here is what my flippers understand:");
            assertContains(output, "deadline <description> /by yyyy-MM-dd");
            assertContains(output, "on yyyy-MM-dd");
            assertContains(output, "list /from yyyy-MM-dd /to yyyy-MM-dd");
            assertContains(output, "find <keyword>");
            assertContains(output, "schedule yyyy-MM-dd");
            assertContains(output,
                    "Penguin tip: use yyyy-MM-dd in an event's /from or /to so \"on\" can find it.");
            assertContains(output, "Penguin tip: add a time like 0900, 9:30 or 2pm to an event's"
                    + " /from to place it on the schedule.");
        } finally {
            deleteDirectory(testDirectory);
        }
    }

    private static void onCommand_matchesDeadlineAndEventDates() throws Exception {
        Path testDirectory = Files.createTempDirectory("nori-regression-test-");
        try {
            String output = runNori(testDirectory, "todo unrelated task\n"
                    + "deadline submit work /by 2024-03-01\n"
                    + "event workshop /from 2024-03-01 0900 /to 2024-03-01 1100\n"
                    + "event overnight session /from 2024-02-29 2300 /to 2024-03-01 0100\n"
                    + "on 2024-03-01\n"
                    + "on 2024-03-02\n"
                    + "bye\n");

            String matchingTasks = getSectionAfter(output,
                    "Noot noot! Things hatching on 2024-03-01:");
            assertContains(matchingTasks, "2.[D][ ] submit work (by: Mar 01 2024)");
            assertContains(matchingTasks, "3.[E][ ] workshop (from: 2024-03-01 0900 to: 2024-03-01 1100)");
            assertContains(matchingTasks,
                    "4.[E][ ] overnight session (from: 2024-02-29 2300 to: 2024-03-01 0100)");
            assertNotContains(matchingTasks, "unrelated task");
            assertContains(output, "Nothing is hatching on 2024-03-02. The ice is quiet.");
        } finally {
            deleteDirectory(testDirectory);
        }
    }

    private static void listCommand_matchesDateRange() throws Exception {
        Path testDirectory = Files.createTempDirectory("nori-regression-test-");
        try {
            String output = runNori(testDirectory, "todo unrelated task\n"
                    + "deadline early deadline /by 2018-12-31\n"
                    + "deadline first day /by 2019-01-01\n"
                    + "deadline last day /by 2021-01-01\n"
                    + "deadline late deadline /by 2021-01-02\n"
                    + "event long project /from 2018-12-01 0900 /to 2021-02-01 1700\n"
                    + "event leap-day workshop /from 2020-02-29 0900 /to 2020-02-29 1200\n"
                    + "event old meeting /from 2017-12-31 0900 /to 2017-12-31 1200\n"
                    + "event undated meeting /from Monday 0900 /to 1200\n"
                    + "list /from 2019-01-01 /to 2021-01-01\n"
                    + "list /from 2022-01-01 /to 2022-01-02\n"
                    + "list /from 2021-01-02 /to 2021-01-01\n"
                    + "list /from 2019-01-01\n"
                    + "list unexpected details\n"
                    + "bye\n");

            String matchingTasks = getSectionAfter(output,
                    "Noot noot! Things hatching from 2019-01-01 to 2021-01-01:");
            assertContains(matchingTasks, "3.[D][ ] first day (by: Jan 01 2019)");
            assertContains(matchingTasks, "4.[D][ ] last day (by: Jan 01 2021)");
            assertContains(matchingTasks, "6.[E][ ] long project");
            assertContains(matchingTasks, "7.[E][ ] leap-day workshop");
            assertNotContains(matchingTasks, "unrelated task");
            assertNotContains(matchingTasks, "early deadline");
            assertNotContains(matchingTasks, "late deadline");
            assertNotContains(matchingTasks, "old meeting");
            assertNotContains(matchingTasks, "undated meeting");
            assertContains(output, "Nothing is hatching from 2022-01-01 to 2022-01-02. The ice is quiet.");
            assertContains(output, "The \"/to\" date cannot be before the \"/from\" date.");
            assertContains(output, "A date-range list needs \"/to\" and an end date.");
            assertContains(output, "Use either \"list\" or \"list /from 2019-01-01 /to 2021-01-01\".");
        } finally {
            deleteDirectory(testDirectory);
        }
    }

    private static void scheduleCommand_ordersTheDayAndGroupsTheRest() throws Exception {
        Path testDirectory = Files.createTempDirectory("nori-regression-test-");
        try {
            String output = runNori(testDirectory, "todo unrelated task\n"
                    + "deadline submit work /by 2024-03-01\n"
                    + "event late review /from 2024-03-01 1600 /to 1700\n"
                    + "event standup /from 2024-03-01 9am /to 9:30am\n"
                    + "event conference /from 2024-02-28 0900 /to 2024-03-03 1700\n"
                    + "schedule 2024-03-01\n"
                    + "schedule 2024-04-01\n"
                    + "bye\n");

            String schedule = getSectionAfter(output, "Noot noot! Schedule for 2024-03-01:");
            assertContains(schedule, "09:00 4.[E][ ] standup (from: 2024-03-01 9am to: 9:30am)");
            assertContains(schedule, "16:00 3.[E][ ] late review (from: 2024-03-01 1600 to: 1700)");
            assertContains(schedule,
                    "      5.[E][ ] conference (from: 2024-02-28 0900 to: 2024-03-03 1700)");
            assertContains(schedule, "      2.[D][ ] submit work (by: Mar 01 2024)");
            assertContains(schedule, "      1.[T][ ] unrelated task");
            assertOrder(schedule, "09:00 4.[E][ ] standup", "16:00 3.[E][ ] late review");
            assertOrder(schedule, "16:00 3.[E][ ] late review", "All day:");
            assertOrder(schedule, "All day:", "Due:");
            assertOrder(schedule, "Due:", "Anytime:");

            String laterDay = getSectionAfter(output, "Noot noot! Schedule for 2024-04-01:");
            assertContains(laterDay, "      1.[T][ ] unrelated task");
            assertNotContains(laterDay, "submit work");
        } finally {
            deleteDirectory(testDirectory);
        }
    }

    private static void savedTasks_restoreAcrossLaunches() throws Exception {
        Path testDirectory = Files.createTempDirectory("nori-regression-test-");
        try {
            runNori(testDirectory, "todo pack bag\n"
                    + "deadline return book /by 2024-12-31\n"
                    + "event celebration /from 2024-12-31 2000 /to 2025-01-01 0100\n"
                    + "mark 1\n"
                    + "mark 3\n"
                    + "bye\n");
            String output = runNori(testDirectory, "list\n"
                    + "on 2025-01-01\n"
                    + "bye\n");

            assertContains(output, "1.[T][X] pack bag");
            assertContains(output, "2.[D][ ] return book (by: Dec 31 2024)");
            assertContains(output, "3.[E][X] celebration (from: 2024-12-31 2000 to: 2025-01-01 0100)");
            assertContains(output, "Noot noot! Things hatching on 2025-01-01:");
            assertContains(output, "3.[E][X] celebration");
        } finally {
            deleteDirectory(testDirectory);
        }
    }

    private static void findCommand_matchesTaskDescriptions() throws Exception {
        Path testDirectory = Files.createTempDirectory("nori-regression-test-");
        try {
            String output = runNori(testDirectory, "todo read book\n"
                    + "deadline return book /by 2019-06-06\n"
                    + "event book fair /from 2019-06-06 1000 /to 2019-06-06 1800\n"
                    + "todo Buy Milk\n"
                    + "mark 1\n"
                    + "find bicycle\n"
                    + "find milk\n"
                    + "find BOOK\n"
                    + "find\n"
                    + "bye\n");

            // The final search is "find BOOK", so this is that search's section.
            // An upper-case keyword matching lower-case descriptions proves the
            // keyword side is case folded.
            String matchingTasks = getSectionAfter(output, "Noot noot! I found these fish:");
            assertContains(matchingTasks, "1.[T][X] read book");
            assertContains(matchingTasks, "2.[D][ ] return book (by: Jun 06 2019)");
            assertContains(matchingTasks, "3.[E][ ] book fair");
            assertNotContains(matchingTasks, "Buy Milk");

            // "find milk" matching "Buy Milk" proves the description side is case
            // folded too, and that a match keeps its number from the full list.
            assertContains(output, "4.[T][ ] Buy Milk");
            assertContains(output, "No matching fish in this sea. Try another keyword!");
            assertContains(output, "NOOT?! \"find\" needs a keyword.");
        } finally {
            deleteDirectory(testDirectory);
        }
    }

    /**
     * Runs one test case and reports it as passed, letting a failure stop the suite.
     *
     * @param testName the name to report for this test.
     * @param testCase the test to run.
     */
    private static void runTest(String testName, NoriTestCase testCase) throws Exception {
        testCase.run();
        System.out.println("PASS: " + testName);
    }

    /**
     * Runs Nori in the given directory with one complete standard-input session.
     *
     * @param workingDirectory the isolated working directory for this Nori launch.
     * @param input the standard-input content to send to Nori.
     * @return Nori's complete console output.
     * @throws IOException if the child process cannot be started or communicated with.
     * @throws InterruptedException if the current thread is interrupted while Nori is running.
     */
    private static String runNori(Path workingDirectory, String input)
            throws IOException, InterruptedException {
        Path storageDirectory = workingDirectory.resolve("data").toAbsolutePath();
        Process process = new ProcessBuilder("java", "-ea",
                "-Dnori.storage.dir=" + storageDirectory,
                "-cp", getAbsoluteClassPath(), "nori.Nori")
                .directory(workingDirectory.toFile())
                .redirectErrorStream(true)
                .start();
        process.getOutputStream().write(input.getBytes(StandardCharsets.UTF_8));
        process.getOutputStream().close();

        String output = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new AssertionError("Nori exited with status " + exitCode + ":\n" + output);
        }
        return output;
    }

    /**
     * Returns the final displayed task list from a Nori session.
     *
     * @param output Nori's complete console output.
     * @return the part of the output beginning with the final task-list heading.
     */
    private static String getFinalList(String output) {
        return getSectionAfter(output, "Noot noot! Tasks currently chilling on the iceberg:");
    }

    /**
     * Returns the final output section beginning with a required heading.
     *
     * @param output Nori's complete console output.
     * @param heading the required section heading.
     * @return the output beginning at the final occurrence of {@code heading}.
     */
    private static String getSectionAfter(String output, String heading) {
        int headingIndex = output.lastIndexOf(heading);
        if (headingIndex == -1) {
            throw new AssertionError("Expected output to contain \"" + heading + "\" but was:\n" + output);
        }
        return output.substring(headingIndex);
    }

    /**
     * Returns the compiled test classpath as an absolute path for child Nori processes.
     *
     * @return the absolute classpath for the compiled test classes.
     */
    private static String getAbsoluteClassPath() {
        String[] classPathEntries = System.getProperty("java.class.path").split(File.pathSeparator);
        return Arrays.stream(classPathEntries)
                .map(entry -> Path.of(entry).toAbsolutePath().toString())
                .collect(Collectors.joining(File.pathSeparator));
    }

    /**
     * Deletes an isolated test directory and all of its contents.
     *
     * @param directory the directory to delete.
     * @throws IOException if the directory tree cannot be read.
     */
    private static void deleteDirectory(Path directory) throws IOException {
        try (Stream<Path> paths = Files.walk(directory)) {
            paths.sorted(Comparator.reverseOrder()).forEach(NoriTest::deletePath);
        }
    }

    /**
     * Deletes one file or directory while cleaning up a test directory.
     *
     * @param path the path to delete.
     */
    private static void deletePath(Path path) {
        try {
            Files.delete(path);
        } catch (IOException exception) {
            throw new AssertionError("Could not clean up test path: " + path, exception);
        }
    }

    /**
     * Fails the current test unless the actual text contains the expected text.
     *
     * @param actual the text to inspect.
     * @param expected the text that must be present.
     */
    private static void assertOrder(String actual, String first, String second) {
        int firstIndex = actual.indexOf(first);
        int secondIndex = actual.indexOf(second);
        if (firstIndex == -1 || secondIndex == -1 || firstIndex > secondIndex) {
            throw new AssertionError("Expected \"" + first + "\" before \"" + second
                    + "\" but was:\n" + actual);
        }
    }

    private static void assertContains(String actual, String expected) {
        if (!actual.contains(expected)) {
            throw new AssertionError("Expected output to contain \"" + expected + "\" but was:\n" + actual);
        }
    }

    /**
     * Fails the current test if the actual text contains the unexpected text.
     *
     * @param actual the text to inspect.
     * @param unexpected the text that must be absent.
     */
    private static void assertNotContains(String actual, String unexpected) {
        if (actual.contains(unexpected)) {
            throw new AssertionError("Expected output not to contain \"" + unexpected + "\" but was:\n"
                    + actual);
        }
    }

    /**
     * Represents one Nori regression test that can report a checked failure.
     */
    @FunctionalInterface
    private interface NoriTestCase {
        /**
         * Runs this test case.
         *
         * @throws Exception if the test fails or its environment cannot be prepared.
         */
        void run() throws Exception;
    }
}
