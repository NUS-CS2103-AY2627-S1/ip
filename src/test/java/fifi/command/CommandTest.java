package fifi.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import fifi.Parser;
import fifi.Storage;
import fifi.TaskList;
import fifi.Ui;
import fifi.exception.InvalidDescriptionException;
import fifi.task.Deadline;
import fifi.task.Event;
import fifi.task.Task;
import fifi.task.ToDo;

/**
 * Tests command execution side effects on tasks, storage, and output.
 */
public class CommandTest {
    private final PrintStream originalOut = System.out;

    @TempDir
    private Path temporaryDirectory;

    @AfterEach
    public void restoreOutput() {
        System.setOut(originalOut);
    }

    @Test
    public void isExit_addTodoCommand_falseReturned() {
        Command command = new AddTodoCommand(new ToDo(false, "read book",
                LocalDateTime.of(2025, 1, 1, 9, 0), null));

        assertFalse(command.isExit());
    }

    @Test
    public void execute_addTodoCommand_taskAddedAndSaved() throws Exception {
        Path dataFile = temporaryDirectory.resolve("fifi.txt");
        TaskList tasks = new TaskList();
        Command command = new AddTodoCommand(new ToDo(false, "read book",
                LocalDateTime.of(2025, 1, 1, 9, 0), null));

        command.execute(tasks, new Ui(), new Storage(dataFile.toString()));

        assertEquals(1, tasks.size());
        assertEquals("T | 0 | read book | 2025-01-01T09:00:00 | -", Files.readString(dataFile));
    }

    @Test
    public void execute_addDeadlineCommand_deadlineAddedAndSaved() throws Exception {
        Path dataFile = temporaryDirectory.resolve("fifi.txt");
        TaskList tasks = new TaskList();
        Command command = new AddDeadlineCommand(new Deadline(false, "return book", LocalDate.of(2019, 12, 2),
                LocalDateTime.of(2019, 11, 30, 9, 0), null));

        command.execute(tasks, new Ui(), new Storage(dataFile.toString()));

        assertEquals("D | 0 | return book | 2019-12-02 | 2019-11-30T09:00:00 | -",
                Files.readString(dataFile));
    }

    @Test
    public void execute_addEventCommand_eventAddedAndSaved() throws Exception {
        Path dataFile = temporaryDirectory.resolve("fifi.txt");
        TaskList tasks = new TaskList();
        Command command = new AddEventCommand(new Event(false, "meeting", LocalDate.of(2019, 12, 2),
                LocalDate.of(2019, 12, 4), LocalDateTime.of(2019, 12, 1, 10, 0), null));

        command.execute(tasks, new Ui(), new Storage(dataFile.toString()));

        assertEquals("E | 0 | meeting | 2019-12-02 | 2019-12-04 | 2019-12-01T10:00:00 | -",
                Files.readString(dataFile));
    }

    @Test
    public void execute_parsedEventWithWhitespace_trimmedFieldsAddedAndSaved() throws Exception {
        Path dataFile = temporaryDirectory.resolve("fifi.txt");
        TaskList tasks = new TaskList();
        Command command = Parser.parse("event   team meeting   /from   2025-10-01   /to   2025-10-03   ");

        command.execute(tasks, new Ui(), new Storage(dataFile.toString()));

        assertEquals(1, tasks.size());
        Event event = (Event) tasks.get(0);
        assertEquals("team meeting", event.getDescription());
        assertEquals(LocalDate.of(2025, 10, 1), event.getStart());
        assertEquals(LocalDate.of(2025, 10, 3), event.getEnd());
        assertFalse(event.isMarked());
        assertTrue(Files.readString(dataFile)
                .startsWith("E | 0 | team meeting | 2025-10-01 | 2025-10-03 | "));
    }

    @Test
    public void execute_taskDescriptionsContainingCommandWords_fullDescriptionsSavedAfterInvalidInputs()
            throws Exception {
        Path dataFile = temporaryDirectory.resolve("fifi.txt");
        TaskList tasks = new TaskList();
        Ui ui = new Ui();
        Storage storage = new Storage(dataFile.toString());

        Parser.parse("todo todo deadline event").execute(tasks, ui, storage);
        assertThrows(InvalidDescriptionException.class, () -> Parser.parse("deadline deadline review"));
        Parser.parse("deadline deadline review /by 2025-10-15").execute(tasks, ui, storage);
        assertThrows(InvalidDescriptionException.class, () -> Parser.parse("event event planning /to 2025-10-03"));
        Parser.parse("event event planning /from 2025-10-01 /to 2025-10-03").execute(tasks, ui, storage);

        String savedTasks = Files.readString(dataFile);
        assertTrue(savedTasks.contains("T | 0 | todo deadline event | "));
        assertTrue(savedTasks.contains("D | 0 | deadline review | 2025-10-15 | "));
        assertTrue(savedTasks.contains("E | 0 | event planning | 2025-10-01 | 2025-10-03 | "));
    }

