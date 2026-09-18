package com.fireinyu.themyth.ui.gui;

import javafx.application.Application;

/**
 * A launcher class to workaround classpath issues with JavaFX.
 */
public class GuiLauncher {
    /**
     * The main entry point for the application.
     *
     * @param args Command line arguments.
     */
    public static void main(String... args) {
        Application.launch(Gui.class);
    }
}
