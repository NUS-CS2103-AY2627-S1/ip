package luckynoslacky.luckytask;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import luckynoslacky.luckyexception.LuckyNoStorageException;
import luckynoslacky.luckyexception.LuckyNoTaskLimitException;
import luckynoslacky.luckyresponse.LuckyNoTaskResponses;
import luckynoslacky.luckystorage.CsvSaver;

/** Tests persistence, capacity, and validation behavior provided by {@link TaskMaster}. */
class TaskMasterPersistenceTest {
    private static final LocalDateTime DEADLINE =
            LocalDateTime.of(2026, 12, 6, 23, 59);

    @TempDir
    Path temporaryDirectory;

    /** Verifies that task-type lookup exposes only the requested category. */
    @Test
    void getTaskType_existingTask_returnsTaskCategory() {
        TaskMaster taskMaster = createTaskMaster();
        taskMaster.loadTasksFromCsvStorageRecord(List.of(
                new TodoTask("read book"), new DeadlineTask("return book", DEADLINE),
                new EventTask("project meeting",
                        LocalDateTime.of(2026, 8, 6, 14, 0),
                        LocalDateTime.of(2026, 8, 6, 16, 0))));

        assertEquals(Task.TaskType.TODO, taskMaster.getTaskType(1));
        assertEquals(Task.TaskType.DEADLINE, taskMaster.getTaskType(2));
        assertEquals(Task.TaskType.EVENT, taskMaster.getTaskType(3));
    }

    /** Verifies that the configured task capacity is enforced. */
    @Test
    void addTask_customCapacity_throwsWhenFull() {
        TaskMaster taskMaster = createTaskMaster(2);

        taskMaster.addTask(new TodoTask("first"));
        taskMaster.addTask(new TodoTask("second"));

        LuckyNoTaskLimitException exception = assertThrows(
                LuckyNoTaskLimitException.class, () ->
                taskMaster.addTask(new TodoTask("third")));
        assertEquals("The task list is full.", exception.getMessage());
        assertEquals(2, taskMaster.getTaskCount());
    }

    /** Verifies small capacities reject the first task beyond their limit. */
    @Test
    void addTask_smallCapacities_preserveStateAfterOverflow() {
        for (int capacity : new int[] {1, 2, 3}) {
            TaskMaster taskMaster = createTaskMaster(capacity);
            for (int taskNumber = 1; taskNumber <= capacity; taskNumber++) {
                taskMaster.addTask(new TodoTask("task " + taskNumber));
            }

            LuckyNoTaskLimitException exception = assertThrows(
                    LuckyNoTaskLimitException.class, () ->
                    taskMaster.addTask(new TodoTask("overflow task")));

            assertEquals("The task list is full.", exception.getMessage());
            assertEquals(capacity, taskMaster.getTaskCount());
        }
    }

