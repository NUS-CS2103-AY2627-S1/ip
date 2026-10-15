package sherlock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests the response API shared by Sherlock's text and graphical interfaces. */
class SherlockTest {
    @TempDir
    private Path temporaryFolder;

    @Test
    void getResponse_addThenList_returnsUpdatedTaskList() {
        Sherlock sherlock = new Sherlock(temporaryFolder.resolve("tasks.txt"));

        assertEquals("A new case has been entered into the casebook:\n  [T][ ] inspect footprint",
                sherlock.getResponse("todo inspect footprint"));
        assertTrue(sherlock.getResponse("list").contains("1. [T][ ] inspect footprint"));
    }

    @Test
    void getResponse_bye_returnsFarewell() {
        Sherlock sherlock = new Sherlock(temporaryFolder.resolve("tasks.txt"));

        assertEquals("The casebook is closed. Until our next investigation!", sherlock.getResponse("bye"));
    }

    @Test
    void getResponse_help_returnsCommandGuide() {
        Sherlock sherlock = new Sherlock(temporaryFolder.resolve("tasks.txt"));

        String response = sherlock.getResponse("help");

        assertTrue(response.contains("todo DESCRIPTION"));
        assertTrue(response.contains("deadline DESCRIPTION /by yyyy-MM-dd"));
        assertTrue(response.contains("help - show this command guide"));
    }

    @Test
    void getResponse_deadlineWithImpossibleDate_returnsFormatError() {
        Sherlock sherlock = new Sherlock(temporaryFolder.resolve("tasks.txt"));

        assertEquals("⚠ The trail has gone cold: "
                        + "Enter deadline dates in yyyy-MM-dd format, for example 2019-10-15.",
                sherlock.getResponse("deadline inspect scene /by 2026-02-30"));
    }

    @Test
    void isErrorResponse_distinguishesErrorsFromSuccessfulResponses() {
        Sherlock sherlock = new Sherlock(temporaryFolder.resolve("tasks.txt"));

        assertTrue(sherlock.isErrorResponse(sherlock.getResponse("unknown clue")));
        assertFalse(sherlock.isErrorResponse(sherlock.getResponse("list")));
    }
}
