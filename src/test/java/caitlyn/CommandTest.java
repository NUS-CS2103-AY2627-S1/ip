package caitlyn;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests command validation, persistence, and rollback when saving fails. */
class CommandTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    void execute_addCommands_persistsAllTaskTypes() throws IOException, CaitlynException {
        TaskStorage storage = new TaskStorage(temporaryDirectory.resolve("tasks.txt"));
        List<Task> tasks = new ArrayList<>();
        List<String> messages = new ArrayList<>();
        Ui ui = new Ui(messages::add);

        new TodoCommand("read book", storage).execute(tasks, ui);
        new DeadlineCommand("return book /by 2025-03-14", storage).execute(tasks, ui);
        new EventCommand("meeting /from 2025-03-15 /to 2025-03-16", storage).execute(tasks, ui);

        assertEquals(List.of(
                "T | 0 | read book",
                "D | 0 | return book | 2025-03-14",
                "E | 0 | meeting | 2025-03-15 | 2025-03-16"),
                storage.load().stream().map(Task::toStorageString).toList());
        assertEquals(3, tasks.size());
        assertEquals("     Now you have 3 tasks in the list.", messages.getLast());
    }

    @Test
    void execute_markAndUnmark_persistsEachStatus() throws IOException, CaitlynException {
        TaskStorage storage = new TaskStorage(temporaryDirectory.resolve("tasks.txt"));
        Task task = new Todo("read book");
        List<Task> tasks = new ArrayList<>(List.of(task));
        List<String> messages = new ArrayList<>();
        Ui ui = new Ui(messages::add);

        new MarkCommand("mark 1", true, storage).execute(tasks, ui);

        assertTrue(task.isDone());
        assertTrue(storage.load().getFirst().isDone());
        assertEquals("       [T][X] read book", messages.getLast());

        new MarkCommand("unmark 1", false, storage).execute(tasks, ui);

        assertFalse(task.isDone());
        assertFalse(storage.load().getFirst().isDone());
        assertEquals("       [T][ ] read book", messages.getLast());
    }

    @Test
    void execute_deleteMiddleTask_preservesRemainingOrder() throws IOException, CaitlynException {
        TaskStorage storage = new TaskStorage(temporaryDirectory.resolve("tasks.txt"));
        Task firstTask = new Todo("first");
        Task lastTask = new Todo("last");
        List<Task> tasks = new ArrayList<>(List.of(firstTask, new Todo("middle"), lastTask));
        List<String> messages = new ArrayList<>();

        new DeleteCommand("delete 2", storage).execute(tasks, new Ui(messages::add));

        assertEquals(List.of(firstTask, lastTask), tasks);
        assertEquals(List.of("first", "last"), storage.load().stream().map(Task::getDescription).toList());
        assertEquals("     Now you have 2 tasks in the list.", messages.getLast());
    }

    @Test
    void execute_addSaveFailure_restoresOriginalList() throws IOException {
        TaskStorage storage = createFailingStorage();
        Task originalTask = new Todo("keep this");
        List<Task> tasks = new ArrayList<>(List.of(originalTask));
        List<Command> commands = List.of(
                new TodoCommand("read book", storage),
                new DeadlineCommand("return book /by 2025-03-14", storage),
                new EventCommand("meeting /from 2025-03-15 /to 2025-03-16", storage));

        for (Command command : commands) {
            assertSaveFailsWithoutConfirmation(command, tasks);
            assertEquals(List.of(originalTask), tasks);
        }
    }

    @Test
    void execute_statusSaveFailure_restoresPreviousStatus() throws IOException {
        TaskStorage storage = createFailingStorage();
        for (boolean wasDone : new boolean[] {false, true}) {
            for (boolean isMarkingDone : new boolean[] {false, true}) {
                Task task = new Todo("keep this");
                if (wasDone) {
                    task.markAsDone();
                }
                List<Task> tasks = new ArrayList<>(List.of(task));
                String commandText = isMarkingDone ? "mark 1" : "unmark 1";

                assertSaveFailsWithoutConfirmation(new MarkCommand(commandText, isMarkingDone, storage), tasks);

                assertEquals(wasDone, task.isDone());
                assertEquals(List.of(task), tasks);
            }
        }
    }

    @Test
    void execute_deleteSaveFailure_restoresTaskAtOriginalPosition() throws IOException {
        TaskStorage storage = createFailingStorage();
        Task removedTask = new Todo("middle");
        removedTask.markAsDone();
        List<Task> originalTasks = List.of(new Todo("first"), removedTask, new Todo("last"));
        List<Task> tasks = new ArrayList<>(originalTasks);

        assertSaveFailsWithoutConfirmation(new DeleteCommand("delete 2", storage), tasks);

        assertEquals(originalTasks, tasks);
        assertTrue(tasks.get(1).isDone());
    }

    @Test
    void taskSelection_rejectsMissingTaskNumber() {
        assertInvalidTaskSelection(new MarkCommand("mark", true),
                "I beg your pardon, master. Please provide a task number, for example: mark 2.");
        assertInvalidTaskSelection(new DeleteCommand("delete"),
                "I beg your pardon, master. Please provide a task number, for example: delete 2.");
    }

    @Test
    void taskSelection_rejectsNonNumericTaskNumber() {
        assertInvalidTaskSelection(new MarkCommand("mark one", true),
                "I beg your pardon, master. Please provide a valid task number, for example: mark 2.");
        assertInvalidTaskSelection(new DeleteCommand("delete one"),
                "I beg your pardon, master. Please provide a valid task number, for example: delete 2.");
    }

    @Test
    void taskSelection_rejectsTaskNumberOutsideList() {
        CaitlynException exception = assertThrows(CaitlynException.class, () ->
                new MarkCommand("mark 2", true)
                        .execute(List.of(new Todo("task")), new Ui(message -> { })));

        assertEquals("I beg your pardon, master, but I could not find task 2.",
                exception.getMessage());
    }

    @Test
    void execute_invalidAddArguments_preservesTasksAndSavedData() throws IOException {
        TaskStorage storage = new TaskStorage(temporaryDirectory.resolve("tasks.txt"));
        Task original = new Todo("keep this");
        original.markAsDone();
        List<Task> tasks = new ArrayList<>(List.of(original));
        storage.save(tasks);
        for (String input : List.of("todo", "deadline", "deadline /by 2027-01-15",
                "deadline task /by", "deadline task /by tomorrow", "event", "event task /from 2027-01-15",
                "event task /to 2027-01-25 /from 2027-01-15", "event /from 2027-01-15 /to 2027-01-25",
                "event task /from /to 2027-01-25", "event task /from 2027-01-15 /to",
                "event task /from tomorrow /to 2027-01-25", "event task /from 2027-01-15 /to tomorrow",
                "event reversed /from 2026-12-03 1600 /to 2026-12-03 1500",
                "event reversed /from 2026-12-04 /to 2026-12-03",
                "event reversed /from 2026-09-21T18:00:45 /to 2026-09-21T18:00:30",
                "event task /from2026-12-03 /to2026-12-04", "deadline task /by2026-12-03",
                "deadline task/by 2026-12-03", "deadline task /by /by 2026-12-03",
                "event task/from 2026-12-03 /to 2026-12-04",
                "event task /from 2026-12-03 /from 2026-12-04 /to 2026-12-05",
                "event task /from 2026-12-03 /to 2026-12-04 /to 2026-12-05")) {
            List<String> messages = new ArrayList<>();
            assertThrows(CaitlynException.class, () -> Parser.parse(input, storage).execute(tasks,
                    new Ui(messages::add)), input);
            assertEquals(List.of(original), tasks, input);
            assertTrue(original.isDone(), input);
            assertEquals(List.of("T | 1 | keep this"),
                    Files.readAllLines(temporaryDirectory.resolve("tasks.txt")), input);
            assertTrue(messages.isEmpty(), input);
        }
    }

    @Test
    void execute_dateMarkersAndWhitespace_preservesDescriptionsAndPrecision() throws Exception {
        TaskStorage storage = new TaskStorage(temporaryDirectory.resolve("tasks.txt"));
        List<Task> tasks = new ArrayList<>();
        List<String> messages = new ArrayList<>();
        Ui ui = new Ui(messages::add);
        Parser.parse("deadline\tread /bytecode\t/by\t2026-09-21  1800", storage).execute(tasks, ui);
        Parser.parse("event\tinspect /fromage and /tools\t/from\t2026-09-21\t1800 /to 2026-09-21 1900",
                storage).execute(tasks, ui);
        Parser.parse("event short /from 2026-09-21T18:00:30 /to 2026-09-21T18:00:45", storage)
                .execute(tasks, ui);
        assertEquals("read /bytecode", tasks.get(0).getDescription());
        assertEquals("inspect /fromage and /tools", tasks.get(1).getDescription());
        assertEquals("[E][ ] short (from: Sep 21 2026 6:00:30 PM to: Sep 21 2026 6:00:45 PM)",
                tasks.get(2).toString());
        assertEquals(tasks.stream().map(Task::toString).toList(),
                storage.load().stream().map(Task::toString).toList());

        Parser.parse("mark\t3", storage).execute(tasks, ui);
        assertTrue(tasks.get(2).isDone());
        Parser.parse("unmark\t3", storage).execute(tasks, ui);
        assertFalse(tasks.get(2).isDone());
        Parser.parse("find\tshort", storage).execute(tasks, ui);
        assertTrue(messages.getLast().contains("3.[E][ ] short"));
        Parser.parse("delete\t3", storage).execute(tasks, ui);
        assertEquals(2, storage.load().size());
    }

    @Test
    void execute_reversedEvent_explainsRangeError() {
        assertEquals("I beg your pardon, master. The event's start must not be after its end.",
                assertThrows(CaitlynException.class, () -> new EventCommand(
                        "reversed /from 2026-12-03 1600 /to 2026-12-03 1500")
                        .execute(new ArrayList<>(), new Ui(message -> { }))).getMessage());
    }

    @Test
    void execute_blankFieldsInDirectCommands_reportsMissingFields() {
        List<Command> commands = List.of(new DeadlineCommand("  /by 2027-01-15"),
                new DeadlineCommand("task /by "), new EventCommand("  /from 2027-01-15 /to 2027-01-25"),
                new EventCommand("task /from /to 2027-01-25"), new EventCommand("task /from 2027-01-15 /to"));
        for (Command command : commands) {
            String expected = command instanceof DeadlineCommand
                    ? "I beg your pardon, master. Please provide both a task description and a deadline."
                    : "I beg your pardon, master. Please provide a description, start time, "
                            + "and end time for the event.";
            assertEquals(expected, assertThrows(CaitlynException.class, () ->
                    command.execute(new ArrayList<>(), new Ui(message -> { }))).getMessage());
        }
    }

    @Test
    void execute_invalidTaskNumbers_rejectsWithoutMutationOrConfirmation() throws IOException {
        TaskStorage storage = new TaskStorage(temporaryDirectory.resolve("tasks.txt"));
        Task original = new Todo("keep");
        original.markAsDone();
        List<Task> tasks = new ArrayList<>(List.of(original));
        storage.save(tasks);
        for (String name : List.of("mark", "unmark", "delete")) {
            for (String argument : List.of("", "0", "-1", "2", "2147483648", "1.0", "one", "1 extra")) {
                String input = name + " " + argument;
                List<String> messages = new ArrayList<>();
                assertThrows(CaitlynException.class, () -> Parser.parse(input, storage)
                        .execute(tasks, new Ui(messages::add)), input);
                assertEquals(List.of(original), tasks, input);
                assertTrue(original.isDone(), input);
                assertTrue(messages.isEmpty(), input);
                assertEquals(List.of("T | 1 | keep"),
                        Files.readAllLines(temporaryDirectory.resolve("tasks.txt")), input);
            }
            assertThrows(CaitlynException.class, () -> Parser.parse(name + " 1", storage)
                    .execute(new ArrayList<>(), new Ui(message -> { })), name);
        }
    }

    @Test
    void execute_repeatedStatusAndDeletingLastTask_persistsExpectedState() throws Exception {
        TaskStorage storage = new TaskStorage(temporaryDirectory.resolve("tasks.txt"));
        List<Task> tasks = new ArrayList<>(List.of(new Todo("keep")));
        List<String> messages = new ArrayList<>();
        Ui ui = new Ui(messages::add);
        for (String input : List.of("mark 1", "mark 1", "unmark 1", "unmark 1")) {
            Parser.parse(input, storage).execute(tasks, ui);
            assertEquals(input.startsWith("mark"), storage.load().getFirst().isDone());
        }
        Parser.parse("delete 1", storage).execute(tasks, ui);
        assertTrue(tasks.isEmpty());
        assertEquals("", Files.readString(temporaryDirectory.resolve("tasks.txt")));
        assertEquals("     Now you have 0 tasks in the list.", messages.getLast());
    }

    @Test
    void commandFlags_everyCommand_declaresExitAndMutationBehavior() {
        for (String name : List.of("todo", "deadline", "event", "within", "mark", "unmark", "delete",
                "list", "find", "unknown", "bye")) {
            Command command = Parser.parse(name);
            assertEquals(name.equals("bye"), command.isExit(), name);
            assertEquals(List.of("list", "find", "unknown", "bye").contains(name), command.isReadOnly(), name);
        }
    }

    @Test
    void execute_defaultAddConstructors_validateBeforeSaving() {
        for (Command command : List.of(new TodoCommand(""), new WithinCommand(""))) {
            List<Task> tasks = new ArrayList<>();
            List<String> messages = new ArrayList<>();
            assertThrows(CaitlynException.class, () -> command.execute(tasks, new Ui(messages::add)));
            assertTrue(tasks.isEmpty());
            assertTrue(messages.isEmpty());
        }
    }

    /** Executes a command with an invalid task selection and checks its error message. */
    private void assertInvalidTaskSelection(Command command, String expectedMessage) {
        CaitlynException exception = assertThrows(CaitlynException.class, () ->
                command.execute(List.of(new Todo("task")), new Ui(message -> { })));

        assertEquals(expectedMessage, exception.getMessage());
    }

    /** Creates a deterministic save failure by placing a file where a directory is required. */
    private TaskStorage createFailingStorage() throws IOException {
        Path blockedDirectory = temporaryDirectory.resolve("blocked");
        Files.writeString(blockedDirectory, "existing file");
        return new TaskStorage(blockedDirectory.resolve("tasks.txt"));
    }

    /** Verifies that failed saves report the storage error without announcing success. */
    private void assertSaveFailsWithoutConfirmation(Command command, List<Task> tasks) {
        List<String> messages = new ArrayList<>();

        CaitlynException exception = assertThrows(CaitlynException.class, () ->
                command.execute(tasks, new Ui(messages::add)));

        assertEquals("I beg your pardon, master. I could not save your tasks to disk.", exception.getMessage());
        assertTrue(messages.isEmpty());
    }
}
