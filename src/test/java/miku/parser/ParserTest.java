package miku.parser;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import miku.MikuException;
import miku.command.AddCommand;
import miku.command.FindCommand;
import miku.command.RescheduleCommand;

/** Tests parser validation for commands with high-value input rules. */
class ParserTest {

    @Test
    void isRecognizedCommandWord_knownWordsRecognizedEvenWithoutValidArguments() {
        String[] words = {"bye", "list", "find", "mark", "unmark", "delete",
            "reschedule", "todo", "deadline", "event"};
        for (String word : words) {
            assertTrue(Parser.isRecognizedCommandWord(word), word);
        }
        assertFalse(Parser.isRecognizedCommandWord("sparkle"));
        assertFalse(Parser.isRecognizedCommandWord("/by"));
        assertFalse(Parser.isRecognizedCommandWord(""));
        assertFalse(Parser.isRecognizedCommandWord("Mark"));
    }

    @Test
    void parse_findWithKeyword_returnsFindCommand() throws MikuException {
        Parser parser = new Parser();

        assertInstanceOf(FindCommand.class, parser.parse("find book"));
    }

    @Test
    void parse_findWithoutKeyword_exceptionThrown() {
        Parser parser = new Parser();

        assertThrows(MikuException.class, () -> parser.parse("find"));
    }

    @Test
    void parse_taskCommands_returnsAddCommands() throws MikuException {
        Parser parser = new Parser();

        assertInstanceOf(AddCommand.class, parser.parse("todo review notes"));
        assertInstanceOf(AddCommand.class, parser.parse("deadline submit work /by 2026-09-08"));
        assertInstanceOf(AddCommand.class, parser.parse("event concert /from 2026-09-08 /to 2026-09-09"));
    }

    @Test
    void parse_eventWithEqualEndpoints_returnsAddCommand() throws MikuException {
        Parser parser = new Parser();

        assertInstanceOf(AddCommand.class, parser.parse("event briefing /from 2026-09-08 0900 /to 2026-09-08 0900"));
    }

    @Test
    void parse_eventEndingBeforeStart_exceptionThrown() {
        Parser parser = new Parser();

        assertThrows(MikuException.class, () -> parser.parse(
                "event briefing /from 2026-09-08 0900 /to 2026-09-08 0859"));
    }

    @Test
    void parse_rescheduleWithDeadlineOrEventSchedule_returnsRescheduleCommand() throws MikuException {
        Parser parser = new Parser();

        assertInstanceOf(RescheduleCommand.class, parser.parse("reschedule 1 /by 2026-09-08 0900"));
        assertInstanceOf(RescheduleCommand.class,
                parser.parse("reschedule 2 /from 2026-09-08 1800 /to 2026-09-08 1800"));
    }

    @Test
    void parse_rescheduleWithInvalidSyntax_exceptionThrown() {
        Parser parser = new Parser();

        assertThrows(MikuException.class, () -> parser.parse("reschedule"));
        assertThrows(MikuException.class, () -> parser.parse("reschedule first /by 2026-09-08"));
        assertThrows(MikuException.class, () -> parser.parse("reschedule 1 /by"));
        assertThrows(MikuException.class, () -> parser.parse("reschedule 1 /from 2026-09-08"));
        assertThrows(MikuException.class, () -> parser.parse(
                "reschedule 1 /from 2026-09-09 /to 2026-09-08"));
    }
}
