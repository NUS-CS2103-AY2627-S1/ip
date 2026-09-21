package twizzy.gui;

import javafx.application.Application;

/** Launches the JavaFX application to avoid JavaFX classpath issues. */
public class Launcher {
    /** Starts the Twizzy graphical application. */
    public static void main(String[] args) {
        Application.launch(TwizzyGui.class, args);
    }
}
