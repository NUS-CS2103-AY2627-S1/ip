package milo.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Ui is the only place that formats Milo's output, so the shape of what it
 * prints, and the plain text it hands the GUI, are worth holding in place.
 */
class UiTest {
    private final PrintStream originalOut = System.out;
    private ByteArrayOutputStream capturedOut;

    @BeforeEach
    void redirectStdOut() {
        capturedOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(capturedOut));
    }

    @AfterEach
    void restoreStdOut() {
        System.setOut(originalOut);
    }

    @Test
    void describeCount_one_isSingular() {
        assertEquals("1 task", Ui.describeCount(1));
    }

    @Test
    void describeCount_otherNumbers_arePlural() {
        assertEquals("0 tasks", Ui.describeCount(0));
        assertEquals("2 tasks", Ui.describeCount(2));
    }

    @Test
    void getLastResponse_beforeAnythingShown_isEmpty() {
        assertEquals("", new Ui().getLastResponse());
    }

    @Test
    void getLastResponse_afterShowResponse_isThePlainTextWithoutDividers() {
        Ui ui = new Ui();
        ui.showResponse("first", "second");
        assertEquals("first\nsecond", ui.getLastResponse());
    }

    @Test
    void showResponse_printsEveryLineIndentedBetweenDividers() {
        Ui ui = new Ui();
        ui.showResponse("hello");

        String printed = capturedOut.toString();
        assertTrue(printed.contains("_____"));
        assertTrue(printed.contains("     hello"));
    }

    @Test
    void showResponse_lineContainingNewlines_isSplitAndIndentedThroughout() {
        Ui ui = new Ui();
        ui.showResponse("one\ntwo");

        String printed = capturedOut.toString();
        assertTrue(printed.contains("     one"));
        assertTrue(printed.contains("     two"));
    }

    @Test
    void showLoadStatus_cleanLoadOfEmptyList_printsNothing() {
        Ui ui = new Ui();
        assertEquals("", ui.showLoadStatus(new ArrayList<>(), 0));
        assertEquals("", capturedOut.toString());
    }

    @Test
    void showLoadStatus_cleanLoadOfOneTask_isSingular() {
        Ui ui = new Ui();
        String shown = ui.showLoadStatus(new ArrayList<>(), 1);
        assertTrue(shown.contains("1 task from last time"));
    }

    @Test
    void showLoadStatus_withWarnings_reportsThemInsteadOfTheCount() {
        Ui ui = new Ui();
        List<String> warnings = List.of("something went wrong");
        String shown = ui.showLoadStatus(warnings, 3);
        assertTrue(shown.contains("something went wrong"));
        assertTrue(!shown.contains("3 tasks"));
    }

    @Test
    void getGreeting_isTheGreetingWithoutTheBanner() {
        assertTrue(!new Ui().getGreeting().contains("|_|"));
    }
}
