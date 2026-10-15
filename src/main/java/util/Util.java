package util;

import java.util.ArrayList;

/**
 * Utility methods for parsing delimited strings and extracting integers.
 */
public class Util {

    /**
     * Converts a delimited text into an array list of the stored values.
     *
     * @param delimitedValues text containing delimited values
     * @param delimiter the character used to separate the values
     * @return array list of the string values in the text
     */
    public static ArrayList<String> toArrayList(String delimitedValues, char delimiter) {
        ArrayList<String> words = new ArrayList<>();
        int prevIdx = -1;

        for (int i = 0; i < delimitedValues.length(); i++) {
            char symbol = delimitedValues.charAt(i);
            if (symbol == delimiter) {
                words.add(delimitedValues.substring(prevIdx + 1, i));
                prevIdx = i;
            }
        }

        words.add(delimitedValues.substring(prevIdx + 1));
        return words;
    }

    /**
     * Converts a delimited text with a sequence of delimiter strings into an array list of values.
     *
     * @param delimitedValues text containing delimited values
     * @param delimiters delimiter strings in the sequence they appear in the text
     * @return array list of the string values in the text
     */
    public static ArrayList<String> toArrayList(String delimitedValues, ArrayList<String> delimiters) {
        ArrayList<String> words = new ArrayList<>();
        int nextDelimiterIdx = 0;

        int prevIdx = 0;
        int currIdx = 0;
        int nextIdx = delimitedValues.indexOf(delimiters.get(nextDelimiterIdx), prevIdx);

        while (currIdx < delimitedValues.length()) {
            if (currIdx == nextIdx) {
                words.add(delimitedValues.substring(prevIdx, nextIdx));

                int delimiterLength = delimiters.get(nextDelimiterIdx).length();
                prevIdx = nextIdx + delimiterLength;
                currIdx = nextIdx + delimiterLength;

                if (nextDelimiterIdx < delimiters.size() - 1) {
                    nextDelimiterIdx += 1;
                    nextIdx = delimitedValues.indexOf(delimiters.get(nextDelimiterIdx), prevIdx);
                } else {
                    nextIdx = delimitedValues.length();
                }
            } else {
                currIdx += 1;
            }
        }

        words.add(delimitedValues.substring(prevIdx));
        return words;
    }

    /**
     * Trim and extracts an integer value from a substring of text containing only an integer 
     * surrounded by whitespace.
     *
     * @param text the text containing the integer substring
     * @param start the start index of the substring (inclusive)
     * @param end the end index of the substring (exclusive)
     * @return the integer value parsed from text.substring(start, end)
     * @throws NumberFormatException if the substring cannot be parsed as a single integer
     */

    public static int trimAndExtractInteger(String text, int start, int end) {
        assert text != null : "Integer extraction requires input text";
        assert start >= 0 && start <= end && end <= text.length()
            : "Integer extraction range must be within the input text";
        return Integer.parseInt(text.substring(start, end).trim());
    }

    /**
     * Trim and extracts an integer value from a substring containing only an integer 
     * surrounded by whitespace
     *
     * @param text the text containing the integer substring
     * @param start the start index from which to extract the integer (inclusive)
     * @return the integer value parsed from text.substring(start)
     * @throws NumberFormatException if the substring cannot be parsed as a single integer
     */
    public static int trimAndExtractInteger(String text, int start) {
        assert text != null : "Integer extraction requires input text";
        assert start >= 0 && start <= text.length()
            : "Integer extraction start must be within the input text";
        return Integer.parseInt(text.substring(start).trim());
    }
}
