package bany.utilities;

import java.util.Locale;

import org.apache.commons.lang3.StringUtils;

/**
 * Provides small utility methods used by the Bany chatbot.
 */
public class Helper {

    /** Creates a helper for Bany's command utilities. */
    public Helper() {
    }

    /**
     * Checks whether text can be parsed as a 32-bit signed integer.
     *
     * @param str text to check.
     * @return {@code true} if the text represents an integer.
     */
    public static boolean isInteger(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Finds a suggested command using a predefined mapping or the smallest
     * Levenshtein distance from the user's input.
     *
     * <p>Command matching is case-insensitive. Predefined mappings from
     * {@link #getCommonConfusionPair(String)}, such as SEARCH to FIND, take
     * priority over distance matching. If multiple commands have the
     * same distance, the first command in {@link CommandCatalog#getAllCommands()}
     * is returned.</p>
     *
     * @param input command text entered by the user.
     * @return the preferred or closest allowed command, or {@code null} for null or blank input.
     */
    public static String getClosestWordMatch(String input) {

        if (input == null || input.isBlank()) {
            return null;
        }

        String normalizedInput = input.trim().toUpperCase(Locale.ROOT);

        if (!(getCommonConfusionPair(input) == null)) {
            return getCommonConfusionPair(input);
        }

        String closestCommand = null;
        int smallestDistance = Integer.MAX_VALUE;

        for (String command : CommandCatalog.getAllCommands()) {
            int distance = StringUtils.getLevenshteinDistance(normalizedInput, command);
            if (distance < smallestDistance) {
                smallestDistance = distance;
                closestCommand = command;
            }
        }

        return closestCommand;
    }

    /**
     * Returns a preferred command for commonly confused command words.
     *
     * @param input command word entered by the user.
     * @return preferred command, or {@code null} if no direct mapping exists.
     */
    public static String getCommonConfusionPair(String input) {
        String normalizedInput = input.trim().toUpperCase(Locale.ROOT);

        if (normalizedInput.equals("SEARCH")) {
            return "FIND";
        }

        if (normalizedInput.equals("TICK")) {
            return "MARK";
        }

        if (normalizedInput.equals("UNTICK")) {
            return "UNMARK";
        }
        return null;
    }
}
