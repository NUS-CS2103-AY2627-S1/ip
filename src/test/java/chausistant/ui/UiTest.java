package chausistant.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

/** Tests the cute and encouraging messages presented by the user interface. */
class UiTest {

    @Test
    void showWelcome_displaysGreetingAndAsciiBanner() {
        StringBuilder response = new StringBuilder();

        new Ui(response).showWelcome();

        assertTrue(response.toString().startsWith(" hi! i'm" + System.lineSeparator()));
        assertTrue(response.toString().contains("chausistant"));
        assertTrue(response.toString().endsWith("ready for a tiny win today?" + System.lineSeparator()
                + System.lineSeparator()));
    }

    @Test
    void showError_recordsErrorStateAndPrefixesMessage() {
        StringBuilder response = new StringBuilder();
        Ui ui = new Ui(response);

        assertFalse(ui.hasShownError());
        ui.showError("unknown command");

        assertTrue(ui.hasShownError());
        assertEquals("oopsie! unknown command" + System.lineSeparator(), response.toString());
    }

    @Test
    void showTaskAdded_singularTaskCount_displaysEncouragement() {
        StringBuilder response = new StringBuilder();

        new Ui(response).showTaskAdded("[T][ ] read book", 1);

        assertEquals(String.join(System.lineSeparator(),
                "yay! i've added this little mission:",
                "[T][ ] read book",
                "don't give up! you have 1 task ahead of you...") + System.lineSeparator(),
                response.toString());
    }

    @Test
    void showTaskAdded_pluralTaskCount_displaysEncouragement() {
        StringBuilder response = new StringBuilder();

        new Ui(response).showTaskAdded("[T][ ] finish assignment", 3);

        assertEquals(String.join(System.lineSeparator(),
                "yay! i've added this little mission:",
                "[T][ ] finish assignment",
                "don't give up! you have 3 tasks ahead of you...") + System.lineSeparator(),
                response.toString());
    }

    @Test
    void showEmptyTaskList_displaysPlayfulCelebration() {
        StringBuilder response = new StringBuilder();

        new Ui(response).showTaskList(List.of());

        assertEquals(String.join(System.lineSeparator(),
                "here's your tiny adventure list:",
                "your list is all clear! tiny victory dance time!") + System.lineSeparator(),
                response.toString());
    }

    @Test
    void showTaskList_populatedListNumbersEachTask() {
        StringBuilder response = new StringBuilder();

        new Ui(response).showTaskList(List.of("[T][ ] read book", "[T][X] return book"));

        assertEquals(String.join(System.lineSeparator(),
                "here's your tiny adventure list:",
                "1.[T][ ] read book",
                "2.[T][X] return book") + System.lineSeparator(), response.toString());
    }

    @Test
    void showTaskStatusAndDeletion_displayUpdatedTaskAndPluralCount() {
        StringBuilder response = new StringBuilder();
        Ui ui = new Ui(response);

        ui.showTaskStatus("[T][X] read book");
        ui.showTaskDeleted("[T][X] read book", 2);

        assertEquals(String.join(System.lineSeparator(),
                "looking good! here's your task now:",
                "[T][X] read book",
                "poof! i've tucked this task away:",
                "[T][X] read book",
                "your list now has 2 tasks.") + System.lineSeparator(), response.toString());
    }

    @Test
    void showMatchingTasks_emptyAndPopulatedResults_displayExpectedMessages() {
        StringBuilder response = new StringBuilder();
        Ui ui = new Ui(response);

        ui.showMatchingTasks(List.of());
        ui.showMatchingTasks(List.of("[T][ ] read book"));

        assertEquals(String.join(System.lineSeparator(),
                "i found these task twins:",
                "no task twins found yet!",
                "i found these task twins:",
                "1.[T][ ] read book") + System.lineSeparator(), response.toString());
    }

    @Test
    void showSchedule_emptyAndPopulatedSections_displayTheirDistinctMessages() {
        StringBuilder response = new StringBuilder();
        Ui ui = new Ui(response);

        ui.showSchedule("Jun 8 2026", List.of(), List.of());
        ui.showSchedule("Jun 9 2026", List.of("[E][ ] meeting"), List.of("[D][ ] submit report"));

        assertEquals(String.join(System.lineSeparator(),
                "here's your day at a glance for Jun 8 2026:",
                "events:",
                "no events here -- your calendar gets a cozy breather!",
                "--------------------",
                "deadlines:",
                "no deadlines here -- you're all clear!",
                "here's your day at a glance for Jun 9 2026:",
                "events:",
                "[E][ ] meeting",
                "--------------------",
                "deadlines:",
                "[D][ ] submit report") + System.lineSeparator(), response.toString());
    }

    @Test
    void showUpcomingDeadlines_emptyAndPopulatedLists_displayExpectedMessages() {
        StringBuilder response = new StringBuilder();
        Ui ui = new Ui(response);

        ui.showUpcomingDeadlines(List.of());
        ui.showUpcomingDeadlines(List.of("[D][ ] submit report"));
        ui.showGoodbye();

        assertEquals(String.join(System.lineSeparator(),
                "peek-a-boo! here are your upcoming deadlines:",
                "no upcoming deadlines in the next 7 days -- you're all caught up!",
                "peek-a-boo! here are your upcoming deadlines:",
                "[D][ ] submit report",
                "bye-bye for now! chausistant will be cheering for you!") + System.lineSeparator(),
                response.toString());
    }
}
