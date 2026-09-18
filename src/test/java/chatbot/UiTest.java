package chatbot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** Tests user-facing message formatting and task searches. */
class UiTest {
    private final Ui ui = new Ui();

    @Test
    void messages_includeExpectedWelcomeHelpAndExitContent() {
        assertTrue(ui.showWelcomeMessage().contains("Hello! I'm Yun."));
        assertTrue(ui.showHelp().contains("todo, event, deadline"));
        assertEquals("Buh bai. Cya again soon!\n\n", ui.showExitMessage());
    }

    @Test
    void showTasksAndFind_formatAllAndMatchingTasks() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("Read Book"));
        tasks.add(new Deadline("submit report", "2026-09-01 1800"));

        assertTrue(ui.showTasks(tasks).contains("1.[T] [ ] Read Book"));
        String matches = ui.showMatchingTasks(tasks, "book");
        assertTrue(matches.contains("1.[T] [ ] Read Book"));
        assertTrue(!matches.contains("submit report"));
    }

    @Test
    void listTasksOnDate_includesDeadlinesAndEveryDateOfMultiDayEvent() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("ordinary task"));
        tasks.add(new Deadline("deadline", "2026-09-01 1800"));
        tasks.add(new Event("cross-day event", "2026-08-30 1000", "2026-09-02 1800"));

        assertTrue(ui.listTasksOnDate(tasks, "2026-08-30").contains("cross-day event"));
        assertTrue(ui.listTasksOnDate(tasks, "2026-08-31").contains("cross-day event"));
        String middleDateResult = ui.listTasksOnDate(tasks, "2026-09-01");
        assertTrue(middleDateResult.contains("deadline"));
        assertTrue(middleDateResult.contains("cross-day event"));
        assertTrue(!middleDateResult.contains("ordinary task"));
        assertTrue(ui.listTasksOnDate(tasks, "2026-09-02").contains("cross-day event"));
    }

    @Test
    void listTasksOnDate_excludesDatesOutsideMultiDayEvent() {
        TaskList tasks = new TaskList();
        tasks.add(new Event("cross-day event", "2026-08-30 1000", "2026-09-02 1800"));

        assertTrue(!ui.listTasksOnDate(tasks, "2026-08-29").contains("cross-day event"));
        assertTrue(!ui.listTasksOnDate(tasks, "2026-09-03").contains("cross-day event"));
    }

    @Test
    void listTasksOnDate_rejectsInvalidDate() {
        assertThrows(InvalidInputException.class, () -> ui.listTasksOnDate(new TaskList(), "01-09-2026"));
    }
}
