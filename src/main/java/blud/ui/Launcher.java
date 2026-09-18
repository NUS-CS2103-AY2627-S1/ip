package blud.ui;

import javafx.application.Application;

/** Launches the JavaFX application separately to work around classpath issues. */
public class Launcher {
    /** Launches the JavaFX application with the supplied command-line arguments. */
    public static void main(String[] args) {
        Application.launch(BludApplication.class, args);
    }
}
