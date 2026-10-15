package charlie;

import javafx.application.Application;

/**
 * Launches the JavaFX application without extending {@link Application} itself.
 * This separate entry point avoids JavaFX classpath issues in packaged builds.
 */
public class Launcher {
    /**
     * Starts the Charlie graphical interface.
     *
     * @param args Command-line arguments passed to JavaFX.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
