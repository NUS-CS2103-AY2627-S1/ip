package botzilla.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import botzilla.BotzillaException;
import botzilla.task.Task;

public class ParserTest {

    // ---- BYE ----
    @Test
    void parseCommandType_bye_returnsBye() {
        assertEquals(Parser.CommandType.BYE, Parser.parseCommandType("bye"));
    }

    // ---- LIST ----
    @Test
    void parseCommandType_list_returnsList() {
        assertEquals(Parser.CommandType.LIST, Parser.parseCommandType("list"));
    }

    // ---- MARK ----
    @Test
    void parseCommandType_markWithArgument_returnsMark() {
        assertEquals(Parser.CommandType.MARK, Parser.parseCommandType("mark 1"));
    }

    @Test
    void parseCommandType_markAlone_returnsMark() {
        // "mark" alone is still recognized as MARK, so the missing task
        // number can be reported as a specific "mark" error rather than a
        // generic "unknown command" one.
        assertEquals(Parser.CommandType.MARK, Parser.parseCommandType("mark"));
    }

    // ---- UNMARK ----
    @Test
    void parseCommandType_unmarkWithArgument_returnsUnmark() {
        assertEquals(Parser.CommandType.UNMARK, Parser.parseCommandType("unmark 2"));
    }

    @Test
    void parseCommandType_unmarkAlone_returnsUnmark() {
        assertEquals(Parser.CommandType.UNMARK, Parser.parseCommandType("unmark"));
    }

    // ---- DELETE ----
    @Test
    void parseCommandType_deleteAlone_returnsDelete() {
        assertEquals(Parser.CommandType.DELETE, Parser.parseCommandType("delete"));
    }

    @Test
    void parseCommandType_deleteWithArgument_returnsDelete() {
        assertEquals(Parser.CommandType.DELETE, Parser.parseCommandType("delete 3"));
    }

    // ---- ON ----
    @Test
    void parseCommandType_onAlone_returnsOn() {
        assertEquals(Parser.CommandType.ON, Parser.parseCommandType("on"));
    }

    @Test
    void parseCommandType_onWithArgument_returnsOn() {
        assertEquals(Parser.CommandType.ON, Parser.parseCommandType("on 2/12/2019"));
    }

    // ---- TODO ----
    @Test
    void parseCommandType_todoAlone_returnsTodo() {
        assertEquals(Parser.CommandType.TODO, Parser.parseCommandType("todo"));
    }

    @Test
    void parseCommandType_todoWithArgument_returnsTodo() {
        assertEquals(Parser.CommandType.TODO, Parser.parseCommandType("todo read book"));
    }

    // ---- DEADLINE ----
    @Test
    void parseCommandType_deadlineAlone_returnsDeadline() {
        assertEquals(Parser.CommandType.DEADLINE, Parser.parseCommandType("deadline"));
    }

    @Test
    void parseCommandType_deadlineWithArgument_returnsDeadline() {
        assertEquals(Parser.CommandType.DEADLINE,
                Parser.parseCommandType("deadline return book /by 2/12/2019 1800"));
    }

    // ---- EVENT ----
    @Test
    void parseCommandType_eventAlone_returnsEvent() {
        assertEquals(Parser.CommandType.EVENT, Parser.parseCommandType("event"));
    }

    @Test
    void parseCommandType_eventWithArgument_returnsEvent() {
        assertEquals(Parser.CommandType.EVENT,
                Parser.parseCommandType("event meeting /from 2/12/2019 1400 /to 2/12/2019 1600"));
    }

    // ---- UNKNOWN / edge cases ----
    @Test
    void parseCommandType_emptyString_returnsUnknown() {
        assertEquals(Parser.CommandType.UNKNOWN, Parser.parseCommandType(""));
    }

    @Test
    void parseCommandType_unrecognizedWord_returnsUnknown() {
        assertEquals(Parser.CommandType.UNKNOWN, Parser.parseCommandType("blah"));
    }

    @Test
    void parseCommandType_differentCasing_returnsUnknown() {
        // Matching is case-sensitive, so "Bye" should not match "bye"
        assertEquals(Parser.CommandType.UNKNOWN, Parser.parseCommandType("Bye"));
    }

    @Test
    void parseCommandType_leadingWhitespace_stillRecognized() {
        // Leading/trailing whitespace around the whole command (e.g. from a
        // copy-paste) is trimmed before matching.
        assertEquals(Parser.CommandType.BYE, Parser.parseCommandType(" bye"));
    }

    @Test
    void parseCommandType_trailingWhitespace_stillRecognized() {
        assertEquals(Parser.CommandType.BYE, Parser.parseCommandType("bye  "));
    }

    @Test
    void parseCommandType_substringOfKeyword_returnsUnknown() {
        // "lis" is not "list" and does not start with any recognized prefix
        assertEquals(Parser.CommandType.UNKNOWN, Parser.parseCommandType("lis"));
    }

