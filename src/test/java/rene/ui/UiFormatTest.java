package rene.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import rene.task.Deadline;
import rene.task.Task;
import rene.task.Todo;

/**
 * Verifies the exact wording and structure of every message the console and
 * graphical UIs present to the user.
 */
class UiFormatTest {
    private final Ui ui = new Ui();

    @Test
    void formatWelcome_noLoadingError_returnsOnlyGreeting() {
        assertEquals("Good day. Rene here, your personal productivity liaison.\n"
                + "Let's align on your priorities for the day.", ui.formatWelcome(null));
    }

    @Test
    void formatWelcome_withLoadingError_appendsErrorAfterGreeting() {
        String response = ui.formatWelcome("I couldn't load tasks from data/rene.txt.");

        assertTrue(response.startsWith("Good day. Rene here, your personal productivity liaison."));
        assertTrue(response.endsWith("Apologies — I couldn't load tasks from data/rene.txt."));
    }

    @Test
    void formatGoodbye_returnsFarewell() {
        assertEquals("Thank you for your time. Standing by — let's touch base again soon.",
                ui.formatGoodbye());
    }

    @Test
    void formatError_prefixesMessageWithApologies() {
        assertEquals(" Apologies — something went wrong", ui.formatError("something went wrong"));
    }

    @Test
    void formatTaskAdded_singletaskSaysActionItem() {
        String response = ui.formatTaskAdded(new Todo("read book"), 1);

        assertEquals(" Noted. I've logged this deliverable on your action items:\n"
                + "   [T][ ] read book\n Your backlog now contains 1 action item.", response);
    }

    @Test
    void formatTaskAdded_multitasksSaysActionItems() {
        String response = ui.formatTaskAdded(new Todo("read book"), 3);

        assertTrue(response.endsWith("Your backlog now contains 3 action items."));
    }

    @Test
    void formatTasks_emptyListShowsOnlyHeading() {
        assertEquals(" Here is your current action-item backlog:", ui.formatTasks(List.of()));
    }

    @Test
    void formatTasks_numbersTasksInOrder() {
        Task deadline = new Deadline("return book", LocalDate.of(2026, 6, 6));

        String response = ui.formatTasks(List.of(new Todo("read book"), deadline));

        assertEquals(" Here is your current action-item backlog:\n"
                + " 1.[T][ ] read book\n"
                + " 2.[D][ ] return book (by: Jun 6 2026)", response);
    }

    @Test
    void formatMatchingTasks_emptyListShowsOnlyHeading() {
        assertEquals(" Here are the action items matching your search criteria:",
                ui.formatMatchingTasks(List.of()));
    }

    @Test
    void formatTaskMarked_andUnmarkedUseDistinctWordings() {
        Task task = new Todo("read book");

        String marked = ui.formatTaskMarked(task);
        Task unmarkedTask = new Todo("read book");
        String unmarked = ui.formatTaskUnmarked(unmarkedTask);

        assertEquals(" Well received. I've marked this item as actioned:\n   [T][ ] read book", marked);
        assertEquals(" Understood. This item has been reopened:\n   [T][ ] read book", unmarked);
    }

    @Test
    void formatTaskDeleted_withRemainingTasksMentionsRenumbering() {
        String response = ui.formatTaskDeleted(new Todo("read book"), 1, true);

        assertEquals(" Noted. This action item has been removed from the pipeline:\n"
                + "   [T][ ] read book\n Your backlog now contains 1 action item.\n"
                + " The remaining action items have been renumbered.", response);
    }

    @Test
    void formatTaskDeleted_lastTaskOmitsRenumberingNote() {
        String response = ui.formatTaskDeleted(new Todo("read book"), 0, false);

        assertEquals(" Noted. This action item has been removed from the pipeline:\n"
                + "   [T][ ] read book\n Your backlog now contains 0 action items.", response);
    }

    @Test
    void formatHelp_listsEveryCommand() {
        String help = ui.formatHelp();

        String[] lines = {
            " todo DESCRIPTION",
            " deadline DESCRIPTION /by yyyy-MM-dd",
            " event DESCRIPTION /from START /to END",
            " list",
            " mark NUMBER",
            " unmark NUMBER",
            " delete NUMBER",
            " find KEYWORD",
            " help",
            " bye",
        };
        for (String line : lines) {
            assertTrue(help.contains(line), "Help text is missing: " + line);
        }
    }
}
