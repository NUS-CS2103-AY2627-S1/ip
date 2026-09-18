package benjamin.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import benjamin.BenjaminException;
import benjamin.command.TagCommand;
import benjamin.command.UntagCommand;

/**
 * Tests how tag and untag commands are read.
 */
public class TagParsingTest {
    @Test
    public void parse_tag_returnsTagCommand() throws BenjaminException {
        assertInstanceOf(TagCommand.class, Parser.parse("tag 1 #fun"));
    }

    @Test
    public void parse_untag_returnsUntagCommand() throws BenjaminException {
        assertInstanceOf(UntagCommand.class, Parser.parse("untag 1 #fun"));
    }

    @Test
    public void parseTagArguments_hashPrefix_isStripped() throws BenjaminException {
        Parser.TagArguments arguments = Parser.parseTagArguments("tag 2 #fun", "tag");

        assertEquals(2, arguments.taskNumber());
        assertEquals("fun", arguments.tag());
    }

    @Test
    public void parseTagArguments_withoutHash_alsoAccepted() throws BenjaminException {
        assertEquals("fun", Parser.parseTagArguments("tag 2 fun", "tag").tag());
    }

    @Test
    public void parseTagArguments_mixedCase_foldedToLowerCase() throws BenjaminException {
        assertEquals("fun", Parser.parseTagArguments("tag 1 #FUN", "tag").tag());
    }

    @Test
    public void parseTagArguments_extraSpaces_ignored() throws BenjaminException {
        Parser.TagArguments arguments = Parser.parseTagArguments("tag   3    #fun  ", "tag");

        assertEquals(3, arguments.taskNumber());
        assertEquals("fun", arguments.tag());
    }

    @Test
    public void parseTagArguments_hyphenAndUnderscore_accepted() throws BenjaminException {
        assertEquals("read-later", Parser.parseTagArguments("tag 1 #read-later", "tag").tag());
        assertEquals("read_later", Parser.parseTagArguments("tag 1 #read_later", "tag").tag());
    }

    @Test
    public void parseTagArguments_nothingAfterKeyword_exceptionThrown() {
        BenjaminException exception = assertThrows(
                BenjaminException.class, () -> Parser.parseTagArguments("tag", "tag"));
        assertEquals("Please provide a task number and a tag after tag.", exception.getMessage());
    }

    @Test
    public void parseTagArguments_numberButNoTag_exceptionThrown() {
        BenjaminException exception = assertThrows(
                BenjaminException.class, () -> Parser.parseTagArguments("tag 1", "tag"));
        assertEquals("Please provide a tag after the task number.", exception.getMessage());
    }

    @Test
    public void parseTagArguments_taskNumberNotANumber_exceptionThrown() {
        BenjaminException exception = assertThrows(
                BenjaminException.class, () -> Parser.parseTagArguments("tag abc #fun", "tag"));
        assertEquals("The task number after tag must be a whole number.", exception.getMessage());
    }

    @Test
    public void parseTagArguments_bareHash_exceptionThrown() {
        BenjaminException exception = assertThrows(
                BenjaminException.class, () -> Parser.parseTagArguments("tag 1 #", "tag"));
        assertEquals("A tag needs a name after the #.", exception.getMessage());
    }

    @Test
    public void parseTagArguments_twoTagsAtOnce_exceptionThrown() {
        BenjaminException exception = assertThrows(
                BenjaminException.class, () -> Parser.parseTagArguments("tag 1 #fun #urgent", "tag"));
        assertEquals("Please add one tag at a time.", exception.getMessage());
    }

    @Test
    public void parseTagArguments_illegalCharacter_exceptionThrown() {
        BenjaminException exception = assertThrows(
                BenjaminException.class, () -> Parser.parseTagArguments("tag 1 #bad!", "tag"));
        assertEquals("A tag can only contain letters, digits, hyphens and underscores.",
                exception.getMessage());
    }

    @Test
    public void parseTagArguments_untagKeyword_wordsMessagesWithUntag() {
        BenjaminException exception = assertThrows(
                BenjaminException.class, () -> Parser.parseTagArguments("untag", "untag"));
        assertEquals("Please provide a task number and a tag after untag.", exception.getMessage());
    }
}
