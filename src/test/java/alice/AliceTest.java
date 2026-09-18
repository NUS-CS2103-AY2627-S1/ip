package alice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class AliceTest {

    @TempDir
    private Path tempDir;

    private Alice newAlice() {
        return new Alice(tempDir.resolve("alice.txt").toString());
    }

    @Test
    public void getResponse_unknownCommand_reportsErrorAndAddsNoTask() {
        Alice alice = newAlice();

        Response response = alice.getResponse("lsit");

        assertTrue(response.isError());
        assertTrue(response.getText().contains("lsit"));
        assertFalse(alice.getResponse("list").getText().contains("lsit"));
    }

    @Test
    public void getResponse_markWithoutNumber_reportsErrorInsteadOfAddingTask() {
        Alice alice = newAlice();

        Response response = alice.getResponse("mark");

        assertTrue(response.isError());
        assertFalse(alice.getResponse("list").getText().contains("mark"));
    }

    @Test
    public void getResponse_eventWithSwappedMarkers_reportsErrorWithoutCrashing() {
        Alice alice = newAlice();

        Response response = alice.getResponse("event meeting /to 2/12/2019 1600 /from 2/12/2019 1400");

        assertTrue(response.isError());
    }

    @Test
    public void getResponse_validTodo_isNotAnError() {
        Alice alice = newAlice();

        Response response = alice.getResponse("todo read book");

        assertFalse(response.isError());
        assertTrue(alice.getResponse("list").getText().contains("read book"));
    }

    @Test
    public void getResponse_blankInput_reportsError() {
        assertTrue(newAlice().getResponse("   ").isError());
    }

    @Test
    public void getResponse_markOutOfRange_reportsHowManyTasksExist() {
        Alice alice = newAlice();
        alice.getResponse("todo read book");

        Response response = alice.getResponse("mark 5");

        assertTrue(response.isError());
        assertTrue(response.getText().contains("1 task(s)"));
    }

    @Test
    public void getTasks_reflectsCommands_andIsUnmodifiable() {
        Alice alice = newAlice();
        alice.getResponse("todo read book");

        assertEquals(1, alice.getTasks().size());
        assertEquals("read book", alice.getTasks().get(0).getDescription());
        assertThrows(UnsupportedOperationException.class, () -> alice.getTasks().add(new Todo("sneaky")));
    }

    @Test
    public void getTypeName_eachTaskType_isNamedForDisplay() {
        Alice alice = newAlice();
        alice.getResponse("todo read book");
        alice.getResponse("deadline essay /by 2/12/2019 1800");
        alice.getResponse("event talk /from 2/12/2019 1400 /to 2/12/2019 1600");

        assertEquals("Todo", alice.getTasks().get(0).getTypeName());
        assertEquals("Deadline", alice.getTasks().get(1).getTypeName());
        assertEquals("Event", alice.getTasks().get(2).getTypeName());
        assertEquals("", alice.getTasks().get(0).getScheduleSummary());
    }

    @Test
    public void getStartupWarning_cleanDataFile_isEmpty() {
        assertTrue(newAlice().getStartupWarning().isEmpty());
    }

    @Test
    public void getResponse_deleteThenList_removesTheRightTask() {
        Alice alice = newAlice();
        alice.getResponse("todo read book");
        alice.getResponse("todo buy milk");

        Response deleted = alice.getResponse("delete 1");

        assertFalse(deleted.isError());
        assertEquals(1, alice.getTasks().size());
        assertEquals("buy milk", alice.getTasks().get(0).getDescription());
    }

    @Test
    public void getResponse_markThenUnmark_togglesDoneState() {
        Alice alice = newAlice();
        alice.getResponse("todo read book");

        alice.getResponse("mark 1");
        assertTrue(alice.getTasks().get(0).isDone());

        alice.getResponse("unmark 1");
        assertFalse(alice.getTasks().get(0).isDone());
    }

    @Test
    public void getResponse_snoozeDeadline_movesTheDueDate() {
        Alice alice = newAlice();
        alice.getResponse("deadline essay /by 2/12/2019 1800");

        Response response = alice.getResponse("snooze 1 3");

        assertFalse(response.isError());
        assertTrue(response.getText().contains("Dec 5 2019"));
    }

    @Test
    public void getResponse_snoozeNonDeadline_isRejected() {
        Alice alice = newAlice();
        alice.getResponse("todo read book");

        assertTrue(alice.getResponse("snooze 1 3").isError());
    }

    @Test
    public void getResponse_snoozeCompletedDeadline_isRejected() {
        Alice alice = newAlice();
        alice.getResponse("deadline essay /by 2/12/2019 1800");
        alice.getResponse("mark 1");

        assertTrue(alice.getResponse("snooze 1 3").isError());
    }

    @Test
    public void getResponse_find_reportsOnlyMatchingTasks() {
        Alice alice = newAlice();
        alice.getResponse("todo read book");
        alice.getResponse("todo buy milk");

        Response found = alice.getResponse("find book");

        assertTrue(found.getText().contains("read book"));
        assertFalse(found.getText().contains("buy milk"));
    }

    @Test
    public void getResponse_eventEndingBeforeItStarts_isRejected() {
        assertTrue(newAlice().getResponse(
                "event talk /from 2/12/2019 1600 /to 2/12/2019 1400").isError());
    }

    @Test
    public void getResponse_everyCommandThatChangesTasks_persistsAcrossRestarts() {
        String path = tempDir.resolve("persist.txt").toString();
        Alice first = new Alice(path);
        first.getResponse("todo read book");
        first.getResponse("deadline essay /by 2/12/2019 1800");
        first.getResponse("mark 1");
        first.getResponse("snooze 2 1");

        Alice second = new Alice(path);

        assertEquals(2, second.getTasks().size());
        assertTrue(second.getTasks().get(0).isDone());
        assertTrue(second.getResponse("list").getText().contains("Dec 3 2019"));
    }

    @Test
    public void isExit_onlyTheByeCommand_endsTheSession() {
        Alice alice = newAlice();

        assertTrue(alice.isExit("bye"));
        assertFalse(alice.isExit("bye bye"));
        assertFalse(alice.isExit("list"));
    }

    @Test
    public void getResponse_todoSurvivesReload_storedAsTodo() {
        String path = tempDir.resolve("reload.txt").toString();
        new Alice(path).getResponse("todo read book");

        Response listed = new Alice(path).getResponse("list");

        assertEquals(true, listed.getText().contains("[T][ ] read book"));
    }
}
