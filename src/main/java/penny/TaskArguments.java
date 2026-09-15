package penny;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Splits dated task arguments at standalone slash parameters in the required order.
 */
final class TaskArguments {
    private static final Pattern PARAMETER = Pattern.compile("(?<!\\S)/\\S*");

    /**
     * Returns the description followed by parameter values, retaining empty values for validation.
     * Slash-prefixed tokens are reserved for parameters; slashes within words or URLs are ordinary text.
     */
    static String[] split(String arguments, String... keywords) throws PennyException {
        String[] values = new String[keywords.length + 1];
        Matcher matcher = PARAMETER.matcher(arguments == null ? "" : arguments);
        int count = 0;
        int valueStart = 0;
        while (matcher.find()) {
            if (count >= keywords.length || !matcher.group().equals(keywords[count])) {
                throw new PennyException("Use each parameter once, in this order: " + String.join(" ", keywords));
            }
            values[count] = arguments.substring(valueStart, matcher.start()).strip();
            valueStart = matcher.end();
            count++;
        }
        if (count != keywords.length) {
            throw new PennyException("Required parameters: " + String.join(" ", keywords));
        }
        values[count] = arguments.substring(valueStart).strip();
        for (String value : values) {
            if (value.isBlank()) {
                throw new PennyException("Task description and parameter values cannot be empty.");
            }
        }
        return values;
    }
}
