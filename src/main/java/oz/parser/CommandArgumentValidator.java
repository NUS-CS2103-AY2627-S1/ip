package oz.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import oz.exception.OzException;

/**
 * Provides shared validation and lookup operations for command parameter flags.
 */
final class CommandArgumentValidator {
    /** Error template shown when duplicate parameter flags are detected. */
    private static final String DUPLICATE_FLAG_MESSAGE_FORMAT =
            "Duplicate '%s' parameter detected.";

    /** Error shown when task input contains the storage delimiter. */
    private static final String RESERVED_DELIMITER_MESSAGE =
            "Task input cannot contain the '|' character because it is reserved for storage.";

    /** Prevents instantiation of this utility class. */
    private CommandArgumentValidator() {
    }

    /**
     * Rejects any parameter flag that occurs more than once.
     *
     * @param arguments Command arguments to inspect.
     * @param flags Parameter flags including their leading slashes.
     * @throws OzException If any supplied flag occurs more than once.
     */
    static void validateNoDuplicateFlags(String arguments, String... flags)
            throws OzException {
        for (String flag : flags) {
            if (countFlagOccurrences(arguments, flag) > 1) {
                throw new OzException(String.format(DUPLICATE_FLAG_MESSAGE_FORMAT, flag));
            }
        }
    }

    /**
     * Rejects task text that would corrupt the pipe-delimited storage format.
     * Credit to user Gnanes99 (https://github.com/Gnanes99) for the insight to forbid the storage delimiter.
     * Link to forum discussion: (https://github.com/NUS-CS2103-AY2627-S1/forum/issues/238)
     * @param arguments Task arguments to validate.
     * @throws OzException If the arguments contain the storage delimiter.
     */
    static void validateNoStorageDelimiter(String arguments) throws OzException {
        if (arguments.contains("|")) {
            throw new OzException(RESERVED_DELIMITER_MESSAGE);
        }
    }

    /**
     * Returns whether arguments contain a flag as a distinct word.
     *
     * @param arguments Command arguments to inspect.
     * @param flag Parameter flag including its leading slash.
     * @return True if the distinct flag is present; false otherwise.
     */
    static boolean containsFlag(String arguments, String flag) {
        return findFlagIndex(arguments, flag) >= 0;
    }

    /**
     * Returns whether arguments contain any supplied flag as a distinct word.
     *
     * @param arguments Command arguments to inspect.
     * @param flags Parameter flags including their leading slashes.
     * @return True if at least one distinct flag is present; false otherwise.
     */
    static boolean containsAnyFlag(String arguments, String... flags) {
        for (String flag : flags) {
            if (containsFlag(arguments, flag)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Finds a distinct parameter flag without matching it inside another word.
     *
     * @param arguments Command arguments to inspect.
     * @param flag Parameter flag including its leading slash.
     * @return Zero-based flag position, or -1 if absent.
     */
    static int findFlagIndex(String arguments, String flag) {
        Matcher matcher = createFlagMatcher(arguments, flag);
        return matcher.find() ? matcher.start() : -1;
    }

    /**
     * Counts appearances of a parameter flag as a distinct argument word.
     *
     * @param arguments Command arguments to inspect.
     * @param flag Parameter flag including its leading slash.
     * @return Number of distinct appearances of the flag.
     */
    private static int countFlagOccurrences(String arguments, String flag) {
        Matcher matcher = createFlagMatcher(arguments, flag);
        int count = 0;
        while (matcher.find()) {
            count++;
        }
        return count;
    }

    /**
     * Creates a matcher that recognizes a flag only at argument boundaries.
     *
     * @param arguments Command arguments to inspect.
     * @param flag Parameter flag including its leading slash.
     * @return Boundary-aware matcher for the flag.
     */
    private static Matcher createFlagMatcher(String arguments, String flag) {
        Pattern flagPattern = Pattern.compile(
                "(?<=\\s|^)" + Pattern.quote(flag) + "(?=\\s|$)");
        return flagPattern.matcher(arguments);
    }
}
