package miku.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

/** Tests the text segments used in command and reply bubbles. */
class DialogBoxTest {
    @Test
    void splitCommand_leadingSpaceAndArguments_preservesSubmittedText() {
        String input = "  deadline submit report /by 2026-09-20  ";

        DialogTextSplitter.CommandParts parts = DialogTextSplitter.splitCommand(input);

        assertEquals("  ", parts.leadingWhitespace());
        assertEquals("deadline", parts.command());
        assertEquals(" submit report /by 2026-09-20  ", parts.arguments());
        assertEquals(input, parts.leadingWhitespace() + parts.command() + parts.arguments());
    }

    @Test
    void splitCommand_unknownCommand_preservesFirstWord() {
        DialogTextSplitter.CommandParts parts = DialogTextSplitter.splitCommand("sparkle later");

        assertEquals("sparkle", parts.command());
        assertEquals(" later", parts.arguments());
    }

    @Test
    void splitCommand_commandWithoutArguments_hasEmptyRemainder() {
        DialogTextSplitter.CommandParts parts = DialogTextSplitter.splitCommand("list");

        assertEquals("list", parts.command());
        assertEquals("", parts.arguments());
    }

    @Test
    void splitArguments_deadlineMarker_preservesTextAndMarksBy() {
        String arguments = " submit report /by 2026-09-20";

        List<DialogTextSplitter.ArgumentPart> parts = DialogTextSplitter.splitArguments(
                "deadline", arguments, true);

        assertEquals(arguments, parts.stream().map(DialogTextSplitter.ArgumentPart::text)
                .reduce("", String::concat));
        assertEquals(List.of("/by"), parts.stream().filter(DialogTextSplitter.ArgumentPart::isMarker)
                .map(DialogTextSplitter.ArgumentPart::text).toList());
    }

    @Test
    void splitArguments_eventMarkers_marksFromAndToOnly() {
        List<DialogTextSplitter.ArgumentPart> parts = DialogTextSplitter.splitArguments("event",
                " rehearsal /from 2026-09-20 /to 2026-09-21 /by note", true);

        assertEquals(List.of("/from", "/to"), parts.stream().filter(DialogTextSplitter.ArgumentPart::isMarker)
                .map(DialogTextSplitter.ArgumentPart::text).toList());
    }

    @Test
    void splitArguments_rescheduleMarkers_marksBothSupportedForms() {
        List<DialogTextSplitter.ArgumentPart> deadline = DialogTextSplitter.splitArguments("reschedule",
                " 2 /by 2026-09-20", true);
        List<DialogTextSplitter.ArgumentPart> event = DialogTextSplitter.splitArguments("reschedule",
                " 2 /from 2026-09-20 /to 2026-09-21", true);

        assertEquals(List.of("/by"), deadline.stream().filter(DialogTextSplitter.ArgumentPart::isMarker)
                .map(DialogTextSplitter.ArgumentPart::text).toList());
        assertEquals(List.of("/from", "/to"), event.stream().filter(DialogTextSplitter.ArgumentPart::isMarker)
                .map(DialogTextSplitter.ArgumentPart::text).toList());
    }

    @Test
    void splitArguments_literalMarkerOrInvalidCommand_doesNotMarkSyntax() {
        List<DialogTextSplitter.ArgumentPart> literal = DialogTextSplitter.splitArguments(
                "todo", " write /by hand", true);
        List<DialogTextSplitter.ArgumentPart> invalid = DialogTextSplitter.splitArguments(
                "deadline", " report /by 2026-09-20", false);

        assertTrue(literal.stream().noneMatch(DialogTextSplitter.ArgumentPart::isMarker));
        assertTrue(invalid.stream().noneMatch(DialogTextSplitter.ArgumentPart::isMarker));
    }

    @Test
    void splitReply_loadingWarning_highlightsOnlyPrefixAndPreservesText() {
        String warning = new ResponseFormatter().formatLoadingError();

        DialogTextSplitter.ReplyParts parts = DialogTextSplitter.splitReply(warning, true);

        assertEquals("OOPS!!!", parts.highlightedPrefix());
        assertEquals(warning, parts.highlightedPrefix() + parts.remainingText());
    }

    @Test
    void splitReply_ordinaryOopsMessage_hasNoHighlightedPrefix() {
        String message = "Oops... I've marked this task as not done yet ♪";

        DialogTextSplitter.ReplyParts parts = DialogTextSplitter.splitReply(message, false);

        assertEquals("", parts.highlightedPrefix());
        assertEquals(message, parts.remainingText());
    }

    @Test
    void splitReply_errorWithoutKnownPrefix_keepsWholeMessageReadable() {
        String message = "Something went wrong";

        DialogTextSplitter.ReplyParts parts = DialogTextSplitter.splitReply(message, true);

        assertEquals("", parts.highlightedPrefix());
        assertEquals(message, parts.remainingText());
    }
}
