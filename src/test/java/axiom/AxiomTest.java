package axiom;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class AxiomTest {
    @TempDir
    private Path tempDir;
    private Axiom axiom;

    @BeforeEach
    void setUp() {
        axiom = new Axiom(tempDir.resolve("axiom.txt").toString());
    }

    @Test
    void getWelcomeMessage_newSession_containsGreeting() {
        String welcome = axiom.getWelcomeMessage();
        assertTrue(welcome.contains("AXIOM"));
        assertTrue(welcome.contains("What can I do for you?"));
        assertTrue(welcome.contains("_____"));
    }

    @Test
    void getGuiWelcomeMessage_newSession_omitsAsciiBanner() {
        String welcome = axiom.getGuiWelcomeMessage();
        assertTrue(welcome.contains("AXIOM"));
        assertTrue(welcome.contains("What can I do for you?"));
        assertFalse(welcome.contains("_____"));
    }

    @Test
    void getResponse_todoCommand_returnsAddedMessage() {
        String response = axiom.getResponse("todo read book");
        assertTrue(response.contains("Got it. I've added this task:"));
        assertTrue(response.contains("read book"));
        assertTrue(response.contains("Now you have 1 tasks in the list."));
        assertFalse(axiom.isExit());
    }

    @Test
    void getResponse_listAfterAdding_returnsTask() {
        axiom.getResponse("todo read book");
        String response = axiom.getResponse("list");
        assertTrue(response.contains("Here are the tasks in your list:"));
        assertTrue(response.contains("1.[T][ ] read book"));
    }

    @Test
    void getResponse_unknownCommand_returnsErrorMessage() {
        String response = axiom.getResponse("blargh");
        assertTrue(response.contains("Sorry, I don't understand that command."));
        assertFalse(axiom.isExit());
    }

    @Test
    void getReply_unknownCommand_isError() {
        AxiomReply reply = axiom.getReply("blargh");
        assertTrue(reply.isError());
        assertTrue(reply.getMessage().contains("Sorry, I don't understand that command."));
    }

    @Test
    void getReply_todoCommand_isSuccess() {
        AxiomReply reply = axiom.getReply("todo read book");
        assertFalse(reply.isError());
        assertTrue(reply.getMessage().contains("Got it. I've added this task:"));
    }

    @Test
    void getResponse_bye_setsExitFlag() {
        String response = axiom.getResponse("bye");
        assertTrue(response.contains("Bye. Hope to see you again soon!"));
        assertTrue(axiom.isExit());
    }

    @Test
    void getResponse_sortCommand_ordersTasksChronologically() {
        axiom.getResponse("todo read book");
        axiom.getResponse("deadline return book /by 2019-10-15");
        axiom.getResponse("event project meeting /from 2019-08-06 1400 /to 2019-08-06 1600");
        axiom.getResponse("deadline homework /by 2019-06-06");

        String response = axiom.getResponse("sort");
        assertTrue(response.contains("sorted your tasks chronologically"));
        assertTrue(response.indexOf("homework") < response.indexOf("project meeting"));
        assertTrue(response.indexOf("project meeting") < response.indexOf("return book"));
        assertTrue(response.indexOf("return book") < response.indexOf("read book"));

        String list = axiom.getResponse("list");
        assertTrue(list.indexOf("homework") < list.indexOf("project meeting"));
        assertTrue(list.indexOf("project meeting") < list.indexOf("return book"));
        assertTrue(list.indexOf("return book") < list.indexOf("read book"));
    }

    @Test
    void getReply_blankInput_isError() {
        AxiomReply reply = axiom.getReply("   ");
        assertTrue(reply.isError());
        assertTrue(reply.getMessage().contains("Please enter a command."));
    }

    @Test
    void getReply_listWithExtraArgument_isError() {
        AxiomReply reply = axiom.getReply("list extra");
        assertTrue(reply.isError());
        assertTrue(reply.getMessage().contains("does not take any arguments"));
    }

    @Test
    void getReply_duplicateTodo_isError() {
        axiom.getReply("todo read book");
        AxiomReply reply = axiom.getReply("todo read book");
        assertTrue(reply.isError());
        assertTrue(reply.getMessage().contains("already in your list"));
    }

    @Test
    void getReply_leadingSpaces_parsesCommand() {
        AxiomReply reply = axiom.getReply("  todo   read book  ");
        assertFalse(reply.isError());
        assertTrue(reply.getMessage().contains("read book"));
    }

    @Test
    void getReply_invalidCalendarDate_isError() {
        AxiomReply reply = axiom.getReply("deadline homework /by 2019-02-30");
        assertTrue(reply.isError());
        assertTrue(reply.getMessage().contains("not a valid date or time"));
    }

    @Test
    void getReply_eventFromAfterTo_isError() {
        AxiomReply reply = axiom.getReply(
                "event meeting /from 2019-08-06 1600 /to 2019-08-06 1400");
        assertTrue(reply.isError());
        assertTrue(reply.getMessage().contains("must be earlier than the /to time"));
    }
}
