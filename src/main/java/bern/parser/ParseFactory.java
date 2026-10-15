package bern.parser;

import java.text.ParseException;
import java.util.NoSuchElementException;

/**
 * Provides utilities for extracting command arguments separated by specified keywords.
 */
public class ParseFactory {
    /**
     * Creates a parser factory whose parsing helpers are shared by subclasses through static methods.
     */
    public ParseFactory() {
    }

    /**
     * Extracts nonempty arguments from token array using given keywords, ignoring the command token at index zero.
     *
     * @param tokens The array of tokens, consisting of at least the command token.
     * @param keywords The keywords separating arguments in their expected order.
     * @return The arguments for each keyword in their expected order.
     * @throws ParseException If a keyword is missing or out of order, with its text as the message and its
     *     zero-based keyword index as the error offset; or if an argument is empty, with the preceding command
     *     or keyword as the message and an error offset of {@code -1}.
     */
    protected static String[] parseData(String[] tokens, String[] keywords) throws ParseException {
        String[] parsedData = new String[keywords.length + 1];
        int next = 1;
        for (int i = 0; i < keywords.length; i++) {
            StringBuilder stringBuilder = new StringBuilder();

            try {
                next = joinTokensFromIndexUntil(tokens, next, stringBuilder, keywords[i]);
            } catch (NoSuchElementException e) {
                // current keyword not found in order
                throw new ParseException(keywords[i], i);
            }
            parsedData[i] = stringBuilder.toString().strip();

            if (parsedData[i].isEmpty()) {
                // parsed argument is empty
                throw new ParseException(i == 0 ? tokens[0] : keywords[i - 1], -1);
            }
        }
        StringBuilder stringBuilder = new StringBuilder();
        joinTokensFromIndex(tokens, next, stringBuilder);
        parsedData[keywords.length] = stringBuilder.toString().strip();

        if (parsedData[keywords.length].isEmpty()) {
            // parsed argument is empty
            throw new ParseException(keywords.length == 0 ? tokens[0] : keywords[keywords.length - 1], -1);
        }

        return parsedData;
    }

    /**
     * Joins all tokens from a given index with spaces and appends to a given StringBuilder.
     *
     * @param tokens The token array.
     * @param index The index of the first token to be included.
     * @param stringBuilder The destination for the joined tokens.
     */
    private static void joinTokensFromIndex(String[] tokens, int index, StringBuilder stringBuilder) {
        while (index < tokens.length) {
            stringBuilder.append(tokens[index]);
            stringBuilder.append(" ");
            index++;
        }
    }

    /**
     * Joins all tokens from a given index with spaces and appends to a given StringBuilder, stops when the next token
     * is the specified keyword.
     *
     * @param tokens The tokens array.
     * @param index The index of the first token to be included.
     * @param stringBuilder The destination for the joined tokens.
     * @param keyword The keyword to stop at.
     * @return The index immediately after the matching keyword.
     * @throws NoSuchElementException If the keyword does not occur at or after the starting index.
     */
    private static int joinTokensFromIndexUntil(
            String[] tokens, int index, StringBuilder stringBuilder, String keyword) throws NoSuchElementException {
        if (index == tokens.length) {
            throw new NoSuchElementException();
        }

        while (!tokens[index].equalsIgnoreCase(keyword)) {
            stringBuilder.append(tokens[index]);
            stringBuilder.append(" ");
            index++;

            if (index == tokens.length) {
                throw new NoSuchElementException();
            }
        }

        return ++index;
    }
}
