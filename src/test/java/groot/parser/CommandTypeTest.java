package groot.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Tests command recognition performed by {@link CommandType#from(String)}.
 */
public class CommandTypeTest {

    /**
     * Verifies that every exact command keyword maps to its corresponding command type.
     */
    @Test
    public void from_exactKeyword_returnsMatchingCommandType() {
        assertEquals(CommandType.BYE, CommandType.from("bye"));
        assertEquals(CommandType.LIST, CommandType.from("list"));
        assertEquals(CommandType.SORT, CommandType.from("sort"));
        assertEquals(CommandType.FIND, CommandType.from("find"));
        assertEquals(CommandType.MARK, CommandType.from("mark"));
        assertEquals(CommandType.UNMARK, CommandType.from("unmark"));
        assertEquals(CommandType.DELETE, CommandType.from("delete"));
        assertEquals(CommandType.TODO, CommandType.from("todo"));
        assertEquals(CommandType.DEADLINE, CommandType.from("deadline"));
        assertEquals(CommandType.EVENT, CommandType.from("event"));
        assertEquals(CommandType.HELP, CommandType.from("help"));
    }

    /**
     * Verifies that common CLI help aliases map to the help command.
     */
    @Test
    public void from_helpAlias_returnsHelpCommandType() {
        assertEquals(CommandType.HELP, CommandType.from("--help"));
        assertEquals(CommandType.HELP, CommandType.from("-h"));
    }

    /**
     * Verifies that command words and aliases in arguments do not override the leading keyword.
     */
    @Test
    public void from_commandWordsInArguments_usesLeadingKeyword() {
        assertEquals(CommandType.TODO, CommandType.from("todo help"));
        assertEquals(CommandType.TODO, CommandType.from("todo --help"));
        assertEquals(CommandType.FIND, CommandType.from("find mark"));
        assertEquals(CommandType.TODO, CommandType.from("todo bye"));
    }

    /**
     * Verifies that commands designed to accept arguments are recognized with arguments present.
     */
    @Test
    public void from_commandThatAcceptsArguments_returnsMatchingCommandType() {
        assertEquals(CommandType.MARK, CommandType.from("mark 1"));
        assertEquals(CommandType.UNMARK, CommandType.from("unmark 1"));
        assertEquals(CommandType.DELETE, CommandType.from("delete 1"));
        assertEquals(CommandType.FIND, CommandType.from("find book"));
        assertEquals(CommandType.TODO, CommandType.from("todo read book"));
        assertEquals(CommandType.DEADLINE,
                CommandType.from("deadline return book /by 2019-12-02"));
        assertEquals(CommandType.EVENT,
                CommandType.from("event meeting /from 2pm /to 3pm"));
    }

    /**
     * Verifies that extra spaces between a command keyword and its arguments remain valid.
     */
    @Test
    public void from_multipleSpacesBeforeArguments_returnsMatchingCommandType() {
        assertEquals(CommandType.MARK, CommandType.from("mark   1"));
        assertEquals(CommandType.FIND, CommandType.from("find   book"));
        assertEquals(CommandType.TODO, CommandType.from("todo   read book"));
        assertEquals(CommandType.DEADLINE,
                CommandType.from("deadline   return book /by 2019-12-02"));
    }

    /**
     * Verifies that commands defined without arguments reject trailing argument text.
     */
    @Test
    public void from_argumentlessCommandWithArguments_returnsUnknown() {
        assertEquals(CommandType.UNKNOWN, CommandType.from("bye now"));
        assertEquals(CommandType.UNKNOWN, CommandType.from("list extra"));
        assertEquals(CommandType.UNKNOWN, CommandType.from("help extra"));
        assertEquals(CommandType.UNKNOWN, CommandType.from("--help extra"));
        assertEquals(CommandType.UNKNOWN, CommandType.from("-h extra"));
    }

    /**
     * Verifies that a keyword embedded at the start of a longer word is not accepted.
     */
    @Test
    public void from_keywordOnlyAsPrefix_returnsUnknown() {
        assertEquals(CommandType.UNKNOWN, CommandType.from("todoist"));
        assertEquals(CommandType.UNKNOWN, CommandType.from("marked 1"));
        assertEquals(CommandType.UNKNOWN, CommandType.from("events meeting"));
        assertEquals(CommandType.UNKNOWN, CommandType.from("--helpful"));
        assertEquals(CommandType.UNKNOWN, CommandType.from("-hello"));
    }

    /**
     * Verifies that keywords must start the command and use a space before any arguments.
     */
    @Test
    public void from_keywordNotAtStartOrWithoutSpaceSeparator_returnsUnknown() {
        assertEquals(CommandType.UNKNOWN, CommandType.from("please list"));
        assertEquals(CommandType.UNKNOWN, CommandType.from("todo\tread book"));
        assertEquals(CommandType.UNKNOWN, CommandType.from("mark\t1"));
    }

    @Test
    public void from_sortCaseAndWhitespaceVariations_returnsSort() {
        for (String command : List.of("sort", "SORT", " Sort ", "sort -r", "sort\t--REVERSE",
                "\tSoRt \t -R\t", "sort invalid", "sort -r -r")) {
            assertEquals(CommandType.SORT, CommandType.from(command), command);
        }
    }

    @Test
    public void from_sortPrefixesAndOtherRelaxedCommands_preservesUnknown() {
        for (String command : List.of("sorting", "sort-r", "sort--reverse", "please sort", "LIST",
                "TODO book", "todo\tbook", "mark\t1", "HELP")) {
            assertEquals(CommandType.UNKNOWN, CommandType.from(command), command);
        }
        assertEquals(CommandType.TODO, CommandType.from("todo sort -r"));
        assertEquals(CommandType.FIND, CommandType.from("find SORT"));
    }

    /**
     * Verifies that empty, unrecognized, and incorrectly cased commands map to unknown.
     */
    @Test
    public void from_emptyUnknownOrWrongCaseCommand_returnsUnknown() {
        assertEquals(CommandType.UNKNOWN, CommandType.from(""));
        assertEquals(CommandType.UNKNOWN, CommandType.from("hello"));
        assertEquals(CommandType.UNKNOWN, CommandType.from("LIST"));
    }
}
