package luckynoslacky.luckystorage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

import luckynoslacky.luckyexception.LuckyNoStorageException;
import luckynoslacky.luckytask.Task;
import luckynoslacky.luckytask.TaskMaster;
import luckynoslacky.luckytask.TodoTask;

/** Tests task-capacity boundaries and physically large CSV files. */
class CsvSaverCapacityTest extends CsvSaverTestSupport {
    private static final int LARGE_CSV_TASK_COUNT = 4;
    private static final int LARGE_DESCRIPTION_LENGTH = 1_000_000;

    /** Verifies that a bounded physically large CSV remains reloadable. */
    @Test
    void load_boundedLargeCsv_returnsAllTasks() throws Exception {
        Path dataFile = temporaryDirectory.resolve("large.csv");
        String largeDescription = "x".repeat(LARGE_DESCRIPTION_LENGTH);
        List<List<String>> records = IntStream.range(
                        0, LARGE_CSV_TASK_COUNT)
                .mapToObj(index -> List.of(
                        "T", "0", largeDescription, "", ""))
                .toList();
        writeCsvRecords(dataFile, records);

        assertTrue(Files.size(dataFile)
                >= (long) LARGE_DESCRIPTION_LENGTH * LARGE_CSV_TASK_COUNT);

        List<Task> loadedTasks = new CsvSaver(dataFile).load();

        assertEquals(LARGE_CSV_TASK_COUNT, loadedTasks.size());
        assertEquals(records.get(0), TaskCsvCodec.toRecord(loadedTasks.get(0)));
        assertEquals(records.get(records.size() - 1),
                TaskCsvCodec.toRecord(loadedTasks.get(loadedTasks.size() - 1)));
    }

    /** Verifies loading beyond task capacity is rejected. */
    @Test
    void load_tasksExceedingCapacity_throwsStorageException() {
        Path dataFile = temporaryDirectory.resolve("luckyNoSlacky.csv");
        CsvSaver saver = new CsvSaver(dataFile);
        TaskMaster taskMaster = new TaskMaster(1, saver);

        LuckyNoStorageException exception = assertThrows(
                LuckyNoStorageException.class, () ->
                taskMaster.loadTasksFromCsvStorageRecord(List.of(
                        new TodoTask("first"),
                        new TodoTask("second"))));
        assertEquals(
                "Saved task list exceeds the maximum capacity.",
                exception.getMessage());
    }

    /** Verifies that loading exactly the configured capacity succeeds. */
    @Test
    void load_tasksAtCapacity_replacesTaskList() throws Exception {
        Path dataFile = temporaryDirectory.resolve("at-capacity.csv");
        writeCsvRecords(dataFile, List.of(
                List.of("T", "0", "first", "", ""),
                List.of("D", "1", "second", "", "2026-12-06 23:59")));
        CsvSaver saver = new CsvSaver(dataFile);
        TaskMaster taskMaster = new TaskMaster(2, saver);

        taskMaster.loadTasksFromCsvStorageRecord(saver.load());

        assertEquals(2, taskMaster.getTaskCount());
        assertEquals(Task.TaskType.TODO, taskMaster.getTaskType(1));
        assertEquals(Task.TaskType.DEADLINE, taskMaster.getTaskType(2));
        assertTrue(taskMaster.listTasks().getTask(2).isDone());
    }

    /** Verifies that an oversized import preserves existing in-memory tasks. */
    @Test
    void load_tasksOverCapacity_preservesExistingTaskList() throws Exception {
        Path dataFile = temporaryDirectory.resolve("over-capacity.csv");
        writeCsvRecords(dataFile, List.of(
                List.of("T", "0", "first", "", ""),
                List.of("E", "1", "second",
                        "2026-08-06 14:00", "2026-08-06 16:00"),
                List.of("D", "0", "third", "", "2026-12-06 23:59")));
        CsvSaver saver = new CsvSaver(dataFile);
        TaskMaster taskMaster = new TaskMaster(2, saver);
        taskMaster.loadTasksFromCsvStorageRecord(
                List.of(new TodoTask("existing task")));
        List<List<String>> recordsBefore = readCsvValues(dataFile);

        LuckyNoStorageException exception = assertThrows(
                LuckyNoStorageException.class, () ->
                taskMaster.loadTasksFromCsvStorageRecord(saver.load()));

        assertEquals(
                "Saved task list exceeds the maximum capacity.",
                exception.getMessage());
        assertEquals(1, taskMaster.getTaskCount());
        assertEquals("existing task", taskMaster.listTasks().getTask(1)
                .getDescription());
        assertEquals(recordsBefore, readCsvValues(dataFile));
    }
}
