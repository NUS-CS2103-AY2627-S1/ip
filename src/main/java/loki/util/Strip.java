package loki.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Replaces zero characters with spaces in every line of a pasted string.
 */
public class Strip {
    /*
     * Paste the string to process between the triple quotation marks.
     * Keep the opening and closing quotation marks on their own lines.
     */
    private static final String TEXT_TO_STRIP = """
            00000000000000000000000000000000000000000000000000
+*+#+*#++0000000000000000000000000000000*+*#+*0000
++0*++*+*00000000000000000000000000000**+*0++**+00
+*+#+*00+0*+000000000000000000000000***0000****000
++0*++0000+0++000000000000000000000++*000+0++*+000
0++#++*00000++00000000000000000000*+00000+0*+**000
0+0*++*0000000+000000000000000000+0000000*+*+00000
000+0++++0000000000000000000000000000000+++++00000
00++0*+*+00000000000000000000000000000*0*++*000000
000+0*+*+00000000000000000000000000000++*+0+000000
00000*+**+#0000000000000000000000000+0****00000000
00000+0++++0000000000+0+0++000000000000++++0+00000
00000*0**#*+*0+0*#**0*+#+*#+*0**#*++00+#***0*00000
0000000+0+0++++0+0++++0+0++++0+0++++0+0+0++0000000
0000000+0+0++++0+0++++0+0++++0+0++++0+0+0++0000000
000000000+0++++0+0++++0+0++++0+0++++0+0+0000000000
000000000+0+0++++000+00+0+0++++0+0+0+++00000000000
00000000++0*++*+*0++*++*+*0+**+0*0*++*++0000000000
000000000+0+0++++0+0+00+0+0++++0+0+0++++0000000000
000000000+0#+**+*0*+#++#+*0***+0*+*++#+*0000000000
000000000+0*0++++0++*++*0+0++*+0*0+++*++0000000000
000000000+0*000+*0++*++*+*0++*+0*+*000++0000000000
000000000+0+0000000000+0++00000000000*+00000000000
00000000000+00000000000000000000000000000000000000
000000000+0*++*00000000000000000++*++0000000000000
0000000000++0++*+00000000000000+++0*0+000000000000
00000000000+0*+*++*000000000+0+**+0*00000000000000
0000000000000*0++*+00000000++0*0+++000000000000000
000000000000000++*+000000000+0*0+00000000000000000
000000000000000+0*0000000000*+00000000000000000000
00000000000000000000000000000000000000000000000000
            """;

    /** Creates a utility instance for stripping zero characters. */
    public Strip() {
    }

    /**
     * Writes the pasted string after replacing every zero with a space.
     *
     * @param args command-line arguments, which are not used
     * @throws IOException if the output file cannot be written
     */
    public static void main(String[] args) throws IOException {
        String[] lines = TEXT_TO_STRIP.split("\\R", -1);
        StringBuilder strippedText = new StringBuilder();

        for (String line : lines) {
            if (strippedText.length() > 0) {
                strippedText.append(System.lineSeparator());
            }
            strippedText.append(replaceZeros(line));
        }

        Files.writeString(Path.of("loki.txt"), strippedText.toString());
    }

    /**
     * Replaces every zero in a line with a space, preserving character positions.
     *
     * @param line the line to process.
     * @return the line with all zeros replaced by spaces.
     */
    public static String replaceZeros(String line) {
        return line.replace('0', ' ');
    }
}
