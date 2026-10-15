package kpei.commands.parser;

import java.util.*;

import kpei.exceptions.BertException;
import kpei.exceptions.MissingArgumentException;

/**
 * Parses raw user input strings into structured {@link ParsedCommand} objects.
 * <p>
 * Enforces strict single-token arguments and Flag values. Arguments or Flag values
 * containing spaces must be enclosed in quotation marks (e.g. {@code "wash the dishes"}).
 * Single-word arguments without spaces (or connected by underscores) do not require quotation marks.
 * </p>
 */
public class CommandParser {

    /**
     * Tokenizes a raw input string into individual tokens, taking quoted strings into account.
     * Characters enclosed in single or double quotes are treated as a single token.
     * The matching quote character can be included in a quoted token by preceding it with a backslash.
     *
     * @param input The raw input line.
     * @return A list of extracted string tokens.
     */
    public List<String> tokenize(String input) {
        List<String> tokens = new ArrayList<>();
        if (input.isBlank()) {
            return tokens;
        }

        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;
        char quoteChar = '"';

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);

            boolean isEscapedQuote = inQuotes && c == '\\'
                    && i + 1 < input.length() && input.charAt(i + 1) == quoteChar;
            if (isEscapedQuote) {
                current.append(quoteChar);
                i++;
            } else if ((c == '"' || c == '\'') && (!inQuotes || c == quoteChar)) {
                if (inQuotes) {
                    inQuotes = false;
                } else {
                    inQuotes = true;
                    quoteChar = c;
                }
            } else if (Character.isWhitespace(c) && !inQuotes) {
                if (!current.isEmpty()) {
                    tokens.add(current.toString());
                    current.setLength(0);
                }
            } else {
                current.append(c);
            }
        }

        if (!current.isEmpty()) {
            tokens.add(current.toString());
        }

        return tokens;
    }

    /**
     * Parses a raw Command string from the user into a {@link ParsedCommand}.
     * Enforces that each argument or Flag value is a single token or a quoted string.
     *
     * @param tokens Tokenized input command where each argument or parameter is one token.
     * @return A {@link ParsedCommand} containing the parsed Command type, arguments, and flags.
     * @throws BertException If the entire command is missing.
     */
    public ParsedCommand parse(List<String> tokens) throws BertException {
        if (tokens.isEmpty()) {
            throw new BertException("User prompt should not be blank.");
        }

        String commandType = tokens.getFirst().toLowerCase();
        List<String> positionalParameters = new ArrayList<>();
        Map<String, String> flaggedParameters = new LinkedHashMap<>();

        int i = 1;

        // collect positional arguments
        while (i < tokens.size()) {
            if (isFlag(tokens.get(i))) {
                break;
            }
            positionalParameters.add(tokens.get(i));
            i++;
        }

        // collect flagged arguments
        while (i < tokens.size()) {
            assert isFlag(tokens.get(i)) : tokens.get(i) + " at index " + i + " should be a Flag";

            String flag = tokens.get(i);
            i++;
            if (i >= tokens.size()) {
                throw new MissingArgumentException(flag);
            }

            String value = tokens.get(i);
            flaggedParameters.put(flag, value);

            i++;
        }

        return new ParsedCommand(commandType, positionalParameters, flaggedParameters);
    }

    /**
     * Checks whether a token should be treated as a Flag in the current Command context.
     *
     * @param token The original token.
     * @return {@code true} if the token is a Flag, {@code false} otherwise.
     */
    private boolean isFlag(String token) {
        return token.startsWith("/") || token.startsWith("--") || token.startsWith("-");
    }
}
