package nori;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import org.junit.jupiter.api.Test;

import nori.testutil.IsolatedSessionTest;
import nori.ui.GuiUi;

/**
 * Tests the command-response boundary used by Nori's graphical interface.
 */
public class NoriGuiTest extends IsolatedSessionTest {
    @Test
    public void executeCommand_addTodo_returnsResponseWithoutExiting() {
        GuiUi guiUi = new GuiUi();
        Nori nori = new Nori(guiUi);

        boolean isExitRequested = nori.executeCommand("todo finish tutorial");

        assertFalse(isExitRequested);
        assertEquals("Noot noot! Task tucked safely under my wing:\n"
                + "  [T][ ] finish tutorial\n"
                + "The iceberg now holds 1 task(s).", guiUi.consumeResponse());
    }

    @Test
    public void hasLoadingError_readableStorage_returnsFalse() {
        Nori nori = new Nori(new GuiUi());

        assertFalse(nori.hasLoadingError());
        assertNull(nori.getLoadingMessage());
    }

    @Test
    public void hasLoadingError_unreadableStorage_returnsTrue() throws IOException {
        Files.writeString(storageDirectory.resolve("nori.txt"), "this is not a task",
                StandardCharsets.UTF_8);

        Nori nori = new Nori(new GuiUi());

        assertTrue(nori.hasLoadingError());
        assertNotNull(nori.getLoadingMessage());
    }

    @Test
    public void executeCommand_validCommand_marksTheResponseAsOrdinary() {
        GuiUi guiUi = new GuiUi();
        Nori nori = new Nori(guiUi);

        nori.executeCommand("todo finish tutorial");

        assertFalse(guiUi.isErrorResponse());
    }

    @Test
    public void executeCommand_unknownCommand_marksTheResponseAsAnError() {
        GuiUi guiUi = new GuiUi();
        Nori nori = new Nori(guiUi);

        nori.executeCommand("xyzzy frobnicate");

        assertTrue(guiUi.isErrorResponse());
    }

    @Test
    public void executeCommand_malformedDeadline_marksTheResponseAsAnError() {
        GuiUi guiUi = new GuiUi();
        Nori nori = new Nori(guiUi);

        nori.executeCommand("deadline submit report");

        assertTrue(guiUi.isErrorResponse());
    }

    @Test
    public void executeCommand_errorThenSuccess_clearsTheErrorMark() {
        GuiUi guiUi = new GuiUi();
        Nori nori = new Nori(guiUi);

        nori.executeCommand("xyzzy frobnicate");
        nori.executeCommand("todo finish tutorial");

        assertFalse(guiUi.isErrorResponse());
    }

    @Test
    public void isErrorResponse_afterConsumingTheResponse_isUnchanged() {
        GuiUi guiUi = new GuiUi();
        Nori nori = new Nori(guiUi);

        nori.executeCommand("xyzzy frobnicate");
        guiUi.consumeResponse();

        assertTrue(guiUi.isErrorResponse());
    }

    @Test
    public void executeCommand_bye_returnsGoodbyeAndRequestsExit() {
        GuiUi guiUi = new GuiUi();
        Nori nori = new Nori(guiUi);

        boolean isExitRequested = nori.executeCommand("bye");

        assertTrue(isExitRequested);
        assertEquals("Noot noot! Time to waddle off. Stay frosty!", guiUi.consumeResponse());
    }

    @Test
    public void executeCommand_multiDayEventQuery_returnsEventOnMiddleDate() {
        GuiUi guiUi = new GuiUi();
        Nori nori = new Nori(guiUi);
        nori.executeCommand("event conference /from 2026-09-01 /to 2026-09-03");
        guiUi.consumeResponse();

        boolean isExitRequested = nori.executeCommand("on 2026-09-02");

        assertFalse(isExitRequested);
        assertEquals("Noot noot! Things hatching on 2026-09-02:\n"
                + "1.[E][ ] conference (from: 2026-09-01 to: 2026-09-03)", guiUi.consumeResponse());
    }

    @Test
    public void executeCommand_reversedEventDates_rejectsEventWithoutSaving() {
        GuiUi guiUi = new GuiUi();
        Nori nori = new Nori(guiUi);

        boolean isExitRequested = nori.executeCommand("event backwards /from 2026-09-03 /to 2026-09-01");

        assertFalse(isExitRequested);
        assertEquals("NOOT?! An event cannot end before it starts. Time only waddles forward.",
                guiUi.consumeResponse());
        nori.executeCommand("list");
        assertEquals("The iceberg is empty. Try \"todo borrow book\". Noot noot!",
                guiUi.consumeResponse());
    }

    @Test
    public void executeCommand_hostileLookingDescription_preservesLiteralTextAfterReload() {
        String description = "<script>alert('x')</script> | ../data & command";
        GuiUi firstGuiUi = new GuiUi();
        Nori firstNori = new Nori(firstGuiUi);
        firstNori.executeCommand("todo " + description);
        firstGuiUi.consumeResponse();

        GuiUi reloadedGuiUi = new GuiUi();
        Nori reloadedNori = new Nori(reloadedGuiUi);
        reloadedNori.executeCommand("list");

        assertEquals("Noot noot! Tasks currently chilling on the iceberg:\n1.[T][ ] " + description,
                reloadedGuiUi.consumeResponse());
    }

    @Test
    public void executeCommand_oversizedTaskNumber_returnsRangeErrorWithoutCrashing() {
        GuiUi guiUi = new GuiUi();
        Nori nori = new Nori(guiUi);
        nori.executeCommand("todo safe task");
        guiUi.consumeResponse();

        boolean isExitRequested = nori.executeCommand("delete 999999999999999999999999999999999999");

        assertFalse(isExitRequested);
        assertEquals("GIANT NOOT! \"999999999999999999999999999999999999\""
                + " is far too large for a task number. Use a whole number from 1 to 1.",
                guiUi.consumeResponse());
    }
}
