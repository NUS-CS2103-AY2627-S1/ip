package doe;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests chat state transitions and persistence without starting the JavaFX toolkit. */
class DoeResponseTest {
    @TempDir
    Path directory;
    private Doe doe;
    private Storage storage;

    @BeforeEach
    void setUp() {
        String file = directory.resolve("tasks.txt").toString();
        doe = new Doe(file);
        storage = new Storage(file);
    }

    @Test
    void mainMenu_commandsAndUnknownInput_keepMainMenuAvailable() {
        assertEquals(">> EURHGGGHHHH!! <<", doe.getResponse("  NEIGH  "));
        assertEquals(">> MEOW! (=^･ω･^=)", doe.getResponse("2"));
        assertEquals("roles and responsibilities\n1. survive nus cs\n2. get a few internships\n"
                + "3. work at mcdonalds\n4. retire as a manager (hopefully)", doe.getResponse("list"));
        assertEquals("horh\n\n" + doe.getWelcomeMessage(), doe.getResponse("nonsense"));
        assertEquals("bye. hope to see you again soon!", doe.getResponse("BYE"));
    }

    @Test
    void nestedMenus_invalidCommandsAndCancellation_preserveCorrectState() {
        String menu = doe.getResponse("todo");
        assertEquals("modify todo list\n1. add\n2. remove\n3. view\n4. sort\n"
                + "5. mark\n6. unmark\n7. find\n8. exit", menu);
        assertEquals("that option is incorrect. please choose one of the todo options below.\n\n" + menu,
                doe.getResponse("wrong"));
        assertEquals("what type of task would you like to add?\n1. todo\n2. deadline\n3. event\n4. exit",
                doe.getResponse("add"));
        assertEquals("that option is incorrect. please choose 1, 2, 3, todo, deadline, or event.",
                doe.getResponse("wrong"));
        assertEquals(menu, doe.getResponse("exit"));
        assertEquals(doe.getWelcomeMessage(), doe.getResponse("exit"));
        assertTrue(storage.load().isEmpty());
    }

    @Test
    void add_eachTaskType_persistsNormalizedDetails() {
        beginAdd("todo");
        assertSaved(doe.getResponse("  阅读 book  "));
        beginAdd("deadline");
        assertEquals("input deadline in format dd-mm-yyyy hhmm (e.g. 17-07-2004 1800):",
                doe.getResponse("report"));
        assertSaved(doe.getResponse(" 29-02-2024   0000 "));
        beginAdd("event");
        assertEquals("input event time in format dd-mm-yyyy hhmm (e.g. 17-07-2004 1100):",
                doe.getResponse("meeting"));
        assertSaved(doe.getResponse("31-12-2026 2359"));
        assertEquals(List.of("TODO|false|阅读 book", "DEADLINE|false|report|29-02-2024 0000",
                "EVENT|false|meeting|31-12-2026 2359"), records());
    }

    @Test
    void add_invalidDescriptionsCanRetryAndDuplicateIsSaved() {
        beginAdd("todo");
        assertEquals("error: input cannot contain '|' character.\n\nwhat you want to add?",
                doe.getResponse("bad|name"));
        assertEquals("error: task description cannot be blank.", doe.getResponse(" "));
        assertEquals("error: task description cannot contain '|', or line breaks.", doe.getResponse("a\nb"));
        assertSaved(doe.getResponse("first"));
        beginAdd("todo");
        assertSaved(doe.getResponse("FIRST"));
        assertEquals(List.of("TODO|false|first", "TODO|false|FIRST"), records());
    }

    @Test
    void add_invalidDatesCanRetryAndDatedDuplicateIsSaved() {
        for (String type : List.of("deadline", "event")) {
            beginAdd(type);
            doe.getResponse("appointment");
            assertEquals("error: input cannot contain '|' character.", doe.getResponse("bad|date"));
            assertEquals("invalid date format. please use dd-mm-yyyy hhmm (e.g. 17-07-2004 1800).",
                    doe.getResponse("29-02-2025 1200"));
            assertSaved(doe.getResponse("01-01-2027 1200"));
            beginAdd(type);
            doe.getResponse("appointment");
            assertSaved(doe.getResponse("01-01-2027 1200"));
        }
        assertEquals(4, storage.load().size());
    }

