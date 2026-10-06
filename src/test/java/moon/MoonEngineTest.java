package moon;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** Tests Moon's user-interface-independent command engine. */
public class MoonEngineTest {
    /** Verifies that a newly created engine can report its loading status. */
    @Test
    public void constructor_loadsValidSavedData() {
        MoonEngine engine = new MoonEngine();

        assertFalse(engine.hasLoadingError());
    }

    /** Verifies that the engine handles a command without a required keyword. */
    @Test
    public void getResponse_findWithoutKeyword_returnsHelpfulError() {
        MoonEngine engine = new MoonEngine();

        String response = engine.getResponse("find");

        assertTrue(response.contains("your find needs a keyword."));
    }

    /** Verifies that missing and whitespace-only commands receive a useful error. */
    @Test
    public void getResponse_missingCommand_reportsHelpfulError() {
        MoonEngine engine = new MoonEngine();

        assertTrue(engine.getResponse(null).contains("Please enter a command"));
        assertTrue(engine.getResponse("   ").contains("Please enter a command"));
    }

    /** Verifies that harmless extra whitespace is normalized before processing. */
    @Test
    public void getResponse_extraWhitespace_stillRecognizesCommand() {
        MoonEngine engine = new MoonEngine();

        assertEquals("Aight, catch you later ✌️", engine.getResponse("  bye   "));
    }

    /** Verifies that an event cannot end before it starts. */
    @Test
    public void getResponse_eventWithInvalidDateRange_reportsHelpfulError() {
        MoonEngine engine = new MoonEngine();

        String response = engine.getResponse("event concert /from 2026-09-20 /to 2026-09-20");

        assertTrue(response.contains("/from date must be before its /to date"));
    }

    /** Verifies that duplicate command parameters are rejected. */
    @Test
    public void getResponse_duplicateDeadlineParameter_reportsHelpfulError() {
        MoonEngine engine = new MoonEngine();

        String response = engine.getResponse("deadline exam /by 2026-09-20 /by 2026-09-21");

        assertTrue(response.contains("only have one /by parameter"));
    }

    /** Verifies that the GUI can display Moon's farewell response. */
    @Test
    public void getResponse_bye_returnsFarewell() {
        MoonEngine engine = new MoonEngine();

        assertEquals("Aight, catch you later ✌️", engine.getResponse("bye"));
    }

    /** Verifies that undo reports clearly when no previous mutation exists. */
    @Test
    public void getResponse_undoWithoutPreviousCommand_reportsNoAction() {
        MoonEngine engine = new MoonEngine();

        assertTrue(engine.getResponse("undo").contains("No previous move to undo"));
    }
}
