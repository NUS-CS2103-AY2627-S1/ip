package lloyd;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests the mood attached to Lloyd's command responses. */
public class LloydMoodTest {
    private static final Clock FIXED_CLOCK = Clock.fixed(
            Instant.parse("2026-09-14T04:00:00Z"), ZoneId.of("Asia/Singapore"));

    @TempDir
    private Path temporaryDirectory;

    /** Verifies that ordinary informational responses remain confident. */
    @Test
    public void getReply_listCommand_returnsConfidentMood() throws IOException {
        Lloyd lloyd = createLloyd("");

        LloydResponse response = lloyd.getReply("list");

        assertEquals(LloydMood.CONFIDENT, response.mood());
        assertEquals("Your task list is empty.", response.text());
    }

    /** Verifies that successful task additions make Lloyd delighted. */
    @Test
    public void getReply_successfulTodo_returnsDelightedMood() throws IOException {
        Lloyd lloyd = createLloyd("");

        assertEquals(
                LloydMood.DELIGHTED,
                lloyd.getReply("todo inspect the site").mood());
    }

    /** Verifies that invalid commands make Lloyd annoyed. */
    @Test
    public void getReply_invalidTodo_returnsAnnoyedMood() throws IOException {
        Lloyd lloyd = createLloyd("");

        assertEquals(LloydMood.ANNOYED, lloyd.getReply("todo").mood());
    }

    /** Verifies that rework makes Lloyd annoyed even when the command succeeds. */
    @Test
    public void getReply_successfulUnmark_returnsAnnoyedMood() throws IOException {
        Lloyd lloyd = createLloyd("T | 1 | inspect the site");

        assertEquals(LloydMood.ANNOYED, lloyd.getReply("unmark 1").mood());
    }

    /** Verifies that urgent reminders make Lloyd alarmed. */
    @Test
    public void getReply_overdueReminder_returnsAlarmedMood() throws IOException {
        Lloyd lloyd = createLloyd("D | 0 | pay invoice | 2026-09-13");

        assertEquals(LloydMood.ALARMED, lloyd.getReply("reminder").mood());
    }

    /** Verifies that an empty reminder report makes Lloyd delighted. */
    @Test
    public void getReply_emptyReminder_returnsDelightedMood() throws IOException {
        Lloyd lloyd = createLloyd("T | 0 | inspect the site");

        assertEquals(LloydMood.DELIGHTED, lloyd.getReply("reminder").mood());
    }

    /** Verifies that a failed save makes Lloyd alarmed. */
    @Test
    public void getReply_unsavableTodo_returnsAlarmedMood() throws IOException {
        Lloyd lloyd = createLloyd("");

        assertEquals(
                LloydMood.ALARMED,
                lloyd.getReply("todo compare A | B").mood());
    }

    /** Creates a Lloyd instance backed by the supplied storage contents. */
    private Lloyd createLloyd(String storageContents) throws IOException {
        Path storagePath = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(storagePath, storageContents, StandardCharsets.UTF_8);
        return new Lloyd(storagePath, FIXED_CLOCK);
    }
}