    /** Verifies that non-positive capacities are rejected. */
    @Test
    void taskMaster_nonPositiveCapacity_throwsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, () -> new TaskMaster(0));
        assertEquals("Maximum tasks must be positive.", exception.getMessage());
        exception = assertThrows(
                IllegalArgumentException.class, () -> new TaskMaster(-1));
        assertEquals("Maximum tasks must be positive.", exception.getMessage());
    }

    /** Verifies that a failed save rolls back an added task. */
    @Test
    void addTask_saveFailure_removesTask() {
        TaskMaster taskMaster = new TaskMaster(100, new FailingSaver());

        LuckyNoStorageException exception = assertThrows(
                LuckyNoStorageException.class, () ->
                taskMaster.addTask(new TodoTask("read book")));
        assertEquals("simulated save failure", exception.getMessage());
        assertEquals(0, taskMaster.getTaskCount());
    }

    /** Verifies that a failed save rolls back a status change. */
    @Test
    void markTaskDone_saveFailure_restoresPreviousStatus() {
        TaskMaster taskMaster = new TaskMaster(100, new FailingSaver());
        TodoTask task = new TodoTask("read book");
        taskMaster.loadTasksFromCsvStorageRecord(List.of(task));

        LuckyNoStorageException exception = assertThrows(
                LuckyNoStorageException.class, () -> taskMaster.markTaskDone(1));
        assertEquals("simulated save failure", exception.getMessage());
        assertFalse(task.isDone());
    }

    /** Verifies that a failed save restores a completed task's status. */
    @Test
    void unmarkTaskUndone_saveFailure_restoresPreviousStatus() {
        TaskMaster taskMaster = new TaskMaster(100, new FailingSaver());
        TodoTask task = new TodoTask("read book");
        task.markAsDone();
        taskMaster.loadTasksFromCsvStorageRecord(List.of(task));

        LuckyNoStorageException exception = assertThrows(
                LuckyNoStorageException.class, () -> taskMaster.unmarkTaskUndone(1));
        assertEquals("simulated save failure", exception.getMessage());
        assertTrue(task.isDone());
    }

    /** Verifies that a failed save restores a deleted task. */
    @Test
    void deleteTask_saveFailure_restoresDeletedTask() {
        TaskMaster taskMaster = new TaskMaster(100, new FailingSaver());
        TodoTask task = new TodoTask("read book");
        taskMaster.loadTasksFromCsvStorageRecord(List.of(task));

        LuckyNoStorageException exception = assertThrows(
                LuckyNoStorageException.class, () -> taskMaster.deleteTask(1));
        assertEquals("simulated save failure", exception.getMessage());
        assertEquals(1, taskMaster.getTaskCount());
        assertEquals("Nah, all these things you need to do:\n1.[📌][❗] read book",
                LuckyNoTaskResponses.listed(taskMaster.listTasks()).message());
    }

    /** Verifies that a failed middle deletion restores the original ordering. */
    @Test
    void deleteTask_middleSaveFailure_restoresOriginalOrdering() {
        TaskMaster taskMaster = new TaskMaster(100, new FailingSaver());
        taskMaster.loadTasksFromCsvStorageRecord(List.of(
                new TodoTask("first"),
                new TodoTask("middle"),
                new TodoTask("last")));

        LuckyNoStorageException exception = assertThrows(
                LuckyNoStorageException.class, () -> taskMaster.deleteTask(2));
        assertEquals("simulated save failure", exception.getMessage());

        assertEquals(List.of("first", "middle", "last"),
                taskMaster.listTasks().getTaskViews().stream()
                        .map(TaskView::description).toList());
    }

    /** Verifies that a failed mutation leaves the existing CSV records intact. */
    @Test
    void addTask_saveFailure_preservesExistingCsvRecords() throws Exception {
        Path dataFile = temporaryDirectory.resolve("existing-tasks.csv");
        CsvSaver workingSaver = new CsvSaver(dataFile);
        TaskMaster initialTaskMaster = new TaskMaster(100, workingSaver);
        initialTaskMaster.addTask(new TodoTask("existing task"));
        List<List<String>> recordsBefore = readCsvRecords(dataFile);

        TaskMaster taskMaster = new TaskMaster(
                100, new FailingSaver(dataFile));
        taskMaster.loadTasksFromCsvStorageRecord(
                List.of(new TodoTask("existing task")));

        assertThrows(LuckyNoStorageException.class, () ->
                taskMaster.addTask(new TodoTask("new task")));

        assertEquals(recordsBefore, readCsvRecords(dataFile));
        assertEquals(List.of("existing task"),
                taskMaster.listTasks().getTaskViews().stream()
                        .map(TaskView::description).toList());
    }

    /** Verifies failed status and deletion mutations preserve persisted data. */
    @Test
    void statusAndDeleteMutation_saveFailure_preservesExistingCsvRecords()
            throws Exception {
        Path dataFile = temporaryDirectory.resolve("status-rollback.csv");
        CsvSaver workingSaver = new CsvSaver(dataFile);
        TaskMaster initialTaskMaster = new TaskMaster(100, workingSaver);
        TodoTask completedTask = new TodoTask("completed task");
        completedTask.markAsDone();
        initialTaskMaster.loadTasksFromCsvStorageRecord(List.of(completedTask));
        initialTaskMaster.addTask(new TodoTask("second task"));
        List<List<String>> recordsBefore = readCsvRecords(dataFile);

        TaskMaster taskMaster = new TaskMaster(
                100, new FailingSaver(dataFile));
        TodoTask loadedCompletedTask = new TodoTask("completed task");
        loadedCompletedTask.markAsDone();
        taskMaster.loadTasksFromCsvStorageRecord(List.of(
                loadedCompletedTask, new TodoTask("second task")));

        LuckyNoStorageException exception = assertThrows(
                LuckyNoStorageException.class, () -> taskMaster.markTaskDone(2));
        assertEquals("simulated save failure", exception.getMessage());
        exception = assertThrows(
                LuckyNoStorageException.class, () -> taskMaster.unmarkTaskUndone(1));
        assertEquals("simulated save failure", exception.getMessage());
        exception = assertThrows(
                LuckyNoStorageException.class, () -> taskMaster.deleteTask(2));
        assertEquals("simulated save failure", exception.getMessage());

        assertEquals(recordsBefore, readCsvRecords(dataFile));
        assertEquals(List.of(true, false), taskMaster.listTasks().getTaskViews().stream()
                .map(TaskView::isDone).toList());
    }

    /** Verifies that null or null-containing loaded lists are rejected. */
    @Test
    void loadTasksFromCsvStorageRecord_nullOrNullTaskList_throwsStorageException() {
        TaskMaster taskMaster = createTaskMaster();

        assertThrows(LuckyNoStorageException.class, () ->
                taskMaster.loadTasksFromCsvStorageRecord(null));
        assertThrows(LuckyNoStorageException.class, () ->
                taskMaster.loadTasksFromCsvStorageRecord(
                        java.util.Arrays.asList(new TodoTask("read book"), null)));
    }

    /** Verifies that searching with a null date and description is rejected. */
    @Test
    void findTasks_nullDate_throwsIllegalArgumentException() {
        TaskMaster taskMaster = createTaskMaster();

        assertThrows(IllegalArgumentException.class, () -> taskMaster.findTasks(null, null));
    }

    /** Creates a task master with the default test capacity. */
    private TaskMaster createTaskMaster() {
        return createTaskMaster(100);
    }

    /** Creates a task master with a temporary backing file and capacity. */
    private TaskMaster createTaskMaster(int maxTasks) {
        return new TaskMaster(
                maxTasks,
                new CsvSaver(temporaryDirectory.resolve("tasks.csv")));
    }

    /** Reads all CSV records from a persistence file. */
    private List<List<String>> readCsvRecords(Path dataFile) throws Exception {
        try (Reader reader = Files.newBufferedReader(dataFile, StandardCharsets.UTF_8);
             CSVParser parser = CSVFormat.DEFAULT.parse(reader)) {
            return parser.getRecords().stream()
                    .map(CSVRecord::toList)
                    .toList();
        }
    }

    /** A saver that fails every save attempt for rollback tests. */
    private static class FailingSaver extends CsvSaver {
        FailingSaver() {
            this(Path.of("unused.csv"));
        }

        FailingSaver(Path dataFile) {
            super(dataFile);
        }

        /** Always throws to simulate a persistence failure. */
        @Override
        public void save(TaskList taskList) {
            throw new LuckyNoStorageException("simulated save failure");
        }
    }
}
