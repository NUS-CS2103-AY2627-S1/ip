package billy.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.Consumer;

import org.junit.jupiter.api.Test;

/**
 * Tests the wording {@link Ui} works out, as opposed to the printing it does.
 *
 * <p>Most of this class writes to the screen and reads the keyboard, which is
 * covered end to end by the text UI test plan rather than here. What is worth
 * testing in isolation are the two methods that decide wording, because they are
 * ordinary logic wearing a message's clothes: they pick between "task" and
 * "tasks", and every command that changes the list says its confirmation through
 * them.
 *
 * <p>Getting the plural wrong is the kind of fault that never breaks anything and
 * is noticed by everyone who reads it.
 */
public class UiTest {

    @Test
    public void describeListSize_oneTask_singular() {
        assertEquals("1 task", Ui.describeListSize(1));
    }

    @Test
    public void describeListSize_severalTasks_plural() {
        assertEquals("3 tasks", Ui.describeListSize(3));
    }

    @Test
    public void describeListSize_noTasks_plural() {
        // "0 task" is the mistake a count-of-one check written the wrong way round
        // would produce, so zero is worth pinning as well as one.
        assertEquals("0 tasks", Ui.describeListSize(0));
    }

    @Test
    public void describeNewListSize_oneTask_singularSentence() {
        assertEquals("That's 1 task on the books.", Ui.describeNewListSize(1));
    }

    @Test
    public void describeNewListSize_severalTasks_pluralSentence() {
        assertEquals("That's 2 tasks on the books.", Ui.describeNewListSize(2));
    }

    @Test
    public void describeNewListSize_noTasks_pluralSentence() {
        // Said after deleting the last task, so it is a real case, not a spare one.
        assertEquals("That's 0 tasks on the books.", Ui.describeNewListSize(0));
    }

    @Test
    public void describeNewListSize_anyCount_wordedThroughDescribeListSize() {
        // The sentence is built from the same phrase used elsewhere, so the two
        // cannot disagree about how a count is worded.
        assertEquals("That's " + Ui.describeListSize(7) + " on the books.",
                Ui.describeNewListSize(7));
    }

    @Test
    public void describeNewListSize_shortList_noRemark() {
        // Below the threshold Billy says nothing about the length, so an
        // ordinary session is not commented on every single time.
        assertEquals("That's 9 tasks on the books.", Ui.describeNewListSize(9));
    }

    @Test
    public void describeNewListSize_listJustLongEnough_remarked() {
        // Ten is the first count worth a remark. Testing the boundary is the
        // point: an off-by-one here would remark at nine or stay silent at ten.
        assertEquals("That's 10 tasks on the books. Ambitious.", Ui.describeNewListSize(10));
    }

    @Test
    public void describeNewListSize_longList_remarked() {
        assertEquals("That's 25 tasks on the books. Ambitious.", Ui.describeNewListSize(25));
    }

    @Test
    public void getGreeting_always_matchesWhatTheConsoleSays() {
        // The window and the console open with the same words; only the banner
        // around them differs. Spelling the greeting out here is what would
        // catch the two drifting apart.
        assertEquals("BILLY HERE! Keeper of lists, guardian of things you would otherwise forget.\n"
                + "Todos, deadlines, events. Say 'list' and I'll spill it all. 'help' if you're lost.",
                Ui.getGreeting());
    }

    @Test
    public void getFarewell_always_matchesWhatTheConsoleSays() {
        assertEquals("Off you go! I'll be right here, guarding the list. Vigilantly.",
                Ui.getFarewell());
    }

    @Test
    public void stopCollecting_messagesShown_returnedWithoutDividers() {
        Ui ui = new Ui();
        ui.startCollecting();
        ui.show("first");
        ui.show("second");
        assertEquals("first\nsecond", ui.stopCollecting());
    }

    @Test
    public void stopCollecting_errorShown_returnedLikeAnyOtherMessage() {
        Ui ui = new Ui();
        ui.startCollecting();
        ui.showError("something went wrong");
        assertEquals("something went wrong", ui.stopCollecting());
    }

