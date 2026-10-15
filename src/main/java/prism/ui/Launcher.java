package prism.ui;

import javafx.application.Application;

/** Launches the JavaFX application to avoid classpath issues. */
public class Launcher {
    /** Starts the Prism JavaFX application. */
    public static void main(String[] args) {
        Application.launch(Gui.class, args);
    }
}
