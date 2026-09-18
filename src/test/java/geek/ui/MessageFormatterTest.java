package geek.ui;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import geek.task.Task;

/**
 * Tests user-facing task-list formatting.
 */
class MessageFormatterTest {
    @Test
    void taskList_multipleTasks_numbersAndJoinsTasksInOrder() {
        List<Task> tasks = List.of(
                Task.newTodo("read book"),
                Task.newTodo("write code")
        );

        assertEquals(
                "Here's what's on your mission board:\n"
                        + "1. [T][ ] read book\n"
                        + "2. [T][ ] write code",
                MessageFormatter.taskList(tasks)
        );
    }

    @Test
    void tasksSorted_multipleTasks_explainsOrderAndNumbersTasks() {
        List<Task> tasks = List.of(
                Task.newDeadline("submit report", "2/12/2019"),
                Task.newTodo("read book")
        );

        assertEquals(
                "Timeline aligned. Dated tasks come first, "
                        + "with undated tasks after them:\n"
                        + "1. [D][ ] submit report (by: Dec 2 2019)\n"
                        + "2. [T][ ] read book",
                MessageFormatter.tasksSorted(tasks)
        );
    }

    @Test
    void greetings_useCalmMissionControlPersonality() {
        assertAll(() -> assertEquals(
                        "Systems online. I'm Geek, "
                                + "your calm mission control.\n"
                                + "What are we tackling next?",
                        MessageFormatter.welcome()
                ), () -> assertEquals(
                        "Mission paused. Powering down for now—"
                                + "see you next time!",
                        MessageFormatter.goodbye()
                )
        );
    }

    @Test
    void taskActions_useMissionControlPersonality() {
        Task task = Task.newTodo("read book");

        assertAll(() -> assertEquals(
                        "Task logged. One less thing to keep in your head:\n"
                                + "  [T][ ] read book\n"
                                + "You now have 1 task on the mission board.",
                        MessageFormatter.taskAdded(task, 1)
                ), () -> assertEquals(
                        "No pressure. This task is back on the radar:\n"
                                + "  [T][ ] read book",
                        MessageFormatter.taskUnmarked(task)
                ), () -> assertEquals(
                        "Cleared from the mission board:\n"
                                + "  [T][ ] read book\n"
                                + "You now have 0 tasks on the board.",
                        MessageFormatter.taskDeleted(task, 0)
                )
        );
    }
}
