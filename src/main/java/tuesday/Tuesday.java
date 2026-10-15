package tuesday;

import javafx.application.Application;

import tuesday.ui.Ui;

/**
 * Runs the chatbot.
 */
public class Tuesday {
    /**
     * Starts the Tuesday chatbot.
     *
     * @param args command-line arguments, which are not used.
     */
    public static void main(String[] args) {
        Application.launch(Ui.class, args);
    }
}
