package larp;

import javafx.application.Application;

/**
 * Starts the JavaFX screenshot generator without triggering launcher module checks.
 */
public class UiScreenshotLauncher {
    /**
     * Launches the screenshot generator with its destination path.
     *
     * @param args Command-line arguments passed to the generator.
     */
    public static void main(String[] args) {
        Application.launch(UiScreenshotGenerator.class, args);
    }
}
