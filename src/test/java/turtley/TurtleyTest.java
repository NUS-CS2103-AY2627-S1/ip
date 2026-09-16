package turtley;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests the application facade used by both the command-line and graphical interfaces.
 */
class TurtleyTest {

    @TempDir
    Path temporaryDirectory;

    @Test
    void getResponse_addTask_returnsResponseAndPersistsTask() {
        Turtley turtley = createTurtley();

        String response = turtley.getResponse("todo read book");

        assertEquals(String.join(System.lineSeparator(),
                "Got it. I've added this task:",
                "  [T][ ] read book",
                "Now you have 1 tasks in the list."), response);
        assertTrue(turtley.getResponse("list").contains("1.[T][ ] read book"));
    }

    @Test
    void getResponse_taggedTask_supportsTaggingAndFiltering() {
        Turtley turtley = createTurtley();

        assertEquals(String.join(System.lineSeparator(),
                "Got it. I've added this task:",
                "  [T][ ] go jogging [#fun] [#health]",
                "Now you have 1 tasks in the list."), turtley.getResponse(
                "todo go jogging /tag #health #fun"));
        assertTrue(turtley.getResponse("find FUN").contains("[#fun]"));
        assertTrue(turtley.getResponse("filter #HE").contains("[#health]"));
        assertTrue(turtley.getResponse("list").contains("1.[T][ ] go jogging [#fun] [#health]"));
    }

    @Test
    void getResponse_list_returnsTasksWithoutConsoleSeparators() {
        Turtley turtley = createTurtley();
        turtley.getResponse("todo read book");

        String response = turtley.getResponse("list");

        assertEquals(String.join(System.lineSeparator(),
                "Here are the tasks in your list:",
                " 1.[T][ ] read book"), response);
    }

    @Test
    void getResponse_invalidCommand_returnsUserFacingError() {
        Turtley turtley = createTurtley();

        String response = turtley.getResponse("unknown command");

        assertEquals("Please input something correct. o/T" + (char) 92 + ">", response);
    }

    @Test
    void getResponseDetails_invalidCommand_marksResponseAsError() {
        Turtley turtley = createTurtley();

        Turtley.Response response = turtley.getResponseDetails("unknown command");

        assertTrue(response.isError());
        assertEquals("Please input something correct. o/T" + (char) 92 + ">", response.text());
    }

    @Test
    void getResponseDetails_validCommand_doesNotMarkResponseAsError() {
        Turtley turtley = createTurtley();

        Turtley.Response response = turtley.getResponseDetails("help");

        assertTrue(!response.isError());
    }

    @Test
    void getResponse_exit_returnsGoodbyeMessage() {
        Turtley turtley = createTurtley();

        String response = turtley.getResponse("bye");

        assertEquals("Bye. See you around! o/T" + (char) 92 + ">", response);
    }

    @Test
    void getWelcomeMessage_returnsBannerAndGreeting() {
        Turtley turtley = createTurtley();

        String response = turtley.getWelcomeMessage();
        assertTrue(response.startsWith("      _____________"));

        assertTrue(response.contains("Turtley"));
        assertTrue(response.contains("Hello! I'm Turtley."));
        assertTrue(response.contains("What can I do for you?"));
    }

    private Turtley createTurtley() {
        Path saveFile = temporaryDirectory.resolve("turtley.txt");
        return new Turtley(saveFile.toString());
    }
}
