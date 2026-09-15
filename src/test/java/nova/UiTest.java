package nova;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Pins down the exact wording Nova shows. Ui is the one place the console and
 * the GUI share their phrasing, so these assertions are deliberately literal:
 * a reworded message should fail here, and only here.
 */
public class UiTest {

    private final Ui ui = new Ui();

    private Event eventFrom(String name, int startHour, int endHour) {
        return new Event(name, false,
                LocalDateTime.of(2026, 1, 5, startHour, 0),
                LocalDateTime.of(2026, 1, 5, endHour, 0));
    }

    @Test
    public void getGreetingMessage_namesTheBotAndOffersHelp() {
        assertEquals("Hello! I'm Nova.\nWhat can I do for you?", ui.getGreetingMessage());
    }

    @Test
    public void getFarewellMessage_saysGoodbye() {
        assertEquals("Bye. Hope to see you again soon!", ui.getFarewellMessage());
    }

    @Test
    public void getTaskAddedMessage_oneTaskInTheList_usesTheSingularNoun() {
        assertEquals("Got it. I've added this task:\n"
                + "  [T][ ] read book\n"
                + "Now you have 1 task in the list.",
                ui.getTaskAddedMessage(new ToDo("read book", false), 1));
    }

    @Test
    public void getTaskAddedMessage_severalTasksInTheList_usesThePluralNoun() {
        assertEquals("Got it. I've added this task:\n"
                + "  [T][ ] read book\n"
                + "Now you have 4 tasks in the list.",
                ui.getTaskAddedMessage(new ToDo("read book", false), 4));
    }

    @Test
    public void getTaskRemovedMessage_oneTaskLeft_usesTheSingularNoun() {
        assertEquals("Noted, I've removed this task:\n"
                + "  [T][ ] read book\n"
                + "Now you have 1 task in the list.",
                ui.getTaskRemovedMessage(new ToDo("read book", false), 1));
    }

    @Test
    public void getTaskRemovedMessage_noneLeft_usesThePluralNoun() {
        assertEquals("Noted, I've removed this task:\n"
                + "  [T][ ] read book\n"
                + "Now you have 0 tasks in the list.",
                ui.getTaskRemovedMessage(new ToDo("read book", false), 0));
    }

    @Test
    public void getTaskMarkedMessage_markedDone_saysSoAndShowsTheTask() {
        assertEquals("Nice! I've marked this task as done:\n  [T][X] read book",
                ui.getTaskMarkedMessage(new ToDo("read book", true), true));
    }

    @Test
    public void getTaskMarkedMessage_markedNotDone_saysSoAndShowsTheTask() {
        assertEquals("OK, I've marked this task as not done yet:\n  [T][ ] read book",
                ui.getTaskMarkedMessage(new ToDo("read book", false), false));
    }

    @Test
    public void getClashSummary_onePair_usesTheSingularNoun() {
        assertEquals("1 pair of events in your list clash.", ui.getClashSummary(1));
    }

    @Test
    public void getClashSummary_severalPairs_usesThePluralNoun() {
        assertEquals("3 pairs of events in your list clash.", ui.getClashSummary(3));
    }

    @Test
    public void getClashMessage_oneClash_listsItAndExplainsTheOverride() {
        assertEquals("That clashes with:\n"
                + "  [E][ ] standup (from: Jan 05 2026, 9:00AM to: Jan 05 2026, 10:00AM)\n"
                + "Re-enter with /force to add it anyway.",
                ui.getClashMessage(List.of(eventFrom("standup", 9, 10))));
    }

    @Test
    public void getClashMessage_severalClashes_listsEveryOne() {
        String message = ui.getClashMessage(List.of(
                eventFrom("standup", 9, 10),
                eventFrom("interview", 11, 12)));

        assertEquals("That clashes with:\n"
                + "  [E][ ] standup (from: Jan 05 2026, 9:00AM to: Jan 05 2026, 10:00AM)\n"
                + "  [E][ ] interview (from: Jan 05 2026, 11:00AM to: Jan 05 2026, 12:00PM)\n"
                + "Re-enter with /force to add it anyway.", message);
    }
}
