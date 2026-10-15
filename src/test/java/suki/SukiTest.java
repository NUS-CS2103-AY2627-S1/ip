package suki;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests complete commands through the same entry point used by the GUI. */
public class SukiTest {
    @TempDir
    private Path tempDir;

    /** Returns a chatbot whose test data cannot affect the user's saved tasks. */
    private Suki createSuki() {
        return new Suki(tempDir.resolve("suki.txt").toString());
    }

    @Test
    public void getResponse_validCommand_confirmsTaskWasAdded() {
        String response = createSuki().getResponse("todo read book");
        assertTrue(response.contains("[T][ ] read book"));
    }

    @Test
    public void getResponse_listWithArguments_reportsError() {
        String response = createSuki().getResponse("list extra");
        assertTrue(response.startsWith("OOPS!!!"));
        assertTrue(response.contains("does not take any arguments"));
    }

    @Test
    public void getResponse_eventWithInvalidRange_reportsError() {
        String response = createSuki().getResponse(
                "event meeting /from 2019-10-15 1600 /to 2019-10-15 1400");
        assertTrue(response.startsWith("OOPS!!!"));
        assertTrue(response.contains("end after it starts"));
    }

    @Test
    public void isExitCommand_onlyAcceptsBye() {
        assertTrue(Suki.isExitCommand("  bye  "));
        assertFalse(Suki.isExitCommand("bye now"));
        assertFalse(Suki.isExitCommand("goodbye"));
    }
}
