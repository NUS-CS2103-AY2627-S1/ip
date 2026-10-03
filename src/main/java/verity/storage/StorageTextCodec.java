package verity.storage;

import verity.exception.VerityException;

/**
 * Encodes and decodes escaped text stored in Verity data files.
 */
public final class StorageTextCodec {
    private StorageTextCodec() {
    }

    /**
     * Encodes backslashes and newlines for one-line storage.
     *
     * @param value Text to encode.
     * @return Encoded text.
     */
    public static String encode(String value) {
        return value.replace("\\", "\\\\")
                .replace("\n", "\\n");
    }

    /**
     * Decodes backslashes and newlines from stored text.
     *
     * @param value Text to decode.
     * @return Decoded text.
     * @throws VerityException If an escape sequence is invalid.
     */
    public static String decode(String value) throws VerityException {
        StringBuilder decoded = new StringBuilder();
        for (int i = 0; i < value.length(); i++) {
            char current = value.charAt(i);
            if (current != '\\') {
                decoded.append(current);
                continue;
            }

            if (i + 1 >= value.length()) {
                throw new VerityException("text ends with an incomplete escape sequence.");
            }

            char escaped = value.charAt(++i);
            if (escaped == '\\') {
                decoded.append('\\');
            } else if (escaped == 'n') {
                decoded.append('\n');
            } else {
                throw new VerityException("unknown escape sequence '\\" + escaped + "'.");
            }
        }
        return decoded.toString();
    }
}
