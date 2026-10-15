package hu9o;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Tests the GUI-facing surface of {@link Hu9o}: that {@link Hu9o#getResponse}
 * returns the reply text as a string, that {@link Hu9o#isLastResponseError}
 * reflects the outcome of that reply, and that {@link Hu9o#isExitCommand}
 * recognises {@code bye} regardless of casing and surrounding spaces.
 *
 * <p>These tests avoid {@code bye}, since handling it writes the save file.
 */
public class Hu9oTest {

    @Test
    public void getResponse_addTodo_returnsConfirmationTextAndNoError() {
        Hu9o hu9o = Hu9o.createForGui();

        String response = hu9o.getResponse("todo read book");

        assertTrue(response.contains("Got it. I've added this task:"));
        assertTrue(response.contains("[T][ ] read book"));
        assertFalse(hu9o.isLastResponseError());
    }

    @Test
    public void getResponse_unknownCommand_flagsErrorOnNextQuery() {
        Hu9o hu9o = Hu9o.createForGui();

        String response = hu9o.getResponse("frobnicate");

        assertTrue(response.contains("Unknown command"));
        assertTrue(hu9o.isLastResponseError());
    }

    @Test
    public void isLastResponseError_errorThenSuccess_reportsPerCommand() {
        Hu9o hu9o = Hu9o.createForGui();

        hu9o.getResponse("mark 999");
        assertTrue(hu9o.isLastResponseError());

        hu9o.getResponse("todo buy milk");
        assertFalse(hu9o.isLastResponseError());
    }

    @Test
    public void isExitCommand_byeInAnyCasingOrSpacing_returnsTrue() {
        Hu9o hu9o = Hu9o.createForGui();

        assertTrue(hu9o.isExitCommand("bye"));
        assertTrue(hu9o.isExitCommand("  ByE  "));
    }

    @Test
    public void isExitCommand_otherCommands_returnsFalse() {
        Hu9o hu9o = Hu9o.createForGui();

        assertFalse(hu9o.isExitCommand("byebye"));
        assertFalse(hu9o.isExitCommand("list"));
    }

    @Test
    public void getResponse_addPerson_returnsConfirmationText() {
        Hu9o hu9o = Hu9o.createForGui();

        String response = hu9o.getResponse("person Hu9oTest Person Alpha /phone 91234567 /email alpha@example.com");

        assertTrue(response.contains("Got it. I've added this person:"));
        assertTrue(response.contains("Hu9oTest Person Alpha"));
        assertFalse(hu9o.isLastResponseError());
    }

    @Test
    public void getResponse_selectThenTodo_addsTaskToPersonsListOnly() {
        Hu9o hu9o = Hu9o.createForGui();
        hu9o.getResponse("person Hu9oTest Person Beta /phone 91234567 /email beta@example.com");
        hu9o.getResponse("select Hu9oTest Person Beta");

        String addResponse = hu9o.getResponse("todo Hu9oTest marker task beta");
        String personListResponse = hu9o.getResponse("list");
        hu9o.getResponse("deselect");
        String globalListResponse = hu9o.getResponse("list");

        assertTrue(addResponse.contains("Got it. I've added this task:"));
        assertTrue(personListResponse.contains("Hu9oTest marker task beta"));
        assertFalse(globalListResponse.contains("Hu9oTest marker task beta"));
    }

    @Test
    public void getResponse_selectUnknownPerson_flagsErrorOnNextQuery() {
        Hu9o hu9o = Hu9o.createForGui();

        String response = hu9o.getResponse("select Hu9oTest Nonexistent Person Gamma");

        assertTrue(response.contains("No person with that name"));
        assertTrue(hu9o.isLastResponseError());
    }

    @Test
    public void getResponse_linkTwoPeopleThenConnections_listsBothNames() {
        Hu9o hu9o = Hu9o.createForGui();
        hu9o.getResponse("person Hu9oTest Person Delta /phone 91234567 /email delta@example.com");
        hu9o.getResponse("person Hu9oTest Person Epsilon /phone 91234567 /email epsilon@example.com");
        hu9o.getResponse("link Hu9oTest Person Delta /with Hu9oTest Person Epsilon");

        String response = hu9o.getResponse("connections Hu9oTest Person Delta");

        assertTrue(response.contains("Hu9oTest Person Epsilon"));
        assertFalse(hu9o.isLastResponseError());
    }

    @Test
    public void getResponse_deletePersonWhoIsSelected_autoDeselects() {
        Hu9o hu9o = Hu9o.createForGui();
        hu9o.getResponse("person Hu9oTest Person Zeta /phone 91234567 /email zeta@example.com");
        hu9o.getResponse("select Hu9oTest Person Zeta");

        hu9o.getResponse("deleteperson 1");
        String response = hu9o.getResponse("deselect");

        assertTrue(response.contains("No one was selected"));
    }

    @Test
    public void getResponse_contactVerbWhilePersonSelected_stillRoutesToContactParser() {
        Hu9o hu9o = Hu9o.createForGui();
        hu9o.getResponse("person Hu9oTest Person Eta /phone 91234567 /email eta@example.com");
        hu9o.getResponse("select Hu9oTest Person Eta");

        String response = hu9o.getResponse("people");

        assertTrue(response.contains("Hu9oTest Person Eta"));
    }

    @Test
    public void getResponse_deadlineWithMinutes_isAccepted() {
        Hu9o hu9o = Hu9o.createForGui();

        String response = hu9o.getResponse("deadline Hu9oTest minute test /by 20/9/26 630 PM");

        assertTrue(response.contains("Hu9oTest minute test (by: 20 Sep 2026, 6:30 PM)"));
        assertFalse(hu9o.isLastResponseError());
    }

    @Test
    public void getResponse_deadlineOnNonexistentDate_isRejectedNotAdjusted() {
        Hu9o hu9o = Hu9o.createForGui();

        String response = hu9o.getResponse("deadline Hu9oTest impossible /by 31/2/26 6 PM");

        assertTrue(response.contains("Invalid Date"));
        assertFalse(response.contains("28 Feb"));
        assertTrue(hu9o.isLastResponseError());
    }

    @Test
    public void getResponse_eventEndingBeforeItStarts_isRejected() {
        Hu9o hu9o = Hu9o.createForGui();

        String response = hu9o.getResponse("event Hu9oTest backwards /from 21/9/26 4 PM /to 21/9/26 2 PM");

        assertTrue(response.contains("ends before it starts"));
        assertTrue(hu9o.isLastResponseError());
    }

    @Test
    public void isClearCommand_clearInAnyCasingOrSpacing_returnsTrue() {
        Hu9o hu9o = Hu9o.createForGui();

        assertTrue(hu9o.isClearCommand("clear"));
        assertTrue(hu9o.isClearCommand("  ClEaR  "));
    }

    @Test
    public void isClearCommand_otherCommands_returnsFalse() {
        Hu9o hu9o = Hu9o.createForGui();

        assertFalse(hu9o.isClearCommand("clearperson"));
        assertFalse(hu9o.isClearCommand("list"));
    }

    @Test
    public void getContactSummaries_noPeopleAdded_returnsEmptyList() {
        Hu9o hu9o = Hu9o.createForGui();

        assertTrue(hu9o.getContactSummaries().isEmpty());
    }

    @Test
    public void getContactSummaries_afterAddingPeople_reflectsEachPersonInOrder() {
        Hu9o hu9o = Hu9o.createForGui();
        hu9o.getResponse("person Hu9oTest Person Theta /phone 91234567 /email theta@example.com");
        hu9o.getResponse("person Hu9oTest Person Iota /phone 91234567 /email iota@example.com");

        List<String> summaries = hu9o.getContactSummaries();

        assertEquals(2, summaries.size());
        assertTrue(summaries.get(0).contains("Hu9oTest Person Theta"));
        assertTrue(summaries.get(1).contains("Hu9oTest Person Iota"));
    }

    @Test
    public void getContactSummaries_doesNotAffectErrorOrOutputState() {
        Hu9o hu9o = Hu9o.createForGui();
        hu9o.getResponse("mark 999");

        hu9o.getContactSummaries();

        // The error flag set by "mark 999" should survive the read-only summaries
        // call untouched, since isLastResponseError() consumes it on read.
        assertTrue(hu9o.isLastResponseError());
    }
}
