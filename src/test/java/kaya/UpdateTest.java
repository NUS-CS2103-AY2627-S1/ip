package kaya;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests updates through the public command interface and persisted data. */
public class UpdateTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    public void update_validFields_preservesOtherDetailsAndPersists() {
        Path file = temporaryDirectory.resolve("kaya.txt");
        Kaya kaya = new Kaya(file);
        kaya.getResponse("todo old name");
        kaya.getResponse("deadline report /by 2026-09-20");
        kaya.getResponse("event meeting /from 2026-09-21 /to 2026-09-23");
        kaya.getResponse("mark 1");
        kaya.getResponse("mark 2");
        kaya.getResponse("mark 3");
        String[] updates = {
            "update 1 /description new name",
            "update 2 /description final report",
            "update 2 /by 2026-09-22",
            "update 3 /description team meeting",
            "update 3 /from 2026-09-22",
            "update 3 /to 2026-09-22"
        };
        for (String update : updates) {
            assertTrue(kaya.getResponse(update).startsWith("All sorted. I've updated this task:"));
            assertEquals(kaya.getResponse("list"), new Kaya(file).getResponse("list"));
        }
        assertEquals("Here's what's on your plate:\n1.[T][X] new name"
                + "\n2.[D][X] final report (by: Sep 22 2026)"
                + "\n3.[E][X] team meeting (from: Sep 22 2026 to: Sep 22 2026)", kaya.getResponse("list"));
    }

    @Test
    public void update_invalidArguments_leavesMemoryAndStorageUnchanged() {
        Path file = temporaryDirectory.resolve("kaya.txt");
        Kaya kaya = new Kaya(file);
        kaya.getResponse("todo read book");
        kaya.getResponse("event meeting /from 2026-09-21 /to 2026-09-23");
        String original = kaya.getResponse("list");
        String[] invalid = {
            "update", "update 1", "update 1 /description", "update 1 /description   ",
            "update 0 /description name", "update 3 /description name", "update abc /description name",
            "update 1 /unknown name", "update 1 /by 2026-09-20", "update 2 /by 2026-09-20",
            "update 2 /from 2026-09-24", "update 2 /to 2026-09-20", "update 2 /to 2026-02-30",
            "update 2 /to tomorrow", "update 2 /to 2026-09-25 /from 2026-09-21"
        };
        for (String update : invalid) {
            assertTrue(kaya.getResponse(update).startsWith("Hmm. "), update);
            assertEquals(original, kaya.getResponse("list"), update);
            assertEquals(original, new Kaya(file).getResponse("list"), update);
        }
    }

    @Test
    public void update_description_preservesIncompleteStatusAndLiteralText() {
        Kaya kaya = new Kaya(temporaryDirectory.resolve("kaya.txt"));
        kaya.getResponse("todo old name");
        assertEquals("All sorted. I've updated this task:\n  [T][ ] notes /by example",
                kaya.getResponse("  update   1   /description   notes /by example  "));
    }
}
