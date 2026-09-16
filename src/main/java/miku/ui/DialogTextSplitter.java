package miku.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Splits dialog text into unchanged display segments without requiring JavaFX. */
final class DialogTextSplitter {
    private static final Pattern ARGUMENT_PARTS = Pattern.compile("\\s+|\\S+");

    private DialogTextSplitter() {
    }

    /** Separates a known error prefix while preserving every character of the reply. */
    static ReplyParts splitReply(String text, boolean isError) {
        if (isError && text.startsWith(ResponseFormatter.ERROR_PREFIX)) {
            return new ReplyParts(ResponseFormatter.ERROR_PREFIX,
                    text.substring(ResponseFormatter.ERROR_PREFIX.length()));
        }
        return new ReplyParts("", text);
    }

    /** Splits a command for display without changing the submitted text. */
    static CommandParts splitCommand(String input) {
        int commandStart = 0;
        while (commandStart < input.length() && Character.isWhitespace(input.charAt(commandStart))) {
            commandStart++;
        }
        int commandEnd = commandStart;
        while (commandEnd < input.length() && !Character.isWhitespace(input.charAt(commandEnd))) {
            commandEnd++;
        }
        return new CommandParts(input.substring(0, commandStart), input.substring(commandStart, commandEnd),
                input.substring(commandEnd));
    }

    /** Splits arguments into exact text runs and marks supported syntax delimiters. */
    static List<ArgumentPart> splitArguments(String command, String arguments, boolean shouldHighlightSyntax) {
        List<ArgumentPart> parts = new ArrayList<>();
        Matcher matcher = ARGUMENT_PARTS.matcher(arguments);
        while (matcher.find()) {
            String part = matcher.group();
            parts.add(new ArgumentPart(part, shouldHighlightSyntax && isMarkerForCommand(command, part)));
        }
        return List.copyOf(parts);
    }

    /** Returns whether a delimiter is meaningful for the submitted command word. */
    private static boolean isMarkerForCommand(String command, String marker) {
        return switch (command) {
            case "deadline" -> marker.equals("/by");
            case "event" -> marker.equals("/from") || marker.equals("/to");
            case "reschedule" -> marker.equals("/by") || marker.equals("/from") || marker.equals("/to");
            default -> false;
        };
    }

    /** Holds the unchanged pieces of a submitted command. */
    record CommandParts(String leadingWhitespace, String command, String arguments) {
    }

    /** Holds an unchanged argument run and whether it is a syntax delimiter. */
    record ArgumentPart(String text, boolean isMarker) {
    }

    /** Holds the styled prefix and remaining reply text. */
    record ReplyParts(String highlightedPrefix, String remainingText) {
    }
}
