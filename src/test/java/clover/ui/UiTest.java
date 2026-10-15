package clover.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import clover.task.ToDo;
import clover.tutoree.Tutoree;

/**
 * Tests for UI messages captured for the JavaFX chat interface.
 */
class UiTest {

    @Test
    void showError_errorShown_responseContainsMessage() {
        Ui ui = new Ui();

        ui.showError("Invalid command");

        assertEquals("The forest path is unclear. Invalid command", ui.buildResponse());
    }

    @Test
    void clearResponse_messageShown_responseCleared() {
        Ui ui = new Ui();
        ui.showError("Invalid command");

        ui.clearResponse();

        assertEquals("", ui.buildResponse());
    }

    @Test
    void showTaskAdded_taskAdded_responseContainsBothMessagesInOrder() {
        Ui ui = new Ui();

        ui.showTaskAdded(new ToDo("read book"), 1);

        assertEquals("A new study quest has taken root: [T] [ ] read book" + System.lineSeparator()
                + "The grove now holds 1 quest.", ui.buildResponse());
    }

    @Test
    void showTaskList_tasksSupplied_responseListsTasksInNumberedOrder() {
        Ui ui = new Ui();

        ui.showTaskList(List.of(new ToDo("read book"), new ToDo("buy groceries")));

        assertEquals("The grove has gathered your study quests:" + System.lineSeparator()
                + "1.[T] [ ] read book" + System.lineSeparator()
                + "2.[T] [ ] buy groceries", ui.buildResponse());
    }

    @Test
    void showTaskList_emptyTaskList_clearEmptyMessageShown() {
        Ui ui = new Ui();

        ui.showTaskList(List.of());

        assertEquals("There are no study quests in the grove yet.", ui.buildResponse());
    }

    @Test
    void showHelp_commandReferenceShown() {
        Ui ui = new Ui();

        ui.showHelp();

        assertEquals(String.join(System.lineSeparator(),
                "How to use Clover",
                "Type lowercase words exactly. Replace <UPPERCASE> placeholders with your details.",
                "[Square brackets show optional parts; do not type the brackets when writing the command.]",
                "",
                "TASKS",
                "• todo <DESCRIPTION> [/for <NAME>]",
                "  Add a task. /for links it to a tutoree.",
                "• deadline <DESCRIPTION> /by <DATE> [/for <NAME>]",
                "  Add a deadline. DATE uses yyyy-MM-dd.",
                "• event <DESCRIPTION> /from <DATE> /to <DATE> [/for <NAME>]",
                "  Add an event. The end date must be after the start date.",
                "• list",
                "  Show all tasks.",
                "• find <KEYWORD>",
                "  Search task descriptions.",
                "• mark <NUMBER>",
                "  Complete a task.",
                "• unmark <NUMBER>",
                "  Reopen a completed task.",
                "• delete <NUMBER>",
                "  Remove a task.",
                "",
                "TUTOREE DIRECTORY",
                "• add-tutoree <NAME> /address <ADDRESS> /fee <AMOUNT>[/RATE]",
                "  Add a tutoree. RATE may be /hour, /session, /lesson, or /month.",
                "• list-tutorees",
                "  Show all tutorees.",
                "• find-tutoree <KEYWORD>",
                "  Search tutoree names.",
                "",
                "OTHER",
                "• help",
                "  Show this command guide.",
                "• bye",
                "  Close Clover."), ui.buildResponse());
    }

    @Test
    void showFindResults_tasksSupplied_responseListsMatchingTasksInNumberedOrder() {
        Ui ui = new Ui();

        ui.showFindResults(List.of(new ToDo("read book"), new ToDo("buy bookcase")));

        assertEquals("The grove found these matching quests:" + System.lineSeparator()
                + "1.[T] [ ] read book" + System.lineSeparator()
                + "2.[T] [ ] buy bookcase", ui.buildResponse());
    }

    @Test
    void taskMutationMessages_singularAndPluralCounts_displayExpectedText() {
        Ui ui = new Ui();
        ToDo task = new ToDo("read book");

        ui.showTaskMarked(task);
        ui.showTaskUnmarked(task);
        ui.showTaskDeleted(task, 2);

        assertEquals("The grove celebrates! This quest is complete: [T] [ ] read book" + System.lineSeparator()
                + "This quest needs a little more tending: [T] [ ] read book" + System.lineSeparator()
                + "This trail has been cleared: [T] [ ] read book" + System.lineSeparator()
                + "The grove now holds 2 quests.", ui.buildResponse());
    }

    @Test
    void tutoreeMessages_emptyAndPopulatedLists_displayExpectedText() {
        Ui ui = new Ui();
        Tutoree alice = new Tutoree("Alice Tan", "12 Example Road", "50/hour");

        ui.showTutoreeAdded(alice, 1);
        assertEquals("A new learning companion has arrived in the grove:" + System.lineSeparator()
                + "Alice Tan" + System.lineSeparator() + "Address: 12 Example Road" + System.lineSeparator()
                + "Fee: 50/hour" + System.lineSeparator() + "The grove now knows 1 learning companion.",
                ui.buildResponse());
        ui.clearResponse();
        ui.showTutoreeList(List.of(alice));
        assertEquals("Here are the learning companions in the grove:" + System.lineSeparator()
                + "1. Alice Tan" + System.lineSeparator() + "   Address: 12 Example Road" + System.lineSeparator()
                + "   Fee: 50/hour", ui.buildResponse());
        ui.clearResponse();
        ui.showTutoreeFindResults(List.of());
        assertEquals("No matching learning companions found.", ui.buildResponse());
    }

    @Test
    void showGoodbye_responseContainsFarewell() {
        Ui ui = new Ui();

        ui.showGoodbye();

        assertEquals("The grove closes for now. Goodbye, and may your path through the grove be gentle.",
                ui.buildResponse());
    }

    @Test
    void welcomeDividerAndEmptySearches_displayExpectedMessages() {
        Ui ui = new Ui();

        ui.showWelcome();
        assertTrue(ui.buildResponse().contains("Hello! I'm Clover."));
        ui.clearResponse();
        ui.showLine();
        ui.showFindResults(List.of());
        ui.showTutoreeList(List.of());
        ui.showTutoreeAdded(new Tutoree("Alice", "Home", "50"), 2);

        assertTrue(ui.buildResponse().startsWith("____________________________________________________________"));
        assertTrue(ui.buildResponse().contains("No matching tasks found."));
        assertTrue(ui.buildResponse().contains("There are no learning companions in the grove yet."));
        assertTrue(ui.buildResponse().endsWith("The grove now knows 2 learning companions."));
    }
}
