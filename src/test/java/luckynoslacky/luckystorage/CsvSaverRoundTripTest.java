package luckynoslacky.luckystorage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.junit.jupiter.api.Test;

import luckynoslacky.luckyresponse.LuckyNoTaskResponses;
import luckynoslacky.luckytask.DeadlineTask;
import luckynoslacky.luckytask.DurationPeriod;
import luckynoslacky.luckytask.EventTask;
import luckynoslacky.luckytask.Task;
import luckynoslacky.luckytask.TaskMaster;
import luckynoslacky.luckytask.TaskTimes;
import luckynoslacky.luckytask.TaskView;
import luckynoslacky.luckytask.TodoTask;

/** Tests successful CSV persistence and task round trips. */
class CsvSaverRoundTripTest extends CsvSaverTestSupport {
    /** Verifies CSV escaping for task records with special characters. */
    @Test
    void save_taskListWithSpecialCharacters_writesCsvRecords() throws Exception {
        Path dataFile = temporaryDirectory.resolve("luckyNoSlacky.csv");
        CsvSaver saver = new CsvSaver(dataFile);
        TaskMaster taskMaster = new TaskMaster(100, saver);

        taskMaster.addTask(new TodoTask("read, book"));
        taskMaster.addTask(new DeadlineTask("return \"book\"", DEADLINE));
        taskMaster.addTask(new EventTask("project meeting", EVENT_START, EVENT_END));
        taskMaster.markTaskDone(1);

        try (Reader reader = Files.newBufferedReader(dataFile, StandardCharsets.UTF_8);
             CSVParser parser = CSVFormat.DEFAULT.parse(reader)) {
            List<CSVRecord> records = parser.getRecords();

            assertEquals(CSV_HEADER, records.get(0).toList());
            assertEquals(List.of("T", "1", "read, book", "", ""),
                    records.get(1).toList());
            assertEquals(List.of("D", "0", "return \"book\"", "",
                            "2026-12-06 23:59"), records.get(2).toList());
            assertEquals(List.of("E", "0", "project meeting",
                            "2026-08-06 14:00", "2026-08-06 16:00"),
                    records.get(3).toList());
        }
    }

    /** Verifies that a deadline duration snooze is written and reloadable. */
    @Test
    void snoozeTaskBy_deadlineDuration_writesUpdatedEndTimeToCsv() throws Exception {
        Path dataFile = temporaryDirectory.resolve("deadline-snooze.csv");
        CsvSaver saver = new CsvSaver(dataFile);
        TaskMaster taskMaster = new TaskMaster(100, saver);
        taskMaster.addTask(new DeadlineTask(
                "return book", LocalDateTime.of(2026, 8, 26, 12, 0)));

        taskMaster.snoozeTaskBy(
                1, new DurationPeriod(Period.ZERO, Duration.ofHours(2)));

        assertCsvRecord(dataFile,
                List.of("D", "0", "return book", "", "2026-08-26 14:00"));
        TaskMaster restored = loadTaskMaster(dataFile);
        assertEquals(
                LocalDateTime.of(2026, 8, 26, 14, 0),
                restored.getTaskEndTime(1));
    }

    /** Verifies that an event duration snooze changes only the saved end time. */
    @Test
    void snoozeTaskBy_eventDuration_writesOnlyUpdatedEndTimeToCsv() throws Exception {
        Path dataFile = temporaryDirectory.resolve("event-snooze.csv");
        CsvSaver saver = new CsvSaver(dataFile);
        TaskMaster taskMaster = new TaskMaster(100, saver);
        taskMaster.addTask(new EventTask(
                "project meeting",
                LocalDateTime.of(2026, 8, 26, 14, 0),
                LocalDateTime.of(2026, 8, 26, 16, 0)));

        taskMaster.snoozeTaskBy(
                1, new DurationPeriod(Period.ZERO, Duration.ofHours(2)));

        assertCsvRecord(dataFile,
                List.of("E", "0", "project meeting",
                        "2026-08-26 14:00", "2026-08-26 18:00"));
        TaskMaster restored = loadTaskMaster(dataFile);
        assertEquals(
                LocalDateTime.of(2026, 8, 26, 14, 0),
                restored.getTaskStartTime(1));
        assertEquals(
                LocalDateTime.of(2026, 8, 26, 18, 0),
                restored.getTaskEndTime(1));
    }

