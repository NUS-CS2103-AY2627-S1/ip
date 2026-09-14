package groot;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import groot.testutil.IsolatedProcess;

/**
 * Tests command responses and failed-save recovery with isolated storage and assertions enabled.
 */
public class GrootTest {
    /** Mixed task data whose main-list order differs from either date-sorted display. */
    private static final String SORT_DATA = String.join("\n",
            "T | 0 | groceries",
            "D | 0 | report | 2026-09-20",
            "D | 0 | beta | 2026-09-12",
            "D | 1 | Alpha | 2026-09-12",
            "E | 0 | meeting | Mon 2pm | 4pm") + "\n";

    private static final String MAIN_LIST = String.join("\n",
            " Here are the tasks in your list:",
            " 1.[T][ ] groceries",
            " 2.[D][ ] report (by: Sep 20 2026)",
            " 3.[D][ ] beta (by: Sep 12 2026)",
            " 4.[D][X] Alpha (by: Sep 12 2026)",
            " 5.[E][ ] meeting (from: Mon 2pm to: 4pm)");

    private static final String SORT_ASCENDING = String.join("\n",
            " Here are your deadlines sorted by date (earliest first):",
            " 4.[D][X] Alpha (by: Sep 12 2026)",
            " 3.[D][ ] beta (by: Sep 12 2026)",
            " 2.[D][ ] report (by: Sep 20 2026)");

    private static final String SORT_DESCENDING = String.join("\n",
            " Here are your deadlines sorted by date (latest first):",
            " 2.[D][ ] report (by: Sep 20 2026)",
            " 4.[D][X] Alpha (by: Sep 12 2026)",
            " 3.[D][ ] beta (by: Sep 12 2026)");

    @TempDir
    public Path temporaryDirectory;

    @Test
    public void main_invalidThenValidCommands_reportsErrorsAndContinues() throws Exception {
        String output = runProcess(Groot.class, "unknown\n\nhelp\nbye\n");
        assertTrue(output.contains("Oops! I don't recognise that command."), output);
        assertTrue(output.contains("Oops! Please enter a command."), output);
        assertTrue(output.contains("Here are the commands you can use:"), output);
        assertTrue(output.contains("Bye. Hope to see you again soon!"), output);
        assertFalse(output.contains("AssertionError"), output);
    }

    @Test
    public void getResponse_repeatedStatusCommands_preservesResponses() throws Exception {
        String input = "todo first\nmark 1\nmark 1\nlist\nunmark 1\nunmark 1\nlist\ndelete 1\nbye\n";
        String output = runProcess(Groot.class, input);
        assertTrue(output.contains(" Nice! I've marked this task as done:\n   [T][X] first"), output);
        assertTrue(output.contains(" Here are the tasks in your list:\n 1.[T][X] first"), output);
        assertTrue(output.contains(" OK, I've marked this task as not done yet:\n   [T][ ] first"), output);
        assertTrue(output.contains(" Here are the tasks in your list:\n 1.[T][ ] first"), output);
        assertTrue(output.contains(" Now you have 0 tasks in the list."), output);
    }

    @Test
    public void getResponse_failedMark_restoresIncompleteStatus() throws Exception {
        assertFailedSavePreservesTasks(false, "mark 2");
    }

    @Test
    public void getResponse_failedUnmark_restoresCompletedStatus() throws Exception {
        assertFailedSavePreservesTasks(true, "unmark 2");
    }

    @Test
    public void getResponse_failedRepeatedMark_preservesCompletedStatus() throws Exception {
        assertFailedSavePreservesTasks(true, "mark 2");
    }

    @Test
    public void getResponse_failedRepeatedUnmark_preservesIncompleteStatus() throws Exception {
        assertFailedSavePreservesTasks(false, "unmark 2");
    }

    @Test
    public void getResponse_failedDelete_restoresPositionAndStatus() throws Exception {
        assertFailedSavePreservesTasks(true, "delete 2");
    }

    @Test
    public void getResponse_failedAdd_removesOnlyNewTask() throws Exception {
        assertFailedSavePreservesTasks(true, "todo new task");
    }

    @Test
    public void getResponse_sortBothDirections_preservesMainListFindAndSavedBytes() throws Exception {
        Path dataFile = writeSortFixture();
        byte[] before = Files.readAllBytes(dataFile);
        String output = runProcess(CommandScenario.class, "", "list", " SoRt\t ", "sort -r",
                " SORT \t--REVERSE ", "sort", "list", "find beta");

        assertEquals(String.join("\n", MAIN_LIST, SORT_ASCENDING, SORT_DESCENDING, SORT_DESCENDING,
                SORT_ASCENDING, MAIN_LIST,
                " Here are the matching tasks in your list:\n 1.[D][ ] beta (by: Sep 12 2026)") + "\n", output);
        assertArrayEquals(before, Files.readAllBytes(dataFile));
        assertEquals(MAIN_LIST + "\n", runProcess(CommandScenario.class, "", "list"));
    }

