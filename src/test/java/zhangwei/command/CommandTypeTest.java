package zhangwei.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import zhangwei.ZhangWeiException;

/**
 * Tests {@link CommandType}, which decides whether a typed word names a
 * command at all. Because the "I don't know that command" message is built
 * from the same values, a test also checks that the message stays in step with
 * the commands that exist.
 */
public class CommandTypeTest {

    @Test
    public void fromKeyword_everyKnownKeyword_matchingTypeReturned() throws ZhangWeiException {
        // Every declared command must be reachable from the word the user types.
        for (CommandType type : CommandType.values()) {
            assertEquals(type, CommandType.fromKeyword(type.getKeyword()));
        }
    }

    @Test
    public void fromKeyword_todo_todoTypeReturned() throws ZhangWeiException {
        assertEquals(CommandType.TODO, CommandType.fromKeyword("todo"));
    }

    @Test
    public void fromKeyword_bye_byeTypeReturned() throws ZhangWeiException {
        assertEquals(CommandType.BYE, CommandType.fromKeyword("bye"));
    }

    @Test
    public void fromKeyword_unknownKeyword_exceptionThrown() {
        ZhangWeiException e = assertThrows(ZhangWeiException.class,
                () -> CommandType.fromKeyword("blah"));
        assertTrue(e.getMessage().contains("blah"));
    }

    @Test
    public void fromKeyword_wrongCase_exceptionThrown() {
        // Keyword matching is case sensitive, so "Todo" is not the todo command.
        assertThrows(ZhangWeiException.class, () -> CommandType.fromKeyword("Todo"));
    }

    @Test
    public void fromKeyword_emptyKeyword_exceptionThrown() {
        assertThrows(ZhangWeiException.class, () -> CommandType.fromKeyword(""));
    }

    @Test
    public void fromKeyword_keywordWithTrailingSpace_exceptionThrown() {
        assertThrows(ZhangWeiException.class, () -> CommandType.fromKeyword("todo "));
    }

    @Test
    public void fromKeyword_unknownKeyword_messageListsEveryKeyword() {
        ZhangWeiException e = assertThrows(ZhangWeiException.class,
                () -> CommandType.fromKeyword("blah"));
        for (CommandType type : CommandType.values()) {
            assertTrue(e.getMessage().contains(type.getKeyword()),
                    "message should mention " + type.getKeyword());
        }
    }

    @Test
    public void getKeyword_eachType_lowercaseWordReturned() {
        assertEquals("deadline", CommandType.DEADLINE.getKeyword());
        assertEquals("unmark", CommandType.UNMARK.getKeyword());
    }
}