    /** Verifies that an explicit deadline snooze is written and reloadable. */
    @Test
    void snoozeTaskTo_deadlineTarget_writesNewEndTimeToCsv() throws Exception {
        Path dataFile = temporaryDirectory.resolve("deadline-snooze-to.csv");
        CsvSaver saver = new CsvSaver(dataFile);
        TaskMaster taskMaster = new TaskMaster(100, saver);
        taskMaster.addTask(new DeadlineTask(
                "return book", LocalDateTime.of(2026, 8, 26, 12, 0)));

        taskMaster.snoozeTaskTo(
                1, LocalDateTime.of(2026, 8, 28, 17, 0));

        assertCsvRecord(dataFile,
                List.of("D", "0", "return book", "", "2026-08-28 17:00"));
        assertEquals(
                LocalDateTime.of(2026, 8, 28, 17, 0),
                loadTaskMaster(dataFile).getTaskEndTime(1));
    }

    /** Verifies that an explicit event snooze preserves the saved start time. */
    @Test
    void snoozeTaskTo_eventTarget_preservesStartTimeInCsv() throws Exception {
        Path dataFile = temporaryDirectory.resolve("event-snooze-to.csv");
        CsvSaver saver = new CsvSaver(dataFile);
        TaskMaster taskMaster = new TaskMaster(100, saver);
        taskMaster.addTask(new EventTask(
                "project meeting",
                LocalDateTime.of(2026, 8, 26, 14, 0),
                LocalDateTime.of(2026, 8, 26, 16, 0)));

        taskMaster.snoozeTaskTo(
                1, LocalDateTime.of(2026, 8, 28, 17, 0));

        assertCsvRecord(dataFile,
                List.of("E", "0", "project meeting",
                        "2026-08-26 14:00", "2026-08-28 17:00"));
        TaskMaster restored = loadTaskMaster(dataFile);
        assertEquals(
                LocalDateTime.of(2026, 8, 26, 14, 0),
                restored.getTaskStartTime(1));
        assertEquals(
                LocalDateTime.of(2026, 8, 28, 17, 0),
                restored.getTaskEndTime(1));
    }

    /** Verifies that a deadline reschedule is written and reloadable. */
    @Test
    void rescheduleTask_newDeadlineTime_writesUpdatedDeadlineToCsv() throws Exception {
        Path dataFile = temporaryDirectory.resolve("deadline-reschedule.csv");
        CsvSaver saver = new CsvSaver(dataFile);
        TaskMaster taskMaster = new TaskMaster(100, saver);
        taskMaster.addTask(new DeadlineTask(
                "return book", LocalDateTime.of(2026, 8, 26, 12, 0)));

        taskMaster.rescheduleTask(
                1,
                TaskTimes.makeDeadlineTimes(
                        LocalDateTime.of(2026, 9, 1, 9, 30)));

        assertCsvRecord(dataFile,
                List.of("D", "0", "return book", "", "2026-09-01 09:30"));
        assertEquals(
                LocalDateTime.of(2026, 9, 1, 9, 30),
                loadTaskMaster(dataFile).getTaskEndTime(1));
    }