    @Test
    public void execute_listCommand_tasksPrinted() throws Exception {
        ByteArrayOutputStream output = replaceSystemOut();
        Command command = new ListCommand();

        command.execute(new TaskList(getSampleTasks()), new Ui(), getUnusedStorage());

        assertOutputContains(output, "Here are the tasks in your list:\n"
                + "1. [T][ ] read book\n"
                + "2. [D][ ] return book (by: Dec 02 2019)\n"
                + "3. [E][ ] project meeting (from: Dec 02 2019 to: Dec 04 2019)");
    }

    @Test
    public void execute_markCommand_taskMarkedAndSaved() throws Exception {
        Path dataFile = temporaryDirectory.resolve("fifi.txt");
        TaskList tasks = new TaskList(getSampleTasks());

        new MarkCommand(0).execute(tasks, new Ui(), new Storage(dataFile.toString()));

        assertTrue(tasks.get(0).isMarked());
        assertFalse(tasks.get(1).isMarked());
        assertTrue(firstSavedLine(dataFile).startsWith("T | 1 | read book | 2019-11-29T08:00:00 | "));
    }

    @Test
    public void execute_brokenMarkTask_assertionPreventsSavingAndSuccessResponse() throws Exception {
        ByteArrayOutputStream output = replaceSystemOut();
        Path dataFile = temporaryDirectory.resolve("fifi.txt");
        Storage storage = new Storage(dataFile.toString());
        TaskList tasks = new TaskList();
        tasks.add(new ToDo(false, "read book") {
            @Override
            public void mark() {
                // Simulates a task implementation that violates the completion contract.
            }

            @Override
            public Task copy() {
                // This deliberately immutable test task retains its broken behavior when staged.
                return this;
            }
        });
        storage.saveTasks(tasks);

        assertThrows(AssertionError.class, () -> new MarkCommand(0).execute(tasks, new Ui(), storage));
        assertTrue(Files.readString(dataFile).startsWith("T | 0 | read book | "));
        assertEquals("", output.toString(StandardCharsets.UTF_8));

        tasks.add(new ToDo(false, "working task"));
        new MarkCommand(1).execute(tasks, new Ui(), storage);
        assertTrue(tasks.get(1).isMarked());
        assertTrue(Files.readString(dataFile).contains("T | 1 | working task"));
    }

    @Test
    public void execute_unmarkCommand_taskUnmarkedAndSaved() throws Exception {
        Path dataFile = temporaryDirectory.resolve("fifi.txt");
        ArrayList<Task> markedTasks = new ArrayList<>();
        markedTasks.add(new ToDo(true, "read book", LocalDateTime.of(2019, 11, 29, 8, 0),
                LocalDateTime.of(2019, 12, 1, 17, 0)));
        markedTasks.add(new Deadline(false, "return book", LocalDate.of(2019, 12, 2),
                LocalDateTime.of(2019, 11, 30, 9, 0), null));
        TaskList tasks = new TaskList(markedTasks);

        new UnmarkCommand(0).execute(tasks, new Ui(), new Storage(dataFile.toString()));

        assertFalse(tasks.get(0).isMarked());
        assertFalse(tasks.get(1).isMarked());
        assertEquals("T | 0 | read book | 2019-11-29T08:00:00 | 2019-12-01T17:00:00",
                firstSavedLine(dataFile));
    }

