package kotha.ui;

import javafx.application.Application;

/** Starts the JavaFX application without making the {@link Application} subclass the JVM entry point. */
public class Launcher {
    /** Launches the Kotha JavaFX interface. */
    public static void main(String[] args) {
        Application.launch(KothaGui.class, args);
    }
}
