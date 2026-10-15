package bob.parser;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import bob.BobException;

/**
 * Represents the text of a command split at its markers: the text before the first
 * marker, and the value that follows each marker used.
 *
 * <p>{@link TaskParser} and {@link EditParser} both read text of this shape, so what
 * counts as a marker is decided once, here, and the two cannot drift apart. A marker
 * counts only as a whole word: at the start of the text or after whitespace, and
 * followed by whitespace or the end. So {@code /tokyo}, {@code /byzantine} and
 * {@code a/b} are ordinary text, where looking for the marker's letters anywhere
 * would have read them as {@code /to}, {@code /by} and so on.
 *
 * <p>Two mistakes are refused while the text is split, because they are mistakes
 * whatever the command: a marker written twice, which leaves no way to tell which
 * value was meant, and a marker in capitals, which the user most likely meant as the
 * lowercase one. Which markers a command takes, and in what order, is for that
 * command's parser to check.
 *
 * <p>This is a simpler cousin of AddressBook-Level3's {@code ArgumentTokenizer},
 * which splits text at a larger set of prefixes in much the same way.
 */
class MarkedArguments {

    /** Marker introducing a task's new description, taken only by {@code edit}. */
    static final String DESC_KEYWORD = "/desc";

    /** Marker introducing a deadline's due date. */
    static final String BY_KEYWORD = "/by";

    /** Marker introducing an event's start. */
    static final String FROM_KEYWORD = "/from";

    /** Marker introducing an event's end. */
    static final String TO_KEYWORD = "/to";

    /**
     * Every marker the chatbot knows, in the order in which complaints about them are made.
     *
     * <p>This is not an order the user has to type them in. Each marker says which
     * detail follows it, so an order is only imposed where a command needs one.
     */
    static final List<String> KEYWORDS = List.of(DESC_KEYWORD, BY_KEYWORD, FROM_KEYWORD, TO_KEYWORD);

    /**
     * Finds any of {@link #KEYWORDS} written as a whole word, in any case.
     *
     * <p>Case is ignored so that {@code /BY} is found and refused with a hint, rather
     * than passing unnoticed into a description. The lookbehind and lookahead are
     * what make a marker a whole word: neither side may touch a character that is
     * not whitespace. Whitespace is judged as {@link bob.command.CommandWord} judges
     * it after a command word, so a tab counts as a space here too.
     */
    private static final Pattern KEYWORD_PATTERN = Pattern.compile(
            "(?<!\\P{javaWhitespace})(?:"
                    + KEYWORDS.stream().map(Pattern::quote).collect(Collectors.joining("|"))
                    + ")(?!\\P{javaWhitespace})",
            Pattern.CASE_INSENSITIVE);

    /** The text before the first marker, or all of it if there is none, trimmed. */
    private final String preamble;

    /**
     * The value after each marker used, keyed by the marker, in the order the markers
     * were typed. Each value runs to the next marker or to the end, trimmed, and is
     * empty when nothing was written after its marker.
     */
    private final Map<String, String> valuesByKeyword;

    private MarkedArguments(String preamble, Map<String, String> valuesByKeyword) {
        this.preamble = preamble;
        this.valuesByKeyword = valuesByKeyword;
    }

    /**
     * Returns {@code text} split at its markers.
     *
     * <p>A value ends at whichever marker comes next, not at the next one in
     * {@link #KEYWORDS}, which is what lets markers be typed in any order.
     *
     * @param text the text to split, such as everything after a command word.
     * @return the text before the first marker, and the value after each marker.
     * @throws BobException if a marker is written in anything but lowercase, or the
     *                      same marker is written twice.
     */
    static MarkedArguments parse(String text) throws BobException {
        List<MatchResult> matches = KEYWORD_PATTERN.matcher(text).results().toList();
        Map<String, String> valuesByKeyword = new LinkedHashMap<>();
        for (int i = 0; i < matches.size(); i++) {
            MatchResult match = matches.get(i);
            String keyword = requireLowercase(match.group());
            if (valuesByKeyword.containsKey(keyword)) {
                throw new BobException("You wrote " + keyword + " twice. Give just one.");
            }
            int valueEnd = (i + 1 < matches.size()) ? matches.get(i + 1).start() : text.length();
            valuesByKeyword.put(keyword, text.substring(match.end(), valueEnd).trim());
        }
        int preambleEnd = matches.isEmpty() ? text.length() : matches.get(0).start();
        return new MarkedArguments(text.substring(0, preambleEnd).trim(), valuesByKeyword);
    }

    /** Returns the text before the first marker, or all of the text if it has no marker. */
    String getPreamble() {
        return preamble;
    }

    /** Returns the markers used, in the order they were typed. */
    List<String> getKeywords() {
        return List.copyOf(valuesByKeyword.keySet());
    }

    /** Returns whether {@code keyword} was used. */
    boolean hasKeyword(String keyword) {
        return valuesByKeyword.containsKey(keyword);
    }

    /**
     * Returns the value written after {@code keyword}, which is an empty string if
     * the marker was followed by nothing, or an empty {@link Optional} if the marker
     * was not used at all.
     */
    Optional<String> getValue(String keyword) {
        return Optional.ofNullable(valuesByKeyword.get(keyword));
    }

    /**
     * Returns the marker as it should have been typed, having checked that it was.
     *
     * <p>{@link Locale#ROOT} lowercases alike on every computer, for the reason given
     * where {@link Parser} lowercases a command word.
     *
     * @param typedKeyword a marker as the user typed it, in any case.
     * @throws BobException if it was not typed in lowercase.
     */
    private static String requireLowercase(String typedKeyword) throws BobException {
        String keyword = typedKeyword.toLowerCase(Locale.ROOT);
        if (!typedKeyword.equals(keyword)) {
            throw new BobException("Markers are lowercase. Did yu mean " + keyword + "?");
        }
        return keyword;
    }
}
