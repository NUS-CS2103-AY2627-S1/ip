package oreo;

import javafx.application.Application;

/** Launches Oreo's JavaFX interface without exposing JavaFX details to the chatbot logic. */
public class Launcher {
    /** Starts the JavaFX application. */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
