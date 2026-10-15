package hse.gui;

import hse.HSE;

/** Provides the HSE task manager to the graphical interface. */
public class HseApplication {
    private final HSE hse;

    /** Creates a chat assistant backed by the standard HSE data file. */
    public HseApplication() {
        hse = new HSE("data/hse.txt");
    }

    /** Generates a response for the user's chat message. */
    public String getResponse(String input) {
        return hse.getResponse(input);
    }
}