    @Test
    void parseCommandType_keywordAsSubstringOfLongerWord_returnsUnknown() {
        // "listing" is neither equal to "list" nor followed by a space,
        // and does not match any startsWith("keyword ") prefix
        assertEquals(Parser.CommandType.UNKNOWN, Parser.parseCommandType("listing"));
    }

    // ---- TAG ----
    @Test
    void parseCommandType_tagWithArgument_returnsTag() {
        assertEquals(Parser.CommandType.TAG, Parser.parseCommandType("tag 1 fun"));
    }

    @Test
    void parseCommandType_tagAlone_returnsTag() {
        assertEquals(Parser.CommandType.TAG, Parser.parseCommandType("tag"));
    }

    // ---- UNTAG ----
    @Test
    void parseCommandType_untagWithArgument_returnsUntag() {
        assertEquals(Parser.CommandType.UNTAG, Parser.parseCommandType("untag 1 fun"));
    }

    @Test
    void parseCommandType_untagAlone_returnsUntag() {
        assertEquals(Parser.CommandType.UNTAG, Parser.parseCommandType("untag"));
    }

    // ---- parseTagCommand() ----
    @Test
    void parseTagCommand_singleTag_returnsZeroBasedIndexAndTagName() throws BotzillaException {
        Parser.TagCommand command = Parser.parseTagCommand("tag 2 fun", Parser.tagKeywordLength(), 3);

        assertEquals(1, command.index());
        assertEquals(List.of("fun"), command.tagNames());
    }

    @Test
    void parseTagCommand_multipleTags_returnsAllTagNamesInOrder() throws BotzillaException {
        Parser.TagCommand command = Parser.parseTagCommand("tag 1 fun urgent", Parser.tagKeywordLength(), 3);

        assertEquals(List.of("fun", "urgent"), command.tagNames());
    }

    @Test
    void parseTagCommand_tagWithHashPrefix_keepsRawTokenForTaskToNormalize() throws BotzillaException {
        Parser.TagCommand command = Parser.parseTagCommand("tag 1 #fun", Parser.tagKeywordLength(), 3);

        assertEquals(List.of("#fun"), command.tagNames());
    }

    @Test
    void parseTagCommand_missingTagName_throwsException() {
        int keywordLength = Parser.tagKeywordLength();
        assertThrows(BotzillaException.class, () -> Parser.parseTagCommand("tag 1", keywordLength, 3));
    }

    @Test
    void parseTagCommand_missingIndexAndTagName_throwsException() {
        int keywordLength = Parser.tagKeywordLength();
        assertThrows(BotzillaException.class, () -> Parser.parseTagCommand("tag", keywordLength, 3));
    }

    @Test
    void parseTagCommand_nonNumericIndex_throwsException() {
        int keywordLength = Parser.tagKeywordLength();
        assertThrows(BotzillaException.class, () -> Parser.parseTagCommand("tag abc fun", keywordLength, 3));
    }

    @Test
    void parseTagCommand_indexOutOfRange_throwsException() {
        int keywordLength = Parser.tagKeywordLength();
        assertThrows(BotzillaException.class, () -> Parser.parseTagCommand("tag 99 fun", keywordLength, 3));
    }

    // ---- inline "#tag" extraction: todo ----
    @Test
    void parseTodo_noInlineTag_hasEmptyTags() throws BotzillaException {
        Task task = Parser.parseTodo("todo read book");

        assertEquals("read book", task.getName());
        assertTrue(task.getTags().isEmpty());
    }

    @Test
    void parseTodo_withInlineTag_stripsTagFromNameAndAddsItToTask() throws BotzillaException {
        Task task = Parser.parseTodo("todo read book #fun");

        assertEquals("read book", task.getName());
        assertEquals(Set.of("fun"), task.getTags());
    }

    @Test
    void parseTodo_withMultipleInlineTags_addsAllOfThem() throws BotzillaException {
        Task task = Parser.parseTodo("todo read book #fun #easy");

        assertEquals("read book", task.getName());
        assertEquals(Set.of("fun", "easy"), task.getTags());
    }

    // ---- inline "#tag" extraction: deadline ----
    @Test
    void parseDeadline_withInlineTagBeforeByClause_stripsTagAndKeepsDate() throws BotzillaException {
        Task task = Parser.parseDeadline("deadline submit report #urgent /by 2/12/2019 1800");

        assertEquals("submit report", task.getName());
        assertEquals(Set.of("urgent"), task.getTags());
        assertTrue(task.getDate().isPresent());
    }

    @Test
    void parseDeadline_withInlineTagAfterByClause_stripsTagAndKeepsDate() throws BotzillaException {
        Task task = Parser.parseDeadline("deadline submit report /by 2/12/2019 1800 #urgent");

        assertEquals("submit report", task.getName());
        assertEquals(Set.of("urgent"), task.getTags());
        assertTrue(task.getDate().isPresent());
    }

