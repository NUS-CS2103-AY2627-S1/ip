package ff15;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Tests which command word {@link CommandWord#match(String)} picks out of a line
 * of input.
 *
 * <p>The rule being guarded is the trailing space: a command that takes arguments
 * matches "{@code word }" but not "{@code word}" glued to whatever follows, so
 * that "{@code marker}" is not read as a "{@code mark}" of task "er".
 */
public class CommandWordTest {

    @Test
    public void match_wordWithNoArguments_returnsThatCommand() {
        assertEquals(CommandWord.LIST, CommandWord.match("list"));
        assertEquals(CommandWord.BYE, CommandWord.match("bye"));
    }

    @Test
    public void match_wordWithArguments_returnsThatCommand() {
        assertEquals(CommandWord.MARK, CommandWord.match("mark 2"));
        assertEquals(CommandWord.UNMARK, CommandWord.match("unmark 2"));
        assertEquals(CommandWord.DELETE, CommandWord.match("delete 2"));
        assertEquals(CommandWord.TODO, CommandWord.match("todo read book"));
        assertEquals(CommandWord.DEADLINE, CommandWord.match("deadline return book /by 2019-12-02"));
        assertEquals(CommandWord.EVENT, CommandWord.match("event meeting /from 2019-12-05 /to 2019-12-06"));
        assertEquals(CommandWord.ON, CommandWord.match("on 2019-12-02"));
        assertEquals(CommandWord.FIND, CommandWord.match("find book"));
    }

    @Test
    public void match_argumentTakingWordAlone_stillReturnsThatCommand() {
        // The missing argument is the parser's problem to report, not a reason to
        // treat the line as an unknown command.
        assertEquals(CommandWord.MARK, CommandWord.match("mark"));
        assertEquals(CommandWord.TODO, CommandWord.match("todo"));
        assertEquals(CommandWord.FIND, CommandWord.match("find"));
    }

    @Test
    public void match_argumentGluedToTheWord_returnsUnknown() {
        assertEquals(CommandWord.UNKNOWN, CommandWord.match("mark2"));
        assertEquals(CommandWord.UNKNOWN, CommandWord.match("todoread book"));
    }

    @Test
    public void match_longerWordStartingWithACommand_returnsUnknown() {
        assertEquals(CommandWord.UNKNOWN, CommandWord.match("marker"));
        assertEquals(CommandWord.UNKNOWN, CommandWord.match("finder"));
        assertEquals(CommandWord.UNKNOWN, CommandWord.match("listing"));
        assertEquals(CommandWord.UNKNOWN, CommandWord.match("byebye"));
    }

    @Test
    public void match_argumentAfterAWordThatTakesNone_returnsUnknown() {
        assertEquals(CommandWord.UNKNOWN, CommandWord.match("list all"));
        assertEquals(CommandWord.UNKNOWN, CommandWord.match("bye now"));
    }

    @Test
    public void match_wrongCase_returnsUnknown() {
        assertEquals(CommandWord.UNKNOWN, CommandWord.match("List"));
        assertEquals(CommandWord.UNKNOWN, CommandWord.match("BYE"));
        assertEquals(CommandWord.UNKNOWN, CommandWord.match("Todo read book"));
    }

    @Test
    public void match_emptyInput_returnsUnknown() {
        assertEquals(CommandWord.UNKNOWN, CommandWord.match(""));
    }

    @Test
    public void match_unrelatedText_returnsUnknown() {
        assertEquals(CommandWord.UNKNOWN, CommandWord.match("blah"));
        assertEquals(CommandWord.UNKNOWN, CommandWord.match("what can you do"));
    }

    @Test
    public void getWord_eachCommand_returnsTheWordTheUserTypes() {
        assertEquals("list", CommandWord.LIST.getWord());
        assertEquals("deadline", CommandWord.DEADLINE.getWord());
        assertEquals("contact", CommandWord.CONTACT.getWord());
        assertEquals("", CommandWord.UNKNOWN.getWord());
    }

    @Test
    public void match_contactWithSubCommand_returnsContact() {
        assertEquals(CommandWord.CONTACT, CommandWord.match("contact add John"));
        assertEquals(CommandWord.CONTACT, CommandWord.match("contact list"));
    }

    @Test
    public void match_wordThatMerelyStartsWithContact_returnsUnknown() {
        assertEquals(CommandWord.UNKNOWN, CommandWord.match("contacts"));
    }
}
