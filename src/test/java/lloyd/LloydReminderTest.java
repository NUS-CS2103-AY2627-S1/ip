package lloyd;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests reminder filtering, ordering, formatting, and storage preservation. */
public class LloydReminderTest {
    private static final Clock FIXED_CLOCK = Clock.fixed(
            Instant.parse("2026-09-14T04:00:00Z"), ZoneId.of("Asia/Singapore"));

    @TempDir
    private Path temporaryDirectory;

    /** Verifies classification, ordering, original numbering, and exclusions. */
    @Test
    public void reminder_mixedTasks_returnsOverdueAndDueSoonTasks() throws IOException {
        Lloyd lloyd = createLloyd(List.of(
                "T | 0 | buy supplies",
                "D | 0 | file permit | 2026-09-17",
                "D | 0 | pay invoice | 2026-09-13",
                "D | 1 | submit report | 2026-09-15",
                "E | 0 | morning inspection | 2026-09-14T09:00 | 2026-09-14T10:00",
                "D | 0 | renew lease | 2026-09-18",
                "E | 0 | building works | 2026-09-12T08:00 | 2026-09-15T18:00",
                "E | 1 | completed visit | 2026-09-14T14:00 | 2026-09-14T15:00"));

        String response = lloyd.getResponse("reminder");

        assertEquals(
                "Reminders:\n"
                        + "Overdue:\n"
                        + "7. [E][ ] building works (from: Sep 12 2026, 8:00 AM"
                        + " to: Sep 15 2026, 6:00 PM)\n"
                        + "3. [D][ ] pay invoice (by: Sep 13 2026)\n"
                        + "Due within 3 days, including today:\n"
                        + "5. [E][ ] morning inspection (from: Sep 14 2026, 9:00 AM"
                        + " to: Sep 14 2026, 10:00 AM)\n"
                        + "2. [D][ ] file permit (by: Sep 17 2026)",
                response);
    }

    /** Verifies that tasks on the same date retain their master-list order. */
    @Test
    public void reminder_tasksOnSameDate_preservesOriginalOrder() throws IOException {
        Lloyd lloyd = createLloyd(List.of(
                "E | 0 | afternoon meeting | 2026-09-14T15:00 | 2026-09-14T16:00",
                "D | 0 | submit plans | 2026-09-14",
                "E | 0 | morning meeting | 2026-09-14T09:00 | 2026-09-14T10:00"));

        String response = lloyd.getResponse("reminder");

        assertEquals(
                "Reminders:\n"
                        + "Due within 3 days, including today:\n"
                        + "1. [E][ ] afternoon meeting (from: Sep 14 2026, 3:00 PM"
                        + " to: Sep 14 2026, 4:00 PM)\n"
                        + "2. [D][ ] submit plans (by: Sep 14 2026)\n"
                        + "3. [E][ ] morning meeting (from: Sep 14 2026, 9:00 AM"
                        + " to: Sep 14 2026, 10:00 AM)",
                response);
    }

    /** Verifies the response when no incomplete dated tasks qualify. */
    @Test
    public void reminder_noQualifyingTasks_returnsEmptyResultMessage() throws IOException {
        Lloyd lloyd = createLloyd(List.of(
                "T | 0 | buy supplies",
                "D | 1 | completed permit | 2026-09-14",
                "D | 0 | distant project | 2026-09-18"));

        assertEquals(
                "No urgent tasks! You have no overdue tasks"
                        + " or tasks due within the next 3 days.",
                lloyd.getResponse("reminder"));
    }

    /** Verifies that reminder arguments are rejected without changing stored tasks. */
    @Test
    public void reminder_withArguments_rejectsCommandAndPreservesStorage() throws IOException {
        Path storagePath = temporaryDirectory.resolve("tasks.txt");
        String originalContents = "D | 0 | file permit | 2026-09-17";
        Files.writeString(storagePath, originalContents, StandardCharsets.UTF_8);
        Lloyd lloyd = new Lloyd(storagePath, FIXED_CLOCK);

        String response = lloyd.getResponse("reminder 5");

        assertEquals(
                "Enter reminder without additional information.",
                response);
        assertEquals(originalContents,
                Files.readString(storagePath, StandardCharsets.UTF_8));
    }

    /** Creates a Lloyd instance whose task storage contains the supplied lines. */
    private Lloyd createLloyd(List<String> taskLines) throws IOException {
        Path storagePath = temporaryDirectory.resolve("tasks.txt");
        Files.write(storagePath, taskLines, StandardCharsets.UTF_8);
        return new Lloyd(storagePath, FIXED_CLOCK);
    }
}
