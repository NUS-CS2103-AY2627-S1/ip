package bloop.gui;

import java.awt.GraphicsEnvironment;

import bloop.Duke;
import javafx.application.Application;

/** Launches Orbit without triggering the Java launcher special-case for JavaFX application classes. */
public final class BloopLauncher {
    private BloopLauncher() {
        // Utility class.
    }

    /** Starts Orbit's GUI, or its console interface when a graphical environment is unavailable. */
    public static void main(String[] args) {
        if (GraphicsEnvironment.isHeadless()) {
            new Duke("data/duke.txt").run();
            return;
        }
        Application.launch(BloopApp.class, args);
    }
}