    @Test
    public void getResponse_invalidSortArguments_preservesStateBetweenValidCommands() throws Exception {
        Path dataFile = writeSortFixture();
        byte[] before = Files.readAllBytes(dataFile);
        String error = " Oops! Use: sort [-r | --reverse].";
        String output = runProcess(CommandScenario.class, "", "sort -x", "sort", "sort reverse",
                "sort -r -r", "list", "sort --reverse extra", "sort -r --reverse", "sort --reverse=true", "list");

        assertEquals(String.join("\n", error, SORT_ASCENDING, error, error, MAIN_LIST,
                error, error, error, MAIN_LIST) + "\n", output);
        assertArrayEquals(before, Files.readAllBytes(dataFile));
    }

    @Test
    public void getResponse_sortWithoutTasks_validatesArgumentsAndCreatesNoDataDirectory() throws Exception {
        String output = runProcess(CommandScenario.class, "", "sort -x", "sort", "sort -r", "sort --reverse");
        assertEquals(" Oops! Use: sort [-r | --reverse].\n"
                + " There are no deadlines to sort.\n".repeat(3), output);
        assertFalse(Files.exists(temporaryDirectory.resolve("data")));
    }

    @Test
    public void getResponse_sortWithoutDeadlines_omitsTodosAndEvents() throws Exception {
        Path dataFile = writeSortFixture();
        String data = "T | 0 | groceries\nE | 1 | meeting | Mon | Tue\n";
        Files.writeString(dataFile, data);

        assertEquals(" There are no deadlines to sort.\n".repeat(2),
                runProcess(CommandScenario.class, "", "sort", "sort -r"));
        assertEquals(data, Files.readString(dataFile));
    }

    @Test
    public void getResponse_modifySortedTaskNumbers_targetsMainListAndRefreshesIndices() throws Exception {
        Path dataFile = writeSortFixture();
        String output = runProcess(CommandScenario.class, "", "sort", "mark 3", "unmark 4", "delete 2", "sort");

        assertEquals(String.join("\n", SORT_ASCENDING,
                " Nice! I've marked this task as done:\n   [D][X] beta (by: Sep 12 2026)",
                " OK, I've marked this task as not done yet:\n   [D][ ] Alpha (by: Sep 12 2026)",
                " Noted. I've removed this task:\n   [D][ ] report (by: Sep 20 2026)"
                        + "\n Now you have 4 tasks in the list.",
                " Here are your deadlines sorted by date (earliest first):",
                " 3.[D][ ] Alpha (by: Sep 12 2026)",
                " 2.[D][X] beta (by: Sep 12 2026)") + "\n", output);
        assertEquals("T | 0 | groceries\nD | 1 | beta | 2026-09-12\nD | 0 | Alpha | 2026-09-12\n"
                + "E | 0 | meeting | Mon 2pm | 4pm\n", Files.readString(dataFile).replace("\r\n", "\n"));
        assertEquals(String.join("\n", " Here are the tasks in your list:", " 1.[T][ ] groceries",
                " 2.[D][X] beta (by: Sep 12 2026)", " 3.[D][ ] Alpha (by: Sep 12 2026)",
                " 4.[E][ ] meeting (from: Mon 2pm to: 4pm)") + "\n",
                runProcess(CommandScenario.class, "", "list"));
    }

    @Test
    public void getResponse_addAfterSort_appendsNormallyAndRetainsOrderAfterRestart() throws Exception {
        Path dataFile = writeSortFixture();
        String output = runProcess(CommandScenario.class, "", "sort -r", "todo new task", "list");
        String listAfterAdd = MAIN_LIST + "\n 6.[T][ ] new task";

        assertEquals(String.join("\n", SORT_DESCENDING,
                " Got it. I've added this task:\n   [T][ ] new task\n Now you have 6 tasks in the list.",
                listAfterAdd) + "\n", output);
        assertEquals(SORT_DATA + "T | 0 | new task\n", Files.readString(dataFile).replace("\r\n", "\n"));
        assertEquals(listAfterAdd + "\n", runProcess(CommandScenario.class, "", "list"));
    }

    @Test
    public void getResponse_sortWhenSavingWouldFail_stillDisplaysWithoutWriting() throws Exception {
        Path dataFile = writeSortFixture();
        byte[] before = Files.readAllBytes(dataFile);

        assertEquals(String.join("\n", SORT_ASCENDING, SORT_DESCENDING, MAIN_LIST) + "\n",
                runProcess(SortWithoutSavingScenario.class, ""));
        assertArrayEquals(before, Files.readAllBytes(dataFile));
    }

