package rice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests the command-processing logic in Rice without exercising the JavaFX GUI. */
public class RiceTest {

    @TempDir
    Path temporaryDirectory;

    @Test
    void getResponseAddTodoAndListReturnsTaskInformation() throws Exception {
        Rice rice = createFreshRice();

        String addResponse = rice.getResponse("todo Read book");
        assertTrue(addResponse.contains("Got it. I've added this task:"));
        assertTrue(addResponse.contains("Read book"));

        String listResponse = rice.getResponse("list");
        assertTrue(listResponse.contains("1.[T][ ] Read book"));
    }

    @Test
    void getResponseMarkAndDeleteUpdatesTaskState() throws Exception {
        Rice rice = createFreshRice();
        rice.getResponse("todo Buy rice");

        String markResponse = rice.getResponse("mark 1");
        assertTrue(markResponse.contains("Nice! I've marked this task as done, we cooked this rice:"));
        assertTrue(markResponse.contains("[X] Buy rice"));

        String deleteResponse = rice.getResponse("delete 1");
        assertTrue(deleteResponse.contains("Removed:"));
        assertTrue(deleteResponse.contains("Buy rice"));

        String listResponse = rice.getResponse("list");
        assertEquals("There are no tasks. Our bowl is empty :(", listResponse);
    }

    @Test
    void getResponseUnmarkTaskSetsTaskBackToIncomplete() throws Exception {
        Rice rice = createFreshRice();
        rice.getResponse("todo Buy rice");
        rice.getResponse("mark 1");

        String unmarkResponse = rice.getResponse("unmark 1");
        assertTrue(unmarkResponse.contains("OK, I've marked this task as not done yet, the rice is RAW!!:"));
        assertTrue(unmarkResponse.contains("[ ] Buy rice"));
    }

    @Test
    void getResponseListOnEmptyTaskListReturnsEmptyListMessage() throws Exception {
        Rice rice = createFreshRice();

        assertEquals("There are no tasks. Our bowl is empty :(", rice.getResponse("list"));
    }

    @Test
    void getResponseSortOnEmptyTaskListReturnsEmptyListMessage() throws Exception {
        Rice rice = createFreshRice();

        assertEquals("There are no tasks. Our bowl is empty :(", rice.getResponse("sort"));
    }

    @Test
    void getResponseFindAndUnknownCommandProduceExpectedOutputs() throws Exception {
        Rice rice = createFreshRice();
        rice.getResponse("todo Read book");
        rice.getResponse("todo Buy rice");

        String findResponse = rice.getResponse("find rice");
        assertTrue(findResponse.contains("Buy rice"));
        assertTrue(findResponse.contains("Read book") || findResponse.contains("Buy rice"));

        String unknownResponse = rice.getResponse("hello");
        assertEquals("I'm sorry, but I dont know what that means :-(", unknownResponse);
    }

    @Test
    void getResponseSortCommandOrdersTasksByDeadline() throws Exception {
        Rice rice = createFreshRice();

        rice.getResponse("deadline Return book /by 2026-12-20");
        rice.getResponse("deadline Submit report /by 2026-12-10");

        String sortResponse = rice.getResponse("sort");
        assertTrue(sortResponse.contains("Sorted tasks by deadline."));

        String listResponse = rice.getResponse("list");
        assertTrue(listResponse.indexOf("Submit report") < listResponse.indexOf("Return book"));
    }

    @Test
    void getResponseSortCommandSavesSortedOrderToStorage() throws Exception {
        Rice rice = createFreshRice();

        rice.getResponse("deadline Return book /by 2026-12-20");
        rice.getResponse("deadline Submit report /by 2026-12-10");

        String sortResponse = rice.getResponse("sort");
        assertEquals("Sorted tasks by deadline.", sortResponse);

        Storage storage = (Storage) getField(rice, "storage");
        List<String> savedLines = Files.readAllLines(storage.getPath());
        assertEquals(List.of(
                "D|0|Submit report|2026-12-10",
                "D|0|Return book|2026-12-20"), savedLines);
    }

    @Test
    void getSuggestionReturnsMatchingAdviceForErrors() throws Exception {
        Rice rice = createFreshRice();

        assertEquals("Try list, find, sort, todo, deadline, event, mark, unmark, or delete.",
                rice.getSuggestion("I'm sorry, but I dont know what that means :-("));
        assertEquals("Try: find <keyword>, e.g. find rice",
                rice.getSuggestion("Please provide a keyword to search for"));
        assertEquals("Use the date format yyyy-MM-DD, e.g. 2026-12-12",
                rice.getSuggestion("Invalid date. Use yyyy-MM-dd, for example 2026-12-12."));
    }

    private Rice createFreshRice() throws Exception {
        Rice rice = new Rice();
        setField(rice, "storage", new Storage(temporaryDirectory.toString(), "tasks.txt"));
        setField(rice, "tasks", new TaskList());
        setField(rice, "hasLoadedSavedTasks", true);
        return rice;
    }

    private static void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = Rice.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    private static Object getField(Object target, String fieldName) throws Exception {
        Field field = Rice.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(target);
    }
}
