package jaku;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import jaku.storage.Storage;

/** Verifies Jaku command outcomes through its UI-independent service API. */
class JakuCommandTest {
    /** Verifies ordinary task commands share one persistent session and reply category. */
    @Test
    void processCommand_managesOrdinaryTasks(@TempDir Path temporaryDirectory) {
        Jaku jaku = new Jaku(new Storage(temporaryDirectory.resolve("jaku.txt")));

        CommandResult todoResult = jaku.processCommand("  todo read book  ");
        assertEquals(MessageKind.REPLY, todoResult.kind());
        assertTrue(todoResult.text().contains("[T][ ] read book"));
        assertTrue(jaku.getResponse("deadline return book /by 2026-09-20").contains("[D][ ] return book"));
        assertTrue(jaku.getResponse("event discuss book /from 14:00 /to 15:00").contains("[E][ ] discuss book"));

        assertTrue(jaku.getResponse("find BOOK").contains("read book"));
        assertTrue(jaku.getResponse("mark 1").contains("[T][X] read book"));
        assertTrue(jaku.getResponse("unmark 1").contains("[T][ ] read book"));
        assertTrue(jaku.getResponse("delete 2").contains("return book"));
        assertTrue(jaku.getResponse("list").contains("discuss book"));
    }

    /** Verifies command errors are categorised and do not add malformed tasks. */
    @Test
    void processCommand_rejectsMalformedParametersWithoutMutation(@TempDir Path temporaryDirectory) {
        Jaku jaku = new Jaku(new Storage(temporaryDirectory.resolve("jaku.txt")));

        CommandResult result = jaku.processCommand("deadline report /by 2026-09-20 /by 2026-09-21");

        assertEquals(MessageKind.ERROR, result.kind());
        assertTrue(result.text().contains("Use: deadline"));
        assertTrue(jaku.getResponse("list").contains("empty"));
    }

    /** Verifies the farewell result also records the request to exit. */
    @Test
    void processCommand_byeRequestsExit(@TempDir Path temporaryDirectory) {
        Jaku jaku = new Jaku(new Storage(temporaryDirectory.resolve("jaku.txt")));

        assertEquals(MessageKind.REPLY, jaku.processCommand("bye").kind());
        assertTrue(jaku.isExitRequested());
    }

    /** Verifies an invalid saved record stays untouched while recovery mode blocks mutation. */
    @Test
    void processCommand_recoveryModePreservesUnreadableData(@TempDir Path temporaryDirectory) throws IOException {
        Path dataFile = temporaryDirectory.resolve("jaku.txt");
        Files.writeString(dataFile, "INVALID");
        Jaku jaku = new Jaku(new Storage(dataFile));

        assertTrue(jaku.getStartupNotice().isPresent());
        assertEquals(MessageKind.ERROR, jaku.getStartupNotice().orElseThrow().kind());
        assertTrue(jaku.processCommand("todo replace data").text().contains("recovery mode"));
        assertEquals("INVALID", Files.readString(dataFile));
        assertFalse(jaku.getStartupNotice().orElseThrow().text().isBlank());
    }
}
