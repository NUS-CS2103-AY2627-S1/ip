package bean.command;

/** Provides text-processing helpers shared by command implementations. */
final class CommandText {

    private CommandText() {
    }

    /** Joins words in the half-open range from {@code startIndex} to {@code endIndex}. */
    static StringBuilder joinWords(String[] words, int startIndex, int endIndex) {
        StringBuilder text = new StringBuilder();
        for (int index = startIndex; index < endIndex; index++) {
            text.append(words[index]).append(" ");
        }
        return text;
    }
}
