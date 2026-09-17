package jonathan;

import javafx.application.Application;

/** Launches the JavaFX application without directly extending Application. */
public class Launcher {
    public static void main(String... args) {
        Application.launch(Main.class, args);
    }
}