    @Test
    public void getCommandResponse_invalidThenValidCommands_classifiesErrorsWithoutChangingState() throws Exception {
        Path dataFile = writeSortFixture();
        byte[] before = Files.readAllBytes(dataFile);
        String output = runProcess(ResponseStatusScenario.class, "", "unknown", "list", "todo",
                "mark 0", "list", "deadline report /by invalid", "sort", "help");
        assertEquals("true\nfalse\ntrue\ntrue\nfalse\ntrue\nfalse\nfalse\n", output);
        assertArrayEquals(before, Files.readAllBytes(dataFile));
    }

    /**
     * Prints response classifications using isolated storage.
     */
    public static class ResponseStatusScenario {
        /**
         * Processes commands in order so errors cannot leak into later successful replies.
         *
         * @param args Commands to process.
         * @throws Exception If saved tasks cannot be loaded.
         */
        public static void main(String[] args) throws Exception {
            Groot groot = new Groot();
            for (String command : args) {
                System.out.println(groot.getCommandResponse(command).isError());
            }
        }
    }

    /**
     * Creates saved tasks inside the child process's isolated working directory.
     */
    private Path writeSortFixture() throws Exception {
        Path dataDirectory = Files.createDirectories(temporaryDirectory.resolve("data"));
        return Files.writeString(dataDirectory.resolve("groot.txt"), SORT_DATA);
    }

    /**
     * Compares the list before failure, after rollback, and after reloading the original file.
     */
    private void assertFailedSavePreservesTasks(boolean isDone, String command) throws Exception {
        String output = runProcess(SaveFailureScenario.class, "", Boolean.toString(isDone), command);
        String expectedList = " Here are the tasks in your list:\n 1.[T][ ] first\n 2.[T]["
                + (isDone ? "X" : " ") + "] second\n 3.[T][ ] third";
        String expectedError = " Oops! I couldn't save your tasks. Your last change was not applied.";
        assertEquals(String.join("\n", expectedList, expectedError, expectedList, expectedList) + "\n", output);
    }

    /**
     * Runs a scenario with its own working directory so tests never touch the user's saved tasks.
     */
    private String runProcess(Class<?> entryPoint, String input, String... arguments) throws Exception {
        return IsolatedProcess.run(temporaryDirectory, entryPoint, input, arguments);
    }

    /**
     * Exercises the shared response API directly, including whitespace normally trimmed by the UI.
     */
    public static class CommandScenario {
        /**
         * Prints each command's response using tasks loaded from the isolated working directory.
         *
         * @param args Commands to process in order.
         * @throws Exception If saved tasks cannot be loaded.
         */
        public static void main(String[] args) throws Exception {
            Groot groot = new Groot();
            for (String command : args) {
                System.out.println(groot.getResponse(command));
            }
        }
    }

    /**
     * Blocks writes after loading tasks to prove that sorting does not depend on saving.
     */
    public static class SortWithoutSavingScenario {
        /**
         * Prints sorted and ordinary lists while the save destination is an invalid directory.
         *
         * @param args Unused command-line arguments.
         * @throws Exception If scenario setup or restoration fails.
         */
        public static void main(String[] args) throws Exception {
            Groot groot = new Groot();
            Path dataFile = Path.of("data", "groot.txt");
            Path backupFile = Path.of("data", "saved.txt");
            Files.move(dataFile, backupFile);
            Files.createDirectory(dataFile);
            try {
                System.out.println(groot.getResponse("sort"));
                System.out.println(groot.getResponse("sort -r"));
                System.out.println(groot.getResponse("list"));
            } finally {
                Files.delete(dataFile);
                Files.move(backupFile, dataFile);
            }
        }
    }

    /**
     * Makes the save destination a directory to trigger a deterministic write failure in a child process.
     */
    public static class SaveFailureScenario {
        /**
         * Prints task state around a failed command and reloads the preserved data file.
         *
         * @param args Initial status of the second task, followed by the command to fail.
         * @throws Exception If scenario setup or restoration fails.
         */
        public static void main(String[] args) throws Exception {
            Groot groot = new Groot();
            groot.getResponse("todo first");
            groot.getResponse("todo second");
            groot.getResponse("todo third");
            if (Boolean.parseBoolean(args[0])) {
                groot.getResponse("mark 2");
            }
            System.out.println(groot.getResponse("list"));

            Path dataFile = Path.of("data", "groot.txt");
            Path backupFile = Path.of("data", "saved.txt");
            Files.move(dataFile, backupFile);
            Files.createDirectory(dataFile);
            System.out.println(groot.getResponse(args[1]));
            System.out.println(groot.getResponse("list"));

            Files.delete(dataFile);
            Files.move(backupFile, dataFile);
            System.out.println(new Groot().getResponse("list"));
        }
    }
}
