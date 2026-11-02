package megia.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import megia.exception.ErrorCode;
import megia.exception.StorageException;
import megia.exception.TaskNotFoundException;
import megia.exception.UserInputException;
import megia.model.CommandResult;
import megia.model.Deadline;
import megia.model.Event;
import megia.model.TaskEntry;
import megia.model.TaskStorage;
import megia.model.Todo;

class CommandExecutorTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void execute_help_returnsHelpResult() throws Exception {
        TaskStorage taskStorage = new TaskStorage();
        Path storagePath = temporaryDirectory.resolve("tasks.csv");
        LocalStorageService localStorageService = new LocalStorageService(
                storagePath.toString());
        CommandExecutor commandExecutor = new CommandExecutor(
                new TaskService(taskStorage, localStorageService));

        assertInstanceOf(CommandResult.Help.class, commandExecutor.execute("help"));
        assertEquals(0, taskStorage.getTaskCount());
        assertTrue(Files.notExists(storagePath));
    }

    @Test
    void execute_addThenList_preservesOriginalTaskId() throws Exception {
        TaskStorage taskStorage = new TaskStorage();
        LocalStorageService localStorageService = new LocalStorageService(
                temporaryDirectory.resolve("tasks.csv").toString());
        CommandExecutor commandExecutor = new CommandExecutor(
                new TaskService(taskStorage, localStorageService));

        CommandResult addResult = commandExecutor.execute("todo borrow book");
        CommandResult.TaskMutation addMutation = assertInstanceOf(
                CommandResult.TaskMutation.class, addResult);
        assertEquals(1, addMutation.task().id());

        CommandResult listResult = commandExecutor.execute("list");
        CommandResult.TaskList taskList = assertInstanceOf(CommandResult.TaskList.class, listResult);
        TaskEntry taskEntry = taskList.entries().get(0);
        assertEquals(1, taskEntry.id());
        assertEquals("borrow book", taskEntry.task().getDescription());
    }

    @Test
    void execute_findAndDateList_returnsMatchingTasksWithOriginalIds() throws Exception {
        TaskStorage taskStorage = new TaskStorage();
        LocalStorageService localStorageService = new LocalStorageService(
                temporaryDirectory.resolve("tasks.csv").toString());
        CommandExecutor commandExecutor = new CommandExecutor(
                new TaskService(taskStorage, localStorageService));

        commandExecutor.execute("todo read book");
        commandExecutor.execute("deadline submit report /by 2026-09-03 1800");
        commandExecutor.execute("event conference /from 2026-09-02 1400 /to 2026-09-04 1600");

        CommandResult.TaskList findResult = assertInstanceOf(
                CommandResult.TaskList.class, commandExecutor.execute("find book"));
        assertEquals(CommandResult.QueryType.FIND, findResult.query().type());
        assertEquals(1, findResult.entries().size());
        assertEquals(1, findResult.entries().get(0).id());

        CommandResult.TaskList dateResult = assertInstanceOf(
                CommandResult.TaskList.class,
                commandExecutor.execute("list " + LocalDate.of(2026, 9, 3)));
        assertEquals(CommandResult.QueryType.DATE, dateResult.query().type());
        assertEquals(2, dateResult.entries().size());
        assertEquals(2, dateResult.entries().get(0).id());
        assertEquals(Deadline.class, dateResult.entries().get(0).task().getClass());
        assertEquals(3, dateResult.entries().get(1).id());
        assertEquals(Event.class, dateResult.entries().get(1).task().getClass());
    }

    @Test
    void execute_successfulMutations_persistAcrossServiceRestart() throws Exception {
        Path storagePath = temporaryDirectory.resolve("tasks.csv");
        LocalStorageService localStorageService = new LocalStorageService(storagePath.toString());
        TaskStorage taskStorage = new TaskStorage();
        CommandExecutor commandExecutor = new CommandExecutor(
                new TaskService(taskStorage, localStorageService));

        commandExecutor.execute("todo 阅读指南");
        commandExecutor.execute("deadline 提交报告 /by 2026-09-10 1800");
        commandExecutor.execute("event 参加会议 /from 2026-09-10 0900 /to 2026-09-11 1700");
        commandExecutor.execute("mark 1");
        commandExecutor.execute("unmark 1");
        commandExecutor.execute("delete 2");

        TaskStorage reloadedStorage = new LocalStorageService(storagePath.toString())
                .loadTaskData()
                .orElseThrow();
        CommandExecutor restartedExecutor = new CommandExecutor(
                new TaskService(reloadedStorage, new LocalStorageService(storagePath.toString())));
        CommandResult.TaskList listResult = assertInstanceOf(
                CommandResult.TaskList.class, restartedExecutor.execute("list"));

        assertEquals(List.of(1, 2), listResult.entries().stream().map(TaskEntry::id).toList());
        assertEquals(2, reloadedStorage.getTaskCount());
        assertFalse(reloadedStorage.getTaskEntries().get(0).task().isDone());
        assertEquals("阅读指南", reloadedStorage.getTaskEntries().get(0).task().getDescription());
        assertFalse(reloadedStorage.getTaskEntries().get(1).task().isDone());
        Event event = assertInstanceOf(Event.class, reloadedStorage.getTaskEntries().get(1).task());
        assertEquals("参加会议", event.getDescription());
        assertEquals(LocalDateTime.of(2026, 9, 10, 9, 0), event.getStartTime());
        assertEquals(LocalDateTime.of(2026, 9, 11, 17, 0), event.getEndTime());
    }

    @Test
    void execute_failedAdd_preservesTasksOrderingCompletionAndIds() throws Exception {
        assertFailedMutationPreservesState("todo another task");
    }

    @Test
    void execute_failedMark_preservesTasksOrderingCompletionAndIds() throws Exception {
        assertFailedMutationPreservesState("mark 1");
    }

    @Test
    void execute_failedMark_preservesCompletionStateForAlreadyDoneTask() throws Exception {
        assertFailedMutationPreservesState("mark 2");
    }

    @Test
    void execute_failedUnmark_preservesTasksOrderingCompletionAndIds() throws Exception {
        assertFailedMutationPreservesState("unmark 2");
    }

    @Test
    void execute_failedUnmark_preservesCompletionStateForIncompleteTask() throws Exception {
        assertFailedMutationPreservesState("unmark 1");
    }

    @Test
    void execute_failedDelete_preservesTasksOrderingCompletionAndIds() throws Exception {
        assertFailedMutationPreservesState("delete 2");
    }

    @Test
    void execute_eventMarkersOutOfOrder_reportsUserInputError() {
        LocalStorageService localStorageService = new LocalStorageService(
                temporaryDirectory.resolve("tasks.csv").toString());
        CommandExecutor commandExecutor = new CommandExecutor(
                new TaskService(new TaskStorage(), localStorageService));

        String command = "event meeting /to 2026-09-03 1600 /from 2026-09-03 1400";
        UserInputException exception = assertThrows(
                UserInputException.class, () -> commandExecutor.execute(command));

        assertEquals(ErrorCode.EVENT_MARKERS_OUT_OF_ORDER, exception.getErrorCode());
    }

    @Test
    void execute_missingParameters_rejectsWithoutChangingTaskData() throws Exception {
        Path storagePath = temporaryDirectory.resolve("tasks.csv");
        TaskStorage taskStorage = new TaskStorage();
        CommandExecutor commandExecutor = createCommandExecutor(taskStorage, storagePath);

        commandExecutor.execute("todo keep this task");
        String storedTasksBeforeRejections = Files.readString(storagePath);
        List<String> invalidCommands = List.of(
                "todo",
                "deadline report",
                "deadline /by 2024-02-29 1800",
                "deadline report /by",
                "event meeting /to 2024-02-29 1000",
                "event meeting /from 2024-02-29 0900",
                "event meeting /on 2024-02-29 /from /to 1000",
                "find",
                "mark",
                "delete");

        for (String invalidCommand : invalidCommands) {
            assertThrows(UserInputException.class, () -> commandExecutor.execute(invalidCommand));
            assertEquals(1, taskStorage.getTaskCount());
            assertEquals("keep this task", taskStorage.getTaskEntries().get(0).task().getDescription());
            assertEquals(storedTasksBeforeRejections, Files.readString(storagePath));
        }
    }

    @Test
    void execute_invalidTaskNumbers_rejectWithoutMutationAndValidNeighborsSucceed() throws Exception {
        Path storagePath = temporaryDirectory.resolve("tasks.csv");
        TaskStorage taskStorage = new TaskStorage();
        CommandExecutor commandExecutor = createCommandExecutor(taskStorage, storagePath);

        commandExecutor.execute("todo keep this task");
        String storedTasksBeforeRejections = Files.readString(storagePath);
        List<String> mutationCommands = List.of("mark", "unmark", "delete");
        List<String> malformedTaskIds = List.of("one", "0", "-1", "2147483648");

        for (String mutationCommand : mutationCommands) {
            for (String malformedTaskId : malformedTaskIds) {
                ErrorCode expectedErrorCode = malformedTaskId.equals("one")
                        ? ErrorCode.TASK_ID_NOT_INTEGER
                        : malformedTaskId.equals("2147483648")
                                ? ErrorCode.TASK_ID_TOO_LARGE
                                : ErrorCode.TASK_ID_NOT_POSITIVE;
                String malformedCommand = mutationCommand + " " + malformedTaskId;
                UserInputException exception = assertThrows(
                        UserInputException.class, () -> commandExecutor.execute(malformedCommand));
                assertEquals(expectedErrorCode, exception.getErrorCode());
                assertTaskDataUnchanged(taskStorage, storagePath, storedTasksBeforeRejections);
            }

            String outOfRangeCommand = mutationCommand + " 2";
            TaskNotFoundException exception = assertThrows(
                    TaskNotFoundException.class, () -> commandExecutor.execute(outOfRangeCommand));
            assertEquals(ErrorCode.TASK_NOT_FOUND, exception.getErrorCode());
            assertTaskDataUnchanged(taskStorage, storagePath, storedTasksBeforeRejections);
        }

        commandExecutor.execute("mark 1");
        assertTrue(taskStorage.getTaskEntries().get(0).task().isDone());
        commandExecutor.execute("unmark 1");
        assertFalse(taskStorage.getTaskEntries().get(0).task().isDone());
        commandExecutor.execute("delete 1");
        assertEquals(0, taskStorage.getTaskCount());
    }

    @Test
    void execute_existingWhitespaceAndBoundaryDates_acceptValidCommands() throws Exception {
        Path storagePath = temporaryDirectory.resolve("tasks.csv");
        TaskStorage taskStorage = new TaskStorage();
        CommandExecutor commandExecutor = createCommandExecutor(taskStorage, storagePath);

        commandExecutor.execute("  todo   buy milk  ");
        commandExecutor.execute("deadline   submit report   /by   29/2/2024 1800");
        commandExecutor.execute(
                "event   team meeting   /on   2024-02-29   /from   0900   /to   1000");

        assertEquals(3, taskStorage.getTaskCount());
        assertEquals("buy milk", taskStorage.getTaskEntries().get(0).task().getDescription());
        Deadline deadline = assertInstanceOf(
                Deadline.class, taskStorage.getTaskEntries().get(1).task());
        Event event = assertInstanceOf(Event.class, taskStorage.getTaskEntries().get(2).task());
        assertEquals(LocalDateTime.of(2024, 2, 29, 18, 0), deadline.getDeadline());
        assertEquals(LocalDateTime.of(2024, 2, 29, 9, 0), event.getStartTime());
        assertEquals(LocalDateTime.of(2024, 2, 29, 10, 0), event.getEndTime());
    }

    @Test
    void execute_taskDescriptionsWithLineBreaks_rejectBeforeMutation() throws Exception {
        Path storagePath = temporaryDirectory.resolve("tasks.csv");
        TaskStorage taskStorage = new TaskStorage();
        LocalStorageService localStorageService = new LocalStorageService(storagePath.toString());
        CommandExecutor commandExecutor = new CommandExecutor(
                new TaskService(taskStorage, localStorageService));
        String[] rawCommands = {
            "todo first line\nsecond line",
            "todo first line\rsecond line",
            "deadline first line\nsecond line /by 2026-09-10 1800",
            "deadline first line\rsecond line /by 2026-09-10 1800",
            "event first line\nsecond line /on 2026-09-10 /from 0900 /to 1000",
            "event first line\rsecond line /on 2026-09-10 /from 0900 /to 1000"
        };

        for (String rawCommand : rawCommands) {
            UserInputException exception = assertThrows(
                    UserInputException.class, () -> commandExecutor.execute(rawCommand));

            assertEquals(ErrorCode.DESCRIPTION_LINE_BREAK, exception.getErrorCode());
            assertEquals(0, taskStorage.getTaskCount());
        }
        assertTrue(Files.notExists(storagePath));
    }

    private void assertFailedMutationPreservesState(String command) throws Exception {
        String operation = command.substring(0, command.indexOf(' '));
        Path storagePath = temporaryDirectory.resolve(operation + "-failure.csv");
        TaskStorage taskStorage = createPersistedTaskStorage(storagePath);
        List<String> expectedTasks = encodeTasks(taskStorage);
        String expectedFile = Files.readString(storagePath);
        LocalStorageService failingStorageService = new LocalStorageService(
                storagePath.toString(), (path, output) -> {
                    throw new IOException("controlled write failure");
                });
        CommandExecutor commandExecutor = new CommandExecutor(
                new TaskService(taskStorage, failingStorageService));

        assertThrows(StorageException.class, () -> commandExecutor.execute(command));

        assertEquals(expectedTasks, encodeTasks(taskStorage));
        assertEquals(expectedFile, Files.readString(storagePath));
        CommandResult.TaskList listResult = assertInstanceOf(
                CommandResult.TaskList.class, commandExecutor.execute("list"));
        assertEquals(List.of(1, 2, 3), listResult.entries().stream()
                .map(TaskEntry::id)
                .toList());
    }

    private static TaskStorage createPersistedTaskStorage(Path storagePath) throws Exception {
        TaskStorage taskStorage = new TaskStorage();
        taskStorage.addTask(new Todo("阅读指南", false));
        taskStorage.addTask(new Deadline(
                "提交报告", true, LocalDateTime.of(2026, 9, 10, 18, 0)));
        taskStorage.addTask(new Event(
                "参加会议",
                false,
                LocalDateTime.of(2026, 9, 10, 9, 0),
                LocalDateTime.of(2026, 9, 11, 17, 0)));
        new LocalStorageService(storagePath.toString()).saveTaskData(taskStorage);
        return taskStorage;
    }

    private static List<String> encodeTasks(TaskStorage taskStorage) {
        return taskStorage.getTaskEntries().stream()
                .map(entry -> entry.task().encode())
                .toList();
    }

    private CommandExecutor createCommandExecutor(TaskStorage taskStorage, Path storagePath) {
        LocalStorageService localStorageService = new LocalStorageService(storagePath.toString());
        return new CommandExecutor(new TaskService(taskStorage, localStorageService));
    }

    private void assertTaskDataUnchanged(
            TaskStorage taskStorage, Path storagePath, String storedTasksBeforeRejections)
            throws Exception {
        assertEquals(1, taskStorage.getTaskCount());
        assertFalse(taskStorage.getTaskEntries().get(0).task().isDone());
        assertEquals("keep this task", taskStorage.getTaskEntries().get(0).task().getDescription());
        assertEquals(storedTasksBeforeRejections, Files.readString(storagePath));
    }
}
