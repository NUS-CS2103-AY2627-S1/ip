package nexus;

/** Handles Nexus console messages. */
public class Ui {
    private static final String DIVIDER = "________________________________________\n";

    /**
     * Joins a variable number of message lines for display.
     *
     * @param lines lines to join in their display order.
     * @return the lines separated by newline characters
     */
    public static String formatLines(String... lines) {
        return String.join("\n", lines);
    }

    /** Shows the startup greeting, including the Nexus banner. */
    public void showWelcome(String banner, String greeting) {
        System.out.print(DIVIDER + banner + "\n" + greeting + "\n\n" + DIVIDER);
    }

    /** Shows one response using Nexus's standard console format. */
    public void showResponse(String response) {
        System.out.print(DIVIDER + response + "\n" + DIVIDER);
    }
}
