package nutrisoy.ui;

import javafx.application.Application;

/**
 * Launches the NutriSoy JavaFX application.
 */
public class Launcher {
    /**
     * Starts the JavaFX application.
     *
     * @param args command-line arguments, which are not used
     */
    public static void main(String[] args) {
        try {
            NativeLibraries.prepare();
        } catch (Exception e) {
            System.err.println("Unable to start NutriSoy: " + e.getMessage());
            System.exit(1);
            return;
        }
        Application.launch(Main.class, args);
    }
}
