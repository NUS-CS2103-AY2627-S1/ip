package yuki;

import javafx.application.Application;
import yuki.gui.Main;

/**
 * Launches Yuki's JavaFX application.
 */
public class Launcher {

    /**
     * Starts the JavaFX application.
     *
     * @param args Command-line arguments passed to JavaFX.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
