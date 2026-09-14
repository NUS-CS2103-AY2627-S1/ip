package nova;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import nova.task.Deadline;
import nova.task.Task;
import nova.task.Todo;

public class UiTest {

    private static final LocalDateTime DEC_2_6PM = LocalDateTime.of(2019, 12, 2, 18, 0);

    private Ui ui;

    @BeforeEach
    public void setUp() {
        ui = new Ui();
    }

    @Test
    public void flush_nothingSaid_returnsEmptyString() {
        assertEquals("", ui.flush());
    }

    @Test
    public void flush_calledTwice_secondCallIsEmpty() {
        ui.showGoodbye();

        assertEquals("Saved. See you next time.", ui.flush());
        assertEquals("", ui.flush());
    }

    @Test
    public void showTaskList_empty_saysSoWithoutNumbering() {
        ui.showTaskList(List.of());

        assertEquals("Your list is empty.", ui.flush());
    }

    @Test
    public void showTaskList_oneTask_usesSingularNoun() {
        ui.showTaskList(List.of(new Todo("read book")));

        assertEquals("1 task in your list." + newline() + "  1. [T][ ] read book", ui.flush());
    }

    @Test
    public void showTaskList_severalTasks_numbersThemFromOne() {
        ui.showTaskList(List.of(new Todo("first"), new Todo("second")));

        assertEquals("2 tasks in your list." + newline()
                + "  1. [T][ ] first" + newline()
                + "  2. [T][ ] second", ui.flush());
    }

    @Test
    public void showAdded_variousPositions_usesCorrectOrdinal() {
        assertEquals("1st", ordinalFor(1));
        assertEquals("2nd", ordinalFor(2));
        assertEquals("3rd", ordinalFor(3));
        assertEquals("4th", ordinalFor(4));
    }

    @Test
    public void showAdded_teens_useThRatherThanStNdRd() {
        assertEquals("11th", ordinalFor(11));
        assertEquals("12th", ordinalFor(12));
        assertEquals("13th", ordinalFor(13));
    }

    @Test
    public void showAdded_pastTwenty_resumesStNdRd() {
        assertEquals("21st", ordinalFor(21));
        assertEquals("22nd", ordinalFor(22));
        assertEquals("23rd", ordinalFor(23));
        assertEquals("111th", ordinalFor(111));
    }

    @Test
    public void showRemoved_oneTaskLeft_usesSingularNoun() {
        ui.showRemoved(new Todo("read book"), 1);

        assertTrue(ui.flush().startsWith("Removed. 1 task left."));
    }

    @Test
    public void showMatchingTasks_noMatches_namesTheKeyword() {
        ui.showMatchingTasks("bicycle", List.of());

        assertEquals("No matches for \"bicycle\".", ui.flush());
    }

    @Test
    public void showMatchingTasks_someMatches_countsThem() {
        ui.showMatchingTasks("book", List.of(new Todo("read book")));

        assertTrue(ui.flush().startsWith("1 task matching \"book\"."));
    }

    @Test
    public void showTasksOn_noTasks_saysNothingOnThatDate() {
        ui.showTasksOn("Dec 02 2019", List.of());

        assertEquals("Nothing on Dec 02 2019.", ui.flush());
    }

    @Test
    public void showTasksOn_someTasks_countsThem() {
        List<Task> matches = List.of(new Deadline("return book", DEC_2_6PM));
        ui.showTasksOn("Dec 02 2019", matches);

        assertTrue(ui.flush().startsWith("1 task on Dec 02 2019."));
    }

    @Test
    public void showSorted_empty_saysNothingToSort() {
        ui.showSorted(List.of());

        assertEquals("Nothing to sort.", ui.flush());
    }

    @Test
    public void showError_anyMessage_addedWithoutAPrefix() {
        ui.showError("Something went wrong.");

        assertEquals("Something went wrong.", ui.flush());
    }

    @Test
    public void showLoadingError_reportsHowManyLinesWereSkipped() {
        ui.showLoadingError(3);

        assertTrue(ui.flush().contains("Skipped 3 unreadable line(s)"));
    }

    /** Runs showAdded for the given position and pulls the ordinal back out. */
    private String ordinalFor(int position) {
        Ui local = new Ui();
        local.showAdded(new Todo("x"), position);
        String first = local.flush().split("\\R")[0];
        return first.replace("Added. Now ", "").replace(" in your list.", "");
    }

    private static String newline() {
        return System.lineSeparator();
    }
}
