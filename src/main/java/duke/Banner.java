package duke;

/**
 * Displays the application's ASCII-art startup banner.
 */
public class Banner {
    /**
     * Returns the ASCII-art banner as a multi-line string.
     *
     * @return the banner text
     */
    public static String getBanner() {
        return " ____              _        \n"
                + "|  _ \\ _   _  ____| | _____ \n"
                + "| | | | | | |/ ___| |/ / _ \\\n"
                + "| |_| | |_| | |___    <  __/\n"
                + "|____/ \\__,_|\\____|__\\_\\___|\n";
    }

    /**
     * Prints the ASCII-art banner to standard output.
     */
    public static void print() {
        System.out.println(getBanner());
    }
}
