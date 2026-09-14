package arthur.commands;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import arthur.messages.ListMessage;
import arthur.messages.Message;
import arthur.state.CommandContext;
import arthur.tasks.TodoTask;

public class FindCommandTest {
    @Test
    public void handle_matchingKeyword_returnsOnlyMatchingTasks() {
        CommandContext context = new CommandContext();
        context.tasks().add(new TodoTask("read book"));
        context.tasks().add(new TodoTask("write report"));
        context.tasks().add(new TodoTask("read notes"));

        Message message = new FindCommand().handle(context, "read");

        assertInstanceOf(ListMessage.class, message);
        String output = message.toString();
        assertTrue(output.contains("read book"));
        assertTrue(output.contains("read notes"));
        assertFalse(output.contains("write report"));
    }

    @Test
    public void handle_noMatchingKeyword_returnsEmptyListMessage() {
        CommandContext context = new CommandContext();
        context.tasks().add(new TodoTask("read book"));

        Message message = new FindCommand().handle(context, "exercise");

        assertInstanceOf(ListMessage.class, message);
        assertTrue(message.toString().contains("No tasks to display."));
    }

    @Test
    public void handle_keywordWithSurroundingWhitespace_matchesTrimmedKeyword() {
        CommandContext context = new CommandContext();
        context.tasks().add(new TodoTask("read book"));

        Message message = new FindCommand().handle(context, "  read  ");

        assertTrue(message.toString().contains("read book"));
    }
}
