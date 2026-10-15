package luckynoslacky.luckyui.gui;

import javafx.application.Application;

/**
 * Launches the LuckyNoSlacky JavaFX interface.
 */
public final class Launcher {
    private Launcher() {
    }

    /**
     * Starts the JavaFX application.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        Application.launch(LuckyNoGui.class, args);
    }
}
