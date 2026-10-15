package littler.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.scene.text.Text;
import littler.command.Command;

/**
 * Converts raw chat text into a list of styled {@link Text} runs that can be placed inside a
 * {@link javafx.scene.text.TextFlow}, so that recognizable pieces (commands, delimiters, task
 * markers, tags, priorities) are visually distinct from plain text.
 *
 * <p>This class only decides which style class each substring gets; the actual colours and fonts
 * live in {@code styles.css}, keeping the two concerns (what to highlight vs. how it looks) apart.
 */
final class MessageFormatter {

    /** Matches a "/word" delimiter token anywhere in user input, e.g. "/by", "/from", "/name". */
    private static final Pattern DELIMITER_PATTERN = Pattern.compile("/\\w+");

    /**
     * Matches the pieces of a bot reply worth highlighting: a leading "N." list index, any
     * bracketed tag (task type/status icons, aliases, priority levels), a "#tag", or a whole-line
     * header ending in ":".
     */
    private static final Pattern REPLY_TOKEN_PATTERN = Pattern.compile(
        "(?<index>^\\d+\\.)"
        + "|(?<bracket>\\[[^\\]\\r\\n]*])"
        + "|(?<tag>#\\S+)"
        + "|(?<header>^[A-Za-z][^\\n:]{0,40}:$)",
        Pattern.MULTILINE);

    private MessageFormatter() {
        // Utility class; not meant to be instantiated.
    }

    /**
     * Formats a raw line of user input, bolding the leading command word if it is recognized,
     * and highlighting any "/delimiter" tokens.
     *
     * @param input the raw text the user typed
     * @return the input split into styled Text runs, in order
     */
    static List<Text> formatUserInput(String input) {
        List<Text> segments = new ArrayList<>();

        int firstSpace = input.indexOf(' ');
        int commandEnd = firstSpace == -1 ? input.length() : firstSpace;
        String commandToken = input.substring(0, commandEnd);
        String rest = input.substring(commandEnd);

        boolean isRecognized = Command.fromInput(input) != null;
        segments.add(styled(commandToken, "bubble-text-inverse", isRecognized ? "command-token" : null));

        int last = 0;
        Matcher matcher = DELIMITER_PATTERN.matcher(rest);
        while (matcher.find()) {
            if (matcher.start() > last) {
                segments.add(plain(rest.substring(last, matcher.start()), "bubble-text-inverse"));
            }
            segments.add(styled(matcher.group(), "bubble-text-inverse", "delimiter-token"));
            last = matcher.end();
        }
        if (last < rest.length()) {
            segments.add(plain(rest.substring(last), "bubble-text-inverse"));
        }
        return segments;
    }

    /**
     * Formats a LittleR reply, highlighting list indices, bracketed tags (task type/status,
     * command aliases, priority levels), hashtags, and whole-line headers.
     *
     * @param reply the reply text produced by {@code LittleR.converse}
     * @return the reply split into styled Text runs, in order
     */
    static List<Text> formatBotReply(String reply) {
        List<Text> segments = new ArrayList<>();
        int last = 0;
        Matcher matcher = REPLY_TOKEN_PATTERN.matcher(reply);
        while (matcher.find()) {
            if (matcher.start() > last) {
                segments.add(plain(reply.substring(last, matcher.start()), "bubble-text"));
            }
            segments.add(styled(matcher.group(), "bubble-text", replyTokenStyleClass(matcher)));
            last = matcher.end();
        }
        if (last < reply.length()) {
            segments.add(plain(reply.substring(last), "bubble-text"));
        }
        return segments;
    }

    /**
     * Determines which style class a matched reply token should get, based on which named group
     * of {@link #REPLY_TOKEN_PATTERN} matched and, for bracketed tags, their content.
     *
     * @param matcher a matcher currently positioned on a successful match of REPLY_TOKEN_PATTERN
     * @return the style class to apply to the matched token
     */
    private static String replyTokenStyleClass(Matcher matcher) {
        if (matcher.group("index") != null) {
            return "list-index";
        }
        if (matcher.group("tag") != null) {
            return "hash-tag";
        }
        if (matcher.group("header") != null) {
            return "section-header";
        }
        String bracket = matcher.group("bracket");
        String inner = bracket.substring(1, bracket.length() - 1);
        if (inner.equals("X")) {
            return "status-done";
        }
        if (inner.isBlank()) {
            return "status-pending";
        }
        if (inner.equalsIgnoreCase("high") || inner.equalsIgnoreCase("medium") || inner.equalsIgnoreCase("low")) {
            return "priority-" + inner.toLowerCase();
        }
        return "bracket-tag";
    }

    /**
     * Creates a Text run with only the given base fill style class (no extra highlight).
     *
     * @param content the text content
     * @param baseStyleClass the base style class controlling the default text colour
     * @return the plain Text run
     */
    private static Text plain(String content, String baseStyleClass) {
        Text text = new Text(content);
        text.getStyleClass().add(baseStyleClass);
        return text;
    }

    /**
     * Creates a Text run with the given base fill style class, plus an optional highlight class
     * layered on top of it.
     *
     * @param content the text content
     * @param baseStyleClass the base style class controlling the default text colour
     * @param highlightStyleClass an additional style class to highlight this run, or null for none
     * @return the styled Text run
     */
    private static Text styled(String content, String baseStyleClass, String highlightStyleClass) {
        if (highlightStyleClass == null) {
            return plain(content, baseStyleClass);
        }
        Text text = new Text(content);
        text.getStyleClass().addAll(baseStyleClass, highlightStyleClass);
        return text;
    }
}
