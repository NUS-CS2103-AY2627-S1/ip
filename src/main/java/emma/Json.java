package emma;

/**
 * Small helpers for writing JSON by hand, so that quoting and escaping are
 * done the same way everywhere.
 */
public class Json {

    /**
     * Wraps text in quotes, escaping the characters JSON does not allow raw.
     *
     * @param value the text to quote.
     * @return the text as a JSON string.
     */
    public static String quote(String value) {
        StringBuilder quoted = new StringBuilder("\"");
        for (char c : value.toCharArray()) {
            switch (c) {
                case '"' -> quoted.append("\\\"");
                case '\\' -> quoted.append("\\\\");
                case '\n' -> quoted.append("\\n");
                case '\t' -> quoted.append("\\t");
                default -> quoted.append(c);
            }
        }
        return quoted.append('"').toString();
    }

    /**
     * Builds one `"key": "value"` pair.
     *
     * @param key the field name.
     * @param value the field's text.
     * @return the pair, ready to join with others.
     */
    public static String buildField(String key, String value) {
        return quote(key) + ": " + quote(value);
    }

    /**
     * Builds one `"key": true` or `"key": false` pair.
     *
     * @param key the field name.
     * @param value the field's value.
     * @return the pair, ready to join with others.
     */
    public static String buildField(String key, boolean value) {
        return quote(key) + ": " + value;
    }
}
