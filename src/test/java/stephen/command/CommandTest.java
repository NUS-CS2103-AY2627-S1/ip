package stephen.command;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import stephen.exception.ChatbotException;
import stephen.parser.Parser;
import stephen.storage.Storage;
import stephen.task.Task;
import stephen.task.TaskList;
import stephen.ui.Ui;

/** Automated checks for command parsing, execution, and exit signaling. */
public class CommandTest {
    /**
     * Runs the command checks without requiring an external test library.
     *
     * @param args command-line arguments; not used
     * @throws Exception if setup, execution, or persistence unexpectedly fails
     */
    public static void main(String... args) throws Exception {
        Path dataFile = prepareDataFile();
        PrintStream originalOutput = System.out;
        ByteArrayOutputStream capturedOutput = new ByteArrayOutputStream();
        System.setOut(new PrintStream(capturedOutput, true, StandardCharsets.UTF_8));
        try {
            runCommandChecks(dataFile);
        } finally {
            System.setOut(originalOutput);
        }

        System.out.println("CommandTest: all checks passed");
    }

    /** Prepares an empty data file location for command persistence checks. */
    private static Path prepareDataFile() throws Exception {
        Path testRoot = Path.of("out", "command-test");
        Path dataFile = testRoot.resolve("tasks.txt");
        Files.createDirectories(testRoot);
        Files.deleteIfExists(dataFile);
        return dataFile;
    }

    /** Creates the shared test objects and runs each command behavior group. */
    private static void runCommandChecks(Path dataFile) throws Exception {
        Parser parser = new Parser();
        TaskList tasks = new TaskList(List.of());
        Storage storage = new Storage(dataFile);
        Ui ui = new Ui();

        checkAddCommands(parser, tasks, ui, storage);
        checkUpdateCommands(parser, tasks, ui, storage);
        checkSortCommand(parser, tasks, ui, storage);
        checkReadOnlyAndExitCommands(parser, tasks);
        checkParseErrors(parser, tasks);
        checkSavedTasks(dataFile, tasks);
    }

    /** Checks parsing and execution of commands that add each task type. */
    private static void checkAddCommands(Parser parser, TaskList tasks, Ui ui,
            Storage storage) throws Exception {
        executeAndCheckType(parser, "todo read book", tasks, ui, storage, AddCommand.class);
        executeAndCheckType(parser, "deadline submit report /by 2019-12-02",
                tasks, ui, storage, AddCommand.class);
        executeAndCheckType(parser, "event project meeting /from 2019-12-02 /to 2019-12-03",
                tasks, ui, storage, AddCommand.class);
        assertTaskData(tasks, List.of(
                "T | 0 | read book",
                "D | 0 | submit report | 2019-12-02",
                "E | 0 | project meeting | 2019-12-02 | 2019-12-03"));
    }

    /** Checks task status changes and deletion. */
    private static void checkUpdateCommands(Parser parser, TaskList tasks, Ui ui,
            Storage storage) throws Exception {
        executeAndCheckType(parser, "mark 1", tasks, ui, storage, MarkCommand.class);
        assertTaskData(tasks, List.of(
                "T | 1 | read book",
                "D | 0 | submit report | 2019-12-02",
                "E | 0 | project meeting | 2019-12-02 | 2019-12-03"));
        executeAndCheckType(parser, "unmark 1", tasks, ui, storage, UnmarkCommand.class);
        executeAndCheckType(parser, "delete 2", tasks, ui, storage, DeleteCommand.class);
        assertTaskData(tasks, List.of(
                "T | 0 | read book",
                "E | 0 | project meeting | 2019-12-02 | 2019-12-03"));
    }

    /** Checks sorting and the resulting stable task order. */
    private static void checkSortCommand(Parser parser, TaskList tasks, Ui ui,
            Storage storage) throws Exception {
        executeAndCheckType(parser, "sort", tasks, ui, storage, SortCommand.class);
        assertTaskData(tasks, List.of(
                "E | 0 | project meeting | 2019-12-02 | 2019-12-03",
                "T | 0 | read book"));
    }

