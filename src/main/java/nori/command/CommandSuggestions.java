package nori.command;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Locale;
import java.util.Optional;

/**
 * Explains input that names no command Nori knows.
 *
 * Most unrecognized input is a command a user very nearly typed: the right
 * word in capitals, one letter too many, one letter missing. Naming the
 * command they probably meant turns a dead end into a correction they can act
 * on, without sending them to the help text to work it out for themselves.
 */
final class CommandSuggestions {
    /** Told to a user whose input resembles no command at all. */
    static final String UNKNOWN_COMMAND_MESSAGE =
            "CONFUSED NOOT! My flippers do not understand that command."
                    + " Try todo, deadline, event, on, list, find, mark, unmark, delete, help, or bye.";

    /**
     * The most single-character edits a word may need before Nori stops guessing.
     *
     * Two edits covers the ordinary slips, such as a doubled letter, a dropped
     * one, or two letters swapped, without reaching so far that an unrelated
     * word is answered with a confident and wrong suggestion.
     */
    private static final int MAX_SUGGESTION_DISTANCE = 2;

    /** Prevents instantiation of this stateless explainer. */
    private CommandSuggestions() {
    }

    /**
     * Explains why some input names no command, naming a likely correction where there is one.
     *
     * @param input the trimmed user input.
     * @return the message to show the user.
     */
    static String explain(String input) {
        assert input != null : "Input is explained only after a user interface has read it.";

        if (input.isEmpty()) {
            return "Noot? You didn't say anything."
                    + " Type \"help\" and I'll show you what my flippers can do.";
        }

        String firstWord = input.split("\\s+", 2)[0];
        Optional<CommandType> exactKeyword = findKeywordEqualTo(firstWord);
        if (exactKeyword.isPresent()) {
            String keyword = exactKeyword.get().getKeyword();
            return "NOOT?! \"" + keyword + "\" takes nothing after it."
                    + " Type \"" + keyword + "\" on its own.";
        }

        String foldedFirstWord = firstWord.toLowerCase(Locale.ROOT);
        Optional<CommandType> misCapitalizedKeyword = findKeywordEqualTo(foldedFirstWord);
        if (misCapitalizedKeyword.isPresent()) {
            return "NOOT?! My commands are all lowercase."
                    + " Did you mean \"" + misCapitalizedKeyword.get().getKeyword() + "\"?";
        }

        return findNearestKeyword(foldedFirstWord)
                .map(keyword -> "NOOT?! I do not know \"" + firstWord + "\"."
                        + " Did you mean \"" + keyword + "\"?")
                .orElse(UNKNOWN_COMMAND_MESSAGE);
    }

    /**
     * Finds the command type whose keyword is exactly the given word.
     *
     * @param word the word to match a keyword against.
     * @return the matching command type, or empty when no keyword is that word.
     */
    private static Optional<CommandType> findKeywordEqualTo(String word) {
        return Arrays.stream(CommandType.values())
                .filter(commandType -> commandType.getKeyword().equals(word))
                .findFirst();
    }

    /**
     * Finds the keyword a mistyped word is closest to, when one is close enough to name.
     *
     * A short keyword is held to a stricter limit than the shared one, because
     * two edits away from a two-letter keyword such as {@code on} is far enough
     * to reach words that have nothing to do with it.
     *
     * @param foldedWord the first word of the input, in lower case.
     * @return the nearest keyword, or empty when none is close enough.
     */
    private static Optional<String> findNearestKeyword(String foldedWord) {
        return Arrays.stream(CommandType.values())
                .map(CommandType::getKeyword)
                .filter(keyword -> isCloseEnough(foldedWord, keyword))
                .min(Comparator.comparingInt(keyword -> findEditDistance(foldedWord, keyword)));
    }

    /**
     * Returns whether a mistyped word is near enough a keyword to be named as its correction.
     *
     * @param foldedWord the first word of the input, in lower case.
     * @param keyword the keyword to measure against.
     * @return {@code true} when the word is within that keyword's edit limit.
     */
    private static boolean isCloseEnough(String foldedWord, String keyword) {
        int editLimit = Math.min(MAX_SUGGESTION_DISTANCE, keyword.length() - 1);
        return findEditDistance(foldedWord, keyword) <= editLimit;
    }

    /**
     * Counts the single-character insertions, deletions and substitutions between two words.
     *
     * Only two rows of the edit table are ever needed at once, so the finished
     * row becomes the scratch space for the next one.
     *
     * @param first the first word.
     * @param second the second word.
     * @return the number of edits that turn one word into the other.
     */
    private static int findEditDistance(String first, String second) {
        int[] previousRow = new int[second.length() + 1];
        int[] currentRow = new int[second.length() + 1];
        for (int column = 0; column <= second.length(); column++) {
            previousRow[column] = column;
        }

        for (int row = 1; row <= first.length(); row++) {
            currentRow[0] = row;
            for (int column = 1; column <= second.length(); column++) {
                int substitutionCost = first.charAt(row - 1) == second.charAt(column - 1) ? 0 : 1;
                currentRow[column] = Math.min(Math.min(currentRow[column - 1] + 1,
                        previousRow[column] + 1), previousRow[column - 1] + substitutionCost);
            }

            int[] completedRow = previousRow;
            previousRow = currentRow;
            currentRow = completedRow;
        }
        return previousRow[second.length()];
    }
}
