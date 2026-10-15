package otaku;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class OtakuTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    public void getResponse_validAndInvalidCommands_preservesStateAfterError() {
        Otaku otaku = new Otaku(temporaryDirectory.resolve("otaku.txt"));

        assertTrue(otaku.getResponse("todo read book").contains("[T][ ] read book"));
        assertEquals(" Please give a whole task number after `mark`.", otaku.getResponse("mark one"));
        assertEquals(" Your quest log:\n1.[T][ ] read book", otaku.getResponse("list"));
    }

    @Test
    public void getResponse_mutatingCommand_savesForNextInstance() {
        Path dataFile = temporaryDirectory.resolve("otaku.txt");
        Otaku firstSession = new Otaku(dataFile);
        firstSession.getResponse("deadline submit report /by 2026-09-30");
        firstSession.getResponse("mark 1");

        Otaku secondSession = new Otaku(dataFile);
        assertEquals(" Your quest log:\n"
                + "1.[D][X] submit report (by: Sep 30 2026)", secondSession.getResponse("list"));
    }

    @Test
    public void getCommandResponse_validAndInvalidCommands_reportsErrorStatus() {
        Otaku otaku = new Otaku(temporaryDirectory.resolve("otaku.txt"));

        assertFalse(otaku.getCommandResponse("list").isError());
        assertTrue(otaku.getCommandResponse("unknown").isError());
    }

    @Test
    public void getResponse_malformedCommands_rejectsErrorsWithoutChangingState() {
        Otaku otaku = new Otaku(temporaryDirectory.resolve("otaku.txt"));

        assertTrue(otaku.getResponse("  todo read book  ").contains("Quest accepted!"));
        assertEquals(" That quest is already in your log.", otaku.getResponse("todo read book"));
        assertEquals(" A deadline accepts exactly one `/by` parameter.",
                otaku.getResponse("deadline report /by 2026-09-20 /by 2026-09-21"));
        assertEquals(" An event's end date must be after its start date.",
                otaku.getResponse("event class /from 2026-09-20 /to 2026-09-20"));
        assertEquals(" Your quest log:\n1.[T][ ] read book", otaku.getResponse("  list  "));
    }

    @Test
    public void getCommandResponse_emptyOrNullCommand_reportsHelpfulError() {
        Otaku otaku = new Otaku(temporaryDirectory.resolve("otaku.txt"));

        assertTrue(otaku.getCommandResponse("   ").isError());
        assertTrue(otaku.getCommandResponse(null).isError());
        assertEquals(" I need a command before I can update the quest log.", otaku.getResponse(""));
    }

    @Test
    public void getGreeting_validAndInvalidStorage_includesAppropriateMessage() throws IOException {
        Path validFile = temporaryDirectory.resolve("valid.txt");
        Path invalidFile = temporaryDirectory.resolve("invalid.txt");
        Files.writeString(invalidFile, "not a task", StandardCharsets.UTF_8);

        assertTrue(new Otaku(validFile).getGreeting().contains("Hello! I'm Otaku, your questkeeper."));
        assertTrue(new Otaku(invalidFile).getGreeting().contains("Starting with an empty task list instead."));
    }

    @Test
    public void getResponse_allSuccessfulCommands_updatesAndQueriesTasks() {
        Otaku otaku = new Otaku(temporaryDirectory.resolve("otaku.txt"));

        assertTrue(otaku.getResponse("todo revise").contains("[T][ ] revise"));
        assertTrue(otaku.getResponse("deadline submit /by 2026-10-03").contains("[D][ ] submit"));
        assertTrue(otaku.getResponse("event demo /from 2026-10-01 /to 2026-10-02").contains("[E][ ] demo"));
        assertTrue(otaku.getResponse("find sub").contains("submit"));
        assertTrue(otaku.getResponse("mark 1").contains("[T][X] revise"));
        assertTrue(otaku.getResponse("unmark 1").contains("[T][ ] revise"));
        assertTrue(otaku.getResponse("sort").contains("1.[E][ ] demo"));
        assertTrue(otaku.getResponse("delete 3").contains("Quest retired"));
        assertEquals("Quest log sealed. Mata ne!", otaku.getResponse("bye"));
    }

    @Test
    public void getResponse_invalidCommandFormats_returnsSpecificErrors() {
        Otaku otaku = new Otaku(temporaryDirectory.resolve("otaku.txt"));

        assertErrorContains(otaku, "nonsense", "don't recognize");
        assertErrorContains(otaku, "todo", "description");
        assertErrorContains(otaku, "deadline report", "needs a description and a time");
        assertErrorContains(otaku, "deadline report /by Friday", "yyyy-MM-dd");
        assertErrorContains(otaku, "event meeting /from 2026-10-01", "needs a description");
        assertErrorContains(otaku, "event meeting /from 2026-10-01 /to invalid", "yyyy-MM-dd");
        assertErrorContains(otaku, "event meeting /from 2026-10-02 /to 2026-10-01", "must be after");
        assertErrorContains(otaku, "event x /from 2026-10-01 /from 2026-10-02 /to 2026-10-03",
                "exactly one `/from`");
        assertErrorContains(otaku, "event x /from 2026-10-01 /to 2026-10-02 /to 2026-10-03",
                "exactly one `/to`");
        assertErrorContains(otaku, "sort extra", "does not accept arguments");
    }

    @Test
    public void getResponse_invalidTaskNumbers_returnsSpecificErrors() {
        Otaku otaku = new Otaku(temporaryDirectory.resolve("otaku.txt"));
        otaku.getResponse("todo revise");

        assertErrorContains(otaku, "mark zero", "whole task number");
        assertErrorContains(otaku, "mark 0", "does not exist");
        assertErrorContains(otaku, "unmark 2", "does not exist");
        assertErrorContains(otaku, "delete", "whole task number");
        assertErrorContains(otaku, "delete 2", "does not exist");
        assertTrue(otaku.getResponse("list").contains("1.[T][ ] revise"));
    }

    /** Verifies both the error flag and a useful fragment of an error message. */
    private static void assertErrorContains(Otaku otaku, String command, String messageFragment) {
        Otaku.CommandResponse response = otaku.getCommandResponse(command);

        assertTrue(response.isError());
        assertTrue(response.message().contains(messageFragment));
    }
}