    @Test
    public void execute_brokenUnmarkTask_assertionPreventsSavingAndSuccessResponse() throws Exception {
        ByteArrayOutputStream output = replaceSystemOut();
        Path dataFile = temporaryDirectory.resolve("fifi.txt");
        Storage storage = new Storage(dataFile.toString());
        TaskList tasks = new TaskList();
        tasks.add(new ToDo(true, "read book") {
            @Override
            public void unmark() {
                // Simulates a task implementation that violates the completion contract.
            }

            @Override
            public Task copy() {
                // This deliberately immutable test task retains its broken behavior when staged.
                return this;
            }
        });
        storage.saveTasks(tasks);

        assertThrows(AssertionError.class, () -> new UnmarkCommand(0).execute(tasks, new Ui(), storage));
        assertTrue(Files.readString(dataFile).startsWith("T | 1 | read book | "));
        assertEquals("", output.toString(StandardCharsets.UTF_8));

        tasks.add(new ToDo(true, "working task"));
        new UnmarkCommand(1).execute(tasks, new Ui(), storage);
        assertFalse(tasks.get(1).isMarked());
        assertTrue(Files.readString(dataFile).contains("T | 0 | working task"));
    }

    @Test
    public void execute_deleteCommand_taskDeletedAndSaved() throws Exception {
        Path dataFile = temporaryDirectory.resolve("fifi.txt");
        TaskList tasks = new TaskList(getSampleTasks());

        new DeleteCommand(1).execute(tasks, new Ui(), new Storage(dataFile.toString()));

        assertEquals(2, tasks.size());
        assertEquals(String.join(System.lineSeparator(),
                "T | 0 | read book | 2019-11-29T08:00:00 | 2019-12-01T17:00:00",
                "E | 0 | project meeting | 2019-12-02 | 2019-12-04 | 2019-12-01T10:00:00 | -"),
                Files.readString(dataFile));
    }

    @Test
    public void execute_showCommand_matchingTasksPrinted() throws Exception {
        ByteArrayOutputStream output = replaceSystemOut();
        Command command = new ShowCommand(LocalDate.of(2019, 12, 3));

        command.execute(new TaskList(getSampleTasks()), new Ui(), getUnusedStorage());

        assertOutputContains(output, "Here are the tasks occurring on your specified date:\n"
                + "1. [E][ ] project meeting (from: Dec 02 2019 to: Dec 04 2019)");
    }

    @Test
    public void execute_findCommand_matchingTasksPrinted() throws Exception {
        ByteArrayOutputStream output = replaceSystemOut();
        Command command = new FindCommand("book");

        command.execute(new TaskList(getSampleTasks()), new Ui(), getUnusedStorage());

        assertOutputContains(output, "Here are the matching tasks in your list:\n"
                + "1. [T][ ] read book\n"
                + "2. [D][ ] return book (by: Dec 02 2019)");
    }

    @Test
    public void execute_statisticsCommand_statusesPrinted() throws Exception {
        ByteArrayOutputStream output = replaceSystemOut();
        LocalDateTime now = LocalDateTime.of(2026, 9, 9, 15, 0);
        Clock clock = Clock.fixed(now.toInstant(ZoneOffset.UTC), ZoneOffset.UTC);
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(new ToDo(true, "done", now.minusDays(1), now));
        tasks.add(new Deadline(false, "late", LocalDate.of(2026, 9, 8), now.minusDays(1), null));
        tasks.add(new Event(false, "today", LocalDate.of(2026, 9, 9),
                LocalDate.of(2026, 9, 9), now.minusDays(1), null));

        new StatisticsCommand(null, clock).execute(new TaskList(tasks), new Ui(), getUnusedStorage());

        assertOutputContains(output, """
                Task statistics for all tasks:
                Total tasks: 3
                Completed tasks: 1
                Incomplete tasks: 2
                Completion rate: 33%""");
        assertOutputContains(output, "Overdue: 1");
        assertOutputContains(output, "Ongoing: 1");
        assertOutputContains(output, "Past: 0");
    }

    @Test
    public void execute_statisticsSince_exclusionReported() throws Exception {
        ByteArrayOutputStream output = replaceSystemOut();
        LocalDateTime now = LocalDateTime.of(2026, 9, 9, 15, 0);
        Clock clock = Clock.fixed(now.toInstant(ZoneOffset.UTC), ZoneOffset.UTC);
        TaskList tasks = new TaskList();
        tasks.add(new ToDo(false, "legacy", null, null));

        new StatisticsCommand(LocalDate.of(2026, 9, 2), clock)
                .execute(tasks, new Ui(), getUnusedStorage());

        assertOutputContains(output, "Task statistics for tasks created since Sep 02 2026:");
        assertOutputContains(output, "Total tasks: 0");
        assertOutputContains(output, "Excluded legacy tasks with unknown creation times: 1");
    }

