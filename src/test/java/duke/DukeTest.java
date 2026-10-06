package duke;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Unit tests for the {@link Duke} class.
 */
public class DukeTest {

    @TempDir
    Path tempDir;

    private Duke duke;

    @BeforeEach
    public void setUp() {
        duke = new Duke(tempDir.resolve("test_duke.txt").toString());
    }

    @Test
    public void getResponse_validTodoCommand_returnsAddedMessage() {
        String response = duke.getResponse("todo read book");
        assertTrue(response.contains("Got it. I've added this task:"));
        assertTrue(response.contains("[T][ ] read book"));
    }

    @Test
    public void getResponse_invalidCommand_returnsErrorMessage() {
        String response = duke.getResponse("invalid command");
        assertEquals("OOPS!!! I'm sorry, but I don't know what that means :-(", response);
    }

    @Test
    public void getResponse_emptyTodoDescription_returnsErrorMessage() {
        String response = duke.getResponse("todo");
        assertEquals("OOPS!!! The description of a todo cannot be empty.", response);
    }

    @Test
    public void getResponse_validPlaceCommand_returnsAddedMessage() {
        String response = duke.getResponse("place Jumbo Seafood /details Clark Quay");
        assertTrue(response.contains("Got it. I've added this place:"));
        assertTrue(response.contains("Jumbo Seafood (details: Clark Quay)"));
    }

    @Test
    public void getResponse_deadlineAndEvent_returnsAddedMessage() {
        String dRes = duke.getResponse("deadline submit homework /by 2026-10-15 1800");
        assertTrue(dRes.contains("Got it. I've added this task:"));
        assertTrue(dRes.contains("[D][ ] submit homework"));

        String eRes = duke.getResponse("event career fair /from 2026-10-16 1000 /to 2026-10-16 1600");
        assertTrue(eRes.contains("Got it. I've added this task:"));
        assertTrue(eRes.contains("[E][ ] career fair"));
    }

    @Test
    public void getResponse_markAndUnmarkAndList_returnsExpected() {
        duke.getResponse("todo wash dishes");
        String markRes = duke.getResponse("mark 1");
        assertTrue(markRes.contains("Nice! I've marked this task as done:"));
        assertTrue(markRes.contains("[T][X] wash dishes"));

        String listRes = duke.getResponse("list");
        assertTrue(listRes.contains("1. [T][X] wash dishes"));

        String unmarkRes = duke.getResponse("unmark 1");
        assertTrue(unmarkRes.contains("OK, I've marked this task as not done yet:"));
        assertTrue(unmarkRes.contains("[T][ ] wash dishes"));
    }

    @Test
    public void getResponse_deleteAndUndo_returnsExpected() {
        duke.getResponse("todo wash dishes");
        String deleteRes = duke.getResponse("delete 1");
        assertTrue(deleteRes.contains("Noted. I've removed this task:"));
        assertTrue(deleteRes.contains("Now you have 0 tasks in the list."));

        String undoRes = duke.getResponse("undo");
        assertTrue(undoRes.contains("Got it. I've undone the previous command."));
        assertTrue(undoRes.contains("Now you have 1 tasks in the list."));
    }

    @Test
    public void getResponse_findTask_returnsMatches() {
        duke.getResponse("todo read book");
        duke.getResponse("todo buy bread");

        String findRes = duke.getResponse("find book");
        assertTrue(findRes.contains("Here are the matching tasks in your list:"));
        assertTrue(findRes.contains("read book"));
        org.junit.jupiter.api.Assertions.assertFalse(findRes.contains("buy bread"));
    }

    @Test
    public void getResponse_placesCommands_returnsExpected() {
        duke.getResponse("place Marina Bay Sands /details SkyPark");
        duke.getResponse("place Sentosa");

        String listPlacesRes = duke.getResponse("places");
        assertTrue(listPlacesRes.contains("Marina Bay Sands"));
        assertTrue(listPlacesRes.contains("Sentosa"));

        String findPlacesRes = duke.getResponse("findplace sands");
        assertTrue(findPlacesRes.contains("Marina Bay Sands"));
        org.junit.jupiter.api.Assertions.assertFalse(findPlacesRes.contains("Sentosa"));

        String deletePlaceRes = duke.getResponse("deleteplace 1");
        assertTrue(deletePlaceRes.contains("Noted. I've removed this place:"));
        assertTrue(deletePlaceRes.contains("Marina Bay Sands"));
    }

    @Test
    public void getResponse_byeCommand_returnsGoodbye() {
        String byeRes = duke.getResponse("bye");
        assertEquals("Bye bye.", byeRes);
    }

    @Test
    public void getResponse_nullInput_throwsAssertionError() {
        assertThrows(AssertionError.class, () -> duke.getResponse(null));
    }
}
