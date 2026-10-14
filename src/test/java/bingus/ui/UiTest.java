package bingus.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import bingus.task.Deadline;
import bingus.task.Event;
import bingus.task.Task;
import bingus.task.TaskList;
import bingus.task.Todo;

/**
 * Tests the user-facing messages formatted by {@link Ui}.
 */
class UiTest {
    private final Ui ui = new Ui();

    /**
     * Verifies that the user interface provides the expected welcome message.
     */
    @Test
    void getWelcomeMessage_returnsIntroduction() {
        assertEquals("Hi! I'm Bingus, your task-watching cat.\n"
                + "Tell me what you need to remember, and I'll keep an eye on it.",
                ui.getWelcomeMessage());
    }

    @Test
    void getTaskAddedMessage_differentTaskCounts_usesCorrectGrammar() {
        Todo task = new Todo("read book");

        assertEquals("Got it! I added this task:\n[T][○] read book\n"
                + "You now have 1 task in your list.", ui.getTaskAddedMessage(task, 1));
        assertEquals("Got it! I added this task:\n[T][○] read book\n"
                + "You now have 2 tasks in your list.", ui.getTaskAddedMessage(task, 2));
    }

    @Test
    void getDeleteTaskMessage_deletedTask_returnsFriendlyConfirmation() {
        Todo task = new Todo("read book");

        assertEquals("Done! I removed this task:\n[T][○] read book\n"
                + "You now have 0 tasks in your list.", ui.getDeleteTaskMessage(task, 0));
    }

    @Test
    void getTaskStatusMessages_task_returnsPersonalityResponses() {
        Todo task = new Todo("read book");
        task.mark();

        assertEquals("Purrfect! This task is complete:\n[T][✓] read book",
                ui.getTaskMarkedMessage(task));
        task.unmark();
        assertEquals("No problem! This task is active again:\n[T][○] read book",
                ui.getTaskUnmarkedMessage(task));
        assertEquals("Got it! I updated this task:\n[T][○] read book",
                ui.getTaskUpdatedMessage(task));
    }

    @Test
    void getTaskListMessage_emptyList_returnsEmptyListMessage() {
        assertEquals("Your task list is empty. Time for a cat nap!",
                ui.getTaskListMessage(new TaskList()));
    }

    @Test
    void getTaskListMessage_tasks_returnsNumberedTasks() {
        TaskList tasks = new TaskList(List.of(new Todo("read book"), new Todo("return book")));

        assertEquals("Here are the tasks I am watching:\n"
                + "1. [T][○] read book\n"
                + "2. [T][○] return book", ui.getTaskListMessage(tasks));
    }

    @Test
    void getFilteredTaskListMessage_matchingDeadline_preservesOriginalTaskNumber() {
        LocalDate date = LocalDate.of(2026, 9, 20);
        Deadline deadline = new Deadline("submit report", LocalDateTime.of(2026, 9, 20, 18, 0));
        TaskList tasks = new TaskList(List.of(new Todo("read book"), deadline));

        assertEquals("Here are the tasks I am watching on Sep 20 2026:\n"
                + "2. [D][○] submit report (by: Sep 20 2026, 6:00 PM)",
                ui.getFilteredTaskListMessage(tasks, date));
    }

    @Test
    void getFilteredTaskListMessage_noMatches_returnsDateSpecificMessage() {
        LocalDate date = LocalDate.of(2026, 9, 20);
        TaskList tasks = new TaskList(List.of(new Todo("read book")));

        assertEquals("I am not watching any tasks on Sep 20 2026.",
                ui.getFilteredTaskListMessage(tasks, date));
    }

    /**
     * Verifies that a multi-day event appears when listing a date between its start and end dates.
     */
    @Test
    void getFilteredTaskListMessage_dateWithinEvent_returnsFormattedEvent() {
        LocalDate date = LocalDate.of(2026, 9, 20);
        Event event = new Event("camp", LocalDateTime.of(2026, 9, 19, 9, 0),
                LocalDateTime.of(2026, 9, 21, 18, 0));
        TaskList tasks = new TaskList(List.of(new Todo("read book"), event));

        assertEquals("Here are the tasks I am watching on Sep 20 2026:\n"
                + "2. [E][○] camp (from: Sep 19 2026, 9:00 AM to: Sep 21 2026, 6:00 PM)",
                ui.getFilteredTaskListMessage(tasks, date));
    }

    @Test
    void getMatchingTasksMessage_matches_returnsNumberedTasks() {
        List<Task> tasks = List.of(new Todo("read book"), new Todo("return book"));

        assertEquals("I found these matching tasks:\n"
                + "1. [T][○] read book\n"
                + "2. [T][○] return book", ui.getMatchingTasksMessage(tasks, "book"));
    }

    @Test
    void getMatchingTasksMessage_noMatches_returnsSearchSpecificMessage() {
        assertEquals("I could not find anything matching `book`.",
                ui.getMatchingTasksMessage(List.of(), "book"));
    }

    @Test
    void getByeMessage_returnsBingusFarewell() {
        assertEquals("Bingus is curling up for a nap. See you soon!", ui.getByeMessage());
    }
}
