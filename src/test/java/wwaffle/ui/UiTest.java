package wwaffle.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import wwaffle.task.Deadline;
import wwaffle.task.Event;
import wwaffle.task.Todo;

class UiTest {
    @Test
    void getTaskAddedMessage_preservesDetailsAndCountsForEachTaskType() {
        Ui ui = new Ui();
        Todo task = new Todo("read book");
        task.markAsDone();
        assertEquals("Task added. 🧁\n[T][X] read book\n2 tasks total.", ui.getTaskAddedMessage(task, 2));
        Deadline deadline = new Deadline("submit", "2026-09-18");
        assertEquals("Deadline added. 🧁\n" + deadline + "\n1 task total.",
                ui.getTaskAddedMessage(deadline, 1));
        assertEquals("Deadline added. 🧁\n" + deadline + "\n3 tasks total.",
                ui.getTaskAddedMessage(deadline, 3));
        Event event = new Event("meeting", "2pm", "3pm");
        assertEquals("Event added. 🧁\n" + event + "\n1 task total.", ui.getTaskAddedMessage(event, 1));
        assertEquals("Event added. 🧁\n" + event + "\n3 tasks total.", ui.getTaskAddedMessage(event, 3));
    }

    @Test
    void getWelcomeMessage_formatsEmptySingularAndPluralCounts() {
        Ui ui = new Ui();
        assertEquals("Welcome back. ☕\nNo tasks yet.", ui.getWelcomeMessage(0));
        assertEquals("Welcome back. ☕\n1 task loaded.", ui.getWelcomeMessage(1));
        assertEquals("Welcome back. ☕\n5 tasks loaded.", ui.getWelcomeMessage(5));
    }
}
