package benjamin;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests the entry points the graphical interface relies on.
 */
public class BenjaminTest {
    @TempDir
    Path root;

    private Benjamin chatbot() {
        return new Benjamin(root.toString(), "data", "benjamin.txt");
    }

    @Test
    public void getWelcome_freshStart_greetsWithoutTheTextBanner() {
        String welcome = chatbot().getWelcome();

        assertTrue(welcome.contains("Benjamin."));
        assertTrue(welcome.contains("I hold your list"));
        assertFalse(welcome.contains("____"));
    }

    @Test
    public void getResponse_addTask_confirmsTheTask() {
        String reply = chatbot().getResponse("todo read book");

        assertTrue(reply.contains("Fine. Added:"));
        assertTrue(reply.contains("[T][ ] read book"));
    }

    @Test
    public void getResponse_unknownCommand_reportsInsteadOfThrowing() {
        String reply = chatbot().getResponse("blah");

        assertTrue(reply.contains("I answer to list, todo"));
    }

    @Test
    public void getResponse_badTaskNumber_reportsInsteadOfThrowing() {
        String reply = chatbot().getResponse("mark 99");

        assertTrue(reply.contains("There are no tasks to mark."));
    }

    @Test
    public void getResponse_eachCall_returnsOnlyTheLatestReply() {
        Benjamin benjamin = chatbot();

        benjamin.getResponse("todo read book");
        String second = benjamin.getResponse("todo join sports club");

        assertTrue(second.contains("join sports club"));
        assertFalse(second.contains("read book"));
    }

    @Test
    public void isExit_beforeBye_isFalse() {
        Benjamin benjamin = chatbot();
        benjamin.getResponse("list");

        assertFalse(benjamin.isExit());
    }

    @Test
    public void isExit_afterBye_isTrueAndSaysGoodbye() {
        Benjamin benjamin = chatbot();
        String reply = benjamin.getResponse("bye");

        assertTrue(reply.contains("The list will keep."));
        assertTrue(benjamin.isExit());
    }

    @Test
    public void getResponse_findCommand_reportsMatches() {
        Benjamin benjamin = chatbot();
        benjamin.getResponse("todo read book");
        benjamin.getResponse("todo join sports club");

        String reply = benjamin.getResponse("find book");

        assertTrue(reply.contains("These match"));
        assertTrue(reply.contains("read book"));
        assertFalse(reply.contains("sports club"));
    }

    @Test
    public void getResponse_tagCommand_attachesAndShowsTheTag() {
        Benjamin benjamin = chatbot();
        benjamin.getResponse("todo read book");

        String reply = benjamin.getResponse("tag 1 #fun");

        assertTrue(reply.contains("Tagged. Riveting:"));
        assertTrue(reply.contains("[T][ ] read book #fun"));
    }

    @Test
    public void getResponse_tagTwice_reportsItInsteadOfDuplicating() {
        Benjamin benjamin = chatbot();
        benjamin.getResponse("todo read book");
        benjamin.getResponse("tag 1 #fun");

        String reply = benjamin.getResponse("tag 1 #FUN");

        assertTrue(reply.contains("already tagged #fun"));
        assertTrue(benjamin.getResponse("list").contains("[T][ ] read book #fun"));
    }

    @Test
    public void getResponse_untagCommand_removesTheTag() {
        Benjamin benjamin = chatbot();
        benjamin.getResponse("todo read book");
        benjamin.getResponse("tag 1 #fun");

        String reply = benjamin.getResponse("untag 1 #fun");

        assertTrue(reply.contains("Tag removed:"));
        assertFalse(reply.contains("#fun"));
    }

    @Test
    public void getResponse_untagMissingTag_reportsInsteadOfFailing() {
        Benjamin benjamin = chatbot();
        benjamin.getResponse("todo read book");

        assertTrue(benjamin.getResponse("untag 1 #nope").contains("never tagged #nope"));
    }

    @Test
    public void getResponse_findByTag_matchesTaggedTasks() {
        Benjamin benjamin = chatbot();
        benjamin.getResponse("todo read book");
        benjamin.getResponse("todo join sports club");
        benjamin.getResponse("tag 2 #fun");

        String reply = benjamin.getResponse("find #fun");

        assertTrue(reply.contains("join sports club"));
        assertFalse(reply.contains("read book"));
    }

    @Test
    public void getResponse_tagsAddedEarlier_surviveIntoANewChatbot() {
        Benjamin benjamin = chatbot();
        benjamin.getResponse("todo read book");
        benjamin.getResponse("tag 1 #fun");

        assertTrue(chatbot().getResponse("list").contains("[T][ ] read book #fun"));
    }

    @Test
    public void getResponse_tasksAddedEarlier_surviveIntoANewChatbot() {
        chatbot().getResponse("todo read book");

        assertTrue(chatbot().getResponse("list").contains("[T][ ] read book"));
    }
}
