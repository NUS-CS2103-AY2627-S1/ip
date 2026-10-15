package ip;

import ip.ui.TabbyGui;
import javafx.application.Application;

/** Starts the JavaFX application without making the JAR's main class an Application subclass. */
public final class Launcher {

    private Launcher() {
        // Utility class; do not instantiate.
    }

    /** Launches the Tabby JavaFX interface. */
    public static void main(String[] args) {
        Application.launch(TabbyGui.class, args);
    }
}
