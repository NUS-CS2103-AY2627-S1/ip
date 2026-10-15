package anders;

import java.io.IOException;
import java.net.URISyntaxException;

import javafx.application.Application;

/** Starts the JavaFX application with the native libraries packaged for this computer. */
public class Launcher {
    /** Selects the bundled libraries and launches the JavaFX runtime. */
    public static void main(String[] args) {
        try {
            NativeLibraries.prepare();
        } catch (IOException | URISyntaxException | IllegalArgumentException exception) {
            System.err.println("Anders could not start: " + exception.getMessage());
            System.exit(1);
            return;
        }
        Application.launch(Main.class, args);
    }
}
