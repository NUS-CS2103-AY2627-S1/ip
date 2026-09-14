package arthur.messages;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import arthur.exceptions.InvalidArgumentException;
import arthur.tasks.TodoTask;

// AI-assisted: Tests non-UI message formatting, final state, and CSS-class contracts.
public class MessageTest {
    @Test
    public void welcomeAndByeMessages_returnExpectedTextAndFinalState() {
        WelcomeMessage welcome = new WelcomeMessage();
        ByeMessage bye = new ByeMessage();

        assertEquals("I'm Dent, Arthur Dent. Would it save you a lot of time\n"
                + "if I just gave up and went mad now?\n", welcome.toString());
        assertFalse(welcome.isFinal());
        assertEquals("So long, and thanks for all the fish.", bye.toString());
        assertTrue(bye.isFinal());
    }

    @Test
    public void taskMessages_formatTheirTaskAndAreNotFinal() {
        TodoTask task = new TodoTask("read book");

        assertMatchesQuoteAndBody(new AddMessage(task), "added:\n[T][ ] read book");
        assertMatchesQuoteAndBody(new MarkMessage(task), "marked:\n[T][ ] read book");
        assertMatchesQuoteAndBody(new RemoveMessage(task), "removed:\n[T][ ] read book");
        assertMatchesQuoteAndBody(new UnmarkMessage(task), "unmarked:\n[T][ ] read book");
    }

    @Test
    public void listMessage_formatsEmptyAndNumberedTaskLists() {
        assertMatchesQuoteAndBody(new ListMessage(List.of()), "No tasks to display.");
        assertMatchesQuoteAndBody(new ListMessage(List.of(new TodoTask("read book"), new TodoTask("write code"))),
                "1. [T][ ] read book\n2. [T][ ] write code");
    }

    @Test
    public void aliasMessage_formatsChangesAndSortedAliases() {
        assertMatchesQuoteAndBody(new AliasMessage("t", "todo"), "alias added:\nt -> todo");
        assertMatchesQuoteAndBody(new AliasMessage("t"), "alias removed:\nt");
        assertMatchesQuoteAndBody(new AliasMessage(Map.of()), "aliases:\nNo aliases to display.");
        assertMatchesQuoteAndBody(new AliasMessage(Map.of("z", "zoom", "a", "add")),
                "aliases:\na -> add\nz -> zoom");
    }

    @Test
    public void errorMessage_formatsExceptionAndProvidesErrorCssClass() {
        ErrorMessage message = new ErrorMessage(new InvalidArgumentException("wrong"));

        assertTrue(message.toString().startsWith("An invalid argument was passed to the command: wrong\n"));
        assertEquals("error-message", message.getClassName());
        assertFalse(message.isFinal());
    }

    @Test
    public void defaultMessageCssClass_isEmpty() {
        Message message = new Message() {
            @Override
            public String toString() {
                return "message";
            }

            @Override
            public boolean isFinal() {
                return false;
            }
        };

        assertEquals("", message.getClassName());
    }

    private void assertMatchesQuoteAndBody(Message message, String body) {
        assertFalse(message.isFinal());
        String normalizedMessage = message.toString().replace(System.lineSeparator(), "\n");
        assertTrue(normalizedMessage.endsWith("\n\n" + body));
    }
}
