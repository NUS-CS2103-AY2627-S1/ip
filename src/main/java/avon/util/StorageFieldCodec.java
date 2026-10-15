package avon.util;

/**
 * Escapes and restores text fields in Avon's tab-separated storage format.
 */
public final class StorageFieldCodec {
    private StorageFieldCodec() {
    }

    /**
     * Escapes backslashes and control-character delimiters in a storage field.
     *
     * @param value the unescaped field value.
     * @return the escaped field value.
     */
    public static String escape(String value) {
        return value.replace("\\", "\\\\")
                .replace("\t", "\\t")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }

    /**
     * Restores backslashes and control-character delimiters in a storage field.
     *
     * @param value the escaped field value.
     * @return the restored field value.
     * @throws IllegalArgumentException if the field contains an unsupported escape sequence.
     */
    public static String unescape(String value) {
        StringBuilder restoredValue = new StringBuilder();
        boolean isEscaped = false;
        for (int index = 0; index < value.length(); index++) {
            char character = value.charAt(index);
            if (!isEscaped && character == '\\') {
                isEscaped = true;
            } else if (isEscaped) {
                restoredValue.append(unescapeCharacter(character));
                isEscaped = false;
            } else {
                restoredValue.append(character);
            }
        }
        if (isEscaped) {
            throw new IllegalArgumentException("Incomplete escape sequence.");
        }
        return restoredValue.toString();
    }

    /**
     * Restores one character that follows an escape marker.
     *
     * @param character the escaped character.
     * @return the corresponding literal character.
     * @throws IllegalArgumentException if the escape sequence is unsupported.
     */
    private static char unescapeCharacter(char character) {
        return switch (character) {
            case '\\' -> '\\';
            case 't' -> '\t';
            case 'n' -> '\n';
            case 'r' -> '\r';
            default -> throw new IllegalArgumentException("Unsupported escape sequence.");
        };
    }
}
