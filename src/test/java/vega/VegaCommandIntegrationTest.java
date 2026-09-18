package vega;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class VegaCommandIntegrationTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    public void taskCommands_completeWorkflow_updatesAndPersistsTasks() {
        String taskFile = temporaryDirectory.resolve("tasks.txt").toString();
        Vega firstSession = new Vega(taskFile);

        assertTrue(firstSession.getResponse("todo read book").contains("[T][ ] read book"));
        String deadlineResponse = firstSession.getResponse("deadline submit report /by 2026-09-18");
        String eventResponse = firstSession.getResponse("event meeting /from 2pm /to 4pm");
        assertTrue(deadlineResponse.contains("[D][ ] submit report (by: Sep 18 2026)"));
        assertTrue(eventResponse.contains("[E][ ] meeting (from: 2pm to: 4pm)"));
        assertTrue(firstSession.getResponse("mark 1").contains("[T][X] read book"));
        assertTrue(firstSession.getResponse("find REPORT").contains("submit report"));

        Vega restartedSession = new Vega(taskFile);
        String restoredTasks = restartedSession.getResponse("list");
        assertTrue(restoredTasks.contains("[T][X] read book"));
        assertTrue(restoredTasks.contains("[D][ ] submit report"));
        assertTrue(restoredTasks.contains("[E][ ] meeting"));
        assertTrue(restartedSession.getResponse("delete 2").contains("removed this task"));
        assertFalse(restartedSession.getResponse("list").contains("submit report"));
    }

    @Test
    public void invalidCommands_commonInputErrors_returnHelpfulMessagesAndKeepRunning() {
        Vega vega = new Vega(temporaryDirectory.resolve("tasks.txt").toString());

        assertTrue(vega.getResponse("todo").contains("needs a description"));
        assertTrue(vega.getResponse("deadline report /by 2026-02-30").contains("yyyy-MM-dd"));
        assertTrue(vega.getResponse("event meeting /from 2pm").contains("needs a description"));
        assertTrue(vega.getResponse("mark abc").contains("valid task number"));
        assertTrue(vega.getResponse("delete 1").contains("no task numbered 1"));
        assertTrue(vega.getResponse("unknown").contains("don't recognise that command"));
        assertFalse(vega.isExitRequested());
    }

    @Test
    public void bye_validCommand_requestsExit() {
        Vega vega = new Vega(temporaryDirectory.resolve("tasks.txt").toString());

        assertTrue(vega.getResponse("bye").contains("Hope to see you again"));
        assertTrue(vega.isExitRequested());
    }
}
