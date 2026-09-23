package sallman.gui;

import javafx.application.Application;

/**
 * Starts the GUI.
 * <p>
 * Launching {@link Main} directly would mean starting a class that extends
 * {@link Application}, which trips a classpath check when the app is run from a
 * JAR. Starting from a class that does not extend it avoids that.
 * <p>
 * Taken from the SE-EDU JavaFX tutorial: https://se-education.org/guides/tutorials/javaFx.html
 */
public class Launcher {

    /**
     * Starts the chatbot's GUI.
     *
     * @param args passed through to JavaFX
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