    @Test
    void markAndUnmark_validNumber_persistAndReturnToMainMenu() {
        beginAdd("todo");
        doe.getResponse("read");
        enterTodoCommand("mark");
        assertEquals("nice! i've marked this task as done:\n[x] read\n\n"
                + "here are the tasks in your list:\n1. [t][x] read\n\n" + doe.getWelcomeMessage(),
                doe.getResponse(" 1 "));
        assertEquals(List.of("TODO|true|read"), records());
        enterTodoCommand("unmark");
        assertEquals("ok i've marked this task as not done yet:\n[] read\n\n"
                + "here are the tasks in your list:\n1. [t][ ] read\n\n" + doe.getWelcomeMessage(),
                doe.getResponse("1"));
        assertEquals(List.of("TODO|false|read"), records());
    }

    @Test
    void markAndUnmark_emptyOrInvalidNumber_leaveTasksUnchanged() {
        for (String command : List.of("mark", "unmark")) {
            enterTodoCommand(command);
            assertEquals("there are no tasks to mark yet. add a task first.\n\n" + doe.getWelcomeMessage(),
                    doe.getResponse("1"));
        }
        beginAdd("todo");
        doe.getResponse("read");
        for (String input : List.of("0", "-1", "2", "2147483648", "read", "")) {
            enterTodoCommand("mark");
            assertEquals("that task number is incorrect. please enter a number from 1 to 1.\n\n"
                    + doe.getWelcomeMessage(), doe.getResponse(input));
        }
        assertEquals(List.of("TODO|false|read"), records());
    }

    @Test
    void remove_nameNumberAndMissingTask_persistOnlySuccessfulRemoval() {
        beginAdd("todo");
        doe.getResponse("first");
        beginAdd("todo");
        doe.getResponse("second");
        enterTodoCommand("remove");
        assertEquals("could not find a task with that name or number. please try again.\n\n"
                + doe.getWelcomeMessage(), doe.getResponse("missing"));
        assertEquals(2, storage.load().size());
        enterTodoCommand("remove");
        assertEquals("item removed successfully!\n[t][ ] first\n\n" + doe.getWelcomeMessage(),
                doe.getResponse("first"));
        enterTodoCommand("remove");
        assertTrue(doe.getResponse("1").startsWith("item removed successfully!\n[t][ ] second"));
        assertTrue(storage.load().isEmpty());
    }

    @Test
    void remove_ambiguousName_canRetryWithNumber() {
        beginAdd("todo");
        doe.getResponse("same");
        beginAdd("event");
        doe.getResponse("same");
        doe.getResponse("01-01-2027 1200");
        enterTodoCommand("remove");
        assertEquals("error: multiple tasks have that name; enter a task number.", doe.getResponse("same"));
        assertTrue(doe.getResponse("2").startsWith("item removed successfully!"));
        assertEquals(List.of("TODO|false|same"), records());
    }

    @Test
    void viewAndFind_emptyAndNoMatches_returnToMainMenu() {
        assertEquals("here are the tasks in your list:\n\n" + doe.getWelcomeMessage(), enterTodoCommand("view"));
        enterTodoCommand("find");
        assertEquals("no matching tasks found.\n\n" + doe.getWelcomeMessage(), doe.getResponse("book"));
        beginAdd("todo");
        doe.getResponse("read book");
        assertTrue(enterTodoCommand("view").contains("1. [t][ ] read book"));
        enterTodoCommand("find");
        assertFalse(doe.getResponse("missing").contains("1. [t]"));
    }

    private void beginAdd(String type) {
        enterTodoCommand("add");
        assertEquals("what you want to add?", doe.getResponse(type));
    }

    private String enterTodoCommand(String command) {
        doe.getResponse("todo");
        return doe.getResponse(command);
    }

    private void assertSaved(String response) {
        assertEquals("item saved successfully!\n\n" + doe.getWelcomeMessage(), response);
    }

    private List<String> records() {
        return storage.load().stream().map(Task::toFileFormat).toList();
    }
}
