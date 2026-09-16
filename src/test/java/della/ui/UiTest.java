package della.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import della.task.Deadline;
import della.task.Event;
import della.task.Todo;

public class UiTest {
    @Test
    public void ui_staticMessages_expectedTextReturned() {
        assertEquals("Hi! I'm Della :))\nHow can I help you?", UI.showWelcome());
        assertEquals("Byee! Rest well!", UI.showFarewell());
        assertEquals("error", UI.showError("error"));
    }

    @Test
    public void showTasks_tasksAreNumbered() {
        assertEquals(
                "1. [T][ ] read book\n"
                        + "2. [D][ ] read book (by Sept 09 2030 9:30pm)\n"
                        + "3. [E][ ] read book (from: Sept 09 2030 9:30pm to: Sept 09 2050 9:30pm)",
                UI.showTasks(
                        new ArrayList<>(
                                List.of(
                                        new Todo("read book"),
                                        new Deadline("read book", LocalDateTime.of(2030, 9, 9, 21, 30)),
                                        new Event(
                                                "read book",
                                                LocalDateTime.of(2030, 9, 9, 21, 30),
                                                LocalDateTime.of(2050, 9, 9, 21, 30))))));
        assertEquals("Wow you have no tasks!", UI.showTasks(new ArrayList<>()));
    }

    @Test
    public void ui_taskMessages_expectedTextReturned() {
        Todo task = new Todo("read book");
        assertEquals("Nice! I have marked this task as done:\n[T][ ] read book", UI.showMarkedTask(task));
        assertEquals("OK, I've marked this task as not done yet:\n[T][ ] read book", UI.showUnmarkedTask(task));
        assertEquals("Got it. I've added this task:\n[T][ ] read book\now you have 1 tasks in the list.",
                UI.showAddedTask(task, 1));
        assertEquals("Noted. I've removed this task:\n[T][ ] read book\nNow you have 0 tasks in the list.",
                UI.showDeletedTask(task, 0));
    }
}
