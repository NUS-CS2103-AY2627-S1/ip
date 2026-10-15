package serina;

/**
 * Provides shared names used in Serina's dialogue.
 */
public final class SerinaDialogue {
    /** Name used when Serina addresses the user. */
    public static final String CAPTAIN_NAME = "Captain Cutter";

    private SerinaDialogue() {
    }

    /**
     * Returns a sentence addressed directly to the captain.
     *
     * @param message Message placed after the captain's name.
     * @return Personalized sentence.
     */
    public static String address(String message) {
        return CAPTAIN_NAME + ", " + message;
    }
}