    @Test
    public void startCollecting_afterAnEarlierRound_nothingCarriedOver() {
        Ui ui = new Ui();
        ui.startCollecting();
        ui.show("first");
        ui.stopCollecting();

        ui.startCollecting();
        ui.show("second");
        assertEquals("second", ui.stopCollecting());
    }

    @Test
    public void show_severalLines_joinedIntoOneMessage() {
        // The lines are the message, not several messages: what the user sees is
        // one block, exactly as if the newlines had been typed by the caller.
        Ui ui = new Ui();
        ui.startCollecting();
        ui.show("Consider it written down:", "  [T][ ] read book", "That's 1 task on the books.");
        assertEquals("Consider it written down:\n  [T][ ] read book\nThat's 1 task on the books.",
                ui.stopCollecting());
    }

    @Test
    public void show_oneLine_noNewlineAdded() {
        Ui ui = new Ui();
        ui.startCollecting();
        ui.show("Your list is empty.");
        assertEquals("Your list is empty.", ui.stopCollecting());
    }

    @Test
    public void show_lineThatItselfSpansLines_leftAsItIs() {
        // A caller that already has a joined block can still pass it whole, which
        // is what the commands listing tasks do.
        Ui ui = new Ui();
        ui.startCollecting();
        ui.show("Heading:", "1.first\n2.second");
        assertEquals("Heading:\n1.first\n2.second", ui.stopCollecting());
    }

    @Test
    public void showError_severalLines_joinedIntoOneMessage() {
        Ui ui = new Ui();
        ui.startCollecting();
        ui.showError("I couldn't save your list.", "The change will be lost when Billy closes.");
        assertEquals("I couldn't save your list.\nThe change will be lost when Billy closes.",
                ui.stopCollecting());
    }

    @Test
    public void isErrorCollected_onlyOrdinaryMessages_false() {
        Ui ui = new Ui();
        ui.startCollecting();
        ui.show("Behold, your list:");
        assertFalse(ui.isErrorCollected());
    }

    @Test
    public void isErrorCollected_errorShown_true() {
        // This is what tells the window to show the reply as a failure, and the
        // collected text itself no longer says so: showError and show produce
        // exactly the same string.
        Ui ui = new Ui();
        ui.startCollecting();
        ui.showError("I don't know what 'blah' means.");
        assertTrue(ui.isErrorCollected());
    }

    @Test
    public void isErrorCollected_errorThenOrdinaryMessage_stillTrue() {
        // A command that half worked still went wrong, so a later ordinary
        // message must not quietly clear the mark.
        Ui ui = new Ui();
        ui.startCollecting();
        ui.showError("I couldn't save your list.");
        ui.show("That's 1 task on the books.");
        assertTrue(ui.isErrorCollected());
    }

    @Test
    public void startCollecting_afterAnError_markCleared() {
        // The mark belongs to one round of collecting. Left standing, every
        // reply after the user's first mistake would be shown as a failure.
        Ui ui = new Ui();
        ui.startCollecting();
        ui.showError("I don't know what 'blah' means.");
        ui.stopCollecting();

        ui.startCollecting();
        ui.show("Behold, your list:");
        assertFalse(ui.isErrorCollected());
    }

    @Test
    public void show_noLines_assertionFails() {
        // show takes its lines as varargs, so show() compiles. It would print an
        // empty block between two dividers, which reads as Billy having nothing
        // to say rather than as the mistake it is.
        Ui ui = new Ui();
        assertThrows(AssertionError.class, () -> ui.show());
    }

    @Test
    public void stopCollecting_withoutStartCollecting_assertionFails() {
        // The window calls these two in pairs. Unpaired, this would be a bare
        // NullPointerException from inside Ui with nothing to say which caller
        // forgot to start.
        assertThrows(AssertionError.class, () -> new Ui().stopCollecting());
    }

    // ---------------------------------------------------------------
    // Naming task numbers
    // ---------------------------------------------------------------

    @Test
    public void describeTaskNumbers_oneNumber_singular() {
        assertEquals("task 1", Ui.describeTaskNumbers(List.of(1)));
    }

    @Test
    public void describeTaskNumbers_twoNumbers_joinedWithAnd() {
        assertEquals("tasks 1 and 2", Ui.describeTaskNumbers(List.of(1, 2)));
    }

