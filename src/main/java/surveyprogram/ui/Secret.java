package surveyprogram.ui;

import surveyprogram.DeviceG;

/**
 * Displays the application's timed introductory sequence and persistence warnings.
 */
public class Secret {
    private static final String DIVIDER =
            "____________________________________________________________________________________";

    private Secret() {
        // This class contains only presentation utilities.
    }

    /**
     * Plays the introduction, starts the command interface, and displays the farewell.
     *
     * @throws InterruptedException if a timed pause is interrupted
     */
    public static void run() throws InterruptedException {
        displayOpeningSequence();
        DeviceG.boot();
        displayClosingSequence();
    }

    /** Displays the timed introductory dialogue. */
    private static void displayOpeningSequence() throws InterruptedException {
        pauseThenPrintln(3000, "\nGREETINGS.\n");
        pauseThenPrintln(3000, "WE MEET ONCE MORE.\n");
        pauseThenPrintln(3000, "WELL THEN,\n");
        pauseThenPrintln(2000, "SHALL WE HASTEN?\n");
        Thread.sleep(2000);
        System.out.println(DIVIDER);
        System.out.println("\nNOW.\n");
        pauseThenPrintln(2000, "LET US POPULATE THE CONTENTS\nOF THE DEVICE\nAS YOU WISH.\n");
        pauseThenPrintln(3000,
                "*  (With repetition, you found yourself answering the machine more fluently.)");
        pauseThenPrintln(1000, "*  (Seems like you know all the commands you need to.)");
        Thread.sleep(1000);
        System.out.println("*  (Still, you remembered you can say \"bye\" to leave!)");
    }

    /** Displays the timed farewell dialogue. */
    private static void displayClosingSequence() throws InterruptedException {
        System.out.println(DIVIDER);
        System.out.println("\nTHEN, UNTIL WE MEET ONCE MORE.");
        System.out.println(DIVIDER);
        pauseThenPrintln(3000, "\n(*  Well, there was not a man here.)");
    }

    /**
     * Displays a warning after stored data cannot be fully read or written.
     *
     * @param hasSavedRecords whether part of the stored data was successfully recovered
     */
    public static void error(boolean hasSavedRecords) {
        try {
            displayErrorIntroduction();
            if (!hasSavedRecords) {
                displayDiscardWarning();
            } else {
                displaySalvageNotice();
            }
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(exception);
        }
    }

    /** Displays the shared introduction for persistence errors. */
    private static void displayErrorIntroduction() throws InterruptedException {
        System.out.println("CURIOUS.\n");
        pauseThenPrintln(3000, "YOU MUST HAVE\nTAMPERED WITH SOMETHING\nYOU SHOULD NOT HAVE.\n");
        pauseThenPrintln(3000, "DID YOU DO SUCH A THING?\n");
        pauseThenPrintln(3000, "WELL. IN ANY CASE.\nTHIS DATA MUST BE DISCARDED.\n");
        Thread.sleep(3000);
    }

    /** Displays the warning used when no saved records could be recovered. */
    private static void displayDiscardWarning() throws InterruptedException {
        System.out.print("TRY");
        pauseThenPrint(1000, " TO BE");
        pauseThenPrintln(1000, " MORE CAUTIOUS");
        pauseThenPrintln(1000, "NEXT TIME.");
    }

    /** Displays the notice used when some saved records were recovered. */
    private static void displaySalvageNotice() throws InterruptedException {
        System.out.println("HOWEVER.\n");
        pauseThenPrintln(3000, "I HAVE SALVAGED\nSOME OF YOUR ANSWERS.\n");
        pauseThenPrintln(2000, "DO WITH THIS\nAS YOU WILL.\n");
    }

    /** Waits for the specified duration before printing text without a line break. */
    private static void pauseThenPrint(long milliseconds, String text) throws InterruptedException {
        Thread.sleep(milliseconds);
        System.out.print(text);
    }

    /** Waits for the specified duration before printing a line of text. */
    private static void pauseThenPrintln(long milliseconds, String text) throws InterruptedException {
        Thread.sleep(milliseconds);
        System.out.println(text);
    }

    /**
     * Returns the introductory dialogue without timed pauses.
     *
     * @return introductory dialogue for the GUI
     */
    public static String getOpeningDialogue() {
        return """
            GREETINGS.

            WE MEET ONCE MORE.

            WELL THEN,

            SHALL WE HASTEN?

            NOW.

            LET US POPULATE THE CONTENTS
            OF THE DEVICE
            AS YOU WISH.

            *  (With repetition, you found yourself answering the machine more fluently.)
            *  (Seems like you know all the commands you need to.)
            *  (Still, you remembered you can say "bye" to leave!)
            """.strip();
    }

    /**
     * Returns the farewell dialogue without timed pauses.
     *
     * @return farewell dialogue for the GUI
     */
    public static String getClosingDialogue() {
        return """
            THEN, UNTIL WE MEET ONCE MORE.

            (*  Well, there was not a man here.)
            """.strip();
    }
}
