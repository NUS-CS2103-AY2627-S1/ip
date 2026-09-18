package bibi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;

import bibi.task.Todo;

/**
 * Tests the capture mode the GUI depends on, and the wording that goes through
 * it. The console path writes to standard output and is covered instead by the
 * scripted tests in test/ui-test-plan.md.
 */
public class UiTest {

    private static Ui createCapturingUi() {
        Ui ui = new Ui();
        ui.startCapture();
        return ui;
    }

    @Test
    public void takeCapturedReply_withoutStartCapture_assertionThrown() {
        // Returning the previous capture would put a stale reply on screen.
        assertThrows(AssertionError.class, () -> new Ui().takeCapturedReply());
    }

    @Test
    public void startCapture_afterAPreviousCapture_startsEmpty() {
        Ui ui = createCapturingUi();
        ui.showMessage("first");
        ui.takeCapturedReply();

        ui.startCapture();
        ui.showMessage("second");

        assertEquals("second", ui.takeCapturedReply().text());
    }

    @Test
    public void showMessage_whileCapturing_omitsTheSpeakerPrefix() {
        Ui ui = createCapturingUi();
        ui.showMessage("hello");

        // The window puts Bibi's icon beside every reply, so the console's
        // "Bibi: " would only be noise repeated down the transcript.
        assertEquals("hello", ui.takeCapturedReply().text());
    }

    @Test
    public void showDetails_severalLines_eachIndentedInOrder() {
        Ui ui = createCapturingUi();
        ui.showDetails("one", "two", "three");

        assertEquals("  one\n  two\n  three", ui.takeCapturedReply().text());
    }

    @Test
    public void showDetails_noLines_addsNothing() {
        Ui ui = createCapturingUi();
        ui.showMessage("only this");
        ui.showDetails();

        assertEquals("only this", ui.takeCapturedReply().text());
    }

    @Test
    public void showNumberedTask_anyTask_prefixesTheNumber() throws BibiException {
        Ui ui = createCapturingUi();
        ui.showNumberedTask(7, new Todo("read book"));

        assertEquals("7. [T][ ] read book", ui.takeCapturedReply().text());
    }

    @Test
    public void showPlain_anyText_passesItThroughUnchanged() {
        Ui ui = createCapturingUi();
        ui.showPlain("no prefix, no indent");

        assertEquals("no prefix, no indent", ui.takeCapturedReply().text());
    }

    @Test
    public void showError_anyMessage_flagsTheReply() {
        Ui ui = createCapturingUi();
        ui.showError("something went wrong");

        Reply reply = ui.takeCapturedReply();
        assertTrue(reply.isError());
        assertEquals("something went wrong", reply.text());
    }

    @Test
    public void showMessage_ordinaryOutput_notFlaggedAsError() {
        Ui ui = createCapturingUi();
        ui.showMessage("all good");

        assertFalse(ui.takeCapturedReply().isError());
    }

    @Test
    public void showError_followedByANewCapture_flagIsCleared() {
        Ui ui = createCapturingUi();
        ui.showError("bad");
        ui.takeCapturedReply();

        ui.startCapture();
        ui.showMessage("fine");

        assertFalse(ui.takeCapturedReply().isError());
    }

    @Test
    public void showWelcome_always_namesBibiAndPointsAtTheCommands() {
        Ui ui = createCapturingUi();
        ui.showWelcome();

        String text = ui.takeCapturedReply().text();
        assertTrue(text.contains("I'm Bibi."));
        assertTrue(text.contains("todo"));
        assertTrue(text.contains("help"));
    }

    @Test
    public void showHelp_always_listsEveryCommand() {
        Ui ui = createCapturingUi();
        ui.showHelp();

        String text = ui.takeCapturedReply().text();
        // "remove|delete" is the help entry verbatim, so a bare "delete" in the
        // description below it cannot satisfy this on its own.
        for (String command : new String[] {"todo", "deadline", "event", "list", "sort",
            "find", "on", "mark", "unmark", "remove|delete", "undo", "help", "bye",
            "hi", "hello", "hey", "thanks"}) {
            assertTrue(text.contains(command), "help omitted " + command);
        }
    }

    @Test
    public void showBanner_always_drawsTheName() {
        Ui ui = createCapturingUi();
        ui.showBanner();

        assertTrue(ui.takeCapturedReply().text().contains("B B B B"));
    }

    @Test
    public void showGoodbye_always_saysTheTasksAreKept() {
        Ui ui = createCapturingUi();
        ui.showGoodbye();

        assertTrue(ui.takeCapturedReply().text().contains("Your saved tasks will be here next time."));
    }

    @Test
    public void showLoaded_oneTask_usesTheSingular() {
        Ui ui = createCapturingUi();
        ui.showLoaded(1);

        assertTrue(ui.takeCapturedReply().text().contains("1 task restored"));
    }

