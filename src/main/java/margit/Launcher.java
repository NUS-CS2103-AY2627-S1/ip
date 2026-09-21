package margit;

import javafx.application.Application;

/** Launches the JavaFX application to avoid JavaFX classpath issues. */
public class Launcher {
    /** Launches the JavaFX Hello World application. */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
