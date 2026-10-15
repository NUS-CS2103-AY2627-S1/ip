package zikiai.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import zikiai.exception.ZikiaiException;
import zikiai.task.Priority;
import zikiai.task.TaskList;
import zikiai.task.Todo;

/**
 * Tests shared response formatting used by both interfaces.
 */
class UiTest {

    @Test
    void formatTaskAdded_singularAndPluralCounts_grammarCorrect() {
        Todo task = new Todo("read book");

        assertEquals(
                "Can! I've added this task for you:\n    [T][ ] read book\n"
                        + "You have 1 task in your list now.",
                Ui.formatTaskAdded(task, 1));
        assertEquals(
                "Can! I've added this task for you:\n    [T][ ] read book\n"
                        + "You have 2 tasks in your list now.",
                Ui.formatTaskAdded(task, 2));
    }

    @Test
    void formatTaskDeleted_zeroAndSingularCounts_grammarCorrect() {
        Todo task = new Todo("read book");

        assertEquals(
                "Okay, removed this task already:\n    [T][ ] read book\n"
                        + "You have 0 tasks in your list now.",
                Ui.formatTaskDeleted(task, 0));
        assertEquals(
                "Okay, removed this task already:\n    [T][ ] read book\n"
                        + "You have 1 task in your list now.",
                Ui.formatTaskDeleted(task, 1));
    }

    @Test
    void formatTaskChanges_changedTask_responsesIncludeLatestState() {
        Todo task = new Todo("read book");
        task.markAsDone();
        assertEquals(
                "Shiok! This task is done already:\n    [T][X] read book",
                Ui.formatTaskMarked(task));

        task.markAsNotDone();
        assertEquals(
                "Okay can, this task is not done yet:\n    [T][ ] read book",
                Ui.formatTaskUnmarked(task));

        task.setPriority(Priority.HIGH);
        assertEquals(
                "Steady! I've updated this task's priority:\n    [T][HIGH][ ] read book",
                Ui.formatTaskPrioritized(task));
    }

    @Test
    void formatTaskCollections_emptyAndMatchingLists_expectedHeadingsReturned() {
        Todo first = new Todo("read book");
        Todo second = new Todo("return book");
        TaskList tasks = new TaskList(List.of(first, second));

        assertEquals(
                "Here are your tasks:\n1.[T][ ] read book\n2.[T][ ] return book",
                Ui.formatTaskList(tasks));
        assertEquals("Here are your tasks:", Ui.formatTaskList(new TaskList()));
        assertEquals(
                "Found these matching tasks for you:\n1.[T][ ] read book",
                Ui.formatMatchingTasks(new TaskList(List.of(first))));
        assertEquals("Aiyo, no matching tasks leh.", Ui.formatMatchingTasks(new TaskList()));
    }

    @Test
    void formatSessionMessages_messageAndException_expectedPersonalityReturned() {
        assertEquals(
                "Hello! I'm Zikiai, your task buddy.\nWhat do you need help with today ah?",
                Ui.getGreeting());
        assertEquals("Okay, bye bye! See you again lah.", Ui.formatGoodbye());
        assertEquals(
                "Aiyo! Something went wrong.",
                Ui.formatError(new ZikiaiException("Something went wrong.")));
    }
}