    @Test
    public void execute_statisticsSinceFutureDate_exceptionThrown() {
        LocalDateTime now = LocalDateTime.of(2026, 9, 9, 15, 0);
        Clock clock = Clock.fixed(now.toInstant(ZoneOffset.UTC), ZoneOffset.UTC);
        StatisticsCommand command = new StatisticsCommand(LocalDate.of(2026, 9, 10), clock);

        InvalidDescriptionException exception = assertThrows(InvalidDescriptionException.class, () ->
                command.execute(new TaskList(), new Ui(), getUnusedStorage()));

        assertEquals("UhOh! You entered a date that is in the future. "
                + "Please enter today or an earlier date.", exception.getMessage());
    }

    @Test
    public void execute_exitCommand_messagePrintedAndExitTrue() throws Exception {
        ByteArrayOutputStream output = replaceSystemOut();
        Command command = new ExitCommand();

        command.execute(new TaskList(), new Ui(), getUnusedStorage());

        assertTrue(command.isExit());
        assertOutputContains(output, "BaiBai! Hope to see you soon ^^");
    }

    @Test
    public void execute_failedSave_preservesTasksTimestampsAndOutputThenAllowsRetry() throws Exception {
        ByteArrayOutputStream output = replaceSystemOut();
        Path dataFile = temporaryDirectory.resolve("fifi.txt");
        Storage workingStorage = new Storage(dataFile.toString());
        Storage failingStorage = new Storage(dataFile.toString()) {
            @Override
            protected void replaceSavedFile(Path temporaryFile) throws IOException {
                throw new IOException("Simulated denied file replacement");
            }
        };
        String[] commands = {"todo new task", "deadline new task /by 2026-09-20",
            "event new task /from 2026-09-20 /to 2026-09-21", "mark 1", "unmark 1", "delete 1"};
        for (String input : commands) {
            ArrayList<Task> originals = getSampleTasks();
            if (input.equals("unmark 1")) {
                originals.get(0).mark();
            }
            TaskList tasks = new TaskList(originals);
            workingStorage.saveTasks(tasks);
            String originalData = Files.readString(dataFile);
            String originalDisplay = tasks.toDisplayString();
            Task firstTask = tasks.get(0);
            output.reset();
            Command command = Parser.parse(input);

            assertThrows(IOException.class, () -> command.execute(tasks, new Ui(), failingStorage), input);
            assertEquals(originalData, Files.readString(dataFile));
            assertEquals(originalDisplay, tasks.toDisplayString());
            assertEquals(firstTask, tasks.get(0));
            assertEquals(originalData, String.join(System.lineSeparator(),
                    tasks.asList().stream().map(Task::toFileString).toList()));
            assertEquals("", output.toString(StandardCharsets.UTF_8));
            try (var files = Files.list(temporaryDirectory)) {
                assertEquals(1, files.count());
            }

            command.execute(tasks, new Ui(), workingStorage);
            assertFalse(originalData.equals(Files.readString(dataFile)), input);
            assertFalse(output.toString(StandardCharsets.UTF_8).isBlank());
        }
    }

    private Storage getUnusedStorage() {
        return new Storage(temporaryDirectory.resolve("unused.txt").toString());
    }

    private String firstSavedLine(Path dataFile) throws Exception {
        return Files.readString(dataFile).split("\\R", 2)[0];
    }

    private void assertOutputContains(ByteArrayOutputStream output, String expectedText) {
        assertTrue(normalizeLineEndings(output.toString(StandardCharsets.UTF_8))
                .contains(normalizeLineEndings(expectedText)));
    }

    private ByteArrayOutputStream replaceSystemOut() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));
        return output;
    }

    private ArrayList<Task> getSampleTasks() {
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(new ToDo(false, "read book", LocalDateTime.of(2019, 11, 29, 8, 0),
                LocalDateTime.of(2019, 12, 1, 17, 0)));
        tasks.add(new Deadline(false, "return book", LocalDate.of(2019, 12, 2),
                LocalDateTime.of(2019, 11, 30, 9, 0), null));
        tasks.add(new Event(false, "project meeting", LocalDate.of(2019, 12, 2),
                LocalDate.of(2019, 12, 4), LocalDateTime.of(2019, 12, 1, 10, 0), null));
        return tasks;
    }

    private String normalizeLineEndings(String text) {
        return text.replace("\r\n", "\n");
    }
}
