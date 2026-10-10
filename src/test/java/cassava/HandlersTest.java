package cassava;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.junit.jupiter.api.Test;

import cassava.task.DeadlineTask;
import cassava.task.EventTask;
import cassava.task.Task;
import cassava.task.ToDoTask;
import cassava.ui.Parser;

public class HandlersTest {

    // ---------- handleInput dispatch ----------

    @Test
    public void testHandleInput_emptyCommand_isError() {
        List<Task> tasks = new ArrayList<>();
        HashMap<String, String> inputArgs = new HashMap<>();
        inputArgs.put("command", "");

        Handlers.HandlerResult result = Handlers.handleInput(tasks, inputArgs, Parser.VALID_CMDS);

        assertTrue(result.isError());
        assertFalse(result.isExit());
    }

    @Test
    public void testHandleInput_unrecognisedCommand_isError() {
        List<Task> tasks = new ArrayList<>();
        HashMap<String, String> inputArgs = new HashMap<>();
        inputArgs.put("command", "frobnicate");

        Handlers.HandlerResult result = Handlers.handleInput(tasks, inputArgs, Parser.VALID_CMDS);

        assertTrue(result.isError());
        assertFalse(result.isExit());
    }

    @Test
    public void testHandleInput_validCommand_routesCorrectly() {
        List<Task> tasks = new ArrayList<>();
        HashMap<String, String> inputArgs = Parser.parseUserInput("todo buy milk");

        Handlers.HandlerResult result = Handlers.handleInput(tasks, inputArgs, Parser.VALID_CMDS);

        assertFalse(result.isError());
        assertFalse(result.isExit());
        assertEquals(1, tasks.size());
    }

    @Test
    public void testHandleInput_deadlineMissingBy_isError() {
        List<Task> tasks = new ArrayList<>();
        HashMap<String, String> inputArgs = Parser.parseUserInput("deadline finish assignment");

        Handlers.HandlerResult result = Handlers.handleInput(tasks, inputArgs, Parser.VALID_CMDS);

        assertTrue(result.isError());
        assertEquals(0, tasks.size());
    }

    @Test
    public void testHandleInput_eventMissingFrom_isError() {
        List<Task> tasks = new ArrayList<>();
        HashMap<String, String> inputArgs = Parser.parseUserInput("event project meeting \\to 2026-08-29");

        Handlers.HandlerResult result = Handlers.handleInput(tasks, inputArgs, Parser.VALID_CMDS);

        assertTrue(result.isError());
        assertEquals(0, tasks.size());
    }

    @Test
    public void testHandleInput_eventMissingTo_isError() {
        List<Task> tasks = new ArrayList<>();
        HashMap<String, String> inputArgs = Parser.parseUserInput("event project meeting \\from 2026-08-28");

        Handlers.HandlerResult result = Handlers.handleInput(tasks, inputArgs, Parser.VALID_CMDS);

        assertTrue(result.isError());
        assertEquals(0, tasks.size());
    }

    // ---------- handleList ----------

    @Test
    public void testHandleList_emptyList() {
        List<Task> tasks = new ArrayList<>();

        Handlers.HandlerResult result = Handlers.handleList(tasks);

        assertFalse(result.isError());
        assertFalse(result.isExit());
        assertEquals("No tasks have been added yet...", result.message());
    }

    @Test
    public void testHandleList_nonEmptyList_joinedAndNumbered() {
        List<Task> tasks = new ArrayList<>(Arrays.asList(
                new ToDoTask("water the plants"),
                new ToDoTask("water the flowers")
        ));

        Handlers.HandlerResult result = Handlers.handleList(tasks);

        String expected = "1. " + tasks.get(0) + "\n2. " + tasks.get(1);
        assertFalse(result.isError());
        assertEquals(expected, result.message());
    }

    // ---------- handleFind ----------

    @Test
    public void testHandleFind_matchFound() {
        List<Task> tasks = new ArrayList<>(Arrays.asList(
                new ToDoTask("water the plants"),
                new ToDoTask("finish assignment")
        ));

        Handlers.HandlerResult result = Handlers.handleFind(tasks, "plants");

        assertFalse(result.isError());
        assertTrue(result.message().contains("water the plants"));
        assertFalse(result.message().contains("finish assignment"));
    }

    @Test
    public void testHandleFind_noMatch() {
        List<Task> tasks = new ArrayList<>(Arrays.asList(
                new ToDoTask("water the plants")
        ));

        Handlers.HandlerResult result = Handlers.handleFind(tasks, "nonexistent");

        assertFalse(result.isError());
        assertEquals("No tasks were found...", result.message());
    }

