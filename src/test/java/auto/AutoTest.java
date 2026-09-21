package auto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import auto.storage.Storage;
import auto.task.Deadline;
import auto.task.Event;
import auto.task.Task;
import auto.task.ToDo;

/** Tests Auto's command responses, persistence integration, and startup recovery. */
class AutoTest {
    private static final Clock STARTUP_CLOCK = Clock.fixed(
            Instant.parse("2026-09-09T16:30:00Z"), ZoneId.of("Asia/Singapore"));
    private static final LocalDate TODAY = LocalDate.of(2026, 9, 10);

    @TempDir
    Path tempDirectory;

    @Test
    void getStartupMessage_todayInLocalZone_listsMatchingTasksWithOriginalNumbers() throws Exception {
        Path dataFile = tempDirectory.resolve("tasks.txt");
        Task deadline = new Deadline("submit report", TODAY);
        deadline.mark();
        new Storage(dataFile.toString()).save(List.of(
                new ToDo("read book"), deadline,
                new Event("starts today", TODAY, TODAY.plusDays(1)),
                new Event("ends today", TODAY.minusDays(1), TODAY),
                new Event("ongoing", TODAY.minusDays(1), TODAY.plusDays(1)),
                new Deadline("old deadline", TODAY.minusDays(1)),
                new Deadline("future deadline", TODAY.plusDays(1)),
                new Event("past event", TODAY.minusDays(2), TODAY.minusDays(1)),
                new Event("future event", TODAY.plusDays(1), TODAY.plusDays(2))));
        String savedData = Files.readString(dataFile);

        Auto auto = new Auto(dataFile.toString(), STARTUP_CLOCK);

        assertEquals(String.join(System.lineSeparator(),
                "Eh, remember these tasks for today (Sep 10 2026):",
                "2. [D][X] submit report (by: Sep 10 2026)",
                "3. [E][ ] starts today (from: Sep 10 2026 to: Sep 11 2026)",
                "4. [E][ ] ends today (from: Sep 09 2026 to: Sep 10 2026)",
                "5. [E][ ] ongoing (from: Sep 09 2026 to: Sep 11 2026)"), auto.getStartupMessage());
        assertEquals(auto.getStartupMessage(),
                new Auto(dataFile.toString(), STARTUP_CLOCK).getStartupMessage());
        assertEquals(savedData, Files.readString(dataFile));
    }

    @Test
    void getStartupMessage_noTasksToday_omitsReminders() throws Exception {
        Path dataFile = tempDirectory.resolve("tasks.txt");
        assertEquals("", new Auto(dataFile.toString(), STARTUP_CLOCK).getStartupMessage());
        new Storage(dataFile.toString()).save(List.of(
                new ToDo("read book"), new Deadline("later", TODAY.plusDays(1))));

        assertEquals("", new Auto(dataFile.toString(), STARTUP_CLOCK).getStartupMessage());
    }

    @Test
    void getStartupMessage_corruptDataWithTaskToday_keepsWarningAndReminder() throws Exception {
        Path dataFile = tempDirectory.resolve("tasks.txt");
        Task deadline = new Deadline("submit report", TODAY);
        Files.writeString(dataFile, deadline.toDataString() + "\nnot a task", StandardCharsets.UTF_8);

        Auto auto = new Auto(dataFile.toString(), STARTUP_CLOCK);

        assertEquals(String.join(System.lineSeparator(),
                "Paiseh, some saved data cannot be read. 1 invalid data line(s) were skipped: "
                        + "line 2 is invalid and was skipped.",
                "", "Eh, remember these tasks for today (Sep 10 2026):",
                "1. [D][ ] submit report (by: Sep 10 2026)"), auto.getStartupMessage());
    }

    @Test
    void getResponse_multipleCommands_preservesStateAndPersistsTasks() throws Exception {
        Path dataFile = tempDirectory.resolve("tasks.txt");
        Auto auto = new Auto(dataFile.toString());

        String addResponse = auto.getResponse("todo read book");
        String markResponse = auto.getResponse("mark 1");
        String listResponse = auto.getResponse("list");

        assertTrue(addResponse.contains("Can! Added this task for you:"));
        assertTrue(markResponse.contains("Steady lah! Marked this task as done:"));
        assertTrue(listResponse.contains("1. [T][X] read book"));
        assertEquals(List.of("T | 1 | cmVhZCBib29r"),
                Files.readAllLines(dataFile, StandardCharsets.UTF_8));
    }

    @Test
    void getResponse_invalidCommand_returnsFriendlyError() {
        Auto auto = new Auto(tempDirectory.resolve("tasks.txt").toString());

        String response = auto.getResponse("borrow book");

        assertEquals("Paiseh, I don't understand this command lah.", response);
    }

    @Test
    void getResponse_existingData_loadsTasksBeforeFirstCommand() throws Exception {
        Path dataFile = tempDirectory.resolve("tasks.txt");
        Files.writeString(dataFile, "T | 1 | cmVhZCBib29r", StandardCharsets.UTF_8);

        Auto auto = new Auto(dataFile.toString());

        assertTrue(auto.getResponse("list").contains("1. [T][X] read book"));
    }

    @Test
    void getStartupMessage_partiallyCorruptedData_returnsWarningAndKeepsValidTasks()
            throws Exception {
        Path dataFile = tempDirectory.resolve("tasks.txt");
        Files.writeString(dataFile, "T | 0 | cmVhZCBib29r\nnot a task", StandardCharsets.UTF_8);
        Auto auto = new Auto(dataFile.toString());

        String startupMessage = auto.getStartupMessage();
        String response = auto.getResponse("list");

        assertTrue(startupMessage.contains(
                "Paiseh, some saved data cannot be read. 1 invalid data line(s) were skipped"));
        assertEquals("Here's what you have on your plate:" + System.lineSeparator()
                + "1. [T][ ] read book", response);
    }

    @Test
    void getResponse_invalidDataPath_reportsLoadAndSaveFailures() {
        Auto auto = new Auto(tempDirectory.toString());

        String startupMessage = auto.getStartupMessage();
        String response = auto.getResponse("todo read book");

        assertTrue(startupMessage.contains(
                "Paiseh, I couldn't load your saved tasks. Starting with an empty task list."));
        assertTrue(response.contains(
                "Paiseh, I couldn't save your tasks. Your latest change was not applied."));
        assertEquals("Here's what you have on your plate:", auto.getResponse("list"));
    }
}
