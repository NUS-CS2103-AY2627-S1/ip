package com.benthecat.kachow;

import javafx.application.Application;

/**
 * Provides the application entry point used by the packaged JAR.
 */
public class Launcher {
    /** Starts Kachow using its default data file. */
    public static void main(String[] args) {
        Application.launch(FxMain.class, args);
    }
}
