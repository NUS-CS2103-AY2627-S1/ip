package friday;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import friday.task.Deadline;
import friday.task.Event;
import friday.task.RepeatFrequency;
import friday.task.Task;
import friday.task.TaskDateTime;
import friday.task.TaskList;

/** Tests regressions reported by Friday's external smoke testers. */
class SmokeRegressionTest {
    @TempDir
    private Path directory;

    @Test
    void add_reservedCharacters_rejectsWithoutLosingExistingTasks() {
        String path = directory.resolve("tasks.txt").toString();
        Friday friday = new Friday(path);
        friday.getResponse("todo ordinary task");
        for (String command : new String[] {"todo tea | coffee", "deadline elon | musk /by 21/9/2026 1800",
            "event fish | chips /from 20/9/2026 1400 /to 20/9/2026 1600", "todo first\nsecond"}) {
            assertTrue(friday.getResponse(command).contains("pipe"));
            assertTrue(friday.wasLastResponseError());
        }
        assertEquals(friday.getResponse("list"), new Friday(path).getResponse("list"));
        assertTrue(friday.getResponse("list").contains("ordinary task"));
    }

    @Test
    void load_corruptFile_displaysWarningAndPreservesOriginalBytes() throws Exception {
        Path path = directory.resolve("tasks.txt");
        String original = "T | 0 | ordinary task\nT | 0 | tea | coffee\n";
        Files.writeString(path, original);
        Friday friday = new Friday(path.toString());
        assertTrue(friday.getWelcome().contains("line 2"));
        assertTrue(friday.getResponse("todo replacement").contains("Changes are disabled"));
        assertEquals(original, Files.readString(path));
    }

    @Test
    void save_failedWrite_rollsBackInMemoryChanges() throws Exception {
        Path path = directory.resolve("tasks.txt");
        Friday friday = new Friday(path.toString());
        friday.getResponse("todo original");
        Files.delete(path);
        Files.createDirectory(path);
        Files.writeString(path.resolve("blocker"), "keep");
        friday.getResponse("edit 1 changed");
        assertTrue(friday.wasLastResponseError());
        assertTrue(friday.getResponse("list").contains("original"));
        assertFalse(friday.getResponse("list").contains("changed"));
    }

    @Test
    void mark_recurringDeadline_createsExactlyOnePersistentSuccessor() {
        String path = directory.resolve("tasks.txt").toString();
        Friday friday = new Friday(path);
        friday.getResponse("deadline work /by 20/9/2026 1800");
        friday.getResponse("repeat 1 daily");
        friday.getResponse("mark 1");
        Friday restored = new Friday(path);
        String expected = restored.getResponse("list");
        restored.getResponse("mark 1");
        restored.getResponse("unmark 1");
        restored.getResponse("mark 1");
        assertEquals(expected, restored.getResponse("list"));
        assertTrue(expected.contains("2. [D][ ] work (repeats: daily)"));
        assertTrue(expected.contains(TaskDateTime.formatForDisplay(LocalDateTime.of(2026, 9, 21, 18, 0))));
        assertFalse(restored.getResponse("reminders").contains("[X]"));
    }

    @Test
    void recurrence_calendarBoundariesAndEventDuration_advanceCorrectly() throws Exception {
        LocalDateTime januaryEnd = LocalDateTime.of(2026, 1, 31, 18, 0);
        assertEquals(januaryEnd.plusDays(1), RepeatFrequency.DAILY.advance(januaryEnd));
        assertEquals(januaryEnd.plusWeeks(1), RepeatFrequency.WEEKLY.advance(januaryEnd));
        assertEquals(januaryEnd.plusWeeks(2), RepeatFrequency.BIWEEKLY.advance(januaryEnd));
        assertEquals(LocalDateTime.of(2026, 2, 28, 18, 0), RepeatFrequency.MONTHLY.advance(januaryEnd));
        assertEquals(LocalDateTime.of(2025, 2, 28, 18, 0),
                RepeatFrequency.YEARLY.advance(LocalDateTime.of(2024, 2, 29, 18, 0)));
        Event event = new Event("meeting", "31/1/2026 1400", "1/2/2026 1600");
        event.setRepeatFrequency(RepeatFrequency.MONTHLY);
        assertTrue(event.nextOccurrence().orElseThrow().toFileString()
                .contains("2026-02-28 1400 | 2026-03-01 1600"));
    }

    @Test
    void edit_description_preservesDatesStatusAndRecurrence() {
        String path = directory.resolve("tasks.txt").toString();
        Friday friday = new Friday(path);
        friday.getResponse("deadline old /by 20/9/2026 1800");
        friday.getResponse("repeat 1 weekly");
        assertTrue(friday.getResponse("edit 1 new description").contains("new description (repeats: weekly)"));
        friday.getResponse("edit 1 bad | description");
        assertTrue(friday.wasLastResponseError());
        assertEquals(friday.getResponse("list"), new Friday(path).getResponse("list"));
    }

    @Test
    void parseSavedTask_descriptionStartingWithRepeat_isNotMetadata() throws Exception {
        Task task = Parser.parseSavedTask("T | 0 | repeat:weekly");
        assertEquals("repeat:weekly", task.getDescription());
        assertTrue(task.nextOccurrence().isEmpty());
    }

    @Test
    void reminders_completedTask_isExcludedFromStartup() throws Exception {
        TaskList tasks = new TaskList();
        tasks.add(new Deadline("done", "20/9/2026 1800"));
        tasks.mark(0);
        assertEquals(0, tasks.getUpcomingReminders(LocalDateTime.of(2026, 9, 19, 0, 0),
                LocalDateTime.of(2026, 9, 21, 0, 0)).size());
    }
}
