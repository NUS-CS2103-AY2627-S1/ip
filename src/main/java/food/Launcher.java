package food;

import javafx.application.Application;

/** Starts the JavaFX GUI indirectly, working around classpath issues with a direct launch. */
public class Launcher {
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