    /** Verifies that full event rescheduling writes both event times. */
    @Test
    void rescheduleTask_newEventTimes_writesBothEventTimesToCsv() throws Exception {
        Path dataFile = temporaryDirectory.resolve("event-reschedule.csv");
        CsvSaver saver = new CsvSaver(dataFile);
        TaskMaster taskMaster = new TaskMaster(100, saver);
        taskMaster.addTask(new EventTask(
                "project meeting",
                LocalDateTime.of(2026, 8, 26, 14, 0),
                LocalDateTime.of(2026, 8, 26, 16, 0)));

        taskMaster.rescheduleTask(
                1,
                TaskTimes.makeEventTimes(
                        LocalDateTime.of(2026, 8, 28, 10, 0),
                        LocalDateTime.of(2026, 8, 28, 11, 0)));

        assertCsvRecord(dataFile,
                List.of("E", "0", "project meeting",
                        "2026-08-28 10:00", "2026-08-28 11:00"));
        TaskMaster restored = loadTaskMaster(dataFile);
        assertEquals(
                LocalDateTime.of(2026, 8, 28, 10, 0),
                restored.getTaskStartTime(1));
        assertEquals(
                LocalDateTime.of(2026, 8, 28, 11, 0),
                restored.getTaskEndTime(1));
    }

    /** Verifies that partial event updates retain the omitted time in CSV. */
    @Test
    void rescheduleTask_partialEventTimes_writeRetainedTimes() throws Exception {
        Path dataFile = temporaryDirectory.resolve("partial-event-reschedule.csv");
        CsvSaver saver = new CsvSaver(dataFile);
        TaskMaster taskMaster = new TaskMaster(100, saver);
        taskMaster.addTask(new EventTask(
                "project meeting",
                LocalDateTime.of(2026, 8, 26, 14, 0),
                LocalDateTime.of(2026, 8, 26, 16, 0)));

        taskMaster.rescheduleTask(
                1,
                TaskTimes.makeEventTimes(
                        LocalDateTime.of(2026, 8, 26, 15, 0),
                        LocalDateTime.of(2026, 8, 26, 16, 0)));
        assertCsvRecord(dataFile,
                List.of("E", "0", "project meeting",
                        "2026-08-26 15:00", "2026-08-26 16:00"));

        taskMaster.rescheduleTask(
                1,
                TaskTimes.makeEventTimes(
                        LocalDateTime.of(2026, 8, 26, 15, 0),
                        LocalDateTime.of(2026, 8, 26, 18, 0)));
        assertCsvRecord(dataFile,
                List.of("E", "0", "project meeting",
                        "2026-08-26 15:00", "2026-08-26 18:00"));
        assertEquals(
                LocalDateTime.of(2026, 8, 26, 18, 0),
                loadTaskMaster(dataFile).getTaskEndTime(1));
    }

    /** Verifies that a completed task remains completed after a time update. */
    @Test
    void snoozeTaskBy_completedTask_preservesCompletionStatusInCsv() throws Exception {
        Path dataFile = temporaryDirectory.resolve("completed-snooze.csv");
        CsvSaver saver = new CsvSaver(dataFile);
        TaskMaster taskMaster = new TaskMaster(100, saver);
        taskMaster.addTask(new DeadlineTask(
                "return book", LocalDateTime.of(2026, 8, 26, 12, 0)));
        taskMaster.markTaskDone(1);

        taskMaster.snoozeTaskBy(
                1, new DurationPeriod(Period.ZERO, Duration.ofHours(2)));

        assertCsvRecord(dataFile,
                List.of("D", "1", "return book", "", "2026-08-26 14:00"));
        TaskView restoredTask = loadTaskMaster(dataFile).listTasks()
                .getTaskViews().getFirst();
        assertEquals(Task.TaskType.DEADLINE, restoredTask.taskType());
        assertTrue(restoredTask.isDone());
        assertEquals("return book", restoredTask.description());
    }

    /** Verifies saving rewrites the file after a task is deleted. */
    @Test
    void save_afterTaskDeletion_rewritesCsvFile() throws Exception {
        Path dataFile = temporaryDirectory.resolve("luckyNoSlacky.csv");
        CsvSaver saver = new CsvSaver(dataFile);
        TaskMaster taskMaster = new TaskMaster(100, saver);

        taskMaster.addTask(new TodoTask("first"));
        taskMaster.addTask(new TodoTask("second"));
        taskMaster.deleteTask(1);

        try (Reader reader = Files.newBufferedReader(dataFile, StandardCharsets.UTF_8);
             CSVParser parser = CSVFormat.DEFAULT.parse(reader)) {
            List<CSVRecord> records = parser.getRecords();

            assertEquals(2, records.size());
            assertEquals(List.of("T", "0", "second", "", ""),
                    records.get(1).toList());
        }
    }

