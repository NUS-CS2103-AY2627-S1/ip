package rei;

import javafx.application.Application;
import rei.gui.MainApp;

/** Launches Rei without extending JavaFX's Application class. */
public class Launcher {
    /** Starts the JavaFX runtime and opens Rei's main window. */
    public static void main(String[] args) {
        Application.launch(MainApp.class, args);
    }
}
