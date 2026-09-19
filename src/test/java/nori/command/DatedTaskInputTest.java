package nori.command;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import nori.Nori;
import nori.testutil.IsolatedSessionTest;
import nori.ui.GuiUi;

/**
 * Tests the fault each malformed deadline and event command is answered with.
 */
public class DatedTaskInputTest extends IsolatedSessionTest {
    @Test
    public void execute_deadlineWithoutDescription_reportsMissingDescription() {
        assertResponse("NOOT?! A deadline needs a description before \"/by\"."
                        + " Try \"deadline submit report /by 2019-10-15\".",
                "deadline /by 2019-10-15");
    }

    @Test
    public void execute_deadlineEndingAtSeparator_reportsMissingDueDate() {
        assertResponse("NOOT?! A deadline needs a due date after \"/by\"."
                        + " Try \"deadline submit report /by 2019-10-15\".",
                "deadline submit report /by");
    }

    @Test
    public void execute_deadlineWithoutSeparator_reportsMissingSeparator() {
        assertResponse("NOOT?! I cannot find the \"/by\" part of that deadline."
                        + " Use \"deadline submit report /by 2019-10-15\".",
                "deadline submit report");
    }

    @Test
    public void execute_deadlineWithWhitespaceDescription_reportsMissingDescription() {
        assertResponse("NOOT?! A deadline needs a description before \"/by\"."
                        + " Try \"deadline submit report /by 2019-10-15\".",
                "deadline    /by 2019-10-15");
    }

    @Test
    public void execute_deadlineWithUnparsableDate_reportsUnreadableDate() {
        assertResponse("NOOT?! I cannot understand \"tomorrow\" as a deadline."
                        + " Use a date like \"2019-10-15\".",
                "deadline submit report /by tomorrow");
    }

    @Test
    public void execute_eventWithoutDescription_reportsMissingDescription() {
        assertResponse("NOOT?! An event needs a description before \"/from\"."
                        + " Try \"event team meeting /from Mon 2pm /to 4pm\".",
                "event /from Mon 2pm /to 4pm");
    }

    @Test
    public void execute_eventWithoutEitherSeparator_reportsBothMissing() {
        assertResponse("NOOT?! An event needs both \"/from\" and \"/to\"."
                        + " Use \"event team meeting /from Mon 2pm /to 4pm\".",
                "event team meeting");
    }

    @Test
    public void execute_eventWithoutStartSeparator_reportsMissingStart() {
        assertResponse("NOOT?! An event is missing \"/from\" and its start time."
                        + " Tell me when to start waddling.",
                "event team meeting /to 4pm");
    }

    @Test
    public void execute_eventWithoutEndSeparator_reportsMissingEnd() {
        assertResponse("NOOT?! An event is missing \"/to\" and its end time."
                        + " Even penguin meetings eventually end.",
                "event team meeting /from Mon 2pm");
    }

    @Test
    public void execute_eventWithSeparatorsReversed_reportsWrongOrder() {
        assertResponse("NOOT?! Put \"/from\" before \"/to\"."
                        + " Time waddles forward, not backward.",
                "event team meeting /to 4pm /from Mon 2pm");
    }

    @Test
    public void execute_eventWithWhitespaceDescription_reportsMissingDescription() {
        assertResponse("NOOT?! An event needs a description before \"/from\"."
                        + " Try \"event team meeting /from Mon 2pm /to 4pm\".",
                "event    /from Mon 2pm /to 4pm");
    }

    @Test
    public void execute_eventWithBlankStart_reportsEmptyStart() {
        assertResponse("NOOT?! \"/from\" needs a start time."
                        + " I cannot waddle in from the void.",
                "event team meeting /from   /to 4pm");
    }

    @Test
    public void execute_eventEndingAtEndSeparator_reportsEmptyEnd() {
        assertResponse("NOOT?! \"/to\" needs an end time."
                        + " Even penguin meetings eventually end.",
                "event team meeting /from Mon 2pm /to");
    }

    @Test
    public void execute_eventWithAdjoiningSeparators_reportsEmptyStart() {
        assertResponse("NOOT?! \"/from\" needs a start time."
                        + " I cannot waddle in from the void.",
                "event team meeting /from /to 4pm");
    }

    @Test
    public void execute_eventWithAdjoiningSeparators_leavesTheListUnchanged() {
        GuiUi guiUi = new GuiUi();
        Nori nori = new Nori(guiUi);

        nori.executeCommand("event team meeting /from /to 4pm");
        nori.executeCommand("list");

        assertEquals("The iceberg is empty. Try \"todo borrow book\". Noot noot!",
                guiUi.consumeResponse());
    }

    @Test
    public void execute_deadlineWithRepeatedDueDate_reportsRepeatedOption() {
        assertResponse("NOOT?! A deadline takes only one \"/by\"."
                        + " Try \"deadline submit report /by 2019-10-15\".",
                "deadline submit report /by 2019-10-15 /by 2019-10-16");
    }

    @Test
    public void execute_deadlineWithEventOption_reportsUnexpectedOption() {
        assertResponse("NOOT?! A deadline does not use \"/from\"."
                        + " Try \"deadline submit report /by 2019-10-15\".",
                "deadline submit report /from Mon /by 2019-10-15");
    }