    /** Verifies a missing data file is treated as an empty task list. */
    @Test
    void load_missingFile_returnsEmptyList() {
        Path dataFile = temporaryDirectory.resolve("missing.csv");
        CsvSaver saver = new CsvSaver(dataFile);

        assertTrue(saver.load().isEmpty());
    }

    /** Verifies an empty data file loads as an empty task list. */
    @Test
    void load_emptyFile_returnsEmptyList() throws Exception {
        Path dataFile = temporaryDirectory.resolve("empty.csv");
        Files.writeString(dataFile, "", StandardCharsets.UTF_8);
        CsvSaver saver = new CsvSaver(dataFile);

        assertTrue(saver.load().isEmpty());
    }

    /** Verifies valid CSV records restore tasks and completion statuses. */
    @Test
    void load_validCsv_returnsTasksAndStatuses() {
        Path dataFile = temporaryDirectory.resolve("luckyNoSlacky.csv");
        CsvSaver saver = new CsvSaver(dataFile);
        TaskMaster original = new TaskMaster(100, saver);

        original.addTask(new TodoTask("read, book"));
        original.addTask(new DeadlineTask("return book", DEADLINE));
        original.addTask(new EventTask("project meeting", EVENT_START, EVENT_END));
        original.markTaskDone(1);

        List<Task> loadedTasks = saver.load();
        TaskMaster restored = new TaskMaster(100, saver);
        restored.loadTasksFromCsvStorageRecord(loadedTasks);

        assertEquals("Nah, all these things you need to do:\n"
                        + "1.[📌][✅] read, book\n"
                        + "2.[⏳][❗] return book (by: Sun Dec 06 2026, 11.59pm)\n"
                        + "3.[📆][❗] project meeting (from: Thu Aug 06 2026, 2.00pm"
                        + " to: Thu Aug 06 2026, 4.00pm)",
                LuckyNoTaskResponses.listed(restored.listTasks()).message());
    }

    /** Verifies that a mixed list remains equivalent after mutation and reload. */
    @Test
    void save_mutatedMixedTaskList_reloadsEquivalentState() {
        Path dataFile = temporaryDirectory.resolve("mutated-tasks.csv");
        CsvSaver saver = new CsvSaver(dataFile);
        TaskMaster original = new TaskMaster(3, saver);
        original.addTask(new TodoTask("read, book"));
        original.addTask(new DeadlineTask("return \"book\"", DEADLINE));
        original.addTask(new EventTask("project/meeting", EVENT_START, EVENT_END));
        original.markTaskDone(1);
        original.snoozeTaskBy(
                2, new DurationPeriod(Period.ZERO, Duration.ofHours(2)));
        original.snoozeTaskTo(3, LocalDateTime.of(2026, 8, 7, 18, 0));

        List<List<String>> expectedRecords = original.listTasks().getTasks()
                .stream()
                .map(TaskCsvCodec::toRecord)
                .toList();
        List<Task> loadedTasks = saver.load();
        TaskMaster restored = new TaskMaster(3, saver);
        restored.loadTasksFromCsvStorageRecord(loadedTasks);

        assertEquals(expectedRecords,
                restored.listTasks().getTasks().stream()
                        .map(TaskCsvCodec::toRecord)
                        .toList());
        assertEquals(original.listTasks().getTaskViews(),
                restored.listTasks().getTaskViews());
    }

    /** Verifies that deleting the final task persists an empty list. */
    @Test
    void deleteTask_lastTask_reloadsEmptyList() {
        Path dataFile = temporaryDirectory.resolve("empty-after-delete.csv");
        CsvSaver saver = new CsvSaver(dataFile);
        TaskMaster taskMaster = new TaskMaster(1, saver);
        taskMaster.addTask(new TodoTask("temporary task"));

        taskMaster.deleteTask(1);

        assertEquals(true, saver.load().isEmpty());
    }
}