    /** Checks read-only command types and exit signaling. */
    private static void checkReadOnlyAndExitCommands(Parser parser, TaskList tasks)
            throws Exception {
        assertCommandType(parser.parse("list", tasks), ListCommand.class);
        assertCommandType(parser.parse("schedule 2019-12-02", tasks), ScheduleCommand.class);
        Command exit = parser.parse("bye", tasks);
        assertCommandType(exit, ExitCommand.class);
        if (!exit.isExit()) {
            throw new AssertionError("ExitCommand must signal application exit");
        }
        if (parser.parse("list", tasks).isExit()) {
            throw new AssertionError("Non-exit commands must not signal application exit");
        }
    }

    /** Checks representative parser failures and their user-facing messages. */
    private static void checkParseErrors(Parser parser, TaskList tasks) throws Exception {
        assertParseError(parser, tasks, "mark 99", "That task number does not exist.");
        assertParseError(parser, tasks, "unknown", "I don't recognise that command.");
    }

    /** Checks that the stored records match the final in-memory task list. */
    private static void checkSavedTasks(Path dataFile, TaskList tasks) throws Exception {
        List<String> savedLines = Files.readAllLines(dataFile, StandardCharsets.UTF_8);
        List<String> expectedLines = tasks.getTasks().stream().map(Task::toDataString).toList();
        if (!savedLines.equals(expectedLines)) {
            throw new AssertionError("Expected saved tasks " + expectedLines
                    + " but was " + savedLines);
        }
    }

    /**
     * Parses, type-checks, and executes one command.
     *
     * @param parser parser used to interpret the input
     * @param input command text to parse
     * @param tasks task list on which to execute the command
     * @param ui console UI supplied to the command
     * @param storage persistence service supplied to the command
     * @param expectedType expected concrete command type
     * @throws Exception if parsing or execution fails
     */
    private static void executeAndCheckType(Parser parser, String input, TaskList tasks,
            Ui ui, Storage storage, Class<? extends Command> expectedType) throws Exception {
        Command command = parser.parse(input, tasks);
        assertCommandType(command, expectedType);
        command.execute(tasks, ui, storage);
    }

    /**
     * Checks that a parsed command has the expected concrete type.
     *
     * @param command parsed command to inspect
     * @param expectedType expected concrete command type
     */
    private static void assertCommandType(Command command,
            Class<? extends Command> expectedType) {
        if (!expectedType.isInstance(command)) {
            throw new AssertionError("Expected " + expectedType.getSimpleName()
                    + " but was " + command.getClass().getSimpleName());
        }
    }

    /**
     * Checks the exact error produced by invalid input.
     *
     * @param parser parser used to interpret the input
     * @param tasks task list used during validation
     * @param input invalid command text
     * @param expectedMessage expected validation message
     * @throws Exception if parsing unexpectedly succeeds or returns the wrong error
     */
    private static void assertParseError(Parser parser, TaskList tasks, String input,
            String expectedMessage) throws Exception {
        try {
            parser.parse(input, tasks);
            throw new AssertionError("Expected parsing to fail for: " + input);
        } catch (ChatbotException e) {
            if (!e.getMessage().equals(expectedMessage)) {
                throw new AssertionError("Expected error '" + expectedMessage
                        + "' but was '" + e.getMessage() + "'");
            }
        }
    }

    /**
     * Checks tasks through their canonical storage representation.
     *
     * @param tasks task list to inspect
     * @param expected expected serialized task records
     */
    private static void assertTaskData(TaskList tasks, List<String> expected) {
        List<String> actual = tasks.getTasks().stream().map(Task::toDataString).toList();
        if (!actual.equals(expected)) {
            throw new AssertionError("Expected " + expected + " but was " + actual);
        }
    }
}
