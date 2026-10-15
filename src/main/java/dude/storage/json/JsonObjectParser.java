package dude.storage.json;

import java.util.HashMap;
import java.util.Map;

/**
 * Parses the limited JSON object shape emitted by {@link TaskJsonCodec}.
 */
final class JsonObjectParser {
    private final String input;
    private int position;

    /**
     * Creates a parser for one serialized JSON object.
     *
     * @param input JSON object to parse.
     */
    JsonObjectParser(String input) {
        this.input = input.trim();
    }

    private static char parseEscape(char character) {
        return switch (character) {
        case '"' -> '"';
        case '\\' -> '\\';
        case 'n' -> '\n';
        case 'r' -> '\r';
        case 't' -> '\t';
        default -> throw new IllegalArgumentException("Unsupported escape");
        };
    }

    /**
     * Returns the fields parsed from the JSON object.
     *
     * @return Parsed field names and values.
     */
    Map<String, String> parse() {
        Map<String, String> fields = new HashMap<>();
        expect('{');
        skipWhitespace();
        if (consume('}')) {
            return fields;
        }

        while (true) {
            String key = parseString();
            skipWhitespace();
            expect(':');
            skipWhitespace();
            fields.put(key, parseValue());
            skipWhitespace();
            if (consume('}')) {
                ensureEnd();
                return fields;
            }
            expect(',');
            skipWhitespace();
        }
    }

    private String parseValue() {
        if (position < input.length() && input.charAt(position) == '"') {
            return parseString();
        }

        int start = position;
        while (position < input.length() && input.charAt(position) != ','
                && input.charAt(position) != '}') {
            position++;
        }
        String value = input.substring(start, position).trim();
        if (value.isEmpty()) {
            throw new IllegalArgumentException("Missing value");
        }
        return value;
    }

    private String parseString() {
        expect('"');
        StringBuilder value = new StringBuilder();
        while (position < input.length()) {
            char character = input.charAt(position++);
            if (character == '"') {
                return value.toString();
            }
            if (character != '\\') {
                value.append(character);
                continue;
            }
            if (position >= input.length()) {
                throw new IllegalArgumentException("Unterminated escape");
            }
            value.append(parseEscape(input.charAt(position++)));
        }
        throw new IllegalArgumentException("Unterminated string");
    }

    private void expect(char expected) {
        skipWhitespace();
        if (position >= input.length() || input.charAt(position++) != expected) {
            throw new IllegalArgumentException("Unexpected JSON character");
        }
    }

    private boolean consume(char expected) {
        if (position < input.length() && input.charAt(position) == expected) {
            position++;
            return true;
        }
        return false;
    }

    private void skipWhitespace() {
        while (position < input.length() && Character.isWhitespace(input.charAt(position))) {
            position++;
        }
    }

    private void ensureEnd() {
        skipWhitespace();
        if (position != input.length()) {
            throw new IllegalArgumentException("Unexpected trailing JSON");
        }
    }
}