    @Test
    public void testHandleFind_partialSubstringMatch() {
        List<Task> tasks = new ArrayList<>(Arrays.asList(
                new ToDoTask("water the plants")
        ));

        Handlers.HandlerResult result = Handlers.handleFind(tasks, "plant");

        assertFalse(result.isError());
        assertTrue(result.message().contains("water the plants"));
    }

    // ---------- handleAddTodo ----------

    @Test
    public void testHandleAddTodo() {
        List<Task> expected = new ArrayList<>(Arrays.asList(
                new ToDoTask("water the plants"),
                new ToDoTask("water the flowers")
        ));
        List<Task> tasks = new ArrayList<>(Arrays.asList(
                new ToDoTask("water the plants")
        ));

        Handlers.HandlerResult result = Handlers.handleAddTodo(tasks, "water the flowers");

        assertFalse(result.isError());
        assertFalse(result.isExit());
        assertEquals(expected.size(), tasks.size());
        for (int i = 0; i < tasks.size(); i++) {
            assertTrue(tasks.get(i).equals(expected.get(i)));
        }
    }

    // ---------- handleAddDeadline ----------

    @Test
    public void testHandleAddDeadline() {
        List<Task> expected = new ArrayList<>(Arrays.asList(
                new ToDoTask("water the plants"),
                new DeadlineTask("finish assignment", LocalDate.parse("2026-08-28"))
        ));
        List<Task> tasks = new ArrayList<>(Arrays.asList(
                new ToDoTask("water the plants")
        ));

        Handlers.HandlerResult result = Handlers.handleAddDeadline(tasks,
                "finish assignment",
                "2026-08-28");

        assertFalse(result.isError());
        assertFalse(result.isExit());
        assertEquals(expected.size(), tasks.size());
        for (int i = 0; i < tasks.size(); i++) {
            assertTrue(tasks.get(i).equals(expected.get(i)));
        }
    }

    @Test
    public void testHandleAddDeadline_invalidDate() {
        List<Task> tasks = new ArrayList<>();

        Handlers.HandlerResult result = Handlers.handleAddDeadline(tasks,
                "finish assignment",
                "this date cannot be parsed");

        assertTrue(result.isError());
        assertFalse(result.isExit());
        assertEquals(0, tasks.size());
    }

    @Test
    public void testHandleAddDeadline_malformedDate() {
        List<Task> tasks = new ArrayList<>();

        Handlers.HandlerResult result = Handlers.handleAddDeadline(tasks,
                "finish assignment",
                "2026-13-40");

        assertTrue(result.isError());
        assertEquals(0, tasks.size());
    }

    // ---------- handleAddEvent ----------

    @Test
    public void testHandleAddEvent() {
        List<Task> expected = new ArrayList<>(Arrays.asList(
                new ToDoTask("water the plants"),
                new EventTask("project meeting",
                        LocalDate.parse("2026-08-28"),
                        LocalDate.parse("2026-08-29"))
        ));
        List<Task> tasks = new ArrayList<>(Arrays.asList(
                new ToDoTask("water the plants")
        ));

        Handlers.HandlerResult result = Handlers.handleAddEvent(tasks,
                "project meeting",
                "2026-08-28",
                "2026-08-29");

        assertFalse(result.isError());
        assertFalse(result.isExit());
        assertEquals(expected.size(), tasks.size());
        for (int i = 0; i < tasks.size(); i++) {
            assertTrue(tasks.get(i).equals(expected.get(i)));
        }
    }

    @Test
    public void testHandleAddEvent_invalidFromDate() {
        List<Task> tasks = new ArrayList<>();

        Handlers.HandlerResult result = Handlers.handleAddEvent(tasks,
                "project meeting",
                "this date cannot be parsed",
                "2026-08-29");

        assertTrue(result.isError());
        assertEquals(0, tasks.size());
    }

    @Test
    public void testHandleAddEvent_invalidToDate() {
        List<Task> tasks = new ArrayList<>();

        Handlers.HandlerResult result = Handlers.handleAddEvent(tasks,
                "project meeting",
                "2026-08-28",
                "this date cannot be parsed");

        assertTrue(result.isError());
        assertEquals(0, tasks.size());
    }

    @Test
    public void testHandleAddEvent_bothDatesInvalid() {
        List<Task> tasks = new ArrayList<>();

        Handlers.HandlerResult result = Handlers.handleAddEvent(tasks,
                "project meeting",
                "not a date",
                "also not a date");

        assertTrue(result.isError());
        assertEquals(0, tasks.size());
    }

    // ---------- handleMark / handleUnmark ----------

