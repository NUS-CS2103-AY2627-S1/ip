package luna.gui;

import javafx.application.Application;

/**
 * Launches the JavaFX application.
 *
 * <p>This separate launcher avoids JavaFX classpath issues when the application is packaged.
 */
public class Launcher {

    /**
     * Starts the JavaFX runtime using {@link Main} as the application class.
     *
     * @param args Command-line arguments passed to JavaFX.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
