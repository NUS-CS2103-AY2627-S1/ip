package magnus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import magnus.exception.MagnusException;
import magnus.ui.Ui;

/**
 * Tests Magnus's chess characterization while preserving explicit command outcomes.
 */
public class MagnusPersonalityTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    public void getGreeting_returnsChessThemedIntroduction() {
        assertEquals("Greetings! I'm Magnus, your task tactician.\n"
                + "The board is ready. What shall be our next move?", Ui.getGreeting());
    }

    @Test
    public void getResponse_taskLifecycle_usesChessPhrasesAndStatesExactActions()
            throws MagnusException {
        Magnus magnus = createMagnus();

        assertEquals("\tClock set - I've added this Deadline task:\n\n"
                        + "\t[D][ ] submit report (by: Sep 20, 2026 15:00)\n\n"
                        + "\tYou now have 1 task in the list.",
                magnus.getResponse("deadline submit report /by 20/09/2026 1500"));
        assertEquals("\tThe position is prepared - I've added this Event task:\n\n"
                        + "\t[E][ ] team meeting (from: Sep 21, 2026 09:00 to: Sep 21, 2026 10:30)\n\n"
                        + "\tYou now have 2 tasks in the list.",
                magnus.getResponse("event team meeting /from 21/09/2026 0900 /to 21/09/2026 1030"));
        assertEquals("\tClock check - here are your Deadline tasks for that date:\n\n"
                        + "\t1. [D][ ] submit report (by: Sep 20, 2026 15:00)",
                magnus.getResponse("list_deadline 20/09/2026"));
        assertEquals("\tBoard surveyed - here are your Event tasks in that date range:\n\n"
                        + "\t1. [E][ ] team meeting "
                        + "(from: Sep 21, 2026 09:00 to: Sep 21, 2026 10:30)",
                magnus.getResponse("list_event 21/09/2026 21/09/2026"));
        assertEquals("\tCheckmate for this task - I've marked it as completed:\n\n"
                        + "\t[D][X] submit report (by: Sep 20, 2026 15:00)\n\n"
                        + "\tYou have completed 1 task.",
                magnus.getResponse("mark 1"));
        assertEquals("\tThis piece is back in play - I've marked the task as incomplete:\n\n"
                        + "\t[D][ ] submit report (by: Sep 20, 2026 15:00)\n\n"
                        + "\tYou have completed 0 tasks.",
                magnus.getResponse("unmark 1"));
        assertEquals("\tPiece captured and cleared - I've deleted this task:\n\n"
                        + "\t[E][ ] team meeting (from: Sep 21, 2026 09:00 to: Sep 21, 2026 10:30)\n\n"
                        + "\tYou now have 1 task in the list.",
                magnus.getResponse("delete 2"));
    }

    @Test
    public void getResponseResult_emptyCommand_promptsForMoveAndRemainsAnError()
            throws MagnusException {
        MagnusResponse response = createMagnus().getResponseResult("   ");

        assertEquals("\tYour move - please enter a command.", response.message());
        assertTrue(response.isError());
    }

    /**
     * Creates a Magnus instance with isolated storage for the current test.
     *
     * @return A Magnus instance backed by a temporary data file.
     * @throws MagnusException If the temporary task data cannot be loaded.
     */
    private Magnus createMagnus() throws MagnusException {
        return new Magnus(this.temporaryDirectory.resolve("magnus.txt"));
    }
}