    @Test
    public void testHandleMark_validIndex() {
        List<Task> tasks = new ArrayList<>(Arrays.asList(
                new ToDoTask("water the plants")
        ));

        Handlers.HandlerResult result = Handlers.handleMark(tasks, "1");

        assertFalse(result.isError());
        assertTrue(result.message().contains("[X]"));
    }

    @Test
    public void testHandleMark_indexTooHigh_isError() {
        List<Task> tasks = new ArrayList<>(Arrays.asList(
                new ToDoTask("water the plants")
        ));

        Handlers.HandlerResult result = Handlers.handleMark(tasks, "5");

        assertTrue(result.isError());
    }

    @Test
    public void testHandleMark_indexZero_isError() {
        List<Task> tasks = new ArrayList<>(Arrays.asList(
                new ToDoTask("water the plants")
        ));

        Handlers.HandlerResult result = Handlers.handleMark(tasks, "0");

        assertTrue(result.isError());
    }

    @Test
    public void testHandleMark_negativeIndex_isError() {
        List<Task> tasks = new ArrayList<>(Arrays.asList(
                new ToDoTask("water the plants")
        ));

        Handlers.HandlerResult result = Handlers.handleMark(tasks, "-1");

        assertTrue(result.isError());
    }

    @Test
    public void testHandleMark_nonNumericIndex_isError() {
        List<Task> tasks = new ArrayList<>(Arrays.asList(
                new ToDoTask("water the plants")
        ));

        Handlers.HandlerResult result = Handlers.handleMark(tasks, "abc");

        assertTrue(result.isError());
    }

    @Test
    public void testHandleUnmark_validIndex() {
        List<Task> tasks = new ArrayList<>(Arrays.asList(
                new ToDoTask("water the plants")
        ));
        tasks.get(0).mark();

        Handlers.HandlerResult result = Handlers.handleUnmark(tasks, "1");

        assertFalse(result.isError());
        assertTrue(result.message().contains("[ ]"));
    }

    @Test
    public void testHandleUnmark_indexOutOfBounds_isError() {
        List<Task> tasks = new ArrayList<>();

        Handlers.HandlerResult result = Handlers.handleUnmark(tasks, "1");

        assertTrue(result.isError());
    }

    // ---------- handlePrioritise ----------

    @Test
    public void testHandlePrioritise_validLevel() {
        List<Task> tasks = new ArrayList<>(Arrays.asList(
                new ToDoTask("water the plants")
        ));

        Handlers.HandlerResult result = Handlers.handlePrioritise(tasks, "1", "high");

        assertFalse(result.isError());
        assertTrue(result.message().contains("high"));
    }

    @Test
    public void testHandlePrioritise_invalidLevel_isError() {
        List<Task> tasks = new ArrayList<>(Arrays.asList(
                new ToDoTask("water the plants")
        ));

        Handlers.HandlerResult result = Handlers.handlePrioritise(tasks, "1", "urgent");

        assertTrue(result.isError());
    }

    @Test
    public void testHandlePrioritise_indexOutOfBounds_isError() {
        List<Task> tasks = new ArrayList<>();

        Handlers.HandlerResult result = Handlers.handlePrioritise(tasks, "1", "high");

        assertTrue(result.isError());
    }

    // ---------- handleDelete ----------

    @Test
    public void testHandleDelete_validIndex_removesTask() {
        List<Task> tasks = new ArrayList<>(Arrays.asList(
                new ToDoTask("water the plants"),
                new ToDoTask("water the flowers")
        ));

        Handlers.HandlerResult result = Handlers.handleDelete(tasks, "1");

        assertFalse(result.isError());
        assertEquals(1, tasks.size());
        assertTrue(tasks.get(0).equals(new ToDoTask("water the flowers")));
    }

    @Test
    public void testHandleDelete_indexOutOfBounds_isError() {
        List<Task> tasks = new ArrayList<>(Arrays.asList(
                new ToDoTask("water the plants")
        ));

        Handlers.HandlerResult result = Handlers.handleDelete(tasks, "5");

        assertTrue(result.isError());
        assertEquals(1, tasks.size());
    }

    // ---------- handleExit ----------

    @Test
    public void testHandleExit() {
        Handlers.HandlerResult result = Handlers.handleExit();

        assertFalse(result.isError());
        assertTrue(result.isExit());
        assertEquals("Bye! See you again soon.", result.message());
    }

    // ---------- handleInvalid ----------

    @Test
    public void testHandleInvalid() {
        Handlers.HandlerResult result = Handlers.handleInvalid("Something went wrong.");

        assertTrue(result.isError());
        assertFalse(result.isExit());
        assertEquals("Something went wrong.", result.message());
    }

}
