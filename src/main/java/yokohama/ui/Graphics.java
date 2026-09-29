package yokohama.ui;

/**
 * Handles printing of ASCII messages:
 * Welcome banner and cat error message.
 */
public class Graphics {
    /**
     * Prints the welcome banner and usage hint.
     */
    public void printWelcomeBanner() {
        String banner = "__   __  ___  _  __  ___  _   _    _    __  __    _    \n"
                + "\\ \\ / / / _ \\| |/ / / _ \\| | | |  / \\  |  \\/  |  / \\   \n"
                + " \\ V / | | | | ' / | | | | |_| | / _ \\ | |\\/| | / _ \\  \n"
                + "  | |  | |_| | . \\ | |_| |  _  |/ ___ \\| |  | |/ ___ \\ \n"
                + "  |_|   \\___/|_|\\_\\ \\___/|_| |_/_/   \\_\\_|  |_/_/   \\_\\\n";

        System.out.println("Hello, welcome to ");
        System.out.println(banner);
        System.out.println("Enter 'exit' to leave the program. Yokohama returns all inputs as is.");
    }

    /**
     * Prints an error message with the error illustration.
     *
     * @param exception Exception containing the error message.
     */
    public void printErrorCat(Exception exception) {
        System.out.println("   |\\---/|    ");
        System.out.println("   | x_x |    ");
        System.out.println("    \\_^_/     ");
        System.out.println("   /  _  \\    ");
        System.out.println("  |  / \\  |   ");
        System.out.println("  / |   | \\   ");
        System.out.println(" \"\"'     '\"\"  ");
        System.out.println("OOPS!!! " + exception.getMessage() + "\n");
    }
}
