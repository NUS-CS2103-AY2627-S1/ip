package puyo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Integration tests for commands, responses, and persistence through the real Puyo application.
 * Written with assistance from ChatGPT.
 */
public class PuyoTest {

    private Path dataFile;
    private Puyo puyo;

    @BeforeEach
    public void setUp(@TempDir Path tempDir) {
        dataFile = tempDir.resolve("data/puyo.txt");
        puyo = new Puyo(dataFile.toString());
    }

    @Test
    public void getResponse_addAndList_displaysAllTaskTypes() {
        assertTrue(puyo.getResponse("todo Read book").contains("[T][X] Read book"));
        assertTrue(puyo.getResponse("deadline Submit report /by 2026-09-20 1800")
                .contains("[D][X] Submit report"));
        assertTrue(puyo.getResponse("event Workshop /from 2026-09-21 1000 /to 2026-09-21 1200")
                .contains("[E][X] Workshop"));

        String list = puyo.getResponse("list");

        assertTrue(list.contains("1. [T][X] Read book"));
        assertTrue(list.contains("2. [D][X] Submit report"));
        assertTrue(list.contains("3. [E][X] Workshop"));
    }

    @Test
    public void getResponse_uppercaseFlags_preservesUnicodeDescriptions() {
        String deadlineResponse = puyo.getResponse("deadline İstanbul Plan /BY 2026-09-20 1800");
        String eventResponse = puyo.getResponse("event İstanbul Plan /FROM 2026-09-21 1000 /TO 2026-09-21 1200");

        assertTrue(deadlineResponse.contains("[D][X] İstanbul Plan (by:"));
        assertTrue(eventResponse.contains("[E][X] İstanbul Plan (from:"));
        String list = puyo.getResponse("list");
        assertTrue(list.contains("1. [D][X] İstanbul Plan (by:"));
        assertTrue(list.contains("2. [E][X] İstanbul Plan (from:"));
    }

    @Test
    public void getResponse_markUnmarkAndDelete_updatesTaskState() {
        puyo.getResponse("todo Read book");

        assertTrue(puyo.getResponse("mark 1").contains("[T][✓] Read book"));
        assertTrue(puyo.getResponse("list").contains("1. [T][✓] Read book"));

        assertTrue(puyo.getResponse("unmark 1").contains("[T][X] Read book"));
        assertTrue(puyo.getResponse("list").contains("1. [T][X] Read book"));

        assertTrue(puyo.getResponse("delete 1").contains("Read book"));
        String list = puyo.getResponse("list");
        assertTrue(list.contains("(no tasks yet)"));
        assertFalse(list.contains("Read book"));
    }

    @Test
    public void getResponse_restart_loadsSavedTasksAndCompletionState() {
        puyo.getResponse("todo Read book");
        puyo.getResponse("deadline Submit report /by 2026-09-20 1800");
        puyo.getResponse("event Workshop /from 2026-09-21 1000 /to 2026-09-21 1200");
        puyo.getResponse("mark 1");

        Puyo restartedPuyo = new Puyo(dataFile.toString());
        String reloadedList = restartedPuyo.getResponse("list");

        assertTrue(Files.exists(dataFile));
        assertTrue(reloadedList.contains("1. [T][✓] Read book"));
        assertTrue(reloadedList.contains("2. [D][X] Submit report"));
        assertTrue(reloadedList.contains("3. [E][X] Workshop"));
        assertEquals(puyo.getResponse("list"), reloadedList);
    }

    @Test
    public void getResponse_invalidDates_preservesTasksAndSavedData() throws IOException {
        puyo.getResponse("todo Keep this task");
        String originalList = puyo.getResponse("list");
        String originalData = Files.readString(dataFile);

        assertTrue(puyo.getResponse("deadline Invalid deadline /by 2026-02-30")
                .contains("Invalid date format"));
        assertTrue(puyo.getResponse("event Invalid event /from 2026-02-29 1000 /to 2026-03-01 1200")
                .contains("Invalid date format"));
        assertTrue(puyo.getResponse("viewschedule 2026-09-31").contains("Please provide a valid date"));

        assertEquals(originalList, puyo.getResponse("list"));
        assertEquals(originalData, Files.readString(dataFile));
        assertEquals(originalList, new Puyo(dataFile.toString()).getResponse("list"));
    }

    @Test
    public void getResponse_multiDaySchedule_includesInteriorDateAndExcludesTextMatches() {
        puyo.getResponse("event Conference /from 2026-09-02 2300 /to 2026-09-04 0000");
        puyo.getResponse("todo Review Sep 03 2026 notes");
        puyo.getResponse("deadline Early meeting /by 2026-09-13 0300");

        String schedule = puyo.getResponse("viewschedule 2026-09-03");

        assertTrue(schedule.contains("Here are the schedules for 2026-09-03:"));
        assertTrue(schedule.contains("1. [E][X] Conference"));
        assertFalse(schedule.contains("Review Sep 03 2026 notes"));
        assertFalse(schedule.contains("Early meeting"));
        assertTrue(puyo.getResponse("viewschedule 2026-09-04").contains("[E][X] Conference"));
        assertTrue(puyo.getResponse("viewschedule 2026-09-05").contains("No schedules found"));
    }
}
