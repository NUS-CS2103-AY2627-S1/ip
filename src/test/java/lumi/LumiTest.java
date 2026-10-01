package lumi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests Lumi's responses to graphical-interface input. */
public class LumiTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    public void getResponse_allTaskCommands_commandsExecuteAndPersist() {
        Path dataFile = temporaryDirectory.resolve("data").resolve("lumi.txt");
        Lumi lumi = new Lumi(dataFile);

        assertEquals(" It's on the map. I've added this task:\n"
                        + "   [T][ ] read book\n"
                        + " Your map now holds 1 task.",
                lumi.getResponse("todo read book"));
        assertEquals(" It's on the map. I've added this task:\n"
                        + "   [D][ ] return book (by: Oct 15 2019)\n"
                        + " Your map now holds 2 tasks.",
                lumi.getResponse("deadline return book /by 2019-10-15"));
        assertEquals(" It's on the map. I've added this task:\n"
                        + "   [E][ ] project meeting (from: Oct 16 2019, 2:00PM"
                        + " to: Oct 16 2019, 4:00PM)\n"
                        + " Your map now holds 3 tasks.",
                lumi.getResponse("event project meeting /from 2019-10-16 1400"
                        + " /to 2019-10-16 1600"));
        assertEquals(" A little brighter. This task is complete:\n   [T][X] read book",
                lumi.getResponse("mark 1"));
        assertEquals(" Back in orbit. This task is active again:\n   [T][ ] read book",
                lumi.getResponse("unmark 1"));
        assertEquals(" These tasks match your signal:\n"
                        + " 1.[T][ ] read book\n"
                        + " 2.[D][ ] return book (by: Oct 15 2019)",
                lumi.getResponse("find book"));
        assertEquals(" Cleared from the map. I've removed this task:\n"
                        + "   [D][ ] return book (by: Oct 15 2019)\n"
                        + " Your map now holds 2 tasks.",
                lumi.getResponse("delete 2"));
        assertEquals(" Here's your current constellation:\n"
                        + " 1.[T][ ] read book\n"
                        + " 2.[E][ ] project meeting (from: Oct 16 2019, 2:00PM"
                        + " to: Oct 16 2019, 4:00PM)",
                lumi.getResponse("list"));

        Lumi reloadedLumi = new Lumi(dataFile);
        assertEquals(lumi.getResponse("list"), reloadedLumi.getResponse("list"));
    }

    @Test
    public void getResponse_invalidCommand_errorReturnedWithoutChangingTasks() {
        Lumi lumi = new Lumi(temporaryDirectory.resolve("lumi.txt"));

        assertEquals(" Signal unclear: a todo needs a description. Try: todo <description>",
                lumi.getResponse("todo"));
        assertEquals(" Here's your current constellation:", lumi.getResponse("list"));
    }

    @Test
    public void getResponse_snoozeDatedTasks_schedulesAndStatusPersist() {
        Path dataFile = temporaryDirectory.resolve("lumi.txt");
        Lumi lumi = new Lumi(dataFile);
        lumi.getResponse("todo read book");
        lumi.getResponse("deadline submit report /by 2019-10-15");
        lumi.getResponse("event meeting /from 2019-10-16 1400 /to 2019-10-16 1600");
        lumi.getResponse("mark 2");

        assertEquals(" Orbit adjusted. I've rescheduled this task:\n"
                        + "   [D][X] submit report (by: Oct 20 2019, 6:00PM)",
                lumi.getResponse("snooze 2 /to 2019-10-20 1800"));
        assertEquals(" Orbit adjusted. I've rescheduled this task:\n"
                        + "   [E][ ] meeting (from: Oct 21 2019, 3:00PM"
                        + " to: Oct 21 2019, 5:00PM)",
                lumi.getResponse("snooze 3 /to 2019-10-21 1500"));
        assertEquals(" Signal unclear: only deadlines and events can be snoozed.",
                lumi.getResponse("snooze 1 /to 2019-10-22"));

        Lumi reloadedLumi = new Lumi(dataFile);
        assertEquals(" Here's your current constellation:\n"
                        + " 1.[T][ ] read book\n"
                        + " 2.[D][X] submit report (by: Oct 20 2019, 6:00PM)\n"
                        + " 3.[E][ ] meeting (from: Oct 21 2019, 3:00PM"
                        + " to: Oct 21 2019, 5:00PM)",
                reloadedLumi.getResponse("list"));
    }

    @Test
    public void getResponse_bye_farewellReturnedAndSessionEnded() {
        Lumi lumi = new Lumi(temporaryDirectory.resolve("lumi.txt"));

        assertTrue(lumi.isRunning());
        assertEquals(" Until next time. Your tasks are safe here.",
                lumi.getResponse("bye"));
        assertFalse(lumi.isRunning());
    }

    @Test
    public void constructor_corruptedStoredLine_validTaskLoadedAndWarningAvailable() throws IOException {
        Path dataFile = temporaryDirectory.resolve("lumi.txt");
        Files.writeString(dataFile, "T | 1 | read book\ncorrupted record\n");
        Lumi lumi = new Lumi(dataFile);

        assertEquals("I found 1 unreadable line(s) in your saved tasks and skipped them.",
                lumi.getStartupMessage());
        assertEquals(" Here's your current constellation:\n 1.[T][X] read book",
                lumi.getResponse("list"));
    }

    @Test
    public void constructor_dataPathIsDirectory_environmentErrorsReportedWithoutCrash()
            throws IOException {
        Path dataPath = temporaryDirectory.resolve("lumi.txt");
        Files.createDirectory(dataPath);

        Lumi lumi = new Lumi(dataPath);

        assertEquals("I couldn't open your saved tasks, so we're starting with a clear map.",
                lumi.getStartupMessage());
        assertEquals(" It's on the map. I've added this task:\n"
                        + "   [T][ ] read book\n"
                        + " Your map now holds 1 task.\n"
                        + " Signal unclear: I couldn't save the latest task changes.",
                lumi.getResponse("todo read book"));
    }
}
