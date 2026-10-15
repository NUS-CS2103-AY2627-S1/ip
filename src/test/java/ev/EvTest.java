package ev;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class EvTest {

    @TempDir
    Path workFolder;

    private Path dataFile() {
        return workFolder.resolve("data").resolve("ev.txt");
    }

    private EV started() {
        EV ev = new EV(dataFile());
        ev.start();
        return ev;
    }

    @Test
    public void start_noSaveFile_greetsAndSaysNothingElse() {
        assertEquals("Hi Peter.\nEV online. What do you need?", new EV(dataFile()).start());
    }

    @Test
    public void start_saveFileWithTasks_tasksAreLoaded() throws IOException {
        Files.createDirectories(dataFile().getParent());
        Files.write(dataFile(), List.of("T | 1 | read book", "T | 0 | water plants"));

        EV ev = started();

        assertEquals("Your list:\n1.[T][X] read book\n2.[T][ ] water plants", ev.getResponse("list"));
    }

    @Test
    public void start_saveFileWithUnreadableLines_warnsAndKeepsTheRest() throws IOException {
        Files.createDirectories(dataFile().getParent());
        Files.write(dataFile(), List.of("T | 1 | read book", "X | 0 | nonsense"));

        String greeting = started().start();

        assertTrue(greeting.contains("Skipped 1 unreadable line(s)"));
    }

    @Test
    public void getResponse_command_answersAndReportsNoError() {
        EV ev = started();
        String response = ev.getResponse("todo read book");

        assertEquals("Added.\n  [T][ ] read book\n1 task.", response);
        assertFalse(ev.isErrorResponse());
        assertFalse(ev.isExit());
    }

    @Test
    public void getResponse_unknownCommand_answersWithTheErrorAndFlagsIt() {
        EV ev = started();
        String response = ev.getResponse("blah");

        assertTrue(response.startsWith("No such command: \"blah\""));
        assertTrue(ev.isErrorResponse());
    }

    @Test
    public void getResponse_errorThenGoodCommand_flagIsClearedAgain() {
        EV ev = started();
        ev.getResponse("blah");
        ev.getResponse("todo read book");

        assertFalse(ev.isErrorResponse());
    }

    @Test
    public void getResponse_bye_marksTheSessionAsOver() {
        EV ev = started();
        assertEquals("", ev.getResponse("bye"));
        assertTrue(ev.isExit());
    }

    @Test
    public void getResponse_changeToTheList_survivesARestart() {
        started().getResponse("todo read book");

        EV later = started();

        assertEquals("Your list:\n1.[T][ ] read book", later.getResponse("list"));
    }

    @Test
    public void getResponse_refusedCommand_leavesTheSaveFileAlone() {
        EV ev = started();
        ev.getResponse("todo read book");
        ev.getResponse("deadline pay rent /by 2019-02-30");

        assertEquals("Your list:\n1.[T][ ] read book", started().getResponse("list"));
    }

    @Test
    public void getFarewell_signsOff() {
        assertEquals("Signing off, Peter.", started().getFarewell());
    }
}
