package hse.gui;

import javafx.application.Application;

/** Launches the JavaFX application without requiring the JavaFX runtime to load {@link Main} directly. */
public class Launcher {
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
