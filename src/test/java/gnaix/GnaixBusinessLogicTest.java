package gnaix;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Locale;

import org.junit.jupiter.api.Test;

import gnaix.task.Deadline;
import gnaix.task.Event;
import gnaix.task.TaskList;
import gnaix.task.Todo;

class GnaixBusinessLogicTest {

    @Test
    void getGuiResponse_listCommand_allTasksReturnedInOrder()
            throws Exception {
        Todo todo = new Todo("buy milk");
        Deadline deadline = new Deadline(
                "submit quiz",
                LocalDate.of(2026, 9, 10));
        Gnaix gnaix = createGnaixWithTasks(todo, deadline);

        GuiResponse response = gnaix.getGuiResponse("list");

        assertFalse(response.isError());
        assertEquals("Here. Your current list:", response.getHeading());
        assertEquals(2, response.getTasks().size());
        assertEquals(1, response.getTasks().get(0).getNumber());
        assertSame(todo, response.getTasks().get(0).getTask());
        assertEquals(2, response.getTasks().get(1).getNumber());
        assertSame(deadline, response.getTasks().get(1).getTask());
    }

    @Test
    void getGuiResponse_findCommand_matchingTasksReturnedWithOriginalNumbers()
            throws Exception {
        Todo first = new Todo("buy milk");
        Todo second = new Todo("read book");
        Todo third = new Todo("buy notebook");
        Gnaix gnaix = createGnaixWithTasks(first, second, third);

        GuiResponse response = gnaix.getGuiResponse("find buy");

        assertFalse(response.isError());
        assertEquals("These are the tasks that match your search:", response.getHeading());
        assertEquals(2, response.getTasks().size());
        assertEquals(1, response.getTasks().get(0).getNumber());
        assertSame(first, response.getTasks().get(0).getTask());
        assertEquals(3, response.getTasks().get(1).getNumber());
        assertSame(third, response.getTasks().get(1).getTask());
    }

    @Test
    void getGuiResponse_findNoMatches_plainResponseReturned()
            throws Exception {
        Gnaix gnaix = createGnaixWithTasks(new Todo("buy milk"));

        GuiResponse response = gnaix.getGuiResponse("find homework");

        assertFalse(response.isError());
        assertFalse(response.hasTasks());
        assertEquals("Nothing matched that search.", response.getText());
    }

    @Test
    void getGuiResponse_turkishLocale_caseInsensitiveFindReturned()
            throws Exception {
        Locale originalLocale = Locale.getDefault();

        try {
            Locale.setDefault(Locale.forLanguageTag("tr-TR"));
            Todo todo = new Todo("INDIGO report");
            Gnaix gnaix = createGnaixWithTasks(todo);

            GuiResponse response = gnaix.getGuiResponse("find indigo");

            assertFalse(response.isError());
            assertEquals(1, response.getTasks().size());
            assertSame(todo, response.getTasks().get(0).getTask());
        } finally {
            Locale.setDefault(originalLocale);
        }
    }

    @Test
    void getGuiResponse_tagCommand_multipleTagsUsesAndSemantics()
            throws Exception {
        Todo schoolOnly = new Todo("read notes");
        schoolOnly.addTag("school");
        Todo schoolAndUrgent = new Todo("submit quiz");
        schoolAndUrgent.addTag("school");
        schoolAndUrgent.addTag("urgent");
        Gnaix gnaix = createGnaixWithTasks(schoolOnly, schoolAndUrgent);

        GuiResponse response = gnaix.getGuiResponse("tag #school #urgent");

        assertFalse(response.isError());
        assertEquals("These are the tasks carrying those tags:", response.getHeading());
        assertEquals(1, response.getTasks().size());
        assertEquals(2, response.getTasks().get(0).getNumber());
        assertSame(schoolAndUrgent, response.getTasks().get(0).getTask());
    }

    @Test
    void getGuiResponse_dateCommand_matchingDatedTasksReturned()
            throws Exception {
        Todo todo = new Todo("buy milk");
        Deadline deadline = new Deadline(
                "submit quiz",
                LocalDate.of(2026, 9, 10));
        Event event = new Event(
                "workshop",
                LocalDateTime.of(2026, 9, 10, 9, 0),
                LocalDateTime.of(2026, 9, 10, 11, 0));
        Gnaix gnaix = createGnaixWithTasks(todo, deadline, event);

        GuiResponse response = gnaix.getGuiResponse("date 2026-09-10");

        assertFalse(response.isError());
        assertEquals("These are the tasks scheduled for that date:", response.getHeading());
        assertEquals(2, response.getTasks().size());
        assertEquals(2, response.getTasks().get(0).getNumber());
        assertSame(deadline, response.getTasks().get(0).getTask());
        assertEquals(3, response.getTasks().get(1).getNumber());
        assertSame(event, response.getTasks().get(1).getTask());
    }

    @Test
    void getGuiResponse_invalidCommand_errorResponseReturned()
            throws Exception {
        Gnaix gnaix = createGnaixWithTasks();

        GuiResponse response = gnaix.getGuiResponse("nonsense");

        assertTrue(response.isError());
        assertFalse(response.hasTasks());
        assertEquals(
                "I don't know what that means."
                        + System.lineSeparator()
                        + "Try a valid command.",
                response.getText());
    }

    @Test
    void getGuiResponse_markInvalidIndex_errorResponseReturned()
            throws Exception {
        Gnaix gnaix = createGnaixWithTasks(new Todo("buy milk"));

        GuiResponse response = gnaix.getGuiResponse("mark 2");

        assertTrue(response.isError());
        assertFalse(response.hasTasks());
        assertEquals(
                "Which task?"
                        + System.lineSeparator()
                        + "You'll need to give me a task number.",
                response.getText());
    }

    private Gnaix createGnaixWithTasks(gnaix.task.Task... tasks)
            throws Exception {
        Gnaix gnaix = new Gnaix();
        TaskList taskList = new TaskList();

        for (gnaix.task.Task task : tasks) {
            taskList.add(task);
        }

        Field tasksField = Gnaix.class.getDeclaredField("tasks");
        tasksField.setAccessible(true);
        tasksField.set(gnaix, taskList);

        return gnaix;
    }
}