    // ---- inline "#tag" extraction: event ----
    @Test
    void parseEvent_withInlineTags_stripsTagsAndKeepsBothDates() throws BotzillaException {
        Task task = Parser.parseEvent("event party /from 1/1/2026 1800 /to 1/1/2026 2200 #fun #social");

        assertEquals("party", task.getName());
        assertEquals(Set.of("fun", "social"), task.getTags());
        assertTrue(task.getDate().isPresent());
    }

    // ---- command format errors: extra whitespace around "/by"/"/from"/"/to" ----
    @Test
    void parseDeadline_extraSpacesAroundByClause_stillParses() throws BotzillaException {
        Task task = Parser.parseDeadline("deadline return book   /by   2/12/2019 1800");

        assertEquals("return book", task.getName());
        assertTrue(task.getDate().isPresent());
    }

    @Test
    void parseEvent_extraSpacesAroundFromToClauses_stillParses() throws BotzillaException {
        Task task = Parser.parseEvent("event party   /from   1/1/2026 1800   /to   1/1/2026 2200");

        assertEquals("party", task.getName());
        assertTrue(task.getDate().isPresent());
    }

    // ---- command format errors: a parameter given more than once ----
    @Test
    void parseDeadline_byGivenTwice_throwsException() {
        assertThrows(BotzillaException.class, () ->
                Parser.parseDeadline("deadline return book /by 2/12/2019 /by 3/12/2019"));
    }

    @Test
    void parseEvent_fromGivenTwice_throwsException() {
        assertThrows(BotzillaException.class, () ->
                Parser.parseEvent("event party /from 1/1/2026 1800 /from 2/1/2026 1800 /to 1/1/2026 2200"));
    }

    @Test
    void parseEvent_toGivenTwice_throwsException() {
        assertThrows(BotzillaException.class, () ->
                Parser.parseEvent("event party /from 1/1/2026 1800 /to 1/1/2026 2200 /to 1/1/2026 2300"));
    }

    // ---- command format errors: special characters that would break the save file ----
    @Test
    void parseTodo_nameContainsPipeCharacter_throwsException() {
        assertThrows(BotzillaException.class, () -> Parser.parseTodo("todo read | book"));
    }

    @Test
    void parseTagCommand_tagContainsPipeCharacter_throwsException() {
        int keywordLength = Parser.tagKeywordLength();
        assertThrows(BotzillaException.class, () -> Parser.parseTagCommand("tag 1 fun|urgent", keywordLength, 3));
    }

    @Test
    void parseTagCommand_tagContainsComma_throwsException() {
        int keywordLength = Parser.tagKeywordLength();
        assertThrows(BotzillaException.class, () -> Parser.parseTagCommand("tag 1 fun,urgent", keywordLength, 3));
    }

    @Test
    void parseTagCommand_bareHashWithNoName_throwsException() {
        int keywordLength = Parser.tagKeywordLength();
        assertThrows(BotzillaException.class, () -> Parser.parseTagCommand("tag 1 #", keywordLength, 3));
    }

    // ---- data not as expected: non-existent calendar dates ----
    @Test
    void parseDeadline_nonExistentDate_throwsException() {
        // February never has 30 days, in a leap year or otherwise.
        assertThrows(BotzillaException.class, () ->
                Parser.parseDeadline("deadline return book /by 30/2/2019 1800"));
    }

    @Test
    void parseDeadline_monthOutOfRange_throwsException() {
        assertThrows(BotzillaException.class, () ->
                Parser.parseDeadline("deadline return book /by 1/13/2019"));
    }

    @Test
    void parseDeadline_timeOutOfRange_throwsException() {
        assertThrows(BotzillaException.class, () ->
                Parser.parseDeadline("deadline return book /by 2/12/2019 2500"));
    }

    @Test
    void parseDeadline_genuineFreeTextDate_isKeptAsRawTextWithoutError() throws BotzillaException {
        // Free text that isn't shaped like a date attempt at all (e.g. no
        // slashes) is intentionally accepted as-is, not treated as an error.
        Task task = Parser.parseDeadline("deadline return book /by whenever I feel like it");

        assertTrue(task.getDate().isEmpty());
    }

    // ---- data not as expected: event start not before end ----
    @Test
    void parseEvent_startAfterEnd_throwsException() {
        assertThrows(BotzillaException.class, () ->
                Parser.parseEvent("event party /from 2/1/2026 1800 /to 1/1/2026 1800"));
    }

    @Test
    void parseEvent_startEqualsEnd_throwsException() {
        assertThrows(BotzillaException.class, () ->
                Parser.parseEvent("event party /from 1/1/2026 1800 /to 1/1/2026 1800"));
    }
}
