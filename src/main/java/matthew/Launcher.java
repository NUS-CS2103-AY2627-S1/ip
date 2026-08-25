package matthew;

import javafx.application.Application;

/** Provides a non-{@link Application} entry point for the JavaFX app. */
public class Launcher {

    /** Creates a launcher. */
    public Launcher() {
    }

    /** Starts the JavaFX application. */
    public static void main(String... args) {
        Application.launch(MatthewGui.class, args);
    }
}
