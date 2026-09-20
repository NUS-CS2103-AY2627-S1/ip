package goop;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Verifies invalid input recovery and preservation of saved data through the public facade.
 */
class MoreErrorHandlingTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    void getResponse_whitespace_normalizesCommandsAndDetails() {
        Goop goop = new Goop(temporaryDirectory.resolve("tasks.txt"));

        assertFalse(goop.getResponse("  todo\tread   book  ").getResponse().startsWith("ERROR:"));
        assertFalse(goop.getResponse("\tmark\t1\t").getResponse().startsWith("ERROR:"));
        assertEquals("Here are the tasks in your list:\n1.[T][X] read book",
                goop.getResponse("\u2003list\u00a0").getResponse());
        assertTrue(goop.getResponse("  bye  ").isExit());
    }

    @Test
    void getResponse_invalidInput_returnsErrorAndAllowsRecovery() {
        Goop goop = new Goop(temporaryDirectory.resolve("tasks.txt"));
        String[] invalidInputs = {
            null, "", "   ", "todo bad\nrecord", "todo bad\u0000record", "todo bad\u2028record",
            "list extra", "bye now", "mark @1", "delete 1 1",
            "deadline task /by 1/1/2026 1200 /by 2/1/2026 1200",
            "event task /from Mon /from Tue /to Wed",
            "event task /from Mon /to Tue /to Wed",
            "event task /to Tue /from Mon",
            "event task /from 30/2/2026 1200 /to 1/3/2026 1300",
            "event task /from 2026-09-15 1400 /to 2026-09-15 1300",
            "event task /from 2026-09-15 /to 2026-09-15",
            "event task /from 4pm /to 2pm",
            "event task /from 1400 /to 14:00",
            "event task /from 25:00 /to 26:00",
            "event task /from 2026-09-15 1400 /to 4pm",
            "event task /from Monday /to monday"
        };

        for (String input : invalidInputs) {
            CommandResult result = goop.getResponse(input);
            assertTrue(result.getResponse().startsWith("ERROR:"), String.valueOf(input));
            assertFalse(result.isExit(), String.valueOf(input));
        }
        assertFalse(Files.exists(temporaryDirectory.resolve("tasks.txt")));
        assertEquals("Here are the tasks in your list:", goop.getResponse("list").getResponse());
        assertFalse(goop.getResponse("todo recovery").getResponse().startsWith("ERROR:"));
    }

    @Test
    void getResponse_duplicateTasks_preservesFileRegardlessOfStatusAndPriority() throws IOException {
        Path file = temporaryDirectory.resolve("tasks.txt");
        Goop goop = new Goop(file);
        String[] commands = {
            "todo task",
            "deadline task /by 15/9/2026 1400",
            "event task /from Mon 2pm /to 4pm"
        };
        for (String command : commands) {
            assertFalse(goop.getResponse(command).getResponse().startsWith("ERROR:"));
        }
        goop.getResponse("mark 1");
        goop.getResponse("priority 1 high");
        String before = Files.readString(file);

        for (String command : commands) {
            assertEquals("ERROR: This task already exists. Use list to find it.",
                    goop.getResponse(command).getResponse());
            assertEquals(before, Files.readString(file));
        }
        assertTrue(goop.getResponse("deadline task /by 2026-09-15 1400")
                .getResponse().startsWith("ERROR:"));
        assertFalse(goop.getResponse("deadline task /by 2026-09-16 1400")
                .getResponse().startsWith("ERROR:"));
        assertFalse(goop.getResponse("event task /from Mon 2pm /to 5pm")
                .getResponse().startsWith("ERROR:"));
        goop.getResponse("event numeric /from 15/9/2026 1400 /to 15/9/2026 1600");
        assertEquals("ERROR: This task already exists. Use list to find it.",
                goop.getResponse("event numeric /from 2026-09-15 1400 /to 2026-09-15 1600").getResponse());
        goop.getResponse("delete 1");
        assertFalse(goop.getResponse("todo task").getResponse().startsWith("ERROR:"));
    }

    @Test
    void getResponse_specialCharacters_roundTripsWithoutCorruptingStorage() {
        Path file = temporaryDirectory.resolve("tasks.txt");
        Goop goop = new Goop(file);
        goop.getResponse("todo read | book \\ notes & café");

        assertEquals("Here are the tasks in your list:\n1.[T][ ] read | book \\ notes & café",
                new Goop(file).getResponse("list").getResponse());
    }

    @Test
    void getResponse_corruptFile_blocksSavingUntilRestart() throws IOException {
        Path file = temporaryDirectory.resolve("tasks.txt");
        String corruptData = "T | 0 | valid task\nX | 0 | broken task\n";
        Files.writeString(file, corruptData);
        Goop goop = new Goop(file);

        assertTrue(goop.getWelcomeMessage().contains("invalid at line 2"));
        assertEquals("ERROR: Saving is disabled because saved tasks could not be loaded. "
                + "Fix or move the data file and restart Goop. No changes were made.",
                goop.getResponse("todo replacement").getResponse());
        assertEquals(corruptData, Files.readString(file));
        assertEquals("Here are the tasks in your list:", goop.getResponse("list").getResponse());
        assertTrue(goop.getResponse("bye").isExit());

        Files.writeString(file, "T | 0 | repaired task\n");
        Goop restartedGoop = new Goop(file);
        assertFalse(restartedGoop.getResponse("todo new task").getResponse().startsWith("ERROR:"));
        assertTrue(restartedGoop.getResponse("list").getResponse().contains("repaired task"));
    }
}
