package hampster.parser;

import java.util.Locale;

import hampster.exception.HampsterException;

/** Parses and validates task tags and the optional creation tag option. */
public final class TagParser {

    private TagParser() {
    }

    /** Stores a description after removing its optional tag option. */
    public record ParsedInput(String description, String tag) {
    }

    /** Extracts and validates one {@code /tag} option from command input. */
    public static ParsedInput parseOption(String input) throws HampsterException {
        String[] parts = input.trim().split("\\s+");
        StringBuilder description = new StringBuilder();
        String tag = "";

        for (int i = 0; i < parts.length; i++) {
            if (parts[i].equals("/tag")) {
                if (!tag.isEmpty() || i + 1 >= parts.length) {
                    throw new HampsterException("The /tag option requires one brand to pin down.");
                }
                tag = normalize(parts[++i]);
            } else {
                if (description.length() > 0) {
                    description.append(' ');
                }
                description.append(parts[i]);
            }
        }

        return new ParsedInput(description.toString(), tag);
    }

    /** Normalizes one tag and rejects unsupported storage characters. */
    public static String normalize(String tag) throws HampsterException {
        if (tag == null || tag.isEmpty() || !tag.startsWith("#")) {
            throw new HampsterException("Brands need a # and no spaces to enter the dossier.");
        }

        for (int i = 0; i < tag.length(); i++) {
            char character = tag.charAt(i);
            if (character > 127 || Character.isWhitespace(character) || character == '|') {
                throw new HampsterException("Brands need plain ASCII characters and no spaces.");
            }
        }

        return tag.toLowerCase(Locale.ROOT);
    }
}
