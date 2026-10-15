package lenzabot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import lenzabot.storage.Storage;

class LenZaBotTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    void getResponse_addThenList_returnsStoredTask() {
        LenZaBot lenZaBot = createLenZaBot();

        assertEquals("Filed on your task desk: [T][ ] read book", lenZaBot.getResponse("todo read book"));
        assertEquals("1. [T][ ] read book", lenZaBot.getResponse("list"));
    }

    @Test
    void getResponse_validDeadline_returnsAddedDeadline() {
        LenZaBot lenZaBot = createLenZaBot();

        assertEquals(
                "Filed on your task desk: [D][ ] return book (by: Dec 2 2019, 6:00 PM)",
                lenZaBot.getResponse("deadline return book /by 2/12/2019 1800")
        );
    }

    @Test
    void getResponse_validEvent_returnsAddedEvent() {
        LenZaBot lenZaBot = createLenZaBot();

        assertEquals(
                "Filed on your task desk: [E][ ] project meeting "
                        + "(from: Dec 2 2019, 2:00 PM to: Dec 2 2019, 4:00 PM)",
                lenZaBot.getResponse("event project meeting /from 2/12/2019 1400 /to 2/12/2019 1600")
        );
    }

    @Test
    void getResponse_eventMarkersInWrongOrder_returnsUsageError() {
        LenZaBot lenZaBot = createLenZaBot();

        assertEquals(
                "Oops: use `event <description> /from <start> /to <end>`.",
                lenZaBot.getResponse("event project meeting /to 2/12/2019 1600 /from 2/12/2019 1400")
        );
    }

    @Test
    void getResponse_invalidCommand_returnsUserFacingError() {
        LenZaBot lenZaBot = createLenZaBot();

        assertEquals(
                "Oops: I don't understand the command \"dance\". Try `list` or add a task.",
                lenZaBot.getResponse("dance")
        );
    }

    @Test
    void getResponse_deleteTask_returnsMultilineConfirmation() {
        LenZaBot lenZaBot = createLenZaBot();
        lenZaBot.getResponse("todo read book");

        String expected = String.join(System.lineSeparator(),
                "Noted. I've removed this task:",
                "  [T][ ] read book",
                "Now you have 0 tasks in the list.");
        assertEquals(expected, lenZaBot.getResponse("delete 1"));
    }

    @Test
    void getResponse_emptyList_returnsGuidance() {
        LenZaBot lenZaBot = createLenZaBot();

        assertEquals(
                "Your task desk is clear. Add one with `todo`, `deadline`, or `event`.",
                lenZaBot.getResponse("list")
        );
    }

    @Test
    void getResponse_eventEndsAtStart_returnsChronologyError() {
        LenZaBot lenZaBot = createLenZaBot();

        assertEquals(
                "Oops: an event must end after it starts.",
                lenZaBot.getResponse("event meeting /from 2/12/2019 1600 /to 2/12/2019 1600")
        );
    }

    @Test
    void getResponse_repeatedDeadlineMarker_returnsMarkerError() {
        LenZaBot lenZaBot = createLenZaBot();

        assertEquals(
                "Oops: `deadline` accepts exactly one `/by` value.",
                lenZaBot.getResponse("deadline return book /by 2/12/2019 /by 3/12/2019")
        );
    }

    @Test
    void getResponse_descriptionContainsSaveSeparator_returnsDescriptionError() {
        LenZaBot lenZaBot = createLenZaBot();

        assertEquals(
                "Oops: task descriptions cannot contain ` | `.",
                lenZaBot.getResponse("todo read | return book")
        );
    }

    @Test
    void getResponse_deadlineDescriptionEndsWithPipe_returnsDescriptionError() {
        LenZaBot lenZaBot = createLenZaBot();

        assertEquals(
                "Oops: task descriptions cannot contain ` | `.",
                lenZaBot.getResponse("deadline trailing | /by 2026-09-30")
        );
    }

    @Test
    void getResponse_eventDescriptionEndsWithPipe_returnsDescriptionError() {
        LenZaBot lenZaBot = createLenZaBot();

        assertEquals(
                "Oops: task descriptions cannot contain ` | `.",
                lenZaBot.getResponse("event trailing | /from 2026-09-20 /to 2026-09-21")
        );
    }

    @Test
    void getResponse_findWithoutMatches_returnsExplicitResult() {
        LenZaBot lenZaBot = createLenZaBot();

        assertEquals("No tasks on the desk match \"book\".", lenZaBot.getResponse("find book"));
    }

    @Test
    void getResponse_saveFails_returnsVisibleWarning() throws Exception {
        Path blockingFile = temporaryDirectory.resolve("not-a-directory");
        java.nio.file.Files.writeString(blockingFile, "block child path");
        LenZaBot lenZaBot = new LenZaBot(new Storage(blockingFile.resolve("data.txt")));

        String expected = String.join(System.lineSeparator(),
                "Filed on your task desk: [T][ ] read book",
                "Warning: This change could not be saved to disk.");
        assertEquals(expected, lenZaBot.getResponse("todo read book"));
    }

    @Test
    void getResponse_bye_stopsLenZaBot() {
        LenZaBot lenZaBot = createLenZaBot();

        assertTrue(lenZaBot.isRunning());
        assertEquals("Bye! See ya later.", lenZaBot.getResponse("bye"));
        assertFalse(lenZaBot.isRunning());
    }

    @Test
    void getResponse_byeWithArgument_returnsErrorAndKeepsRunning() {
        LenZaBot lenZaBot = createLenZaBot();

        assertEquals("Oops: `bye` does not take extra text.", lenZaBot.getResponse("bye later"));
        assertTrue(lenZaBot.isRunning());
    }

    private LenZaBot createLenZaBot() {
        return new LenZaBot(new Storage(temporaryDirectory.resolve("data.txt")));
    }
}
