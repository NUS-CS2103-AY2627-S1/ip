package wobble.parser;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/** Tests command suggestions for misspelled and incorrectly formatted input. */
public class CommandSuggesterTest {
    @Test
    public void suggest_typoInTodoCommand_returnsCorrectedCommand() {
        assertEquals("\"todo buy milk\"", CommandSuggester.suggest("todoo buy milk"));
    }

    @Test
    public void suggest_malformedDeadline_returnsDeadlineFormat() {
        assertEquals("\"deadline <description> /by <date/time>\"",
                CommandSuggester.suggest("deadline submit report"));
    }

    @Test
    public void suggest_typoInDueOnCommand_returnsCorrectedCommand() {
        assertEquals("\"due on 2026-09-17\"", CommandSuggester.suggest("dueon 2026-09-17"));
    }

    @Test
    public void suggest_typoInRemoveCommand_returnsCorrectedCommand() {
        assertEquals("\"remove 2\"", CommandSuggester.suggest("remvoe 2"));
    }

    @Test
    public void suggest_extraTimeDigit_returnsCorrectedDeadlineCommand() {
        assertEquals("\"deadline project /by 2026-09-16 1800\"",
                CommandSuggester.suggest("deadline project /by 2026-09-16 18000"));
    }

    @Test
    public void suggest_unknownCommand_returnsHelpFormat() {
        assertEquals("\"help\"", CommandSuggester.suggest("blah"));
    }

    @Test
    public void suggest_emptyCommand_returnsHelpFormat() {
        assertEquals("\"help\"", CommandSuggester.suggest("   "));
    }

    @Test
    public void suggest_supportedCommands_returnsTheirFormats() {
        assertAll(
                () -> assertEquals("\"todo <description>\"", CommandSuggester.suggest("todo")),
                () -> assertEquals("\"deadline <description> /by <date/time>\"",
                        CommandSuggester.suggest("deadline")),
                () -> assertEquals("\"event <description> /from <start> /to <end>\"",
                        CommandSuggester.suggest("event")),
                () -> assertEquals("\"list\"", CommandSuggester.suggest("list")),
                () -> assertEquals("\"find <keyword>\"", CommandSuggester.suggest("find")),
                () -> assertEquals("\"mark <number>\"", CommandSuggester.suggest("mark")),
                () -> assertEquals("\"unmark <number>\"", CommandSuggester.suggest("unmark")),
                () -> assertEquals("\"delete <number>\"", CommandSuggester.suggest("delete")),
                () -> assertEquals("\"remove <number>\"", CommandSuggester.suggest("remove")),
                () -> assertEquals("\"due on <date>\"", CommandSuggester.suggest("due on")),
                () -> assertEquals("\"reminders [number of days]\"", CommandSuggester.suggest("reminders")),
                () -> assertEquals("\"help\"", CommandSuggester.suggest("help")),
                () -> assertEquals("\"bye\"", CommandSuggester.suggest("bye")));
    }

    @Test
    public void suggest_caseInsensitiveCommand_returnsCommandFormat() {
        assertEquals("\"todo <description>\"", CommandSuggester.suggest("TODO"));
    }

    @Test
    public void suggest_typoInListCommand_returnsCorrectedFormat() {
        assertEquals("\"list\"", CommandSuggester.suggest("lis"));
    }

    @Test
    public void suggest_misspelledDateTimeCommands_preservesCorrectedArguments() {
        assertAll(
                () -> assertEquals("\"deadline project /by 2026-09-16 1800\"",
                        CommandSuggester.suggest("deadlne project /by 2026-09-16 1800")),
                () -> assertEquals("\"event project /from 2026-09-16 1800 /to 2026-09-16 1900\"",
                        CommandSuggester.suggest("evnt project /from 2026-09-16 1800 /to 2026-09-16 1900")));
    }

    @Test
    public void suggest_dateTimeTypo_returnsCorrectedDateTimeCommand() {
        assertAll(
                () -> assertEquals("\"deadline project /by 2026-09-16 1800\"",
                        CommandSuggester.suggest("deadline project /by 2026-09-16 18000")),
                () -> assertEquals("\"event project /from 2026-09-16 1800 /to 2026-09-16 1900\"",
                        CommandSuggester.suggest(
                                "event project /from 2026-09-16 18000 /to 2026-09-16 19000")),
                () -> assertEquals("\"due on 2026-09-16\"",
                        CommandSuggester.suggest("due on 2026-09-160")));
    }

    @Test
    public void suggest_dateCommandWithoutCorrection_returnsDateFormat() {
        assertAll(
                () -> assertEquals("\"due on <date>\"",
                        CommandSuggester.suggest("due on 2026-09-16")),
                () -> assertEquals("\"deadline <description> /by <date/time>\"",
                        CommandSuggester.suggest("deadline project /by 2026-09-16")));
    }

    @Test
    public void suggest_unknownCommandWithArguments_returnsHelpFormat() {
        assertEquals("\"help\"", CommandSuggester.suggest("completely unknown"));
    }

    @Test
    public void suggest_nullCommand_returnsHelpFormat() {
        assertEquals("\"help\"", CommandSuggester.suggest(null));
    }
}