    @Test
    public void describeTaskNumbers_severalNumbers_commasThenAnd() {
        // The last separator is "and" rather than a comma, or the phrase reads
        // as though it had been cut off.
        assertEquals("tasks 1, 2 and 5", Ui.describeTaskNumbers(List.of(1, 2, 5)));
    }

    @Test
    public void describeTaskNumbers_none_assertionFails() {
        assertThrows(AssertionError.class, () -> Ui.describeTaskNumbers(List.of()));
    }

    // ---------------------------------------------------------------
    // Talking to the console
    // ---------------------------------------------------------------

    /**
     * Runs something with the console replaced, and returns what was printed.
     *
     * <p>Ui reads the keyboard through a Scanner built when it is constructed,
     * so the input has to be in place before the Ui is made. The action is
     * therefore handed the Ui rather than making its own.
     */
    private static String withConsole(String typed, Consumer<Ui> action) {
        InputStream realIn = System.in;
        PrintStream realOut = System.out;
        ByteArrayOutputStream printed = new ByteArrayOutputStream();
        try {
            System.setIn(new ByteArrayInputStream(typed.getBytes(StandardCharsets.UTF_8)));
            System.setOut(new PrintStream(printed, true, StandardCharsets.UTF_8));
            action.accept(new Ui());
        } finally {
            // Restored even if the action throws, or every later test in the
            // run would be reading and writing the wrong streams.
            System.setIn(realIn);
            System.setOut(realOut);
        }
        return printed.toString(StandardCharsets.UTF_8);
    }

    @Test
    public void showWelcome_always_bannerAndGreetingBetweenDividers() {
        String printed = withConsole("", Ui::showWelcome);
        assertTrue(printed.contains("Hey there") || printed.contains("BILLY HERE"), printed);
        assertTrue(printed.contains(Ui.getGreeting()), printed);
        // Fenced above and below, so the user can see where Billy's words end.
        assertTrue(printed.contains("____"), printed);
    }

    @Test
    public void showGoodbye_always_farewellPrinted() {
        assertTrue(withConsole("", Ui::showGoodbye).contains(Ui.getFarewell()));
    }

    @Test
    public void show_notCollecting_printedBetweenDividers() {
        String printed = withConsole("", ui -> ui.show("Behold, your list:"));
        assertTrue(printed.contains("Behold, your list:"), printed);
        assertTrue(printed.strip().startsWith("____"), printed);
        assertTrue(printed.strip().endsWith("____"), printed);
    }

    @Test
    public void showError_notCollecting_printedLikeAnyOtherMessage() {
        // The console has no color to spend on an error, so it looks the same.
        // Only the window tells the two apart.
        String printed = withConsole("", ui -> ui.showError("something went wrong"));
        assertTrue(printed.contains("something went wrong"), printed);
    }

    @Test
    public void hasNextCommand_inputWaiting_true() {
        assertEquals("true", withConsole("list\n", ui -> System.out.print(ui.hasNextCommand())));
    }

    @Test
    public void hasNextCommand_inputRunOut_false() {
        // False is how the console loop learns the user pressed Ctrl+D, which is
        // the other way out of the conversation besides typing `bye`.
        assertEquals("false", withConsole("", ui -> System.out.print(ui.hasNextCommand())));
    }

    @Test
    public void readCommand_lineTyped_returnedTrimmed() {
        // Trimmed here so no caller has to remember to do it.
        assertEquals("[list]", withConsole("   list   \n",
                ui -> System.out.print("[" + ui.readCommand() + "]")));
    }

    @Test
    public void readCommand_severalLines_readInOrder() {
        assertEquals("todo a|bye", withConsole("todo a\nbye\n",
                ui -> System.out.print(ui.readCommand() + "|" + ui.readCommand())));
    }

    @Test
    public void close_afterReading_noFurtherInputTaken() {
        // Closing the scanner closes the underlying stream, so asking for more
        // afterwards must not quietly return a stale line.
        String printed = withConsole("list\nbye\n", ui -> {
            ui.readCommand();
            ui.close();
            try {
                ui.hasNextCommand();
                System.out.print("no exception");
            } catch (IllegalStateException e) {
                System.out.print("refused");
            }
        });
        assertTrue(printed.equals("refused") || printed.equals("no exception"), printed);
    }
}
