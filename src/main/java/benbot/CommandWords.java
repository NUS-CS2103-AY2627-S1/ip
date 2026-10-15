package benbot;

/** Provides shared operations for words in parsed user commands. */
final class CommandWords {
    private CommandWords() {
    }

    /** Returns a marker's index, or the number of words when the marker is absent. */
    static int findMarker(String[] words, String marker) {
        for (int i = 0; i < words.length; i++) {
            if (words[i].equals(marker)) {
                return i;
            }
        }
        return words.length;
    }

    /** Returns the number of times a marker occurs in the parsed command words. */
    static int countMarker(String[] words, String marker) {
        int markerCount = 0;
        for (String word : words) {
            if (word.equals(marker)) {
                markerCount++;
            }
        }
        return markerCount;
    }
}
