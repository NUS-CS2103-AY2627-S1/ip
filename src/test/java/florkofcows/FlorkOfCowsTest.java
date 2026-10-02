package florkofcows;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class FlorkOfCowsTest {
    @Test
    public void getResponse_validTodoCommand_succeeds() {
        FlorkOfCows app = new FlorkOfCows();

        String output = app.getResponse("todo buy milk");

        assertFalse(app.isLastCommandError());
        assertTrue(output.contains("added") || output.contains("buy milk"));
    }

    @Test
    public void getResponse_invalidCommand_setsErrorFlag() {
        FlorkOfCows app = new FlorkOfCows();

        String output = app.getResponse("todo");

        assertTrue(app.isLastCommandError());
        assertTrue(output.contains("No todo description") || output.contains("That command blew up"));
    }

    @Test
    public void getResponse_byeCommand_requestsExit() {
        FlorkOfCows app = new FlorkOfCows();

        String output = app.getResponse("bye");

        assertFalse(app.isLastCommandError());
        assertTrue(app.isExitRequested());
        assertTrue(output.contains("See ya!"));
    }
}