    @Test
    public void showLoaded_severalTasks_usesThePlural() {
        Ui ui = createCapturingUi();
        ui.showLoaded(4);

        assertTrue(ui.takeCapturedReply().text().contains("4 tasks restored"));
    }

    @Test
    public void showLoadWarnings_damagedLines_listsThemAndFlagsAnError() {
        Ui ui = createCapturingUi();
        ui.showLoadWarnings(List.of("Line 2: nonsense", "Line 5: also nonsense"));

        Reply reply = ui.takeCapturedReply();
        assertTrue(reply.isError());
        assertTrue(reply.text().contains("Line 2: nonsense"));
        assertTrue(reply.text().contains("Line 5: also nonsense"));
    }

    @Test
    public void showLoadError_anyFailure_namesTheFileAndFlagsAnError() {
        Ui ui = createCapturingUi();
        ui.showLoadError(Path.of("data", "bibi.txt"), new NoSuchFileException("bibi.txt"));

        Reply reply = ui.takeCapturedReply();
        assertTrue(reply.isError());
        assertTrue(reply.text().contains("bibi.txt"));
        assertTrue(reply.text().contains("NoSuchFileException"));
    }

    @Test
    public void showSaveError_anyFailure_warnsAndFlagsAnError() {
        Ui ui = createCapturingUi();
        ui.showSaveError(Path.of("data", "bibi.txt"), new IOException("disk full"));

        // The command itself worked, but losing the session's changes is not
        // something the user should mistake for a routine confirmation.
        Reply reply = ui.takeCapturedReply();
        assertTrue(reply.isError());
        assertTrue(reply.text().contains("bibi.txt"));
        assertTrue(reply.text().contains("disk full"));
        assertTrue(reply.text().contains("The change is applied in memory but isn't saved."));
        assertTrue(reply.text().contains("Don't repeat it."));
    }

    @Test
    public void showHelp_always_groupsTheCommandsInOrder() {
        Ui ui = createCapturingUi();
        ui.showHelp();

        String text = ui.takeCapturedReply().text();
        for (String group : new String[] {"Add", "View", "Update", "Session"}) {
            assertTrue(text.contains("  " + group + "\n"), "help omitted group " + group);
        }
        assertTrue(text.indexOf("Add\n") < text.indexOf("View\n"));
        assertTrue(text.indexOf("View\n") < text.indexOf("Update\n"));
        assertTrue(text.indexOf("Update\n") < text.indexOf("Session\n"));
    }

    @Test
    public void showHelp_always_staysShortAndPointsAtTheExamples() {
        Ui ui = createCapturingUi();
        ui.showHelp();

        String text = ui.takeCapturedReply().text();
        assertTrue(text.contains("Usage: <command> [arguments]"));
        assertTrue(text.contains("Run help --examples"));

        // The reference detail belongs behind the flag. Keeping it out is the
        // point of the short help, so its absence is asserted rather than assumed.
        assertFalse(text.contains("d/M/yy uses 00-99 for 2000-2099."));
        assertFalse(text.contains("Ctrl+C (Cmd+C on macOS)"));
    }

    @Test
    public void showHelp_always_alignsTheDescriptionColumn() {
        Ui ui = createCapturingUi();
        ui.showHelp();

        // The synopses are padded so the reply scans like a command-line tool's
        // help; an unpadded row would leave the descriptions ragged.
        String text = ui.takeCapturedReply().text();
        int todoColumn = text.indexOf("add an undated task");
        int listColumn = text.indexOf("show every task with its number");
        assertEquals(todoColumn - text.lastIndexOf('\n', todoColumn),
                listColumn - text.lastIndexOf('\n', listColumn));
    }

    @Test
    public void showHelpExamples_always_explainsDatesNumberingAndKeyboard() {
        Ui ui = createCapturingUi();
        ui.showHelpExamples();

        String text = ui.takeCapturedReply().text();
        assertTrue(text.contains("deadline return book /by 21/12/2026 1800"));
        assertTrue(text.contains("d/M/yy uses 00-99 for 2000-2099."));
        assertTrue(text.contains("Numeric dates are always day first."));
        assertTrue(text.contains("Existing yyyy-MM-dd input still works."));
        assertTrue(text.contains("Sort and remove/delete change them"));
        assertTrue(text.contains("Up/Down recall commands without sending"));
        assertTrue(text.contains("restores your draft"));
        assertTrue(text.contains("Ctrl+C (Cmd+C on macOS)"));
    }

    @Test
    public void describeCount_oneAndMany_pickTheRightPlural() {
        assertEquals("0 tasks", Ui.describeCount(0));
        assertEquals("1 task", Ui.describeCount(1));
        assertEquals("2 tasks", Ui.describeCount(2));
    }
}
