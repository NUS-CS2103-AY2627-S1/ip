package orbit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class OrbitTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    void getResponse_taskWorkflow_returnsResponsesAndUpdatesList() {
        Orbit orbit = createOrbit();

        assertEquals("Mission logged:\n"
                + "  [T][ ] read book\n"
                + "Flight plan now contains 1 mission.", orbit.getResponse("todo read book"));
        assertEquals("Here is your flight plan:\n1.[T][ ] read book", orbit.getResponse("list"));
        assertEquals("Mission accomplished! Marked as complete:\n  [T][X] read book", orbit.getResponse("mark 1"));
        assertEquals("Scanner found these matching missions:\n- [T][X] read book",
                orbit.getResponse("find book"));
        assertEquals("Mission scrubbed from the flight plan:\n"
                + "  [T][X] read book\n"
                + "Flight plan now contains 0 missions.", orbit.getResponse("delete 1"));
    }

    @Test
    void getResponse_scheduleCommand_returnsTasksOnRequestedDate() {
        Orbit orbit = createOrbit();
        orbit.getResponse("todo read book");
        orbit.getResponse("deadline return book /by 2019-12-02");
        orbit.getResponse("event camp /from 2019-12-01 /to 2019-12-03");

        assertEquals("Mission timeline for Dec 2 2019:\n"
                + "- [D][ ] return book (by: Dec 2 2019)\n"
                + "- [E][ ] camp (from: Dec 1 2019 to: Dec 3 2019)",
                orbit.getResponse("schedule 2019-12-02"));
    }

    @Test
    void getResponse_invalidAndByeCommands_returnsErrorsAndTracksExit() {
        Orbit orbit = createOrbit();

        assertEquals("Orbit online.\nMission control is ready. What shall we accomplish?",
                orbit.getWelcomeMessage());
        assertFalse(orbit.isExit());
        assertEquals("Every mission needs an objective. Add a description after todo.", orbit.getResponse("todo"));
        assertTrue(orbit.isLastResponseError());
        assertEquals("Command not recognized by mission control. "
                + "Try todo, deadline, event, list, find, schedule, mark, unmark, delete, or bye.",
                orbit.getResponse("unknown"));
        assertTrue(orbit.isLastResponseError());
        assertEquals("Orbit signing off. Clear skies!", orbit.getResponse("bye"));
        assertFalse(orbit.isLastResponseError());
        assertTrue(orbit.isExit());
    }

    @Test
    void constructorOrGetResponse_nullInternalInput_throwsAssertionError() {
        Orbit orbit = createOrbit();

        assertThrows(AssertionError.class, () -> new Orbit(null));
        assertThrows(AssertionError.class, () -> orbit.getResponse(null));
    }

    private Orbit createOrbit() {
        return new Orbit(temporaryDirectory.resolve("data/orbit.txt").toString());
    }
}