    @Test
    public void execute_eventWithRepeatedEnd_reportsRepeatedOption() {
        assertResponse("NOOT?! An event takes only one \"/to\"."
                        + " Use \"event team meeting /from Mon 2pm /to 4pm\".",
                "event team meeting /from Mon 2pm /to 4pm /to 6pm");
    }

    @Test
    public void execute_eventWithDeadlineOption_reportsUnexpectedOption() {
        assertResponse("NOOT?! An event does not use \"/by\"."
                        + " Use \"event team meeting /from Mon 2pm /to 4pm\".",
                "event team meeting /from Mon 2pm /to 4pm /by 2019-10-15");
    }

    @Test
    public void execute_eventWithRepeatedEnd_leavesTheListUnchanged() {
        GuiUi guiUi = new GuiUi();
        Nori nori = new Nori(guiUi);

        nori.executeCommand("event team meeting /from Mon 2pm /to 4pm /to 6pm");
        nori.executeCommand("list");

        assertEquals("The iceberg is empty. Try \"todo borrow book\". Noot noot!",
                guiUi.consumeResponse());
    }

    @Test
    public void execute_eventWithReversedDates_reportsReversedDates() {
        assertResponse("NOOT?! An event cannot end before it starts."
                        + " Time only waddles forward.",
                "event backwards /from 2026-09-03 /to 2026-09-01");
    }

    @Test
    public void execute_eventWithReversedBareTimes_reportsReversedTimes() {
        assertResponse("NOOT?! That event ends at or before it starts."
                        + " Time only waddles forward.",
                "event talk /from 2pm /to 1pm");
    }

    @Test
    public void execute_eventEndingBeforeItStartsOnOneDate_reportsReversedTimes() {
        assertResponse("NOOT?! That event ends at or before it starts."
                        + " Time only waddles forward.",
                "event talk /from 2019-06-06 1000 /to 0800");
    }

    @Test
    public void execute_eventWithImpossibleTime_reportsTheTime() {
        assertResponse("NOOT?! I cannot understand \"9:75\" as an event time."
                        + " Use a time like \"1400\" or \"2:30pm\".",
                "event talk /from 2026-10-01 9:75 /to 2026-10-01 1000");
    }

    @Test
    public void execute_eventWithReversedTimes_leavesTheListUnchanged() {
        GuiUi guiUi = new GuiUi();
        Nori nori = new Nori(guiUi);

        nori.executeCommand("event talk /from 2pm /to 1pm");
        nori.executeCommand("list");

        assertEquals("The iceberg is empty. Try \"todo borrow book\". Noot noot!",
                guiUi.consumeResponse());
    }

    @Test
    public void execute_eventNamingDaysInWords_addsTheTask() {
        assertResponse("Noot noot! Task tucked safely under my wing:\n"
                        + "  [E][ ] standup (from: Mon 2pm to: Tue 1pm)\n"
                        + "The iceberg now holds 1 task(s).",
                "event standup /from Mon 2pm /to Tue 1pm");
    }

    @Test
    public void execute_validDeadline_addsTheTask() {
        assertResponse("Noot noot! Task tucked safely under my wing:\n"
                        + "  [D][ ] submit report (by: Oct 15 2019)\n"
                        + "The iceberg now holds 1 task(s).",
                "deadline submit report /by 2019-10-15");
    }

    @Test
    public void execute_validEvent_addsTheTask() {
        assertResponse("Noot noot! Task tucked safely under my wing:\n"
                        + "  [E][ ] team meeting (from: Mon 2pm to: 4pm)\n"
                        + "The iceberg now holds 1 task(s).",
                "event team meeting /from Mon 2pm /to 4pm");
    }

    @Test
    public void execute_repeatedDeadline_reportsTheStoredTask() {
        GuiUi guiUi = new GuiUi();
        Nori nori = new Nori(guiUi);

        nori.executeCommand("deadline report /by 2019-10-15");
        nori.executeCommand("deadline report /by 2019-10-15");

        assertEquals("NOOT?! The iceberg already holds that task:" + "\n"
                + "  [D][ ] report (by: Oct 15 2019)" + "\n"
                + "I won\'t carry the same fish twice.", guiUi.consumeResponse());
    }

    @Test
    public void execute_deadlineOnAnotherDate_addsTheTask() {
        GuiUi guiUi = new GuiUi();
        Nori nori = new Nori(guiUi);

        nori.executeCommand("deadline report /by 2019-10-15");
        nori.executeCommand("deadline report /by 2019-10-16");

        assertEquals("Noot noot! Task tucked safely under my wing:" + "\n"
                + "  [D][ ] report (by: Oct 16 2019)" + "\n"
                + "The iceberg now holds 2 task(s).", guiUi.consumeResponse());
    }

    /**
     * Runs one command in a fresh session and checks the whole response.
     *
     * @param expectedResponse the response the command should produce.
     * @param input the command to run.
     */
    private void assertResponse(String expectedResponse, String input) {
        GuiUi guiUi = new GuiUi();
        Nori nori = new Nori(guiUi);

        nori.executeCommand(input);

        assertEquals(expectedResponse, guiUi.consumeResponse());
    }
}
