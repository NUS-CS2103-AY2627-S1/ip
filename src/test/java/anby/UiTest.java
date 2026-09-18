package anby;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import anby.task.Task;
import anby.task.Todo;

public class UiTest {
    @Test
    public void getGreeting_returnsBannerAndIntro() {
        Ui ui = new Ui();

        assertTrue(ui.getGreeting().contains("Hey, I'm Anby"));
        assertTrue(ui.getGreeting().contains("What do you need me for?"));
    }

    @Test
    public void getGoodbye_returnsGoodbyeMessage() {
        Ui ui = new Ui();

        assertEquals("Alright see you. Don't forget my burgers okay", ui.getGoodbye());
    }

    @Test
    public void getList_emptyTaskList_returnsEmptyMessage() {
        Ui ui = new Ui();
        TaskList tasks = new TaskList(new ArrayList<>());

        assertEquals("lol you have no tasks!\n", ui.getList(tasks));
    }

    @Test
    public void getList_nonEmptyTaskList_returnsNumberedTasks() {
        Ui ui = new Ui();
        TaskList tasks = new TaskList(new ArrayList<>());

        tasks.add(new Todo("eat burger"), new Todo("drink tea"));

        assertEquals("finish these and then reward me with burgers:\n"
                + "1. [T][ ] eat burger\n"
                + "2. [T][ ] drink tea\n"
                + "\nmanageable combo meal, chef.\n", ui.getList(tasks));
    }

    @Test
    public void getMarked_task_returnsMarkedMessage() {
        Ui ui = new Ui();
        Task task = new Todo("eat burger");

        task.markAsDone();

        assertEquals("ooo you're done with this! that'll be one burger please:\n"
                + "[T][X] eat burger", ui.getMarked(task));
    }

    @Test
    public void getUnmarked_task_returnsUnmarkedMessage() {
        Ui ui = new Ui();
        Task task = new Todo("eat burger");

        assertEquals("hey why didn't you do this already?\n[T][ ] eat burger", ui.getUnmarked(task));
    }

    @Test
    public void getDeleted_task_returnsDeletedMessage() {
        Ui ui = new Ui();
        Task task = new Todo("eat burger");

        assertEquals("okay i've taken away this task for you:\n[T][ ] eat burger", ui.getDeleted(task));
    }

    @Test
    public void getAdded_task_returnsAddedMessageWithTaskCount() {
        Ui ui = new Ui();
        Task task = new Todo("eat burger");

        assertEquals("okay, this is a new task: \n"
                + "[T][ ] eat burger"
                + "\nyou've got 1 task(s) waiting for you..."
                + "\n\njust one item, light snack.", ui.getAdded(task, 1));
    }

    @Test
    public void getFindResults_emptyTaskList_returnsEmptyMessage() {
        Ui ui = new Ui();
        TaskList tasks = new TaskList(new ArrayList<>());

        assertEquals("i dug up all the nearest burger chains and found nothing oopsie\n"
                + " try a smaller keyword maybe", ui.getFindResults(tasks));
    }

    @Test
    public void getFindResults_nonEmptyTaskList_returnsMatchingTasks() {
        Ui ui = new Ui();
        TaskList tasks = new TaskList(new ArrayList<>());

        tasks.add(new Todo("eat burger"));

        assertEquals("heres what i found:\n"
                + "1. [T][ ] eat burger\n", ui.getFindResults(tasks));
    }

    @Test
    public void getError_message_returnsMessage() {
        Ui ui = new Ui();

        assertEquals("bad input", ui.getError("bad input"));
    }
}
